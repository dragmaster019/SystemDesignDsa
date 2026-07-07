# All 16 Patterns — Q&A + How to Think + Dry Run

**Structure per pattern:**
1. Recognition signal (what in the problem triggers this?)
2. How to arrive at it (brute force → spot waste → name the pattern)
3. Dry run on the starter ▶ problem
4. Q&A — what an interviewer asks, what you say

---

## 1. Sliding Window

### Recognition signal
"Contiguous subarray/substring", "longest/shortest such that X", "of size k"
→ you need to examine a window of elements that slides across an array without re-scanning from scratch.

### How to arrive at it
**Brute force:** nested loop — for every start, scan every end. O(n²).
**Spot the waste:** when you move the window right by 1, you're re-examining all elements already in the window.
**Pattern:** keep a window [left, right]. Expand right by 1 each step. Only shrink left when the window violates the constraint. Every element enters and exits at most once → O(n).

### Dry run — LC 3: Longest Substring Without Repeating Characters
Input: `s = "abcabcbb"`

```
left=0, right=0, seen={}, maxLen=0

right=0 'a': seen={a:0}, window="a", len=1, max=1
right=1 'b': seen={a:0,b:1}, window="ab", len=2, max=2
right=2 'c': seen={a:0,b:1,c:2}, window="abc", len=3, max=3
right=3 'a': 'a' already in seen at idx 0 → left = max(left, 0+1) = 1
         seen={a:3,b:1,c:2}, window="bca", len=3, max=3
right=4 'b': 'b' in seen at idx 1 → left = max(1, 1+1) = 2
         seen={a:3,b:4,c:2}, window="cab", len=3, max=3
right=5 'c': 'c' in seen at idx 2 → left = max(2, 2+1) = 3
         seen={a:3,b:4,c:5}, window="abc", len=3, max=3
right=6 'b': 'b' in seen at idx 4 → left = max(3, 4+1) = 5
         seen={a:3,b:6,c:5}, window="cb", len=2, max=3
right=7 'b': 'b' in seen at idx 6 → left = max(5, 6+1) = 7
         window="b", len=1, max=3

Answer: 3
```

### Q&A
**Q: Why `left = max(left, seen[c]+1)` and not just `seen[c]+1`?**
A: Because `seen` stores the last position of every character seen ever, including ones already outside our current window. If we moved left past it already, we don't want to go backwards.

**Q: Fixed vs dynamic window?**
A: Fixed window (LC 643) — slide right and drop leftmost simultaneously, window size stays k.
Dynamic (LC 3, 76) — shrink left until constraint is satisfied again. Shrink condition is the key thing to identify.

---

## 2. Two Pointers

### Recognition signal
Sorted array + find a pair/triplet summing to target, or squeeze from both ends.
→ instead of nested loop O(n²), use two pointers moving toward each other.

### How to arrive at it
**Brute force:** for every pair (i,j), check if they meet the condition. O(n²).
**Spot the waste:** array is sorted. If `arr[left]+arr[right] > target`, moving right left makes the sum smaller. If too small, move left right. One pass, O(n).
**Pattern:** left=0, right=n-1. Move one pointer per step based on comparison.

### Dry run — LC 125: Valid Palindrome
Input: `s = "A man, a plan, a canal: Panama"`
After cleaning: `"amanaplanacanalpanama"`

```
left=0  'a'
right=19 'a'  → match, left++, right--
left=1  'm'
right=18 'm'  → match, left++, right--
left=2  'a'
right=17 'a'  → match ...
...continues matching symmetrically...
left=9  'a'
right=10 'n'  → 'a' != 'n' ... 

Wait: "amanaplanacanalpanama"
       0123456789012345678901
Let me recount:
a-m-a-n-a-p-l-a-n-a-c-a-n-a-l-p-a-n-a-m-a
0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20

left=0 'a', right=20 'a' → match
left=1 'm', right=19 'm' → match
left=2 'a', right=18 'a' → match
left=3 'n', right=17 'n' → match
...all match → return true
```

