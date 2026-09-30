# 04 — Concurrency & Distributed State (BEAM Superpower)

## What breaks in prod
100 branch agents hit `POST /transfers` at once → double-spend without `SELECT ... FOR UPDATE` + unique idempotency key.

## BEAM answers
1. **Isolation**: each request = lightweight process (~2KB). One crash doesn't kill others.
2. **GenServer serialization**: route same-account transfers through one process or DB row-lock.
3. **Distributed**: Horde / libcluster / `:global` for multi-node locks. Postgres remains final arbiter.

## Run
```bash
elixir 04-concurrency-distributed/transfer_server.exs
```
Spawns 1000 concurrent transfers against a GenServer ledger — shows serialized state stays consistent.

## Interview line
> "I serialize per-account writes (row lock or GenServer), keep Ecto.Multi transactions short to avoid lock contention, and use Postgres as the distributed lock — BEAM processes scale, DB guarantees correctness."
