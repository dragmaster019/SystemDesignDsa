# 01 — Elixir for Production (Not Tutorial Elixir)

## Why this matters for fintech
Real money = no `raise` in prod, no float for money, no unhandled crashes. You use OTP the way banks use vaults.

## 1. Pattern matching + `with` (the fintech pipeline)
`with` chains DB → validation → ledger post. First error short-circuits. This is 80% of production Elixir.

## 2. OTP: GenServer + Supervisor (own reliability)
- GenServer holds in-memory rate-limiter / idempotency cache.
- Supervisor restarts it with strategy `:one_for_one`.
- If transfer worker crashes mid-transaction, DB rollback saves you, supervisor restarts worker.

## 3. Money: NEVER use float
Use integer paise/cents. `100.10 * 100 != 10010` in float. Use `Money` lib or integers.

## Run
```bash
elixir 01-elixir-prod-basics/ledger_core.exs
```

## Interview line
> "I model money as integers, wrap multi-table writes in Ecto.Multi transactions, and isolate risky work in supervised processes so one bad transfer can't take down the node."
