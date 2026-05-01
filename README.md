# Local-Marketplace

## UI Design (Figma)

🔗 [View full design on Figma](https://www.figma.com/design/wrqSNPDOn4xGY3ivw1ItlS/Untitled?node-id=0-1&p=f&t=hSeXirzZ7zWpaRUo-0)

## Flutter implementation 
🔗 [flutter design](https://github.com/Abdelrhmaneldeeb011-max/Local-Marketplace)

## Run with Docker Compose

1. Create your environment file:
   ```bash
   cp .env.example .env
   ```
2. Update `.env` with strong values for `DB_PASSWORD` and `JWT_SECRET`.
3. Start services:
   ```bash
   docker compose up --build
   ```
4. API will be available at `http://localhost:8080`.

## Runtime environment variables

The backend expects these variables in deployed environments:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`

Do **not** deploy with `SPRING_PROFILES_ACTIVE=dev`.

Firebase auth will be added later; `.env.example` already includes placeholders for upcoming Firebase secrets.
