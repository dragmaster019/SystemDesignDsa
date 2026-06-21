# Interview Question Bank — All 16 Patterns

> The actual problems interviewers pull from, grouped by the pattern that solves them.
> Within each pattern they're ordered **easy → hard** so you build up.
>
> **★ = must-do** (high frequency / on Blind 75 / NeetCode 150 — do these first).
> **▶ = start here** (the cleanest first problem to learn the pattern on).
>
> **How to use this:** for each problem, run the loop you've been practicing —
> *derive the brute force → spot the waste → name the pattern → code → dry run.*
> Don't just solve; make sure you can explain **why** the pattern fits.
>
> All numbers are LeetCode. Check problems off as you go.

---

## 1. Sliding Window

| ✓ | # | Problem | Difficulty | |
|---|---|---------|------------|---|
| ☐ | 643 | Maximum Average Subarray I | Easy | ▶ fixed window |
| ☐ | 3 | Longest Substring Without Repeating Characters | Medium | ★ |
| ☐ | 209 | Minimum Size Subarray Sum | Medium | shrink-while-valid |
| ☐ | 424 | Longest Repeating Character Replacement | Medium | ★ |
| ☐ | 567 | Permutation in String | Medium | ★ |
| ☐ | 438 | Find All Anagrams in a String | Medium | |
| ☐ | 340 | Longest Substring with At Most K Distinct Characters | Medium | |
| ☐ | 904 | Fruit Into Baskets | Medium | |
| ☐ | 76 | Minimum Window Substring | Hard | ★ (the boss) |
| ☐ | 239 | Sliding Window Maximum | Hard | (deque variant) |

**Recognize it:** "contiguous subarray/substring," "longest/shortest such that…," "of size k."

---

## 2. Two Pointers

| ✓ | # | Problem | Difficulty | |
|---|---|---------|------------|---|
| ☐ | 125 | Valid Palindrome | Easy | ▶ converging |
| ☐ | 977 | Squares of a Sorted Array | Easy | fill from back |
| ☐ | 26 | Remove Duplicates from Sorted Array | Easy | same-direction |
| ☐ | 680 | Valid Palindrome II | Easy | |
| ☐ | 167 | Two Sum II – Input Array Is Sorted | Medium | ★ |
| ☐ | 15 | 3Sum | Medium | ★ (fix one + 2ptr) |
| ☐ | 11 | Container With Most Water | Medium | ★ |
| ☐ | 75 | Sort Colors (Dutch flag) | Medium | three-way |
| ☐ | 16 | 3Sum Closest | Medium | |
| ☐ | 42 | Trapping Rain Water | Hard | ★ |

**Recognize it:** sorted array + find a pair/triplet, or squeezing from both ends.

---

## 3. Fast & Slow Pointers

| ✓ | # | Problem | Difficulty | |
|---|---|---------|------------|---|
| ☐ | 141 | Linked List Cycle | Easy | ▶ ★ detect cycle |
| ☐ | 876 | Middle of the Linked List | Easy | |
| ☐ | 202 | Happy Number | Easy | cycle in disguise |
| ☐ | 234 | Palindrome Linked List | Easy | mid + reverse |
| ☐ | 142 | Linked List Cycle II | Medium | ★ find cycle start |
| ☐ | 287 | Find the Duplicate Number | Medium | ★ (array as links) |
| ☐ | 457 | Circular Array Loop | Medium | |

**Recognize it:** linked list + cycle / middle / kth-from-end, with O(1) space.

---

## 4. Merge Intervals

| ✓ | # | Problem | Difficulty | |
|---|---|---------|------------|---|
| ☐ | 252 | Meeting Rooms | Easy | ▶ overlap check |
| ☐ | 56 | Merge Intervals | Medium | ★ |
| ☐ | 57 | Insert Interval | Medium | ★ |
| ☐ | 253 | Meeting Rooms II | Medium | ★ (min rooms) |
| ☐ | 435 | Non-overlapping Intervals | Medium | |
| ☐ | 986 | Interval List Intersections | Medium | |
| ☐ | 759 | Employee Free Time | Hard | |

