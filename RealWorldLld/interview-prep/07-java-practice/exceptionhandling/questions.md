# Exception Handling — Production-Focused Questions

These go a level deeper than "checked vs unchecked" — they're about how real services are
structured, not just syntax. See [`OrderServiceExceptionDemo.java`](OrderServiceExceptionDemo.java)
for all of these in action.

1. **Checked vs unchecked — which should your service layer throw, and why?**
   Most real backends throw unchecked (`RuntimeException`) domain exceptions from the service
   layer, caught once at a boundary (e.g. a global `@ControllerAdvice` in Spring). Checked
   exceptions make sense for genuinely recoverable, caller-must-handle-it conditions at a low
   level (I/O, file access) — forcing every layer above to declare `throws` just to bubble a
   business error up to an HTTP handler is boilerplate with no benefit.

2. **How do you design a custom exception hierarchy for a service?**
   One base (`ServiceException`) carrying a stable error code, with specific subtypes
   (`OrderNotFoundException`, `InsufficientInventoryException`, ...). The error code — not the
   exception's class name or message — is what a caller/API contract should key off of, since
   messages can change without notice.

3. **What's wrong with `catch (Exception e)` everywhere?**
   It swallows bugs indiscriminately — a `NullPointerException` from a real defect gets treated
   the same as an expected business-rule violation. It also breaks fail-fast: programming errors
   should crash loudly in dev/test, not get silently absorbed into a generic error path.

4. **Why does exception chaining (passing the original as `cause`) matter?**
   Without it, wrapping an exception destroys the original stack trace — you see "inventory
   service unavailable" in your logs with no way to tell it was actually a socket timeout three
   layers down. Always pass the original exception as `cause`.

5. **try-with-resources vs. try/finally — why prefer it?**
   Guaranteed close in reverse (LIFO) order, correct handling of "both the try block and close()
   threw" (the close()'s exception becomes a **suppressed** exception attached to the original,
   not a separate one that silently discards the first), and less boilerplate.

6. **What happens if the try block throws AND `close()` also throws?**
   The try block's exception propagates; `close()`'s exception is attached to it via
   `addSuppressed()` and retrievable with `getSuppressed()`. Nothing is silently lost.

7. **Should you use exceptions for control flow?**
   No — filling in a stack trace is comparatively expensive, and code that uses exceptions to
   signal expected, common outcomes (e.g. "not found" on every lookup) is harder to read than a
   plain `Optional`/return-value check.

8. **`Throwable` vs `Error` vs `Exception` — would you ever catch `Error`?**
   Almost never. `Error` (`OutOfMemoryError`, `StackOverflowError`) signals the JVM itself is in
   trouble — catching and "handling" it usually just delays an inevitable crash in a more
   confusing way.

9. **How do you implement retry for a transient failure (e.g. a flaky network call)?**
   Bounded retry count, and on final failure wrap the *last* underlying exception as `cause`
   rather than throwing a bare "it failed" — see `checkStockWithRetry` in the demo.

10. **Why is logging AND rethrowing at every layer a problem?**
    It produces the same failure logged N times (once per layer) as the exception bubbles up,
    making logs noisy and misleading about how many times something actually happened. Log once,
    at the boundary that terminates the exception (or that has enough context to act on it).
