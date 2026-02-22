# CI/CD & Deployment Design

## Overview

Automated CI/CD pipeline for fixdev-io2 blog platform. Single-repo approach — one workflow with build + deploy jobs.

## Architecture

```
push to main ──► [ci-cd.yml]
                   ├── job: build
                   │   test → bootJar → Docker image → GHCR
                   │
                   └── job: deploy (needs: build)
                       SSH to Contabo → docker compose pull → up -d
                       ▼
                   Server: fixdev.io via shared Traefik
```

## Repo Structure

```
fixdev-io/
├── .github/workflows/ci-cd.yml   # build + deploy
├── deployment/
│   ├── docker-compose.yml         # prod: fixdev-blog + fixdev-postgres
│   └── .env.example               # template for secrets
├── Dockerfile
└── src/
```

## docker-compose.yml

Services:
- **fixdev-blog**: image from GHCR, Traefik labels for `fixdev.io`, networks `web` + `internal`, uploads volume
- **fixdev-postgres**: postgres:17, network `internal` only, persistent volume

## Infrastructure

- **Domain**: fixdev.io
- **Server**: Contabo (shared with push_helper)
- **Traefik**: shared external `web` network
- **Keycloak**: shared from push_helper stack (auth.notifioflow.com)
- **PostgreSQL**: dedicated container (separate from push_helper postgres)

## Environment Variables (deployment/.env)

```
DB_PASSWORD=<postgres password>
KEYCLOAK_CLIENT_SECRET=<keycloak client secret>
KEYCLOAK_ISSUER_URI=https://auth.notifioflow.com/auth/realms/fixdev
```

## Required GitHub Secrets

- `SERVER_HOST` — Contabo server IP
- `SERVER_USER` — SSH username
- `SERVER_SSH_KEY` — private SSH key