**Recognize it:** list of `[start, end]` pairs + merge/overlap/intersection. **Sort by start first.**

---

## 5. Cyclic Sort

| ✓ | # | Problem | Difficulty | |
|---|---|---------|------------|---|
| ☐ | 268 | Missing Number | Easy | ▶ ★ |
| ☐ | 448 | Find All Numbers Disappeared in an Array | Easy | ★ |
| ☐ | 645 | Set Mismatch | Easy | corrupt pair |
| ☐ | 442 | Find All Duplicates in an Array | Medium | |
| ☐ | 287 | Find the Duplicate Number | Medium | |
| ☐ | 41 | First Missing Positive | Hard | ★ (the boss) |

**Recognize it:** array of n values in range `[1,n]` or `[0,n-1]` + missing/duplicate, O(1) space.

---

## 6. In-place Reversal of Linked List

| ✓ | # | Problem | Difficulty | |
|---|---|---------|------------|---|
| ☐ | 206 | Reverse Linked List | Easy | ▶ ★ |
| ☐ | 92 | Reverse Linked List II | Medium | ★ (sublist) |
| ☐ | 24 | Swap Nodes in Pairs | Medium | |
| ☐ | 61 | Rotate List | Medium | |
| ☐ | 143 | Reorder List | Medium | mid + reverse + merge |
| ☐ | 25 | Reverse Nodes in k-Group | Hard | ★ |

**Recognize it:** reverse a list (or a portion) with O(1) space.

---

## 7. Tree BFS

| ✓ | # | Problem | Difficulty | |
|---|---|---------|------------|---|
| ☐ | 102 | Binary Tree Level Order Traversal | Medium | ▶ ★ |
| ☐ | 637 | Average of Levels in Binary Tree | Easy | |
| ☐ | 111 | Minimum Depth of Binary Tree | Easy | |
| ☐ | 103 | Binary Tree Zigzag Level Order Traversal | Medium | ★ |
| ☐ | 199 | Binary Tree Right Side View | Medium | ★ |
| ☐ | 116 | Populating Next Right Pointers in Each Node | Medium | |
| ☐ | 994 | Rotting Oranges | Medium | grid BFS ★ |
| ☐ | 542 | 01 Matrix | Medium | multi-source BFS |

**Recognize it:** anything "per level" — width, depth, layer, nearest. Use a queue.

---

## 8. DFS (Tree & Graph)

| ✓ | # | Problem | Difficulty | |
|---|---|---------|------------|---|
| ☐ | 104 | Maximum Depth of Binary Tree | Easy | ▶ |
| ☐ | 112 | Path Sum | Easy | |
| ☐ | 543 | Diameter of Binary Tree | Easy | post-order |
| ☐ | 200 | Number of Islands | Medium | ★ grid flood |
| ☐ | 113 | Path Sum II | Medium | backtracking |
| ☐ | 695 | Max Area of Island | Medium | |
| ☐ | 133 | Clone Graph | Medium | ★ |
| ☐ | 417 | Pacific Atlantic Water Flow | Medium | |
| ☐ | 129 | Sum Root to Leaf Numbers | Medium | |
| ☐ | 124 | Binary Tree Maximum Path Sum | Hard | ★ post-order |

**Recognize it:** whole paths, subtrees, connected components, cycle detection.

---

## 9. Two Heaps

| ✓ | # | Problem | Difficulty | |
|---|---|---------|------------|---|
| ☐ | 295 | Find Median from Data Stream | Hard | ▶ ★ |
| ☐ | 480 | Sliding Window Median | Hard | (add + remove) |
| ☐ | 502 | IPO | Hard | two priority queues |
| ☐ | 436 | Find Right Interval | Medium | |

**Recognize it:** running median, or split a stream into two prioritized halves.

