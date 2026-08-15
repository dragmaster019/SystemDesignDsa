/**
 * @param {number[]} nums
 * @param {number} k
 * @return {number}
 */
function findMaxAverage(nums, k) {
    // TODO: your logic here
    let sum =0;

    for(let i=0; i<k; i++){
        sum += nums[i];
    }

    let maxSum = sum;

    for(let i=k; i<nums.length; i++){
        sum += nums[i] - nums[i-k];
        maxSum = Math.max(maxSum, sum);
    }

    return maxSum / k;
}

const nums = [1, 12, -5, -6, 50, 3];
const k = 4;

console.log("The answer is", findMaxAverage(nums, k));
