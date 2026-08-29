# HashMap Internals — Questions

Full explanation in [`HashMapInternals.md`](HashMapInternals.md); from-scratch implementation in
[`CustomHashMapDemo.java`](CustomHashMapDemo.java).

1. **Walk me through exactly what happens when you call `map.put(key, value)`.**
2. **Why is the default capacity 16 and not, say, 10 or 100?**
   Power-of-two capacity is what lets `hash & (capacity - 1)` substitute for `hash % capacity` —
   a bitmask instead of a division on every single put/get.
3. **What is the load factor, and why is the default 0.75?**
   It's the space/time tradeoff dial: a higher load factor means less wasted array space but more
   collisions (slower lookups); lower means faster lookups but more memory and more frequent
   resizes. 0.75 is the JDK's chosen middle ground.
4. **What triggers a resize, and how expensive is it?**
   `size > capacity * loadFactor`. It's O(n) — every entry gets rehashed into the new table.
5. **Why does `HashMap` XOR the hashCode with its own upper 16 bits before using it?**
   To spread entropy from the high bits into the low bits the bucket-index mask actually uses —
   otherwise a `hashCode()` with weak low-bit variance would collide far more than necessary.
6. **When does a bucket convert to a tree instead of staying a linked list, and why does it also check the table size?**
   At 8+ nodes in one bucket, *and only if* the table has at least 64 buckets total — treeifying a
   small table doesn't help (a resize would spread the load better), so it resizes instead below
   that threshold.
7. **If you override `equals()` but forget `hashCode()`, what breaks — concretely?**
   Two objects that are logically equal can hash to different buckets. `map.put(a, x)` followed by
   `map.put(b, y)` where `a.equals(b)` is true but their hash codes differ produces **two separate
   entries** instead of `b` overwriting `a` — a silent correctness bug, not a crash.
8. **Is `HashMap` thread-safe? What goes wrong under concurrent `put()` without synchronization?**
   No. Worst historically: Java 7's head-insertion during a concurrent resize could form a cycle
   in a bucket's linked list, spinning a thread in `get()` forever (100% CPU, no exception, no
   crash — just hung).
9. **`HashMap` vs `ConcurrentHashMap` vs `Collections.synchronizedMap` — how does each handle concurrency?**
   `HashMap`: not handled at all. `Collections.synchronizedMap`: one lock guards the *entire* map,
   serializing all access. `ConcurrentHashMap`: CAS on individual bins + `synchronized` scoped to
   just the bin under contention, so unrelated keys don't block each other.
10. **How would you implement a HashMap from scratch?**
    See `CustomHashMapDemo.java` — array of buckets, separate chaining via a linked list per
    bucket, resize-and-rehash when the load factor is exceeded. (It skips the Java 8+ treeify
    step for simplicity — noted inline in the code.)
11. **What happens with a `null` key? Multiple `null` values?**
    One `null` key is allowed (stored at bucket 0, treated as hash 0). `null` values are
    unrestricted — any number of keys can map to `null`.
