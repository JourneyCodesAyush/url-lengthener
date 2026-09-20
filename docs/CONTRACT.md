# URL Lengthener — Contract & Decisions

## Status

- **Phase 1** (current): Frontend only, localStorage as the KV store.
- **Phase 2** (later): Spring Boot + SQLite backend, frontend swaps storage layer to call the API.

## Concept

A URL "lengthener" — the inverse joke of a shortener. Each submitted URL is hashed
with SHA-256, and the full 64-character hex hash becomes the lengthened link:
`/url/<64-char-sha256-hex>`.

Because hashing is deterministic, the same input URL always produces the same hash,
so duplicates are naturally impossible — no explicit dedup logic needed.

## Normalization Rule

Applied before hashing, identically on frontend and backend (Phase 2), so
hashes never diverge across phases:

- Lowercase scheme and host.
- Strip a single trailing slash from the path (`/` and `` are equivalent).
- Leave query strings and fragments untouched (a URL with `?ref=x` is a distinct
  entry from the same URL without it).

> `normalize(url)` must be applied before hashing, on both create and lookup.

## Data Model

One record per entry:

| field       | type   | notes                                       |
| ----------- | ------ | ------------------------------------------- |
| `hash`      | string | SHA-256 hex digest of `normalize(url)`, key |
| `url`       | string | original (non-normalized) URL as submitted  |
| `createdAt` | string | ISO 8601 timestamp, optional but cheap      |

## Frontend Routes

| Route        | Purpose                                                       |
| ------------ | ------------------------------------------------------------- |
| `/`          | Form to submit a URL, view result, copy link, import/export   |
| `/url/:hash` | Look up `hash`, redirect to original URL, or show "not found" |

No separate results route — result is local state on `/` after submission.

## Storage Abstraction (frontend)

Regardless of backing store (localStorage now, API later), expose exactly:

- `getUrl(hash) → url | null`
- `saveUrl(url) → hash`

Components only ever call these two functions — never touch localStorage or
`fetch` directly. This is the seam Phase 2 cuts along.

Storage backend (`localStorage` vs `api`) is selected via env config, not
hardcoded, so switching phases is a config change.

## Import / Export

- **Export**: all entries as a flat JSON array, optionally wrapped for
  future-proofing:
  ```json
  { "version": 1, "urls": [ { "hash": "...", "url": "...", "createdAt": "..." } ] }
  ```
- **Import**: re-derive `hash` from each entry's `url` (never trust the file's
  `hash` field). Same-hash-same-url collisions are a no-op (safe by construction,
  since hashing is deterministic).
- Export is always full (no partial export) for now.

## Backend API (Phase 2 — reference only, not built yet)

| Method | Path               | Body                       | Response                                 |
| ------ | ------------------ | -------------------------- | ---------------------------------------- |
| POST   | `/api/urls`        | `{ "url": "" }`            | `{ "hash": "", "url": "" }` (idempotent) |
| GET    | `/api/urls/{hash}` | —                          | `{ "url": "" }` or 404                   |
| GET    | `/api/urls/export` | —                          | JSON array/object as above               |
| POST   | `/api/urls/import` | JSON array/object as above | per-entry result                         |

Redirect logic stays client-side: frontend resolves `/url/:hash` via `GET
/api/urls/{hash}`, then `window.location.href = url`. This means the link only
resolves through the SPA, not as a true server-side HTTP redirect — accepted
tradeoff, not a bug.

Endpoint naming: `/api/urls`, resource-style (not `/api/shorten` or
`/api/lengthen`) — matches REST conventions and stays consistent with the
export/import paths already nested under it.

## Tooling

- **Linter**: [oxlint](https://oxc.rs/) (Vite's scaffolding prompt default),
  not ESLint. Rust-based, near-instant, no separate `eslint-plugin-react*`
  setup needed for basic rules. Note: `react/react-in-jsx-scope` is disabled
  in `.oxlintrc.json` — it's a false positive under React's automatic JSX
  runtime (Vite's default), which never puts `React` in scope by design.
  `react/jsx-uses-react` is ESLint-only and doesn't exist as an oxlint rule.
- **Formatter**: Prettier (oxlint doesn't format, so both coexist).
- **Test runner**: Vitest + React Testing Library.
- **Router**: react-router-dom.
- **Tests**: none on the frontend (deliberate — learning focus is Spring
  Core/Boot, not frontend test coverage). Standing convention for the
  project: a mirrored `tests/` directory (not colocated `*.test.js` files),
  matching Spring Boot's own `src/test/java` layout, for consistency once
  Phase 2 starts.

## Explicitly Deferred

- **Auth**: none. Public create/resolve, no per-user data. Revisit as its own
  topic later, separate from Spring Core.
- **Database**: SQLite over Postgres for Phase 2 — simplicity for a solo
  learning project, standard JPA/Hibernate keeps a future Postgres swap cheap.
