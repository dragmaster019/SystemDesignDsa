# 05 — Reliability, Performance & Observability

## Own it like SRE
1. **Telemetry**: `:telemetry.execute([:fintech, :transfer], %{duration_ms: d}, %{status: ok/error})`
2. **OpenTelemetry**: ash + opentelemetry_phoenix + opentelemetry_ecto → traces per transfer.
3. **Logs**: structured JSON (`LoggerJSON`), include `transfer_id`, `idempotency_key`, `branch_id`.
4. **Alerts**: p99 latency > 500ms, error rate > 0.1%, DB pool saturation, Electric lag.

## DB performance checklist (fintech)
- `EXPLAIN ANALYZE` every new query. Index on `(branch_id, inserted_at DESC)`.
- Keep transactions SHORT: validate before `Multi`, never call HTTP inside tx.
- Pool: `pool_size = 2 * cores`, separate pool for migrations.
- Lock timeout: `SET lock_timeout = '5s'` so a stuck transfer fails fast, not deadlocks all branches.

## Snippet: telemetry around transfer
```elixir
def timed_transfer(a, b, amt, key) do
  t0 = System.monotonic_time(:millisecond)
  result = Ledger.transfer(a, b, amt, key)
  :telemetry.execute([:fintech, :transfer],
    %{duration_ms: System.monotonic_time(:millisecond) - t0},
    %{status: elem(result, 0)})
  result
end
```

## Interview line
> "Every transfer emits duration + status. I dashboard p50/p99, alert on error-rate and pool queue, and trace slow ones down to the exact SQL via Ecto telemetry."