### Q&A
**Q: When do you use converging (left↔right) vs same-direction (slow/fast)?**
A: Converging — when the array is sorted and you're looking for a pair with a sum/product constraint.
Same-direction — when removing duplicates in-place, or one pointer tracks "valid prefix" and other scans ahead.

**Q: 3Sum — how does two pointers help?**
A: Fix one element with an outer loop (i), then run two pointers on the rest of the sorted array to find pairs summing to `-nums[i]`. O(n²) total — better than O(n³) brute force.

---

## 3. Fast & Slow Pointers

### Recognition signal
Linked list + cycle detection / find middle / kth from end, with O(1) space.
→ two pointers at different speeds; slow moves 1 step, fast moves 2.

### How to arrive at it
**Brute force:** store visited nodes in a HashSet — O(n) space.
**Spot the waste:** if there's a cycle, a fast pointer will "lap" a slow pointer inside the cycle — they must meet.
**Pattern:** slow=head, fast=head. Loop: slow=slow.next, fast=fast.next.next. If fast==slow → cycle.

### Dry run — LC 141: Linked List Cycle
Input: `1 → 2 → 3 → 4 → 2 (cycle back to node with value 2)`

```
Initial: slow=1, fast=1

Step 1: slow=2, fast=3
Step 2: slow=3, fast=2  (fast went 3→4→2)
Step 3: slow=4, fast=4  (slow went 3→4, fast went 2→3→4)
slow == fast → CYCLE DETECTED ✓
```

### Q&A
**Q: Why does fast pointer always catch slow if there's a cycle?**
A: Once both are inside the cycle, fast gains 1 step on slow per iteration. If the cycle has length L, they meet within L steps. They can never skip past each other because the gain is exactly 1 per step.

**Q: LC 142 — how do you find WHERE the cycle starts?**
A: After they meet inside the cycle, reset one pointer to head. Move both 1 step at a time. They meet again exactly at the cycle start. (Math: meeting point is `dist_from_cycle_start` steps from start — resetting one pointer makes them converge at the entry.)

---

## 4. Merge Intervals

### Recognition signal
List of `[start, end]` pairs + merge/count overlaps/find gaps.
**Always sort by start first** — that's the only way to guarantee overlapping intervals are adjacent.

### How to arrive at it
**Brute force:** for every pair of intervals, check if they overlap. O(n²).
**Spot the waste:** if sorted by start, you only ever need to compare current interval with the last merged one.
**Pattern:** sort → iterate → if current.start <= last.end → merge (extend end). Else → push last, start new.

### Dry run — LC 56: Merge Intervals
Input: `[[1,3],[2,6],[8,10],[15,18]]`
Already sorted by start.

```
merged = [[1,3]]

[2,6]: current.start=2 <= last.end=3 → overlap → merge: last.end = max(3,6) = 6
merged = [[1,6]]

[8,10]: current.start=8 > last.end=6 → no overlap → push
merged = [[1,6],[8,10]]

[15,18]: current.start=15 > last.end=10 → no overlap → push
merged = [[1,6],[8,10],[15,18]]

Answer: [[1,6],[8,10],[15,18]]
```

### Q&A
**Q: Why sort by start?**
A: After sorting, any interval that overlaps the current one must have start ≤ current.end. Without sorting, an overlapping interval could be anywhere in the array.

**Q: Meeting Rooms II (LC 253) — how many rooms needed?**
A: Sort by start. Use a min-heap of end times. For each new meeting: if heap.top ≤ new.start, reuse that room (pop and push new end). Else add a new room (push). Heap size at end = answer. The heap tells you "what's the earliest any room becomes free."

---

## 5. Cyclic Sort

