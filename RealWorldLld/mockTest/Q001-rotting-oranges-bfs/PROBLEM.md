# Q001 — Rotting Oranges

**Type:** DSA
**Time box:** 25-30 minutes

## Problem Statement

You are given an `m x n` grid where each cell can have one of three values:

- `0` — an empty cell
- `1` — a fresh orange
- `2` — a rotten orange

Every minute, any fresh orange that is **4-directionally adjacent** to a rotten orange
becomes rotten.

Return *the minimum number of minutes that must elapse until no cell has a fresh orange*.
If this is impossible, return `-1`.

## Method signature

```java
public int orangesRotting(int[][] grid)
```

Write it as a standalone class (e.g. `Solution.java`) with a `main` method that runs it
against at least the example cases below plus one edge case of your own choosing.

## Examples

```
Input: grid = [[2,1,1],[1,1,0],[0,1,1]]
Output: 4

Input: grid = [[2,1,1],[0,1,1],[1,0,1]]
Output: -1
Explanation: the orange in the bottom left corner is never rotten, because rotting
only happens 4-directionally.

Input: grid = [[0,2]]
Output: 0
```

## Constraints

- `1 <= m, n <= 10`
- `grid[i][j]` is only `0`, `1`, or `2`
- Aim for the best time complexity you can justify — be ready to state it and explain why
  it's optimal.
