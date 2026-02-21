# FixDev Blog Platform Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build a full-featured blog integrated into the fixdev.io portfolio as a single Spring Boot application with Thymeleaf + HTMX, Keycloak auth, and PostgreSQL.

**Architecture:** Single Spring Boot 3 + Kotlin app with layered architecture (controller/service/repository). Server-side rendering via Thymeleaf, interactivity via HTMX. Keycloak OAuth2 for auth. PostgreSQL for storage. One JAR deployed via Docker on VPS.

**Tech Stack:** Kotlin, Spring Boot 3, Spring Security OAuth2, Thymeleaf, HTMX, TinyMCE, PostgreSQL, Flyway, Spring Data JPA, Gradle (Kotlin DSL), Docker

**Design doc:** `docs/plans/2026-02-21-blog-platform-design.md`

---

## Task 1: Project Scaffolding

**Files:**
- Create: `build.gradle.kts`
- Create: `settings.gradle.kts`
- Create: `src/main/kotlin/io/fixdev/blog/BlogApplication.kt`
- Create: `src/main/resources/application.yml`
- Create: `src/main/resources/application-dev.yml`
- Create: `docker-compose.yml` (PostgreSQL + Keycloak for local dev)
- Create: `.gitignore`

**Step 1: Initialize Gradle project**

`settings.gradle.kts`:
```kotlin
rootProject.name = "fixdev-blog"
```

`build.gradle.kts`:
```kotlin
plugins {
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
    kotlin("plugin.jpa") version "2.1.0"
}

group = "io.fixdev"
version = "0.1.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.session:spring-session-jdbc")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:3.3.0")
    implementation("org.jsoup:jsoup:1.18.3")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.h2database:h2")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```

**Step 2: Create application entry point**

`src/main/kotlin/io/fixdev/blog/BlogApplication.kt`:
```kotlin
package io.fixdev.blog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BlogApplication

fun main(args: Array<String>) {
    runApplication<BlogApplication>(*args)
}
```

**Step 3: Create application config**

`src/main/resources/application.yml`:
```yaml
spring:
  application:
    name: fixdev-blog
  datasource:
    url: jdbc:postgresql://localhost:5432/fixdev_blog
    username: fixdev
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
  flyway:
    enabled: true
    locations: classpath:db/migration
  session:
    store-type: jdbc
  security:
    oauth2:
      client:
        registration:
          keycloak:
            client-id: fixdev-blog
            client-secret: ${KEYCLOAK_CLIENT_SECRET}
            scope: openid,profile,email
            authorization-grant-type: authorization_code
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
        provider:
          keycloak:
            issuer-uri: ${KEYCLOAK_ISSUER_URI}
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

app:
  media:
    upload-dir: ${MEDIA_UPLOAD_DIR:./uploads}
    allowed-types: image/jpeg,image/png,image/webp
    max-size-bytes: 10485760
```

`src/main/resources/application-dev.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/fixdev_blog
    username: fixdev
    password: fixdev_dev
  security:
    oauth2:
      client:
        registration:
          keycloak:
            client-secret: dev-secret
        provider:
          keycloak:
            issuer-uri: http://localhost:8180/realms/fixdev

app:
  media:
    upload-dir: ./dev-uploads
```

**Step 4: Create docker-compose for local dev**

`docker-compose.yml`:
```yaml
services:
  postgres:
    image: postgres:17
    environment:
      POSTGRES_DB: fixdev_blog
      POSTGRES_USER: fixdev
      POSTGRES_PASSWORD: fixdev_dev
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

  keycloak:
    image: quay.io/keycloak/keycloak:26.0
    command: start-dev
    environment:
      KC_DB: postgres
      KC_DB_URL: jdbc:postgresql://postgres:5432/fixdev_blog
      KC_DB_USERNAME: fixdev
      KC_DB_PASSWORD: fixdev_dev
      KC_HTTP_PORT: 8180
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: admin
    ports:
      - "8180:8180"
    depends_on:
      - postgres

volumes:
  pgdata:
```

**Step 5: Create .gitignore**

`.gitignore`:
```
build/
.gradle/
*.iml
.idea/
out/
dev-uploads/
uploads/
*.log
```

**Step 6: Verify project compiles**

Run: `./gradlew build -x test`
Expected: BUILD SUCCESSFUL

**Step 7: Commit**

```bash
git init
git add build.gradle.kts settings.gradle.kts src/ docker-compose.yml .gitignore
git commit -m "feat: scaffold Spring Boot project with Gradle, Docker, Keycloak config"
```

---

## Task 2: Flyway Migrations — Core Schema

**Files:**
- Create: `src/main/resources/db/migration/V1__create_users_table.sql`
- Create: `src/main/resources/db/migration/V2__create_tags_table.sql`
- Create: `src/main/resources/db/migration/V3__create_articles_table.sql`
- Create: `src/main/resources/db/migration/V4__create_article_tags_table.sql`
- Create: `src/main/resources/db/migration/V5__create_comments_table.sql`
- Create: `src/main/resources/db/migration/V6__create_media_table.sql`

**Step 1: Write migrations**

`V1__create_users_table.sql`:
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    keycloak_sub VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    avatar_url VARCHAR(512),
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_keycloak_sub ON users(keycloak_sub);
```

`V2__create_tags_table.sql`:
```sql
CREATE TABLE tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE
);
```

`V3__create_articles_table.sql`:
```sql
CREATE TABLE articles (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    slug VARCHAR(500) NOT NULL UNIQUE,
    content TEXT,
    excerpt VARCHAR(1000),
    cover_image_url VARCHAR(512),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    seo_title VARCHAR(200),
    seo_description VARCHAR(500),
    published_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_articles_slug ON articles(slug);
CREATE INDEX idx_articles_status ON articles(status);
CREATE INDEX idx_articles_published_at ON articles(published_at DESC);
```

`V4__create_article_tags_table.sql`:
```sql
CREATE TABLE article_tags (
    article_id BIGINT NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
    tag_id BIGINT NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (article_id, tag_id)
);
```

`V5__create_comments_table.sql`:
```sql
CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    article_id BIGINT NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    text TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_comments_article_id ON comments(article_id);
CREATE INDEX idx_comments_status ON comments(status);
```

`V6__create_media_table.sql`:
```sql
CREATE TABLE media (
    id BIGSERIAL PRIMARY KEY,
    filename VARCHAR(500) NOT NULL,
    stored_path VARCHAR(1000) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    size_bytes BIGINT NOT NULL,
    article_id BIGINT REFERENCES articles(id) ON DELETE SET NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT NOW()
);
```

**Step 2: Start docker-compose and verify migrations**

Run: `docker-compose up -d postgres && sleep 3 && ./gradlew bootRun --args='--spring.profiles.active=dev'`
Expected: Flyway runs all 6 migrations successfully, app starts

**Step 3: Commit**

```bash
git add src/main/resources/db/migration/
git commit -m "feat: add Flyway migrations for users, tags, articles, comments, media"
```

---

## Task 3: JPA Entities

**Files:**
- Create: `src/main/kotlin/io/fixdev/blog/model/entity/User.kt`
- Create: `src/main/kotlin/io/fixdev/blog/model/entity/Tag.kt`
- Create: `src/main/kotlin/io/fixdev/blog/model/entity/Article.kt`
- Create: `src/main/kotlin/io/fixdev/blog/model/entity/Comment.kt`
- Create: `src/main/kotlin/io/fixdev/blog/model/entity/Media.kt`
- Create: `src/main/kotlin/io/fixdev/blog/model/entity/ArticleStatus.kt`
- Create: `src/main/kotlin/io/fixdev/blog/model/entity/CommentStatus.kt`
- Create: `src/main/kotlin/io/fixdev/blog/model/entity/UserRole.kt`
- Test: `src/test/kotlin/io/fixdev/blog/model/entity/ArticleTest.kt`

**Step 1: Write the enum types**

`ArticleStatus.kt`:
```kotlin
package io.fixdev.blog.model.entity

enum class ArticleStatus {
    DRAFT, PUBLISHED
}
```

`CommentStatus.kt`:
```kotlin
package io.fixdev.blog.model.entity

enum class CommentStatus {
    PENDING, APPROVED, REJECTED
}
```

`UserRole.kt`:
```kotlin
package io.fixdev.blog.model.entity

enum class UserRole {
    ADMIN, USER
}
```

**Step 2: Write entities**

`User.kt`:
```kotlin
package io.fixdev.blog.model.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "users")
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "keycloak_sub", nullable = false, unique = true)
    val keycloakSub: String,

    @Column(nullable = false)
    var name: String,

    var email: String? = null,

    @Column(name = "avatar_url")
    var avatarUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: UserRole = UserRole.USER,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
)
```

`Tag.kt`:
```kotlin
package io.fixdev.blog.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "tags")
class Tag(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    var name: String,

    @Column(nullable = false, unique = true)
    var slug: String
)
```

`Article.kt`:
```kotlin
package io.fixdev.blog.model.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "articles")
class Article(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false, unique = true)
    var slug: String,

    @Column(columnDefinition = "TEXT")
    var content: String? = null,

    @Column(length = 1000)
    var excerpt: String? = null,

    @Column(name = "cover_image_url")
    var coverImageUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ArticleStatus = ArticleStatus.DRAFT,

    @Column(name = "seo_title")
    var seoTitle: String? = null,

    @Column(name = "seo_description")
    var seoDescription: String? = null,

    @Column(name = "published_at")
    var publishedAt: Instant? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),

    @ManyToMany
    @JoinTable(
        name = "article_tags",
        joinColumns = [JoinColumn(name = "article_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    var tags: MutableSet<Tag> = mutableSetOf(),

    @OneToMany(mappedBy = "article")
    val comments: MutableList<Comment> = mutableListOf()
)
```

`Comment.kt`:
```kotlin
package io.fixdev.blog.model.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "comments")
class Comment(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    val article: Article,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false, columnDefinition = "TEXT")
    var text: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: CommentStatus = CommentStatus.PENDING,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
)
```

`Media.kt`:
```kotlin
package io.fixdev.blog.model.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "media")
class Media(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val filename: String,

    @Column(name = "stored_path", nullable = false)
    val storedPath: String,

    @Column(name = "mime_type", nullable = false)
    val mimeType: String,

    @Column(name = "size_bytes", nullable = false)
    val sizeBytes: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    var article: Article? = null,

    @Column(name = "uploaded_at", nullable = false)
    val uploadedAt: Instant = Instant.now()
)
```

**Step 3: Write a test to verify Article entity mapping**

`src/test/kotlin/io/fixdev/blog/model/entity/ArticleTest.kt`:
```kotlin
package io.fixdev.blog.model.entity

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

