// LEETCODE-PROBLEM-START
// 287. Find the Duplicate Number [Medium]
// https://leetcode.com/problems/find-the-duplicate-number/
//
// Given an array of integers nums containing n + 1 integers where each integer is in the range [1, n] inclusive.
//
// There is only one repeated number in nums, return this repeated number.
//
// You must solve the problem without modifying the array nums and using only constant extra space.
//
// Example 1:
//
// Input: nums = [1,3,4,2,2]
// Output: 2
//
// Example 2:
//
// Input: nums = [3,1,3,4,2]
// Output: 3
//
// Example 3:
//
// Input: nums = [3,3,3,3,3]
// Output: 3
//
// Constraints:
//
// - 1 <= n <= 10^5
//
// - nums.length == n + 1
//
// - 1 <= nums[i] <= n
//
// - All the integers in nums appear only once except for precisely one integer which appears two or more times.
//
// Follow up:
//
// - How can we prove that at least one duplicate number must exist in nums?
//
// - Can you solve the problem in linear runtime complexity?
// LEETCODE-PROBLEM-END


class Solution {

    public static int result(int[] nums) {

        int i = 0;
        
        while(i<nums.length){

            if(nums[i] != i+1){
                int currindex = nums[i] -1;

                if(nums[i] != nums[currindex]){
                    swap(nums, i, currindex);
                }
                else{
                    return nums[i];
                }
            }
            else{
                i++;
            }
        }

        for(int j= 0; j< nums.length; j++){
            if(nums[j] != j+1){
                return j+1;
            }
        }

        return -1;





    }

    public static void swap(int[]nums , int a, int b){

        int temp = nums[a];
        nums[a] = nums[b];
        nums[b] = temp; 
    }

    public static void main(String[] args) {

        int[] nums = {1, 3, 4, 2, 2};
        System.out.println(Solution.result(nums));
    }
}
