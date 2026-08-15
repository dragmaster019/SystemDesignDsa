#!/usr/bin/env node
// Scaffolds a LeetCode problem: creates <problemsDir>/<number>/ containing
// <number>.java, <number>.cpp, <number>.js, <number>.py, each with the
// problem statement inserted as a comment block.
//
// Usage: node scaffold-leetcode.js <problemNumber> [problemsDir]

const fs = require("fs");
const path = require("path");
const {
  START_MARKER,
  END_MARKER,
  commentStyleForExt,
  buildCommentBlock,
  fetchProblemByNumber,
} = require("./leetcode-lib");

const LANGUAGE_EXTS = [".java", ".cpp", ".js", ".py"];

function writeCommentBlock(filePath, commentBlock) {
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
  const [, , problemNumber, problemsDirArg] = process.argv;

  if (!problemNumber) {
    console.error("Usage: node scaffold-leetcode.js <problemNumber> [problemsDir]");
    process.exit(1);
  }

  const problemsDir =
    problemsDirArg || path.resolve(__dirname, "..");
  const folder = path.join(problemsDir, String(problemNumber));

  fs.mkdirSync(folder, { recursive: true });

  const problem = await fetchProblemByNumber(problemNumber);

  for (const ext of LANGUAGE_EXTS) {
    const filePath = path.join(folder, `${problemNumber}${ext}`);
    const commentStyle = commentStyleForExt(ext);
    const commentBlock = buildCommentBlock(problem, commentStyle);
    writeCommentBlock(filePath, commentBlock);
  }

  console.log(
    `Created ${folder}/ with ${problemNumber}${LANGUAGE_EXTS.join(`, ${problemNumber}`)} for "${problem.title}"`
  );
}

main().catch((err) => {
  console.error(err.message);
  process.exit(1);
});
