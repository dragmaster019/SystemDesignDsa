// ============================================================
// Streams
// ============================================================
// Streams process data in CHUNKS instead of loading it all into memory
// at once. This is Node's answer to handling large files/network data
// efficiently. 4 types: Readable, Writable, Duplex (both), Transform
// (duplex that modifies data as it passes through, e.g. gzip).

const fs = require("fs");
const { Transform } = require("stream");
const path = require("path");

const inputFile = __filename; // read this very file as the demo source
const outputFile = path.join(__dirname, "07-streams-output.txt");

// 1) WITHOUT streams: fs.readFile loads the ENTIRE file into memory at once
// - fine for small files, dangerous for large ones (e.g. a 10GB log file
// would try to allocate 10GB of RAM).
fs.readFile(inputFile, "utf8", (err, data) => {
  if (err) throw err;
  console.log("1) fs.readFile loaded whole file:", data.length, "chars in one go");
});

// 2) WITH a Readable stream: data arrives in CHUNKS via the 'data' event.
// Memory usage stays flat regardless of file size, because you only ever
// hold one chunk (a Buffer, default ~64KB) at a time.
let chunkCount = 0;
let totalBytes = 0;
const readStream = fs.createReadStream(inputFile, { highWaterMark: 256 }); // small chunk size just to force multiple chunks for this demo

readStream.on("data", (chunk) => {
  chunkCount++;
  totalBytes += chunk.length;
});

readStream.on("end", () => {
  console.log(`2) Readable stream: got ${chunkCount} chunks, ${totalBytes} bytes total`);
});

readStream.on("error", (err) => console.error("stream error:", err));

// 3) Writable stream - write output progressively instead of building one
// giant string/buffer in memory and calling fs.writeFile once.
const writeStream = fs.createWriteStream(outputFile);
writeStream.write("line 1\n");
writeStream.write("line 2\n");
writeStream.end("line 3 (final)\n"); // .end() signals no more data is coming
writeStream.on("finish", () => {
  console.log("3) Writable stream finished writing", outputFile);
  fs.unlinkSync(outputFile); // cleanup demo file
});

// 4) pipe() - connects a Readable directly to a Writable, handling
// backpressure automatically (pauses the source if the destination can't
// keep up). This is the idiomatic way to move data between streams.
// 5) Transform stream - a duplex stream that modifies data as it passes
// through (e.g. uppercase-ing every chunk).
const upperCaseTransform = new Transform({
  transform(chunk, encoding, callback) {
    callback(null, chunk.toString().toUpperCase());
  },
});

const pipeOutput = path.join(__dirname, "07-pipe-output.txt");
fs.createReadStream(inputFile)
  .pipe(upperCaseTransform)
  .pipe(fs.createWriteStream(pipeOutput))
  .on("finish", () => {
    console.log("4/5) pipe() through Transform stream complete ->", pipeOutput);
    fs.unlinkSync(pipeOutput); // cleanup demo file
  });

/*
Run: node 07-streams.js

Actual output (verified, chunk/byte counts match this file's own size; ORDER
varies run to run since these are 4 independent async operations racing
each other, not a real correctness issue):
3) Writable stream finished writing .../07-streams-output.txt
4/5) pipe() through Transform stream complete -> .../07-pipe-output.txt
1) fs.readFile loaded whole file: 3950 chars in one go
2) Readable stream: got 16 chunks, 3950 bytes total

Interview soundbite: "Streams process data in chunks instead of loading
everything into memory, which matters for large files or network data -
constant memory usage instead of memory proportional to file size. There
are 4 types: Readable (source, e.g. reading a file), Writable (destination),
Duplex (both directions, e.g. a TCP socket), and Transform (duplex that
modifies data in-flight, e.g. gzip or my uppercase example). `pipe()` is the
idiomatic way to connect them because it automatically handles backpressure
- if the writable side is slower than the readable side, pipe pauses the
source instead of buffering everything in memory."
*/
