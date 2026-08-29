# How HashMap Works Internally (Java 8+)

## Structure

Backed by `Node<K,V>[] table` — an array of buckets. Default capacity **16**, load factor
**0.75**, so the resize threshold is `16 * 0.75 = 12`. Each bucket holds either nothing, a single
`Node`, a linked list of `Node`s (a collision chain), or — since Java 8 — a red-black tree of
`TreeNode`s once a bucket gets crowded enough.

## `put(key, value)` step by step

1. Compute `key.hashCode()`.
2. **Spread the hash**: `h = hashCode(); h ^= (h >>> 16)`. This XORs the high 16 bits into the low
   16 bits. Why: bucket index only uses the low bits (next step), so if a `hashCode()` has weak
   low-bit entropy (common with sequential/typical hashCodes), collisions would spike without this
   spreading step.
3. **Bucket index** = `hash & (table.length - 1)`. Because capacity is always kept a power of two,
   this bitmask is exactly equivalent to `hash % capacity` but far cheaper — and it *only* works
   correctly because of that power-of-two invariant.
4. If the target bucket is empty, insert the node directly.
5. If not, walk the bucket's chain comparing cached `hash` first (cheap int comparison) then
   `equals()` (authoritative) against each existing key.
   - Match found → overwrite the value.
   - No match → append to the **tail** of the chain (Java 8+; Java 7 prepended at the *head*,
     which — under concurrent resizing without synchronization — could form a cycle. That's the
     infamous Java 7 "HashMap infinite loop" production bug under concurrent access).
6. If a bucket's chain grows to **8+** nodes *and* the table has at least **64** buckets, that
   bucket converts from a linked list to a red-black tree, dropping worst-case lookup in that
   bucket from O(n) to O(log n) — this defends against adversarial inputs engineered to collide
   into the same bucket (a hash-flooding DoS). Below 64 total buckets, the map just resizes
   instead of treeifying, since treeifying a tiny table doesn't help.
7. If `size` exceeds `capacity * loadFactor` (the threshold), the table **doubles** in size and
   every existing entry is rehashed into the new, larger table.

## `get(key)`

Same hash + bucket-index computation, then walk the bucket (list or tree) using `equals()` until a
match is found, or return `null`.

## Why the `equals()`/`hashCode()` contract matters here specifically

If two keys are `.equals()` but return different `hashCode()`s, they can land in *different*
buckets — the map treats them as two unrelated keys, so you silently get two entries where you
expected one to overwrite the other. The contract only runs one direction: equal objects **must**
have equal hash codes; unequal objects are allowed to share one (that's just an ordinary
collision, handled by the chain/tree).

## Resizing cost

Every resize is a full O(n) rehash of every existing entry. If you know you're about to insert
~10,000 entries, constructing `new HashMap<>()` and letting it resize itself upward ~10 times
(16 → 32 → 64 → ... ) wastes that repeated rehashing — pass an initial capacity instead
(`new HashMap<>(16384)`, rounded up to the next power of two internally) to avoid it.

## Null keys

Exactly one `null` key is allowed; it's treated as hash `0` and stored in bucket 0. Null *values*
are unrestricted (any number of keys can map to `null`).

## Iteration order

Not guaranteed, and not stable across a resize (entries get redistributed). Need insertion order?
Use `LinkedHashMap`. Need sorted order? Use `TreeMap` (O(log n) operations, backed by a red-black
tree over the *whole* map, not just crowded buckets).

## Thread safety

`HashMap` is not thread-safe. Concurrent structural modification (put/resize) from multiple
threads without external synchronization can corrupt a bucket's chain — in Java 7 the
head-insertion-during-resize behavior described above could literally spin a thread in an infinite
loop. Use `ConcurrentHashMap` for concurrent access: Java 8+ achieves thread safety via CAS
operations on individual bins plus `synchronized` only on the specific bin being modified during a
collision — not one lock for the whole map (a major improvement over the old `Hashtable`, which
synchronizes every method on a single lock and serializes all access regardless of which keys are
touched).

## HashSet, for free

`HashSet<E>` is literally a thin wrapper around `HashMap<E, Object>`, where every value is the
same dummy sentinel (`PRESENT`). `add(e)` is just `map.put(e, PRESENT) == null`.
