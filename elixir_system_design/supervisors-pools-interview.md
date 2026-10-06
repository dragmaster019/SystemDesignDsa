# BEAM Processes, Supervisors & Pools — Interview Doc

## 1. The 30-second pitch (say this first)

> "BEAM processes are ~2KB isolated actors, not OS threads. Data is immutable, so sharing never corrupts. A finished process dies and is GC'd. Crashes are expected — supervisors restart workers, pools bound how many run. That's how one node holds millions of concurrent requests."

## 2. Process lifecycle

- Spawn → run function → return → die → memory reclaimed automatically.
- No manual cleanup. A dead process holds nothing.

```elixir
Task.async(fn -> 1 + 1 end) |> Task.await()  # 2, worker died after
```

- Java thread ≈ 1MB + OS scheduling (≈10k max). BEAM process ≈ 2KB, scheduled by the VM (millions fine).

## 3. Why immutability is the biggest advantage

- `%{book | available: false}` creates a NEW copy; the old one never changes.
- Two processes holding the same book can never corrupt each other — no `synchronized`, no locks, no half-written state.
- "Update" = make a copy and file it (`Map.put`). Old readers keep seeing the clean original.

## 4. Supervisor — "don't stay dead"

Restarts crashed children. Crash is a normal control-flow event on the BEAM.

```elixir
children = [LibrarySystem.Data]
Supervisor.start_link(children, strategy: :one_for_one)
```

| Strategy | Meaning | Use when |
|---|---|---|
| `:one_for_one` | restart only the dead child | children independent (default) |
| `:one_for_all` | restart all children | children depend on each other |
| `:rest_for_one` | restart dead + children started after it | ordered startup chain |

Interview line: "Let it crash, supervisor revives it. I put restart logic in the tree, not try/catch everywhere."

## 5. Pool — "don't spawn unlimited"

A pool caps concurrency so the node never goes "full."

```elixir
Task.Supervisor.start_link(name: MyPool, max_children: 100)
```

- 101st task waits instead of spawning → memory safe.
- Alternatives: `GenStage`/`Flow` backpressure, `GenServer` mailbox with `:max_demand`, DB connection pool (`pool_size = 2 * cores`).

## 6. What if the system gets full?

1. You spawned unbounded processes in a loop → bound with pool/max_children.
2. Mailbox grows (consumer slower than producer) → add backpressure, monitor `:observer`, shed load.
3. OOM crash → supervisor restarts critical workers; fix the leak (usually an ever-growing list/state, or atoms created from user input).

## 7. Interview Q&A (memorize)

**Q: BEAM process vs Java thread?**
A: "Process is ~2KB, isolated heap, no shared memory, dies cleanly. Thread is ~1MB, shares heap, needs locks. That's why 10k concurrent users OOMs threads but not processes."

**Q: Where do BEAM processes share state?**
A: "They don't — they message-pass immutable data. Shared mutable state lives in one supervised process (Agent/GenServer/ETS) that serializes access."

**Q: What happens when a process finishes?**
A: "It exits with its return value; the VM garbage-collects it. The caller (or supervisor) decides what to do with the result."

**Q: Supervisor vs try/catch?**
A: "try/catch handles an expected error inline. Supervisor handles the unexpected by restarting to a known-good state. Use both: validate input with tuples, supervise workers."

**Q: How do you stop unbounded spawn?**
A: "Task.Supervisor with max_children, queues with backpressure, separate pools per workload so one spike can't starve everything."

**Q: one_for_one vs one_for_all?**
A: "one_for_one for independent workers; one_for_all when children hold references to each other and a partial restart leaves inconsistency."
