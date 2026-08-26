# TC/SC Reference — All 16 Patterns

> Target (optimal) Time Complexity and Space Complexity for every problem in
> `interview_question_bank.md`, grouped the same way. Use this to check whether
> your own solution's complexity matches the intended one — not just "does it pass,"
> but "is it the complexity the pattern is supposed to achieve."
>
> `n` = input size (array length / string length) unless noted otherwise.
> Space complexity excludes the space required for the output itself, unless noted.

---

## 1. Sliding Window

| # | Problem | Time | Space | Note |
|---|---------|------|-------|------|
| 643 | Maximum Average Subarray I | O(n) | O(1) | fixed window |
| 3 | Longest Substring Without Repeating Characters | O(n) | O(min(n, charset)) | |
| 209 | Minimum Size Subarray Sum | O(n) | O(1) | |
| 424 | Longest Repeating Character Replacement | O(n) | O(26) → O(1) | |
| 567 | Permutation in String | O(n) | O(26) → O(1) | |
| 438 | Find All Anagrams in a String | O(n) | O(26) → O(1) | |
| 340 | Longest Substring with At Most K Distinct Characters | O(n) | O(k) | |
| 904 | Fruit Into Baskets | O(n) | O(1) | at most 2 distinct types |
| 76 | Minimum Window Substring | O(n + m) | O(charset) | |
| 239 | Sliding Window Maximum | O(n) | O(k) | monotonic deque |

---

## 2. Two Pointers

| # | Problem | Time | Space | Note |
|---|---------|------|-------|------|
| 125 | Valid Palindrome | O(n) | O(1) | |
| 977 | Squares of a Sorted Array | O(n) | O(n) | output array required |
| 26 | Remove Duplicates from Sorted Array | O(n) | O(1) | |
| 680 | Valid Palindrome II | O(n) | O(1) | |
| 167 | Two Sum II – Sorted | O(n) | O(1) | |
| 15 | 3Sum | O(n²) | O(1)–O(n) | O(n) if sort isn't in-place |
| 11 | Container With Most Water | O(n) | O(1) | |
| 75 | Sort Colors | O(n) | O(1) | Dutch flag, one pass |
| 16 | 3Sum Closest | O(n²) | O(1) | |
| 42 | Trapping Rain Water | O(n) | O(1) | two-pointer version |

---

## 3. Fast & Slow Pointers

| # | Problem | Time | Space | Note |
|---|---------|------|-------|------|
| 141 | Linked List Cycle | O(n) | O(1) | |
| 876 | Middle of the Linked List | O(n) | O(1) | |
| 202 | Happy Number | O(log n) | O(1) | cycle detection on digit-sum chain |
| 234 | Palindrome Linked List | O(n) | O(1) | mid + reverse half |
| 142 | Linked List Cycle II | O(n) | O(1) | |
| 287 | Find the Duplicate Number | O(n) | O(1) | array-as-linked-list |
| 457 | Circular Array Loop | O(n) | O(1) | |

---

## 4. Merge Intervals

| # | Problem | Time | Space | Note |
|---|---------|------|-------|------|
| 252 | Meeting Rooms | O(n log n) | O(1) | sort dominates |
| 56 | Merge Intervals | O(n log n) | O(n) | output |
| 57 | Insert Interval | O(n) | O(n) | already sorted, no re-sort needed |
| 253 | Meeting Rooms II | O(n log n) | O(n) | heap or two sorted arrays |
| 435 | Non-overlapping Intervals | O(n log n) | O(1) | |
| 986 | Interval List Intersections | O(n + m) | O(n + m) | output |
| 759 | Employee Free Time | O(n log n) | O(n) | |

---

## 5. Cyclic Sort

| # | Problem | Time | Space | Note |
|---|---------|------|-------|------|
| 268 | Missing Number | O(n) | O(1) | |
| 448 | Find All Numbers Disappeared in an Array | O(n) | O(1) | excluding output list |
| 645 | Set Mismatch | O(n) | O(1) | |
| 442 | Find All Duplicates in an Array | O(n) | O(1) | excluding output list |
| 287 | Find the Duplicate Number | O(n) | O(1) | |
| 41 | First Missing Positive | O(n) | O(1) | the whole point of the pattern |

---

## 6. In-place Reversal of Linked List

| # | Problem | Time | Space | Note |
|---|---------|------|-------|------|
| 206 | Reverse Linked List | O(n) | O(1) | |
| 92 | Reverse Linked List II | O(n) | O(1) | |
| 24 | Swap Nodes in Pairs | O(n) | O(1) | |
| 61 | Rotate List | O(n) | O(1) | |
| 143 | Reorder List | O(n) | O(1) | mid + reverse + merge |
| 25 | Reverse Nodes in k-Group | O(n) | O(1) | |

