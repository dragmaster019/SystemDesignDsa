#!/usr/bin/env node
// Scaffolds one or more LeetCode problems: creates <problemsDir>/<number>/
// containing <number>.java, <number>.cpp, <number>.js, <number>.py, each
// with the problem statement inserted as a comment block.
//
// Usage: node scaffold-leetcode.js <problemNumber>[,<problemNumber>...] [problemsDir]
//node RealWorldLld/scripts/scaffold-leetcode.js 42
//node RealWorldLld/scripts/scaffold-leetcode.js 42,209,76


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

async function scaffoldOne(problemNumber, problemsDir) {
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

async function main() {
  const [, , problemNumbersArg, problemsDirArg] = process.argv;

  if (!problemNumbersArg) {
    console.error("Usage: node scaffold-leetcode.js <problemNumber>[,<problemNumber>...] [problemsDir]");
    process.exit(1);
  }

  const problemsDir = problemsDirArg || path.resolve(__dirname, "..");
  const problemNumbers = problemNumbersArg
    .split(",")
    .map((n) => n.trim())
    .filter(Boolean);

  let hadError = false;
  for (const problemNumber of problemNumbers) {
    try {
      await scaffoldOne(problemNumber, problemsDir);
    } catch (err) {
      hadError = true;
      console.error(`${problemNumber}: ${err.message}`);
    }
  }

  if (hadError) process.exit(1);
}

main().catch((err) => {
  console.error(err.message);
  process.exit(1);
});
