# Security Default JWT and Minimal Gateway Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make `athena-framework-starter-security` work as a minimal security layer for gateway and ordinary services, with JWT as the default token mode and `local` as an explicit fallback.

**Architecture:** Keep the request-authentication path in `auth-core`, keep token implementations in token-specific modules, and make login/user-repository wiring conditional on user modules being present. Gateway and read-only services should be able to depend on the core security starter plus JWT token support without pulling in user persistence or RBAC. User-facing login services can add `user-jpa` or `user-mybatis` to activate authentication, authorization, and enrichment beans.

**Tech Stack:** Maven multi-module Java, Spring Boot auto-configuration, Spring Security crypto, JWT, JPA, MyBatis.

---

### Task 1: Make JWT the default token mode and keep local as an explicit fallback

**Files:**
- Modify: `athena-project/athena-framework-starters/athena-framework-starter-security/athena-framework-starter-security-auth-core/src/main/java/org/athena/framework/security/auth/core/config/SecurityAuthProperties.java`
- Modify: `athena-project/athena-framework-starters/athena-framework-starter-security/athena-framework-starter-security/src/main/java/org/athena/framework/security/starter/properties/SecurityProperties.java`
- Modify: `athena-project/athena-framework-starters/athena-framework-starter-security/athena-framework-starter-security-token-jwt/src/main/java/org/athena/framework/security/token/jwt/config/SecurityJwtTokenAutoConfiguration.java`
- Modify: `athena-project/athena-framework-starters/athena-framework-starter-security/athena-framework-starter-security-auth-core/src/main/java/org/athena/framework/security/auth/core/config/SecurityAuthCoreAutoConfiguration.java`
- Modify: `athena-project/athena-framework-starters/athena-framework-starter-security/athena-framework-starter-security/src/main/java/org/athena/framework/security/starter/config/SecurityAutoConfiguration.java`

- [ ] **Step 1: Change the default token mode in configuration**

```java
// SecurityProperties.Token
private String type = "jwt";
```

```java
// SecurityAuthProperties
private boolean enabled = true;
// keep tokenHeader/tokenPrefix/ignoreUrls unchanged
```

- [ ] **Step 2: Make the JWT auto-configuration activate by default**

```java
@AutoConfiguration
@ConditionalOnBean(SecurityCoreMarker.class)
@ConditionalOnProperty(prefix = "athena.security.token", name = "type", havingValue = "jwt", matchIfMissing = true)
public class SecurityJwtTokenAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(TokenManager.class)
    public TokenManager tokenManager(ObjectMapper objectMapper, JwtTokenProperties properties) {
        return new JwtTokenManager(objectMapper, properties);
    }
}
```

- [ ] **Step 3: Add an explicit local fallback token manager in `auth-core`**

```java
@Bean
@ConditionalOnMissingBean(TokenManager.class)
@ConditionalOnProperty(prefix = "athena.security.token", name = "type", havingValue = "local")
public TokenManager tokenManager() {
    log.info("loading default LocalTokenManager");
    return new LocalTokenManager();
}
```

- [ ] **Step 4: Keep the startup validator aligned with the new default**

```java
// SecurityAutoConfiguration.SecurityTokenTypeValidator
// - accept "jwt" as the default mode
// - keep "local" as an explicit override
// - preserve the current startup error for unsupported values
```

- [ ] **Step 5: Verify the security modules still compile cleanly**

Run:

```bash
mvn -q -pl \
  athena-project/athena-framework-starters/athena-framework-starter-security/athena-framework-starter-security,\
  athena-project/athena-framework-starters/athena-framework-starter-security/athena-framework-starter-security-auth-core,\
  athena-project/athena-framework-starters/athena-framework-starter-security/athena-framework-starter-security-token-jwt \
  -am -DskipTests compile
```

Expected: compile success.

### Task 2: Make login/user beans conditional so gateway and read-only services do not need user modules

