# Changelog

## [0.2.0](https://github.com/madgeek-arc/mailer/compare/0.1.0...0.2.0) (2026-11-02)

### Features

* optional API key for POST /mails ([3026184](https://github.com/madgeek-arc/mailer/commit/3026184378316ca0bee7d42b52c850c14759fadc))

### Bug Fixes

* apply default SMTP timeouts ([a9b6407](https://github.com/madgeek-arc/mailer/commit/a9b6407774c1fe1df7cc315716832829e8f551aa))
* drop credential-leaking mail debug defaults ([56a3eb8](https://github.com/madgeek-arc/mailer/commit/56a3eb801a75e92fcf47d445fde2fc5edafc31ce))
* make the mailer-client auto-configuration overridable ([a1ac9e7](https://github.com/madgeek-arc/mailer/commit/a1ac9e746016848c4c4ed876db5289a1084d30de))
* report invalid messages and delivery failures to the caller ([c6e7530](https://github.com/madgeek-arc/mailer/commit/c6e7530e399853b92a42aba341b7539adbab9259))

### Upgrade notes

* `POST /mails` no longer returns 201 when delivery fails. It returns 400 for a message without `from` or without a valid recipient (or one that can't be built), and 502 when the SMTP server rejects it. Callers using `mailer-client` get a `RestClientException`
* `mailer-client` requires `mailer.client.host` at startup and no longer ships an `application.properties` in its jar
* `mailer-service` fails to start when no mail account is configured
* When `mail.api-key` is set, `POST /mails` requires a matching `X-API-Key` header (401 otherwise). `mailer-client` sends it from `mailer.client.api-key`

## 0.1.0

Tagged `0.1.0`. Detailed notes weren't recorded at the time. See the
[commits between `mailer-0.0.1` and `0.1.0`](https://github.com/madgeek-arc/mailer/compare/mailer-0.0.1...0.1.0).

## 0.0.1

Tagged `mailer-0.0.1`. See the [commit history up to the tag](https://github.com/madgeek-arc/mailer/commits/mailer-0.0.1).