---

## 10. Subsets / Backtracking

| ✓ | # | Problem | Difficulty | |
|---|---|---------|------------|---|
| ☐ | 78 | Subsets | Medium | ▶ ★ |
| ☐ | 46 | Permutations | Medium | ★ |
| ☐ | 22 | Generate Parentheses | Medium | ★ |
| ☐ | 17 | Letter Combinations of a Phone Number | Medium | ★ |
| ☐ | 39 | Combination Sum | Medium | ★ |
| ☐ | 90 | Subsets II | Medium | dup handling |
| ☐ | 40 | Combination Sum II | Medium | |
| ☐ | 47 | Permutations II | Medium | dup handling |
| ☐ | 79 | Word Search | Medium | grid backtracking |
| ☐ | 131 | Palindrome Partitioning | Medium | |
| ☐ | 51 | N-Queens | Hard | ★ |

**Recognize it:** "generate all …" — subsets, permutations, combinations, arrangements.

---

## 11. Modified Binary Search

| ✓ | # | Problem | Difficulty | |
|---|---|---------|------------|---|
| ☐ | 704 | Binary Search | Easy | ▶ the template |
| ☐ | 35 | Search Insert Position | Easy | |
| ☐ | 33 | Search in Rotated Sorted Array | Medium | ★ |
| ☐ | 153 | Find Minimum in Rotated Sorted Array | Medium | ★ |
| ☐ | 34 | Find First and Last Position of Element | Medium | ★ boundary |
| ☐ | 162 | Find Peak Element | Medium | |
| ☐ | 74 | Search a 2D Matrix | Medium | |
| ☐ | 875 | Koko Eating Bananas | Medium | ★ search the answer |
| ☐ | 410 | Split Array Largest Sum | Hard | search the answer |
| ☐ | 4 | Median of Two Sorted Arrays | Hard | ★ (the boss) |

**Recognize it:** sorted/rotated input + find/first/last/peak, or "minimize the maximum."

---

## 12. Bitwise XOR

| ✓ | # | Problem | Difficulty | |
|---|---|---------|------------|---|
| ☐ | 136 | Single Number | Easy | ▶ ★ |
| ☐ | 268 | Missing Number | Easy | |
| ☐ | 191 | Number of 1 Bits | Easy | |
| ☐ | 338 | Counting Bits | Easy | ★ |
| ☐ | 461 | Hamming Distance | Easy | |
| ☐ | 137 | Single Number II | Medium | |
| ☐ | 260 | Single Number III | Medium | two singles |
| ☐ | 371 | Sum of Two Integers | Medium | add without + |

**Recognize it:** lone element among pairs, bit counting/flipping, O(1) space.

---

## 13. Top K Elements

| ✓ | # | Problem | Difficulty | |
|---|---|---------|------------|---|
| ☐ | 703 | Kth Largest Element in a Stream | Easy | ▶ |
| ☐ | 215 | Kth Largest Element in an Array | Medium | ★ |
| ☐ | 347 | Top K Frequent Elements | Medium | ★ |
| ☐ | 973 | K Closest Points to Origin | Medium | ★ |
| ☐ | 692 | Top K Frequent Words | Medium | |
| ☐ | 451 | Sort Characters By Frequency | Medium | |
| ☐ | 658 | Find K Closest Elements | Medium | |
| ☐ | 621 | Task Scheduler | Medium | ★ |
| ☐ | 767 | Reorganize String | Medium | |

**Recognize it:** K largest/smallest/most-frequent/closest — a ranked slice, not full order.

---

## 14. K-way Merge

| ✓ | # | Problem | Difficulty | |
|---|---|---------|------------|---|
| ☐ | 21 | Merge Two Sorted Lists | Easy | ▶ (the 2-way base) |
| ☐ | 23 | Merge k Sorted Lists | Hard | ★ |
| ☐ | 378 | Kth Smallest Element in a Sorted Matrix | Medium | ★ |
| ☐ | 373 | Find K Pairs with Smallest Sums | Medium | |
| ☐ | 632 | Smallest Range Covering Elements from K Lists | Hard | |

