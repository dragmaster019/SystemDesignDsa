# HLD Concepts — Interview-Ready Guide

Every concept below follows this structure:
**What it is → Why it exists → How it works → When to use it → Interview answer**

---

## 1. CAP Theorem

### What it is
In a distributed system you can only guarantee **2 of these 3**:
- **C**onsistency — every read gets the most recent write
- **A**vailability — every request gets a response (no timeout)
- **P**artition Tolerance — system keeps working even if some nodes can't talk to each other

### The brutal truth
**Network partitions ALWAYS happen** in distributed systems (cables fail, nodes crash).
So P is non-negotiable. Real choice is: **CP or AP**.

```
CP (Consistency + Partition Tolerance)
  → During a partition, system refuses to respond rather than return stale data
  → Examples: HBase, Zookeeper, MongoDB (in strong consistency mode)
  → Use when: banking, payments, inventory — wrong data is worse than no data

AP (Availability + Partition Tolerance)
  → During a partition, system responds but data might be stale
  → Examples: Cassandra, DynamoDB, CouchDB
  → Use when: social feeds, likes count, recommendations — stale data is acceptable
```

### Interview answer
> "CAP says you can't have all three in a distributed system. Since partitions are inevitable, you choose CP or AP based on the domain. For financial systems I'd pick CP — I'd rather return an error than show a wrong account balance. For a social feed I'd pick AP — showing a slightly stale post count is fine, uptime matters more."

---

## 2. Caching

### What it is
Store frequently-read data in fast memory (Redis, Memcached) so the DB isn't hit every time.

### The 3 caching patterns

**Cache-Aside (most common)**
```
Read:  check cache → miss → read DB → write to cache → return
Write: write to DB → delete cache key (invalidate)
```
App controls the cache manually. Cache only populated on demand (lazy).

**Write-Through**
```
Write: write to cache AND DB simultaneously
Read:  always hits cache (always warm)
```
Slower writes, but cache is always fresh. Good when reads >> writes.

**Write-Behind (Write-Back)**
```
Write: write to cache immediately, DB updated asynchronously later
Read:  hits cache
```
Fast writes, risk of data loss if cache crashes before DB sync.

### Cache eviction policies
- **LRU** (Least Recently Used) — evict the item not accessed longest. Most common.
- **LFU** (Least Frequently Used) — evict item accessed fewest times
- **TTL** — evict after a fixed time regardless of access

### What to cache
✅ Cache: user sessions, product catalog, account metadata, config/reference data  
❌ Don't cache: user-specific financial transactions, OTPs, anything that must be real-time

### Cache stampede problem
If a popular cache key expires, thousands of requests all hit the DB simultaneously.
Fix: use **mutex lock** (only one request fetches, others wait) or **probabilistic early expiry** (refresh before it expires).

### Interview answer
> "We use Redis with cache-aside pattern. On a read, check Redis first. Miss → query DB → store with TTL. On a write, always write to DB first, then delete the cache key so the next read gets fresh data. TTL handles stale data for low-stakes data; active invalidation handles anything financial."

---

## 3. DB Scaling

