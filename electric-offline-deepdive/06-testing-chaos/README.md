# 06 — Testing Chaos (Prove It Doesn't Lose Money)

## 4 tests every fintech sync needs (write these before interview demo)
1. **Double-tap / retry** — POST same idempotency key 5x → exactly 1 ledger row.
2. **Concurrent overspend** — 50 parallel transfers exceeding balance → sum posted ≤ opening balance, rest 422.
3. **Offline queue + reconnect** — kill network, queue 3 pays, restore, flush → all confirmed once, shape converges.
4. **Shape resume** — save `electric-offset`, kill client, restart with offset → no missed rows, no duplicates.

## Manual chaos script
```bash
# terminal 1: server
mix phx.server
# terminal 2: electric + db
docker compose up
# terminal 3: hammer with same key (expect 1x201 + 4x200)
for i in 1 2 3 4 5; do
  curl -s -X POST localhost:4000/api/transfers \
    -H "Idempotency-Key: chaos-1" -H "Content-Type: application/json" \
    -d '{"from":"alice","to":"bob","amount_paise":100}' &
done; wait
# check: exactly 1 row with chaos-1
psql $DATABASE_URL -c "SELECT count(*) FROM ledger_entries WHERE idempotency_key='chaos-1';"
# expect: 1
```

## Elixir concurrency test snippet (ExUnit)
```elixir
test "concurrent debits never overspend" do
  tasks = for _ <- 1..50, do: Task.async(fn ->
    Ledger.transfer("alice", "bob", 1_000, Ecto.UUID.generate())
  end)
  results = Task.await_many(tasks)
  posted = Enum.count(results, &match?({:ok, _}, &1))
  assert Repo.get!(Account, "alice").balance_paise >= 0
end
```
