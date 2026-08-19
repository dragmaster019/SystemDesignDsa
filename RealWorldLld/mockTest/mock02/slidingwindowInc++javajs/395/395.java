// LEETCODE-PROBLEM-START
// 395. Longest Substring with At Least K Repeating Characters [Medium]
// https://leetcode.com/problems/longest-substring-with-at-least-k-repeating-characters/
//
// Given a string s and an integer k, return the length of the longest substring of s such that the frequency of each character in this substring is greater than or equal to k.
//
// if no such substring exists, return 0.
//
// Example 1:
//
// Input: s = "aaabb", k = 3
// Output: 3
// Explanation: The longest substring is "aaa", as 'a' is repeated 3 times.
//
// Example 2:
//
// Input: s = "ababbc", k = 2
// Output: 5
// Explanation: The longest substring is "ababb", as 'a' is repeated 2 times and 'b' is repeated 3 times.
//
// Constraints:
//
// - 1 <= s.length <= 10^4
//
// - s consists of only lowercase English letters.
//
// - 1 <= k <= 10^5
// LEETCODE-PROBLEM-END

class Solution {

    public static int result(String s, int k) {

        int maxLength = 0;

        for (int uniqueTarget = 1; uniqueTarget <= 26; uniqueTarget++) {

            int[] freq = new int[26];
            int left = 0;
            int uniqueCount = 0;
            int countAtLeastK = 0;

            for (int right = 0; right < s.length(); right++) {

                char rightChar = s.charAt(right);
                if (freq[rightChar - 'a'] == 0) {
                    uniqueCount++;
                }
                freq[rightChar - 'a']++;
                if (freq[rightChar - 'a'] == k) {
                    countAtLeastK++;
                }

                while (uniqueCount > uniqueTarget) {
                    char leftChar = s.charAt(left);
                    if (freq[leftChar - 'a'] == k) {
                        countAtLeastK--;
                    }
                    freq[leftChar - 'a']--;
                    if (freq[leftChar - 'a'] == 0) {
                        uniqueCount--;
                    }
                    left++;
                }

                if (uniqueCount == uniqueTarget && uniqueCount == countAtLeastK) {
                    maxLength = Math.max(maxLength, right - left + 1);
                }
            }
        }

        return maxLength;
    }

    public static void main(String[] args) {

        String s = "ababbc";
        int k = 2;

        System.out.println(Solution.result(s, k));
    }
}
