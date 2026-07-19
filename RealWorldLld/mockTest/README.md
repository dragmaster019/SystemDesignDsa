# Mock Interview Coding System

Why this exists: explaining logic verbally isn't the same skill as writing working code
under time pressure. This folder closes that gap through a repeatable loop.

## Loop

1. You ask for a question — optionally naming a topic, or "give me an LLD one" / "give me
   a DSA one" / "surprise me".
2. I create `Q0NN-slug/PROBLEM.md` with the question. No solution skeleton, no hints about
   which classes/patterns/algorithms to use — deciding that is the point.
3. You write the full solution directly inside that folder (classes, interfaces, functions —
   structure it however you would in a real interview). Java only.
4. You say "review Q0NN".
5. I read every file in that folder, write `REVIEW.md`, and append a row to `progress.md`.

## Question format (`PROBLEM.md`)

- Title
- Type: LLD or DSA
- Suggested time box (30-45 min, matching real interview pressure)
- Problem Statement
- Requirements/Constraints (functional requirements for LLD; input/output/edge cases for DSA)

## Review rubric (`REVIEW.md`)

1. **Correctness** — does it compile/run logically, are edge cases handled
2. **LLD questions**: class/interface design, SOLID adherence, pattern usage only where it
   actually fits (not forced)
3. **DSA questions**: time/space complexity, whether the optimal approach was found or just
   brute force
4. **Structure/naming/readability** — would this pass a real interviewer's skim
5. **Interview readiness** — written as if the interviewer just asked "walk me through your
   design/algorithm and why", flagging spots where you'd struggle to defend a decision

Verdict on every review: **Ready / Needs Work / Not Ready**, plus a concrete next step
(redo this question vs. move on).

## Folder layout

```
mockTest/
  README.md
  progress.md
  Q001-slug/
    PROBLEM.md
    <your .java files>
    REVIEW.md
  Q002-slug/
    ...
```
