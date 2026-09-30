# 07 — Production Debugging Playbook (Across the Stack)

## 1. Transfer failing / stuck?
```
1. Check logs for idempotency_key → duplicate or validation error?
2. :observer.start() → process mailbox growing? (GenServer bottleneck)
3. SELECT * FROM pg_locks WHERE NOT granted; → who blocks whom?
4. EXPLAIN ANALYZE the transfer query → seq scan? missing index?
```

## 2. BEAM commands (learn these 5)
```elixir
:observer.start()            # GUI: processes, memory, supervisors
Process.list() |> length()   # process count leak?
:sys.get_state(TransferServer)
:telemetry.list_handlers([:fintech, :transfer])
Logger.configure(level: :debug)
```

## 3. DB performance
```sql
-- top slow queries
SELECT query, mean_exec_time, calls FROM pg_stat_statements ORDER BY mean_exec_time DESC LIMIT 10;
-- lock diagnosis
SELECT pid, usename, query, state FROM pg_stat_activity WHERE state = 'active';
-- shape lag: compare max(inserted_at) in Postgres vs Electric client count
```

## 4. Electric sync gap?
- Check Electric logs for `electrified` errors (REPLICA IDENTITY missing).
- Shape `where` clause too broad → syncs whole table → OOM on device. Narrow by branch.
- Client writes bypassing Phoenix → conflicting balances. Enforce server-side validation.

## Interview story template
> "Branch reported missing payments. Traced via idempotency_key → found duplicate-key 200s (client retrying). Root cause: 8s tx holding row lock due to HTTP call inside Multi. Moved HTTP out, p99 8s → 120ms."
