# Mailer — Operations

## Start / stop

A standard Spring Boot process. Stop it with `SIGTERM`. Graceful shutdown is enabled by default in
Spring Boot 3.4+ (this repository uses 3.5 and doesn't override `server.shutdown`), so in-flight
requests finish before exit.

## Health, readiness, liveness

None. `spring-boot-starter-actuator` isn't a dependency. (`mailer-service/pom.xml`).
Use a TCP check on the service port, or the post-deployment test message in
[deployment.md](deployment.md#post-deployment-verification).

## Logging

| Logger | Packaged level | Content |
|---|---|---|
| root | `INFO` | Startup, a warning when redirect mode is active (`Mail redirect is ACTIVE ...`) |
| `gr.athenarc.messaging.mailer.config.ApiKeyFilter` | inherited | `WARN mail.api-key is not set ...` at startup when the API is unauthenticated |
| `gr.athenarc.messaging.mailer.service.MailerService` | inherited | `ERROR` on SMTP send failures, with exception |
| `MultiMailerServiceService` | inherited | `WARN No mailer session found for sender, attempting to use default`, `ERROR Could not create session for provider` |

Backend: Log4j2 with Spring Boot's default console layout. No file appender is configured.
(`mailer-service/src/main/resources/application.properties`, POMs)

Delivery failures are returned to the caller as `502` and logged at `ERROR`. Alert on `ERROR` lines from
`gr.athenarc.messaging.mailer` to catch failures that callers ignore.

## Metrics and alerting

None built in.

## Background jobs, schedules, queues

None. Every send is synchronous within the HTTP request.

## Data, backups, restore

No persistent state, so there's nothing to back up apart from the configuration and its secrets.

## Dependencies

- Requires: the SMTP server(s) configured under `mail.mailer.*`.
