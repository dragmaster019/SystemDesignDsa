"""
PYTHON DSA CRASH REFERENCE - coming from Java/C++/JS
Run this whole file:  python practice.py
Every section prints its own output. Read top to bottom, then re-run
pieces on your own by editing values below.
"""

from collections import deque, defaultdict, Counter
import heapq


# ============================================================
# BIG-O CHEAT SHEET - the numbers interviewers expect you to know cold.
# Keep this in your head, not just in this file.
#
#   LIST (dynamic array)
#     index arr[i]          O(1)
#     append(x)              O(1) amortized
#     pop() (from end)        O(1)
#     insert(0, x) / pop(0)    O(n)   <- shifts every element, avoid in loops
#     x in arr                  O(n)   <- linear scan
#     sort() / sorted()          O(n log n)
#     slice arr[a:b]               O(k), k = slice length
#
#   DICT / SET (hash table)
#     get/set/delete/"in" check   O(1) average, O(n) worst case (hash collisions - ignore worst case in interviews)
#     iterate all items             O(n)
#     sorted(d.items(), key=...)     O(n log n)
#
#   DEQUE (collections.deque, doubly linked list under the hood)
#     append / appendleft            O(1)
#     pop / popleft                    O(1)   <- this is WHY you use deque over list for queues
#
#   HEAP (heapq, binary heap over a list)
#     heappush / heappop                O(log n)
#     heapify(existing_list)              O(n)
#     peek smallest (heap[0])               O(1)
#     nlargest(k, arr) / nsmallest(k, arr)    O(n log k)
#
#   STRING
#     concatenation s1 + s2                    O(n)  <- building via += in a loop is O(n^2) total, use "".join() instead
#     slicing s[a:b]                             O(k)
#     s in text (substring search)                  O(n*m) worst case
# ============================================================


# ============================================================
# 1. LIST  (Java ArrayList / C++ vector / JS Array)
# ============================================================
def list_demo():
    arr = [5, 3, 8, 1]
    arr.append(9)                 # add to end          -> arr.add(9)                  | O(1) amortized
    arr.insert(0, 100)            # insert at index      -> arr.add(0, 100)             | O(n) - shifts everything right
    arr.pop()                     # remove+return LAST   -> arr.remove(arr.size()-1)    | O(1)
    arr.pop(0)                    # remove+return index0 -> arr.remove(0)               | O(n) - shifts everything left
    length = len(arr)             # length check         -> arr.size() / .length        | O(1) (works on str/dict/set too)
    arr.sort()                    # sort ascending, in place -- a LIST METHOD, no import needed | O(n log n)
    arr.sort(reverse=True)        # sort descending                                     | O(n log n)
    arr.sort(key=lambda x: -x)    # custom comparator (e.g. sort pairs by 2nd value)     | O(n log n)
    exists = 8 in arr             # membership check -> arr.contains(8)                  | O(n) - linear scan
    last = arr[-1] if arr else None   # last element, no more arr.get(arr.size()-1)      | O(1) - direct index

    # 2D list - THE classic interview bug:
    # grid = [[0]*3]*3   <-- WRONG. All 3 rows are the SAME list (aliased). Mutating one mutates all.
    grid = [[0] * 3 for _ in range(3)]   # RIGHT: comprehension makes 3 independent rows | O(n*m) time and space
    grid[0][0] = 1                        # proves independence: only row 0 changes     | O(1)
    print("1. LIST  ->", arr, "| len:", length, "| 8 in arr:", exists, "| grid:", grid)


# ============================================================
# 1b. 2D LIST TRAVERSAL  (matrix / grid problems)
# ============================================================
def matrix_traversal_demo():
    grid = [[1, 2, 3], [4, 5, 6], [7, 8, 9]]
    rows, cols = len(grid), len(grid[0])   # row count, column count -> grid.length, grid[0].length in Java

    # index-based (Java-style)
    total = 0
    for r in range(rows):
        for c in range(cols):
            total += grid[r][c]

    # value-based (Pythonic, skip indices if you don't need them)
    total2 = 0
    for row in grid:
        for val in row:
            total2 += val

    # 4-directional neighbor check - the pattern behind island/flood-fill/maze problems
    directions = [(-1, 0), (1, 0), (0, -1), (0, 1)]   # up, down, left, right
    r, c = 1, 1   # center cell, has all 4 neighbors in bounds
    neighbors = []
    for dr, dc in directions:
        nr, nc = r + dr, c + dc
        if 0 <= nr < rows and 0 <= nc < cols:          # ALWAYS bounds-check before indexing
            neighbors.append(grid[nr][nc])

    print("1b. MATRIX -> sum (index-based):", total, "| sum (value-based):", total2,
          "| neighbors of grid[1][1]:", neighbors)