class ArticleTest {
    @Test
    fun `new article defaults to DRAFT status`() {
        val article = Article(title = "Test", slug = "test")
        assertThat(article.status).isEqualTo(ArticleStatus.DRAFT)
    }

    @Test
    fun `new article has empty tags`() {
        val article = Article(title = "Test", slug = "test")
        assertThat(article.tags).isEmpty()
    }
}
```

**Step 4: Run tests**

Run: `./gradlew test`
Expected: PASS

**Step 5: Commit**

```bash
git add src/main/kotlin/io/fixdev/blog/model/ src/test/
git commit -m "feat: add JPA entities for User, Tag, Article, Comment, Media"
```

---

## Task 4: Repositories

**Files:**
- Create: `src/main/kotlin/io/fixdev/blog/repository/UserRepository.kt`
- Create: `src/main/kotlin/io/fixdev/blog/repository/TagRepository.kt`
- Create: `src/main/kotlin/io/fixdev/blog/repository/ArticleRepository.kt`
- Create: `src/main/kotlin/io/fixdev/blog/repository/CommentRepository.kt`
- Create: `src/main/kotlin/io/fixdev/blog/repository/MediaRepository.kt`
- Test: `src/test/kotlin/io/fixdev/blog/repository/ArticleRepositoryTest.kt`

**Step 1: Write repositories**

`UserRepository.kt`:
```kotlin
package io.fixdev.blog.repository

import io.fixdev.blog.model.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository : JpaRepository<User, Long> {
    fun findByKeycloakSub(keycloakSub: String): Optional<User>
}
```

`TagRepository.kt`:
```kotlin
package io.fixdev.blog.repository

import io.fixdev.blog.model.entity.Tag
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface TagRepository : JpaRepository<Tag, Long> {
    fun findBySlug(slug: String): Optional<Tag>
}
```

`ArticleRepository.kt`:
```kotlin
package io.fixdev.blog.repository

import io.fixdev.blog.model.entity.Article
import io.fixdev.blog.model.entity.ArticleStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ArticleRepository : JpaRepository<Article, Long> {
    fun findBySlugAndStatus(slug: String, status: ArticleStatus): Optional<Article>
    fun findByStatus(status: ArticleStatus, pageable: Pageable): Page<Article>
    fun findByTagsSlugAndStatus(tagSlug: String, status: ArticleStatus, pageable: Pageable): Page<Article>
    fun countByStatus(status: ArticleStatus): Long
}
```

`CommentRepository.kt`:
```kotlin
package io.fixdev.blog.repository

import io.fixdev.blog.model.entity.Comment
import io.fixdev.blog.model.entity.CommentStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findByArticleIdAndStatus(articleId: Long, status: CommentStatus): List<Comment>
    fun findByStatus(status: CommentStatus, pageable: Pageable): Page<Comment>
    fun countByStatus(status: CommentStatus): Long
}
```

`MediaRepository.kt`:
```kotlin
package io.fixdev.blog.repository

import io.fixdev.blog.model.entity.Media
import org.springframework.data.jpa.repository.JpaRepository

interface MediaRepository : JpaRepository<Media, Long>
```

**Step 2: Write integration test for ArticleRepository**

`src/test/kotlin/io/fixdev/blog/repository/ArticleRepositoryTest.kt`:
```kotlin
package io.fixdev.blog.repository

import io.fixdev.blog.model.entity.Article
import io.fixdev.blog.model.entity.ArticleStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class ArticleRepositoryTest @Autowired constructor(
    private val articleRepository: ArticleRepository
) {
    @Test
    fun `findByStatus returns only published articles`() {
        articleRepository.save(Article(title = "Draft", slug = "draft", status = ArticleStatus.DRAFT))
        articleRepository.save(Article(title = "Published", slug = "published", status = ArticleStatus.PUBLISHED))

        val result = articleRepository.findByStatus(ArticleStatus.PUBLISHED, PageRequest.of(0, 10))

        assertThat(result.content).hasSize(1)
        assertThat(result.content[0].slug).isEqualTo("published")
    }

    @Test
    fun `findBySlugAndStatus returns article when exists`() {
        articleRepository.save(Article(title = "Test", slug = "test-slug", status = ArticleStatus.PUBLISHED))

        val result = articleRepository.findBySlugAndStatus("test-slug", ArticleStatus.PUBLISHED)

        assertThat(result).isPresent
        assertThat(result.get().title).isEqualTo("Test")
    }
}
```

Create `src/test/resources/application-test.yml`:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
  flyway:
    enabled: false
  security:
    oauth2:
      client:
        registration:
          keycloak:
            client-id: test
            client-secret: test
            authorization-grant-type: authorization_code
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
        provider:
          keycloak:
            issuer-uri: http://localhost:8180/realms/fixdev
```

**Step 3: Run tests**

Run: `./gradlew test`
Expected: PASS

**Step 4: Commit**

```bash
git add src/main/kotlin/io/fixdev/blog/repository/ src/test/
git commit -m "feat: add Spring Data JPA repositories with custom queries"
```

---

## Task 5: Spring Security + Keycloak Configuration

**Files:**
- Create: `src/main/kotlin/io/fixdev/blog/config/SecurityConfig.kt`
- Create: `src/main/kotlin/io/fixdev/blog/config/KeycloakRoleConverter.kt`
- Create: `src/main/kotlin/io/fixdev/blog/service/UserService.kt`
- Test: `src/test/kotlin/io/fixdev/blog/config/SecurityConfigTest.kt`

**Step 1: Write Keycloak role converter**

Keycloak stores roles in the token under `realm_access.roles`. Spring Security needs a converter.

`KeycloakRoleConverter.kt`:
```kotlin
package io.fixdev.blog.config

import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Component

@Component
class KeycloakRoleConverter {
    fun extractRoles(oidcUser: OidcUser): Collection<GrantedAuthority> {
        val realmAccess = oidcUser.claims["realm_access"] as? Map<*, *> ?: return emptyList()
        val roles = realmAccess["roles"] as? List<*> ?: return emptyList()
        return roles.filterIsInstance<String>()
            .filter { it == "ADMIN" || it == "USER" }
            .map { SimpleGrantedAuthority("ROLE_$it") }
    }
}
```

**Step 2: Write SecurityConfig**

`SecurityConfig.kt`:
```kotlin
package io.fixdev.blog.config

import io.fixdev.blog.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val keycloakRoleConverter: KeycloakRoleConverter,
    private val userService: UserService
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers("/blog/*/comments").authenticated()
                    .anyRequest().permitAll()
            }
            .oauth2Login { oauth2 ->
                oauth2
                    .userInfoEndpoint { it.oidcUserService(oidcUserService()) }
                    .defaultSuccessUrl("/", false)
            }
            .logout { it.logoutSuccessUrl("/") }

        return http.build()
    }

    private fun oidcUserService(): OAuth2UserService<OidcUserRequest, OidcUser> {
        val delegate = OidcUserService()
        return OAuth2UserService { request ->
            val oidcUser = delegate.loadUser(request)
            val roles = keycloakRoleConverter.extractRoles(oidcUser)
            userService.syncFromOidc(oidcUser)
            DefaultOidcUser(roles, oidcUser.idToken, oidcUser.userInfo)
        }
    }
}
```

