# Mailer — Development

## Tools

| Tool | Version | Evidence |
|---|---|---|
| JDK | 17+ | Spring Boot 3.5 parent default `java.version` |
| Maven | via wrapper (`./mvnw`) | `.mvn/wrapper`, `mvnw` |
| Docker | optional, for `spring-boot:build-image` | README |

## Common commands

```bash
./mvnw clean verify                    # build, tests, license header check
./mvnw initialize license:format       # add/refresh Apache 2.0 headers on *.java
./mvnw -pl mailer-service -am spring-boot:run \
  -Dspring-boot.run.arguments=--spring.config.additional-location=file:./application.properties
./mvnw install -DskipTests             # install core/client SNAPSHOTs for local consumers
```

Verified: `./mvnw verify` passes (29 tests).

## Local SMTP

Point an account at a local SMTP sink, for example GreenMail standalone or MailHog/Mailpit on
`localhost:1025`, with `mail.smtp.auth=false`. Alternatively, enable `mail.redirect.*` and send only
to your own address. See [configuration.md](configuration.md).

## Working on the client

1. `./mvnw install -DskipTests`.
2. In the consuming application, depend on `gr.athenarc.messaging:mailer-client:<SNAPSHOT>` and set
   `mailer.client.host` to the local service URL, including any context path (plus
   `mailer.client.api-key` if the service sets `mail.api-key`).

## Conventions

- Every `*.java` file needs the Apache header from `license-header.txt`. `validate` fails otherwise.
  The header check needs full git history.
- No formatter or linter is configured.
- Hot reload: not configured (no `spring-boot-devtools`).
