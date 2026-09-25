# url-lengthener

Not a URL shortener :P

A URL "lengthener" — the inverse joke of a shortener. Every submitted URL is
hashed with SHA-256, and the full 64-character hex digest becomes the
lengthened link. Deterministic hashing means the same URL always produces the
same link, so duplicates are naturally impossible.

## Status

- **Phase 1** — Frontend (React + Vite), backed by localStorage.
- **Phase 2** — Spring Boot + SQLite backend, `POST`/`GET` endpoints wired to
  the frontend via a swappable storage layer.
- Import/export endpoints on the backend — not yet built.

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

## Why this exists

Built as a side project while learning Spring Core, to have something small
and real to migrate from a localStorage stub to a proper Spring Boot + JPA
backend once the fundamentals were in place.

## License

MIT — see [`LICENSE`](./LICENSE).
