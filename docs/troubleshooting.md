# Mailer — Troubleshooting

| Problem | Possible cause | What to check | Resolution |
|---|---|---|---|
| `502` from `/mails` | The SMTP server rejected the message or couldn't be reached | Service log: `ERROR` from `MailerService` (for example `535 Authentication credentials invalid`) | Fix the account credentials or SMTP settings under `mail.mailer.<name>.*` |
| `400` from `/mails` | `from` missing, no valid recipient, or malformed JSON | The request body | Send `from` and at least one valid `to`/`cc`/`bcc` address |
| `401` from `/mails` | `mail.api-key` is set and the caller's `X-API-Key` is missing or different | The caller's `mailer.client.api-key` | Use the same value on both sides |
| Mail goes out from the wrong account | `from` doesn't match any `mail.from`, so the first account is used | `WARN No mailer session found for sender` | Make `from` equal the account's `mail.from`, or reorder the accounts |
| Every mail goes to the dev team with `[REDIRECTED]` in the subject | `mail.redirect.enabled=true` | Startup `WARN Mail redirect is ACTIVE` | Disable redirect in production |
| Startup fails: `mail.redirect.enabled is true but mail.redirect.to has no valid addresses` | Redirect on with an empty or invalid list | `mail.redirect.to` | Provide at least one valid address, or disable redirect |
| Startup fails: `No mail account configured` | No `mail.mailer.<name>.*` block, or none could be built | Effective configuration, `ERROR Could not create session for provider` | Configure at least one account |
| Client startup fails: `mailer.client.host must be set` | Property missing in the calling application | Effective configuration of the caller | Set `mailer.client.host` to an absolute `http(s)://` URL including the context path |
| Client: `RestClientException` / `ResourceAccessException` | Wrong `mailer.client.host`, missing context path, service down, or timeout | `mailer.client.host` in the caller, reachability of the service | Correct the URL (include the context path) or restore the service |
| Build fails at `validate` with license header errors | Missing headers, or a shallow clone | `./mvnw license:check` output, `git rev-parse --is-shallow-repository` | `./mvnw initialize license:format`. Use a full clone |
