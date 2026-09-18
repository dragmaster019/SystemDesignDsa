// LEETCODE-PROBLEM-START
// 113. Path Sum II [Medium]
// https://leetcode.com/problems/path-sum-ii/
//
// Given the root of a binary tree and an integer targetSum, return all root-to-leaf paths where the sum of the node values in the path equals targetSum. Each path should be returned as a list of the node values, not node references.
//
// A root-to-leaf path is a path starting from the root and ending at any leaf node. A leaf is a node with no children.
//
// Example 1:
//
// Input: root = [5,4,8,11,null,13,4,7,2,null,null,5,1], targetSum = 22
// Output: [[5,4,11,2],[5,8,4,5]]
// Explanation: There are two paths whose sum equals targetSum:
// 5 + 4 + 11 + 2 = 22
// 5 + 8 + 4 + 5 = 22
//
// Example 2:
//
// Input: root = [1,2,3], targetSum = 5
// Output: []
//
// Example 3:
//
// Input: root = [1,2], targetSum = 0
// Output: []
//
// Constraints:
//
// - The number of nodes in the tree is in the range [0, 5000].
//
// - -1000 <= Node.val <= 1000
//
// - -1000 <= targetSum <= 1000
// LEETCODE-PROBLEM-END

import java.util.*;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {}

    TreeNode(int val) {
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

class Solution {

    public List<List<Integer>> pathSum(TreeNode root, int targetSum) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        List<Integer> path = new ArrayList<>();
        dfs(root, targetSum, path, result);
        return result;
    }

    private void dfs(TreeNode root, int targetSum, List<Integer> path, List<List<Integer>> result) {
        if (root == null) return;

        path.add(root.val);

        if (root.left == null && root.right == null && targetSum == root.val) {
            result.add(new ArrayList<>(path));
        } else {
            dfs(root.left, targetSum - root.val, path, result);
            dfs(root.right, targetSum - root.val, path, result);
        }

        path.remove(path.size() - 1);
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(5);

        root.left = new TreeNode(4);
        root.right = new TreeNode(8);

        root.left.left = new TreeNode(11);
        root.right.left = new TreeNode(13);
        root.right.right = new TreeNode(4);
        root.left.left.left = new TreeNode(7);
        root.left.left.right = new TreeNode(2);

        root.right.right.left = new TreeNode(5);
        root.right.right.right = new TreeNode(1);

        Solution s = new Solution();
        int targetSum = 22;
        System.out.println(s.pathSum(root, targetSum));
    }
}

