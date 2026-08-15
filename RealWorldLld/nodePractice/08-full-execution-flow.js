// ============================================================
// MASTER EXAMPLE: call stack + callback + Promise + setTimeout +
// setImmediate + setInterval, all in one trace
// ============================================================
// Rule to hold onto the whole way through:
//   1. JS reads and RUNS code top-to-bottom, but "running" a line only
//      means something if it's SYNCHRONOUS. Async API calls (setTimeout
//      etc.) just register-and-return instantly - the callback runs later.
//   2. The call stack must be COMPLETELY EMPTY before the event loop
//      pulls anything off any queue.
//   3. When the stack empties: drain ALL microtasks (Promise/nextTick)
//      first, THEN take ONE macrotask (timer/immediate/interval tick)
//      from the appropriate queue, run it, then drain microtasks again,
//      then take the next macrotask. Repeat.

console.log("A: line 1 - synchronous, runs immediately on the call stack");

setTimeout(() => {
  console.log("F: setTimeout callback - macrotask, TIMERS phase");
}, 0);
// ^ This CALL (registering the timer) is synchronous and instant.
//   The arrow function does NOT run here - libuv holds it until >=0ms
//   pass AND the timers phase is reached.

setImmediate(() => {
  console.log("E: setImmediate callback - macrotask, CHECK phase");
});
// ^ Same idea - registration is instant, callback deferred to CHECK phase.

Promise.resolve().then(() => {
  console.log("C: Promise.then callback - MICROTASK, drained before any macrotask");
});
// ^ Promise.resolve() itself resolves synchronously right now, but the
//   .then callback is STILL pushed to the microtask queue, not run inline.

function greet(name) {
  console.log(`B: greet("${name}") - a normal function call, pushed onto`, "the call stack, runs fully, then pops off");
}
greet("interview"); // fully synchronous - runs to completion before line below

let ticks = 0;
const intervalId = setInterval(() => {
  ticks++;
  console.log(`G${ticks}: setInterval tick #${ticks} - macrotask, TIMERS phase, repeats`);
  if (ticks === 2) {
    clearInterval(intervalId); // MUST clear or this fires forever
    console.log("H: cleared the interval after 2 ticks");
  }
}, 20);
// ^ Registration instant. Each tick's callback is a SEPARATE macrotask,
//   re-queued by libuv every ~20ms until clearInterval stops it.

console.log("D: last line of synchronous code - the call stack is about to go empty");

/*
Run: node 08-full-execution-flow.js

Actual output (verified - E and F swap between runs, everything else is stable):
A: line 1 - synchronous, runs immediately on the call stack
B: greet("interview") - a normal function call, pushed onto the call stack, runs fully, then pops off
D: last line of synchronous code - the call stack is about to go empty
C: Promise.then callback - MICROTASK, drained before any macrotask
F: setTimeout callback - macrotask, TIMERS phase
E: setImmediate callback - macrotask, CHECK phase
G1: setInterval tick #1 - macrotask, TIMERS phase, repeats
G2: setInterval tick #2 - macrotask, TIMERS phase, repeats
H: cleared the interval after 2 ticks

STEP-BY-STEP TRACE OF WHAT'S HAPPENING:

1. A, B, D print FIRST, in source order, with NOTHING in between - because
   every line up to setInterval() is either plain sync code (console.log,
   greet()) or an async API call that registers instantly and returns
   (setTimeout/setImmediate/Promise.resolve/setInterval registration
   itself is sync too - only their CALLBACKS are deferred). The call
   stack processes them one at a time, top to bottom, with zero
   interruption - the event loop cannot inject ANYTHING while the stack
   is non-empty.

2. Once the last line (D) finishes, the call stack is finally empty. NOW
   the event loop wakes up. First it fully drains the MICROTASK queue:
   only one microtask is queued (C, the Promise.then) - it runs.

3. Microtask queue is empty. Event loop starts its phase cycle: timers
   phase comes before check phase, and on this run the process had
   already crossed into (or past) the timers phase by the time its 0ms
   timer was ready, so F (setTimeout) printed before E (setImmediate).
   This E-vs-F ordering is NOT guaranteed at the top level of a script -
   it depends on how long startup took relative to the 0ms delay - it's
   only GUARANTEED inside an I/O callback (see 04-event-loop.js).

4. Check phase: E (setImmediate) runs.

5. Timers phase also handles setInterval - but only when 20ms has
   actually elapsed (longer than the 0ms setTimeout), so it runs LAST
   despite being registered before D printed. This proves: REGISTRATION
   ORDER in your source code does NOT determine execution order -
   DELAY/READINESS does.

6. G1 fires at ~20ms, G2 fires at ~40ms (each a fresh macrotask, with a
   full microtask-drain in between, though there's nothing queued this
   time), and inside G2's callback clearInterval() stops future ticks, so
   H prints immediately after, still inside that same macrotask.

ONE-LINE SUMMARY: sync code always runs first, completely, uninterrupted.
Then microtasks (Promise/process.nextTick) drain completely. Then ONE
macrotask (timer/immediate/interval-tick) runs, then microtasks drain
again, then the next macrotask - forever, in a loop. Which macrotask runs
next depends on which one's condition (delay elapsed, I/O done) is
satisfied, NOT on the order you wrote them in the source.
*/
