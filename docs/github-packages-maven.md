# GitHub Packages Maven Publishing

This project now includes a `github-packages` Maven profile so we can publish the built artifacts to GitHub Packages without changing the existing Nexus publishing configuration.

## 1. Prepare a GitHub repository

Create or choose a repository that will host the Maven package, for example:

- owner: `your-org`
- repository: `athena-framework-packages`

The Maven registry URL will be:

```text
https://maven.pkg.github.com/your-org/athena-framework-packages
```

## 2. Create a GitHub token

For local publishing with Maven, GitHub Packages requires a Personal Access Token (classic).

Required scopes:

- `write:packages`
- `read:packages`
- `repo`

## 3. Configure local Maven credentials

Copy the template from [docs/maven/settings-github-packages.xml](/home/workroom/items/athena-framework/docs/maven/settings-github-packages.xml) into `~/.m2/settings.xml`, then replace:

- `YOUR_GITHUB_USERNAME`
- `YOUR_GITHUB_CLASSIC_TOKEN`

The `<server><id>` must stay as `github`, because the project publishes with that id.

## 4. Publish locally

Release-style version example:

```bash
mvn -Pgithub-packages \
  -DskipTests \
  -Dgithub.packages.owner=your-org \
  -Dgithub.packages.repo=athena-framework-packages \
  -Drevision=1.3.0-company-1 \
  clean deploy
```

Snapshot-style version example:

```bash
mvn -Pgithub-packages \
  -DskipTests \
  -Dgithub.packages.owner=your-org \
  -Dgithub.packages.repo=athena-framework-packages \
  -Drevision=1.3.0-SNAPSHOT \
  clean deploy
```

## 5. Consume from another project

Add the GitHub repository to the consuming project's `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/your-org/athena-framework-packages</url>
    </repository>
</repositories>
```

Add credentials for the same `github` server id in the consumer machine's `~/.m2/settings.xml`.

Then depend on the published version:

```xml
<dependency>
    <groupId>org.athena</groupId>
    <artifactId>athena-framework-starter-web</artifactId>
    <version>1.3.0-company-1</version>
</dependency>
```

## 6. Optional GitHub Actions publishing

This repository also includes a workflow at [publish-github-packages.yml](/home/workroom/items/athena-framework/.github/workflows/publish-github-packages.yml).

Before using it:

1. Push this project to GitHub.
2. Open the workflow in GitHub Actions.
3. Use `Run workflow`.
4. Provide the version you want to publish.

The workflow publishes to the current GitHub repository owner/name automatically.

## 7. Versioning recommendation

Avoid republishing the exact same release version repeatedly. Prefer:

- `1.3.0-company-1`
- `1.3.0-company-2`

For frequent internal rebuilds, use:

- `1.3.0-SNAPSHOT`