---

## 7. Tree BFS

| # | Problem | Time | Space | Note |
|---|---------|------|-------|------|
| 102 | Binary Tree Level Order Traversal | O(n) | O(n) | queue holds a level, worst-case n/2 |
| 637 | Average of Levels in Binary Tree | O(n) | O(n) | |
| 111 | Minimum Depth of Binary Tree | O(n) | O(n) worst / O(log n) avg | |
| 103 | Binary Tree Zigzag Level Order Traversal | O(n) | O(n) | |
| 199 | Binary Tree Right Side View | O(n) | O(n) | |
| 116 | Populating Next Right Pointers in Each Node | O(n) | O(1) | using already-set next pointers |
| 994 | Rotting Oranges | O(rows·cols) | O(rows·cols) | multi-source BFS |
| 542 | 01 Matrix | O(rows·cols) | O(rows·cols) | multi-source BFS |

---

## 8. DFS (Tree & Graph)

| # | Problem | Time | Space | Note |
|---|---------|------|-------|------|
| 104 | Maximum Depth of Binary Tree | O(n) | O(h) | h = height, recursion stack |
| 112 | Path Sum | O(n) | O(h) | |
| 543 | Diameter of Binary Tree | O(n) | O(h) | post-order |
| 200 | Number of Islands | O(rows·cols) | O(rows·cols) | worst-case recursion stack |
| 113 | Path Sum II | O(n²) worst | O(h) excl. output | backtracking |
| 695 | Max Area of Island | O(rows·cols) | O(rows·cols) | |
| 133 | Clone Graph | O(V + E) | O(V) | |
| 417 | Pacific Atlantic Water Flow | O(rows·cols) | O(rows·cols) | |
| 129 | Sum Root to Leaf Numbers | O(n) | O(h) | |
| 124 | Binary Tree Maximum Path Sum | O(n) | O(h) | post-order |

---

## 9. Two Heaps

| # | Problem | Time | Space | Note |
|---|---------|------|-------|------|
| 295 | Find Median from Data Stream | O(log n) per insert | O(n) | two heaps |
| 480 | Sliding Window Median | O(n log k) | O(k) | |
| 502 | IPO | O(n log n) | O(n) | two priority queues |
| 436 | Find Right Interval | O(n log n) | O(n) | sort + binary search, heap optional |

---

## 10. Subsets / Backtracking

| # | Problem | Time | Space | Note |
|---|---------|------|-------|------|
| 78 | Subsets | O(n · 2ⁿ) | O(n) excl. output | |
| 46 | Permutations | O(n · n!) | O(n) excl. output | |
| 22 | Generate Parentheses | O(4ⁿ / √n) | O(n) excl. output | Catalan number bound |
| 17 | Letter Combinations of a Phone Number | O(4ⁿ · n) | O(n) excl. output | |
| 39 | Combination Sum | O(2ᵗ) roughly | O(target) | t relates to target/min candidate |
| 90 | Subsets II | O(n · 2ⁿ) | O(n) excl. output | dup handling |
| 40 | Combination Sum II | O(2ⁿ) | O(n) excl. output | dup handling |
| 47 | Permutations II | O(n · n!) | O(n) excl. output | dup handling |
| 79 | Word Search | O(rows·cols · 4ᴸ) | O(L) | L = word length |
| 131 | Palindrome Partitioning | O(n · 2ⁿ) | O(n) excl. output | |
| 51 | N-Queens | O(n!) | O(n) excl. output | |

---

## 11. Modified Binary Search

| # | Problem | Time | Space | Note |
|---|---------|------|-------|------|
| 704 | Binary Search | O(log n) | O(1) | |
| 35 | Search Insert Position | O(log n) | O(1) | |
| 33 | Search in Rotated Sorted Array | O(log n) | O(1) | |
| 153 | Find Minimum in Rotated Sorted Array | O(log n) | O(1) | |
| 34 | Find First and Last Position of Element | O(log n) | O(1) | two binary searches |
| 162 | Find Peak Element | O(log n) | O(1) | |
| 74 | Search a 2D Matrix | O(log(rows·cols)) | O(1) | treat as 1D |
| 875 | Koko Eating Bananas | O(n log m) | O(1) | search-the-answer, m = max pile |
| 410 | Split Array Largest Sum | O(n log(sum)) | O(1) | search-the-answer |
| 4 | Median of Two Sorted Arrays | O(log(min(m, n))) | O(1) | |

