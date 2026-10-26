# Changelog

All notable changes to this project are documented in this file. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and the project uses
[Semantic Versioning](https://semver.org/).

## Unreleased

### Added
- Optional API key: when `mail.api-key` is set, `POST /mails` requires a matching `X-API-Key` header
  (401 otherwise). `mailer-client` sends it from `mailer.client.api-key`.
- Default SMTP timeouts (`connectiontimeout` 10 s, `timeout` and `writetimeout` 30 s, for `smtp` and
  `smtps`). Values under an account's `props` take precedence.
- Tests for the REST endpoint, the API key, message validation, timeouts and the client
  auto-configuration.
- Repository documentation (`docs/`), example configuration, contribution guide and this changelog.

### Changed
- **`POST /mails` no longer returns 201 when delivery fails.** It returns 400 for a message without
  `from` or without a valid recipient (or one that can't be built), and 502 when the SMTP server
  rejects it. Callers using `mailer-client` get a `RestClientException`.
- `mailer-service` fails to start when no mail account is configured.
- `mailer-client` requires `mailer.client.host` at startup, and its `Mailer` bean backs off when the
  application defines its own (`@ConditionalOnMissingBean`).

### Removed
- The `application.properties` shipped inside the `mailer-client` jar.
- `mail.debug.auth=true` and `logging.level.jakarta.mail=TRACE` from the packaged service defaults.
- Unused `Email`, `Status` and `MailerConfig` classes.

## 0.1.0

Tagged `0.1.0`. Detailed notes weren't recorded at the time. See the
[commits between `mailer-0.0.1` and `0.1.0`](https://github.com/madgeek-arc/mailer/compare/mailer-0.0.1...0.1.0).

## 0.0.1

Tagged `mailer-0.0.1`. See the [commit history up to the tag](https://github.com/madgeek-arc/mailer/commits/mailer-0.0.1).
