package com.codementor.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"app.jwt.secret=unit-test-secret-that-is-long-enough-for-hs256-1234567890",
		"app.jwt.expiration-minutes=1440"
})
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