---

## 12. Bitwise XOR

| # | Problem | Time | Space | Note |
|---|---------|------|-------|------|
| 136 | Single Number | O(n) | O(1) | |
| 268 | Missing Number | O(n) | O(1) | XOR variant of cyclic-sort problem |
| 191 | Number of 1 Bits | O(1) | O(1) | bounded by 32 bits |
| 338 | Counting Bits | O(n) | O(n) | output required |
| 461 | Hamming Distance | O(1) | O(1) | |
| 137 | Single Number II | O(n) | O(1) | bit counting mod 3 |
| 260 | Single Number III | O(n) | O(1) | two singles |
| 371 | Sum of Two Integers | O(1) | O(1) | bounded by 32-bit loop |

---

## 13. Top K Elements

| # | Problem | Time | Space | Note |
|---|---------|------|-------|------|
| 703 | Kth Largest Element in a Stream | O(log k) per insert | O(k) | |
| 215 | Kth Largest Element in an Array | O(n log k) | O(k) | or O(n) avg via quickselect |
| 347 | Top K Frequent Elements | O(n log k) | O(n) | or O(n) via bucket sort |
| 973 | K Closest Points to Origin | O(n log k) | O(k) | |
| 692 | Top K Frequent Words | O(n log k) | O(n) | |
| 451 | Sort Characters By Frequency | O(n log n) | O(n) | |
| 658 | Find K Closest Elements | O(log n + k) | O(k) | binary search + window |
| 621 | Task Scheduler | O(n) | O(26) → O(1) | |
| 767 | Reorganize String | O(n log 26) → O(n) | O(n) | |

---

## 14. K-way Merge

| # | Problem | Time | Space | Note |
|---|---------|------|-------|------|
| 21 | Merge Two Sorted Lists | O(n + m) | O(1) | in-place relinking |
| 23 | Merge k Sorted Lists | O(N log k) | O(k) | N = total nodes |
| 378 | Kth Smallest Element in a Sorted Matrix | O(k log n) or O(n log(max−min)) | O(n) | heap or binary search on value |
| 373 | Find K Pairs with Smallest Sums | O(k log k) | O(k) | |
| 632 | Smallest Range Covering Elements from K Lists | O(n log k) | O(k) | n = total elements |

---

## 15. 0/1 Knapsack & DP

| # | Problem | Time | Space | Note |
|---|---------|------|-------|------|
| 198 | House Robber | O(n) | O(1) | rolling variables |
| 416 | Partition Equal Subset Sum | O(n · sum) | O(sum) | 1D rolling array |
| 494 | Target Sum | O(n · sum) | O(sum) | |
| 1049 | Last Stone Weight II | O(n · sum) | O(sum) | |
| 474 | Ones and Zeroes | O(strings · m · n) | O(m · n) | 2D knapsack |
| 322 | Coin Change | O(amount · coins) | O(amount) | unbounded knapsack |
| 518 | Coin Change II | O(amount · coins) | O(amount) | count ways |
| 1143 | Longest Common Subsequence | O(n · m) | O(min(n, m)) | rolling 2D → 1D |
| 300 | Longest Increasing Subsequence | O(n log n) | O(n) | patience sorting / binary search |
| 72 | Edit Distance | O(n · m) | O(min(n, m)) | rolling 2D → 1D |

---

## 16. Topological Sort

| # | Problem | Time | Space | Note |
|---|---------|------|-------|------|
| 207 | Course Schedule | O(V + E) | O(V + E) | |
| 210 | Course Schedule II | O(V + E) | O(V + E) | |
| 310 | Minimum Height Trees | O(V) | O(V) | leaf-trimming BFS |
| 444 | Sequence Reconstruction | O(V + E) | O(V + E) | |
| 269 | Alien Dictionary | O(C) | O(1) → O(26) | C = total characters across words |
| 2115 | Find All Possible Recipes from Given Supplies | O(V + E) | O(V + E) | |

---

## How to use this

1. Solve the problem, get it correct first — don't chase complexity before correctness.
2. Once correct, name your own solution's TC/SC and compare it to the table.
3. If yours is worse (e.g. you got O(n²) where the table says O(n)), that's a signal
   there's a smarter pattern-fit you're missing — worth re-deriving before moving on,
   per the "derive brute force → spot the waste → name the pattern" loop in
   `interview_question_bank.md`.
4. If yours matches, you've actually mastered that problem — not just passed it.