**Recognize it:** merge K sorted lists, or Kth smallest across sorted sources.

---

## 15. 0/1 Knapsack & DP

| ✓ | # | Problem | Difficulty | |
|---|---|---------|------------|---|
| ☐ | 198 | House Robber | Medium | ▶ DP intro |
| ☐ | 416 | Partition Equal Subset Sum | Medium | ★ subset-sum |
| ☐ | 494 | Target Sum | Medium | ★ |
| ☐ | 1049 | Last Stone Weight II | Medium | |
| ☐ | 474 | Ones and Zeroes | Medium | 2D knapsack |
| ☐ | 322 | Coin Change | Medium | ★ (unbounded) |
| ☐ | 518 | Coin Change II | Medium | count ways |
| ☐ | 1143 | Longest Common Subsequence | Medium | ★ classic 2D DP |
| ☐ | 300 | Longest Increasing Subsequence | Medium | ★ |
| ☐ | 72 | Edit Distance | Hard | ★ |

**Recognize it:** pick a subset under a budget; "can we reach exactly X?"; overlapping subproblems.
*(DP is huge — this is the knapsack family plus the most-asked general DP. Master these patterns,
then branch into grid DP, interval DP, etc.)*

---

## 16. Topological Sort

| ✓ | # | Problem | Difficulty | |
|---|---|---------|------------|---|
| ☐ | 207 | Course Schedule | Medium | ▶ ★ (can finish?) |
| ☐ | 210 | Course Schedule II | Medium | ★ (give order) |
| ☐ | 310 | Minimum Height Trees | Medium | |
| ☐ | 444 | Sequence Reconstruction | Medium | |
| ☐ | 269 | Alien Dictionary | Hard | ★ (the boss) |
| ☐ | 2115 | Find All Possible Recipes from Given Supplies | Medium | |

**Recognize it:** tasks with dependencies, build/course order, "is an ordering possible?"

---

# The roadmap

**If you do nothing else, do the ★ problems** — that's roughly 45 problems covering every pattern at
interview frequency. That alone makes you dangerous in an SDE-1 loop.

**Suggested order (build confidence → tackle bosses):**

1. **Week 1–2 — arrays & strings:** Patterns 1, 2, 4, 5. Start with each ▶, then the ★s. Highest ROI;
   these show up most.
2. **Week 3 — linked lists:** Patterns 3, 6. Short and mechanical once the pointer dance clicks.
3. **Week 4–5 — trees & graphs:** Patterns 7, 8, 16. BFS and DFS reinforce each other; topo sort is
   "DFS/BFS with dependencies."
4. **Week 6 — heaps & search:** Patterns 9, 13, 14, 11. The heap trio shares one mental model; binary
   search rewards careful boundary thinking.
5. **Week 7–8 — the hard conceptual ones:** Patterns 10, 12, 15. Backtracking and DP need the most
   reps — budget extra time and re-solve them.

**Per-problem discipline (this is what separates pass from fail):**
- Give yourself ~25–30 min. Stuck? Read the *idea* only (not the full solution), then code it yourself.
- After solving, **re-solve from scratch the next day.** First solve = exposure; second = retention.
- Always say out loud: *"The dumb way is ___, wasteful because ___, so I use [pattern]."*
- Dry-run your code by hand on a small input before running it — exactly like we've been doing.

**The boss problems** (do these last, per pattern — they signal mastery): 76 Min Window Substring,
42 Trapping Rain Water, 41 First Missing Positive, 25 Reverse k-Group, 124 Max Path Sum,
295 Median from Stream, 51 N-Queens, 4 Median of Two Sorted Arrays, 23 Merge k Lists, 72 Edit Distance,
269 Alien Dictionary.

You already understand the patterns. Now it's reps. Check the boxes — momentum compounds.
