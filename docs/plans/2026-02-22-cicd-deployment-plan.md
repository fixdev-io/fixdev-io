# CI/CD & Deployment Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Automated CI/CD pipeline — push to main triggers tests, Docker image build, push to GHCR, and SSH deployment to Contabo server. All in one repo.

**Architecture:** Single-repo approach. One workflow with two jobs: `build` (test → JAR → Docker image → GHCR) and `deploy` (SSH to Contabo → docker compose pull → up). Shares Traefik and Keycloak with existing push_helper stack.

**Tech Stack:** GitHub Actions, GHCR, Docker Compose, Traefik, PostgreSQL 17, SSH deploy

---

### Task 1: Create CI/CD workflow

**Files:**
- Create: `.github/workflows/ci-cd.yml`

**Step 1: Create the workflow file**

```yaml
name: CI/CD

on:
  push:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: write

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: 'gradle'

      - name: Run tests
        run: ./gradlew test

      - name: Build JAR
        run: ./gradlew bootJar

      - name: Log in to GHCR
        uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      - name: Build and push Docker image
        uses: docker/build-push-action@v5
        with:
          context: .
          push: true
          tags: |
            ghcr.io/fixdev-io/fixdev-io:latest
            ghcr.io/fixdev-io/fixdev-io:sha-${{ github.sha }}

  deploy:
    needs: build
    runs-on: ubuntu-latest

    steps:
      - name: Deploy to Contabo
        uses: appleboy/ssh-action@v1
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SERVER_SSH_KEY }}
          script: |
            set -e
            cd /home/const/fixdev-io
            git pull origin main
            docker compose -f deployment/docker-compose.yml pull fixdev-blog
            docker compose -f deployment/docker-compose.yml up -d
            docker compose -f deployment/docker-compose.yml ps
```

**Step 2: Commit**

```bash
git add .github/workflows/ci-cd.yml
git commit -m "ci: add CI/CD workflow with GHCR and SSH deploy"
```

---

### Task 2: Create deployment docker-compose.yml

**Files:**
- Create: `deployment/docker-compose.yml`

**Step 1: Create docker-compose.yml**

```yaml
services:
  fixdev-blog:
    image: ghcr.io/fixdev-io/fixdev-io:latest
    container_name: fixdev-blog
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:postgresql://fixdev-postgres:5432/fixdev_blog
      SPRING_DATASOURCE_USERNAME: fixdev
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      DB_PASSWORD: ${DB_PASSWORD}
      KEYCLOAK_CLIENT_SECRET: ${KEYCLOAK_CLIENT_SECRET}
      KEYCLOAK_ISSUER_URI: ${KEYCLOAK_ISSUER_URI}
      MEDIA_UPLOAD_DIR: /data/uploads
    volumes:
      - uploads:/data/uploads
    depends_on:
      - fixdev-postgres
    labels:
      - "traefik.enable=true"
      - "traefik.http.routers.fixdev-blog.rule=Host(`fixdev.io`)"
      - "traefik.http.routers.fixdev-blog.entrypoints=websecure"
      - "traefik.http.routers.fixdev-blog.tls.certresolver=le"
      - "traefik.http.services.fixdev-blog.loadbalancer.server.port=8080"
      - "traefik.docker.network=web"
    networks:
      - web
      - internal
    restart: unless-stopped

  fixdev-postgres:
    image: postgres:17
    container_name: fixdev-postgres
    environment:
      POSTGRES_DB: fixdev_blog
      POSTGRES_USER: fixdev
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - pgdata:/var/lib/postgresql/data
    networks:
      - internal
    restart: unless-stopped

networks:
  web:
    external: true
  internal:
    driver: bridge

volumes:
  pgdata:
  uploads:
```

**Step 2: Commit**

```bash
git add deployment/docker-compose.yml
git commit -m "feat: add production docker-compose with Traefik and postgres"
```

---

### Task 3: Create .env.example and update .gitignore

**Files:**
- Create: `deployment/.env.example`
- Modify: `.gitignore` (add `deployment/.env`)

**Step 1: Create .env.example**

```
DB_PASSWORD=changeme
KEYCLOAK_CLIENT_SECRET=changeme
KEYCLOAK_ISSUER_URI=https://auth.notifioflow.com/auth/realms/fixdev
```

**Step 2: Add deployment/.env to .gitignore**

Append `deployment/.env` to the existing `.gitignore`.

**Step 3: Commit**

```bash
git add deployment/.env.example .gitignore
git commit -m "feat: add .env.example and ignore deployment secrets"
```

---

### Task 4: Manual setup steps (checklist for user)

These steps must be done manually:

1. **Add secrets to fixdev-io repo** (Settings → Secrets → Actions):
   - `SERVER_HOST` — Contabo server IP
   - `SERVER_USER` — SSH username (e.g., `const`)
   - `SERVER_SSH_KEY` — private SSH key
2. **On server** — clone repo and create `.env`:
   ```bash
   cd /home/const
   git clone git@github.com:fixdev-io/fixdev-io.git
   cd fixdev-io
   cp deployment/.env.example deployment/.env
   # edit deployment/.env with real values
   ```
3. **Verify Keycloak realm** `fixdev` exists at `auth.notifioflow.com`
4. **DNS** — point `fixdev.io` A record to Contabo server IP
