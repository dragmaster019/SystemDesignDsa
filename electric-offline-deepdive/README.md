# ElectricSQL + Offline Conflicts — Deep Dive (Separate Folder)

This folder is 100% focused on your JD line:
> "Build real-time, local-first sync features with ElectricSQL on top of PostgreSQL"

## Rule #1 for fintech (memorize this)
> **Postgres = source of truth. Phoenix = only writer. Electric = read-only sync-out. Device SQLite = cache, never truth.**

If you let devices write ledger rows directly, two offline branches will create money. Don't.

## Folder map
- `00-architecture/` — the full diagram + request lifecycle (online / offline / reconnect)
- `01-postgres-setup/` — `docker-compose.yml`, `schema.sql`, electrify + publication
- `02-shapes/` — what Shapes are, per-branch shapes, params, security
- `03-phoenix-writes/` — validated writes via `Ecto.Multi`, idempotency, outbox
- `04-client-offline/` — JS client: ShapeStream, local SQLite, optimistic UI
- `05-conflicts/` — runnable conflict simulator (`conflict_sim.exs`) + resolution policy
- `06-testing-chaos/` — offline chaos tests, double-spend test, sync-lag test
- `07-debugging-ops/` — Electric lag, missing rows, shape OOM debugging

## Quick start
```bash
cd electric-offline-deepdive/01-postgres-setup
docker compose up -d
psql postgresql://postgres:password@localhost:5432/fintech -f schema.sql -f electrify.sql

# no-deps simulations (no Elixir install needed beyond elixir itself):
elixir ../05-conflicts/conflict_sim.exs
elixir ../05-conflicts/reconciliation.exs
```

Read in order 00 → 07. Then do the interview drill at the bottom of this file.

## Interview drill (5 answers to rehearse)
1. What is a Shape? → `SELECT ... WHERE branch_id='X'` live subscription over HTTP.
2. Where do writes go? → Phoenix API only, never direct to Electric/Postgres from device.
3. Offline branch pays while offline? → Queue locally as `pending`, POST on reconnect with idempotency key, server revalidates.
4. Two branches overspend same account offline? → Second one gets `402 insufficient_funds` on sync, client reconciles (no auto-merge money).
5. Electric lagging? → Check `wal_level=logical`, `REPLICA IDENTITY FULL`, shape scope, Electric logs.
