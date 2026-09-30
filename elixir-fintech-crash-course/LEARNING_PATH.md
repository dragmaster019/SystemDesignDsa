# 7-Day Fast Plan (3-4 hrs/day)

- **Day 1**: 01 — run `ledger_core.exs`, rewrite it from memory. Learn `with`, `GenServer`, supervisor basics. Read: Elixir guides (OTP chapter).
- **Day 2**: 02 — scaffold `mix phx.new fintech`, copy `transfer_controller.ex`, write `ledger_test.exs` (happy path + overdraft + duplicate key).
- **Day 3**: 03 — `docker compose up`, apply `schema.sql`, run `client_sync.js`. Break it: kill network, pay offline, reconnect, watch sync.
- **Day 4**: 04 + 06 — run `transfer_server.exs` + `double_entry.exs`. Explain double-entry + row-lock to a friend in 2 min.
- **Day 5**: 05 + 07 — add Telemetry to a transfer, run EXPLAIN ANALYZE, practice `:observer` + `pg_locks` queries.
- **Day 6**: 08 — mock interview: whiteboard UPI transfer, offline-conflict, p99 debug. Record yourself, <10 min each.
- **Day 7**: Build mini-project: branch ledger API + Electric shape + offline HTML page. Push to GitHub, link on resume.

## If you know Java/C++ (your background)
| Java/C++ | Elixir |
|----------|--------|
| synchronized / mutex | GenServer call / `SELECT ... FOR UPDATE` |
| thread pool | BEAM scheduler (millions of processes) |
| try/catch | `with` + `{:ok,_}/{:error,_}` tuples |
| double / BigDecimal | integer paise + `Decimal` lib |
| Spring @Transactional | `Ecto.Multi` + `Repo.transaction()` |
