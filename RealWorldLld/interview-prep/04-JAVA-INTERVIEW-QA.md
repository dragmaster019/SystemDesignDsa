# Java Interview — Most Asked Q&A (Train Out Loud)

Read each Q, cover the A, say the answer in your own words, then check.
These are the exact questions that come up in 80% of Java interviews.

---

## SECTION 1 — OOP (always asked first)

**Q: What are the 4 pillars of OOP?**
A: Encapsulation (hide data, expose via methods), Inheritance (reuse parent code), Polymorphism (one interface, many behaviors at runtime), Abstraction (expose what, hide how).

**Q: Difference between abstract class and interface?**
A:
| | Abstract Class | Interface |
|---|---|---|
| Can have fields | Yes, any | Only `public static final` |
| Constructor | Yes | No |
| Method implementation | Yes (some) | Only `default`/`static` (Java 8+) |
| Extends/implements | `extends` one only | `implements` many |
Use abstract class when subclasses share state. Use interface when you just need a contract.

**Q: What is method overloading vs overriding?**
A: Overloading = same method name, different parameters, resolved at COMPILE time.
Overriding = subclass redefines a parent method with same signature, resolved at RUNTIME (dynamic dispatch).

**Q: Can we override a static method?**
A: No. Static methods belong to the class, not an instance. A subclass can hide it (method hiding), but it's not true overriding — no dynamic dispatch.

**Q: What is the `super` keyword?**
A: Refers to the parent class. `super()` calls parent constructor. `super.method()` calls parent's version of an overridden method.

**Q: What does `final` do?**
A: On a variable → can't reassign. On a method → can't override. On a class → can't extend (e.g. `String` is final).

---

## SECTION 2 — String (very commonly asked)

**Q: Why is String immutable in Java?**
A: String objects can't be changed after creation. Any "modification" creates a new String object. This makes Strings safe to share across threads, safe as HashMap keys, and allows the String Pool to work.

**Q: What is the String Pool?**
A: A special memory area in the heap. When you write `String s = "hello"`, Java checks the pool first — if "hello" already exists, it reuses the same object. `new String("hello")` always creates a new object, bypassing the pool.

**Q: `==` vs `.equals()` for Strings?**
A: `==` compares references (memory address). `.equals()` compares content (characters).
```java
String a = "hello";
String b = "hello";
a == b      // true  (same pool object)
a.equals(b) // true

String c = new String("hello");
a == c      // FALSE (different objects)
a.equals(c) // true
```
**Always use `.equals()` to compare String content.**

**Q: String vs StringBuilder vs StringBuffer?**
A:
- `String` — immutable, thread-safe by default, slow for repeated concatenation
- `StringBuilder` — mutable, NOT thread-safe, fast (use in single-threaded loops)
- `StringBuffer` — mutable, thread-safe (synchronized), slower than StringBuilder
Use `StringBuilder` for building strings in a loop. Use `String` for constants.

---

## SECTION 3 — Collections (always asked)

**Q: ArrayList vs LinkedList — when to use which?**
A:
- `ArrayList` — backed by array. Fast random access O(1). Slow insert/delete in middle O(n) due to shifting. Use when you read more than write.
- `LinkedList` — backed by doubly-linked nodes. Fast insert/delete at any position O(1) with an iterator. Slow random access O(n). Use when you insert/delete heavily from middle.

**Q: How does HashMap work internally?**
A: HashMap uses an array of "buckets." When you call `put(key, value)`:
1. Compute `key.hashCode()` → maps to a bucket index
2. If bucket is empty → store directly
3. If collision → store as a linked list (or tree if >8 entries, Java 8+)
On `get(key)` → hash the key → find bucket → compare with `.equals()` to find the exact entry.
**Default capacity = 16, load factor = 0.75** → resizes (doubles) when 75% full.

**Q: HashMap vs HashSet?**
A: HashSet is just a HashMap where the value is a dummy object (`PRESENT`). Internally identical. HashSet uses `hashCode()` + `equals()` to ensure uniqueness.

**Q: HashMap vs LinkedHashMap vs TreeMap?**
A:
- `HashMap` — no order, O(1) get/put
- `LinkedHashMap` — insertion order maintained, O(1) get/put
- `TreeMap` — sorted by key (natural order or Comparator), O(log n) get/put

**Q: What is the `equals()` and `hashCode()` contract?**
A: If `a.equals(b)` is true → `a.hashCode() == b.hashCode()` MUST be true.
The reverse is not required (collision is OK).
**If you override `equals()`, you MUST override `hashCode()`** — otherwise HashMap/HashSet break because equal objects could go to different buckets.

