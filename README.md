# Local Marketplace Backend

## UI references

- Figma: https://www.figma.com/design/wrqSNPDOn4xGY3ivw1ItlS/Untitled?node-id=0-1&p=f&t=hSeXirzZ7zWpaRUo-0
- Flutter client: https://github.com/Abdelrhmaneldeeb011-max/Local-Marketplace

<<<<<<< HEAD
## Flutter implementation 
🔗 [flutter design](https://github.com/Abdelrhmaneldeeb011-max/Local-Marketplace)
=======
## Local run (Docker Compose)

1. Copy env template:
   ```bash
   cp .env.example .env
   ```
2. Set real secrets in `.env` (at minimum: `DB_PASSWORD`, `JWT_SECRET`).
3. Start:
   ```bash
   docker compose up --build -d
   ```
4. API:
   - Base URL: `http://localhost:8080`
   - Health: `http://localhost:8080/actuator/health`

## Runtime configuration

Required environment variables:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`

Optional:

- `SPRING_PROFILES_ACTIVE` (use `prod` for deployment, `dev` only for local insecure testing)

## Database migrations

Schema is managed by Flyway:

- Migration folder: `src/main/resources/db/migration`
- Baseline migration: `V1__init_schema.sql`
- Migrations run automatically at startup

## Production runbook

### 1. Pre-deploy checklist

- `SPRING_PROFILES_ACTIVE=prod`
- Strong `JWT_SECRET` (32+ random chars)
- Production PostgreSQL reachable from app host
- CI (`.github/workflows/ci.yml`) passing

### 2. Deploy

```bash
cp .env.example .env
# edit .env with production values
docker compose --env-file .env up -d --build
```

### 3. Post-deploy smoke checks

```bash
curl -fsS http://localhost:8080/actuator/health
curl -fsS http://localhost:8080/api/categories
```

### 4. Rollback (simple)

1. Checkout previous stable commit/tag.
2. Rebuild and restart:
   ```bash
   docker compose --env-file .env up -d --build
   ```

## Performance smoke gate (pre-prod)

Run:

```bash
scripts/perf-smoke.sh
```

Defaults:

- Endpoint: `GET /api/categories`
- Sample size: `100`
- Pass thresholds:
  - `avg <= 0.150s`
  - `p95 <= 0.300s`

Override thresholds/sample size:

```bash
REQUESTS=200 AVG_MAX_SECONDS=0.200 P95_MAX_SECONDS=0.400 scripts/perf-smoke.sh
```

## CI

GitHub Actions workflow at `.github/workflows/ci.yml` runs:

1. Maven tests
2. Docker build validation
>>>>>>> 4d25da9 (First build succes)
