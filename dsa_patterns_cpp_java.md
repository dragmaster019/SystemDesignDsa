# The 16 DSA Patterns — A Deep Dive with Dry Runs (C++ & Java)

> A complete reference for getting back into Data Structures & Algorithms.
> Each pattern includes: the core intuition, how to *recognize* it in a problem,
> the mechanics, reusable **C++ and Java** templates, and a **full dry run** that
> traces every variable step-by-step on a concrete input.

**How to read a dry run:** each table row is one iteration of the main loop. Watch how the
state columns change — that mental model is the whole point. If you can predict the next row
before reading it, you understand the pattern.

**Language notes worth keeping in mind throughout:**
- **Heaps**: C++ `priority_queue<int>` is a **max-heap** by default; use
  `priority_queue<int, vector<int>, greater<int>>` for a min-heap. Java `PriorityQueue` is a
  **min-heap** by default; use `new PriorityQueue<>(Collections.reverseOrder())` for a max-heap.
  (No negation tricks needed — both languages give you real heaps.)
- **Integer overflow**: `(low + high) / 2` can overflow `int` in C++/Java. Always write
  `low + (high - low) / 2`.
- **Maps**: C++ `unordered_map` / Java `HashMap` replace dictionaries; both default-construct
  values on first access patterns shown below.

---

## Table of Contents

