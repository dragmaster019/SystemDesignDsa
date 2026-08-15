// ============================================================
// Promises
// ============================================================
// A Promise represents the eventual result of an async operation.
// It's always in one of 3 states: pending -> fulfilled, or pending -> rejected.
// Once settled (fulfilled/rejected), it NEVER changes state again.

// 1) Creating and consuming a basic Promise
function delay(ms, value, shouldFail = false) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (shouldFail) reject(new Error(`failed after ${ms}ms`));
      else resolve(value);
    }, ms);
  });
}

delay(10, "hello").then((v) => console.log("1) resolved:", v));

delay(10, null, true).catch((e) => console.log("2) rejected:", e.message));

// 2) CHAINING - each .then returns a NEW promise, letting you compose
// transformations sequentially instead of nesting (this is what fixes
// callback hell)
delay(10, 5)
  .then((n) => n * 2) // 10
  .then((n) => n + 1) // 11
  .then((n) => console.log("3) chained result:", n));

// 3) Promise.all - run in PARALLEL, waits for all, fails fast if any reject
Promise.all([delay(10, "a"), delay(20, "b"), delay(5, "c")]).then((results) =>
  console.log("4) Promise.all:", results) // order matches input array, NOT completion order
);

// 4) Promise.allSettled - like all(), but never short-circuits; gives you
// the outcome of EVERY promise, success or failure
Promise.allSettled([delay(5, "ok"), delay(5, null, true)]).then((results) =>
  console.log("5) allSettled:", results.map((r) => r.status))
);

// 5) Promise.race - settles as soon as the FIRST promise settles (win or lose)
Promise.race([delay(50, "slow"), delay(5, "fast")]).then((winner) =>
  console.log("6) race winner:", winner)
);

/*
Run: node 05-promises.js

Actual output (verified - order reflects each delay's timing, NOT source order):
5) allSettled: [ 'fulfilled', 'rejected' ]   <- both its delays (5ms) finish first
6) race winner: fast                          <- race's faster delay is 5ms
1) resolved: hello                            <- 10ms
2) rejected: failed after 10ms                <- 10ms
3) chained result: 11                         <- 10ms + 2 microtask hops after
4) Promise.all: [ 'a', 'b', 'c' ]             <- waits for slowest input, 20ms

Interview soundbite: "A Promise has 3 states - pending, fulfilled, rejected
- and is immutable once settled. Promise.all runs things in parallel and
rejects as soon as ANY one rejects (fail-fast); allSettled waits for every
one regardless of outcome and gives you status per item; race settles with
whichever promise finishes first, success or failure. The key thing .then
chaining gives you over nested callbacks is that each .then returns a new
promise, so you get flat, sequential-looking composition instead of a
pyramid."
*/
