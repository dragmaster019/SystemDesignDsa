// LEETCODE-PROBLEM-START
// 637. Average of Levels in Binary Tree [Easy]
// https://leetcode.com/problems/average-of-levels-in-binary-tree/
//
// Given the root of a binary tree, return the average value of the nodes on each level in the form of an array. Answers within 10^-5 of the actual answer will be accepted.
//
// Example 1:
//
// Input: root = [3,9,20,null,null,15,7]
// Output: [3.00000,14.50000,11.00000]
// Explanation: The average value of nodes on level 0 is 3, on level 1 is 14.5, and on level 2 is 11.
// Hence return [3, 14.5, 11].
//
// Example 2:
//
// Input: root = [3,9,20,15,7]
// Output: [3.00000,14.50000,11.00000]
//
// Constraints:
//
// - The number of nodes in the tree is in the range [1, 10^4].
//
// - -2^31 <= Node.val <= 2^31 - 1
// LEETCODE-PROBLEM-END


import java.util.*;

class TreeNode{
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {}

    TreeNode (int val){
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right){

        this.val = val;
        this.left = left;
        this.right = right;
    }
}

class Solution{

    public List<Double>avgLevle(TreeNode root){

    List<Double> result = new ArrayList<>();

    if(root == null) return result;

    Queue<TreeNode> q = new ArrayDeque<>();

    q.offer(root);

    while(!q.isEmpty()){

        int size = q.size();

        long sum = 0;

        List<Integer> ls = new ArrayList<>();

        for(int i =0 ; i< size; i++){

            TreeNode temp = q.poll();

            sum += temp.val;


            if(temp.left!= null){
                q.offer(temp.left);
            }

            if(temp.right!= null){
                q.offer(temp.right);
            }            
        }

        result.add((double) sum / size);

    }

    return result;
}



    public static void main(String[] args){

        TreeNode root = new TreeNode(3);

        root.left = new TreeNode(9);
        root.right = new TreeNode(20);
        root.right.left = new TreeNode(15);
        root.right.right = new TreeNode(7);

        Solution solution = new Solution();

        List<Double> result = solution.avgLevle(root);

        System.out.println(result);
    }
}