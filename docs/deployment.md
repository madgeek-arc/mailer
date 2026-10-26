# Mailer — Deployment

The repository contains no Dockerfile, compose file, Kubernetes manifest or CI pipeline. The steps
below are the build mechanisms it provides.

## Prerequisites

- JDK 17+ and network access to Maven Central (the wrapper downloads Maven).
- A **full git clone**. `license-maven-plugin-git` derives copyright years from history, so shallow
  clones fail the `validate` phase. (README, `pom.xml`)
- Reachable SMTP server(s) and their credentials.
- Docker daemon, only for image builds.

## Build artefacts

| Command | Artefact |
|---|---|
| `./mvnw clean package` | `mailer-service/target/mailer-service-<version>.jar` (Spring Boot executable jar), plus `mailer-core` and `mailer-client` jars |
| `./mvnw install -DskipTests && ./mvnw -pl mailer-service spring-boot:build-image` | OCI image `mailer-service:<version>` (Cloud Native Buildpacks) |
| `mvn release:prepare release:perform` | Tag `<version>`, deploy to `madgik-releases` (`pom.xml` `distributionManagement`) |

Images are built with `spring-boot:build-image` (the POM also declares `native-maven-plugin` for the
image build). A CI job outside the repository builds and tags them like this:

```bash
mvn -U clean install
image="docker.madgik.di.uoa.gr/mailer-service:$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout | tail -1 | sed s/-SNAPSHOT//g)"
mvn -f mailer-service/pom.xml spring-boot:build-image -Dspring-boot.build-image.imageName=${image}
```

The image build uses only `mailer-service/pom.xml`, so it takes `mailer-core` from the local Maven
repository. `install` puts the freshly built modules there; with `package` alone, the build compiles
against whichever `mailer-core` snapshot was previously installed or downloaded.

## Deployment options

Production deployment isn't defined in this repository. The service can run as:

- the executable jar, managed as a system service (for example a systemd unit);
- a container built from the OCI image, started with Docker Compose or deployed on Kubernetes.

## Runtime

| Aspect | Value |
|---|---|
| Port | `8080` (`server.port`) |
| Context path | none by default. Set `server.servlet.context-path` (the README uses `/mailer`) |
| Volumes | none required. Optionally mount `/config/application.properties` read-only |
| Outbound network | SMTP host/port of each configured account |
| Dependent services | SMTP only. Deploy `mailer-service` before the applications that call it |
| Database / migrations | none |

```bash
java -jar mailer-service/target/mailer-service-<version>.jar \
  --spring.config.additional-location=file:./application.properties
```

See the README for a compose example with a mounted properties file.

## Production requirements

- Set `mail.api-key`, give the same value to callers as `mailer.client.api-key`, and expose the API on
  an internal network only ([security-model.md](security-model.md)).
- Set `mail.redirect.enabled=false`.

## Post-deployment verification

There's no health endpoint (no actuator). Verify with a test message to a team mailbox:

```bash
curl -i -X POST http://<host>:8080/<context-path>/mails -H 'Content-Type: application/json' \
  -H 'X-API-Key: <api-key>' \
  -d '{"from":"<sender-address>","to":["<team-address>"],"subject":"deploy check","text":"ok"}'
```

A `201` means the SMTP server accepted the message. A `502` means delivery failed; the service log has
the SMTP error.

## Rollback

The service is stateless, so redeploy the previous jar or image tag. Messages that failed in between
are not recoverable from the service.
