# Java vs C++ vs JavaScript — Syntax Differences Log

Running notes on where these three languages actually differ, captured while solving problems
from `interview_question_bank.md`. This file grows as new gotchas come up — not a full language
reference, just the things that actually tripped me up or were non-obvious.

---

## 1. Do you even need a class?

| | Required? |
|---|---|
| **Java** | **Yes, always.** Java has no free-standing functions — even `main` must live inside a class. |
| **C++** | **No** — free functions are legal. `class Solution { ... }` is a *LeetCode convention*, not a language requirement. You could write `double findMaxAverage(...)` directly at global scope. |
| **JavaScript** | **No.** Functions are first-class and standalone — `function findMaxAverage(nums, k) { }` needs no wrapper of any kind. |

## 2. Entry point / how the program starts

| | Entry point |
|---|---|
| **Java** | Explicit `public static void main(String[] args)` inside some class. You run it by **class name** (`java Main`), not by filename. |
| **C++** | Explicit `int main() { }` at global scope. You run the **compiled binary** (`./a.out`), not the source file. |
| **JavaScript** | No entry point needed — top-level code just runs top to bottom when the file is executed (`node file.js`). No `main` wrapper at all. |

## 3. Compiling & running

| | Command |
|---|---|
| **Java** | `javac 643lc.java` → produces `.class` files → `java Main` (runs the class containing `main`, by name, not by file) |
| **C++** | `g++ 643lc.c++ -o sol` → `./sol` |
| **JavaScript** | `node 643lc.js` — one step, no compile. Source is interpreted directly. |

**Gotcha (Java/C++ only):** if you edit the source but don't recompile, you're still running the old
compiled binary/`.class` — it'll silently give stale output. This can't happen in JS since there's
nothing precompiled to go stale.

## 4. `private` / `public` access modifiers

- Only mean something **inside a class body**. Writing `private:` / `public:` at global scope
  (outside any `class { }`) in C++ is invalid — it doesn't compile.
- A `private` member can only be called from **inside the class** (or a `friend`). `main()`, being
  a free function, cannot call a private method directly — it needs a `public` method to go through.

## 5. Java: public class name must match the filename

- Only **one `public` top-level class per file is allowed**, and if you have one, its name must
  **exactly match the filename** (`Solution.java` → `class Solution`).
- Class names can't start with a digit, so a file like `643lc.java` can never contain a `public class
  643lc` (illegal identifier).
- **Workaround used here:** make neither class `public` (just `class Solution`, `class Main`).
  Non-public classes have no filename-matching restriction, so the file can be named anything.

## 6. Numeric types & casting

| | Behavior |
|---|---|
| **Java** | `int` and `double` are distinct types. Dividing two `int`s truncates — need `(double) x / k` to get a decimal result. |
| **C++** | Same issue as Java. Cast with `(double) x / k` (C-style) or the more idiomatic `static_cast<double>(x) / k`. |
| **JavaScript** | **One number type only** (always double-precision float). `x / k` already gives a decimal — no cast needed, ever. |

## 7. Loop bounds: signed vs unsigned comparison (C++-specific gotcha)

- `vector::size()` returns `size_t`, which is **unsigned**. Comparing it against a signed `int` loop
  variable (`int j; j < nums.size()`) compiles but is a classic footgun (`-Wsign-compare` warning).
- Fix: cast (`j < (int)nums.size()`) or use `static_cast<int>`, or keep a separate `int n = nums.size();`
  to compare against.
- **Doesn't happen in Java** (`array.length` is a plain `int`) or **JS** (`array.length` is just a
  number, no signed/unsigned distinction exists).

## 8. Type hints on functions (JSDoc header)

- Java and C++ are **statically typed** — the signature itself already says the types:
  `double findMaxAverage(vector<int>& nums, int k)` / `public double findMaxAverage(int[] nums, int k)`.
  No extra annotation needed.
- JavaScript is **untyped** — `function findMaxAverage(nums, k)` alone tells you nothing about what
  `nums` or `k` are. The convention (matches LeetCode's own JS template) is a **JSDoc comment** above
  the function to document intended types:
  ```js
  /**
   * @param {number[]} nums
   * @param {number} k
   * @return {number}
   */
  function findMaxAverage(nums, k) {
      ...
  }
  ```
- Purely a comment for humans/editors (VS Code uses it for hover hints and autocomplete) — it's not
  enforced at runtime the way Java/C++ types are. Optional, but good habit since it's the only way
  JS communicates a function's expected shape.

## 9. `#include <bits/stdc++.h>` doesn't work on macOS

- `bits/stdc++.h` is a **GNU-only convenience header** (pulls in nearly the whole standard
  library in one line). It's why it works on LeetCode/Codeforces/etc. — those judges run real
  GCC on Linux.
- macOS ships **Apple Clang** as `g++`/`clang++` by default, which doesn't have this header at
  all → `fatal error: 'bits/stdc++.h' file not found`.
- Fix: include only what you actually use, e.g. `#include <iostream>`, `<string>`,
  `<unordered_map>`, `<vector>`, `<algorithm>`. More typing, but it's the portable way and
  forces you to know what each header actually gives you — useful for interviews anyway, since
  `bits/stdc++.h` won't fly in most onsite environments either.

---

*Add new sections below as new problems surface new differences — don't need to re-derive
the above each time, just append.*
