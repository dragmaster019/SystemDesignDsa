# Java Practice — Production Patterns

Complements [`../04-JAVA-INTERVIEW-QA.md`](../04-JAVA-INTERVIEW-QA.md) (quick definitional Q&A)
with **runnable, production-style code** you can compile and pick apart, plus a deep dive on how
`HashMap` actually works internally — one of the most-asked "explain the internals" questions.

## Structure

| Folder | What it covers |
|---|---|
| [`exceptionhandling/`](exceptionhandling/) | Custom exception hierarchy, try-with-resources, exception chaining, retry-on-transient-failure — modeled as a mini order service |
| [`multithreading/`](multithreading/) | `ExecutorService` + proper shutdown, race conditions (broken vs. fixed), producer-consumer with `BlockingQueue`, `CompletableFuture` composition |
| [`collections-hashmap/`](collections-hashmap/) | How `HashMap` really works bucket-by-bucket, plus a from-scratch implementation |

## How to use each folder

1. Read `questions.md` first and answer out loud before looking at the code.
2. Compile and run the `.java` file(s) (`javac X.java && java X`) to see the behavior for real.
3. Read the source for the production patterns baked in: no swallowed exceptions, no thread-pool
   leaks, no silent data races.

Every example is single-file and self-contained (no external dependencies) so it runs with a plain
JDK — no build tool required.
