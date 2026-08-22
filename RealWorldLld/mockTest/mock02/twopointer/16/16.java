/// LEETCODE-PROBLEM-START
// 16. 3Sum Closest [Medium]
// https://leetcode.com/problems/3sum-closest/
//
// Given an integer array nums of length n and an integer target, find three integers at distinct indices in nums such that the sum is closest to target.
//
// Return the sum of the three integers.
//
// You may assume that each input would have exactly one solution.
//
// Example 1:
//
// Input: nums = [-1,2,1,-4], target = 1
// Output: 2
// Explanation: The sum that is closest to the target is 2. (-1 + 2 + 1 = 2).
//
// Example 2:
//
// Input: nums = [0,0,0], target = 1
// Output: 0
// Explanation: The sum that is closest to the target is 0. (0 + 0 + 0 = 0).
//
// Constraints:
//
// - 3 <= nums.length <= 500
//
// - -1000 <= nums[i] <= 1000
//
// - -10^4 <= target <= 10^4
// LEETCODE-PROBLEM-END

import java.util.*;
class Solution{

    public static int result(int[] nums, int target){

        Arrays.sort(nums);
        int closestSum = nums[0] + nums[1] + nums[2];

        for(int i = 0; i < nums.length-2; i++){

            int left = i+1;
            int right = nums.length -1;

            while(left < right){

                int sum = nums[i] + nums[left] + nums[right];

                if (Math.abs(target - sum) < Math.abs(target - closestSum)) {
                    closestSum = sum;
                }

                if(sum < target){
                    left++;
                }

                else if(sum > target){
                    right--;
                }

                else{
                    return sum;
                }
            }
        }

        return closestSum;
    }

public static void main(String[] args){

    int[] nums = {-1,2,1,-4};
    int target = 1;

    System.out.println(Solution.result(nums,target));
}
}