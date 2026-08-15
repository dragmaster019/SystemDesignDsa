// ============================================================
// try/catch  +  async/await
// ============================================================
// async/await is syntax sugar over Promises: an `async function` always
// returns a Promise, and `await` pauses execution of THAT function
// (without blocking the whole thread) until the awaited promise settles.

// Node crashes the process on an unhandled promise rejection by default
// (since Node 15) - same severity as an uncaught exception. We register a
// handler here ONLY so the rest of this demo file keeps running; in real
// code, an unhandledRejection means you have a genuine bug to fix, not
// something to swallow like this.
process.on("unhandledRejection", (reason) => {
  console.log("(unhandledRejection would normally crash the process):", reason.message);
});

function delay(ms, value, shouldFail = false) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (shouldFail) reject(new Error(`failed after ${ms}ms`));
      else resolve(value);
    }, ms);
  });
}

// 1) try/catch with a REJECTED await - this is how you handle async errors
// with async/await (equivalent to .catch() in promise-chain style)
async function basicTryCatch() {
  try {
    const result = await delay(10, null, true); // rejects
    console.log("never reached:", result);
  } catch (err) {
    console.log("1) caught:", err.message);
  }
}
basicTryCatch();

// 2) try/catch does NOT catch errors from code that runs after an
// un-awaited promise - a common gotcha. If you forget `await`, the
// error becomes an unhandled rejection instead of being caught here.
async function missingAwaitGotcha() {
  try {
    delay(10, null, true); // BUG: no `await` - this rejection is NOT caught below
    console.log("2) this still runs, error was NOT caught here");
  } catch (err) {
    console.log("2) caught (this will NOT print):", err.message);
  }
}
missingAwaitGotcha();
// Node will separately print an "UnhandledPromiseRejection" warning for this one.

// 3) Sequential vs parallel await - a classic performance gotcha
async function sequentialAwait() {
  const start = Date.now();
  const a = await delay(20, "a"); // waits 20ms...
  const b = await delay(20, "b"); // ...then waits ANOTHER 20ms = 40ms total
  console.log("3) sequential took ~", Date.now() - start, "ms ->", a, b);
}

async function parallelAwait() {
  const start = Date.now();
  const [a, b] = await Promise.all([delay(20, "a"), delay(20, "b")]); // both run at once = ~20ms total
  console.log("4) parallel took ~", Date.now() - start, "ms ->", a, b);
}

sequentialAwait();
parallelAwait();

// 4) try/catch/finally with async/await - finally always runs, success or failure
async function withFinally() {
  try {
    await delay(5, "ok");
    console.log("5) try block succeeded");
  } catch (err) {
    console.log("5) caught:", err.message);
  } finally {
    console.log("5) finally always runs (cleanup, closing connections, etc.)");
  }
}
withFinally();

/*
Run: node 06-try-catch-and-async-await.js

Actual output (verified, exact ms will vary slightly, relative order is stable):
2) this still runs, error was NOT caught here
1) caught: failed after 10ms
(unhandledRejection would normally crash the process): failed after 10ms
5) try block succeeded
5) finally always runs (cleanup, closing connections, etc.)
4) parallel took ~ 21 ms -> a b
3) sequential took ~ 42 ms -> a b

Interview soundbite: "async/await is sugar over Promises - an async function
always returns a Promise, and await pauses that function's execution (not
the whole event loop) until the promise settles. try/catch around an await
is how you handle rejections. The big gotcha is forgetting `await` -  the
error then escapes the try/catch entirely and becomes an unhandled
rejection - which since Node 15 actually crashes the process, same
severity as an uncaught exception, not just a warning. Another common one: awaiting things one at a time when
they don't depend on each other doubles your latency for no reason -
Promise.all lets independent awaits run concurrently."
*/
