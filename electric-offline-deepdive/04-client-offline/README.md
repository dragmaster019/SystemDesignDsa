# 04 — Client Offline: SQLite + ShapeStream + Outbox

## Local tables (SQLite / OPFS / Expo-SQLite — same idea)
```sql
-- mirror of server truth (read-only, overwritten by shape)
CREATE TABLE ledger_entries(id TEXT PRIMARY KEY, from_id TEXT, to_id TEXT, amount_paise INT, status TEXT);
CREATE TABLE accounts(id TEXT PRIMARY KEY, balance_paise INT);
-- device-owned queue (the only table the app INSERTs into offline)
CREATE TABLE pending_outbox(
  idempotency_key TEXT PRIMARY KEY,
  from_id TEXT, to_id TEXT, amount_paise INT,
  status TEXT DEFAULT 'queued', -- queued | sent | failed
  attempts INT DEFAULT 0,
  created_at TEXT
);
CREATE TABLE shape_state(handle TEXT PRIMARY KEY, offset TEXT); -- resume cursor
```

## UI rule
- Shape data → `CONFIRMED ✓`
- Outbox `queued/sent` → `PENDING ⏳` (do NOT add to balance display twice)
- `failed` → `FAILED — tap to review` + show server reason

See `client_offline.js` for full ShapeStream + outbox worker (~120 lines, commented).
