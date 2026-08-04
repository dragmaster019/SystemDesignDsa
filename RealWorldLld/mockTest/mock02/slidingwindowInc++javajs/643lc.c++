#include <bits/stdc++.h>
using namespace std;

class Solution{

public:
   double result(vector<int> &nums, int k){
       
       int j = 0;
       int ans = 0;
       int maxi = 0;
       
       for(int i =0 ;i < k; i++){
           ans+= nums[i];
       }
       
       maxi = ans;
       
       for(j = k; j< nums.size(); j++){
           ans+=nums[j] - nums[j-k];
           maxi=max(maxi,ans);
       }
       
       return double(maxi)/k;

    
   }

};

   int main(){

    vector<int> nums = {1,12,-5,-6,50,3};
    int k =4;
    
    Solution obj;

    cout << "The answer is" << " " << obj.result(nums,k) << endl;

}