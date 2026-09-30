# 07 — Debugging + Ops (When Sync Breaks in Prod)

## Symptom → check

| Symptom | Check first | Command |
|---------|-------------|---------|
| Shape empty / missing new rows | `REPLICA IDENTITY` + publication | `SELECT relreplident FROM pg_class WHERE relname='ledger_entries';` (want `f` = FULL) |
| Electric won't start | `wal_level` | `SHOW wal_level;` (want `logical`) |
| Device OOM / huge initial sync | shape too broad | narrow `where=branch_id='...'`; add `LIMIT` + pagination via offset |
| Balances diverge between devices | someone writes bypassing Phoenix | `SELECT * FROM ledger_entries ORDER BY inserted_at DESC LIMIT 5;` — every row must have valid idempotency_key from API |
| p99 POST slow | lock contention | `SELECT pid, now()-query_start AS age, query FROM pg_stat_activity WHERE state='active' ORDER BY age DESC;` |
| Deadlock errors | tx too long / wrong lock order | always lock accounts in sorted id order; keep tx < 5s; `SET lock_timeout='5s'` |

## Electric logs that matter
```bash
docker logs electric 2>&1 | grep -i -E "electrif|error|shape|replication" | tail -50
```

## Shape resume debugging
Client must persist `electric-offset` + `electric-handle` per shape in SQLite (`shape_state` table). On restart, pass `offset` back. If you always pass `-1`, you re-sync everything (slow) — if you pass a stale handle, Electric returns `409` → drop and re-snapshot.

## One-line health check (add to runbook)
```bash
psql $DATABASE_URL -c "SELECT count(*), max(inserted_at) FROM ledger_entries;" \
&& curl -s "http://localhost:5133/v1/shape?table=ledger_entries&offset=-1" -o /dev/null -w "electric HTTP %{http_code}\n"
```