### Recognition signal
Array of n integers in range `[1,n]` or `[0,n-1]` + find missing/duplicate + O(1) space.
→ each number tells you exactly which index it SHOULD be at. Exploit that.

### How to arrive at it
**Brute force:** HashSet to find what's missing/duplicate. O(n) space.
**Spot the waste:** value `v` belongs at index `v-1`. We can sort the array in-place by swapping each element to its correct position — O(n) time, O(1) space.
**Pattern:** i=0. While `nums[i] != i+1`, swap `nums[i]` with `nums[nums[i]-1]`. If already there, i++. After sort, scan for `nums[i] != i+1` → that's the missing/duplicate.

### Dry run — LC 268: Missing Number
Input: `nums = [3, 0, 1]`, n=3, expected range [0,3]

Cyclic sort variant (place each num at index num, skip n since n has no slot):
```
i=0: nums[0]=3, correct slot=3, but 3==n → skip, i++
i=1: nums[1]=0, correct slot=0 → swap nums[1]↔nums[0] → [0,3,1], i stays
i=1: nums[1]=3, correct slot=3, 3==n → skip, i++
i=2: nums[2]=1, correct slot=1 → swap nums[2]↔nums[1] → [0,1,3], i stays
i=2: nums[2]=3, 3==n → skip, i++
i=3: done

Scan: nums[0]=0✓, nums[1]=1✓, nums[2]=3≠2 → missing = 2
```
(Or simpler for this specific problem: XOR all indices and values, or `n*(n+1)/2 - sum(nums)`)

### Q&A
**Q: Why is this O(n) and not O(n²)?**
A: Each swap puts at least one element in its correct position permanently. An element is only moved if it's wrong. Total swaps ≤ n, so the while loop overall runs O(n).

**Q: What if there are duplicates (LC 442)?**
A: When swapping, if `nums[i] == nums[nums[i]-1]` (the slot already has the right value), you can't swap anymore — skip. Then in the scan phase, `nums[i] != i+1` means `nums[i]` is a duplicate.

---

## 6. In-place Reversal of Linked List

### Recognition signal
Reverse a list (or a portion) with O(1) space.
→ re-wire `.next` pointers without extra storage.

### How to arrive at it
**Brute force:** collect all values in an array, write back reversed. O(n) space.
**Spot the waste:** you don't need a copy — you can change `.next` pointers in-place as you traverse.
**Pattern:** `prev=null, curr=head`. Each step: save `next=curr.next`, set `curr.next=prev`, advance `prev=curr, curr=next`.

### Dry run — LC 206: Reverse Linked List
Input: `1 → 2 → 3 → 4 → 5 → null`

```
prev=null, curr=1

Step 1: next=2, curr(1).next=null,  prev=1, curr=2
        null ← 1    2→3→4→5

Step 2: next=3, curr(2).next=1,     prev=2, curr=3
        null ← 1 ← 2    3→4→5

Step 3: next=4, curr(3).next=2,     prev=3, curr=4
        null ← 1 ← 2 ← 3    4→5

Step 4: next=5, curr(4).next=3,     prev=4, curr=5
Step 5: next=null, curr(5).next=4,  prev=5, curr=null

curr==null → stop. Return prev=5

5 → 4 → 3 → 2 → 1 → null ✓
```

### Q&A
**Q: Reverse only positions m to n (LC 92)?**
A: Walk to node m-1 (the "pre" node), then run the 3-pointer reversal for (n-m+1) steps. Re-attach: `pre.next = prev`, `start.next = curr`.

**Q: Reverse in k-groups (LC 25)?**
A: Count k nodes ahead. If fewer than k remain, stop. Reverse that group. Recursively process the rest. Stitch: `groupStart.next = reverseKGroup(curr, k)`.

---

## 7. Tree BFS

### Recognition signal
"Per level" — width, average per level, right side view, nearest distance.
→ process nodes layer by layer using a queue.

