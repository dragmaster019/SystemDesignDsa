// # Q001 — Rotting Oranges

// **Type:** DSA
// **Time box:** 25-30 minutes

// ## Problem Statement

// You are given an `m x n` grid where each cell can have one of three values:

// - `0` — an empty cell
// - `1` — a fresh orange
// - `2` — a rotten orange

// Every minute, any fresh orange that is **4-directionally adjacent** to a rotten orange
// becomes rotten.

// Return *the minimum number of minutes that must elapse until no cell has a fresh orange*.
// If this is impossible, return `-1`.

// ## Method signature

// ```java
// public int orangesRotting(int[][] grid)
// ```

// Write it as a standalone class (e.g. `Solution.java`) with a `main` method that runs it
// against at least the example cases below plus one edge case of your own choosing.

// ## Examples

// ```
// Input: grid = [[2,1,1],[1,1,0],[0,1,1]]
// Output: 4

// Input: grid = [[2,1,1],[0,1,1],[1,0,1]]
// Output: -1
// Explanation: the orange in the bottom left corner is never rotten, because rotting
// only happens 4-directionally.

// Input: grid = [[0,2]]
// Output: 0
// ```

// ## Constraints

// - `1 <= m, n <= 10`
// - `grid[i][j]` is only `0`, `1`, or `2`
// - Aim for the best time complexity you can justify — be ready to state it and explain why
//   it's optimal.

import java.util.*;

public class mock1 {

    //so my first appraoch will be i have to find the minimum minutes until no fresh oranges are left to be rotten

    //in the graph  everytime i will pick one rotten orange move bidirectinally and minutes will be become 1 then take another rotten move bidicetional and increase the minutes as it require level by level traversal i will choose bfs

    // example of bfs is like this
    // 2 1 1
    // 1 1 0
    // 0 1 1

    // so starting from cell (0,0) will move 4 direction correct? 

    //(0,0) 4 direction (0,1) eka right, (1,0) eka down rest left and up are out of bound 
    // so the level is 1 , so this becomes rotten

    // then next i will choose (0,1) -> (0,2) right, (1,1) down, (0,0) left, (-1,1) up out of bound
    // so the level is 2, so this becomes rotten

    //next (1,0) -> (1,1) right which already rotten, (2,0) down which is empty rest out of bound

    // next (1,1) -> right empty eka (1,2) so this way the way become total 4 minutes

    // so it is bfs will use queue first in first out for every entry point and after every traversal i have to increase the minutes
    
    public static int rottenOranges(int [][] grid){

        int m = grid.length;
        int n = grid[0].length;

        Queue<int []> q = new LinkedList<>();

        for(int i=0; i<m;i++){
            for(int j=0; j<n; j++){
                if(grid[i][j] == 2){
                    q.add(new int[] {i,j,0});

                }

            }

        }   

                int[] dx ={0,0,-1,1};
                int[] dy ={1,-1,0,0};
                int ans =0;


                        while(!q.isEmpty()){
                            int curr[] = q.poll();
                            int row = curr[0];
                            int col = curr[1];
                            int time = curr[2];
                            

                            ans = Math.max(ans,time);

                    for(int k =0 ; k<4;k++){
                        int newX = dx[k] + row;
                        int newY = dy[k] + col;
                        if(newX >=0 && newX < m && newY >=0 && newY < n && grid[newX][newY] == 1){
                            grid[newX][newY] = 2;
                            q.add(new int[] {newX, newY, time+1});

                    }

                }
            }

    for(int i=0; i<m;i++){
        for(int j=0; j<n; j++){
            if(grid[i][j] == 1){
                return -1;
            }
        }
    }

    return ans;

}
    public static void main(String[] args){

        int [][] grid ={
            {0,2}
        };

        System.out.println("minimum time taken has" + " " + mock1.rottenOranges(grid));

    }
};

