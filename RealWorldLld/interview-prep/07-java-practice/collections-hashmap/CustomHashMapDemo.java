public class CustomHashMapDemo {

    // A minimal separate-chaining HashMap that shows the mechanics the real java.util.HashMap
    // uses under the hood. Left out for simplicity vs. the real thing: treeifying crowded buckets
    // (Java 8+), and fail-fast iterator support.
    static class SimpleHashMap<K, V> {

        private static class Entry<K, V> {
            final K key;
            V value;
            Entry<K, V> next; // collision chain

            Entry(K key, V value, Entry<K, V> next) {
                this.key = key;
                this.value = value;
                this.next = next;
            }
        }

        private Entry<K, V>[] buckets;
        private int size = 0;
        private static final double LOAD_FACTOR = 0.75;

        @SuppressWarnings("unchecked")
        SimpleHashMap() {
            buckets = new Entry[16]; // must start (and stay) a power of two — see indexFor()
        }

        // Same "spread the bits" trick the real HashMap uses, so a weak hashCode's low bits alone
        // don't determine the bucket.
        private int hash(K key) {
            if (key == null) return 0;
            int h = key.hashCode();
            return h ^ (h >>> 16);
        }

        private int indexFor(int hash, int capacity) {
            return hash & (capacity - 1); // valid only because capacity is always a power of two
        }

        void put(K key, V value) {
            int idx = indexFor(hash(key), buckets.length);
            Entry<K, V> curr = buckets[idx];

            while (curr != null) {
                if (keysEqual(curr.key, key)) {
                    curr.value = value; // key already present -> overwrite
                    return;
                }
                curr = curr.next;
            }

            // Not found in the chain -> insert at the head of this bucket
            buckets[idx] = new Entry<>(key, value, buckets[idx]);
            size++;

            if (size > buckets.length * LOAD_FACTOR) {
                resize();
            }
        }

        V get(K key) {
            int idx = indexFor(hash(key), buckets.length);
            Entry<K, V> curr = buckets[idx];
            while (curr != null) {
                if (keysEqual(curr.key, key)) {
                    return curr.value;
                }
                curr = curr.next;
            }
            return null;
        }

        V remove(K key) {
            int idx = indexFor(hash(key), buckets.length);
            Entry<K, V> curr = buckets[idx];
            Entry<K, V> prev = null;

            while (curr != null) {
                if (keysEqual(curr.key, key)) {
                    if (prev == null) {
                        buckets[idx] = curr.next;
                    } else {
                        prev.next = curr.next;
                    }
                    size--;
                    return curr.value;
                }
                prev = curr;
                curr = curr.next;
            }
            return null;
        }

        private boolean keysEqual(K a, K b) {
            return a == null ? b == null : a.equals(b);
        }

        // Doubling + full rehash of every entry — exactly why the real HashMap docs recommend
        // sizing it upfront if you know roughly how many entries you'll insert.
        @SuppressWarnings("unchecked")
        private void resize() {
            Entry<K, V>[] old = buckets;
            buckets = new Entry[old.length * 2];
            size = 0;

            for (Entry<K, V> head : old) {
                Entry<K, V> curr = head;
                while (curr != null) {
                    put(curr.key, curr.value); // rehash into the new, larger table
                    curr = curr.next;
                }
            }
        }

        int size() {
            return size;
        }
    }

    public static void main(String[] args) {
        SimpleHashMap<String, Integer> map = new SimpleHashMap<>();

        map.put("apple", 1);
        map.put("banana", 2);
        map.put("cherry", 3);
        map.put("apple", 10); // overwrite

        System.out.println("apple -> " + map.get("apple"));
        System.out.println("banana -> " + map.get("banana"));
        System.out.println("missing -> " + map.get("missing"));

        map.remove("banana");
        System.out.println("banana after remove -> " + map.get("banana"));
        System.out.println("size -> " + map.size());

        // Force several resizes and confirm lookups still work correctly afterward.
        for (int i = 0; i < 50; i++) {
            map.put("key" + i, i);
        }
        System.out.println("size after bulk insert -> " + map.size());
        System.out.println("key37 -> " + map.get("key37"));
    }
}