### How to arrive at it
**Brute force (DFS with level tracking):** works but awkward for "process all at one level" tasks.
**Pattern:** queue starts with root. Each "level loop": drain the entire current queue size, process those nodes, add their children. Queue naturally separates levels.

### Dry run — LC 102: Binary Tree Level Order Traversal
```
Tree:
      3
     / \
    9   20
       /  \
      15    7
```
```
queue=[3], result=[]

Level 1: size=1
  pop 3 → add to level → push 9, push 20
  level=[3], result=[[3]]

Level 2: size=2
  pop 9  → level=[9], no children
  pop 20 → level=[9,20], push 15, push 7
  result=[[3],[9,20]]

Level 3: size=2
  pop 15 → level=[15], no children
  pop 7  → level=[15,7], no children
  result=[[3],[9,20],[15,7]]

Answer: [[3],[9,20],[15,7]]
```

### Q&A
**Q: How is BFS different from DFS for trees?**
A: BFS uses a queue, explores level by level, finds the SHORTEST path to a node. DFS uses a stack (or recursion), goes deep first, better for "whole path" problems. For "nearest" or "level" questions → BFS. For "all paths" or "subtree" → DFS.

**Q: Rotting Oranges (LC 994) — why multi-source BFS?**
A: All rotten oranges rot their neighbors simultaneously. Start BFS with ALL rotten oranges already in the queue (not just one). Each BFS level = 1 minute. Answer = levels elapsed.

---

## 8. DFS (Tree & Graph)

### Recognition signal
Whole paths, subtrees, connected components, "visit everything reachable."
→ go deep, backtrack, mark visited.

### How to arrive at it
**When to pick DFS over BFS:** when you need to explore a full path before evaluating it, or when "what's in this subtree" matters more than "how far away."
**Pattern (tree):** recursive — base case at null node. Process current, recurse left, recurse right (pre/in/post-order depending on need).
**Pattern (graph):** visited set + recursive/iterative DFS from each unvisited node.

### Dry run — LC 200: Number of Islands
```
grid:
1 1 0 0 0
1 1 0 0 0
0 0 1 0 0
0 0 0 1 1
```
```
i=0,j=0: grid[0][0]=1 → DFS, mark visited
  DFS(0,0): mark '0', recurse right(0,1), down(1,0)
  DFS(0,1): mark '0', recurse right(0,2)=0 stop, down(1,1)
  DFS(1,0): mark '0', recurse right(1,1)
  DFS(1,1): mark '0', recurse right(1,2)=0, down(2,1)=0
  → entire top-left island visited. count=1

Scan continues: (0,1),(1,0),(1,1) already '0'
i=2,j=2: grid[2][2]=1 → DFS → just this cell. count=2
i=3,j=3: grid[3][3]=1 → DFS
  DFS(3,3): mark '0', recurse right(3,4)
  DFS(3,4): mark '0'
  → count=3

Answer: 3
```

