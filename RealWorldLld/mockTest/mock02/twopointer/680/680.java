// LEETCODE-PROBLEM-START
// 680. Valid Palindrome II [Easy]
// https://leetcode.com/problems/valid-palindrome-ii/
//
// Given a string s, return true if the s can be palindrome after deleting at most one character from it.
//
// Example 1:
//
// Input: s = "aba"
// Output: true
//
// Example 2:
//
// Input: s = "abca"
// Output: true
// Explanation: You could delete the character 'c'.
//
// Example 3:
//
// Input: s = "abc"
// Output: false
//
// Constraints:
//
// - 1 <= s.length <= 10^5
//
// - s consists of lowercase English letters.
// LEETCODE-PROBLEM-END

class Solution {

    private static boolean isPalindrome(String s, int left, int right) {
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) return false;
            left++;
            right--;
        }
        return true;
    }


    public static boolean result(String s) {

        int left = 0;
        int right = s.length() -1;

        while(left < right){

            if(s.charAt(right) == s.charAt(left)){
                left++;
                right--;
            }

           else{

                return isPalindrome(s,left+1,right) || isPalindrome(s,left,right-1);
           }


        }

        return true;     
    }

    public static void main(String[] args) {

        String s = "abca";

        System.out.println(Solution.result(s));
    }
}
