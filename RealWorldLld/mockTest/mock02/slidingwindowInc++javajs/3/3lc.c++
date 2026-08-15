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


#include <iostream>
#include <string>
#include <unordered_map>
#include <algorithm>
using namespace std;

class Solution{
  public:
  
  int fnc12(string s){
      
      int n = s.size();
      int maxi = 0;
      
      int windowStart =0;
      
      unordered_map<char,int> mp;
      
      for(int windowEnd = 0 ; windowEnd< n ; windowEnd++){
          
          mp[s[windowEnd]]++;
          
          while(mp[s[windowEnd]] > 1){
            
              mp[s[windowStart]]--;
              windowStart++;  
          }
          
          maxi = max(maxi, windowEnd- windowStart +1);
          
         
      }
      
       return maxi;
  }
  
};
    

int main(){
    Solution sl;
    string s = "abcabcbb";
    
    cout << "the answer is" << " "<< sl.fnc12(s) << endl; 
}


