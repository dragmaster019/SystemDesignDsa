# 02 — Shapes: The Only Thing the Device Subscribes To

## What is a Shape?
A live, filtered `SELECT` served over HTTP by Electric:
```
GET http://localhost:5133/v1/shape?table=ledger_entries&where=branch_id='blr'
→ initial snapshot + live log of inserts (chunked, with offset resume)
```

## Rules for fintech shapes
1. **One shape per branch** — `where=branch_id='blr'`. Never sync whole ledger to a phone.
2. **ledger_entries = immutable log** — perfect for shapes (only INSERTs, no update conflicts).
3. **accounts = mutable** — sync it too, but treat device copy as display-only.
4. **Auth at proxy** — devices must NOT hit Electric directly in prod. Proxy via Phoenix which injects `branch_id` from JWT.

## Shape catalog for this project
| Shape | Table + where | Used for |
|-------|---------------|----------|
| branch-ledger | `ledger_entries WHERE branch_id=$branch` | transaction list |
| branch-accounts | `accounts WHERE branch_id=$branch` | balances header |
| my-pending | local only (SQLite, not Electric) | offline outbox |

## Phoenix proxy (don't expose Electric port publicly)
```elixir
# lib/fintech_web/controllers/shape_controller.ex
def show(conn, %{"table" => table}) do
  branch = conn.assigns.current_user.branch_id  # from auth plug
  allowed = ["ledger_entries", "accounts"]
  if table in allowed do
    url = "http://electric:5133/v1/shape?table=#{table}&where=branch_id%3D%27#{branch}%27"
    # stream Electric response through, preserving offset headers
    Proxy.stream(conn, url)
  else
    send_resp(conn, 403, "forbidden shape")
  end
end
```

## Try with curl
```bash
# snapshot
curl "http://localhost:5133/v1/shape?table=ledger_entries" -i | head -20
# branch-filtered + live (offset gives resume)
curl "http://localhost:5133/v1/shape?table=ledger_entries&where=branch_id%20%3D%20%27blr%27"
```
Response headers `electric-offset` + `electric-handle` are how the client resumes after offline — save them in SQLite.
