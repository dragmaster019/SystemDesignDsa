// 🗳️ Array / Vector — Voter Registration System
// Design a VoterQueue class that tracks voters arriving at a polling booth. Support:

// addVoter(name) — register a voter as they arrive
// getLastN(n) — return the last n voters who arrived
// totalCount() — return total number of voters who have arrived so far

// Constraints:

// Use an array or vector as your core data structure
// If n is greater than total voters, return all of them
// Voter names are strings

// Example:
// addVoter("Alice")
// addVoter("Bob")
// addVoter("Charlie")
// addVoter("Diana")

// totalCount()     → 4
// getLastN(2)      → ["Charlie", "Diana"]
// getLastN(10)     → ["Alice", "Bob", "Charlie", "Diana"]

// Go ahead and write your solution — any language works!AskSonnet 4.6


#include <iostream>
#include <vector>
#include <string>   

using namespace std;

class VoterQueue {

    vector<string> voters;

    public: 

    void addVoter(string name){

        voters.push_back(name);

    }

    int countTotalVoters(){

        return voters.size();

    }

    vector<string> getLastN(int n){
        int start = max(0, (int)voters.size() - n);
        return vector<string>(voters.begin() + start, voters.end());
    }

    


};

int main (){

    VoterQueue vq;

    vq.addVoter("Alice");
    vq.addVoter("Bob");
    vq.addVoter("Charlie");
    vq.addVoter("Diana");   

    cout << vq.countTotalVoters() << endl; 

    vector<string> lastTwoVoters = vq.getLastN(2);
    for (const string& voter : lastTwoVoters) {
        cout << voter << " ";
    }
    cout << endl;



}