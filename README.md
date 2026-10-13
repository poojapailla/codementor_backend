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
| `DB_URL`                  | JDBC URL (defaults to Aiven MySQL with `sslMode=VERIFY_CA`) |
| `DB_USERNAME`             | MySQL user (defaults to `avnadmin`) |
| `DB_PASSWORD`             | MySQL password (required)        |
| `DB_SSL_KEYSTORE`         | Server-cert truststore URL (defaults to `file:certs/aiven-truststore.p12`, auto-generated at startup from the bundled CA) |
| `DB_SSL_KEYSTORE_PASSWORD`| Truststore password (default `codementor-ca`) |
| `JWT_SECRET`              | HS256 signing secret (>=32 chars)|
| `AI_SERVICE_URL`          | URL of the FastAPI AI service    |

## Run locally

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/codementor_db"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "yourpassword"
$env:JWT_SECRET = "a-very-long-secret-at-least-32-characters"
.\mvnw.cmd spring-boot:run
```

Pointing at the Aiven (cloud) database requires no `DB_URL` override — just
set `DB_PASSWORD` to the Aiven `avnadmin` password. The CA certificate bundled
at `src/main/resources/certs/aiven-ca.pem` is exported to `./certs/` (a
PKCS12 truststore) on startup and loaded by MySQL Connector/J with
`sslMode=VERIFY_CA`, so the server certificate is verified against Aiven.

## Tests

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "yourpassword"
.\mvnw.cmd test
```

## Related repositories

- Frontend: https://github.com/poojapailla/codementor_frontend
- AI service: hosted separately on Render (FastAPI, GroqCloud-backed)