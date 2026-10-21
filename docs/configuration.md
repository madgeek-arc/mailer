# Mailer — Configuration

Both deployables are Spring Boot applications, so any Spring property source works:
`application.properties`, `--spring.config.additional-location`, environment variables through relaxed
binding, or command-line arguments. Precedence follows Spring Boot's standard order (command line >
environment > external files > packaged `application.properties`). A copyable example is in
[`../application-example.properties`](../application-example.properties).

## mailer-service

Bound by `mailer-service/src/main/java/gr/athenarc/messaging/mailer/config/MailerProperties.java`
(`@ConfigurationProperties(prefix = "mail")`).

| Property | Required | Default | Description |
|---|---|---|---|
| `mail.mailer.<name>.username` | When SMTP auth is used | — | SMTP user for account `<name>` |
| `mail.mailer.<name>.password` | When SMTP auth is used | — | SMTP password. **Secret** |
| `mail.api-key` | No (recommended) | — | When set, every request must carry it in `X-API-Key` (401 otherwise). When unset, the API is unauthenticated and a startup `WARN` says so. **Secret** |
| `mail.mailer.<name>.props.*` | Yes (at least one account, otherwise startup fails) | — | Passed to `jakarta.mail.Session.getInstance`, for example `mail.smtp.host`, `mail.smtp.port`, `mail.smtp.auth`, `mail.smtp.starttls.enable`, `mail.smtp.ssl.enable`, `mail.transport.protocol` |
| `mail.mailer.<name>.props.mail.smtp.connectiontimeout` / `.timeout` / `.writetimeout` | No | `10000` / `30000` / `30000` ms | Applied when not set. The same defaults apply to `mail.smtps.*` |
| `mail.mailer.<name>.props.mail.from` | Recommended | — | Sender address that selects this account |
| `mail.mailer.<name>.props.mail.display-name` | No | — | Display name for the `From` header (read in `MailerService#createMessage`) |
| `mail.redirect.enabled` | No | `false` | Send every message to `mail.redirect.to` instead of its recipients |
| `mail.redirect.to` | When redirect is enabled | packaged: `dev1@example.org,dev2@example.org` | Comma-separated targets. Startup fails if none of them is valid |
| `mail.redirect.include-original-recipients-masked` | No | `false` | Append masked original recipients to the body |
| `server.port` | No | `8080` | Spring Boot standard |
| `server.servlet.context-path` | No | none | Context path. Clients must include it in `mailer.client.host` |

Account selection: the first account, in configuration order, whose `mail.from` equals the message
`from`, or whose `<name>` is a substring of `from`. Otherwise the first account.
(`MultiMailerServiceService#getSessionFromHost`)

Packaged defaults in `mailer-service/src/main/resources/application.properties` include a placeholder
account `XXX` with empty credentials. Override it in deployed environments.

## mailer-client

Bound by `mailer-client/.../client/config/MailClientProperties.java`
(`@ConfigurationProperties("mailer")`).

| Property | Required | Default | Description |
|---|---|---|---|
| `mailer.client.host` | **Yes** | — | Base URL of `mailer-service`, including any context path, for example `http://mailer:8080/mailer`. Startup fails if missing |
| `mailer.client.api-key` | When the service sets `mail.api-key` | — | Sent as `X-API-Key`. **Secret** |
| `mailer.client.connect-timeout` | No | `5s` | `Duration` |
| `mailer.client.read-timeout` | No | `10s` | `Duration` |

The auto-configured `Mailer` bean backs off when the application defines its own `Mailer`
(`@ConditionalOnMissingBean`). (`MailClientConfig#mailerClient`)

## Environment differences

| Environment | Typical settings |
|---|---|
| Local / tests | A local SMTP sink. The tests use GreenMail on dynamic ports (`MailerServiceIntegrationTests`) |
| Test / beta | `mail.redirect.enabled=true` with team addresses, so real users are never emailed |
| Production | Real SMTP accounts, redirect disabled, `mail.api-key` set |

Values are set per deployment through environment variables or an external configuration. They aren't stored in the repository.

## Secrets

`mail.mailer.<name>.password`, `mail.api-key` and `mailer.client.api-key` are secrets. Supply them
through the environment, for example `MAIL_MAILER_<NAME>_PASSWORD` or `MAIL_APIKEY` (Spring Boot relaxed binding), or
through a mounted, access-restricted properties file. Never commit them.
