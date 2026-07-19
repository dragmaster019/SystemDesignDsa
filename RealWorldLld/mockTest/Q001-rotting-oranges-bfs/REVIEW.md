# Review — Q001 Rotting Oranges

**Verdict: Ready**

## 1. Correctness

Fixed. Re-ran all cases, all pass:

| Input | Expected | Got |
|---|---|---|
| `[[2,1,1],[1,1,0],[0,1,1]]` | 4 | 4 |
| `[[2,1,1],[0,1,1],[1,0,1]]` | -1 | -1 |
| `[[0,2]]` | 0 | 0 |
| `[[0,0,0]]` (all empty) | 0 | 0 |
| `[[1]]` (fresh, no rotten source) | -1 | -1 |
| `[[2,1,1,1,1,1,1,1,1,2]]` (two-source counterexample from last review) | 4 | 4 |

The seeding bug from the previous review is fixed — you now do one pass to collect every
initially-rotten cell into the queue, then a single `while` loop expands all sources
together, level by level. That's the correct multi-source BFS shape.

## 2. Complexity

`O(m*n)` time and space — every cell is enqueued/dequeued at most once. Correctly matches
what the problem is asking to justify.

## 3. Structure / readability — remaining minor points (not blocking)

- Method name is still `rottenOranges`; the problem's signature was `orangesRotting`. Low
  stakes here since this is practice, but in a real interview match the exact signature
  you're handed.
- The narrative scratch comments (lines 55-76) are good for your own thinking but should
  be stripped before calling code "final" — an interviewer wants to see clean code, not the
  thought process left inline.
- Trailing `;` after the class's closing `}` (last line) is legal but non-idiomatic.

None of these affect correctness — they're polish for next time.

## 4. Interview readiness

You can now explain this cleanly: "seed all rotten oranges first so BFS expands every
source simultaneously, level by level — that's what makes the level counter equal to the
true shortest time for every cell." That's the answer an interviewer is listening for on
this problem, and you arrived at it yourself after the counterexample exposed the gap.

## Next step

Q001 is done. Move on to Q002 (Library Management System) when ready, or ask for another
DSA question.
