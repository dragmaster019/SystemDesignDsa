// LEETCODE-PROBLEM-START
// 567. Permutation in String [Medium]
// https://leetcode.com/problems/permutation-in-string/
//
// Given two strings s1 and s2, return true if s2 contains a permutation of s1, or false otherwise.
//
// In other words, return true if one of s1's permutations is the substring of s2.
//
// Example 1:
//
// Input: s1 = "ab", s2 = "eidbaooo"
// Output: true
// Explanation: s2 contains one permutation of s1 ("ba").
//
// Example 2:
//
// Input: s1 = "ab", s2 = "eidboaoo"
// Output: false
//
// Constraints:
//
// - 1 <= s1.length, s2.length <= 10^4
//
// - s1 and s2 consist of lowercase English letters.
// LEETCODE-PROBLEM-END

import java.util.Stack;

class Solution{

    public static boolean isMatch(int[] freq1, int[] freq2){

        for(int i = 0; i<26; i++){
            if(freq1[i]!= freq2[i]) return false;
        }
        return true;

    }
    public static boolean result(String s1, String s2){

        if(s2.length() < s1.length()) return false;

        int[] freq1 = new int[26];
        int[] freq2 = new int[26];

        for(int i=0; i< s1.length();i++){

            freq1[s1.charAt(i) - 'a']++;
            freq2[s2.charAt(i) - 'a']++;

        }

        if(isMatch(freq1, freq2)) return true;

        int left =0; 
        
        for(int right = s1.length(); right < s2.length(); right++){

            freq2[s2.charAt(right) - 'a']++;
            freq2[s2.charAt(left) - 'a']--;
            left++;
        

        if(isMatch(freq1, freq2)) return true;
        }

        return false;

    }


public static void main(String [] args){

    String s1 = "ab";
    String s2 = "eidbaooo";

    System.out.println(Solution.result(s1,s2));
}
}
