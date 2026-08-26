// LEETCODE-PROBLEM-START
// 448. Find All Numbers Disappeared in an Array [Easy]
// https://leetcode.com/problems/find-all-numbers-disappeared-in-an-array/
//
// Given an array nums of n integers where nums[i] is in the range [1, n], return an array of all the integers in the range [1, n] that do not appear in nums.
//
// Example 1:
//
// Input: nums = [4,3,2,7,8,2,3,1]
// Output: [5,6]
//
// Example 2:
//
// Input: nums = [1,1]
// Output: [2]
//
// Constraints:
//
// - n == nums.length
//
// - 1 <= n <= 10^5
//
// - 1 <= nums[i] <= n
//
// Follow up: Could you do it without extra space and in O(n) runtime? You may assume the returned list does not count as extra space.
// LEETCODE-PROBLEM-END

import java.util.ArrayList;
import java.util.List;

class Solution {

    public static List<Integer> result(int[] nums) {

        int i = 0;

        List<Integer> ls = new ArrayList<>();

        while(i<nums.length){

            if(nums[i] != i+1){
                int currIndex = nums[i] -1 ;

                if(nums[i] != nums[currIndex]){
                    swap(nums, i, currIndex);
                }
                else{
                    i++;
                }
            }
            else{
                i++;
            }
        }

        for(int j = 0 ; j< nums.length; j++){

            if(nums[j]!= j+1){

                ls.add(j+1);

            }
        }

        return ls;

    }

    public static void swap(int[]nums, int a , int b){
        int temp = nums[a];
        nums[a] = nums[b];
        nums[b] = temp;
    }





    

    public static void main(String[] args) {

        int[] nums = {4, 3, 2, 7, 8, 2, 3, 1};
        System.out.println(Solution.result(nums));
    }
}
