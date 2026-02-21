# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Blog platform for FixDev built with Spring Boot 3 + Kotlin. Features include article management, tag filtering, HTMX-powered comments, Keycloak OAuth2 authentication, and an admin panel.

## Tech Stack

- **Backend:** Spring Boot 3.4.2, Kotlin 2.1.0, JDK 21 (Corretto)
- **Database:** PostgreSQL 17, Flyway migrations, Spring Data JPA
- **Auth:** Keycloak OIDC via Spring Security OAuth2
- **Frontend:** Thymeleaf + Layout Dialect, HTMX 2.0.4
- **Build:** Gradle 8.12 (Kotlin DSL)
- **Testing:** JUnit 5, mockito-kotlin, H2 in-memory DB

## Development

```bash
# Run tests
./gradlew test

# Build JAR
./gradlew bootJar

# Run locally (requires PostgreSQL + Keycloak)
./gradlew bootRun

# Docker production
docker compose -f docker-compose.prod.yml up --build
```

**Prerequisites:** PostgreSQL on `localhost:5432`, Keycloak on `localhost:8180`. See `docker-compose.yml` for dev services, `application.yml` for config.

## Architecture

```
src/main/kotlin/io/fixdev/blog/
├── config/          # SecurityConfig, KeycloakRoleConverter, MediaProperties
├── controller/      # PublicController, CommentController
│   └── admin/       # Admin CRUD controllers (articles, tags, comments, media)
├── model/
│   ├── dto/         # ArticleForm
│   └── entity/      # JPA entities (User, Article, Tag, Comment, Media)
├── repository/      # Spring Data JPA repositories
└── service/         # ArticleService, CommentService, TagService, MediaService, etc.
```

### Key Patterns

- **Security:** `@WebMvcTest` tests mock `ClientRegistrationRepository` to avoid OIDC discovery. Admin endpoints require `ROLE_ADMIN`.
- **Templates:** Thymeleaf with Layout Dialect. Public pages use `layout/base.html`, admin pages use `admin/layout/admin-base.html`.
- **Comments:** HTMX POST to `/blog/{slug}/comments`, returns fragment `fragments/comment :: comment`.
- **Slugs:** `SlugService` transliterates Cyrillic to Latin for URL-friendly slugs.
- **HTML sanitization:** `HtmlSanitizer` uses jsoup Safelist to prevent XSS in article content.
- **Media:** Files stored on disk at `app.media.upload-dir`, metadata in DB.

### Test Configuration

- Test profile: `application-test.yml` uses H2, disables Flyway and sessions.
- `@WebMvcTest` tests must `@Import(SecurityConfig::class, KeycloakRoleConverter::class)` and mock `ClientRegistrationRepository`.
- `@DataJpaTest` tests work with H2 directly.

## Legacy

`index-v6.html` is the original single-page portfolio site (reference only). CSS/JS were extracted into `src/main/resources/static/`.
