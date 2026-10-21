# Mailer — API

The service exposes a single endpoint. It doesn't generate OpenAPI or Swagger. (no
springdoc dependency in `mailer-service/pom.xml`)

| Item | Value |
|---|---|
| Base URL | `http://<host>:<server.port><server.servlet.context-path>`. The repository sets no context path, so the default is `/`. The README examples use `/mailer` |
| Versioning | None in the path |
| Authentication | Optional shared key in the `X-API-Key` header, required when `mail.api-key` is set (see [security.md](security.md)) |
| Content type | `application/json` |

## `POST /mails`

Source: `mailer-service/src/main/java/gr/athenarc/messaging/mailer/MailsController.java#send`. The
path constant is `mailer-core/.../RelativePaths.MAILS`.

Request body (`mailer-core/.../domain/EmailMessage.java`):

| Field | Type | Notes |
|---|---|---|
| `from` | string | **Required.** Used to pick the sender account |
| `to`, `cc`, `bcc` | array of strings | At least one valid address across the three is required. Invalid addresses are dropped |
| `subject` | string | Defaults to `[No Subject]` |
| `text` | string | Body. Defaults to empty |
| `html` | boolean | `true` sends `text` as `text/html; charset=utf-8` |

Responses:

| Status | When |
|---|---|
| `201 Created` | The SMTP server accepted the message |
| `400 Bad Request` | Malformed JSON, missing `from`, no valid recipient, or a message that can't be built |
| `401 Unauthorized` | `mail.api-key` is set and `X-API-Key` is missing or wrong |
| `415 Unsupported Media Type` | Content type isn't `application/json` |
| `502 Bad Gateway` | The SMTP server rejected the message or couldn't be reached (see [integration.md](integration.md#failure-behaviour)) |

See the README for a `curl` example.