1. [Sliding Window](#1-sliding-window)
2. [Two Pointers](#2-two-pointers)
3. [Fast & Slow Pointers](#3-fast--slow-pointers)
4. [Merge Intervals](#4-merge-intervals)
5. [Cyclic Sort](#5-cyclic-sort)
6. [In-place Reversal of Linked List](#6-in-place-reversal-of-linked-list)
7. [Tree Breadth-First Search](#7-tree-breadth-first-search)
8. [Depth-First Search](#8-depth-first-search)
9. [Two Heaps](#9-two-heaps)
10. [Subsets / Backtracking](#10-subsets--backtracking)
11. [Modified Binary Search](#11-modified-binary-search)
12. [Bitwise XOR](#12-bitwise-xor)
13. [Top K Elements](#13-top-k-elements)
14. [K-way Merge](#14-k-way-merge)
15. [0/1 Knapsack (DP)](#15-01-knapsack-dp)
16. [Topological Sort](#16-topological-sort)

---

## 1. Sliding Window

### The intuition
You have a linear structure (array/string) and you care about a **contiguous block** of it.
The naive approach recomputes the answer for every block from scratch — that's O(n·k) or O(n²).
The window trick says: when the block slides one step right, you *added one element and removed
one element*. So instead of recomputing, you **update incrementally**. That single insight turns
O(n²) into O(n).

There are two flavors:
- **Fixed window** — the size `k` never changes. Both edges move together.
- **Variable window** — the size grows and shrinks based on a condition (longest substring with
  at most K distinct chars, smallest subarray with sum ≥ target, etc.). The right edge expands;
  the left edge contracts only when the window becomes "invalid."

### How to recognize it
- "contiguous subarray / substring"
- "of size k" → fixed window
- "longest / shortest / maximum / minimum ... such that [condition]" → variable window
- The brute force is "try every subarray."

### Code template (variable window — longest substring with K distinct chars)

**C++**
```cpp
int longestSubstringKDistinct(const string& s, int k) {
    unordered_map<char,int> freq;
    int windowStart = 0, maxLen = 0;
    for (int windowEnd = 0; windowEnd < (int)s.size(); ++windowEnd) {
        char rightChar = s[windowEnd];
        freq[rightChar]++;                          // expand window
        while ((int)freq.size() > k) {              // invalid → shrink from left
            char leftChar = s[windowStart];
            if (--freq[leftChar] == 0) freq.erase(leftChar);
            windowStart++;
        }
        maxLen = max(maxLen, windowEnd - windowStart + 1);
    }
    return maxLen;
}
```

**Java**
```java
int longestSubstringKDistinct(String s, int k) {
    Map<Character,Integer> freq = new HashMap<>();
    int windowStart = 0, maxLen = 0;
    for (int windowEnd = 0; windowEnd < s.length(); windowEnd++) {
        char rightChar = s.charAt(windowEnd);
        freq.put(rightChar, freq.getOrDefault(rightChar, 0) + 1);   // expand
        while (freq.size() > k) {                                   // shrink from left
            char leftChar = s.charAt(windowStart);
            freq.put(leftChar, freq.get(leftChar) - 1);
            if (freq.get(leftChar) == 0) freq.remove(leftChar);
            windowStart++;
        }
        maxLen = Math.max(maxLen, windowEnd - windowStart + 1);
    }
    return maxLen;
}
```

### Dry run
**Problem:** longest substring with at most `k = 2` distinct characters.
**Input:** `s = "araaci"`

| end | char in | freq map | distinct > 2? | shrink action | start | window | len | maxLen |
|----|--------|-----------|---------------|---------------|-------|--------|-----|---------|
| 0 | a | {a:1} | no | — | 0 | "a" | 1 | 1 |
| 1 | r | {a:1, r:1} | no | — | 0 | "ar" | 2 | 2 |
| 2 | a | {a:2, r:1} | no | — | 0 | "ara" | 3 | 3 |
| 3 | a | {a:3, r:1} | no | — | 0 | "araa" | 4 | 4 |
| 4 | c | {a:3, r:1, c:1} | **yes (3)** | erase s[0]=a → {a:2,r:1,c:1} still 3; erase s[1]=r → {a:2,c:1}; start=2 | 2 | "aac" | 3 | 4 |
| 5 | i | {a:2, c:1, i:1} | **yes (3)** | erase s[2]=a → {a:1,c:1,i:1} still 3; erase s[3]=a → {c:1,i:1}; start=4 | 4 | "ci" | 2 | 4 |

**Answer: 4** (the substring `"araa"`).

Notice at `end=4` the inner `while` ran twice — it kept shrinking until the window was valid again.
That's the key: the `while` is *not* an `if`. The window can shrink by more than one step.

### Complexity
- Time: **O(n)** — each element is added once and removed at most once, so the total work of both
  pointers is bounded by 2n.
- Space: **O(k)** for the frequency map (at most k+1 distinct keys).

### Common variants
- Max sum subarray of size k (fixed window)
- Longest substring without repeating characters
- Minimum window substring (hardest — shrink while window *contains* the target)
- Permutation in a string / find all anagrams

---

## 2. Two Pointers

### The intuition
When data is **sorted** (or can be sorted), two indices moving toward each other let you make a
decision at every step that eliminates possibilities. Compare the two ends; the comparison tells
you *which pointer to move* without ever needing to check the combinations you skipped. The sorted
order guarantees you never miss a valid answer.

Three sub-shapes:
- **Converging** (start + end moving inward) — pair-with-target-sum, valid palindrome, container with most water.
- **Same direction / fast-slow on arrays** — remove duplicates, move zeros, partitioning.
- **Fixed + roaming** — for triplets: fix one element, two-pointer the rest.

### How to recognize it
- The input is sorted, or sorting it doesn't lose information you need.
- You're looking for a pair / triplet / subsequence that meets a numeric condition.
- Brute force is a nested loop comparing pairs → O(n²). Two pointers collapses it to O(n).

### Code template (pair with target sum)

**C++**
```cpp
vector<int> pairWithTargetSum(const vector<int>& arr, int target) {
    int left = 0, right = (int)arr.size() - 1;
    while (left < right) {
        int sum = arr[left] + arr[right];
        if (sum == target) return {left, right};
        if (sum < target) left++;       // need a bigger sum
        else              right--;      // need a smaller sum
    }
    return {-1, -1};
}
```

**Java**
```java
int[] pairWithTargetSum(int[] arr, int target) {
    int left = 0, right = arr.length - 1;
    while (left < right) {
        int sum = arr[left] + arr[right];
        if (sum == target) return new int[]{left, right};
        if (sum < target) left++;       // need a bigger sum
        else              right--;      // need a smaller sum
    }
    return new int[]{-1, -1};
}
```

### Dry run
**Problem:** find indices of a pair summing to `target = 6`.
**Input:** `arr = [1, 2, 3, 4, 6]` (already sorted)

| left | right | arr[left] | arr[right] | sum | vs target | action |
|------|-------|-----------|-----------|-----|-----------|--------|
| 0 | 4 | 1 | 6 | 7 | 7 > 6 | sum too big → right-- |
| 0 | 3 | 1 | 4 | 5 | 5 < 6 | sum too small → left++ |
| 1 | 3 | 2 | 4 | 6 | 6 == 6 | **found! return [1, 3]** |

**Answer: [1, 3]**

Why this is correct and not just lucky: at the first step, `1 + 6 = 7 > 6`. Since the array is
sorted, `6` paired with *anything* to its left except index 0 would be even larger — so `6` can
never be part of a valid pair. We safely discard it forever by moving `right` left. Each move
permanently eliminates one element from consideration → O(n).

### Dry run #2 (triplets — 3Sum to zero)
**Input:** `arr = [-3, -1, 0, 1, 2]`, looking for triplets summing to 0.
Outer loop fixes `arr[i]`, two pointers scan `i+1 … n-1` for `-arr[i]`.

| i | fixed | need | left | right | sum of 3 | action |
|---|-------|------|------|-------|----------|--------|
| 0 | -3 | 3 | 1 | 4 | -3+-1+2 = -2 | < 0 → left++ |
| 0 | -3 | 3 | 2 | 4 | -3+0+2 = -1 | < 0 → left++ |
| 0 | -3 | 3 | 3 | 4 | -3+1+2 = 0 | **found [-3,1,2]**; left++, right-- |
| 1 | -1 | 1 | 2 | 4 | -1+0+2 = 1 | > 0 → right-- |
| 1 | -1 | 1 | 2 | 3 | -1+0+1 = 0 | **found [-1,0,1]**; left++, right-- |

**Answer: [[-3,1,2], [-1,0,1]]**

### Complexity
- Pair: Time **O(n)**, Space **O(1)** (if pre-sorted). With sorting: O(n log n).
- Triplets: **O(n²)** — outer loop n times, inner two-pointer O(n) each.

### Common variants
- Remove duplicates in place / move zeros (same-direction)
- Squaring a sorted array (converging, fill result from the back)
- Dutch national flag (three-way partition)
- Container with most water

---

## 3. Fast & Slow Pointers

### The intuition
Also called **Floyd's cycle detection** or the "tortoise and hare." Two pointers traverse the same
linked list at **different speeds** (slow +1, fast +2). The magic: if there's a cycle, the fast
pointer will eventually lap the slow one and they'll collide *inside* the loop. If there's no cycle,
fast just reaches the end. The speed difference is what guarantees a meeting.

A second superpower: when fast reaches the end, slow is exactly at the **middle** — because slow
travels half as far. Many list problems (palindrome, reorder) start by finding the middle this way.

### How to recognize it
- Linked list + "does it have a cycle?" / "where does the cycle start?"
- "find the middle" / "find the kth from the end" in a single pass with O(1) memory.
- Number-theory disguises: "happy number" (the sequence of digit-square-sums forms a cycle).

### Code template (cycle detection + finding start)

**C++**
```cpp
ListNode* findCycleStart(ListNode* head) {
    ListNode *slow = head, *fast = head;
    bool hasCycle = false;
    // Phase 1: detect a meeting point inside the cycle
    while (fast && fast->next) {
        slow = slow->next;
        fast = fast->next->next;
        if (slow == fast) { hasCycle = true; break; }
    }
    if (!hasCycle) return nullptr;
    // Phase 2: reset one pointer to head; move both one step until they meet
    ListNode* ptr = head;
    while (ptr != slow) {
        ptr  = ptr->next;
        slow = slow->next;
    }
    return ptr;     // the cycle's entry node
}
```

**Java**
```java
ListNode findCycleStart(ListNode head) {
    ListNode slow = head, fast = head;
    boolean hasCycle = false;
    // Phase 1: detect a meeting point inside the cycle
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) { hasCycle = true; break; }
    }
    if (!hasCycle) return null;
    // Phase 2: reset one pointer to head; move both one step until they meet
    ListNode ptr = head;
    while (ptr != slow) {
        ptr  = ptr.next;
        slow = slow.next;
    }
    return ptr;     // the cycle's entry node
}
```

### Dry run
**List:** `1 → 2 → 3 → 4 → 5 → 6 → 3` (the tail `6` points back to node `3`, forming a cycle).
Nodes are referenced by value here for readability.

**Phase 1 — detect the meeting:**

| step | slow at | fast at | met? |
|------|---------|---------|------|
| start | 1 | 1 | no |
| 1 | 2 | 3 | no |
| 2 | 3 | 5 | no |
| 3 | 4 | 3 (5→6→3) | no |
| 4 | 5 | 5 (3→4→5) | **yes — meet at node 5** |

**Phase 2 — find cycle start:** reset `ptr` to head (node 1). Move both one step at a time.

| step | ptr at | slow at | equal? |
|------|--------|---------|--------|
| start | 1 | 5 | no |
| 1 | 2 | 6 | no |
| 2 | 3 | 3 | **yes — cycle starts at node 3** |

**Answer: cycle begins at node 3.**

Why Phase 2 works (the elegant part): let the distance from head to cycle-start be `a`, and the
meeting point be `b` steps into the cycle. The math works out so that `a` (distance from head to
start) equals the remaining distance from the meeting point back to the start. So two pointers
moving at equal speed — one from head, one from the meeting point — collide exactly at the entry.

### Complexity
- Time: **O(n)** — phase 1 is at most ~n steps, phase 2 at most n.
- Space: **O(1)** — two pointers, nothing else.

### Common variants
- Detect cycle (just phase 1, return true/false)
- Find middle of linked list
- Palindrome linked list (find middle → reverse second half → compare)
- Happy number (treat digit-square-sum as "next" pointer)
- Find duplicate number in array (indices as links — sneaky but powerful)

---

## 4. Merge Intervals

### The intuition
Intervals are pairs `[start, end]`. Problems ask you to merge overlapping ones, insert a new one,
find intersections, or count overlaps. The unlock is almost always: **sort by start time first.**
Once sorted, you only ever compare each interval to the *last one you kept*. If they overlap, you
extend; if not, you start a fresh interval. You never have to look backward more than one step.

Two intervals `A` and `B` (with `A.start ≤ B.start` after sorting) overlap when `B.start ≤ A.end`.
That single comparison is the heart of the whole pattern.

### How to recognize it
- The input is a list of `[start, end]` pairs (times, ranges, segments).
- "merge", "overlap", "intersection", "conflicting", "minimum rooms/platforms".

### Code template (merge overlapping)

**C++**
```cpp
vector<vector<int>> merge(vector<vector<int>>& intervals) {
    sort(intervals.begin(), intervals.end());           // sort by start (then end)
    vector<vector<int>> merged;
    merged.push_back(intervals[0]);
    for (int i = 1; i < (int)intervals.size(); ++i) {
        int start = intervals[i][0], end = intervals[i][1];
        if (start <= merged.back()[1])                   // overlap → extend
            merged.back()[1] = max(merged.back()[1], end);
        else                                             // disjoint → new interval
            merged.push_back({start, end});
    }
    return merged;
}
```

**Java**
```java
int[][] merge(int[][] intervals) {
    Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));  // sort by start
    List<int[]> merged = new ArrayList<>();
    merged.add(intervals[0]);
    for (int i = 1; i < intervals.length; i++) {
        int start = intervals[i][0], end = intervals[i][1];
        int[] last = merged.get(merged.size() - 1);
        if (start <= last[1])                            // overlap → extend
            last[1] = Math.max(last[1], end);
        else                                             // disjoint → new interval
            merged.add(new int[]{start, end});
    }
    return merged.toArray(new int[merged.size()][]);
}
```

### Dry run
**Input:** `[[1,4], [2,5], [7,9], [6,8]]`

**Step 0 — sort by start:** `[[1,4], [2,5], [6,8], [7,9]]`

| interval | merged before | start ≤ last.end? | action | merged after |
|----------|---------------|-------------------|--------|--------------|
| [1,4] | [] | — | seed | [[1,4]] |
| [2,5] | [[1,4]] | 2 ≤ 4 ✓ | extend end → max(4,5)=5 | [[1,5]] |
| [6,8] | [[1,5]] | 6 ≤ 5 ✗ | disjoint, append | [[1,5],[6,8]] |
| [7,9] | [[1,5],[6,8]] | 7 ≤ 8 ✓ | extend end → max(8,9)=9 | [[1,5],[6,9]] |

**Answer: [[1,5], [6,9]]**

The reason sorting is mandatory: without it, `[6,8]` and `[7,9]` might be processed before `[1,4]`
and you'd have to re-scan. Sorting guarantees that once you "close" an interval (move past it), no
future interval can ever reopen it — because every future start is ≥ the current one.

### Complexity
- Time: **O(n log n)** — dominated by the sort. The merge pass itself is O(n).
- Space: **O(n)** for the output list (O(log n) to O(n) for the sort depending on implementation).

### Common variants
- Insert interval into a sorted, non-overlapping list (three phases: before / merge / after)
- Interval intersection of two lists
- Minimum meeting rooms (sort starts and ends separately, sweep line)
- Employee free time

---

## 5. Cyclic Sort

### The intuition
A very specific but very powerful trick. **When an array contains `n` numbers drawn from the range
`1..n` (or `0..n-1`)**, every value has a *natural home index*: value `v` belongs at index `v-1`.
So you can sort in **O(n) time and O(1) space** — beating comparison sorts — by simply walking the
array and swapping each number to where it belongs. After this, any index whose value is "wrong"
immediately reveals a missing or duplicated number.

You're not really sorting for sorting's sake — you're using the index-as-address property to find
missing/duplicate/corrupt values without extra memory.

### How to recognize it
- Array of `n` elements with values in a bounded range `[1, n]` or `[0, n-1]`.
- "find the missing number / all missing numbers / the duplicate / the corrupt pair."
- The interviewer hints at O(1) extra space.

### Code template

**C++**
```cpp
void cyclicSort(vector<int>& nums) {
    int i = 0;
    while (i < (int)nums.size()) {
        int correct = nums[i] - 1;                 // value v → index v-1
        if (nums[i] != nums[correct])
            swap(nums[i], nums[correct]);          // send to its home
        else
            i++;                                   // already placed → advance
    }
}
```

**Java**
```java
void cyclicSort(int[] nums) {
    int i = 0;
    while (i < nums.length) {
        int correct = nums[i] - 1;                 // value v → index v-1
        if (nums[i] != nums[correct]) {
            int tmp = nums[i];                     // send to its home
            nums[i] = nums[correct];
            nums[correct] = tmp;
        } else {
            i++;                                   // already placed → advance
        }
    }
}
```

### Dry run
**Input:** `nums = [3, 1, 5, 4, 2]` (values 1..5, n=5)

We only advance `i` when the current element is already home. Otherwise we swap and **re-check the
same `i`** (because the swapped-in value also needs a home).

| i | nums | nums[i] | target idx (v-1) | nums[target] | placed? | action |
|---|------|---------|------------------|--------------|---------|--------|
| 0 | [3,1,5,4,2] | 3 | 2 | 5 | no | swap idx0↔idx2 |
| 0 | [5,1,3,4,2] | 5 | 4 | 2 | no | swap idx0↔idx4 |
| 0 | [2,1,3,4,5] | 2 | 1 | 1 | no | swap idx0↔idx1 |
| 0 | [1,2,3,4,5] | 1 | 0 | 1 | yes | i++ |
| 1 | [1,2,3,4,5] | 2 | 1 | 2 | yes | i++ |
| 2 | [1,2,3,4,5] | 3 | 2 | 3 | yes | i++ |
| 3 | [1,2,3,4,5] | 4 | 3 | 4 | yes | i++ |
| 4 | [1,2,3,4,5] | 5 | 4 | 5 | yes | i++ → done |

**Sorted: [1, 2, 3, 4, 5]**

Notice `i` stayed at 0 for *four rows* while it kept swapping incoming values into place. Even though
there's a `while` driving it, total swaps are bounded: each swap puts at least one number in its
final home, and a number never leaves home once placed → at most n swaps → **O(n)**.

### Applying it — find the missing number
**Input:** `nums = [4, 0, 3, 1]` (values 0..3, length 4 → values can be 0..n=4).
After cyclic sort (here value `v` → index `v`), scan for the first index where `nums[i] != i`.
Sorted result: `[0, 1, _, 3]` with `4` having nowhere to go (out of range, skipped).
Scanning: index 2 holds the wrong value → **missing number is 2**.

### Complexity
- Time: **O(n)** — at most n swaps plus n advances.
- Space: **O(1)** — fully in place.

### Common variants
- Find the missing number / find all missing numbers
- Find the duplicate number / find all duplicates
- Find the corrupt pair (one duplicated, one missing)
- Find the smallest missing positive integer

---

## 6. In-place Reversal of Linked List

### The intuition
Reversing a linked list with O(1) extra space means you **re-point each node's `next` backward** as
you walk forward. The trick is bookkeeping: before you overwrite `curr.next`, you must save it, or
you lose the rest of the list. Three pointers — `prev`, `curr`, `next` — leapfrog down the list,
flipping one arrow per step.

This generalizes: reverse a *sub-list* (positions p..q), reverse in *groups of k*, etc. The core
3-pointer dance never changes; you just manage the boundaries around it.

### How to recognize it
- "reverse a linked list" / "reverse between position m and n" / "reverse every k nodes."
- The constraint is O(1) space (otherwise you'd just use a stack or array).

### Code template (full reverse)

**C++**
```cpp
ListNode* reverse(ListNode* head) {
    ListNode* prev = nullptr;
    ListNode* curr = head;
    while (curr) {
        ListNode* nextNode = curr->next;   // 1. save the rest
        curr->next = prev;                 // 2. flip the arrow
        prev = curr;                       // 3. advance prev
        curr = nextNode;                   // 4. advance curr
    }
    return prev;                           // prev is the new head
}
```

**Java**
```java
ListNode reverse(ListNode head) {
    ListNode prev = null;
    ListNode curr = head;
    while (curr != null) {
        ListNode nextNode = curr.next;     // 1. save the rest
        curr.next = prev;                  // 2. flip the arrow
        prev = curr;                       // 3. advance prev
        curr = nextNode;                   // 4. advance curr
    }
    return prev;                           // prev is the new head
}
```

### Dry run
**List:** `1 → 2 → 3 → 4 → null`

| step | prev | curr | nextNode (saved) | after `curr.next = prev` | list so far (from prev) |
|------|------|------|-------------------|--------------------------|--------------------------|
| init | null | 1 | — | — | — |
| 1 | null | 1 | 2 | 1 → null | 1 → null |
| 2 | 1 | 2 | 3 | 2 → 1 | 2 → 1 → null |
| 3 | 2 | 3 | 4 | 3 → 2 | 3 → 2 → 1 → null |
| 4 | 3 | 4 | null | 4 → 3 | 4 → 3 → 2 → 1 → null |
| end | 4 | null | — | loop ends (curr is null) | **return prev = 4** |

**Reversed list: `4 → 3 → 2 → 1 → null`**

The single most common bug: forgetting to save `nextNode` first. If you do `curr.next = prev`
before saving, you've cut the bridge to the rest of the list and can never reach it again.

### Dry run #2 — reverse a sub-list (positions 2 to 4)
**List:** `1 → 2 → 3 → 4 → 5`, reverse positions **2..4** (the nodes `2,3,4`).

1. Walk to the node *before* position 2 → that's node `1` (call it `prevBoundary`).
2. Reverse exactly 3 nodes starting at `2` using the 3-pointer dance: `2→3→4` becomes `4→3→2`.
3. Reconnect: `prevBoundary.next` (node 1) → points to `4` (new sub-head);
   the original sub-head `2` → points to `5` (the node after the reversed section).

Result: `1 → 4 → 3 → 2 → 5`. The dance is identical; only the stitching at the two seams is extra.

### Complexity
- Time: **O(n)** — one pass.
- Space: **O(1)** — three pointers.

### Common variants
- Reverse a sub-list (m..n)
- Reverse every k-element group (reverse, reconnect, repeat)
- Rotate a list by k (find new tail, relink)
- Swap nodes in pairs

---

## 7. Tree Breadth-First Search

### The intuition
BFS explores a tree **level by level**, left to right, using a **queue** (FIFO). You enqueue the
root, then repeatedly dequeue a node and enqueue its children. The crucial trick for level-aware
problems: **record the queue's size at the start of each level** — that count tells you exactly how
many nodes belong to the current level, so you can process one level fully before touching the next.

Anything phrased in terms of "levels," "depth," "width," "nearest," or "layer" is a BFS signal.

### How to recognize it
- "level order traversal," "zigzag," "right side view," "level averages," "minimum depth."
- You need to process nodes in order of their distance from the root.

### Code template (level order)

**C++**
```cpp
vector<vector<int>> levelOrder(TreeNode* root) {
    vector<vector<int>> result;
    if (!root) return result;
    queue<TreeNode*> q;
    q.push(root);
    while (!q.empty()) {
        int levelSize = q.size();              // nodes in THIS level
        vector<int> level;
        for (int i = 0; i < levelSize; ++i) {
            TreeNode* node = q.front(); q.pop();
            level.push_back(node->val);
            if (node->left)  q.push(node->left);
            if (node->right) q.push(node->right);
        }
        result.push_back(level);
    }
    return result;
}
```

**Java**
```java
List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    while (!queue.isEmpty()) {
        int levelSize = queue.size();          // nodes in THIS level
        List<Integer> level = new ArrayList<>();
        for (int i = 0; i < levelSize; i++) {
            TreeNode node = queue.poll();
            level.add(node.val);
            if (node.left  != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        result.add(level);
    }
    return result;
}
```

### Dry run
**Tree:**
```
        1
       / \
      2   3
     / \    \
    4   5    6
```

We track the queue contents and the output after each level. `levelSize` at the top of the loop
fixes how many we pop for the current level.

| level | levelSize | queue at start | pop order | children enqueued | level list | result |
|-------|-----------|----------------|-----------|--------------------|------------|--------|
| 0 | 1 | [1] | 1 | 2, 3 | [1] | [[1]] |
| 1 | 2 | [2, 3] | 2, then 3 | 2→{4,5}, 3→{6} | [2, 3] | [[1],[2,3]] |
| 2 | 3 | [4, 5, 6] | 4, 5, 6 | none have children | [4, 5, 6] | [[1],[2,3],[4,5,6]] |
| — | — | [] (empty) | — | — | — | loop ends |

**Answer: [[1], [2, 3], [4, 5, 6]]**

Snapshot of the queue evolving: `[1]` → (pop 1, push 2,3) `[2,3]` → (pop 2, push 4,5) `[3,4,5]` →
(pop 3, push 6) `[4,5,6]` → pop all three, no children → `[]`. Capturing `levelSize` *before* the
inner loop is what stops level 1's processing from accidentally consuming level 2's nodes.

### Complexity
- Time: **O(n)** — every node enqueued and dequeued exactly once.
- Space: **O(n)** — in the worst case (a full last level) the queue holds ~n/2 nodes.

### Common variants
- Zigzag / spiral order (reverse every other level)
- Right side view (last node of each level) / left side view (first node)
- Level averages, level maximums
- Minimum depth (first leaf you reach), connect level-order siblings

---

## 8. Depth-First Search

### The intuition
DFS goes **deep before wide** — follow one path all the way down, then backtrack and try the next.
It's naturally **recursive** (the call stack *is* your stack), though you can use an explicit stack.
For trees, *when* you process a node relative to its children defines the traversal order:
- **Pre-order**: node → left → right (process before descending)
- **In-order**: left → node → right (for BSTs, yields sorted order)
- **Post-order**: left → right → node (process after children — needed for "bottom-up" answers)

For path problems, you carry state (remaining sum, current path) *down* the recursion and
**backtrack** (undo your choice) when returning, so siblings start clean.

### How to recognize it
- "path sum," "all root-to-leaf paths," "does a path exist," "tree diameter," "max path sum."
- Graph: "connected components," "number of islands," "cycle detection," "clone graph."
- You need to explore complete paths or full subtrees.

### Code template (root-to-leaf path sum)

**C++**
```cpp
bool hasPathSum(TreeNode* root, int target) {
    if (!root) return false;
    // leaf? check if this path hits the target exactly
    if (!root->left && !root->right)
        return target == root->val;
    int remaining = target - root->val;
    return hasPathSum(root->left,  remaining) ||
           hasPathSum(root->right, remaining);
}
```

**Java**
```java
boolean hasPathSum(TreeNode root, int target) {
    if (root == null) return false;
    // leaf? check if this path hits the target exactly
    if (root.left == null && root.right == null)
        return target == root.val;
    int remaining = target - root.val;
    return hasPathSum(root.left,  remaining) ||
           hasPathSum(root.right, remaining);
}
```

### Dry run
**Tree:**
```
        5
       / \
      4   8
     /   / \
    11  13  4
```
**Target = 22.** We descend, subtracting each node's value from the remaining target.
The trace below shows depth via indentation; `rem` is the remaining target on entry.

```
visit 5, rem=22   → not leaf, recurse left with rem=22-5=17
  visit 4, rem=17 → not leaf, recurse left with rem=17-4=13
    visit 11, rem=13 → LEAF. 13 == 11? No → returns false
    (4 has no right child) → 4 returns false
  (left of 5 returned false) → recurse right with rem=17
  visit 8, rem=17 → not leaf, recurse left with rem=17-8=9
    visit 13, rem=9 → LEAF. 9 == 13? No → returns false
    visit 4,  rem=9 → LEAF. 9 == 4?  No → returns false
  → 8 returns false
→ 5 returns false
```
**Answer: false** — no root-to-leaf path sums to 22.

Now trace **target = 27** quickly: `5 → 8 → 4` gives `5+8+4 = 17`... not 27. `5 → 8 → 13 = 26`.
`5 → 4 → 11 = 20`. None equal 27 either → false. (Try target = 20: path `5→4→11` → **true**.)

The backtracking is implicit here: each recursive call gets its *own* `remaining` value as a
parameter, so returning from the left subtree automatically "restores" the state for the right
subtree. When you instead mutate a shared path container, you must explicitly pop the last element
on the way up.

### Complexity
- Time: **O(n)** — each node visited once (for path-collection variants that copy paths, add the
  cost of copying: up to O(n) per leaf → O(n·log n) balanced, O(n²) skewed).
- Space: **O(h)** — recursion stack depth equals tree height (O(log n) balanced, O(n) skewed).

### Common variants
- All paths for a target sum (collect, with explicit backtracking)
- Sum of path numbers, count paths that sum to K
- Tree diameter (post-order: longest left + longest right at each node)
- Max path sum (post-order, allow negative pruning)
- Graph DFS: number of islands, flood fill, connected components

---

## 9. Two Heaps

### The intuition
Sometimes you must repeatedly access the **median** or split a stream into a smaller half and a
larger half with instant access to the boundary. Keep two heaps:
- a **max-heap** holding the smaller half (its top = the largest of the small numbers),
- a **min-heap** holding the larger half (its top = the smallest of the large numbers).

Keep them **balanced** (sizes differ by at most 1). Then the median is either the top of the bigger
heap (odd count) or the average of the two tops (even count) — both **O(1)** to read, **O(log n)**
to insert.

Language note: C++ gives you a max-heap natively (`priority_queue<int>`) and a min-heap via
`priority_queue<int, vector<int>, greater<int>>`. Java's `PriorityQueue` is a min-heap by default;
use `Collections.reverseOrder()` for the max-heap. No value-negation hacks required.

### How to recognize it
- "median of a data stream / sliding window median."
- "schedule to maximize profit picking from two prioritized sets" (IPO).
- You need the middle element, or a dynamic split into two prioritized groups.

### Code template (median finder)

**C++**
```cpp
class MedianFinder {
    priority_queue<int> small;                               // max-heap: lower half
    priority_queue<int, vector<int>, greater<int>> large;    // min-heap: upper half
public:
    void addNum(int num) {
        small.push(num);
        large.push(small.top()); small.pop();      // move max of small into large
        if (large.size() > small.size()) {          // rebalance
            small.push(large.top()); large.pop();
        }
    }
    double findMedian() {
        if (small.size() > large.size()) return small.top();
        return (small.top() + large.top()) / 2.0;
    }
};
```

**Java**
```java
class MedianFinder {
    PriorityQueue<Integer> small = new PriorityQueue<>(Collections.reverseOrder()); // max-heap
    PriorityQueue<Integer> large = new PriorityQueue<>();                            // min-heap

    void addNum(int num) {
        small.offer(num);
        large.offer(small.poll());                  // move max of small into large
        if (large.size() > small.size())             // rebalance
            small.offer(large.poll());
    }
    double findMedian() {
        if (small.size() > large.size()) return small.peek();
        return (small.peek() + large.peek()) / 2.0;
    }
}
```

### Dry run
**Stream:** add `5, 15, 1, 3` and query the median after each.
`small` is a max-heap, `large` is a min-heap (both shown as their actual contents).

| add | step 1: push→small, move top→large | step 2: rebalance | small (max-heap) | large (min-heap) | median |
|-----|-------------------------------------|-------------------|------------------|------------------|--------|
| 5 | push 5 to small; move 5 to large | large bigger → move 5 back to small | [5] | [] | 5 |
| 15 | push 15 to small (top 15); move 15 to large | balanced (1,1) | [5] | [15] | (5+15)/2 = **10** |
| 1 | push 1 to small (top 5); move 5 to large | large bigger (1,2) → move 5 back | [5, 1] | [15] | top of small = **5** |
| 3 | push 3 to small (top 5); move 5 to large | balanced (2,2) | [3, 1] | [5, 15] | (3+5)/2 = **4** |

**Medians over time: 5, 10, 5, 4.**

Walk through the `add 1` row carefully — it shows why step 2 exists. After step 1 we had
`small=[1]`, `large=[5,15]` (sizes 1 and 2). The min-heap is too big, so we pop its smallest (5)
and push it onto `small`, giving `small=[5,1]`, `large=[15]` (sizes 2 and 1). Now the median is just
`small`'s top, 5. The invariant — *every number in `small` ≤ every number in `large`, sizes within 1*
— is what makes the median readable in O(1).

### Complexity
- Insert: **O(log n)** (a couple of heap pushes/pops).
- Find median: **O(1)** (peek the tops).
- Space: **O(n)**.

### Common variants
- Sliding window median (add/remove with lazy deletion)
- IPO / maximize capital (one heap by capital, one by profit)
- Next interval

---

## 10. Subsets / Backtracking

### The intuition
To generate **all** subsets, permutations, or combinations, you systematically build candidates and
explore every branching choice. Two equivalent mental models:
- **Iterative (BFS-style)**: start with `[[]]`. For each new number, take every subset built so far
  and create a copy that *also includes* the new number. The set doubles each round.
- **Recursive (backtracking/DFS)**: at each position, *choose* an element, recurse, then *un-choose*
  (backtrack) to try the next. The "un-choose" step is what lets one path's choices not pollute the
  next.

Handling duplicates: **sort first**, then when the same value appears again at the same recursion
level, skip it (otherwise you generate identical subsets).

### How to recognize it
- "generate all subsets / permutations / combinations."
- "all ways to ...", "every valid arrangement", "letter combinations."
- The answer is a *collection of collections*, and brute-force enumeration is expected.

### Code template (subsets, iterative)

**C++**
```cpp
vector<vector<int>> subsets(vector<int>& nums) {
    vector<vector<int>> result = {{}};
    for (int num : nums) {
        int n = result.size();
        for (int i = 0; i < n; ++i) {
            vector<int> subset = result[i];   // copy an existing subset
            subset.push_back(num);            // add the new element
            result.push_back(subset);
        }
    }
    return result;
}
```

**Java**
```java
List<List<Integer>> subsets(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    result.add(new ArrayList<>());
    for (int num : nums) {
        int n = result.size();
        for (int i = 0; i < n; i++) {
            List<Integer> subset = new ArrayList<>(result.get(i)); // copy
            subset.add(num);                                       // add new element
            result.add(subset);
        }
    }
    return result;
}
```

### Dry run
**Input:** `nums = [1, 2, 3]`

We process one number at a time. Each round, every existing subset spawns a copy with the new number.

| process | existing subsets | new subsets created (add the number) | result after round |
|---------|------------------|--------------------------------------|--------------------|
| start | — | — | [[]] |
| 1 | [[]] | [[1]] | [[], [1]] |
| 2 | [[], [1]] | [[2], [1,2]] | [[], [1], [2], [1,2]] |
| 3 | [[], [1], [2], [1,2]] | [[3], [1,3], [2,3], [1,2,3]] | [[], [1], [2], [1,2], [3], [1,3], [2,3], [1,2,3]] |

**Answer: 8 subsets** (= 2³, as expected — each element is independently in or out).

The doubling is visible: 1 → 2 → 4 → 8. That's why the time complexity is O(2ⁿ) — there are
genuinely 2ⁿ subsets, and you can't list them faster than they exist.

### Dry run #2 — permutations via backtracking
**Input:** `[1, 2, 3]`. We build a `path`, choosing an unused element at each level, undoing after.

```
path=[]                      choose 1
  path=[1]                   choose 2
    path=[1,2]               choose 3 → path=[1,2,3] record, backtrack
    path=[1] (undid 2)       choose 3
    path=[1,3]               choose 2 → path=[1,3,2] record, backtrack
  path=[] (undid 1)          choose 2
    ... yields [2,1,3], [2,3,1]
  path=[] (undid 2)          choose 3
    ... yields [3,1,2], [3,2,1]
```
**Answer: 6 permutations** (= 3!). Each `backtrack` (the "undid X" lines) restores `path` so the
next sibling choice starts from a clean state. In C++/Java this is literally a `path.pop_back()` /
`path.remove(path.size()-1)` after the recursive call returns.

### Complexity
- Subsets: **O(2ⁿ)** subsets, each up to length n → O(n·2ⁿ) to materialize.
- Permutations: **O(n!)** arrangements → O(n·n!).
- Space: O(n) recursion depth plus the output.

### Common variants
- Subsets with duplicates (sort + skip duplicates at a level)
- Permutations (with/without duplicates)
- Combination sum, letter combinations of a phone number
- Generate balanced parentheses, palindrome partitioning

---

## 11. Modified Binary Search

### The intuition
Plain binary search halves a sorted array each step: compare the middle to the target, throw away
the impossible half. The "modified" family applies the *same halving logic* to trickier setups:
rotated arrays, finding boundaries (first/last occurrence), peaks, or searching on an **answer space**
("what's the smallest capacity that works?"). The skill is identifying the **monotonic property** you
can binary-search over, and writing a clean condition that says which half to keep.

For rotated arrays the key observation: even after rotation, **at least one half is always sorted**.
Check which half is sorted, see if the target lies within its range, and discard accordingly.

**Overflow guard (C++/Java):** always compute `mid = low + (high - low) / 2`, never
`(low + high) / 2`, which can overflow `int` for large indices. (Python's unbounded ints make this
a non-issue there, but it matters here.)

### How to recognize it
- Sorted (or rotated-sorted) input + "find / search / first / last / smallest index such that..."
- O(log n) is expected, or the input is huge.
- "minimize the maximum" / "smallest value that satisfies a condition" → binary search on the answer.

### Code template (search in rotated sorted array)

**C++**
```cpp
int searchRotated(const vector<int>& arr, int target) {
    int low = 0, high = (int)arr.size() - 1;
    while (low <= high) {
        int mid = low + (high - low) / 2;
        if (arr[mid] == target) return mid;
        if (arr[low] <= arr[mid]) {                          // left half is sorted
            if (arr[low] <= target && target < arr[mid]) high = mid - 1;
            else                                          low  = mid + 1;
        } else {                                             // right half is sorted
            if (arr[mid] < target && target <= arr[high]) low  = mid + 1;
            else                                          high = mid - 1;
        }
    }
    return -1;
}
```

**Java**
```java
int searchRotated(int[] arr, int target) {
    int low = 0, high = arr.length - 1;
    while (low <= high) {
        int mid = low + (high - low) / 2;
        if (arr[mid] == target) return mid;
        if (arr[low] <= arr[mid]) {                          // left half is sorted
            if (arr[low] <= target && target < arr[mid]) high = mid - 1;
            else                                          low  = mid + 1;
        } else {                                             // right half is sorted
            if (arr[mid] < target && target <= arr[high]) low  = mid + 1;
            else                                          high = mid - 1;
        }
    }
    return -1;
}
```

### Dry run
**Input:** `arr = [4, 5, 6, 7, 0, 1, 2]` (a sorted array rotated), `target = 0`.

| low | high | mid | arr[mid] | which half sorted? | target in sorted half? | action |
|-----|------|-----|----------|--------------------|------------------------|--------|
| 0 | 6 | 3 | 7 | left (arr[0]=4 ≤ 7) | is 4 ≤ 0 < 7? No | discard left → low=4 |
| 4 | 6 | 5 | 1 | left (arr[4]=0 ≤ 1) | is 0 ≤ 0 < 1? **Yes** | discard right → high=4 |
| 4 | 4 | 4 | 0 | arr[mid]==target | — | **found at index 4** |

**Answer: index 4.**

Trace the logic at step 1: `mid=3` holds 7. The left half `[4,5,6,7]` is sorted (because
`arr[low]=4 ≤ arr[mid]=7`). Our target 0 is *not* inside `[4,7)`, so it can't be in the left half —
discard it. At step 2, `mid=5` holds 1; now the left part `[0,1]` is sorted and 0 *is* in `[0,1)`,
so we keep the left. Each step still halves the search space → **O(log n)** despite the rotation.

### Dry run #2 — first occurrence (find leftmost index of a value)
**Input:** `arr = [1, 2, 2, 2, 3]`, `target = 2`. We don't stop at the first match — we keep
searching left to find the *first* 2.

| low | high | mid | arr[mid] | vs target | action (on match, go LEFT) | best |
|-----|------|-----|----------|-----------|----------------------------|------|
| 0 | 4 | 2 | 2 | == | record idx 2, search left → high=1 | 2 |
| 0 | 1 | 0 | 1 | < 2 | low=1 | 2 |
| 1 | 1 | 1 | 2 | == | record idx 1, search left → high=0 | **1** |
| low>high → stop | | | | | | 1 |

**Answer: first occurrence at index 1.** The twist vs vanilla binary search: on a match you *record
and keep going left* instead of returning immediately.

### Complexity
- Time: **O(log n)** — the search space halves every iteration.
- Space: **O(1)** iterative.

### Common variants
- Find first/last position of an element (boundary search)
- Find peak element, find minimum in rotated array
- Ceiling/floor of a number, next greatest letter
- Search a 2D sorted matrix
- "Binary search on the answer": Koko eating bananas, ship within D days, split array largest sum

---

## 12. Bitwise XOR

### The intuition
XOR (`^`) has three properties that combine into surprising tricks:
1. `a ^ a = 0` (a number XORed with itself cancels),
2. `a ^ 0 = a` (XOR with zero is identity),
3. it's **commutative and associative** (order doesn't matter).

Put together: if you XOR a whole list where everything appears in pairs except one lone element,
all the pairs cancel to 0 and you're left with exactly the lone element. No extra memory, one pass.
Other bit tricks ride along: `x & (x-1)` clears the lowest set bit; `x & (-x)` isolates it.

### How to recognize it
- "every element appears twice except one — find it."
- "two elements appear once, the rest twice."
- "find the missing number using O(1) space," swap without a temp, count/flip bits.

### Code template (single number, and two singles)

**C++**
```cpp
int singleNumber(const vector<int>& nums) {
    int result = 0;
    for (int n : nums) result ^= n;     // pairs cancel, lone survivor remains
    return result;
}

vector<int> twoSingleNumbers(const vector<int>& nums) {
    int xorAll = 0;
    for (int n : nums) xorAll ^= n;             // = a ^ b (the two unique numbers)
    int rightmostBit = xorAll & (-xorAll);      // a bit where a and b differ
    int a = 0, b = 0;
    for (int n : nums) {
        if (n & rightmostBit) a ^= n;           // group 1: this bit set
        else                  b ^= n;           // group 2: this bit clear
    }
    return {a, b};
}
```

**Java**
```java
int singleNumber(int[] nums) {
    int result = 0;
    for (int n : nums) result ^= n;     // pairs cancel, lone survivor remains
    return result;
}

int[] twoSingleNumbers(int[] nums) {
    int xorAll = 0;
    for (int n : nums) xorAll ^= n;             // = a ^ b (the two unique numbers)
    int rightmostBit = xorAll & (-xorAll);      // a bit where a and b differ
    int a = 0, b = 0;
    for (int n : nums) {
        if ((n & rightmostBit) != 0) a ^= n;    // group 1: this bit set
        else                         b ^= n;    // group 2: this bit clear
    }
    return new int[]{a, b};
}
```

### Dry run
**Problem:** every number appears twice except one. **Input:** `nums = [4, 1, 2, 1, 2]`.
We accumulate XOR into `result`. (XOR is associative, so the order doesn't change the outcome —
here we go left to right.)

| step | n | result before | result after (result ^ n) |
|------|---|----------------|---------------------------|
| 1 | 4 | 0 | 0 ^ 4 = 4 |
| 2 | 1 | 4 | 4 ^ 1 = 5 |
| 3 | 2 | 5 | 5 ^ 2 = 7 |
| 4 | 1 | 7 | 7 ^ 1 = 6 |
| 5 | 2 | 6 | 6 ^ 2 = **4** |

**Answer: 4.**

Why it lands on 4: reorder the XOR (legal, since it's associative/commutative) as
`(1^1) ^ (2^2) ^ 4 = 0 ^ 0 ^ 4 = 4`. The duplicates annihilate each other regardless of where they
sit in the array, leaving only the unpaired number. In binary, step by step:
`0100 ^ 0001 = 0101 (5)`, `0101 ^ 0010 = 0111 (7)`, `0111 ^ 0001 = 0110 (6)`, `0110 ^ 0010 = 0100 (4)`.

### Complexity
- Time: **O(n)** — single pass.
- Space: **O(1)** — one accumulator.

### Common variants
- Find the two non-repeating numbers (partition by a differing bit, as above)
- Find the missing number (XOR all indices with all values)
- Complement of a base-10 number, flip an image
- Count set bits (Brian Kernighan: `x &= x-1` repeatedly)

---

## 13. Top K Elements

### The intuition
"Find the K largest / K smallest / K most frequent" sounds like it needs a full sort (O(n log n)),
but you only care about K items — so maintain a **heap of size K** and you get **O(n log K)**, which
is much better when K ≪ n. The counter-intuitive part: to find the K *largest*, you use a **min-heap**
of size K. The smallest of your "top K so far" sits at the heap's top; whenever a bigger element
arrives, you pop that smallest and push the newcomer. After one pass the heap holds exactly the K
largest.

### How to recognize it
- "K largest / K smallest / Kth largest / K most frequent / K closest."
- You need a *subset* ranked by some key, not a full ordering.

### Code template (Kth largest + top-K frequent)

**C++**
```cpp
int kthLargest(const vector<int>& nums, int k) {
    priority_queue<int, vector<int>, greater<int>> minHeap;  // min-heap of size k
    for (int n : nums) {
        minHeap.push(n);
        if ((int)minHeap.size() > k) minHeap.pop();          // drop smallest of top-k
    }
    return minHeap.top();                                     // heap top = Kth largest
}

vector<int> topKFrequent(const vector<int>& nums, int k) {
    unordered_map<int,int> freq;
    for (int n : nums) freq[n]++;
    // min-heap of (count, num)
    priority_queue<pair<int,int>, vector<pair<int,int>>, greater<>> heap;
    for (auto& [num, count] : freq) {
        heap.push({count, num});
        if ((int)heap.size() > k) heap.pop();                // drop least frequent
    }
    vector<int> result;
    while (!heap.empty()) { result.push_back(heap.top().second); heap.pop(); }
    return result;
}
```

**Java**
```java
int kthLargest(int[] nums, int k) {
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();   // min-heap of size k
    for (int n : nums) {
        minHeap.offer(n);
        if (minHeap.size() > k) minHeap.poll();               // drop smallest of top-k
    }
    return minHeap.peek();                                     // heap top = Kth largest
}

int[] topKFrequent(int[] nums, int k) {
    Map<Integer,Integer> freq = new HashMap<>();
    for (int n : nums) freq.put(n, freq.getOrDefault(n, 0) + 1);
    // min-heap ordered by frequency: {count, num}
    PriorityQueue<int[]> heap = new PriorityQueue<>((a, b) -> a[0] - b[0]);
    for (Map.Entry<Integer,Integer> e : freq.entrySet()) {
        heap.offer(new int[]{e.getValue(), e.getKey()});
        if (heap.size() > k) heap.poll();                     // drop least frequent
    }
    int[] result = new int[heap.size()];
    for (int i = 0; !heap.isEmpty(); i++) result[i] = heap.poll()[1];
    return result;
}
```

### Dry run
**Problem:** find the **3rd largest** in `nums = [3, 1, 5, 12, 2, 11]` using a min-heap of size 3.
The heap always keeps the 3 largest seen so far; its top is the smallest of those three.

| n | push to heap | size > 3? | pop (smallest) | heap after (min-heap) |
|---|--------------|-----------|----------------|------------------------|
| 3 | [3] | no | — | [3] |
| 1 | [1, 3] | no | — | [1, 3] |
| 5 | [1, 3, 5] | no | — | [1, 3, 5] |
| 12 | [1, 3, 5, 12] | yes (4) | pop 1 | [3, 5, 12] |
| 2 | [2, 3, 5, 12] | yes (4) | pop 2 | [3, 5, 12] |
| 11 | [3, 5, 11, 12] | yes (4) | pop 3 | [5, 11, 12] |

Final heap: `[5, 11, 12]` → its top (smallest) is **5 = the 3rd largest.**

Sanity check by sorting descending: `12, 11, 5, 3, 2, 1` → the 3rd is indeed 5. The heap never held
more than 3 elements, so each operation cost O(log 3) instead of O(log n). When `2` arrived, it was
pushed then immediately popped because it couldn't beat the current 3rd-largest — exactly the
filtering behavior we want.

### Complexity
- Time: **O(n log K)** — n pushes, each O(log K) on a size-K heap.
- Space: **O(K)**.
- (QuickSelect gives O(n) average for "Kth largest" specifically, but the heap is simpler and
  streams naturally. C++ also has `nth_element` for the QuickSelect approach.)

### Common variants
- Kth smallest (use a max-heap of size K)
- K closest points to the origin, K closest numbers to a target
- Top K frequent elements / words, frequency sort
- Sort a nearly-sorted (K-sorted) array, rearrange string so no two adjacent repeat

---

## 14. K-way Merge

### The intuition
You have **K sorted** lists/arrays and need to merge them, or pull elements in global sorted order
(the merged list, the Kth smallest across all, the smallest range covering all lists). A min-heap
holding **one element from each list** gives you the global minimum in O(log K). Pop it, then push
the *next* element **from the same list** the popped element came from. Repeat. The heap is always
"K candidates, one per source," and the smallest is always ready at the top.

### How to recognize it
- "merge K sorted lists/arrays."
- "Kth smallest element in a sorted matrix / in M sorted lists."
- "smallest range that includes at least one number from each of the K lists."

### Code template (merge K sorted arrays)

**C++**
```cpp
vector<int> mergeKSorted(vector<vector<int>>& lists) {
    using T = tuple<int,int,int>;                  // (value, listIndex, elemIndex)
    priority_queue<T, vector<T>, greater<T>> minHeap;
    for (int i = 0; i < (int)lists.size(); ++i)
        if (!lists[i].empty())
            minHeap.push({lists[i][0], i, 0});     // seed with each list's first element

    vector<int> result;
    while (!minHeap.empty()) {
        auto [val, li, ei] = minHeap.top(); minHeap.pop();
        result.push_back(val);
        if (ei + 1 < (int)lists[li].size())        // push next from the SAME list
            minHeap.push({lists[li][ei + 1], li, ei + 1});
    }
    return result;
}
```

**Java**
```java
int[] mergeKSorted(int[][] lists) {
    // min-heap of {value, listIndex, elemIndex}
    PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> a[0] - b[0]);
    int total = 0;
    for (int i = 0; i < lists.length; i++) {
        if (lists[i].length > 0) minHeap.offer(new int[]{lists[i][0], i, 0});
        total += lists[i].length;
    }
    int[] result = new int[total];
    int idx = 0;
    while (!minHeap.isEmpty()) {
        int[] top = minHeap.poll();
        int val = top[0], li = top[1], ei = top[2];
        result[idx++] = val;
        if (ei + 1 < lists[li].length)             // push next from the SAME list
            minHeap.offer(new int[]{lists[li][ei + 1], li, ei + 1});
    }
    return result;
}
```

### Dry run
**Input:** three sorted lists — `L0 = [2, 6, 8]`, `L1 = [3, 6, 7]`, `L2 = [1, 3, 4]`.
Heap entries are `(value, listIndex, elemIndex)`. We seed with each list's first element.

**Seed:** heap = `(1,L2,0), (2,L0,0), (3,L1,0)` → smallest is `(1,L2,0)`.

| pop (value) | from list | append | push next from same list | heap (values only) after |
|-------------|-----------|--------|---------------------------|---------------------------|
| 1 | L2[0] | 1 | L2[1]=3 | {2, 3(L1), 3(L2)} |
| 2 | L0[0] | 2 | L0[1]=6 | {3(L1), 3(L2), 6} |
| 3 | L1[0] | 3 | L1[1]=6 | {3(L2), 6(L0), 6(L1)} |
| 3 | L2[1] | 3 | L2[2]=4 | {4, 6(L0), 6(L1)} |
| 4 | L2[2] | 4 | L2 exhausted | {6(L0), 6(L1)} |
| 6 | L0[1] | 6 | L0[2]=8 | {6(L1), 8} |
| 6 | L1[1] | 6 | L1[2]=7 | {7, 8} |
| 7 | L1[2] | 7 | L1 exhausted | {8} |
| 8 | L0[2] | 8 | L0 exhausted | {} → done |

**Merged result: [1, 2, 3, 3, 4, 6, 6, 7, 8]**

The invariant that makes this correct: at any moment the heap contains the *smallest unpicked
element from each list that still has candidates*. So the heap's minimum is genuinely the global
next-smallest — nothing smaller can be hiding, because each list is internally sorted and we always
hold its current frontier.

### Complexity
- Time: **O(N log K)** where N is the total number of elements and K is the number of lists.
- Space: **O(K)** for the heap.

### Common variants
- Kth smallest in M sorted lists / in a sorted matrix
- Smallest range covering elements from K lists
- K pairs with smallest sums

---

## 15. 0/1 Knapsack (DP)

### The intuition
You have items, each with a **weight** and a **value**, and a knapsack with a **capacity**. Maximize
total value without exceeding capacity, where each item is taken **0 or 1 times** (not fractional,
not repeated — that's the "0/1"). The brute force tries every subset (2ⁿ). DP collapses it by
noticing **overlapping subproblems**: the best value for "first i items, capacity c" depends only on
smaller versions of the same question.

At each item you make a binary choice: **skip it** (value stays as it was without this item) or
**take it** (gain its value, but capacity drops by its weight). Take the better of the two.

`dp[i][c] = max( dp[i-1][c],  value[i] + dp[i-1][c - weight[i]] )`

### How to recognize it
- "select a subset under a capacity/budget to maximize/satisfy something."
- "can we partition / reach exactly this sum?" → subset-sum flavor (boolean DP).
- Each item is used at most once, choices are binary, and greedy doesn't work.

### Code template (2D table, then space-optimized 1D)

**C++**
```cpp
int knapsack(const vector<int>& weights, const vector<int>& values, int capacity) {
    int n = weights.size();
    vector<vector<int>> dp(n + 1, vector<int>(capacity + 1, 0));
    for (int i = 1; i <= n; ++i) {
        int w = weights[i-1], v = values[i-1];
        for (int c = 0; c <= capacity; ++c) {
            dp[i][c] = dp[i-1][c];                            // skip item i
            if (w <= c)                                        // can we afford to take it?
                dp[i][c] = max(dp[i][c], v + dp[i-1][c - w]);
        }
    }
    return dp[n][capacity];
}

int knapsack1D(const vector<int>& weights, const vector<int>& values, int capacity) {
    vector<int> dp(capacity + 1, 0);
    for (int i = 0; i < (int)weights.size(); ++i) {
        int w = weights[i], v = values[i];
        for (int c = capacity; c >= w; --c)                   // iterate BACKWARD
            dp[c] = max(dp[c], v + dp[c - w]);
    }
    return dp[capacity];
}
```

**Java**
```java
int knapsack(int[] weights, int[] values, int capacity) {
    int n = weights.length;
    int[][] dp = new int[n + 1][capacity + 1];
    for (int i = 1; i <= n; i++) {
        int w = weights[i-1], v = values[i-1];
        for (int c = 0; c <= capacity; c++) {
            dp[i][c] = dp[i-1][c];                            // skip item i
            if (w <= c)                                        // can we afford to take it?
                dp[i][c] = Math.max(dp[i][c], v + dp[i-1][c - w]);
        }
    }
    return dp[n][capacity];
}

int knapsack1D(int[] weights, int[] values, int capacity) {
    int[] dp = new int[capacity + 1];
    for (int i = 0; i < weights.length; i++) {
        int w = weights[i], v = values[i];
        for (int c = capacity; c >= w; c--)                   // iterate BACKWARD
            dp[c] = Math.max(dp[c], v + dp[c - w]);
    }
    return dp[capacity];
}
```

### Dry run
**Items:** weights `[1, 2, 3]`, values `[15, 20, 30]`. **Capacity = 5.**
We fill `dp[i][c]` = best value using the first `i` items with capacity `c`. Row 0 (no items) is all 0.

Item 1: weight 1, value 15. Item 2: weight 2, value 20. Item 3: weight 3, value 30.

| i \ c | 0 | 1 | 2 | 3 | 4 | 5 |
|-------|---|---|---|---|---|---|
| **0** (none) | 0 | 0 | 0 | 0 | 0 | 0 |
| **1** (w1,v15) | 0 | 15 | 15 | 15 | 15 | 15 |
| **2** (w2,v20) | 0 | 15 | 20 | 35 | 35 | 35 |
| **3** (w3,v30) | 0 | 15 | 20 | 35 | 45 | 50 |

**Answer: dp[3][5] = 50.**

Let's verify two interesting cells:
- `dp[2][3]` (first 2 items, capacity 3): skip item 2 → `dp[1][3]=15`; take item 2 (w=2,v=20) →
  `20 + dp[1][3-2=1] = 20 + 15 = 35`. Max(15, 35) = **35** ✓ (take both items 1 and 2: 15+20).
- `dp[3][5]` (all items, capacity 5): skip item 3 → `dp[2][5]=35`; take item 3 (w=3,v=30) →
  `30 + dp[2][5-3=2] = 30 + 20 = 50`. Max(35, 50) = **50** ✓ (items 2 and 3: weight 2+3=5, value 20+30).

The optimal pick is items 2 and 3 (weight 5 exactly, value 50), beating "all three" which would need
weight 6 (over capacity). The table *built that answer up from smaller subproblems* — never
re-solving the same `(i, c)` twice.

**Why the 1D version iterates capacity backward:** going backward ensures that when you compute
`dp[c]`, the `dp[c - w]` you read still reflects the *previous* item row (item not yet taken). Going
forward would let you take the same item twice — which is the *unbounded* knapsack, a different problem.

### Complexity
- Time: **O(n · W)** — n items × W capacities.
- Space: **O(n · W)** for the 2D table, reducible to **O(W)** with the 1D rolling array.
- (Note: this is "pseudo-polynomial" — it depends on the numeric value of W, not just input size.)

### Common variants
- Subset sum (can we hit exactly target?) — boolean DP
- Equal subset partition (target = total / 2)
- Count of subsets with a given sum, target sum (assign +/-)
- Minimum subset sum difference

---

## 16. Topological Sort

### The intuition
Given tasks with **dependencies** ("B requires A first"), produce a valid linear order. This only
makes sense on a **Directed Acyclic Graph (DAG)** — if there's a cycle, no valid order exists.
**Kahn's algorithm** (BFS flavor) is the cleanest: compute each node's **in-degree** (how many
prerequisites point at it). Nodes with in-degree 0 have nothing blocking them — start there. As you
"complete" a node, decrement its neighbors' in-degrees; any that hit 0 become newly available.
If you can't process all nodes, a cycle exists.

### How to recognize it
- "task/course scheduling with prerequisites," "build order," "compilation order."
- "is this ordering possible?" / "find any valid order" on a directed graph.
- "alien dictionary," "reconstruct itinerary," dependency resolution.

### Code template (Kahn's algorithm)

**C++**
```cpp
vector<int> topologicalSort(int numNodes, vector<vector<int>>& edges) {
    vector<vector<int>> graph(numNodes);
    vector<int> inDegree(numNodes, 0);
    for (auto& e : edges) {                  // edge {u, v} means u must come before v
        graph[e[0]].push_back(e[1]);
        inDegree[e[1]]++;
    }
    queue<int> q;
    for (int i = 0; i < numNodes; ++i)
        if (inDegree[i] == 0) q.push(i);

    vector<int> order;
    while (!q.empty()) {
        int node = q.front(); q.pop();
        order.push_back(node);
        for (int nei : graph[node])
            if (--inDegree[nei] == 0) q.push(nei);
    }
    return (int)order.size() == numNodes ? order : vector<int>{};  // empty = cycle
}
```

**Java**
```java
int[] topologicalSort(int numNodes, int[][] edges) {
    List<List<Integer>> graph = new ArrayList<>();
    for (int i = 0; i < numNodes; i++) graph.add(new ArrayList<>());
    int[] inDegree = new int[numNodes];
    for (int[] e : edges) {                  // edge {u, v} means u must come before v
        graph.get(e[0]).add(e[1]);
        inDegree[e[1]]++;
    }
    Queue<Integer> queue = new LinkedList<>();
    for (int i = 0; i < numNodes; i++)
        if (inDegree[i] == 0) queue.offer(i);

    int[] order = new int[numNodes];
    int idx = 0;
    while (!queue.isEmpty()) {
        int node = queue.poll();
        order[idx++] = node;
        for (int nei : graph.get(node))
            if (--inDegree[nei] == 0) queue.offer(nei);
    }
    return idx == numNodes ? order : new int[0];  // empty = cycle
}
```

### Dry run
**Graph:** 6 nodes (0..5). Edges (prerequisite → dependent):
`5→2, 5→0, 4→0, 4→1, 2→3, 3→1`.

**Step 0 — compute in-degrees** (count incoming edges):

| node | incoming edges | in-degree |
|------|----------------|-----------|
| 0 | from 5, 4 | 2 |
| 1 | from 4, 3 | 2 |
| 2 | from 5 | 1 |
| 3 | from 2 | 1 |
| 4 | (none) | 0 |
| 5 | (none) | 0 |

**Initial queue** (in-degree 0): `[4, 5]`.

| pop | order so far | neighbors decremented | new in-degrees | newly 0 → enqueue | queue after |
|-----|--------------|------------------------|----------------|--------------------|-------------|
| 4 | [4] | 0→1, 1→1 | 0:1, 1:1 | none | [5] |
| 5 | [4, 5] | 2→0, 0→0 | 2:0, 0:0 | 2, 0 | [2, 0] |
| 2 | [4, 5, 2] | 3→0 | 3:0 | 3 | [0, 3] |
| 0 | [4, 5, 2, 0] | (0 has no out-edges) | — | none | [3] |
| 3 | [4, 5, 2, 0, 3] | 1→0 | 1:0 | 1 | [1] |
| 1 | [4, 5, 2, 0, 3, 1] | (no out-edges) | — | none | [] → done |

**Answer: [4, 5, 2, 0, 3, 1]** — a valid topological order. `order.size() == 6 == numNodes`, so **no
cycle.**

Sanity check a couple of dependencies: `5` appears before `2` and `0` ✓; `2` before `3` ✓; `3`
before `1` ✓; `4` before `0` and `1` ✓. (The order isn't unique — `[5, 4, 2, 0, 3, 1]` is equally
valid. Any order respecting all the arrows is correct.)

**If there were a cycle** (say add edge `1→4`), nodes in the cycle would never reach in-degree 0,
the queue would empty early, `order.size() < 6`, and we'd return empty to signal "impossible."

### Complexity
- Time: **O(V + E)** — visit every vertex and every edge once.
- Space: **O(V + E)** for the adjacency list, in-degree array, and queue.

### Common variants
- Course schedule (can you finish? / give an order)
- Alien dictionary (derive letter order from sorted words)
- Reconstruct itinerary, sequence reconstruction
- Cycle detection in a directed graph (3-color DFS is the alternative)

---

# Putting it all together — a comeback study plan

After 11 months away, don't try to absorb all 16 at once. Layer them:

**Week 1 — array & string foundations (highest ROI):**
Sliding Window → Two Pointers → Merge Intervals → Cyclic Sort. These four cover a huge fraction of
easy/medium problems and rebuild your "see the pattern" muscle fastest.

**Week 2 — linked lists & trees:**
Fast & Slow Pointers → In-place Reversal → Tree BFS → DFS. BFS and DFS reinforce each other; learn
them back to back.

**Week 3 — heaps & search:**
Top K Elements → K-way Merge → Two Heaps → Modified Binary Search. The heap trio shares the same
mental model (one heap, size-K filter / one-per-list / two balanced halves).

**Week 4 — the harder conceptual ones:**
Subsets/Backtracking → Bitwise XOR → 0/1 Knapsack → Topological Sort. DP and backtracking take the
most reps; budget extra time.

**The single best habit:** for every new problem, *before coding*, ask "which of the 16 is this?"
The recognition signals in each section's "How to recognize it" are your cheat sheet. Pattern
recognition — not memorizing solutions — is what makes DSA click and stick.

**Language-specific gotchas to keep handy:**
- Heaps: C++ `priority_queue` is max by default, Java `PriorityQueue` is min by default.
- Binary search: always `mid = low + (high - low) / 2` to dodge int overflow.
- Custom heap ordering: C++ uses comparator template args or `greater<>`; Java uses a lambda
  `(a, b) -> a[0] - b[0]` (careful — subtraction can overflow; use `Integer.compare` for safety).
- Recursion depth: deep DFS can stack-overflow; both languages have limited default stacks for
  very skewed inputs (~10⁴–10⁵ frames).

**Rustiest-after-a-break list** (review these first): Cyclic Sort (easy to forget it exists), the
Fast & Slow cycle-start math, Two Heaps rebalancing, and the backward iteration in 1D knapsack.

Good luck — you've got this.
