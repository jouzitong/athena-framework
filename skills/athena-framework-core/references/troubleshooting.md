# Troubleshooting

## Start with evidence

Run:

```bash
python3 .codex/skills/athena-framework-core/scripts/inspect_project.py --project .
python3 .codex/skills/athena-framework-core/scripts/validate_project.py --project .
./mvnw dependency:tree -Dincludes=org.athena
```

Then inspect the resolved version's POM and `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`.

## Dependency cannot resolve

Check, in order:

1. Resolved Athena version/property.
2. Repository URL and active Maven profile/mirror.
3. Repository ID matching the server ID in settings.
4. Package read permission without printing the credential.
5. Snapshot vs release repository.
6. Whether the artifact/status actually exists in the intended publication.

Do not add a second repository or hardcoded token as a quick fix.

## Auto-configuration is missing

- Confirm the starter is in the runtime dependency tree, not only dependency management.
- Confirm its imports resource lists the expected configuration.
- Check every `@ConditionalOnClass`, `@ConditionalOnProperty`, and `@ConditionalOnBean` condition.
- Use Spring Boot's condition evaluation report (`--debug`) in a controlled run.
- Check component/base-package scanning, especially OpenFeign's default `org.athena` package.
- Check whether a consumer bean caused the default to back off.

## Configuration does not bind

- Use the exact prefix from the resolved property class.
- Prefer kebab-case YAML; remember Spring relaxed binding accepts legacy camel case but the skill normalizes keys.
- Check profile activation, config import/bootstrap order, and environment placeholder availability.
- Look for the current snapshot's legacy `lib.common`, `lib.jdbc`, and `lib.web` prefixes alongside `athena.*` prefixes.
- Never log the bound object when it contains credentials.

## Security startup failure

- JWT type requires the JWT token starter and enabled JWT configuration.
- Local type requires the auth-core local manager.
- Redis token mode is not implemented in this snapshot.
- JPA and MyBatis user stores cannot both be enabled.
- Enabled JPA/MyBatis/authorization options require their respective starters.
- Replace default credentials/secrets before treating startup as secure.

## Data behavior differs

- Verify the actual current base service/controller class names.
- Check `IConvert` full-update vs edit mapping.
- Check `BaseRequest.filedQueries` spelling and supported query operators.
- Inspect entity annotations before assuming logical delete or optimistic locking.
- Check DDL automation flags and the real database dialect.
- Ensure driver dependencies are present because persistence starters use provided scope for drivers.

## WebSocket works locally but not in a cluster

The default bus, registry, sessions, subscriptions, and resume store are local/in-memory. Replace the coordinated set required for cross-instance delivery/recovery and test instance loss. A load balancer alone does not create shared state.

## Build output is ambiguous

Long or truncated output is not proof. Re-run the narrowest relevant command with bounded output and check the process exit code. Distinguish pre-existing warnings/test failures from regressions introduced by the change.
