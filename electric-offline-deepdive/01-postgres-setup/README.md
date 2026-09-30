# 01 — Postgres Setup for Electric

## `wal_level=logical` is mandatory
Electric tails the Write-Ahead Log. Without logical replication, no sync.

## Files
- `docker-compose.yml` — Postgres 14 + Electric
- `schema.sql` — accounts + ledger_entries (append-only, idempotency UNIQUE)
- `electrify.sql` — replica identity + publication

## Run
```bash
docker compose up -d
psql postgresql://postgres:password@localhost:5432/fintech -f schema.sql -f electrify.sql
docker logs electric -f  # should say "electrified ledger_entries"
```
