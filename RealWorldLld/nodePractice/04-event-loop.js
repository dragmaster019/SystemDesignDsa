// ============================================================
// The Event Loop (Node.js specifics)
// ============================================================
// Node is single-threaded for JS execution but non-blocking for I/O.
// It uses libuv under the hood, which runs I/O on a thread pool and
// notifies the main thread via the event loop when work is done.
//
// Event loop PHASES (simplified, in order, repeating each "tick"):
//   1. timers        - setTimeout / setInterval callbacks
//   2. pending callbacks - some system-level callbacks (rare to touch directly)
//   3. poll           - fetch new I/O events, execute I/O callbacks
//   4. check          - setImmediate callbacks
//   5. close callbacks - e.g. socket.on('close', ...)
//
// Between EVERY phase transition (and after every callback), Node drains
// two microtask queues, HIGHEST priority first:
//   1. process.nextTick queue  (Node-specific, runs before Promises)
//   2. Promise microtask queue (.then/.catch/.finally, async/await continuations)
//
// So the priority order is roughly:
//   synchronous code > process.nextTick > Promise microtasks > timers/setImmediate/I/O (macrotasks)

console.log("1: sync start");

setTimeout(() => console.log("2: setTimeout (macrotask)"), 0);

setImmediate(() => console.log("3: setImmediate (macrotask, check phase)"));

Promise.resolve().then(() => console.log("4: Promise.then (microtask)"));

process.nextTick(() => console.log("5: process.nextTick (highest-priority microtask)"));

console.log("6: sync end");

/*
Run: node 04-event-loop.js

Actual output (verified - note 2 and 3 can swap between runs, see below):
1: sync start
6: sync end
5: process.nextTick (highest-priority microtask)
4: Promise.then (microtask)
3: setImmediate (macrotask, check phase)
2: setTimeout (macrotask)

Why this order:
- All synchronous code runs first, top to bottom -> "1" then "6"
  (note "6" prints before any of 2/3/4/5, even though they were scheduled
  earlier in the file - because none of them are synchronous).
- After the synchronous script finishes, Node drains microtasks:
  process.nextTick queue FIRST (Node-specific priority), then Promise
  microtasks -> "5" then "4".
- Only then does the event loop move into its phases: on this run "3"
  (setImmediate) printed before "2" (setTimeout) - but that's NOT
  guaranteed at the top level of a script, it depends on how long process
  startup took relative to the 0ms timer threshold, so you may see it flip
  between runs. INSIDE an I/O callback (e.g. an fs.readFile callback),
  though, setImmediate is always guaranteed to run before a
  setTimeout(fn, 0) - that guarantee-vs-no-guarantee distinction is a
  classic follow-up interview question.

Interview soundbite: "Node's event loop has phases - timers, poll, check,
close - and between every phase it drains two microtask queues:
process.nextTick first, then Promise callbacks. So synchronous code always
wins, then nextTick, then Promises, then timers/setImmediate/I/O. The one
sharp edge is setTimeout(fn,0) vs setImmediate at the top level isn't
ordering-guaranteed, but inside an I/O callback setImmediate always fires
first."
*/
