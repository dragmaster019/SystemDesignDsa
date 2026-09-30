# 06 — Fintech Domain: Double-Entry, Idempotency, Lending

## Double-entry (non-negotiable)
Every rupee debited = rupee credited. `ledger_entries` is append-only; balances are derived/cached, never edited directly.

Run: `elixir 06-fintech-domain/double_entry.exs`

## Idempotency pattern
Client generates UUID → sends in header → DB unique constraint → retry returns original. Protects against double-charge on network retry / double-click.

## Lending mini-model
- `loans`: principal, interest_bps, status (active/closed/defaulted)
- `repayments` reference `loan_id`; late fee = function of days overdue.
- Never delete financial rows — use `reversing_entry_id` for corrections (audit trail).

## Interview lines
- "Floats lose paise; I store BIGINT paise and validate with CHECK constraints too, not just app code."
- "Refunds are new entries pointing to the original, never DELETEs — auditors need the trail."
