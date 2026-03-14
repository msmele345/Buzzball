# BuzzBall MLB Stats App — Plan Overview

```mermaid
flowchart TD
    subgraph P1["Phase 1: Project Foundation"]
        P1A[git init + .gitignore]
        P1B[Monorepo layout\nfrontend/ + backend/]
        P1C[Spring Boot 3.3 pom.xml\nJava 21]
        P1D[Frontend packages\nReact Query · Recharts · Zustand]
        P1E[docker-compose.yml\nCosmos Emulator + Backend]
        P1A --> P1B --> P1C
        P1B --> P1D
        P1B --> P1E
    end

    subgraph P2["Phase 2: Data Layer"]
        P2A[Domain Models\nPlayer · BattingStats · PitchingStats\nFieldingStats · Team · MatchupProjection]
        P2B[Cosmos DB Containers\n6 containers with partition keys]
        P2C[Repositories\nCosmosRepository extensions]
        P2D[Ingestion Services\nMlbStatsApiClient · StatcastClient\nFanGraphsClient + circuit breaker]
        P2E[DataIngestionOrchestrator\nmerge sources → upsert Cosmos]
        P2F[DataRefreshScheduler\ncron: 2h · 4h · 6AM · 7AM ET]
        P2A --> P2B --> P2C
        P2D --> P2E --> P2F
    end

    subgraph P3["Phase 3: Backend API"]
        P3A[Controllers\nPlayer · Team · Dashboard\nMatchup · LiveUpdate SSE]
        P3B[DTOs + MapStruct\nPlayerSummaryDto · TrendingPlayerDto\nTeamComparisonDto · etc.]
        P3C[Config\nAzureConfig · WebConfig CORS\nCacheConfig Caffeine]
        P3D[SpringDoc OpenAPI UI]
        P3A --> P3B --> P3C --> P3D
    end

    subgraph P4["Phase 4: Frontend Core"]
        P4A[Router\ncreateBrowserRouter\n/ · /players/:id · /teams · /teams/:id]
        P4B[API Client\nAxios + interceptors\nReact Query hooks]
        P4C[TypeScript Types\nmirror backend DTOs]
        P4D[Chart Components\nRadarChart · LineChart\nBarChart · SparkLine]
        P4E[Dashboard Components\nTrendingPlayers · Standings\nLeagueLeaders · TeamComparison]
        P4F[Player Components\nPlayerHeader · StatsSummaryCards\nSeasonProgression · AdvancedMetricsRadar\nSplitStatsTable · UpcomingMatchups]
        P4G[Zustand Stores\nuiStore · playerStore]
        P4A --> P4B --> P4C
        P4B --> P4D & P4E & P4F
        P4G --> P4E & P4F
    end

    subgraph P5["Phase 5: Advanced Features"]
        P5A[Matchup Prediction Engine\n60% current splits\n30% career vs handedness\n10% park factor]
        P5B[ParkFactorService\nBaseball Savant data]
        P5C[SSE Live Updates\nLiveUpdateController endpoint]
        P5D[useLiveUpdates hook\nEventSource + jitter invalidation]
        P5A --> P5B
        P5C --> P5D
    end

    subgraph P6["Phase 6: Infrastructure & CI/CD"]
        P6A[Azure Bicep IaC\ncosmos · container-apps\nkeyvault · static-web-app · acr]
        P6B[GitHub Actions\nfrontend-ci · backend-ci\ndeploy-prod OIDC]
        P6C[Environments\ndev auto · staging auto\nprod manual approval]
        P6A --> P6B --> P6C
    end

    subgraph DS["Data Sources"]
        DS1["Tier 1: MLB Stats API\nstatsapi.mlb.com\nRosters · Schedules · Box scores"]
        DS2["Tier 2: Baseball Savant CSV\nxBA · xSLG · xwOBA\nExit velocity · Barrel%"]
        DS3["Tier 3: FanGraphs\nWAR · FIP · xFIP · wRC+\n⚡ circuit breaker"]
    end

    subgraph HOST["Azure Hosting"]
        H1[Static Web Apps\nFrontend CDN + PR previews]
        H2[Container Apps\nBackend scale-to-zero]
        H3[Cosmos DB Serverless\nJSON-native database]
        H4[Key Vault + Managed Identity\nNo credentials in code]
    end

    P1 --> P2 --> P3 --> P4 --> P5 --> P6
    DS1 & DS2 & DS3 --> P2D
    P6A --> H1 & H2 & H3 & H4
```

## Data Flow

```mermaid
sequenceDiagram
    participant FE as Frontend (SWA)
    participant BE as Backend (Container Apps)
    participant C as Cosmos DB
    participant MLB as MLB Stats API
    participant SC as Baseball Savant
    participant FG as FanGraphs

    Note over BE,FG: Scheduled ingestion (background)
    BE->>MLB: Roster/schedule fetch (every 4h)
    BE->>SC: Statcast CSV fetch (daily 6AM ET)
    BE->>FG: WAR/FIP fetch (daily 7AM ET) ⚡ circuit breaker
    BE->>C: Upsert merged player stats

    Note over FE,C: Runtime request flow
    FE->>BE: GET /api/v1/dashboard/trending
    BE->>C: Query batting-stats + pitching-stats
    C-->>BE: Cached results (Caffeine: 2min)
    BE-->>FE: TrendingPlayerDto[]

    Note over FE,BE: Live updates (SSE)
    FE->>BE: GET /api/v1/live/updates (EventSource)
    BE-->>FE: data: refresh event
    FE->>FE: invalidateQueries() + jitter(0-2s)
```

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React 19 · TypeScript · Vite · Recharts · React Query · Zustand |
| Backend | Java 21 · Spring Boot 3.3 · Spring Data Cosmos · Resilience4j |
| Database | Azure Cosmos DB (NoSQL, serverless dev) |
| Hosting | Azure Static Web Apps + Container Apps |
| Secrets | Azure Key Vault + Managed Identity |
| IaC | Azure Bicep |
| CI/CD | GitHub Actions + OIDC (no stored secrets) |
| Local Dev | Docker Compose (Cosmos emulator + backend) |

