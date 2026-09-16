// LEETCODE-PROBLEM-START
// 103. Binary Tree Zigzag Level Order Traversal [Medium]
// https://leetcode.com/problems/binary-tree-zigzag-level-order-traversal/
//
// Given the root of a binary tree, return the zigzag level order traversal of its nodes' values. (i.e., from left to right, then right to left for the next level and alternate between).
//
// Example 1:
//
// Input: root = [3,9,20,null,null,15,7]
// Output: [[3],[20,9],[15,7]]
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
// - -100 <= Node.val <= 100
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


    public List<List<Integer>> zigZag(TreeNode root){

        List<List<Integer>> result = new ArrayList<>();

        if(root == null) return result;

        Queue<TreeNode> q = new ArrayDeque<>();

        q.offer(root);

        int level = 1;

        while(!q.isEmpty()){
            int size = q.size();

            List<Integer> lvl = new ArrayList<>();

            for(int i =0; i<size; i++){

                TreeNode temp = q.poll();
                lvl.add(temp.val);

                if(temp.left!= null){
                    q.offer(temp.left);
                }

                if(temp.right!= null){
                    q.offer(temp.right);
                }
            }

            if(level % 2 ==0 ){

                Collections.reverse(lvl);       
            }
                result.add(lvl);   
                level++;
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

        List<List<Integer>> result = solution.zigZag(root);

        System.out.println(result);


    }
}