# ============================================================
# 2. DICT  (Java HashMap / C++ unordered_map / JS object)
# ============================================================
def dict_demo():
    d = {}
    d["a"] = 1                    # add key/value        -> d.put("a", 1)                    | O(1) average
    d["b"] = 2
    d["a"] = d.get("a", 0) + 1    # safe increment, no KeyError even if missing               | O(1) average
    has_key = "a" in d             # membership check      -> d.containsKey("a")               | O(1) average
    val = d.get("z", -1)          # get with default, avoids KeyError -> like getOrDefault     | O(1) average
    d.pop("b", None)               # remove key safely (no error if missing)                  | O(1) average

    for k, v in d.items():        # iterate key+value together -> for (Map.Entry<K,V> e : ...) | O(n) total
        pass

    # FREQUENCY COUNT - the #1 daily DSA pattern. Two ways:

    words = ["a", "b", "a", "c", "b", "a"]

    # Way 1: manual, using get() default (works everywhere, no import needed) | O(n) time, O(n) space total
    freq = {}
    for w in words:
        freq[w] = freq.get(w, 0) + 1

    # Way 2: defaultdict - skips the get()/default boilerplate entirely       | O(n) time, O(n) space total
    freq2 = defaultdict(int)
    for w in words:
        freq2[w] += 1              # auto-inits missing key to 0, then +1

    # Way 3: Counter - one line, plus built-in ranking                       | O(n) time, O(n) space total
    freq3 = Counter(words)
    top2 = freq3.most_common(2)    # [('a', 3), ('b', 2)] - sorted already   | O(n log k) for top-k

    # sort a dict by its VALUE (e.g. "top k frequent elements" style problems) | O(n log n) time, O(n) space
    sorted_by_freq = sorted(freq.items(), key=lambda pair: pair[1], reverse=True)

    print("2. DICT  ->", d, "| freq:", dict(freq), "| Counter:", freq3, "| top2:", top2)
    print("          sorted by freq:", sorted_by_freq)


# ============================================================
# 3. STACK  (Java Deque-as-stack / C++ stack<int> / JS array push/pop)
#    Python has NO separate Stack class for this - a plain list IS your stack.
#    append() = push, pop() = pop. Both O(1) from the END.
# ============================================================
def stack_demo():
    stack = []
    stack.append(1)                # push -> stack.push(1)          | O(1) amortized
    stack.append(2)
    stack.append(3)
    top = stack[-1]                 # peek without removing -> stack.peek()  | O(1)
    popped = stack.pop()            # pop -> stack.pop()             | O(1)
    is_empty = len(stack) == 0      # empty check -> stack.isEmpty()  | O(1)
    print("3. STACK ->", stack, "| top was:", top, "| popped:", popped, "| empty:", is_empty)


# ============================================================
# 4. QUEUE / DEQUE  (Java ArrayDeque / C++ deque)
#    IMPORTANT: never use list.pop(0) as a queue - that's O(n) per call.
#    Always use collections.deque for queues - O(1) both ends.
# ============================================================
def queue_deque_demo():
    q = deque()
    q.append(1)                     # add to right/back    -> q.offer(1)     | O(1)
    q.append(2)
    q.append(3)
    front = q.popleft()             # remove from left/front -> q.poll()  (THIS makes it a queue - FIFO) | O(1)
    print("4a. QUEUE ->", list(q), "| dequeued:", front)

    dq = deque([1, 2, 3])
    dq.appendleft(0)                # push to front -> O(1), a plain list can't do this efficiently | O(1)
    dq.append(4)                    # push to back                                                  | O(1)
    dq.popleft()                    # pop from front                                                | O(1)
    dq.pop()                        # pop from back                                                 | O(1)
    print("4b. DEQUE ->", list(dq), "(used for BFS, sliding window, and stack+queue combined)")


