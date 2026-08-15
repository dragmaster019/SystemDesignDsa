---
name: leetcode
description: Scaffold a LeetCode problem by number - creates RealWorldLld/mockTest/mock02/<number>/ with <number>.java, <number>.cpp, <number>.js, <number>.py, each pre-filled with the problem statement as a comment block. Use when the user gives a LeetCode problem number and wants practice files set up.
---

Run the scaffolding script with the problem number the user gave, using the
Java version (the user's preferred one to read/maintain — it needs a one-time
`javac *.java` compile if `.class` files aren't already present):

```
cd RealWorldLld/mockTest/mock02/scripts/java
javac *.java   # only if ScaffoldLeetcode.class is missing or sources changed
java ScaffoldLeetcode <problemNumber>
```

A Node equivalent also exists at
`RealWorldLld/mockTest/mock02/scripts/scaffold-leetcode.js` (`node scaffold-leetcode.js <problemNumber>`,
run from the repo root) — same output, no compile step. Use it only if asked
for explicitly.

Either way this creates `RealWorldLld/mockTest/mock02/<problemNumber>/`
containing `<problemNumber>.java`, `<problemNumber>.cpp`, `<problemNumber>.js`,
and `<problemNumber>.py`. Each file gets the problem's title, difficulty, URL,
and full description inserted as a comment block (using `//` or `#` as
appropriate for the language).

Re-running for the same number replaces the existing comment block in each
file rather than duplicating it, and leaves any code already written below
the block untouched.

After running, report which folder/files were created (or updated) and the
problem title. If the script errors (e.g. invalid number, network failure),
surface the error message directly rather than retrying blindly.