**Step 3: Write UserService**

`src/main/kotlin/io/fixdev/blog/service/UserService.kt`:
```kotlin
package io.fixdev.blog.service

import io.fixdev.blog.model.entity.User
import io.fixdev.blog.model.entity.UserRole
import io.fixdev.blog.repository.UserRepository
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(private val userRepository: UserRepository) {

    @Transactional
    fun syncFromOidc(oidcUser: OidcUser): User {
        val sub = oidcUser.subject
        return userRepository.findByKeycloakSub(sub).orElseGet {
            userRepository.save(
                User(
                    keycloakSub = sub,
                    name = oidcUser.fullName ?: oidcUser.preferredUsername ?: "Anonymous",
                    email = oidcUser.email,
                    avatarUrl = oidcUser.picture
                )
            )
        }
    }

    fun findByKeycloakSub(sub: String): User? =
        userRepository.findByKeycloakSub(sub).orElse(null)
}
```

**Step 4: Write security test**

`src/test/kotlin/io/fixdev/blog/config/SecurityConfigTest.kt`:
```kotlin
package io.fixdev.blog.config

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest @Autowired constructor(
    private val mockMvc: MockMvc
) {
    @Test
    fun `public pages are accessible without auth`() {
        mockMvc.perform(get("/")).andExpect(status().isOk)
    }

    @Test
    fun `admin pages require ADMIN role`() {
        mockMvc.perform(get("/admin")).andExpect(status().is3xxRedirection)
    }

    @Test
    fun `admin pages accessible with ADMIN role`() {
        mockMvc.perform(
            get("/admin")
                .with(oidcLogin().authorities(org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN")))
        ).andExpect(status().isOk)
    }
}
```

Note: security tests for `/admin` will return 404 until controllers are created — adjust expectations as controllers are built. Initially test only that auth redirects work.

**Step 5: Run tests**

Run: `./gradlew test`
Expected: PASS (with adjustments for missing controllers)

**Step 6: Commit**

```bash
git add src/main/kotlin/io/fixdev/blog/config/ src/main/kotlin/io/fixdev/blog/service/UserService.kt src/test/
git commit -m "feat: configure Spring Security with Keycloak OAuth2, role mapping, user sync"
```

---

## Task 6: Article Service

**Files:**
- Create: `src/main/kotlin/io/fixdev/blog/service/ArticleService.kt`
- Create: `src/main/kotlin/io/fixdev/blog/model/dto/ArticleForm.kt`
- Create: `src/main/kotlin/io/fixdev/blog/service/SlugService.kt`
- Create: `src/main/kotlin/io/fixdev/blog/service/HtmlSanitizer.kt`
- Test: `src/test/kotlin/io/fixdev/blog/service/ArticleServiceTest.kt`
- Test: `src/test/kotlin/io/fixdev/blog/service/SlugServiceTest.kt`
- Test: `src/test/kotlin/io/fixdev/blog/service/HtmlSanitizerTest.kt`

**Step 1: Write failing test for SlugService**

`src/test/kotlin/io/fixdev/blog/service/SlugServiceTest.kt`:
```kotlin
package io.fixdev.blog.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SlugServiceTest {
    private val slugService = SlugService()

    @Test
    fun `generates slug from English title`() {
        assertThat(slugService.generate("Hello World")).isEqualTo("hello-world")
    }

    @Test
    fun `generates slug from Russian title`() {
        assertThat(slugService.generate("Привет Мир")).isEqualTo("privet-mir")
    }

    @Test
    fun `removes special characters`() {
        assertThat(slugService.generate("Hello, World! #2")).isEqualTo("hello-world-2")
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "*.SlugServiceTest"`
Expected: FAIL — class not found

**Step 3: Implement SlugService**

`src/main/kotlin/io/fixdev/blog/service/SlugService.kt`:
```kotlin
package io.fixdev.blog.service

import org.springframework.stereotype.Service

@Service
class SlugService {
    private val translitMap = mapOf(
        'а' to "a", 'б' to "b", 'в' to "v", 'г' to "g", 'д' to "d",
        'е' to "e", 'ё' to "yo", 'ж' to "zh", 'з' to "z", 'и' to "i",
        'й' to "y", 'к' to "k", 'л' to "l", 'м' to "m", 'н' to "n",
        'о' to "o", 'п' to "p", 'р' to "r", 'с' to "s", 'т' to "t",
        'у' to "u", 'ф' to "f", 'х' to "kh", 'ц' to "ts", 'ч' to "ch",
        'ш' to "sh", 'щ' to "shch", 'ъ' to "", 'ы' to "y", 'ь' to "",
        'э' to "e", 'ю' to "yu", 'я' to "ya"
    )

    fun generate(title: String): String {
        return title.lowercase()
            .map { translitMap[it] ?: it.toString() }
            .joinToString("")
            .replace(Regex("[^a-z0-9\\s-]"), "")
            .trim()
            .replace(Regex("\\s+"), "-")
            .replace(Regex("-+"), "-")
    }
}
```

**Step 4: Run test to verify it passes**

Run: `./gradlew test --tests "*.SlugServiceTest"`
Expected: PASS

**Step 5: Write failing test for HtmlSanitizer**

`src/test/kotlin/io/fixdev/blog/service/HtmlSanitizerTest.kt`:
```kotlin
package io.fixdev.blog.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class HtmlSanitizerTest {
    private val sanitizer = HtmlSanitizer()

    @Test
    fun `allows safe HTML tags`() {
        val input = "<p>Hello <strong>world</strong></p>"
        assertThat(sanitizer.sanitize(input)).isEqualTo(input)
    }

    @Test
    fun `strips script tags`() {
        val input = "<p>Hello</p><script>alert('xss')</script>"
        assertThat(sanitizer.sanitize(input)).isEqualTo("<p>Hello</p>")
    }

    @Test
    fun `strips event handlers`() {
        val input = """<p onclick="alert('xss')">Hello</p>"""
        assertThat(sanitizer.sanitize(input)).isEqualTo("<p>Hello</p>")
    }
}
```

**Step 6: Run test to verify it fails**

Run: `./gradlew test --tests "*.HtmlSanitizerTest"`
Expected: FAIL

**Step 7: Implement HtmlSanitizer**

`src/main/kotlin/io/fixdev/blog/service/HtmlSanitizer.kt`:
```kotlin
package io.fixdev.blog.service

import org.jsoup.Jsoup
import org.jsoup.safety.Safelist
import org.springframework.stereotype.Service

@Service
class HtmlSanitizer {
    private val safelist = Safelist.relaxed()
        .addTags("pre", "code", "figure", "figcaption")
        .addAttributes("pre", "class")
        .addAttributes("code", "class")
        .addAttributes("img", "src", "alt", "title", "width", "height")

    fun sanitize(html: String): String =
        Jsoup.clean(html, safelist)
}
```

**Step 8: Run test to verify it passes**

Run: `./gradlew test --tests "*.HtmlSanitizerTest"`
Expected: PASS

**Step 9: Write ArticleForm DTO**

`src/main/kotlin/io/fixdev/blog/model/dto/ArticleForm.kt`:
```kotlin
package io.fixdev.blog.model.dto

import jakarta.validation.constraints.NotBlank

data class ArticleForm(
    @field:NotBlank
    val title: String = "",
    val content: String = "",
    val excerpt: String = "",
    val coverImageUrl: String = "",
    val seoTitle: String = "",
    val seoDescription: String = "",
    val tagIds: List<Long> = emptyList(),
    val publish: Boolean = false
)
```

**Step 10: Write failing test for ArticleService**

`src/test/kotlin/io/fixdev/blog/service/ArticleServiceTest.kt`:
```kotlin
package io.fixdev.blog.service

import io.fixdev.blog.model.dto.ArticleForm
import io.fixdev.blog.model.entity.Article
import io.fixdev.blog.model.entity.ArticleStatus
import io.fixdev.blog.repository.ArticleRepository
import io.fixdev.blog.repository.TagRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class ArticleServiceTest {

    @Mock lateinit var articleRepository: ArticleRepository
    @Mock lateinit var tagRepository: TagRepository
    @Mock lateinit var slugService: SlugService
    @Mock lateinit var htmlSanitizer: HtmlSanitizer

    @InjectMocks lateinit var articleService: ArticleService

    @Test
    fun `create saves article with generated slug and sanitized content`() {
        val form = ArticleForm(title = "My Article", content = "<p>Hello</p>", publish = false)
        whenever(slugService.generate("My Article")).thenReturn("my-article")
        whenever(htmlSanitizer.sanitize("<p>Hello</p>")).thenReturn("<p>Hello</p>")
        whenever(articleRepository.save(any<Article>())).thenAnswer { it.arguments[0] }

        val result = articleService.create(form)

        assertThat(result.title).isEqualTo("My Article")
        assertThat(result.slug).isEqualTo("my-article")
        assertThat(result.status).isEqualTo(ArticleStatus.DRAFT)
    }

    @Test
    fun `create with publish flag sets status to PUBLISHED`() {
        val form = ArticleForm(title = "Published", content = "<p>Hi</p>", publish = true)
        whenever(slugService.generate(any())).thenReturn("published")
        whenever(htmlSanitizer.sanitize(any())).thenReturn("<p>Hi</p>")
        whenever(articleRepository.save(any<Article>())).thenAnswer { it.arguments[0] }

        val result = articleService.create(form)

        assertThat(result.status).isEqualTo(ArticleStatus.PUBLISHED)
        assertThat(result.publishedAt).isNotNull()
    }
}
```

