# Local Development Guide

## Prerequisites

- **Java 21** (for backend compilation)
- **Maven 3.9+**
- **Node.js 18+** (for frontend)
- **Docker & Docker Compose**

## Architecture Overview

| Service | Port | Description |
|---------|------|-------------|
| Frontend (Vite) | 5173 | React dev server, proxies `/api` to backend |
| Backend (Spring Boot) | 8080 | REST API, Swagger UI, Actuator |
| Cosmos DB Emulator | 8081 | Local Azure Cosmos DB + Data Explorer |

## Option 1: Docker Compose (Backend + Cosmos)

Start the Cosmos DB emulator and backend together:

```bash
docker-compose up -d
```

This starts two containers:

- **cosmos-emulator** — Azure Cosmos DB emulator on `https://localhost:8081`
- **backend** — Spring Boot API on `http://localhost:8080` (waits for Cosmos to be healthy)

Then start the frontend dev server:

```bash
npm run dev
```

Frontend runs at `http://localhost:5173` and proxies all `/api/*` requests to the backend.

### Useful Docker commands

```bash
# View logs
docker-compose logs -f backend
docker-compose logs -f cosmos-emulator

# Restart just the backend (after code changes)
docker-compose up -d --build backend

# Stop everything
docker-compose down

# Stop and remove volumes (wipes Cosmos data)
docker-compose down -v
```

## Option 2: Run Backend Outside Docker

If you prefer running the backend directly (faster iteration, debugger support):

### 1. Start the Cosmos DB emulator

```bash
docker-compose up -d cosmos-emulator
```

Wait for it to be healthy:

```bash
docker-compose ps
```

### 2. Start the backend with the `local` profile

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

The `local` profile (`application-local.yml`) points to the Cosmos emulator at `https://localhost:8081` with the public emulator key.

### 3. Start the frontend

```bash
npm run dev
```

## Verifying Services

| URL | What to check |
|-----|---------------|
| http://localhost:5173 | Frontend app |
| http://localhost:8080/actuator/health | Backend health check |
| http://localhost:8080/swagger-ui.html | API documentation |
| http://localhost:8080/api-docs | OpenAPI spec (JSON) |
| https://localhost:8081/_explorer/index.html | Cosmos DB Data Explorer |

## Building

### Backend

```bash
cd backend
mvn clean package           # compile + test + package JAR
mvn clean package -DskipTests  # skip tests for faster builds
```

### Frontend

```bash
npm run build          # production build (output: frontend/dist/)
npm run lint           # ESLint
npm run test           # Vitest (watch mode)
npm run test:coverage  # Vitest with coverage report
```

## Environment Variables

The backend reads these from the environment (or from Spring profile YAML files):

| Variable | Default (local profile) | Description |
|----------|------------------------|-------------|
| `COSMOS_ENDPOINT` | `https://localhost:8081` | Cosmos DB endpoint |
| `COSMOS_KEY` | Public emulator key | Cosmos DB access key |

When running via `docker-compose`, these are set automatically in `docker-compose.yml`. When running the backend directly, the `local` profile provides them.

## Troubleshooting

**Backend won't connect to Cosmos emulator**
- Ensure the emulator is healthy: `docker-compose ps`
- The emulator can take 30-60 seconds to start
- Check logs: `docker-compose logs cosmos-emulator`

**Frontend can't reach the API**
- Vite proxies `/api/*` to `http://localhost:8080` — make sure the backend is running
- Check `vite.config.ts` proxy configuration if you changed the backend port

**Cosmos emulator SSL errors**
- The emulator uses a self-signed certificate. Java may reject it by default.
- The `local` profile is configured to work with the emulator's default settings.
