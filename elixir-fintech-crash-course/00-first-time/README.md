# Elixir First Time? Read This First (Simple + Prod Q&A)

For Java/C++ people. No prior Elixir needed.
Format everywhere: **Problem → Why it is a problem → Solution + code.**

---

## PART 0: 10 definitions in 1 line each

1. **Elixir** = language like Java but for concurrent, never-crash servers.
2. **BEAM** = VM that runs Elixir (like JVM runs Java). Runs millions of tiny processes.
3. **Process** = tiny isolated task (~2KB). Not OS thread. If one crashes, others live.
4. **OTP** = ready-made toolkit: GenServer + Supervisor + Tasks. Like Spring Boot for concurrency.
5. **GenServer** = one worker that holds state and handles one message at a time. Like a single-threaded service class.
6. **Supervisor** = babysitter that restarts crashed workers. Like auto-restart in Kubernetes.
7. **Pattern matching** = `{:ok, x} = result`. Like destructuring + if-check in one line.
8. **Pipe `|>`** = `x |> f() |> g()` means `g(f(x))`. Like method chaining.
9. **Ecto** = database library (like Hibernate/JPA but explicit, no magic).
10. **Phoenix** = web framework (like Spring Boot). Handles API requests.

---

## PART 1: Basics (must know before interview)

### 1. Variables are immutable
**Problem:** You try `x = 1; x = x + 1` and think it changes old x.
**Why problem:** In Elixir old value never changes. If you assume mutation, your balance math will be wrong.
**Solution:** Rebind = new box with same name. Always return new state.
```elixir
x = 10
x = x + 5   # x is now 15, old 10 is gone. No mutation, just rebind.
balance = 100
balance = balance - 30  # 70. You must SAVE/RETURN this new value.
```

### 2. Pattern matching (most important)
**Problem:** You write `if (result != null) { data = result.data }` nested checks.
**Why problem:** Fintech has 5 failure cases per transfer. Nested ifs hide bugs.
**Solution:** Match shape directly. Crash fast if shape wrong.
```elixir
# Instead of if-else:
{:ok, balance} = {:ok, 5000}   # works, balance = 5000
# {:ok, balance} = {:error, :no_funds}  # raises MatchError -> you SEE the bug

case File.read("a.txt") do
  {:ok, text} -> IO.puts(text)
  {:error, _} -> IO.puts("file missing")
end
```

### 3. Atoms = labels, not strings
**Problem:** You use `"ok"` string for status.
**Why problem:** Strings waste memory, typos `"Ok"` vs `"ok"` pass silently.
**Solution:** Use `:ok`, `:error`, `:insufficient_funds`. Compared instantly, typo fails at compile/runtime visibly.
```elixir
{:ok, _} = {:ok, 100}
{:error, :insufficient_funds} = {:error, :insufficient_funds}
```

### 4. Pipe `|>` chains steps
**Problem:** `update(validate(parse(input)))` — hard to read inside-out.
**Why problem:** In prod you chain 6 steps (parse → validate → lock → debit → credit → log). Inside-out hides which step failed.
**Solution:** Pipe top-to-bottom.
```elixir
"  alice  "
|> String.trim()      # "alice"
|> String.upcase()    # "ALICE"
```

### 5. `with` = happy-path pipeline with early exit (USE FOR MONEY)
**Problem:** 4 validations, first failure should stop.
**Why problem:** Without `with`, you nest case-in-case 4 levels deep. Easy to forget a check → money bug.
**Solution:**
```elixir
with {:ok, amt} <- check_amount(100),
     {:ok, _} <- check_different("a", "b"),
     {:ok, _} <- check_funds(5000, 100) do
  {:ok, "transfer done"}
else
  {:error, reason} -> {:error, reason}
end
# If any <- fails, it jumps to else. No nesting.
```