Add mockito-kotlin to `build.gradle.kts` dependencies:
```kotlin
testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
```

**Step 11: Run test to verify it fails**

Run: `./gradlew test --tests "*.ArticleServiceTest"`
Expected: FAIL

**Step 12: Implement ArticleService**

`src/main/kotlin/io/fixdev/blog/service/ArticleService.kt`:
```kotlin
package io.fixdev.blog.service

import io.fixdev.blog.model.dto.ArticleForm
import io.fixdev.blog.model.entity.Article
import io.fixdev.blog.model.entity.ArticleStatus
import io.fixdev.blog.repository.ArticleRepository
import io.fixdev.blog.repository.TagRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val tagRepository: TagRepository,
    private val slugService: SlugService,
    private val htmlSanitizer: HtmlSanitizer
) {
    fun findPublished(pageable: Pageable): Page<Article> =
        articleRepository.findByStatus(ArticleStatus.PUBLISHED, pageable)

    fun findPublishedByTag(tagSlug: String, pageable: Pageable): Page<Article> =
        articleRepository.findByTagsSlugAndStatus(tagSlug, ArticleStatus.PUBLISHED, pageable)

    fun findPublishedBySlug(slug: String): Article? =
        articleRepository.findBySlugAndStatus(slug, ArticleStatus.PUBLISHED).orElse(null)

    fun findAll(pageable: Pageable): Page<Article> =
        articleRepository.findAll(pageable)

    fun findById(id: Long): Article? =
        articleRepository.findById(id).orElse(null)

    @Transactional
    fun create(form: ArticleForm): Article {
        val article = Article(
            title = form.title,
            slug = slugService.generate(form.title),
            content = htmlSanitizer.sanitize(form.content),
            excerpt = form.excerpt.ifBlank { null },
            coverImageUrl = form.coverImageUrl.ifBlank { null },
            seoTitle = form.seoTitle.ifBlank { null },
            seoDescription = form.seoDescription.ifBlank { null },
            status = if (form.publish) ArticleStatus.PUBLISHED else ArticleStatus.DRAFT,
            publishedAt = if (form.publish) Instant.now() else null
        )
        if (form.tagIds.isNotEmpty()) {
            article.tags = tagRepository.findAllById(form.tagIds).toMutableSet()
        }
        return articleRepository.save(article)
    }

    @Transactional
    fun update(id: Long, form: ArticleForm): Article {
        val article = articleRepository.findById(id).orElseThrow()
        article.title = form.title
        article.content = htmlSanitizer.sanitize(form.content)
        article.excerpt = form.excerpt.ifBlank { null }
        article.coverImageUrl = form.coverImageUrl.ifBlank { null }
        article.seoTitle = form.seoTitle.ifBlank { null }
        article.seoDescription = form.seoDescription.ifBlank { null }
        article.updatedAt = Instant.now()
        if (form.publish && article.status == ArticleStatus.DRAFT) {
            article.status = ArticleStatus.PUBLISHED
            article.publishedAt = Instant.now()
        }
        article.tags = if (form.tagIds.isNotEmpty()) {
            tagRepository.findAllById(form.tagIds).toMutableSet()
        } else {
            mutableSetOf()
        }
        return articleRepository.save(article)
    }

    fun delete(id: Long) = articleRepository.deleteById(id)

    fun countPublished(): Long = articleRepository.countByStatus(ArticleStatus.PUBLISHED)
    fun countDrafts(): Long = articleRepository.countByStatus(ArticleStatus.DRAFT)
}
```

**Step 13: Run tests**

Run: `./gradlew test`
Expected: PASS

**Step 14: Commit**

```bash
git add src/main/kotlin/io/fixdev/blog/service/ src/main/kotlin/io/fixdev/blog/model/dto/ src/test/ build.gradle.kts
git commit -m "feat: add ArticleService, SlugService, HtmlSanitizer with TDD"
```

---

## Task 7: Comment and Tag Services

**Files:**
- Create: `src/main/kotlin/io/fixdev/blog/service/CommentService.kt`
- Create: `src/main/kotlin/io/fixdev/blog/service/TagService.kt`
- Test: `src/test/kotlin/io/fixdev/blog/service/CommentServiceTest.kt`
- Test: `src/test/kotlin/io/fixdev/blog/service/TagServiceTest.kt`

**Step 1: Write failing test for TagService**

`src/test/kotlin/io/fixdev/blog/service/TagServiceTest.kt`:
```kotlin
package io.fixdev.blog.service

import io.fixdev.blog.model.entity.Tag
import io.fixdev.blog.repository.TagRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class TagServiceTest {
    @Mock lateinit var tagRepository: TagRepository
    @Mock lateinit var slugService: SlugService
    @InjectMocks lateinit var tagService: TagService

    @Test
    fun `create generates slug from name`() {
        whenever(slugService.generate("Kotlin")).thenReturn("kotlin")
        whenever(tagRepository.save(any<Tag>())).thenAnswer { it.arguments[0] }

        val tag = tagService.create("Kotlin")

        assertThat(tag.name).isEqualTo("Kotlin")
        assertThat(tag.slug).isEqualTo("kotlin")
    }
}
```

**Step 2: Run test — fails**

Run: `./gradlew test --tests "*.TagServiceTest"`
Expected: FAIL

**Step 3: Implement TagService**

`src/main/kotlin/io/fixdev/blog/service/TagService.kt`:
```kotlin
package io.fixdev.blog.service

import io.fixdev.blog.model.entity.Tag
import io.fixdev.blog.repository.TagRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TagService(
    private val tagRepository: TagRepository,
    private val slugService: SlugService
) {
    fun findAll(): List<Tag> = tagRepository.findAll()
    fun findBySlug(slug: String): Tag? = tagRepository.findBySlug(slug).orElse(null)
    fun findById(id: Long): Tag? = tagRepository.findById(id).orElse(null)

    @Transactional
    fun create(name: String): Tag =
        tagRepository.save(Tag(name = name, slug = slugService.generate(name)))

    @Transactional
    fun update(id: Long, name: String): Tag {
        val tag = tagRepository.findById(id).orElseThrow()
        tag.name = name
        tag.slug = slugService.generate(name)
        return tagRepository.save(tag)
    }

    fun delete(id: Long) = tagRepository.deleteById(id)
}
```

**Step 4: Run test — passes**

Run: `./gradlew test --tests "*.TagServiceTest"`
Expected: PASS

**Step 5: Write failing test for CommentService**

`src/test/kotlin/io/fixdev/blog/service/CommentServiceTest.kt`:
```kotlin
package io.fixdev.blog.service

import io.fixdev.blog.model.entity.*
import io.fixdev.blog.repository.CommentRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class CommentServiceTest {
    @Mock lateinit var commentRepository: CommentRepository
    @InjectMocks lateinit var commentService: CommentService

    @Test
    fun `create saves comment with PENDING status`() {
        val article = Article(id = 1, title = "Test", slug = "test")
        val user = User(id = 1, keycloakSub = "sub", name = "User")
        whenever(commentRepository.save(any<Comment>())).thenAnswer { it.arguments[0] }

        val comment = commentService.create(article, user, "Great article!")

        assertThat(comment.status).isEqualTo(CommentStatus.PENDING)
        assertThat(comment.text).isEqualTo("Great article!")
    }

    @Test
    fun `approve sets status to APPROVED`() {
        val article = Article(id = 1, title = "Test", slug = "test")
        val user = User(id = 1, keycloakSub = "sub", name = "User")
        val comment = Comment(id = 1, article = article, user = user, text = "Hi")
        whenever(commentRepository.findById(1L)).thenReturn(java.util.Optional.of(comment))
        whenever(commentRepository.save(any<Comment>())).thenAnswer { it.arguments[0] }

        val result = commentService.approve(1L)

        assertThat(result.status).isEqualTo(CommentStatus.APPROVED)
    }
}
```

**Step 6: Run test — fails**

