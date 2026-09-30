# 00 — Architecture: How It All Fits

## Diagram (branch office with flaky network)

```
┌─────────────┐   POST /api/transfers + Idempotency-Key   ┌──────────────┐
│ Branch App  │ ────────────────────────────────────────▶ │ Phoenix/Elixir│
│ (web/RN)    │                                           │ validate →    │
│ SQLite cache│ ◀──────────────────────────────────────── │ Ecto.Multi tx │
└─────────────┘   Electric Shape: GET /v1/shape?table=    └──────┬───────┘
        ▲         ledger_entries&where=branch_id='blr'            │ commit
        │                                                        ▼
        │                                                ┌──────────────┐
        └──────────────────────────────────────────────── │ Postgres     │
              ElectricSQL sync engine tails WAL          │ truth tables │
              (logical replication)                      └──────────────┘
```

## Three modes

### 1. Online (happy path)
1. User taps Pay → app POSTs to Phoenix with `Idempotency-Key: uuid`
2. Phoenix: validate → `SELECT ... FOR UPDATE` → `Ecto.Multi` insert + balance update → commit
3. Postgres WAL → Electric → Shape pushes row to all subscribed branch devices in ~100ms
4. Device updates SQLite + UI from Shape (not from POST response alone)

### 2. Offline (store-and-forward)
1. No network → app saves intent to local `pending_outbox` table in SQLite with same UUID
2. UI shows `PENDING ⏳` — never `SUCCESS`. This wording matters for money.
3. Background sync worker retries with exponential backoff.

### 3. Reconnect (reconciliation)
1. Worker POSTs queued intents in order, same idempotency keys
2. Server accepts or rejects each (e.g. insufficient funds due to another branch spending first)
3. Device deletes accepted from outbox, marks rejected as `FAILED — refresh balance`
4. Shape replays truth → device converges to server state

## Why not write directly to Electric/Postgres from device?
- No validation (negative amounts, overdrafts pass through)
- Split-brain: two devices both think they spent the same 1000 paise
- No audit (who approved? which idempotency key?)
- Shapes are designed for sync-out; writes need consensus = Postgres tx.