### 6. Enum = loops without for-loops
**Problem:** You write `for (i=0; i<n; i++) sum += arr[i]`.
**Why problem:** Manual index → off-by-one. In Elixir you can't mutate loop var anyway.
**Solution:**
```elixir
Enum.map([1,2,3], fn x -> x * 2 end)      # [2,4,6]
Enum.reduce([1,2,3], 0, fn x, acc -> x + acc end)  # 6
Enum.filter([100, -5, 200], fn x -> x > 0 end)     # [100, 200]
```

### 7. Functions: `def` vs `defp` vs multiple clauses
**Problem:** One big `if (type == A) else if (type == B)` function.
**Why problem:** Big ifs grow to 200 lines, untestable.
**Solution:** Split by pattern. Elixir picks matching clause.
```elixir
defmodule Pay do
  def fee(amount) when amount < 1000, do: 5
  def fee(amount) when amount >= 1000, do: 10
  defp secret(), do: "only inside module"  # defp = private like Java private
end
Pay.fee(500)  # 5
```

### 8. Structs + Maps
**Problem:** You pass 7 args `transfer(a,b,amt,branch,key,time,note)`.
**Why problem:** Order mix-up sends money wrong way.
**Solution:** Group in map/struct.
```elixir
user = %{id: "alice", balance: 5000}  # map, like HashMap
%{balance: b} = user  # extract via match
```

---

## PART 2: Prod Q&A (what interviewer actually asks)

### Q1. Why not use float for money?
**Problem:** `0.1 + 0.2 = 0.30000000000000004` in float.
**Why problem:** 1 paise lost per 1000 txns = lakhs lost per year + audit fail.
**Solution:** Integer paise. 100 rupees = 10000 paise.
```elixir
# BAD: 100.10 * 100
# GOOD:
amount_paise = 100_00  # 100 rupees
balance_paise = 500_00
new_balance = balance_paise - amount_paise  # always exact
```

### Q2. What is GenServer? When to use?
**Problem:** 1000 requests update same balance at once → race, double-spend.
**Why problem:** Without serialization, read-modify-write overlaps. Balance goes negative.
**Solution:** GenServer handles one call at a time. Like `synchronized` in Java but built-in.
```elixir
GenServer.call(TransferServer, {:transfer, "a", "b", 100})
# second caller waits till first finishes. No race.
```

### Q3. What is Supervisor?
**Problem:** Worker crashes (DB timeout) → whole service down.
**Why problem:** In Java one uncaught exception can kill thread pool. In money service, downtime = branches stop.
**Solution:** Supervisor restarts only crashed child.
```elixir
# strategy :one_for_one = "if one child dies, restart only that one"
children = [{TransferServer, %{}}]
Supervisor.start_link(children, strategy: :one_for_one)
```

### Q4. What is Ecto.Multi + transaction?
**Problem:** Debit succeeds, credit fails (power cut) → money disappears.
**Why problem:** Two separate DB writes are not atomic.
**Solution:** Wrap in transaction. All-or-nothing like `@Transactional` in Spring.
```elixir
Ecto.Multi.new()
|> Ecto.Multi.insert(:entry, entry_changeset)
|> Ecto.Multi.update_all(:debit, debit_query, inc: [balance: -100])
|> Ecto.Multi.update_all(:credit, credit_query, inc: [balance: 100])
|> Repo.transaction()  # if any fails, ALL rollback
```

### Q5. What is idempotency key? Why mandatory?
**Problem:** User double-clicks Pay / network retries same request twice.
**Why problem:** Without key, 2 rows = double charge. Support tickets + refunds.
**Solution:** Client sends `Idempotency-Key: uuid`. DB has UNIQUE on it. Retry returns old result.
```
POST /transfers, Header Idempotency-Key: abc-123 → 201 created
POST again same key → 200 duplicate (no second charge)
```