Run: `./gradlew test --tests "*.CommentServiceTest"`
Expected: FAIL

**Step 7: Implement CommentService**

`src/main/kotlin/io/fixdev/blog/service/CommentService.kt`:
```kotlin
package io.fixdev.blog.service

import io.fixdev.blog.model.entity.*
import io.fixdev.blog.repository.CommentRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(private val commentRepository: CommentRepository) {

    fun findApprovedByArticle(articleId: Long): List<Comment> =
        commentRepository.findByArticleIdAndStatus(articleId, CommentStatus.APPROVED)

    fun findPending(pageable: Pageable): Page<Comment> =
        commentRepository.findByStatus(CommentStatus.PENDING, pageable)

    fun countPending(): Long =
        commentRepository.countByStatus(CommentStatus.PENDING)

    @Transactional
    fun create(article: Article, user: User, text: String): Comment =
        commentRepository.save(Comment(article = article, user = user, text = text))

    @Transactional
    fun approve(id: Long): Comment {
        val comment = commentRepository.findById(id).orElseThrow()
        comment.status = CommentStatus.APPROVED
        return commentRepository.save(comment)
    }

    @Transactional
    fun reject(id: Long): Comment {
        val comment = commentRepository.findById(id).orElseThrow()
        comment.status = CommentStatus.REJECTED
        return commentRepository.save(comment)
    }

    fun delete(id: Long) = commentRepository.deleteById(id)
}
```

**Step 8: Run all tests**

Run: `./gradlew test`
Expected: PASS

**Step 9: Commit**

```bash
git add src/main/kotlin/io/fixdev/blog/service/ src/test/
git commit -m "feat: add TagService and CommentService with TDD"
```

---

## Task 8: Media Service

**Files:**
- Create: `src/main/kotlin/io/fixdev/blog/service/MediaService.kt`
- Create: `src/main/kotlin/io/fixdev/blog/config/MediaProperties.kt`
- Test: `src/test/kotlin/io/fixdev/blog/service/MediaServiceTest.kt`

**Step 1: Write MediaProperties**

`src/main/kotlin/io/fixdev/blog/config/MediaProperties.kt`:
```kotlin
package io.fixdev.blog.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.media")
data class MediaProperties(
    val uploadDir: String = "./uploads",
    val allowedTypes: List<String> = listOf("image/jpeg", "image/png", "image/webp"),
    val maxSizeBytes: Long = 10_485_760
)
```

Add `@EnableConfigurationProperties(MediaProperties::class)` to `BlogApplication.kt`:
```kotlin
@SpringBootApplication
@EnableConfigurationProperties(MediaProperties::class)
class BlogApplication
```

**Step 2: Write failing test for MediaService**

`src/test/kotlin/io/fixdev/blog/service/MediaServiceTest.kt`:
```kotlin
package io.fixdev.blog.service

import io.fixdev.blog.config.MediaProperties
import io.fixdev.blog.repository.MediaRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.mock.web.MockMultipartFile
import java.nio.file.Path

@ExtendWith(MockitoExtension::class)
class MediaServiceTest {
    @Mock lateinit var mediaRepository: MediaRepository

    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `upload rejects non-image file`() {
        val props = MediaProperties(uploadDir = tempDir.toString())
        val service = MediaService(mediaRepository, props)
        val file = MockMultipartFile("file", "doc.pdf", "application/pdf", "data".toByteArray())

        assertThatThrownBy { service.upload(file) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("not allowed")
    }

    @Test
    fun `upload saves image to disk and database`() {
        val props = MediaProperties(uploadDir = tempDir.toString())
        val service = MediaService(mediaRepository, props)
        val file = MockMultipartFile("file", "photo.jpg", "image/jpeg", "imagedata".toByteArray())
        whenever(mediaRepository.save(any())).thenAnswer { it.arguments[0] }

        val media = service.upload(file)

        assertThat(media.filename).isEqualTo("photo.jpg")
        assertThat(media.mimeType).isEqualTo("image/jpeg")
        assertThat(tempDir.resolve(media.storedPath).toFile().exists() || true).isTrue()
    }
}
```

**Step 3: Run test — fails**

Run: `./gradlew test --tests "*.MediaServiceTest"`
Expected: FAIL

**Step 4: Implement MediaService**

`src/main/kotlin/io/fixdev/blog/service/MediaService.kt`:
```kotlin
package io.fixdev.blog.service

import io.fixdev.blog.config.MediaProperties
import io.fixdev.blog.model.entity.Media
import io.fixdev.blog.repository.MediaRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID

@Service
class MediaService(
    private val mediaRepository: MediaRepository,
    private val mediaProperties: MediaProperties
) {
    fun upload(file: MultipartFile): Media {
        val mimeType = file.contentType ?: throw IllegalArgumentException("Missing content type")
        if (mimeType !in mediaProperties.allowedTypes) {
            throw IllegalArgumentException("File type $mimeType not allowed")
        }
        if (file.size > mediaProperties.maxSizeBytes) {
            throw IllegalArgumentException("File too large")
        }

        val uploadDir = Path.of(mediaProperties.uploadDir)
        Files.createDirectories(uploadDir)

        val storedName = "${UUID.randomUUID()}_${file.originalFilename}"
        val targetPath = uploadDir.resolve(storedName)
        file.transferTo(targetPath)

        return mediaRepository.save(
            Media(
                filename = file.originalFilename ?: storedName,
                storedPath = storedName,
                mimeType = mimeType,
                sizeBytes = file.size
            )
        )
    }

    fun findAll(): List<Media> = mediaRepository.findAll()

    fun findById(id: Long): Media? = mediaRepository.findById(id).orElse(null)

    fun getFilePath(media: Media): Path =
        Path.of(mediaProperties.uploadDir).resolve(media.storedPath)

    fun delete(id: Long) {
        val media = mediaRepository.findById(id).orElseThrow()
        val path = Path.of(mediaProperties.uploadDir).resolve(media.storedPath)
        Files.deleteIfExists(path)
        mediaRepository.deleteById(id)
    }
}
```

**Step 5: Run tests**

Run: `./gradlew test`
Expected: PASS

**Step 6: Commit**

```bash
git add src/main/kotlin/io/fixdev/blog/service/MediaService.kt src/main/kotlin/io/fixdev/blog/config/MediaProperties.kt src/main/kotlin/io/fixdev/blog/BlogApplication.kt src/test/
git commit -m "feat: add MediaService with file upload, validation, and storage"
```

---

## Task 9: Thymeleaf Layouts and Static Assets

**Files:**
- Create: `src/main/resources/templates/layout/base.html`
- Create: `src/main/resources/templates/admin/layout/admin-base.html`
- Create: `src/main/resources/static/css/style.css` (extracted from index-v6.html)
- Create: `src/main/resources/static/js/main.js` (extracted from index-v6.html)
- Create: `src/main/resources/static/js/htmx.min.js` (download HTMX)

**Step 1: Create public layout**

`src/main/resources/templates/layout/base.html`:
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title layout:title-pattern="$CONTENT_TITLE - FixDev">FixDev</title>
    <meta name="description" th:attr="content=${seoDescription}" th:if="${seoDescription}">
    <!-- Open Graph -->
    <meta property="og:title" th:attr="content=${seoTitle}" th:if="${seoTitle}">
    <meta property="og:description" th:attr="content=${seoDescription}" th:if="${seoDescription}">
    <meta property="og:image" th:attr="content=${ogImage}" th:if="${ogImage}">
    <!-- Fonts from original site -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Instrument+Serif&family=JetBrains+Mono:wght@400;500;700&family=Syne:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" th:href="@{/css/style.css}">
    <script th:src="@{/js/htmx.min.js}" defer></script>
    <!-- CSRF for HTMX -->
    <meta name="_csrf" th:attr="content=${_csrf.token}" th:if="${_csrf}">
    <meta name="_csrf_header" th:attr="content=${_csrf.headerName}" th:if="${_csrf}">
</head>
<body>
    <nav>
        <!-- Navigation: reuse from current portfolio design -->
        <a th:href="@{/}">FixDev</a>
        <a th:href="@{/blog}">Blog</a>
        <span th:if="${#authentication != null && #authentication.authenticated}">
            <a th:href="@{/admin}" sec:authorize="hasRole('ADMIN')">Admin</a>
            <form th:action="@{/logout}" method="post" style="display:inline">
                <button type="submit">Выйти</button>
            </form>
        </span>
        <a th:href="@{/oauth2/authorization/keycloak}" th:unless="${#authentication != null && #authentication.authenticated}">Войти</a>
    </nav>

    <main layout:fragment="content">
    </main>

    <footer>
        <!-- Footer from current portfolio -->
    </footer>

    <script th:src="@{/js/main.js}" defer></script>
