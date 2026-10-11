# CodeMentor AI — Backend

Spring Boot REST API powering the CodeMentor AI learning platform.

## Features

- User registration and login with JWT (BCrypt password hashing).
- Ownership-based access: users can only view/review their own code
  submissions (IDOR-safe — cross-user access returns 404).
- AI Code Review, Tutor Mode, and Test Case Generation via a hosted LLM API.
- Local Complexity Analysis (heuristic).
- Review history endpoints.

## Tech

- Java, Spring Boot (via Spring Initializr starter layout)
- Spring Security (stateless JWT) + jBCrypt
- MySQL (JPA/Hibernate)

## Configuration

The application reads every secret from environment variables (no secrets in
source, `.env` is git-ignored):

| Variable                  | Purpose                          |
|---------------------------|----------------------------------|
| `DB_USERNAME`             | MySQL user                       |
| `DB_PASSWORD`             | MySQL password                   |
| `JWT_SECRET`              | HS256 signing secret (>=32 chars)|
| `AI_SERVICE_URL`          | URL of the FastAPI AI service    |

## Run locally

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "yourpassword"
$env:JWT_SECRET = "a-very-long-secret-at-least-32-characters"
.\mvnw.cmd spring-boot:run
```

## Tests

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "yourpassword"
.\mvnw.cmd test
```

## Related repositories

- Frontend: https://github.com/poojapailla/codementor_frontend
- AI service: hosted separately on Render (FastAPI, GroqCloud-backed)