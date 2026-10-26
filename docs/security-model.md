# Mailer — Security model

## Authentication and authorisation

When `mail.api-key` is set, `ApiKeyFilter` rejects every request whose `X-API-Key` header doesn't match
it with **401** (constant-time comparison). `mailer-client` sends the header from
`mailer.client.api-key`. (`config/ApiKeyFilter`, `MailClientConfig`, `MailsControllerTests`)

The key is optional. When it's unset the API is unauthenticated and a startup `WARN` says so. One key
covers all callers.

Mitigations:

- Set `mail.api-key` in every deployed environment.
- Expose the service only on an internal network, restricted to known calling services. Don't publish it
  through a public ingress.
- Serve it over HTTPS (or a trusted internal network), because the key travels in a header.

## Transport security

- **Client to service:** plain HTTP unless `mailer.client.host` is `https://` and TLS is terminated
  in front of the service.
- **Service to SMTP:** controlled per account through `props`. Use `mail.smtp.starttls.enable=true`
  (and ideally `mail.smtp.starttls.required=true`) or `mail.smtp.ssl.enable=true`. The tests cover
  implicit TLS (`MailerServiceIntegrationTests#deliversOverImplicitTlsWithAuth`). Don't copy the tests'
  `mail.smtp.ssl.trust=*` / `checkserveridentity=false` settings to production.

## Secrets

SMTP passwords (`mail.mailer.<name>.password`) and the API key (`mail.api-key`, `mailer.client.api-key`)
are the secrets. Inject them through the environment or a protected mounted file. Never commit them. See
[configuration.md](configuration.md#secrets).

Don't enable `mail.debug.auth` or `TRACE` logging for `jakarta.mail` in deployed environments: the SMTP
`AUTH` exchange contains base64-encoded credentials.

## Personal data

- The service processes recipient addresses and message bodies but doesn't store them.
- The service itself doesn't log message content. `mailer-client` logs the target URL and the full
  `EmailMessage` (recipients and body) at `DEBUG` (`MailClient#sendMail`). Keep `DEBUG` off for
  `gr.athenarc.messaging.mailer.client` in production.
- Redirect mode never sends original recipients in clear text. If enabled, they're appended only in
  masked form (`s***@g***.com`). The tests assert this. (`MailRedirector`,
  `MailRedirectorTests`, `MailerServiceIntegrationTests#redirectFooterCarriesOnlyMaskedRecipients`)

## Input handling

- Addresses are parsed with `InternetAddress`. Invalid ones are dropped. A message without `from` or
  without any valid recipient is rejected with 400. (`MailerService#validate`)
- With `html=true`, the HTML body is sent verbatim. Callers are responsible for escaping any
  user-supplied content they put in it.

## CORS / CSRF

No CORS configuration and no CSRF protection, since there's no browser-facing use.