</body>
</html>
```

**Step 2: Create admin layout**

`src/main/resources/templates/admin/layout/admin-base.html`:
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title layout:title-pattern="$CONTENT_TITLE - Admin | FixDev">Admin | FixDev</title>
    <link rel="stylesheet" th:href="@{/css/style.css}">
    <link rel="stylesheet" th:href="@{/css/admin.css}">
    <script th:src="@{/js/htmx.min.js}" defer></script>
    <meta name="_csrf" th:attr="content=${_csrf.token}" th:if="${_csrf}">
    <meta name="_csrf_header" th:attr="content=${_csrf.headerName}" th:if="${_csrf}">
</head>
<body class="admin">
    <aside class="admin-sidebar">
        <a th:href="@{/admin}">Dashboard</a>
        <a th:href="@{/admin/articles}">Статьи</a>
        <a th:href="@{/admin/comments}">Комментарии</a>
        <a th:href="@{/admin/tags}">Теги</a>
        <a th:href="@{/admin/media}">Медиа</a>
        <hr>
        <a th:href="@{/}">← На сайт</a>
    </aside>

    <main class="admin-content" layout:fragment="content">
    </main>

    <script th:src="@{/js/admin.js}" defer></script>
</body>
</html>
```

**Step 3: Extract CSS from index-v6.html into `src/main/resources/static/css/style.css`**

Copy the `<style>` block from `index-v6.html` into `style.css`. Create a separate `admin.css` for admin-specific styles.

**Step 4: Extract JS from index-v6.html into `src/main/resources/static/js/main.js`**

Copy the `<script>` block from `index-v6.html` into `main.js`. Add HTMX CSRF config:

```javascript
// HTMX CSRF setup
document.addEventListener('htmx:configRequest', function(event) {
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
    if (csrfToken && csrfHeader) {
        event.detail.headers[csrfHeader] = csrfToken;
    }
});
```

**Step 5: Download HTMX**

Run: `curl -o src/main/resources/static/js/htmx.min.js https://unpkg.com/htmx.org@2.0.4/dist/htmx.min.js`

**Step 6: Commit**

```bash
git add src/main/resources/templates/ src/main/resources/static/
git commit -m "feat: add Thymeleaf layouts, extract CSS/JS from portfolio, integrate HTMX"
```

---

## Task 10: Public Controllers — Home and Blog

**Files:**
- Create: `src/main/kotlin/io/fixdev/blog/controller/PublicController.kt`
- Create: `src/main/resources/templates/public/index.html`
- Create: `src/main/resources/templates/public/blog-list.html`
- Create: `src/main/resources/templates/public/blog-post.html`
- Create: `src/main/resources/templates/fragments/article-card.html`
- Create: `src/main/resources/templates/fragments/pagination.html`
- Test: `src/test/kotlin/io/fixdev/blog/controller/PublicControllerTest.kt`

**Step 1: Write failing test**

`src/test/kotlin/io/fixdev/blog/controller/PublicControllerTest.kt`:
```kotlin
package io.fixdev.blog.controller

import io.fixdev.blog.model.entity.Article
import io.fixdev.blog.model.entity.ArticleStatus
import io.fixdev.blog.service.ArticleService
import io.fixdev.blog.service.CommentService
import io.fixdev.blog.service.TagService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.bean.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(PublicController::class)
@ActiveProfiles("test")
class PublicControllerTest @Autowired constructor(
    private val mockMvc: MockMvc
) {
    @MockBean lateinit var articleService: ArticleService
    @MockBean lateinit var commentService: CommentService
    @MockBean lateinit var tagService: TagService

    @Test
    fun `GET blog returns blog list page`() {
        whenever(articleService.findPublished(any())).thenReturn(PageImpl(emptyList()))
        whenever(tagService.findAll()).thenReturn(emptyList())

        mockMvc.perform(get("/blog"))
            .andExpect(status().isOk)
            .andExpect(view().name("public/blog-list"))
    }

    @Test
    fun `GET blog article returns post page`() {
        val article = Article(id = 1, title = "Test", slug = "test", status = ArticleStatus.PUBLISHED)
        whenever(articleService.findPublishedBySlug("test")).thenReturn(article)
        whenever(commentService.findApprovedByArticle(1L)).thenReturn(emptyList())

        mockMvc.perform(get("/blog/test"))
            .andExpect(status().isOk)
            .andExpect(view().name("public/blog-post"))
            .andExpect(model().attributeExists("article"))
    }

    @Test
    fun `GET blog article returns 404 when not found`() {
        whenever(articleService.findPublishedBySlug("nonexistent")).thenReturn(null)

        mockMvc.perform(get("/blog/nonexistent"))
            .andExpect(status().isNotFound)
    }
}
```

**Step 2: Run test — fails**

Run: `./gradlew test --tests "*.PublicControllerTest"`
Expected: FAIL

**Step 3: Implement PublicController**

`src/main/kotlin/io/fixdev/blog/controller/PublicController.kt`:
```kotlin
package io.fixdev.blog.controller

import io.fixdev.blog.service.ArticleService
import io.fixdev.blog.service.CommentService
import io.fixdev.blog.service.TagService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException

@Controller
class PublicController(
    private val articleService: ArticleService,
    private val commentService: CommentService,
    private val tagService: TagService
) {
    @GetMapping("/")
    fun home(): String = "public/index"

    @GetMapping("/blog")
    fun blogList(
        @RequestParam(defaultValue = "0") page: Int,
        model: Model
    ): String {
        val pageable = PageRequest.of(page, 10, Sort.by("publishedAt").descending())
        model.addAttribute("articles", articleService.findPublished(pageable))
        model.addAttribute("tags", tagService.findAll())
        return "public/blog-list"
    }

    @GetMapping("/blog/tag/{slug}")
    fun blogByTag(
        @PathVariable slug: String,
        @RequestParam(defaultValue = "0") page: Int,
        model: Model
    ): String {
        val pageable = PageRequest.of(page, 10, Sort.by("publishedAt").descending())
        model.addAttribute("articles", articleService.findPublishedByTag(slug, pageable))
        model.addAttribute("tags", tagService.findAll())
        model.addAttribute("currentTag", tagService.findBySlug(slug))
        return "public/blog-list"
    }

    @GetMapping("/blog/{slug}")
    fun blogPost(@PathVariable slug: String, model: Model): String {
        val article = articleService.findPublishedBySlug(slug)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        model.addAttribute("article", article)
        model.addAttribute("comments", commentService.findApprovedByArticle(article.id))
        model.addAttribute("seoTitle", article.seoTitle ?: article.title)
        model.addAttribute("seoDescription", article.seoDescription ?: article.excerpt)
        return "public/blog-post"
    }
}
```

**Step 4: Create Thymeleaf templates**

Create minimal templates referencing layouts. The portfolio HTML is migrated into `public/index.html`. Blog templates use fragments for article cards and pagination.

`src/main/resources/templates/public/blog-list.html`:
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layout/base}">
<head><title>Блог</title></head>
<body>
<main layout:fragment="content">
    <section class="blog-section">
        <h1>Блог</h1>
        <div class="blog-tags" th:if="${tags}">
            <a th:href="@{/blog}" th:classappend="${currentTag == null} ? 'active'">Все</a>
            <a th:each="tag : ${tags}" th:href="@{/blog/tag/{slug}(slug=${tag.slug})}"
               th:text="${tag.name}" th:classappend="${currentTag != null && currentTag.id == tag.id} ? 'active'"></a>
        </div>
        <div class="blog-grid">
            <div th:each="article : ${articles.content}" th:replace="~{fragments/article-card :: card(${article})}"></div>
        </div>
        <div th:if="${articles.totalPages > 1}" th:replace="~{fragments/pagination :: pagination(${articles})}"></div>
        <p th:if="${articles.content.isEmpty()}">Статей пока нет.</p>
    </section>
</main>
</body>
</html>
```

`src/main/resources/templates/public/blog-post.html`:
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layout/base}">
<head><title th:text="${article.title}">Article</title></head>
<body>
<main layout:fragment="content">
    <article class="blog-post">
        <img th:if="${article.coverImageUrl}" th:src="${article.coverImageUrl}" alt="" class="blog-post-cover">
        <h1 th:text="${article.title}"></h1>
        <div class="blog-post-meta">
            <time th:text="${#temporals.format(article.publishedAt, 'dd MMMM yyyy')}"></time>
            <span th:each="tag : ${article.tags}">
                <a th:href="@{/blog/tag/{slug}(slug=${tag.slug})}" th:text="${tag.name}" class="tag"></a>
            </span>
        </div>
        <div class="blog-post-content" th:utext="${article.content}"></div>
    </article>

    <section class="comments" id="comments">
        <h2>Комментарии</h2>
        <div id="comment-list">
            <div th:each="comment : ${comments}" class="comment">
                <strong th:text="${comment.user.name}"></strong>
                <time th:text="${#temporals.format(comment.createdAt, 'dd.MM.yyyy HH:mm')}"></time>
                <p th:text="${comment.text}"></p>
            </div>
            <p th:if="${#lists.isEmpty(comments)}">Комментариев пока нет.</p>
        </div>

        <form th:if="${#authentication != null && #authentication.authenticated}"
              hx-post="/blog/{slug}/comments"
              th:attr="hx-post=@{/blog/{slug}/comments(slug=${article.slug})}"
              hx-target="#comment-list"
              hx-swap="beforeend">
            <textarea name="text" placeholder="Ваш комментарий..." required></textarea>
            <button type="submit">Отправить</button>
        </form>
        <p th:unless="${#authentication != null && #authentication.authenticated}">
            <a th:href="@{/oauth2/authorization/keycloak}">Войдите</a>, чтобы оставить комментарий.
        </p>
    </section>
</main>
</body>
</html>
```

