# 03 — PostgreSQL + ElectricSQL: Real-Time Local-First Sync

## The architecture (say this in interview)
```
Postgres (source of truth, MarkAI client DB)
   ^ writes via Phoenix (Ecto.Multi, FOR UPDATE locks)
   |
ElectricSQL sync engine (electrifies tables, serves Shapes)
   |
Phoenix Channels / Electric client (React Native / web at branch office)
   v
Local SQLite/OPFS (works offline, syncs when back online)
```

- **Writes go to Phoenix**, not directly to Electric. Electric is for *sync-out*, Phoenix for *validated writes*. This avoids offline-conflict money bugs.
- **Shapes** = filtered subsets: `WHERE branch_id = $1` so a branch only syncs its own ledger.

## Files here
- `schema.sql` — electrified tables (+ idempotency unique index)
- `docker-compose.yml` — Postgres 14 + Electric
- `client_sync.js` — Electric TS client subscribing to a shape
- `elixir_shape.ex` — how Phoenix serves / proxies shapes

## Run
```bash
docker compose up -d
psql $DATABASE_URL -f schema.sql
npm i @electric-sql/client
node client_sync.js
```

## Interview line
> "Postgres stays source of truth with row-level locks. Electric only replicates committed rows as immutable shapes. All money mutations go through Elixir validation; clients never write ledger rows directly — that eliminates split-brain balances."
