# Contributing

Contributions are welcome: bug reports, feature requests and pull requests.

## Reporting issues

Use [GitHub Issues](https://github.com/madgeek-arc/mailer/issues) for bugs, feature requests and
questions. For bugs, include the version, the environment, the steps to reproduce, and the expected
and actual behaviour.

## Branching

- `main` is the integration branch and always carries the next `-SNAPSHOT` version.
- Work on short-lived branches named `<type>/<short-description>` (for example `feat/async-delivery`,
  `docs/deployment`) and open a pull request against `main`.
- Without write access to the repository, fork it, create the branch in your fork and open the
  pull request from there.

## Commit messages

Use [Conventional Commits](https://www.conventionalcommits.org/): `feat:`, `fix:`, `docs:`, `refactor:`,
`test:`, `build:`, `chore:`. The history already follows this, and the release plugin prefixes its
commits with `chore(release):`.

## Pull requests

- Describe the change and its motivation, and link the related issue.
- At least one maintainer review is required before merging.
- Update `README.md`/`docs/` when the API, configuration or deployment changes.
- Add an entry to `CHANGELOG.md` under *Unreleased*.

## Required checks

- `./mvnw clean verify` must pass. This includes the tests and the license header check.
- New `*.java` files need the Apache 2.0 header: run `./mvnw initialize license:format`. The check needs a full
  (non-shallow) clone.
- Changes to sending or redirect behaviour need GreenMail integration tests.

## Releases and versioning

- [Semantic Versioning](https://semver.org/).
- Release with `maven-release-plugin` (`./mvnw release:prepare release:perform`). Tags are named
  `<version>` (`tagNameFormat=@{project.version}`), and artefacts deploy to the repositories in
  `pom.xml` `distributionManagement`.
- Move the *Unreleased* changelog entries under the new version heading as part of the release.
