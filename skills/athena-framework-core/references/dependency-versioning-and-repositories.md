# Dependency, versioning, and repositories

## Bundled baseline

The current snapshot records:

| Item | Value |
| --- | --- |
| Athena revision | `1.4.2-SNAPSHOT` |
| Java | 17 |
| Spring Boot | 3.2.5 |
| Spring Cloud | 2023.0.4 |
| Spring Cloud Alibaba | 2023.0.1.0 |
| MyBatis starter | 3.0.3 |
| MyBatis-Plus | 3.5.5 |

Read the manifest rather than copying these values when the skill has been refreshed.

## Import the BOM

Use one authoritative Athena version property:

```xml
<properties>
    <java.version>17</java.version>
    <athena.version>1.4.2-SNAPSHOT</athena.version>
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.athena</groupId>
            <artifactId>framework-dependencies</artifactId>
            <version>${athena.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Omit versions from managed Athena dependencies. Do not mix independently versioned Athena starters unless an explicit compatibility document requires it.

## Resolve repository source

Athena may come from the configured Nexus distribution, GitHub Packages, or a local Maven install. Determine the consumer's real repository before editing the POM.

- Keep repository credentials in `~/.m2/settings.xml`, CI secrets, or a Maven settings mirror.
- Match `<repository><id>` with `<server><id>` without reading or printing credentials.
- Never embed a token in a repository URL or project POM.
- For GitHub Packages, consumers need package read access even for public source repositories.

## Diagnose resolution

Run targeted Maven evidence commands:

```bash
./mvnw -q help:evaluate -Dexpression=athena.version -DforceStdout
./mvnw dependency:tree -Dincludes=org.athena
./mvnw help:effective-pom
```

Use `mvn` when no wrapper exists. Avoid relying only on declared POM text; parent properties, profiles, and dependency management may change the resolved result.

## Compare the skill snapshot

If the consumer version differs from `framework-manifest.json`:

1. Mark the bundled API/configuration details as potentially stale.
2. Locate the resolved artifact in the local Maven repository or obtain matching source.
3. Inspect its public types, POM dependencies, auto-configuration imports, and property classes.
4. Update code against that evidence.
5. Do not refresh the globally installed skill to a private or one-off fork unless that fork is the intended new baseline.

## Upgrade safely

1. Change the single Athena version source.
2. Resolve and compare `dependency:tree` before and after.
3. Review starter status, configuration prefixes/defaults, public API signatures, and conditional auto-configuration.
4. Compile all affected modules.
5. Run unit, integration, context-start, and external-service smoke checks as applicable.
6. Record any consumer adapter or configuration migration explicitly.

Snapshots are mutable by convention. Prefer immutable release versions for reproducible delivery when available.