**Files:**
- Modify: `athena-project/athena-framework-starters/athena-framework-starter-security/athena-framework-starter-security-auth-core/src/main/java/org/athena/framework/security/auth/core/config/SecurityAuthCoreAutoConfiguration.java`
- Modify: `athena-project/athena-framework-starters/athena-framework-starter-security/athena-framework-starter-security-auth-core/src/main/java/org/athena/framework/security/auth/core/service/SecurityAuthenticationService.java`
- Modify: `athena-project/athena-framework-starters/athena-framework-starter-security/athena-framework-starter-security-auth-core/src/main/java/org/athena/framework/security/auth/core/web/SecurityAuthController.java`
- Modify: `athena-project/athena-framework-starters/athena-framework-starter-security/athena-framework-starter-security-auth-core/src/main/java/org/athena/framework/security/auth/core/service/DefaultAuthenticator.java`
- Modify: `athena-project/athena-framework-starters/athena-framework-starter-security/athena-framework-starter-security-auth-core/src/main/java/org/athena/framework/security/auth/core/service/DefaultIdentityProvider.java`

- [ ] **Step 1: Guard user-repository-backed beans with `@ConditionalOnBean(SecurityUserRepository.class)`**

```java
@Bean
@ConditionalOnBean(SecurityUserRepository.class)
@ConditionalOnMissingBean
public IdentityProvider identityProvider(SecurityUserRepository securityUserRepository) { ... }

@Bean
@ConditionalOnBean(SecurityUserRepository.class)
@ConditionalOnMissingBean
public Authenticator authenticator(SecurityUserRepository securityUserRepository,
                                   CredentialVerifier credentialVerifier) { ... }

@Bean
@ConditionalOnBean(SecurityUserRepository.class)
@ConditionalOnMissingBean
public SecurityAuthenticationService securityAuthenticationService(...) { ... }

@Bean
@ConditionalOnBean(SecurityAuthenticationFacade.class)
@ConditionalOnMissingBean
public SecurityAuthController securityAuthController(...) { ... }
```

- [ ] **Step 2: Keep the default credential verifier available only as a reusable fallback**

```java
@Bean
@ConditionalOnMissingBean
public CredentialVerifier credentialVerifier() {
    return new PlainCredentialVerifier();
}
```

- [ ] **Step 3: Leave `SecurityContextFilter` independent from user persistence**

```java
// no user repository injection here
// keep token extraction, parse result, enrichers, and request interceptors only
```

- [ ] **Step 4: Run a focused compile after the conditional wiring change**

Run:

```bash
mvn -q -pl \
  athena-project/athena-framework-starters/athena-framework-starter-security/athena-framework-starter-security,\
  athena-project/athena-framework-starters/athena-framework-starter-security/athena-framework-starter-security-auth-core,\
  athena-project/athena-framework-starters/athena-framework-starter-security/athena-framework-starter-security-user-jpa,\
  athena-project/athena-framework-starters/athena-framework-starter-security/athena-framework-starter-security-user-mybatis \
  -am -DskipTests compile
```

Expected: compile success.

### Task 3: Update module wiring and document the minimal dependency contract

**Files:**
- Modify: `athena-project/athena-framework-starters/athena-framework-starter-security/athena-framework-starter-security/pom.xml`
- Modify: `athena-project/athena-framework-starters/athena-framework-starter-security/README.md` if present in the module tree
- Modify: `athena-project/athena-framework-starters/athena-framework-starter-security/athena-framework-starter-security/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` if any new auto-config class needs to be listed

- [ ] **Step 1: Confirm the starter dependency surface matches the new gateway contract**

```xml
<!-- keep api + auth-core as the starter foundation -->
<!-- do not pull in user-jpa or user-mybatis here -->
```

- [ ] **Step 2: Document the intended application split**

```text
gateway / read-only service: starter-security + token-jwt
login / user service: starter-security + token-jwt + user-jpa or user-mybatis
authorization service: add authorization and rbac-related modules only where needed
```

- [ ] **Step 3: Re-run compile for the security parent and note the resulting contract**

Run:

```bash
mvn -q -pl athena-project/athena-framework-starters/athena-framework-starter-security -am -DskipTests compile
```

Expected: compile success, and the generated dependency graph still excludes user persistence from the gateway minimum path.

