# Mailer — Testing

| Suite | Module | Type | What it covers |
|---|---|---|---|
| `MailerApplicationTests` | `mailer-service` | Spring context | Application context starts with the packaged properties |
| `MailsControllerTests` | `mailer-service` | Web layer (`@WebMvcTest`, mocked `MailerService`) | 201, 400, 415 and 502 responses; API key filter (401 without or with a wrong key, 201 with the right one) |
| `service/MailerServiceIntegrationTests` | `mailer-service` | Integration (GreenMail JUnit 5, in-process SMTP/SMTPS on dynamic ports) | Delivery to TO/CC/BCC (BCC absent from headers), HTML content type, account selection by sender, redirect mode with and without the masked footer, SMTP AUTH success and failure (502), implicit TLS, missing sender and recipients (400), no configured accounts, default SMTP timeouts |
| `service/MailRedirectorTests` | `mailer-service` | Unit | Recipient replacement, subject prefix, masking rules, HTML footer, startup validation |
| `client/config/MailClientConfigTests` | `mailer-client` | Auto-configuration (`ApplicationContextRunner`, in-JVM HTTP server) | Startup failure without `mailer.client.host`, back-off when the application defines a `Mailer`, `X-API-Key` sent only when configured |

## Running

```bash
./mvnw test                              # all modules
./mvnw -pl mailer-service test -Dtest=MailRedirectorTests
```

No external services are needed. GreenMail runs inside the JVM.

Result: 29 tests, 0 failures. The `ERROR ... 535 5.7.8 Authentication credentials invalid` log line
during the run is expected: it comes from `rejectsWrongPassword`.

## Coverage and CI

No coverage tool (JaCoCo) and no CI configuration are in the repository.

## Gaps

- No test of `MailClient` against a real `mailer-service` (end to end).
- `mailer-core` has no tests (it holds only the DTO, interface and constants).
