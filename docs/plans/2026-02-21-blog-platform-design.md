# FixDev Blog Platform — Design Document

## Overview

Full-featured blog integrated into the fixdev.io portfolio site. Single Spring Boot application with server-side rendering (Thymeleaf + HTMX), Keycloak authentication, and PostgreSQL storage. Deployed as one JAR on VPS via Docker.

## 1. General Architecture

One Spring Boot project, one JAR for deployment on VPS:

- **Public part** — portfolio site (current `index-v6.html` migrated to Thymeleaf templates) + blog pages, article page, comments
- **Admin panel** (`/admin/*`) — protected via Keycloak (ADMIN role only), article CRUD with WYSIWYG editor, tag management, comment moderation, image uploads
- **Comments API** — users authenticate through Keycloak, leave comments via HTMX (no page reload)

**Database:** PostgreSQL (articles, tags, comments, users, media metadata)
**Images:** stored on VPS filesystem (or S3-compatible storage later if needed)

## 2. Data Model

**Article** — title, slug (URL), content (HTML from editor), excerpt, cover image, status (DRAFT/PUBLISHED), SEO metadata (title, description), publish date, update date

**Tag** — name, slug. Many-to-many relationship with Article

**Comment** — text, author (linked to User), article, created date, status (PENDING/APPROVED/REJECTED)

**User** — data from Keycloak (sub, name, email, avatar). Created on first login. Roles: ADMIN (owner) and USER (readers)

**Media** — filename, disk path, MIME type, size, upload date. Optional link to article

## 3. Pages and Routing

### Public

- `/` — home (current portfolio)
- `/blog` — article list with pagination and tag filtering
- `/blog/{slug}` — article page with comments
- `/blog/tag/{slug}` — articles by tag
- `/login` — redirect to Keycloak

### Admin (ADMIN role only)

- `/admin` — dashboard (article count, drafts, pending comments)
- `/admin/articles` — article list with status filters
- `/admin/articles/new` — create article (WYSIWYG editor)
- `/admin/articles/{id}/edit` — edit article
- `/admin/comments` — comment moderation (approve/reject)
- `/admin/tags` — tag management
- `/admin/media` — uploaded files

### HTMX Endpoints (no page reload)

- Submit/delete comments
- Approve/reject comments in admin
- Image upload in editor
- Article list pagination

## 4. Technology Stack

- **Spring Boot 3** + Kotlin
- **Spring Security OAuth2** — Keycloak integration
- **Thymeleaf** — server-side templates (public and admin)
- **HTMX** — interactivity without JS frameworks (comments, pagination, media upload, moderation)
- **TinyMCE** (free community edition) — WYSIWYG article editor
- **PostgreSQL** — database
- **Flyway** — DB migrations
- **Spring Data JPA / Hibernate** — ORM
- **Gradle (Kotlin DSL)** — build
- **Docker** — deployment on VPS (Spring Boot + PostgreSQL via docker-compose)

**SEO:** server-side rendering via Thymeleaf produces ready HTML — search engines index without issues. Open Graph tags for social media sharing.

## 5. Security

- **Keycloak** — single authentication point for everyone: owner (ADMIN) and readers (USER)
- **Admin panel** — accessible only to ADMIN role, Spring Security filters on `/admin/**`
- **Comments** — authorized users only, rate-limiting for spam protection
- **File uploads** — MIME type and size validation, images only (jpg, png, webp), stored outside public directory, served through controller
- **CSRF** — Spring Security default, HTMX supports via meta tag
- **Article content** — HTML sanitization from WYSIWYG editor before saving (XSS protection)

### Keycloak Details

**Realm:** one realm (`fixdev`) in Keycloak.

**Client:** one confidential client `fixdev-blog` — Spring Boot communicates with Keycloak via OAuth2 Authorization Code Flow.

**Roles:**
- `ADMIN` — owner. Assigned manually in Keycloak. Access to `/admin/**`
- `USER` — regular reader. Assigned automatically on registration (default role in Keycloak)

**Reader flow:**
1. Clicks "Sign in" on the site
2. Redirect to Keycloak login page (theme can be customized to match fixdev.io style)
3. Registers or signs in (email + password, or via Identity Provider — GitHub/Google if configured)
4. Keycloak redirects back to site with token
5. Spring Security creates session, extracts role and user data from token
6. On first login — User record created in PostgreSQL (sub, name, email)

**Admin flow:**
1. Same flow, but account in Keycloak has `ADMIN` role
2. Spring Security sees role — grants access to `/admin/**`

**Spring Security configuration:**
- `/`, `/blog/**` — public access (anonymous)
- `POST /blog/{slug}/comments` — `ROLE_USER` or `ROLE_ADMIN` only
- `/admin/**` — `ROLE_ADMIN` only
- Everything else — denied by default

**Sessions:** server-side (Spring Session), not JWT. Simpler for Thymeleaf — user stays logged in while session is alive, no need to refresh tokens on the frontend.

## 6. Application Architecture

### Layered Structure

```
controller/        -> Thymeleaf controllers and HTMX endpoints
service/           -> Business logic (articles, comments, media, tags)
repository/        -> Spring Data JPA repositories
model/entity/      -> JPA entities (Article, Tag, Comment, User, Media)
model/dto/         -> DTOs for forms and responses
config/            -> Spring Security, Keycloak, file storage
```

### Controllers (separated by zone)

- `PublicController` — home, blog, article page
- `CommentController` — HTMX endpoints for comments
- `AdminArticleController` — article CRUD in admin
- `AdminCommentController` — comment moderation
- `AdminTagController` — tag management
- `AdminMediaController` — file upload and management

### Templates (Thymeleaf)

```
templates/
  layout/          -> base.html (shared layout with header/footer)
  public/          -> index, blog-list, blog-post
  admin/
    layout/        -> admin-base.html (admin layout with sidebar)
    articles/      -> list, form
    comments/      -> list
    tags/          -> list
  fragments/       -> reusable pieces (article-card, comment, pagination)
```

### Principles

- Controllers contain no business logic — only mapping: request -> service -> model -> template
- Services are HTTP-agnostic — work with DTOs and entities
- Thymeleaf fragments are reused between public part and admin (e.g., article card)
- HTMX requests return HTML fragments, not full pages