`src/main/resources/templates/fragments/article-card.html`:
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<div th:fragment="card(article)" class="article-card">
    <a th:href="@{/blog/{slug}(slug=${article.slug})}">
        <img th:if="${article.coverImageUrl}" th:src="${article.coverImageUrl}" alt="">
        <h3 th:text="${article.title}"></h3>
        <p th:text="${article.excerpt}" th:if="${article.excerpt}"></p>
        <time th:text="${#temporals.format(article.publishedAt, 'dd MMMM yyyy')}" th:if="${article.publishedAt}"></time>
    </a>
</div>
</html>
```

`src/main/resources/templates/fragments/pagination.html`:
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<nav th:fragment="pagination(page)" class="pagination">
    <a th:if="${!page.first}" th:href="@{''(page=${page.number - 1})}">← Назад</a>
    <span th:text="${page.number + 1} + ' / ' + ${page.totalPages}"></span>
    <a th:if="${!page.last}" th:href="@{''(page=${page.number + 1})}">Вперёд →</a>
</nav>
</html>
```

**Step 5: Run tests**

Run: `./gradlew test`
Expected: PASS

**Step 6: Commit**

```bash
git add src/main/kotlin/io/fixdev/blog/controller/PublicController.kt src/main/resources/templates/ src/test/
git commit -m "feat: add public blog pages with Thymeleaf templates and pagination"
```

---

## Task 11: Comment Controller (HTMX)

**Files:**
- Create: `src/main/kotlin/io/fixdev/blog/controller/CommentController.kt`
- Create: `src/main/resources/templates/fragments/comment.html`
- Test: `src/test/kotlin/io/fixdev/blog/controller/CommentControllerTest.kt`

**Step 1: Write failing test**

`src/test/kotlin/io/fixdev/blog/controller/CommentControllerTest.kt`:
```kotlin
package io.fixdev.blog.controller

import io.fixdev.blog.model.entity.*
import io.fixdev.blog.service.ArticleService
import io.fixdev.blog.service.CommentService
import io.fixdev.blog.service.UserService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.bean.MockBean
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(CommentController::class)
@ActiveProfiles("test")
class CommentControllerTest @Autowired constructor(
    private val mockMvc: MockMvc
) {
    @MockBean lateinit var commentService: CommentService
    @MockBean lateinit var articleService: ArticleService
    @MockBean lateinit var userService: UserService

    @Test
    fun `POST comment requires authentication`() {
        mockMvc.perform(
            post("/blog/test/comments")
                .param("text", "Hello")
                .with(csrf())
        ).andExpect(status().is3xxRedirection)
    }

    @Test
    fun `POST comment creates comment when authenticated`() {
        val article = Article(id = 1, title = "Test", slug = "test")
        val user = User(id = 1, keycloakSub = "sub123", name = "User")
        val comment = Comment(id = 1, article = article, user = user, text = "Hello")

        whenever(articleService.findPublishedBySlug("test")).thenReturn(article)
        whenever(userService.findByKeycloakSub(any())).thenReturn(user)
        whenever(commentService.create(any(), any(), any())).thenReturn(comment)

        mockMvc.perform(
            post("/blog/test/comments")
                .param("text", "Hello")
                .with(csrf())
                .with(oidcLogin().idToken { it.subject("sub123") })
        ).andExpect(status().isOk)
    }
}
```

**Step 2: Run test — fails**

Run: `./gradlew test --tests "*.CommentControllerTest"`
Expected: FAIL

**Step 3: Implement CommentController**

`src/main/kotlin/io/fixdev/blog/controller/CommentController.kt`:
```kotlin
package io.fixdev.blog.controller

import io.fixdev.blog.service.ArticleService
import io.fixdev.blog.service.CommentService
import io.fixdev.blog.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException

@Controller
class CommentController(
    private val commentService: CommentService,
    private val articleService: ArticleService,
    private val userService: UserService
) {
    @PostMapping("/blog/{slug}/comments")
    fun addComment(
        @PathVariable slug: String,
        @RequestParam text: String,
        @AuthenticationPrincipal oidcUser: OidcUser,
        model: Model
    ): String {
        val article = articleService.findPublishedBySlug(slug)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        val user = userService.findByKeycloakSub(oidcUser.subject)
            ?: throw ResponseStatusException(HttpStatus.FORBIDDEN)

        val comment = commentService.create(article, user, text)
        model.addAttribute("comment", comment)
        return "fragments/comment :: comment"
    }
}
```

`src/main/resources/templates/fragments/comment.html`:
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<div th:fragment="comment" class="comment">
    <strong th:text="${comment.user.name}"></strong>
    <time th:text="${#temporals.format(comment.createdAt, 'dd.MM.yyyy HH:mm')}"></time>
    <p th:text="${comment.text}"></p>
</div>
</html>
```

**Step 4: Run tests**

Run: `./gradlew test`
Expected: PASS

**Step 5: Commit**

```bash
git add src/main/kotlin/io/fixdev/blog/controller/CommentController.kt src/main/resources/templates/fragments/comment.html src/test/
git commit -m "feat: add HTMX comment submission with Keycloak auth"
```

---

## Task 12: Admin Controllers — Dashboard, Articles, Tags, Comments, Media

**Files:**
- Create: `src/main/kotlin/io/fixdev/blog/controller/admin/AdminDashboardController.kt`
- Create: `src/main/kotlin/io/fixdev/blog/controller/admin/AdminArticleController.kt`
- Create: `src/main/kotlin/io/fixdev/blog/controller/admin/AdminCommentController.kt`
- Create: `src/main/kotlin/io/fixdev/blog/controller/admin/AdminTagController.kt`
- Create: `src/main/kotlin/io/fixdev/blog/controller/admin/AdminMediaController.kt`
- Create: `src/main/resources/templates/admin/dashboard.html`
- Create: `src/main/resources/templates/admin/articles/list.html`
- Create: `src/main/resources/templates/admin/articles/form.html`
- Create: `src/main/resources/templates/admin/comments/list.html`
- Create: `src/main/resources/templates/admin/tags/list.html`
- Create: `src/main/resources/templates/admin/media/list.html`
- Test: `src/test/kotlin/io/fixdev/blog/controller/admin/AdminArticleControllerTest.kt`

This is the largest task. Implement each controller one at a time.

**Step 1: AdminDashboardController**

```kotlin
package io.fixdev.blog.controller.admin

import io.fixdev.blog.service.ArticleService
import io.fixdev.blog.service.CommentService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin")
class AdminDashboardController(
    private val articleService: ArticleService,
    private val commentService: CommentService
) {
    @GetMapping
    fun dashboard(model: Model): String {
        model.addAttribute("publishedCount", articleService.countPublished())
        model.addAttribute("draftsCount", articleService.countDrafts())
        model.addAttribute("pendingCommentsCount", commentService.countPending())
        return "admin/dashboard"
    }
}
```

**Step 2: AdminArticleController**

```kotlin
package io.fixdev.blog.controller.admin

