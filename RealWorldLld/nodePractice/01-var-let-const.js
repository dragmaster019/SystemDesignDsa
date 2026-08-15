// ============================================================
// var vs let vs const
// ============================================================

// 1) SCOPE: var is function-scoped, let/const are block-scoped
function scopeDemo() {
  if (true) {
    var x = "var value";
    let y = "let value";
  }
  console.log(x); // "var value"  -> leaks out of the if-block
  try {
    console.log(y); // ReferenceError: y is not defined
  } catch (e) {
    console.log(e.message); // "y is not defined"
  }
}
scopeDemo();

// 2) HOISTING + TDZ (Temporal Dead Zone)
// var is hoisted AND initialized with `undefined`.
// let/const are hoisted but NOT initialized -> accessing before
// declaration throws, because they sit in the "temporal dead zone".
console.log(typeof hoistedVar); // "undefined" (declaration hoisted, value not yet assigned)
var hoistedVar = 5;

try {
  console.log(hoistedLet); // ReferenceError: Cannot access 'hoistedLet' before initialization
} catch (e) {
  console.log(e.message);
}
let hoistedLet = 5;

// 3) RE-DECLARATION: var allows re-declaring in the same scope, let/const don't
var a = 1;
var a = 2; // fine
console.log(a); // 2

let b = 1;
// let b = 2; // SyntaxError if uncommented: Identifier 'b' has already been declared

// 4) REASSIGNMENT: const can't be reassigned, but object/array CONTENTS are still mutable
const obj = { count: 1 };
obj.count = 2; // allowed - mutating the object, not reassigning the binding
console.log(obj); // { count: 2 }
try {
  obj = {}; // TypeError: Assignment to constant variable.
} catch (e) {
  console.log(e.message);
}

// 5) THE CLASSIC INTERVIEW GOTCHA: var in a loop with setTimeout
console.log("--- var loop ---");
for (var i = 0; i < 3; i++) {
  setTimeout(() => console.log("var i:", i), 0);
}
// Output (all three callbacks share ONE `i` binding, loop finishes before timers fire):
// var i: 3
// var i: 3
// var i: 3

console.log("--- let loop ---");
for (let j = 0; j < 3; j++) {
  setTimeout(() => console.log("let j:", j), 0);
}
// Output (let creates a NEW binding per iteration, each closure captures its own j):
// let j: 0
// let j: 1
// let j: 2

/*
Run: node 01-var-let-const.js

Actual output (verified):
var value
y is not defined
undefined
Cannot access 'hoistedLet' before initialization
2
{ count: 2 }
Assignment to constant variable.
--- var loop ---
--- let loop ---
var i: 3
var i: 3
var i: 3
let j: 0
let j: 1
let j: 2

Interview soundbite: "var is function-scoped and hoisted with `undefined`,
let/const are block-scoped and hoisted into a temporal dead zone. The
var-in-a-loop-with-setTimeout question is the classic way this gets tested —
var shares one binding across iterations, let creates a fresh binding each
iteration, which is why closures over let capture the right value."
*/
