# 05 — Conflicts: Why Money Must Never Auto-Merge

## The scenario (will be asked in interview)
- Alice has 10,000 paise.
- Branch A (offline) queues `A→B 8,000`.
- Branch B (offline) queues `A→C 7,000`.
- Both come online. Total attempted 15,000 > 10,000. One MUST fail.

## Policy (copy-paste for design doc)
1. **Ledger rows are immutable facts, never merged.** Last-write-wins is FORBIDDEN for money.
2. **Server revalidates every queued intent** against current balance inside `FOR UPDATE` tx. First committer wins, second gets `422 insufficient_funds`.
3. **Client reconciles, never retries blindly**: mark failed, refresh shape, ask user.
4. **Idempotency makes retry safe**: same key → `200 duplicate`, never second charge.

## Run the simulations (no deps)
```bash
elixir 05-conflicts/conflict_sim.exs      # shows overspend rejected, total conserved
elixir 05-conflicts/reconciliation.exs    # shows outbox flush: 201 / 200 / 422 handling
```
