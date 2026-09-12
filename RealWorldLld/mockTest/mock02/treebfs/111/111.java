// LEETCODE-PROBLEM-START
// 111. Minimum Depth of Binary Tree [Easy]
// https://leetcode.com/problems/minimum-depth-of-binary-tree/
//
// Given a binary tree, find its minimum depth.
//
// The minimum depth is the number of nodes along the shortest path from the root node down to the nearest leaf node.
//
// Note: A leaf is a node with no children.
//
// Example 1:
//
// Input: root = [3,9,20,null,null,15,7]
// Output: 2
//
// Example 2:
//
// Input: root = [2,null,3,null,4,null,5,null,6]
// Output: 5
//
// Constraints:
//
// - The number of nodes in the tree is in the range [0, 10^5].
//
// - -1000 <= Node.val <= 1000
// LEETCODE-PROBLEM-END

import java.util.*;

class TreeNode{
    int val;
    TreeNode left;
    TreeNode right;


    TreeNode(){}

    TreeNode(int val){
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right){
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

class Solution{

    public int avgLevel(TreeNode root){

        if(root == null) return 0;

        Queue<TreeNode> q = new ArrayDeque<>();
        int level = 1;

        q.offer(root);

        while(!q.isEmpty()){

            int size = q.size();
            

            for(int i =0; i< size; i++){

                TreeNode temp = q.poll();


                // Leaf node
                if (temp.left == null && temp.right == null) {
                    return level;
                }

               

                if(temp.left!=null){
                    q.offer(temp.left);
                }

                if(temp.right!= null){
                    q.offer(temp.right);
                }

            }

            level++;

        }

        return level;
    }

    public static void main(String[] args){

        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(9);
        root.right = new TreeNode(20);

        root.right.left = new TreeNode(15);
        root.right.right = new TreeNode(7);

        Solution sol = new Solution();

        int result = sol.avgLevel(root);

        System.out.println(result);
    }
    

}



