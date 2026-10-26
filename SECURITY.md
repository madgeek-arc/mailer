# Security Policy

## Supported Versions

| Version | Supported |
|---|---|
| Latest 0.x release | :white_check_mark: |
| Older releases | :x: |

Fixes are released as a new version of each supported line; upgrade to it.

## Reporting a Vulnerability

Report vulnerabilities privately through GitHub: open the repository's **Security** tab and choose
**Report a vulnerability** ([direct link](https://github.com/madgeek-arc/mailer/security/advisories/new)).
Don't report them in public issues, pull requests or discussions.

Include:

- the affected version or commit;
- steps to reproduce, or a proof of concept;
- the impact you expect.

The maintainers follow up in the private advisory, fix the issue in the supported versions, and publish
the advisory once a fixed release is available.

## Scope

This repository covers `mailer-service`, `mailer-client` and `mailer-core`. A deployment must set
`mail.api-key` or otherwise restrict who can reach the service.

[docs/security-model.md](docs/security-model.md) describes the component's security model and the
settings a deployment should use.