### Vertical Scaling (Scale Up)
Bigger machine — more CPU, RAM, faster disk.
- Simple, no code changes
- Has a hard limit (you can't buy an infinite server)
- Single point of failure
- Use first — cheapest and fastest fix

### Horizontal Scaling (Scale Out)
More machines. Two main strategies:

**Read Replicas**
```
All WRITES → Primary DB
All READS  → Replica 1, Replica 2, Replica 3 (copies of primary)
```
- Replication is async → replicas may lag behind primary by milliseconds
- 80-90% of traffic is reads in most apps → huge relief on primary
- Your own code does this: `useReaderInstance = true` in the AUM report

**When replica lag matters:** if user writes data and immediately reads it back, they might get stale data from replica. Fix: route that specific read to primary, or use **read-your-own-writes consistency**.

### Connection Pooling
DB connections are expensive to create. A pool keeps N connections alive and reuses them.
Without pooling: each Lambda invocation opens+closes a connection → DB runs out.
With pooling: 10 connections serve 1000 concurrent requests.
- AWS RDS Proxy does this for Lambda → solves the "Lambda connection explosion" problem.

### Interview answer
> "First I'd add read replicas to separate read traffic from writes — that handles 80% of scaling problems. If write throughput is also a bottleneck, that's when I look at sharding. I'd also add an RDS Proxy in front to handle connection pooling, especially since Lambda creates a new connection per invocation."

---

## 4. DB Sharding & Partitioning

### Partitioning (within one DB)
Split a large table into smaller pieces **on the same server**.

**Horizontal Partitioning (most common)**
Split rows by a key range or hash:
```
Partition A: users where id 1–1M
Partition B: users where id 1M–2M
Partition C: users where id 2M–3M
```
Query planner knows which partition to scan → faster queries.

**Vertical Partitioning**
Split columns into separate tables:
```
users_core: id, name, email         (read often)
users_blob: id, profile_pic, bio    (read rarely)
```
Hot columns stay in a faster/cached table.

### Sharding (across multiple DBs)
Same idea as horizontal partitioning but each shard is a **completely separate DB server**.
```
Shard 1 (server 1): users A–F
Shard 2 (server 2): users G–M
Shard 3 (server 3): users N–Z
```

**Shard key choice is critical:**
- Good shard key: distributes data evenly, queries rarely span shards
- Bad shard key: all traffic hits one shard (hotspot), or every query needs all shards (scatter-gather)

**Common shard keys:**
- User ID (hash mod N) — works for user-centric apps
- Geographic region — users in India hit India shard
- Date/time — time-series data (logs, events)

### The downsides of sharding (say these unprompted — signals maturity)
- JOINs across shards are expensive or impossible
- Transactions across shards are hard (no ACID across machines)
- Resharding when you add servers is painful
- Added operational complexity

### Interview answer
> "Sharding is the last resort for write scaling — I'd exhaust read replicas, caching, and query optimization first. When I do shard, the shard key decision is everything. I'd choose a key that distributes load evenly and keeps related data on the same shard so queries don't scatter. The main tradeoffs are losing cross-shard JOINs and distributed transactions."

---

## 5. Load Balancing

### What it is
Distribute incoming requests across multiple servers so no single server is overwhelmed.

```
Client
  ↓
Load Balancer  (single entry point)
  ↙    ↓    ↘
Server1  Server2  Server3
```

### Load balancing algorithms

| Algorithm | How it works | Use when |
|---|---|---|
| Round Robin | Request 1→S1, 2→S2, 3→S3, 4→S1... | Servers are identical |
| Least Connections | Send to server with fewest active requests | Requests have variable processing time |
| IP Hash | Hash client IP → always same server | Need session stickiness |
| Weighted Round Robin | Server with more capacity gets more requests | Servers have different specs |

### Layer 4 vs Layer 7 Load Balancer
- **L4 (Transport)** — routes based on IP/TCP only. Fast. Doesn't look at request content.
- **L7 (Application)** — routes based on URL, headers, cookies. Can route `/api/*` to API servers and `/static/*` to CDN. AWS ALB is L7.

### Health checks
Load balancer pings each server every few seconds. If a server doesn't respond → removed from rotation automatically. This is how you get **zero-downtime deployments** — deploy to one server at a time.

### Interview answer
> "Load balancer sits in front of all servers — it's the single public IP/DNS. It distributes requests using least-connections or round-robin, runs health checks to auto-remove unhealthy instances, and enables horizontal scaling by adding servers behind it without changing the client. For the web tier I'd use AWS ALB (L7) so I can route by URL path."

---

## 6. Rate Limiting

### What it is
Limit how many requests a client can make in a time window. Protects your service from abuse, bots, and accidental overload.

### Algorithms

**Token Bucket (most common)**
```
Bucket holds N tokens. Tokens refill at rate R per second.
Each request consumes 1 token. No token → reject with 429.
```
Allows short bursts (use up saved tokens) then limits to steady rate.
AWS API Gateway uses this: "rate" = refill speed, "burst" = bucket size.

**Fixed Window Counter**
```
Count requests per time window (e.g. 100 req per minute).
Reset counter every minute.
```
Simple but has edge case: 100 requests at 00:59 + 100 at 01:01 = 200 requests in 2 seconds.

**Sliding Window Log**
Store timestamp of each request. Count timestamps within last N seconds. Most accurate, most memory.

**Sliding Window Counter**
Hybrid — use previous window's count weighted by overlap. Good balance.

### Where to implement
- **API Gateway level** — before Lambda even runs (cheapest — rejected early)
- **Application level** — more granular (per user, per endpoint)
- **Redis-based** — shared rate limit across multiple Lambda instances using `INCR` + `EXPIRE`

```js
// Redis sliding window rate limit
async function isAllowed(userId) {
  const key = `rate:${userId}:${Math.floor(Date.now() / 60000)}` // per-minute bucket
  const count = await redis.incr(key)
  if (count === 1) await redis.expire(key, 60) // set TTL on first request
  return count <= 100 // allow 100 req/min
}
```

### Interview answer
> "I implement rate limiting at two levels: API Gateway handles coarse throttling (protect from DDoS), and application-level Redis-based rate limiting handles per-user quotas. Token bucket is my default algorithm because it allows legitimate bursts while enforcing a steady-state limit. If a user hits the limit, return 429 with a Retry-After header."

---

## 7. The "When to Use What" Decision Tree

This is what interviewers actually test — not definitions, but judgment.

```
System is slow — where do you look first?
│
├─ Slow reads?
│   ├─ Same data read repeatedly → ADD CACHE (Redis)
│   ├─ DB queries slow → ADD INDEXES, optimize queries
│   └─ Too many reads hitting DB → ADD READ REPLICAS
│
├─ Slow writes?
│   ├─ DB write throughput maxed → SHARD the DB
│   ├─ Writes can be async → USE MESSAGE QUEUE (SQS/Kafka)
│   └─ Writes are synchronous but slow → optimize transactions, batch writes
│
├─ Single server maxed out?
│   ├─ CPU/RAM → VERTICAL SCALE first (quick fix)
│   └─ Still not enough → HORIZONTAL SCALE + LOAD BALANCER
│
├─ Being abused / DDoS?
│   └─ RATE LIMITING at API Gateway + WAF
│
└─ Distributed system correctness issue?
    ├─ Need strong consistency (financial) → CP system (locks, single master)
    └─ Need high availability (social) → AP system (eventual consistency)
```

---

## 8. How to answer a system design question end-to-end

**Framework (say this order every time):**

1. **Clarify requirements** — "Is this read-heavy or write-heavy? How many users? Any consistency requirements?"
2. **Estimate scale** — "1M users, 10K req/sec, 1TB data/year"
3. **High-level design** — Client → CDN → Load Balancer → App Servers → Cache → DB
4. **Deep dive** — pick the hardest parts: DB schema, sharding strategy, cache invalidation
5. **Bottlenecks & tradeoffs** — "The shard key tradeoff is...", "With eventual consistency we risk..."

**The sentence that impresses interviewers:**
> "Before I shard the database I'd first add read replicas, introduce Redis caching for hot data, and profile slow queries — sharding adds enormous operational complexity and should be a last resort. I'd only shard if write throughput was genuinely maxed out after those optimizations."

This shows you understand that **simpler solutions exist before reaching for complex ones**.

---

## Quick fire — one-line answers

| Question | Answer |
|---|---|
| What is eventual consistency? | Replicas will become consistent given enough time — no guarantee of when |
| SQL vs NoSQL — when? | SQL: structured data, relations, ACID needed. NoSQL: flexible schema, massive scale, high write throughput |
| CDN — what is it? | Edge servers near users that cache static assets (images, JS, CSS) — reduces latency + server load |
| What is a message queue? | Async buffer between services (SQS, Kafka) — producer writes, consumer reads at its own pace. Decouples services |
| Horizontal vs Vertical scaling? | Vertical = bigger machine. Horizontal = more machines + load balancer |
| What is a hotspot in sharding? | One shard getting disproportionate traffic because the shard key isn't evenly distributed |
| What is database index? | Data structure that speeds up lookups at the cost of slower writes and more storage |
| ACID vs BASE? | ACID: SQL consistency guarantees. BASE: Basically Available, Soft-state, Eventual consistency — NoSQL model |
| What is a reverse proxy? | Server that sits in front of backends (e.g. Nginx) — handles SSL, load balancing, caching, compression |
| When NOT to use microservices? | Small team, early stage — monolith is simpler. Microservices add network complexity and operational overhead |