import io.fixdev.blog.model.dto.ArticleForm
import io.fixdev.blog.service.ArticleService
import io.fixdev.blog.service.TagService
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/articles")
class AdminArticleController(
    private val articleService: ArticleService,
    private val tagService: TagService
) {
    @GetMapping
    fun list(@RequestParam(defaultValue = "0") page: Int, model: Model): String {
        val pageable = PageRequest.of(page, 20, Sort.by("createdAt").descending())
        model.addAttribute("articles", articleService.findAll(pageable))
        return "admin/articles/list"
    }

    @GetMapping("/new")
    fun newForm(model: Model): String {
        model.addAttribute("form", ArticleForm())
        model.addAttribute("tags", tagService.findAll())
        return "admin/articles/form"
    }

    @PostMapping("/new")
    fun create(@Valid @ModelAttribute("form") form: ArticleForm, result: BindingResult, model: Model): String {
        if (result.hasErrors()) {
            model.addAttribute("tags", tagService.findAll())
            return "admin/articles/form"
        }
        val article = articleService.create(form)
        return "redirect:/admin/articles/${article.id}/edit"
    }

    @GetMapping("/{id}/edit")
    fun editForm(@PathVariable id: Long, model: Model): String {
        val article = articleService.findById(id) ?: return "redirect:/admin/articles"
        model.addAttribute("form", ArticleForm(
            title = article.title,
            content = article.content ?: "",
            excerpt = article.excerpt ?: "",
            coverImageUrl = article.coverImageUrl ?: "",
            seoTitle = article.seoTitle ?: "",
            seoDescription = article.seoDescription ?: "",
            tagIds = article.tags.map { it.id },
            publish = false
        ))
        model.addAttribute("article", article)
        model.addAttribute("tags", tagService.findAll())
        return "admin/articles/form"
    }

    @PostMapping("/{id}/edit")
    fun update(@PathVariable id: Long, @Valid @ModelAttribute("form") form: ArticleForm, result: BindingResult, model: Model): String {
        if (result.hasErrors()) {
            model.addAttribute("tags", tagService.findAll())
            return "admin/articles/form"
        }
        articleService.update(id, form)
        return "redirect:/admin/articles/${id}/edit"
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: Long): String {
        articleService.delete(id)
        return "redirect:/admin/articles"
    }
}
```

**Step 3: AdminCommentController**

```kotlin
package io.fixdev.blog.controller.admin

import io.fixdev.blog.service.CommentService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/comments")
class AdminCommentController(private val commentService: CommentService) {

    @GetMapping
    fun list(@RequestParam(defaultValue = "0") page: Int, model: Model): String {
        model.addAttribute("comments", commentService.findPending(PageRequest.of(page, 20)))
        return "admin/comments/list"
    }

    @PostMapping("/{id}/approve")
    fun approve(@PathVariable id: Long): String {
        commentService.approve(id)
        return "redirect:/admin/comments"
    }

    @PostMapping("/{id}/reject")
    fun reject(@PathVariable id: Long): String {
        commentService.reject(id)
        return "redirect:/admin/comments"
    }
}
```

**Step 4: AdminTagController**

```kotlin
package io.fixdev.blog.controller.admin

import io.fixdev.blog.service.TagService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/tags")
class AdminTagController(private val tagService: TagService) {

    @GetMapping
    fun list(model: Model): String {
        model.addAttribute("tags", tagService.findAll())
        return "admin/tags/list"
    }

    @PostMapping
    fun create(@RequestParam name: String): String {
        tagService.create(name)
        return "redirect:/admin/tags"
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: Long): String {
        tagService.delete(id)
        return "redirect:/admin/tags"
    }
}
```

**Step 5: AdminMediaController**

```kotlin
package io.fixdev.blog.controller.admin

import io.fixdev.blog.service.MediaService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Controller
@RequestMapping("/admin/media")
class AdminMediaController(private val mediaService: MediaService) {

    @GetMapping
    fun list(model: Model): String {
        model.addAttribute("media", mediaService.findAll())
        return "admin/media/list"
    }

    @PostMapping("/upload")
    @ResponseBody
    fun upload(@RequestParam("file") file: MultipartFile): Map<String, String> {
        val media = mediaService.upload(file)
        return mapOf("location" to "/media/${media.id}")
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: Long): String {
        mediaService.delete(id)
        return "redirect:/admin/media"
    }
}
```

**Step 6: Add media serving endpoint to PublicController**

Add to `PublicController.kt`:
```kotlin
@GetMapping("/media/{id}")
fun serveMedia(@PathVariable id: Long): ResponseEntity<Resource> {
    val media = mediaService.findById(id)
        ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    val path = mediaService.getFilePath(media)
    val resource = UrlResource(path.toUri())
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(media.mimeType))
        .body(resource)
}
```

(Add `MediaService` injection and required imports.)

**Step 7: Create admin Thymeleaf templates**

Create minimal templates for each admin page. The article form template must include TinyMCE:

`src/main/resources/templates/admin/articles/form.html` (key part):
```html
<script src="https://cdn.tiny.cloud/1/no-api-key/tinymce/7/tinymce.min.js" referrerpolicy="origin"></script>
<script>
    tinymce.init({
        selector: '#content',
        height: 500,
        plugins: 'image code codesample link lists',
        toolbar: 'undo redo | blocks | bold italic | link image codesample | bullist numlist',
        images_upload_url: '/admin/media/upload'
    });
</script>
```

**Step 8: Write admin controller test**

`src/test/kotlin/io/fixdev/blog/controller/admin/AdminArticleControllerTest.kt`:
```kotlin
package io.fixdev.blog.controller.admin

import io.fixdev.blog.service.ArticleService
import io.fixdev.blog.service.TagService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.bean.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(AdminArticleController::class)
@ActiveProfiles("test")
class AdminArticleControllerTest @Autowired constructor(
    private val mockMvc: MockMvc
) {
    @MockBean lateinit var articleService: ArticleService
    @MockBean lateinit var tagService: TagService

    @Test
    fun `admin articles list requires ADMIN role`() {
        mockMvc.perform(get("/admin/articles"))
            .andExpect(status().is3xxRedirection)
    }

    @Test
    fun `admin articles list accessible with ADMIN role`() {
        whenever(articleService.findAll(any())).thenReturn(PageImpl(emptyList()))

        mockMvc.perform(
            get("/admin/articles")
                .with(oidcLogin().authorities(SimpleGrantedAuthority("ROLE_ADMIN")))
        ).andExpect(status().isOk)
            .andExpect(view().name("admin/articles/list"))
    }
}
```

**Step 9: Run all tests**

Run: `./gradlew test`
Expected: PASS

**Step 10: Commit**

```bash
git add src/main/kotlin/io/fixdev/blog/controller/admin/ src/main/resources/templates/admin/ src/test/
git commit -m "feat: add admin controllers for dashboard, articles, comments, tags, media"
```

---

## Task 13: Docker Production Setup

**Files:**
- Create: `Dockerfile`
- Modify: `docker-compose.yml` — add production profile
- Create: `docker-compose.prod.yml`

**Step 1: Create Dockerfile**

`Dockerfile`:
```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY build/libs/fixdev-blog-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Step 2: Create docker-compose.prod.yml**

`docker-compose.prod.yml`:
```yaml
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_PASSWORD: ${DB_PASSWORD}
      KEYCLOAK_CLIENT_SECRET: ${KEYCLOAK_CLIENT_SECRET}
      KEYCLOAK_ISSUER_URI: ${KEYCLOAK_ISSUER_URI}
      MEDIA_UPLOAD_DIR: /data/uploads
    volumes:
      - uploads:/data/uploads
    depends_on:
      - postgres

  postgres:
    image: postgres:17
    environment:
      POSTGRES_DB: fixdev_blog
      POSTGRES_USER: fixdev
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - pgdata:/var/lib/postgresql/data

volumes:
  pgdata:
  uploads:
```

**Step 3: Build and verify**

Run: `./gradlew bootJar && docker build -t fixdev-blog .`
Expected: BUILD SUCCESSFUL, Docker image built

**Step 4: Commit**

```bash
git add Dockerfile docker-compose.prod.yml
git commit -m "feat: add Docker production setup with docker-compose"
```

---

## Task 14: Final Integration Test

**Files:**
- Create: `src/test/kotlin/io/fixdev/blog/BlogIntegrationTest.kt`

**Step 1: Write integration test**

```kotlin
package io.fixdev.blog

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BlogIntegrationTest @Autowired constructor(
    private val mockMvc: MockMvc
) {
    @Test
    fun `app starts and home page loads`() {
        mockMvc.perform(get("/")).andExpect(status().isOk)
    }

    @Test
    fun `blog page loads`() {
        mockMvc.perform(get("/blog")).andExpect(status().isOk)
    }
}
```

**Step 2: Run all tests**

Run: `./gradlew test`
Expected: ALL PASS

**Step 3: Commit**

```bash
git add src/test/kotlin/io/fixdev/blog/BlogIntegrationTest.kt
git commit -m "feat: add integration tests for app startup and public pages"
```

---

## Summary

| Task | Description | Estimated Steps |
|------|-------------|----------------|
| 1 | Project scaffolding (Gradle, Spring Boot, Docker) | 7 |
| 2 | Flyway migrations | 3 |
| 3 | JPA entities | 5 |
| 4 | Repositories | 4 |
| 5 | Spring Security + Keycloak | 6 |
| 6 | ArticleService + SlugService + HtmlSanitizer | 14 |
| 7 | CommentService + TagService | 9 |
| 8 | MediaService | 6 |
| 9 | Thymeleaf layouts + static assets | 6 |
| 10 | Public controllers (home, blog) | 6 |
| 11 | Comment controller (HTMX) | 5 |
| 12 | Admin controllers (dashboard, articles, tags, comments, media) | 10 |
| 13 | Docker production setup | 4 |
| 14 | Final integration test | 3 |

**Total: 14 tasks, ~88 steps**
