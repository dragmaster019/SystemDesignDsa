// ============================================================
// How JavaScript actually executes: Call Stack + Event Loop
// ============================================================
// CORRECTING THE MISCONCEPTION: a function does NOT "go to the event
// loop" when you DEFINE it. `function foo(){}` just stores the function
// in memory - nothing runs, nothing queues, nothing happens yet.
// A function only becomes "active" when you CALL/INVOKE it: foo().
//
// From there, two completely different paths depending on WHAT you call:
//
//  A) Call a NORMAL function (console.log(...), stepOne(), add(2,3)...)
//     -> pushed onto the CALL STACK, runs top-to-bottom RIGHT NOW,
//        popped off when it returns. Fully synchronous. Blocks the
//        single JS thread until it's done - nothing else can run
//        (including the event loop) while it's on the stack.
//
//  B) Call an ASYNC API (setTimeout, setInterval, setImmediate,
//     fs.readFile, a network request...)
//     -> the CALL ITSELF is synchronous and returns almost instantly -
//        it just hands a registration off to libuv (Node's C++ engine
//        underneath: "run this callback after 0ms" / "run this after
//        this file is read"). The CALLBACK you passed does NOT run at
//        this point. It sits with libuv until its condition is met, then
//        libuv drops the callback into a QUEUE for the event loop to
//        pick up later.
//
// THE EVENT LOOP's entire job, forever, is:
//   "Is the call stack empty? If yes: take the next ready callback off a
//    queue, push IT onto the call stack, run it to completion, repeat."
// That's it. That's the whole event loop.

function stepOne() {
  console.log("1) stepOne runs on the call stack");
}

function stepTwo() {
  console.log("2) stepTwo runs on the call stack");
}

function scheduleStuff() {
  // This setTimeout CALL is synchronous and returns instantly - it just
  // registers a timer with libuv. The arrow function inside does NOT run now.
  setTimeout(() => {
    console.log("5) setTimeout callback - pulled from the TIMERS queue, only once the call stack was empty");
  }, 0);

  // Same idea - setImmediate call is instant, registers with libuv's "check" queue.
  setImmediate(() => {
    console.log("4) setImmediate callback - pulled from the CHECK queue");
  });

  // Promise.resolve() itself runs synchronously right here (no timer
  // involved) - but .then's callback is STILL deferred to the microtask
  // queue, never run inline.
  Promise.resolve().then(() => {
    console.log("3) Promise .then callback - pulled from the MICROTASK queue (always drained before timers/immediate)");
  });

  console.log("2.5) scheduleStuff's own synchronous code - finishes BEFORE any callback above runs");
}

console.log("--- start ---");
stepOne(); // pushed onto stack, runs, popped off - done before next line
stepTwo(); // same
scheduleStuff(); // pushed, runs (registers 3 callbacks + logs), popped off
console.log("--- end of synchronous code ---");
// ONLY NOW, with the call stack finally empty, does the event loop start
// pulling queued callbacks - microtasks first, then macrotasks.

/*
Run: node practice.js

Actual output (verified; 4 and 5 can swap between runs - see 04-event-loop.js
for why that pair specifically isn't order-guaranteed at the top level):
--- start ---
1) stepOne runs on the call stack
2) stepTwo runs on the call stack
2.5) scheduleStuff's own synchronous code - finishes BEFORE any callback above runs
--- end of synchronous code ---
3) Promise .then callback - pulled from the MICROTASK queue (always drained before timers/immediate)
4) setImmediate callback - pulled from the CHECK queue
5) setTimeout callback - pulled from the TIMERS queue, only once the call stack was empty

Interview soundbite: "Defining a function does nothing - it's just stored
in memory. Calling a normal function pushes it onto the call stack and it
runs immediately, blocking the single JS thread until it returns. Calling
an async API like setTimeout is ITSELF synchronous and returns instantly -
it just registers the real work with libuv. Only the callback gets queued,
not the setTimeout call. The event loop's only job is: once the call stack
is empty, pull the next ready callback off a queue and run it - microtask
queues (Promise, process.nextTick) get fully drained before it even looks
at timer/immediate/I/O queues, which is why a Promise .then always beats a
setTimeout(fn, 0), regardless of which was written first in the source."
*/
