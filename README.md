# url-lengthener

[![Frontend CI](https://github.com/journeycodesayush/url-lengthener/actions/workflows/frontend.yml/badge.svg)](https://github.com/journeycodesayush/url-lengthener/actions/workflows/frontend.yml)
[![Backend CI](https://github.com/journeycodesayush/url-lengthener/actions/workflows/backend.yml/badge.svg)](https://github.com/journeycodesayush/url-lengthener/actions/workflows/backend.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)


Not a URL shortener :P

A URL "lengthener" — the inverse joke of a shortener. Every submitted URL is
hashed with SHA-256, and the full 64-character hex digest becomes the
lengthened link. Deterministic hashing means the same URL always produces the
same link, so duplicates are naturally impossible.

## Status

- **Phase 1** — Frontend (React + Vite), backed by localStorage.
- **Phase 2** — Spring Boot + SQLite backend, all four endpoints (`create`,
  `lookup`, `export`, `import`) wired to the frontend via a swappable storage
  layer.
- Service-layer tests, CI (lint/format/test for both frontend and backend),
  and Docker support for both sides.

See [`docs/CONTRACT.md`](docs/CONTRACT.md) for the full data model, API
contract, and every design decision made along the way (why SQLite over
Postgres, why hashing is deterministic, why auth was deliberately skipped).

## Structure

```
url-lengthener/
├── frontend/   React + Vite, oxlint + Prettier
├── backend/    Spring Boot + SQLite
└── docs/       CONTRACT.md — the project's decision record
```

## Running locally

**Frontend**
```bash
cd frontend
npm install
npm run dev
```

**Backend**
```bash
cd backend
./mvnw spring-boot:run
```

By default the frontend runs against localStorage. To point it at the
backend instead, set in `frontend/.env.local`:
```
VITE_STORAGE_MODE=api
VITE_API_BASE_URL=http://localhost:8080
```

## Running with Docker

Runs both services together, with the SQLite database persisted in a named
Docker volume:

```bash
docker compose up --build
```

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`

The frontend image is built with `VITE_API_BASE_URL=http://localhost:8080`
baked in at build time (Vite env vars aren't runtime-configurable) — see
`docker-compose.yml` if you need to point it elsewhere.

To stop and remove the containers (the database volume persists):
```bash
docker compose down
```

To also wipe the stored data:
```bash
docker compose down -v
```

## Why this exists

Built as a side project while learning Spring Core, to have something small
and real to migrate from a localStorage stub to a proper Spring Boot + JPA
backend once the fundamentals were in place.

## License

MIT — see [`LICENSE`](./LICENSE).
