// //question
// 3. Longest Substring Without Repeating Characters

// Given a string s, find the length of the longest substring without duplicate characters.
// Example 1:

// Input: s = "abcabcbb"
// Output: 3
// Explanation: The answer is "abc", with the length of 3. Note that "bca" and "cab" are also correct answers.
// Example 2:

// Input: s = "bbbbb"
// Output: 1
// Explanation: The answer is "b", with the length of 1.
// Example 3:

// Input: s = "pwwkew"
// Output: 3
// Explanation: The answer is "wke", with the length of 3.
// Notice that the answer must be a substring, "pwke" is a subsequence and not a substring.
// Constraints:
// 0 <= s.length <= 105
// s consists of English letters, digits, symbols and spaces.

import java.util.*;
class Main{

    public static int solution(String s){
        int n = s.length();

        HashMap<Character, Integer> mp = new HashMap<>();

        int windowEnd = 0;

        int maxi = 0;

        for(int windowStart = 0 ; windowStart <n ; windowStart++){

            mp.put(s.charAt(windowStart),mp.getOrDefault(s.charAt(windowStart),0)+1);

            while(mp.get(s.charAt(windowStart)) > 1){

                mp.put(s.charAt(windowEnd),mp.get(s.charAt(windowEnd))-1);

                windowEnd++;
            }

            maxi = Math.max(maxi, windowStart - windowEnd + 1);


        }

        return maxi;




    }

    public static void main(String[] args){

        String s = "abcabcbb";
        System.out.println(Main.solution(s));



    }
}