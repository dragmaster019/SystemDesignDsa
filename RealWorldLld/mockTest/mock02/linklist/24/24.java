// LEETCODE-PROBLEM-START
// 24. Swap Nodes in Pairs [Medium]
// https://leetcode.com/problems/swap-nodes-in-pairs/
//
// Given a linked list, swap every two adjacent nodes and return its head. You must solve the problem without modifying the values in the list's nodes (i.e., only nodes themselves may be changed.)
//
// Example 1:
//
// Input: head = [1,2,3,4]
//
// Output: [2,1,4,3]
//
// Explanation:
//
// Example 2:
//
// Input: head = []
//
// Output: []
//
// Example 3:
//
// Input: head = [1]
//
// Output: [1]
//
// Example 4:
//
// Input: head = [1,2,3]
//
// Output: [2,1,3]
//
// Constraints:
//
// - The number of nodes in the list is in the range [0, 100].
//
// - 0 <= Node.val <= 100
// LEETCODE-PROBLEM-END

class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}

class Solution {

    public static ListNode result(ListNode head) {


    }

    public static ListNode build(int[] vals) {
        ListNode dummy = new ListNode();
        ListNode curr = dummy;
        for (int v : vals) {
            curr.next = new ListNode(v);
            curr = curr.next;
        }
        return dummy.next;
    }

    public static void print(ListNode head) {
        StringBuilder sb = new StringBuilder();
        while (head != null) {
            sb.append(head.val);
            if (head.next != null) sb.append(" -> ");
            head = head.next;
        }
        System.out.println(sb);
    }

    public static void main(String[] args) {

        ListNode head = build(new int[]{1, 2, 3, 4});
        print(Solution.result(head));
    }
}
