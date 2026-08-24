// LEETCODE-PROBLEM-START
// 977. Squares of a Sorted Array [Easy]
// https://leetcode.com/problems/squares-of-a-sorted-array/
//
// Given an integer array nums sorted in non-decreasing order, return an array of the squares of each number sorted in non-decreasing order.
//
// Example 1:
//
// Input: nums = [-4,-1,0,3,10]
// Output: [0,1,9,16,100]
// Explanation: After squaring, the array becomes [16,1,0,9,100].
// After sorting, it becomes [0,1,9,16,100].
//
// Example 2:
//
// Input: nums = [-7,-3,2,3,11]
// Output: [4,9,9,49,121]
//
// Constraints:
//
// - 1 <= nums.length <= 10^4
//
// - -10^4 <= nums[i] <= 10^4
//
// - nums is sorted in non-decreasing order.
//
// Follow up: Squaring each element and sorting the new array is very trivial, could you find an O(n) solution using a different approach?
// LEETCODE-PROBLEM-END

import java.util.*;

class Solution {

    public static int[] result(int[] nums) {

        int[] squares = new int[nums.length];

        int left = 0;
        int right = nums.length - 1;

        for (int pos = nums.length - 1; pos >= 0; pos--) {

            int leftSquare = nums[left] * nums[left];
            int rightSquare = nums[right] * nums[right];

            if (leftSquare > rightSquare) {
                squares[pos] = leftSquare;
                left++;
            } else {
                squares[pos] = rightSquare;
                right--;
            }
        }

        return squares;
    }

    public static void main(String[] args) {

        int[] nums = {-4, -1, 0, 3, 10};

        System.out.println(Arrays.toString(Solution.result(nums)));
    }
}
