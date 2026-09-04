// LEETCODE-PROBLEM-START
// 102. Binary Tree Level Order Traversal [Medium]
// https://leetcode.com/problems/binary-tree-level-order-traversal/
//
// Given the root of a binary tree, return the level order traversal of its nodes' values. (i.e., from left to right, level by level).
//
// Example 1:
//
// Input: root = [3,9,20,null,null,15,7]
// Output: [[3],[9,20],[15,7]]
//
// Example 2:
//
// Input: root = [1]
// Output: [[1]]
//
// Example 3:
//
// Input: root = []
// Output: []
//
// Constraints:
//
// - The number of nodes in the tree is in the range [0, 2000].
//
// - -1000 <= Node.val <= 1000
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
    public List<List<Integer>> levelOrder(TreeNode root) {

        List<List<Integer>> result = new ArrayList<>();

        if(root == null) return result;

        Queue<TreeNode> q = new ArrayDeque<>();

        q.offer(root);

        while(!q.isEmpty()){

            int size = q.size();

            List<Integer> level = new ArrayList<>();

            for(int i =0 ; i< size; i++){

                TreeNode temp = q.poll();

                level.add(temp.val);

                if(temp.left!= null ){
                    q.offer(temp.left);
                }

                if(temp.right!= null){
                    q.offer(temp.right);
                }
            }

            result.add(level);            
        }

        return result;


        
    }

    public static void main(String[] Args){

         TreeNode root = new TreeNode(3);

        root.left = new TreeNode(9);
        root.right = new TreeNode(20);

        root.right.left = new TreeNode(15);
        root.right.right = new TreeNode(7);

        Solution solution = new Solution();

        List<List<Integer>> result = solution.levelOrder(root);

        System.out.println(result);


    }
}