# ============================================================
# 5. HEAP  (Java PriorityQueue / C++ priority_queue)
#    Python heapq is MIN-HEAP ONLY, and it operates directly on a plain list -
#    there's no separate PriorityQueue object.
# ============================================================
def heap_demo():
    h = []
    heapq.heappush(h, 5)              # O(log n) each
    heapq.heappush(h, 1)
    heapq.heappush(h, 3)
    smallest = heapq.heappop(h)      # always removes the SMALLEST -> pq.poll() | O(log n)
    print("5a. HEAP (min) -> popped:", smallest, "| remaining:", h)

    # MAX-HEAP TRICK: negate on the way in and out (Python has no max-heap built in)
    max_h = []
    for x in [5, 1, 3]:
        heapq.heappush(max_h, -x)      # O(log n) each, same complexity as min-heap - negation is free
    biggest = -heapq.heappop(max_h)   # O(log n)
    print("5b. HEAP (max via negation) -> biggest:", biggest)

    # Heap of tuples - priority + payload (Dijkstra, k-closest-points, task scheduling)
    # Tuples compare left-to-right, so this sorts by priority first automatically.
    task_h = []
    heapq.heappush(task_h, (2, "low priority task"))   # O(log n)
    heapq.heappush(task_h, (1, "high priority task"))
    next_task = heapq.heappop(task_h)   # (1, "high priority task") - lowest number wins | O(log n)
    print("5c. HEAP (tuples) -> next:", next_task)

    # Shortcuts - often faster to write live than a manual heap loop:
    nums = [7, 2, 9, 4, 1]
    print("5d. nlargest(2):", heapq.nlargest(2, nums), "| nsmallest(2):", heapq.nsmallest(2, nums))
    # nlargest/nsmallest -> O(n log k), better than sorting the whole array (O(n log n)) when k is small


# ============================================================
# 6. FUNCTIONS  (standalone, outside any class)
# ============================================================
def add(a: int, b: int = 10) -> int:   # type hints are optional but common in interviews: name: type, -> return type
    return a + b                        # no semicolon needed - newline ends the statement


def make_adder(x):                      # closures - a function returning a function
    def inner(y):
        return x + y
    return inner


# ============================================================
# 7. CLASS - what self actually is, and how objects get created
# ============================================================
class Stack:
    """A stack built on a list, wrapped in a class - shows self end to end."""

    def __init__(self, items=None):        # constructor. Runs automatically when you call Stack().
        self.items = items if items else []  # self.items = an instance field, like `private List<Integer> items;`

    def push(self, val):                    # self MUST be the first param of every method, even if you never
        self.items.append(val)              # write it yourself when calling: stack.push(5) auto-passes stack as self

    def pop(self):
        if not self.items:                  # 'not self.items' is True when list is empty -> items.isEmpty()
            return None
        return self.items.pop()

    def peek(self):
        return self.items[-1] if self.items else None

    def is_empty(self):
        return len(self.items) == 0


class Solution:                             # LeetCode's standard wrapper - your actual answer goes in here
    def two_sum(self, nums, target):
        # Time:  O(n)  - single pass, dict lookup/insert are O(1) average each
        # Space: O(n)  - worst case, `seen` holds every element before finding the pair
        # (this is what you SAY out loud after writing it - interviewers expect it unprompted)
        seen = {}
        for i, n in enumerate(nums):
            if target - n in seen:
                return [seen[target - n], i]
            seen[n] = i
        return []


# ============================================================
# MAIN - Python's version of `public static void main`
# ============================================================
# Python does NOT require a main function to run a file - it executes top to
# bottom automatically. The `if __name__ == "__main__":` guard below is still
# standard practice because:
#   - __name__ equals "__main__" ONLY when this file is run directly
#   - if some other file imports this one instead, this block is SKIPPED
# So you still "need to write it" for any real project - it's just not
# mandatory the way Java's main() is; a script with no main block still runs.
if __name__ == "__main__":
    list_demo()
    matrix_traversal_demo()
    dict_demo()
    stack_demo()
    queue_deque_demo()
    heap_demo()

    print("6. FUNC  -> add(3):", add(3), "| add(3, 5):", add(3, 5))
    add5 = make_adder(5)
    print("          make_adder(5)(10):", add5(10))

    # Creating an object: same idea as Java's `new Stack()`, just no `new` keyword.
    s = Stack()
    s.push(1)
    s.push(2)
    s.push(3)
    print("7a. CLASS Stack -> peek:", s.peek(), "| pop:", s.pop())

    # Calling a method: object.method(args) - self is auto-passed, you never type it here.
    sol = Solution()
    nums, target = [2, 7, 11, 15], 9
    print("7b. CLASS Solution -> two_sum:", sol.two_sum(nums, target))

    # Taking input from a real user (rare on LeetCode, useful for standalone scripts):
    # name = input("Enter your name: ")      # input() always returns a str
    # n = int(input("Enter a number: "))      # cast explicitly if you need a number