### Q&A
**Q: Pre-order vs post-order — when does it matter?**
A: Pre-order — process node BEFORE children (e.g. serialize a tree, check path sum as you go down).
Post-order — process node AFTER children return (e.g. compute subtree height, diameter — you need children's answers first).

**Q: LC 124 Max Path Sum — why post-order?**
A: The max path through a node = node.val + best from left subtree + best from right subtree. You can't compute that until both children have returned their best contributions. Classic post-order.

---

## 9. Two Heaps

### Recognition signal
"Running median", split a stream into two halves where you need the boundary value fast.
→ one max-heap for the lower half, one min-heap for the upper half.

### How to arrive at it
**Brute force:** sort on every insert to find median. O(n log n) per query.
**Spot the waste:** you don't need full sort — you only need the top of each half.
**Pattern:** max-heap (lo) holds the smaller half. min-heap (hi) holds the larger half. Keep sizes balanced (|lo| - |hi| ≤ 1). Median = lo.top() or average of lo.top()+hi.top().

### Dry run — LC 295: Find Median from Data Stream
```
addNum(1): lo=[1] (max-heap), hi=[] (min-heap)
  median = 1.0

addNum(2): add to lo → lo=[2,1], rebalance: lo.size > hi.size+1 → move lo.top(2) to hi
  lo=[1], hi=[2]
  median = (1+2)/2 = 1.5

addNum(3): 3 > lo.top(1) → add to hi → hi=[2,3]
  hi.size > lo.size → move hi.top(2) to lo
  lo=[2,1], hi=[3]
  median = lo.top() = 2.0

addNum(0): 0 < lo.top(2) → add to lo → lo=[2,1,0]
  lo.size > hi.size+1 → move lo.top(2) to hi
  lo=[1,0], hi=[2,3]
  median = (1+2)/2 = 1.5
```

### Q&A
**Q: When do you move elements between heaps?**
A: After every insert: if new element goes to wrong side (compare with lo.top()), swap tops. Then rebalance sizes. Rule: lo.size == hi.size OR lo.size == hi.size+1.

**Q: Why max-heap for lo, min-heap for hi?**
A: lo.top() gives the LARGEST of the small half in O(1). hi.top() gives the SMALLEST of the large half in O(1). Those two tops are exactly what you need to compute the median.

---

## 10. Subsets / Backtracking

### Recognition signal
"Generate ALL ___" — subsets, permutations, combinations, arrangements.
→ build solutions incrementally, abandon a path ("backtrack") as soon as it violates a constraint.

### How to arrive at it
**Think of it as a decision tree:** at each step, you decide "include this element or not" (subsets) or "which element goes next" (permutations).
**Pattern:** recursive function with a `current` list. At each level, make a choice, recurse, then UN-make the choice (backtrack) to try the next option.

### Dry run — LC 78: Subsets
Input: `nums = [1,2,3]`

```
backtrack(start=0, current=[]):
  add [] to result

  choose 1: current=[1]
    backtrack(start=1, current=[1]):
      add [1]
      choose 2: current=[1,2]
        backtrack(start=2): add [1,2]
          choose 3: current=[1,2,3]
            backtrack(start=3): add [1,2,3], return
          backtrack: current=[1,2]
        current=[1]
      choose 3: current=[1,3]
        backtrack(start=3): add [1,3], return
      current=[1]
    return
  current=[]

  choose 2: current=[2]
    backtrack(start=2): add [2]
      choose 3: current=[2,3]
        backtrack(start=3): add [2,3], return
      current=[2]
    return
  current=[]

  choose 3: current=[3]
    backtrack(start=3): add [3], return
  current=[]

Result: [[], [1], [1,2], [1,2,3], [1,3], [2], [2,3], [3]]
```

### Q&A
**Q: Subsets vs Permutations — what changes in the code?**
A: Subsets: pass `start` index, only pick elements from `start` onwards (no reuse, order doesn't matter).
Permutations: no start index, use a `visited[]` array, pick any unused element at each step (order matters).

**Q: How do you handle duplicates (LC 90 Subsets II)?**
A: Sort first. In the loop, skip `nums[i] == nums[i-1]` when `i > start` — this avoids generating the same subset twice from duplicate elements.

---

## 11. Modified Binary Search

### Recognition signal
Sorted/rotated array + find/first/last/peak, or "minimize the maximum value."
→ each step, eliminate half the search space by determining which side the answer is on.

### How to arrive at it
**Brute force:** linear scan O(n).
**Pattern:** left=0, right=n-1. Each iteration: mid=(left+right)//2. Compare mid to determine which half to discard. Key discipline: always ask "can I eliminate this half entirely?"

### Dry run — LC 33: Search in Rotated Sorted Array
Input: `nums=[4,5,6,7,0,1,2]`, target=0

```
left=0, right=6

mid=3, nums[3]=7
  Left half [4,5,6,7] is sorted (nums[left]=4 ≤ nums[mid]=7)
  target=0 NOT in [4,7] → search right half
  left=4

left=4, right=6, mid=5, nums[5]=1
  Left half [0,1] is sorted (nums[4]=0 ≤ nums[5]=1)
  target=0 IN [0,1] → search left half
  right=4

left=4, right=4, mid=4, nums[4]=0 == target → return 4 ✓
```

### Q&A
**Q: How do you know which half is sorted in a rotated array?**
A: If `nums[left] ≤ nums[mid]`, the left half is sorted (the rotation point is on the right side). Otherwise, the right half is sorted. Once you know which half is sorted, check if target falls in that range.

**Q: "Search the answer" pattern (LC 875 Koko, LC 410)?**
A: Binary search on the *answer value*, not an array index. Define a feasibility function `canDo(k)`. Find the smallest k where `canDo(k)` is true. `lo=min_possible, hi=max_possible`, shrink toward the answer.

---

## 12. Bitwise XOR

### Recognition signal
Find the lone element among pairs, bit counting, "without using +/extra space."
→ XOR: `a^a=0`, `a^0=a`, commutative+associative → all pairs cancel, lone element survives.

### How to arrive at it
**Brute force:** sort and scan pairs, or use HashSet. O(n) space.
**Spot the waste:** pairs cancel each other. XOR everything — pairs zero out, the unpaired number remains.
**Pattern:** `result = 0; for num in nums: result ^= num; return result`

### Dry run — LC 136: Single Number
Input: `[4, 1, 2, 1, 2]`

```
result = 0
^ 4 → 0100
^ 1 → 0101
^ 2 → 0111
^ 1 → 0110  (1 cancelled)
^ 2 → 0100  (2 cancelled)

result = 4 (binary 0100) ✓
```

### Q&A
**Q: Why does XOR work here?**
A: `x ^ x = 0` (same number XORed with itself = 0). `x ^ 0 = x`. Since all pairs cancel to 0, only the single number XORed with 0 remains.

**Q: LC 260 — two single numbers. XOR gives you `a^b`. How do you separate them?**
A: Find any set bit in `a^b` (use `xor & (-xor)` to isolate lowest set bit). That bit differs between a and b. Partition all numbers by whether they have that bit set → XOR each partition → one gives `a`, other gives `b`.

---

## 13. Top K Elements

### Recognition signal
K largest/smallest/most-frequent/closest — a ranked slice, not full sort.
→ maintain a heap of size K instead of sorting everything.

### How to arrive at it
**Brute force:** sort all n elements, take last/first k. O(n log n).
**Spot the waste:** you don't need full order — just the top k. A min-heap of size k keeps the k largest seen so far. If a new element beats the smallest in the heap, swap it in.
**Pattern:** push each element to a min-heap; if heap.size > k, pop the smallest. After n elements, heap contains the k largest. O(n log k).

### Dry run — LC 215: Kth Largest Element (k=2)
Input: `nums=[3,2,1,5,6,4]`, k=2

```
min-heap (size ≤ k=2):

push 3: heap=[3]
push 2: heap=[2,3]
push 1: heap size=2, 1 < heap.top(2) → skip (or push then pop min)
  push 1 → [1,3,2], pop min(1) → heap=[2,3]
push 5: push → [2,3,5], pop min(2) → heap=[3,5]
push 6: push → [3,5,6], pop min(3) → heap=[5,6]
push 4: push → [4,5,6], pop min(4) → heap=[5,6]

heap.top() = 5 = 2nd largest ✓
```

### Q&A
**Q: Why min-heap for K largest (not max-heap)?**
A: Min-heap lets you quickly check if a new element is larger than the current minimum of the "top k" group. If yes, evict the smallest and add the new one. Max-heap would require O(n) to find the minimum.

**Q: Top K Frequent (LC 347) — how do you apply this?**
A: Count frequencies with a HashMap. Then push (frequency, element) pairs into a min-heap of size k. After processing all, heap contains k most frequent. O(n log k).

---

## 14. K-way Merge

### Recognition signal
Merge K sorted lists, or find Kth smallest across multiple sorted sources.
→ always need the current minimum across K lists — use a min-heap.

### How to arrive at it
**Brute force:** dump everything into one array, sort. O(n·k log(n·k)).
**Spot the waste:** lists are already sorted — at any point the next element must come from the HEAD of one of the k lists. A heap on k heads gives you the minimum in O(log k).
**Pattern:** push the first element of each list into a min-heap. Each step: pop the min (add to result), push the NEXT element from that same list.

### Dry run — LC 23: Merge k Sorted Lists
Input: `[1→4→5], [1→3→4], [2→6]`

```
min-heap with (value, listIdx, node):
Initial push: (1,0,node1), (1,1,node1'), (2,2,node2)
heap = [(1,0), (1,1), (2,2)]

pop (1,0): result=[1], push next from list0 → (4,0)
heap = [(1,1),(2,2),(4,0)]

pop (1,1): result=[1,1], push next from list1 → (3,1)
heap = [(2,2),(3,1),(4,0)]

pop (2,2): result=[1,1,2], push next from list2 → (6,2)
heap = [(3,1),(4,0),(6,2)]

pop (3,1): result=[1,1,2,3], push (4,1)
heap = [(4,0),(4,1),(6,2)]

pop (4,0): result=[1,1,2,3,4], push (5,0)
pop (4,1): result=[1,1,2,3,4,4], list1 exhausted
pop (5,0): result=[1,1,2,3,4,4,5], list0 exhausted
pop (6,2): result=[1,1,2,3,4,4,5,6], list2 exhausted

Answer: 1→1→2→3→4→4→5→6 ✓
```

### Q&A
**Q: Why is this O(n log k) not O(n log n)?**
A: The heap never has more than k elements. Each pop+push is O(log k). Total n elements processed → O(n log k). Since k ≤ n, this is always at least as good as O(n log n).

**Q: Kth Smallest in Sorted Matrix (LC 378) — same idea?**
A: Yes. Treat each row as a sorted list. Push (value, row, col) for col 0 of every row. Pop k times total; when you pop (val, r, c), push (matrix[r][c+1], r, c+1) if c+1 < n. The kth pop is your answer.

---

## 15. 0/1 Knapsack & DP

### Recognition signal
"Can we make exactly sum X from a subset?", pick elements under a budget, overlapping subproblems.
→ decision at each element: take it or leave it. Result depends on previous decisions → DP.

### How to arrive at it
**Brute force:** try all subsets. O(2^n).
**Spot the waste:** you recompute the same (remaining capacity, remaining items) subproblems repeatedly.
**Pattern:** `dp[j]` = can we reach sum j? For each item with weight w: iterate j from capacity DOWN to w (to avoid reusing same item): `dp[j] = dp[j] || dp[j-w]`.

### Dry run — LC 416: Partition Equal Subset Sum
Input: `nums=[1,5,11,5]`, total=22, target=11

```
dp = [T, F, F, F, F, F, F, F, F, F, F, F]  (index 0..11)
     dp[0]=true (empty subset sums to 0)

Process 1:
  j=11 down to 1:
  dp[1] = dp[1] || dp[1-1] = F||T = T
  dp = [T,T,F,F,F,F,F,F,F,F,F,F]

Process 5:
  j=11..5:
  dp[6] = dp[6]||dp[1] = T
  dp[5] = dp[5]||dp[0] = T
  dp = [T,T,F,F,F,T,T,F,F,F,F,F]

Process 11:
  j=11: dp[11]=dp[11]||dp[0]=T ← FOUND!
  dp[11] = true → return true ✓
```

### Q&A
**Q: Why iterate j backwards (high to low)?**
A: Because each item can only be used ONCE (0/1 knapsack). Iterating down ensures that when we update `dp[j]`, we're reading `dp[j-w]` from the PREVIOUS item's state — not the current item already placed once this round. Iterating forward would allow using the same item multiple times (unbounded knapsack, like Coin Change).

**Q: Coin Change (LC 322) vs Partition Sum — what's different?**
A: Coin Change is UNBOUNDED (each coin reusable). So iterate j FORWARD: `dp[j] = min(dp[j], dp[j-coin]+1)`. Partition Sum is 0/1 (each number used once) → iterate backward.

---

## 16. Topological Sort

### Recognition signal
Tasks with prerequisites/dependencies, "is an ordering possible?", "give me the valid order."
→ dependency graph + process nodes that have no remaining dependencies first.

### How to arrive at it
**Brute force:** try all orderings, check each for validity. O(n!).
**Spot the waste:** only nodes with in-degree 0 can come first. Process them, reduce neighbors' in-degrees. Repeat.
**Pattern (Kahn's BFS):** build adjacency list + in-degree count. Queue all nodes with in-degree 0. Pop one, add to order, decrement neighbors' in-degrees; if any hit 0, add to queue. If total processed < n → cycle exists → impossible.

### Dry run — LC 207: Course Schedule
Input: `numCourses=4`, `prerequisites=[[1,0],[2,0],[3,1],[3,2]]`
(Edge [a,b] means: must take b before a)

```
Graph:
0 → 1, 0 → 2
1 → 3
2 → 3

in-degree: {0:0, 1:1, 2:1, 3:2}

Queue (in-degree 0): [0]
processed=0

Pop 0: processed=1, reduce neighbors 1 and 2:
  1: in-degree 1→0 → add to queue
  2: in-degree 1→0 → add to queue
Queue: [1, 2]

Pop 1: processed=2, reduce neighbor 3:
  3: in-degree 2→1
Queue: [2]

Pop 2: processed=3, reduce neighbor 3:
  3: in-degree 1→0 → add to queue
Queue: [3]

Pop 3: processed=4
Queue: []

processed(4) == numCourses(4) → NO CYCLE → return true ✓
Order: [0, 1, 2, 3] or [0, 2, 1, 3]
```

### Q&A
**Q: How does Kahn's algorithm detect a cycle?**
A: If a cycle exists, the nodes in the cycle never reach in-degree 0 (they all wait on each other). So the queue empties before processing all nodes. `processed < numCourses` → cycle.

**Q: DFS-based topo sort vs Kahn's BFS — when to use which?**
A: Kahn's BFS naturally detects cycles AND gives you the order. DFS topo sort (post-order, reverse the result) is cleaner for just getting an order when you know no cycle exists. For interviews, Kahn's is usually clearer to explain and implement correctly.

---

## Quick cheat-sheet: "which pattern?" decision tree

```
Problem mentions...                          → Pattern
─────────────────────────────────────────────────────────
contiguous subarray / of size k              → Sliding Window
sorted array + pair/triplet                  → Two Pointers
linked list + cycle / middle / O(1) space    → Fast & Slow Pointers
[start,end] intervals                        → Merge Intervals
n numbers in range [1,n], find missing/dup   → Cyclic Sort
reverse list / portion / in-place            → In-place Reversal
"per level" / nearest / multi-source         → Tree BFS
all paths / subtree / connected components   → DFS
running median / stream split                → Two Heaps
generate ALL subsets/permutations/combos     → Backtracking
sorted/rotated + find/minimize-max           → Modified Binary Search
single among pairs / bit count               → XOR
K largest/smallest/most-frequent             → Top K (heap)
merge K sorted sources / Kth across K lists  → K-way Merge
subset-sum / pick under budget               → 0/1 Knapsack DP
prerequisites / dependencies / ordering      → Topological Sort
```