### Q6. What is SELECT ... FOR UPDATE?
**Problem:** Two txns both read balance=5000, both think 4000 debit is OK, both debit → -3000.
**Why problem:** Read-then-write race even inside transaction if you don't lock.
**Solution:** Lock row on read. Second txn waits.
```sql
SELECT * FROM accounts WHERE id='alice' FOR UPDATE;
-- second txn blocks here until first commits/rollbacks
```

### Q7. BEAM vs JVM threads?
**Problem:** 10k concurrent branch agents → Java needs 10k threads = OOM.
**Why problem:** OS thread ~1MB. 10k threads = 10GB.
**Solution:** BEAM process ~2KB. 1M processes on one node possible. Crash isolated.
```
Java: 1 thread per request, shared memory, synchronized blocks.
Elixir: 1 process per request, no shared memory, message passing.
```

### Q8. What is ElectricSQL Shape?
**Problem:** Branch app polls `GET /transactions` every 2 sec → server dies, data stale.
**Why problem:** Polling wastes DB + shows old balance.
**Solution:** Shape = live filtered SELECT pushed over HTTP. Postgres WAL → Electric → device.
```
GET /v1/shape?table=ledger_entries&where=branch_id='blr'
→ snapshot + live updates, resume with offset
```

### Q9. Where do writes go in local-first?
**Problem:** Device writes directly to DB while offline → two devices create conflicting money.
**Why problem:** No validation, split-brain balances.
**Solution:** Writes ONLY via Phoenix API. Electric syncs DOWN. Device queues UP via outbox + idempotency key.
```
Device SQLite (cache) → POST Phoenix (validate) → Postgres (truth) → Electric (sync out)
```

### Q10. Offline conflict: two branches overspend, who wins?
**Problem:** Alice has 10k. A (offline) sends 8k, B (offline) sends 7k. Total 15k.
**Why problem:** If you auto-merge (last-write-wins), you create money from thin air.
**Solution:** First to reach server wins. Second gets 422. No auto-merge for money.
```elixir
# server revalidates each queued intent inside FOR UPDATE tx
# A posts 8k → balance 2k. B tries 7k → rejected. B's app shows FAILED.
```

### Q11. How do you debug slow transfer (p99 2 sec)?
**Problem:** Random slowness, branches complain.
**Why problem:** Could be missing index, lock wait, or HTTP inside tx.
**Solution checklist:**
1. `EXPLAIN ANALYZE` query → seq scan? add index.
2. `SELECT * FROM pg_locks WHERE NOT granted` → who blocks?
3. Telemetry: measure tx duration, alert if >500ms.
4. Never call SMS/HTTP inside `Repo.transaction`.

### Q12. What is Telemetry / observability in 1 min?
**Problem:** Prod fails at 2am, no logs = blind.
**Why problem:** Can't fix what you can't see.
**Solution:** Every transfer emits event with duration + status. Dashboard p50/p99 + error rate.
```elixir
:telemetry.execute([:fintech, :transfer], %{duration_ms: 120}, %{status: :ok})
```

---

## PART 3: Glossary (one-line revision)

- **MatchError** = pattern didn't match. Good — bug surfaced early.
- **Pipe** = chain. **With** = chain with early exit.
- **GenServer.call** = wait for reply (like sync). **cast** = fire-forget (like async).
- **Ecto changeset** = validator before DB (like Bean Validation).
- **Migration** = versioned DB schema change.
- **Idempotency** = same request twice = same result once.
- **Double-entry** = every debit has equal credit. Sum always conserved.
- **Append-only** = never UPDATE/DELETE money rows. Corrections = new reversing row.
- **WAL** = Postgres diary Electric reads to sync.
- **Offset/handle** = bookmark so device resumes sync without re-downloading all.

## What to run now
```bash
brew install elixir
elixir elixir-fintech-crash-course/01-elixir-prod-basics/ledger_core.exs
elixir electric-offline-deepdive/05-conflicts/conflict_sim.exs
```
If both print `money conserved`, you got the core idea.