**Q: List vs Set vs Map?**
A: List — ordered, allows duplicates. Set — no duplicates, unordered (HashSet) or sorted (TreeSet). Map — key→value pairs, keys unique.

---

## SECTION 4 — Exception Handling

**Q: Checked vs Unchecked exceptions?**
A:
- **Checked** — must be caught or declared with `throws`. Compiler enforces it. Examples: `IOException`, `SQLException`. For recoverable conditions (file not found — tell the user).
- **Unchecked** — extend `RuntimeException`. Compiler doesn't force you to handle. Examples: `NullPointerException`, `ArrayIndexOutOfBoundsException`. For programming bugs.

**Q: `finally` block — when does it NOT run?**
A: `finally` always runs EXCEPT if:
- `System.exit()` is called
- JVM crashes
- Thread is killed

**Q: `throw` vs `throws`?**
A: `throw` → actually throw an exception object: `throw new RuntimeException("oops")`.
`throws` → declare in method signature that this method might throw: `void read() throws IOException`.

**Q: Can we catch multiple exceptions?**
A: Yes, Java 7+:
```java
try {
    ...
} catch (IOException | SQLException e) {
    // handle both
}
```

**Q: What is exception chaining?**
A: Wrapping a lower-level exception in a higher-level one so the original cause isn't lost:
```java
try {
    db.query(...);
} catch (SQLException e) {
    throw new DatabaseError("fetch failed", e); // e is the "cause"
}
```

---

## SECTION 5 — Java 8 Features (heavily asked)

**Q: What is a Lambda expression?**
A: A short anonymous function. Instead of:
```java
Runnable r = new Runnable() {
    public void run() { System.out.println("hi"); }
};
```
Write:
```java
Runnable r = () -> System.out.println("hi");
```
Lambdas work with **functional interfaces** (interfaces with exactly one abstract method).

**Q: What is a functional interface?**
A: An interface with exactly one abstract method. `@FunctionalInterface` annotation enforces this.
Key built-in ones:
- `Predicate<T>` — takes T, returns boolean: `x -> x > 0`
- `Function<T,R>` — takes T, returns R: `s -> s.length()`
- `Consumer<T>` — takes T, returns nothing: `x -> System.out.println(x)`
- `Supplier<T>` — takes nothing, returns T: `() -> new User()`

**Q: What is the Stream API?**
A: A pipeline to process collections declaratively (filter → map → collect) without mutating the original.
```java
List<String> result = names.stream()
    .filter(n -> n.startsWith("S"))  // keep only names starting with S
    .map(String::toUpperCase)         // transform each
    .sorted()                          // sort
    .collect(Collectors.toList());     // collect back to list
```
Streams are lazy — nothing runs until a terminal operation (`.collect`, `.forEach`, `.count`).

**Q: `map` vs `flatMap`?**
A: `map` — one input → one output (transform each element).
`flatMap` — one input → multiple outputs, then flatten into one stream.
```java
// map: List<List<Integer>> → List<List<Integer>>
// flatMap: List<List<Integer>> → List<Integer>  (flattened)
lists.stream().flatMap(Collection::stream).collect(toList());
```

**Q: What is Optional?**
A: A container that either holds a value or is empty — replaces `null` and forces you to handle the "absent" case explicitly.
```java
Optional<User> user = findById(id);
user.ifPresent(u -> System.out.println(u.getName()));
String name = user.map(User::getName).orElse("Unknown");
```
Never call `.get()` without `.isPresent()` — defeats the purpose.

**Q: Default methods in interfaces (Java 8)?**
A: Interfaces can now have concrete method implementations marked `default`. This lets you add new methods to an interface without breaking all existing implementations.
```java
interface Vehicle {
    default void start() { System.out.println("Starting..."); }
}
```

---

## SECTION 6 — Memory & Garbage Collection

**Q: Stack vs Heap?**
A:
- **Stack** — method call frames, local variables, references. LIFO. Automatically cleaned when method returns. Fast. Per-thread.
- **Heap** — all objects (`new` keyword). Shared across threads. GC managed. Slower than stack.
`String s = new String("hi")` → `s` (reference) is on stack, the String object is on heap.

**Q: What is Garbage Collection?**
A: Java automatically reclaims memory from objects with no live references. You don't call `free()`. GC runs in the background. You can suggest it with `System.gc()` but can't force it.

**Q: What is a memory leak in Java?**
A: When objects are no longer needed but still referenced, preventing GC from reclaiming them. Common causes: static collections holding objects forever, unclosed streams/connections, listener registrations never removed.

---

## SECTION 7 — Multithreading Basics

