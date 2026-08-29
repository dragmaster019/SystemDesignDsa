# Multithreading — Production-Focused Questions

1. **Thread vs Runnable vs Callable — when do you reach for `Callable`?**
   When the task needs to *return a value* or throw a checked exception. `Runnable.run()` can't
   do either. `Thread` vs `Runnable` for task definition: prefer `Runnable`/`Callable` — extending
   `Thread` burns your one `extends` slot and conflates "what to run" with "how it runs."

2. **Why use an `ExecutorService`/thread pool instead of `new Thread()` per task?**
   Creating an OS thread is expensive (allocation, kernel scheduling overhead), and an unbounded
   number of them exhausts memory/CPU. A pool caps concurrency and reuses threads across tasks.
   See [`ExecutorServiceDemo.java`](ExecutorServiceDemo.java).

3. **How do you correctly shut down an `ExecutorService`?**
   `shutdown()` (stop accepting new tasks, let queued ones finish) → `awaitTermination(timeout)` →
   `shutdownNow()` as a forceful fallback if it doesn't finish in time. Forgetting this in a
   long-lived app is a classic thread leak — the pool's threads are non-daemon by default and will
   keep the JVM alive.

4. **What's a race condition? Show it, then fix it two ways.**
   `count++` is read-modify-write — three steps that can interleave across threads, losing
   updates. See [`RaceConditionDemo.java`](RaceConditionDemo.java) for the broken version plus a
   `synchronized` fix and a lock-free `AtomicInteger` fix.

5. **`synchronized` vs `ReentrantLock` — what does the Lock API add?**
   `tryLock(timeout)` (don't block forever waiting for contention), interruptible acquisition,
   a fairness policy, and support for multiple `Condition` objects per lock (vs. one implicit
   wait-set with `synchronized`).

6. **What's a deadlock, and how do you prevent it?**
   Two threads each hold a lock the other needs, and both wait forever. Prevent by always
   acquiring locks in the same global order across the codebase, or using `tryLock` with a
   timeout so a thread can back off instead of waiting indefinitely.

7. **What does `volatile` guarantee — and NOT guarantee?**
   Visibility (a write from one thread is immediately visible to others; the JVM/CPU won't cache
   a stale value), but **not atomicity**. `volatile int count; count++;` is still a race — the
   increment is still three separate steps.

8. **How does `ConcurrentHashMap` achieve thread safety without locking the whole map?**
   Java 8+: CAS operations on individual bins, with `synchronized` used only on the specific bin
   being modified during a collision — not a single map-wide lock. Contrast with the old
   `Hashtable`, which synchronizes every method on one lock, serializing all access regardless of
   which keys are touched.

9. **Producer-consumer — why use `BlockingQueue` instead of hand-rolled `wait()`/`notify()`?**
   `BlockingQueue` correctly handles the wait/notify plumbing and capacity bookkeeping (blocking
   `put()` when full, blocking `take()` when empty) — rolling your own is easy to get subtly
   wrong (missed signals, spurious wakeups). See
   [`ProducerConsumerDemo.java`](ProducerConsumerDemo.java).

10. **How do you compose async calls with `CompletableFuture` without blocking, and handle errors in the chain?**
    `thenApply` (transform the result), `thenCompose` (flatten a chained async call instead of
    nesting futures), `thenCombine` (join two independent futures). For errors: `exceptionally`
    (recover with a fallback value) or `handle` (see both the success and failure case). See
    [`CompletableFutureDemo.java`](CompletableFutureDemo.java).

11. **What's `ThreadLocal` for, and why is it a common memory-leak source in pooled apps?**
    Per-thread state without passing it explicitly through every call. The leak: pooled threads
    outlive any single task, so if you don't call `remove()` after use, stale data lingers
    attached to a thread that gets reused for unrelated work later — in app-server contexts this
    has caused classloader leaks across redeploys.

12. **Real life: how does a server like Tomcat use threads per request, and why is blocking I/O on that pool a scalability problem?**
    Thread-per-request: each incoming request occupies one pooled thread until the response is
    fully written. A pool of, say, 200 threads means at most 200 concurrent in-flight requests. A
    slow blocking call (a slow DB query, a slow downstream HTTP call) holds that thread the whole
    time, so under load the pool starves and new requests queue up even though the CPU is mostly
    idle waiting on I/O. This is the motivation behind async/reactive stacks (Netty, WebFlux) that
    free the thread while I/O is in flight instead of blocking it.
