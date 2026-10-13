package com.codementor.backend.security;

import com.codementor.backend.model.CodeReview;
import com.codementor.backend.model.CodeSubmission;
import com.codementor.backend.model.User;
import com.codementor.backend.repository.CodeReviewRepository;
import com.codementor.backend.repository.CodeSubmissionRepository;
import com.codementor.backend.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.jwt.secret=unit-test-secret-that-is-long-enough-for-hs256-1234567890",
        "app.jwt.expiration-minutes=1440"
})
@AutoConfigureMockMvc
@Transactional
public class AuthenticationAndAuthorizationTest {

    private static final AtomicInteger COUNTER = new AtomicInteger();
    private static final String PASSWORD = "Passw0rd!123";
    private static final String JWT_TEST_SECRET =
            "unit-test-secret-that-is-long-enough-for-hs256-1234567890";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CodeSubmissionRepository submissionRepository;

    @Autowired
    private CodeReviewRepository reviewRepository;

    private String uniqueEmail() {
        return "user" + COUNTER.incrementAndGet() + "@example.com";
    }

    private String register(String name, String email) throws Exception {
        String body = mapper.writeValueAsString(
                java.util.Map.of(
                        "name", name,
                        "email", email,
                        "password", PASSWORD
                )
        );
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
        return email;
    }

    private JsonNode login(String email, String password) throws Exception {
        String body = mapper.writeValueAsString(
                java.util.Map.of("email", email, "password", password)
        );
        String raw = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return mapper.readTree(raw);
    }

    private JsonNode loginExpectingFailure(String email, String password) throws Exception {
        String body = mapper.writeValueAsString(
                java.util.Map.of("email", email, "password", password)
        );
        String raw = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return mapper.readTree(raw);
    }

    private long submitCode(String token, String title) throws Exception {
        String body = mapper.writeValueAsString(
                java.util.Map.of(
                        "title", title,
                        "language", "java",
                        "code", "public class Sample {}",
                        "problemDescription", "sample problem"
                )
        );
        String raw = mockMvc.perform(post("/api/code-submissions")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return mapper.readTree(raw).get("id").asLong();
    }

    // ---------------------------------------------------------------
    // Authentication
    // ---------------------------------------------------------------

    @Test
    @Rollback
    void protectedEndpointsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/users/me")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/code-submissions/me")).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/code-submissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/code-submissions/1")).andExpect(status().isUnauthorized());
    }

    @Test
    @Rollback
    void garbageBearerTokenIsRejected() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer not-a-real-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Rollback
    void expiredBearerTokenIsRejected() throws Exception {
        String expiredToken = new JwtService(JWT_TEST_SECRET, 0)
                .issueToken(1L, "expired@example.com", "USER");

        mockMvc.perform(get("/api/users/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Rollback
    void registerLoginThenAccessMeFlow() throws Exception {
        String email = uniqueEmail();
        register("Alice", email);

        JsonNode login = login(email, PASSWORD);
        String token = login.get("token").asText();
        org.junit.jupiter.api.Assertions.assertFalse(token.isBlank());

        mockMvc.perform(get("/api/users/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    @Rollback
    void wrongPasswordReturnsUnauthorized() throws Exception {
        String email = uniqueEmail();
        register("Alice", email);

        JsonNode failure = loginExpectingFailure(email, "WrongPassword!1");
        org.junit.jupiter.api.Assertions.assertEquals(
                "Invalid email or password",
                failure.get("message").asText()
        );
    }

    @Test
    @Rollback
    void passwordsAreStoredHashed() throws Exception {
        String email = uniqueEmail();
        register("Alice", email);

        User stored = userRepository.findByEmail(email).orElseThrow();
        org.junit.jupiter.api.Assertions.assertTrue(stored.getPassword().startsWith("$2"));
        org.junit.jupiter.api.Assertions.assertNotEquals(PASSWORD, stored.getPassword());
    }

    @Test
    @Rollback
    void registerResponseNeverLeaksPassword() throws Exception {
        String email = uniqueEmail();
        String body = mapper.writeValueAsString(
                java.util.Map.of("name", "Alice", "email", email, "password", PASSWORD)
        );
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("password"))));
    }

    // ---------------------------------------------------------------
    // IDOR: code submissions
    // ---------------------------------------------------------------

    @Test
    @Rollback
    void forgedUserIdInBodyIsIgnored() throws Exception {
        String aEmail = uniqueEmail();
        register("Alice", aEmail);
        JsonNode alice = login(aEmail, PASSWORD);
        String aToken = alice.get("token").asText();
        long aliceId = alice.get("id").asLong();

        String bEmail = uniqueEmail();
        register("Bob", bEmail);
        JsonNode bob = login(bEmail, PASSWORD);
        long bobId = bob.get("id").asLong();

        String body = mapper.writeValueAsString(
                java.util.Map.of(
                        "title", "Forged body",
                        "language", "java",
                        "code", "public class Sample {}",
                        "problemDescription", "sample problem",
                        "userId", bobId
                )
        );

        mockMvc.perform(post("/api/code-submissions")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + aToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(aliceId));
    }

    @Test
    @Rollback
    void userCannotReadOrListAnotherUsersSubmission() throws Exception {
        String aEmail = uniqueEmail();
        register("Alice", aEmail);
        String aToken = login(aEmail, PASSWORD).get("token").asText();
        long aId = login(aEmail, PASSWORD).get("id").asLong();
        long aSubmissionId = submitCode(aToken, "Alice's submission");

        String bEmail = uniqueEmail();
        register("Bob", bEmail);
        String bToken = login(bEmail, PASSWORD).get("token").asText();

        mockMvc.perform(get("/api/code-submissions/" + aSubmissionId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bToken))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/code-submissions/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(get("/api/code-submissions/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + aToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].userId", hasItem((int) aId)));
    }

    @Test
    @Rollback
    void userCannotProfileAnotherUsersSubmission() throws Exception {
        String aEmail = uniqueEmail();
        register("Alice", aEmail);
        String aToken = login(aEmail, PASSWORD).get("token").asText();
        long aSubmissionId = submitCode(aToken, "Secret submission");

        String bEmail = uniqueEmail();
        register("Bob", bEmail);
        String bToken = login(bEmail, PASSWORD).get("token").asText();

        String body = mapper.writeValueAsString(
                java.util.Map.of("submissionId", aSubmissionId)
        );
        mockMvc.perform(post("/api/code-reviews")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------------
    // IDOR: code reviews
    // ---------------------------------------------------------------

    @Test
    @Rollback
    void userCanReadOwnReviewButNotOthers() throws Exception {
        String aEmail = uniqueEmail();
        register("Alice", aEmail);
        String aToken = login(aEmail, PASSWORD).get("token").asText();
        long aSubmissionId = submitCode(aToken, "Alice's submission");

        CodeSubmission submission =
                submissionRepository.findById(aSubmissionId).orElseThrow();
        CodeReview review = reviewRepository.save(
                new CodeReview(
                        submission,
                        "No bugs",
                        "Your code is fine",
                        "Consider adding validation",
                        "improved code",
                        85
                )
        );
        long reviewId = review.getId();

        mockMvc.perform(get("/api/code-reviews/" + reviewId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + aToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bug_analysis").value("No bugs"));

        String bEmail = uniqueEmail();
        register("Bob", bEmail);
        String bToken = login(bEmail, PASSWORD).get("token").asText();

        mockMvc.perform(get("/api/code-reviews/" + reviewId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bToken))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/code-reviews/submission/" + aSubmissionId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bToken))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/code-reviews/submission/" + aSubmissionId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + aToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quality_score").value(85));
    }
}