// ============================================================
// Callbacks + Callback Hell
// ============================================================
// A callback is just a function passed as an argument to be invoked
// later - either synchronously, or (more commonly in Node) after some
// async operation completes.

const fs = require("fs");

// 1) Basic async callback (Node's classic "error-first" callback style)
// Signature convention: callback(err, result) - ALWAYS check err first.
fs.readFile(__filename, "utf8", (err, data) => {
  if (err) {
    console.error("Read failed:", err.message);
    return;
  }
  console.log("File size (basic callback):", data.length, "chars");
});

// 2) CALLBACK HELL - nesting async operations inside each other
// Each step depends on the previous one's result, so they nest deeper
// and deeper -> the "pyramid of doom". Hard to read, hard to handle
// errors (must check `err` at every single level), hard to reuse.
function callbackHellDemo() {
  fs.readFile(__filename, "utf8", (err1, data1) => {
    if (err1) return console.error(err1);
    fs.stat(__filename, (err2, stats) => {
      if (err2) return console.error(err2);
      fs.readdir(__dirname, (err3, files) => {
        if (err3) return console.error(err3);
        console.log("--- callback hell result ---");
        console.log("chars:", data1.length, "size:", stats.size, "filesInDir:", files.length);
        // imagine 3 more levels of nesting for a real workflow...
        // this is what "callback hell" / "pyramid of doom" looks like
      });
    });
  });
}
callbackHellDemo();

// 3) THE FIX: promisify + async/await flattens the pyramid (see 06/08)
const fsPromises = require("fs/promises");
async function flattenedVersion() {
  const data1 = await fsPromises.readFile(__filename, "utf8");
  const stats = await fsPromises.stat(__filename);
  const files = await fsPromises.readdir(__dirname);
  console.log("--- flattened (async/await) result ---");
  console.log("chars:", data1.length, "size:", stats.size, "filesInDir:", files.length);
}
flattenedVersion();

/*
Run: node 03-callbacks-and-hell.js

Actual output (verified, exact numbers vary by file/dir contents, ORDER varies too - see note below):
File size (basic callback): <N> chars
--- callback hell result ---
chars: <N> size: <N> filesInDir: <N>
--- flattened (async/await) result ---
chars: <N> size: <N> filesInDir: <N>

Interview soundbite: "Callback hell happens when you nest async callbacks
inside each other because each step depends on the last one's result - it
gets hard to read and you have to duplicate error-checking at every level.
The fix is Promises + async/await, which flattens the nesting into
sequential-looking code while staying non-blocking. Node's convention is
'error-first callbacks' - callback(err, result) - so you always check err
before using the result."

NOTE on ordering: these three async calls all kick off near-simultaneously,
so which one's console.log fires FIRST depends on which I/O completes first
- that's worth mentioning if asked "what's the exact output order" - the
answer is "it's not guaranteed" unless you explicitly chain/await them.
*/