**Q: Thread vs Runnable?**
A: Two ways to create a thread:
```java
// Option 1: extend Thread
class MyThread extends Thread {
    public void run() { ... }
}
new MyThread().start();

// Option 2: implement Runnable (preferred — doesn't waste your one extends slot)
Thread t = new Thread(() -> System.out.println("running"));
t.start();
```
Always call `.start()` not `.run()` — `.run()` just calls the method on the current thread, no new thread created.

**Q: What is `synchronized`?**
A: A lock that ensures only one thread runs that block/method at a time. Prevents race conditions on shared data.
```java
synchronized void increment() {
    count++; // only one thread at a time
}
```

**Q: `volatile` keyword?**
A: Tells the JVM that a variable's value will be modified by multiple threads — always read from main memory, never from a thread's local cache. Guarantees visibility but NOT atomicity.

**Q: Deadlock — what is it and how to avoid?**
A: Two threads each hold a lock the other needs → both wait forever.
Avoid by: always acquiring locks in the same order across all threads, or using `tryLock()` with a timeout.

---

## SECTION 8 — Static & Keywords

**Q: What does `static` mean?**
A: Belongs to the CLASS, not an instance. One copy shared by all instances.
- `static` field — shared state (e.g. a counter)
- `static` method — can call without creating an object (e.g. `Math.abs()`)
- `static` block — runs once when class loads (initialization)

**Q: `this` keyword?**
A: Refers to the current instance. Used to distinguish instance variables from local variables with same name, or to call another constructor: `this(args)`.

**Q: Can a `static` method access instance variables?**
A: No. Static context has no `this` reference. It can only access other static members.

---

## SECTION 9 — Common Trap Questions

**Q: What is the output?**
```java
String a = "hello";
String b = "hello";
System.out.println(a == b);     // true  (same pool object)
System.out.println(a.equals(b)); // true
```

**Q: What happens here?**
```java
int[] arr = null;
System.out.println(arr.length); // NullPointerException
```

**Q: Pass by value or reference in Java?**
A: Always **pass by value**. For primitives, the value is copied. For objects, the *reference* is copied — both the caller and method point to the same object, but reassigning the parameter doesn't affect the caller's reference.
```java
void change(StringBuilder sb) {
    sb.append(" world");   // DOES affect caller's object
    sb = new StringBuilder("new"); // does NOT affect caller's reference
}
```

**Q: `int` vs `Integer`?**
A: `int` — primitive, stored on stack, can't be null, faster.
`Integer` — wrapper object, stored on heap, can be null, needed for Collections.
**Autoboxing** — Java automatically converts between them: `Integer x = 5` (boxes), `int y = x` (unboxes).
Watch out: `Integer x = null; int y = x;` → NullPointerException on unbox.

---

## SECTION 10 — Quick Fire Round (say the answer in 1 sentence)

| Question | Answer |
|---|---|
| What is polymorphism? | Same method call, different behavior at runtime based on actual object type |
| What is encapsulation? | Private fields + public getters/setters — control how data is accessed |
| Is Java pure OOP? | No — it has primitives (int, boolean) which aren't objects |
| Can interface extend another interface? | Yes, with `extends` (not `implements`) |
| Can we instantiate an abstract class? | No — it's incomplete. You must subclass it |
| What is `NullPointerException`? | Calling a method on a null reference |
| Difference between `==` and `equals()`? | `==` compares references; `equals()` compares content |
| What is autoboxing? | Automatic conversion between primitive (`int`) and wrapper (`Integer`) |
| What is a Singleton? | A class that allows only one instance, shared everywhere |
| What is the `hashCode()` contract? | Equal objects must have equal hash codes |
| What happens if you don't override `hashCode()` when you override `equals()`? | HashMap/HashSet break — equal objects land in different buckets |
| What is `try-with-resources`? | Auto-closes resources (streams, connections) after the try block |
| What is an enum? | A fixed set of named constants — type-safe, can have methods/fields |
| What is method chaining? | Each method returns `this`, enabling `builder.name("x").email("y").build()` |
| What is `instanceof`? | Checks if an object is an instance of a class/interface at runtime |

---

## How to answer OOP questions in an interview

**Don't just define — give a one-line example from real code:**
- Encapsulation: "In my notification service, `recipient` and `message` are private — I expose them only through getters so no caller can corrupt them mid-send."
- Polymorphism: "My `NotificationDispatcher` holds a `List<Notification>` and calls `n.send()` — whether that sends an SMS or email is decided at runtime, dispatcher code never changes."
- Abstraction: "The `abstract Notification` class exposes `send()` without implementing it — callers know notifications can be sent without knowing how."
- Inheritance: "SMS, Email, Push all extend `Notification` and reuse the recipient validation I wrote once in the parent constructor."

These answers immediately signal you understand OOP in practice, not just theory.
