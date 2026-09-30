# 08 — Client Delivery: Be MarkAI's Technical Voice On-Site

## Day-1 scoping script (use with product/business)
1. "What is the money flow? Who debits, who credits, when is it final?"
2. "What happens on retry / double-click / offline? (forces idempotency talk)"
3. "Which data must work offline? (defines Electric shapes)"
4. "What is the audit requirement? (append-only vs editable)"
5. Flag risks early: "If two branches post same loan offline, last-write-wins loses money — so writes must go through server validation."

## How to talk to non-engineers
- Bad: "We need SERIALIZABLE isolation for the Ecto.Multi."
- Good: "If two people tap Pay at the same second, we lock the balance for 100ms so money can't be spent twice. Costs 50ms extra, prevents lost money."

## Raise the bar
- PR template: money-impact? idempotency tested? EXPLAIN ANALYZE attached? rollback plan?
- Feed learnings back: "Branch-Blr needed Marathi offline receipts → propose shape-per-language playbook for MarkAI."

## Mock Q&A (practice out loud)
1. Design UPI-like transfer in Elixir in 10 min → draw: App → Phoenix validate → Multi tx → Postgres → Electric shape → device.
2. Offline branch posts payment, comes online with conflict → answer: server revalidates, rejects overdraft, client reconciles via shape (never auto-merge money).
3. p99 spiked to 2s → answer: check pool queue → pg_stat_statements → missing index on (branch_id, inserted_at).
4. Why Elixir for fintech? → isolated processes + OTP supervisors + pattern matching make concurrent money flows safe and readable.
