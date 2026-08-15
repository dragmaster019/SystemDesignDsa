// ============================================================
// Closures
// ============================================================
// A closure is a function that "remembers" the variables from the
// scope it was created in, even after that outer scope has finished
// executing. In JS, functions carry a reference to their lexical
// environment, not a snapshot/copy of it.

// 1) Basic counter - the classic interview example
function makeCounter() {
  let count = 0; // private state, not accessible from outside
  return function () {
    count++;
    return count;
  };
}

const counter1 = makeCounter();
const counter2 = makeCounter(); // separate closure, separate `count`
console.log(counter1()); // 1
console.log(counter1()); // 2
console.log(counter2()); // 1  <- independent state, proves each call to makeCounter() creates a new closure

// 2) Closures capture variables BY REFERENCE, not by value
function outer() {
  let value = "initial";
  function inner() {
    console.log(value);
  }
  value = "changed";
  return inner;
}
outer()(); // "changed"  <- inner() sees the live value, not what it was when inner was defined

// 3) Practical use: data privacy / module pattern (poor man's private fields)
function createBankAccount(initialBalance) {
  let balance = initialBalance; // truly private, no way to access except via returned methods
  return {
    deposit(amount) {
      balance += amount;
      return balance;
    },
    withdraw(amount) {
      if (amount > balance) throw new Error("Insufficient funds");
      balance -= amount;
      return balance;
    },
    getBalance() {
      return balance;
    },
  };
}
const account = createBankAccount(100);
console.log(account.deposit(50)); // 150
console.log(account.withdraw(30)); // 120
console.log(account.balance); // undefined - no direct access, only through closure methods

/*
Run: node 02-closures.js

Actual output (verified):
1
2
1
changed
150
120
undefined

Interview soundbite: "A closure lets an inner function retain access to its
outer function's variables after the outer function has returned. It captures
variables by reference (the live binding), not a snapshot - which is exactly
what causes the var-vs-let loop bug. Practically, I use closures for private
state, like a counter or bank account where the internal variable is never
exposed directly, only through returned methods."
*/
