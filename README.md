# Mailer

A small email-sending service with a Spring Boot client. Applications post a JSON message to the service, which delivers it over SMTP using one of several configured sender accounts.

## Modules

| Module | Description |
|--------|-------------|
| `mailer-core` | Shared `EmailMessage` model, `Mailer` interface and REST paths |
| `mailer-service` | Spring Boot application exposing the REST API and sending mail over SMTP |
| `mailer-client` | Spring Boot auto-configured `Mailer` implementation that calls the service |

## Using the client

Add the dependency (Java 17+, Spring Boot 3):

```xml
<dependency>
    <groupId>gr.athenarc.messaging</groupId>
    <artifactId>mailer-client</artifactId>
    <version>0.1.0</version>
</dependency>
```

Configure the service location:

```properties
mailer.client.host=http://localhost:8080/mailer
# optional, defaults shown
mailer.client.connect-timeout=5s
mailer.client.read-timeout=10s
```

The client auto-configures a `Mailer` bean; inject it and send:

```java
@Service
public class Notifications {

    private final Mailer mailer;

    public Notifications(Mailer mailer) {
        this.mailer = mailer;
    }

    public void welcome(String address) {
        mailer.sendMail(new EmailMessage.EmailBuilder("no-reply@example.org", List.of(address))
                .setSubject("Welcome")
                .setText("<p>Hello!</p>")
                .setHtml(true)
                .build());
    }
}
```

`EmailMessage` fields: `from`, `to`, `cc`, `bcc`, `subject`, `text`, `html` (`true` sends `text` as HTML).

A failed request (connection error, non-2xx response) surfaces as a Spring `RestClientException`.

### Calling the API directly

```bash
curl -X POST http://localhost:8080/mailer/mails \
  -H 'Content-Type: application/json' \
  -d '{
        "from": "no-reply@example.org",
        "to": ["user@example.org"],
        "subject": "Hello",
        "text": "Hi there",
        "html": false
      }'
```

Returns `201 Created` on success.

## Running the service

### Build

```bash
./mvnw clean package
```

This runs the tests and the license header check, and produces an executable jar at `mailer-service/target/mailer-service-<version>.jar`.

### Configure

Sender accounts are defined under `mail.mailer.<name>.*`; the settings under `props` are passed to Jakarta Mail as-is.

```properties
server.port=8080
server.servlet.context-path=/mailer

mail.mailer.example.username=no-reply@example.org
mail.mailer.example.password=secret
mail.mailer.example.props.mail.from=no-reply@example.org
mail.mailer.example.props.mail.host=smtp.example.org
mail.mailer.example.props.mail.smtp.port=587
mail.mailer.example.props.mail.smtp.auth=true
mail.mailer.example.props.mail.smtp.starttls.enable=true
```

The sender account for a message is chosen by matching the message's `from` against each account's `mail.from`, or against the account name appearing in `from`. If nothing matches, the first configured account is used.

#### Redirecting mail (testing and beta)

```properties
mail.redirect.enabled=true
mail.redirect.to=dev1@example.org,dev2@example.org
mail.redirect.include-original-recipients-masked=true
```

When enabled, every message goes to the listed addresses instead of its real recipients, optionally with the masked original recipients appended to the body. Startup fails if enabled without at least one valid address.

### Run

```bash
java -jar mailer-service/target/mailer-service-<version>.jar \
  --spring.config.additional-location=file:./application.properties
```

Any property can also be supplied as an environment variable (e.g. `MAIL_REDIRECT_ENABLED=true`).

### Docker

Build an image with the Spring Boot buildpacks (requires a running Docker daemon):

```bash
./mvnw install -DskipTests
./mvnw -pl mailer-service spring-boot:build-image
```

The first command installs `mailer-core` into the local repository; the second builds the image for the service module only (running the goal on `mailer-core` would fail, as it has no main class).

The image is named `mailer-service:<version>`. Run it with a properties file via `docker-compose.yml`, adjusting the image tag to the built version:

```yaml
services:
  mailer:
    image: docker.io/library/mailer-service:<version>
    ports:
      - 8080:8080
    env_file: application.properties
```

```bash
docker compose up -d
```

Note that `env_file` expects `KEY=value` lines, and dotted property names are not valid environment variable names for every runtime. If the properties are not picked up, mount the file instead and point Spring at it:

```yaml
    volumes:
      - ./application.properties:/config/application.properties:ro
```

Spring Boot loads `/config/application.properties` automatically.

## Contributing: license headers

Every Java file carries an Apache 2.0 header, checked during `validate`. After adding or editing files, run:

```bash
./mvnw license:format
```

Copyright years are derived from each file's git history, so builds need a full (non-shallow) clone.

## License

Apache License 2.0. See [LICENSE](LICENSE).
