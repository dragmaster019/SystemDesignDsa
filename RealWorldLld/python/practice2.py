# list, queue, 2d list, deque, map, set


class Solution:
    def add_two_number(self, l1, l2):    # <- fixed: was missing `self` as the first param
        pass                              # TODO: not implemented yet, come back to this one


class TwoSumSolution:                        # class keyword, PascalCase name, colon, then indented body
    def two_sum(self, nums, target):          # self = required first param on every method (like Java's implicit `this`)
        # Time:  O(n)  - one pass through nums
        # Space: O(n)  - `seen` can hold up to n-1 elements in the worst case
        seen = {}                              # value -> index map
        for i, n in enumerate(nums):           # i = index, n = value at that index
            complement = target - n             # the number we'd need to reach target
            if complement in seen:               # have we already stored that number?
                return [seen[complement], i]       # yes -> return [its index, current index]
            seen[n] = i                          # no -> remember this number's index for later
        return []                              # LeetCode guarantees a solution exists, but return something safe anyway


# ------------------------------------------------------------
# missing-number example: your original code, fixed. Bugs it had:
#   1. `for i in range n:`        -> range is a FUNCTION, needs parens: range(n)
#   2. `n = len(arr)`               -> off by one; array holds n-1 numbers (one missing),
#                                       so the true range upper bound is len(arr) + 1
#   3. `n * (n+1)/2`                 -> `/` gives a float (8.0); use `//` for a clean int
# ------------------------------------------------------------
def missing(arr):
    n = len(arr) + 1              # full range is 1..n; array holds n-1 numbers (one missing)
    total = n * (n + 1) // 2      # expected sum of 1..n, floor division keeps it an int
    arrsum = 0
    for i in range(len(arr)):     # sum every element actually in arr
        arrsum += arr[i]
    return total - arrsum


def big(arr):
    arr.sort(reverse=True)
    return missing(arr)


def modify(arr):
    arr.append(6)
    arr.append(7)
    arr.append(9)
    return big(arr)


if __name__ == "__main__":                    # Python's version of `public static void main`
    sol = TwoSumSolution()                      # create the object -> like `new TwoSumSolution()`, no `new` keyword
    nums = [2, 7, 11, 15]                       # hardcoded input, LeetCode style (no need for input() here)
    target = 9

    result = sol.two_sum(nums, target)          # call the method -> object.method(args), self is auto-passed
    print("nums:", nums, "| target:", target, "| result:", result)

    # try a second case to prove it's not hardcoded to one input
    result2 = sol.two_sum([3, 2, 4], 6)
    print("second test:", result2)

    arr = [1, 2, 3, 4, 5]
    print("missing element:", modify(arr))       # -> 8
