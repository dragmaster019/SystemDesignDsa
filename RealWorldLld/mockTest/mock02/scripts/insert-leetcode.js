#!/usr/bin/env node
// Fetches a LeetCode problem by its number and writes it into a single file
// as a comment block.
//
// Usage: node insert-leetcode.js <targetFile> <problemNumber>

const fs = require("fs");
const path = require("path");
const {
  START_MARKER,
  END_MARKER,
  commentStyleForExt,
  buildCommentBlock,
  fetchProblemByNumber,
} = require("./leetcode-lib");

function insertIntoFile(filePath, commentBlock) {
  const existing = fs.existsSync(filePath)
    ? fs.readFileSync(filePath, "utf8")
    : "";

  const blockRegex = new RegExp(
    `[^\\n]*${START_MARKER}[\\s\\S]*?${END_MARKER}\\n?`,
    "m"
  );

  let updated;
  if (blockRegex.test(existing)) {
    updated = existing.replace(blockRegex, `${commentBlock}\n`);
  } else {
    updated = existing.length
      ? `${commentBlock}\n\n${existing}`
      : `${commentBlock}\n`;
  }

  fs.writeFileSync(filePath, updated, "utf8");
}

async function main() {
  const [, , targetFile, problemNumber] = process.argv;

  if (!targetFile || !problemNumber) {
    console.error("Usage: node insert-leetcode.js <targetFile> <problemNumber>");
    process.exit(1);
  }

  const problem = await fetchProblemByNumber(problemNumber);
  const commentStyle = commentStyleForExt(path.extname(targetFile));
  const commentBlock = buildCommentBlock(problem, commentStyle);

  insertIntoFile(targetFile, commentBlock);
  console.log(`Inserted LeetCode #${problemNumber} (${problem.title}) into ${targetFile}`);
}

main().catch((err) => {
  console.error(err.message);
  process.exit(1);
});
