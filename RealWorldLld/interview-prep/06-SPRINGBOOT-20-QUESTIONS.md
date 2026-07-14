# 20 Spring Boot Interview Questions — Complete Answers

---

**Q1. Why might @Transactional not roll back a transaction?**

Three common reasons:

1. **Checked exception thrown** — by default `@Transactional` only rolls back on `RuntimeException` (unchecked). If you throw `IOException` (checked), it commits anyway.
   Fix: `@Transactional(rollbackFor = Exception.class)`

2. **Self-invocation** — calling a `@Transactional` method from within the same class bypasses Spring's proxy. The transaction never starts.
   ```java
   // WRONG — self-call, proxy bypassed
   public void doWork() { this.save(); }

   @Transactional
   public void save() { ... }
   ```
   Fix: inject the bean into itself or move to a separate class.

3. **Method is not public** — Spring's proxy only intercepts public methods. `@Transactional` on private/protected methods is silently ignored.

---

**Q2. How does Spring Boot auto-configuration work internally?**

When app starts, Spring Boot reads `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` (or `spring.factories` in older versions). This lists hundreds of `@Configuration` classes.

Each is annotated with `@ConditionalOn...`:
```java
@ConditionalOnClass(DataSource.class)      // only if DataSource is on classpath
@ConditionalOnMissingBean(DataSource.class) // only if you haven't defined your own
@EnableConfigurationProperties(DataSourceProperties.class) // reads application.properties
public class DataSourceAutoConfiguration { ... }
```

So if you add `spring-boot-starter-data-jpa` to `pom.xml` → `DataSource`, `EntityManager`, `TransactionManager` are all auto-configured. Override any by defining your own `@Bean`.

**Interview line:** "Auto-configuration is just conditional `@Configuration` classes triggered by what's on the classpath — you opt out by defining your own bean, not by turning off a switch."

---

**Q3. What's the difference between @Component, @Service, and @Repository?**

All three register a bean in the Spring container. Functionally almost identical. The difference is **semantic** and one **technical**:

| Annotation | Meaning | Extra behaviour |
|---|---|---|
| `@Component` | Generic Spring-managed bean | None |
| `@Service` | Business logic layer | None (just clarity) |
| `@Repository` | Data access layer | **Translates DB exceptions** to Spring's `DataAccessException` hierarchy |

`@Repository`'s exception translation is the only real technical difference — SQL exceptions from different DBs (MySQL, Postgres) get wrapped into a consistent Spring exception so your service layer doesn't depend on DB-specific exceptions.

---

**Q4. How would you handle duplicate API requests in a payment service?**

**Idempotency keys.** Client generates a unique key per payment attempt and sends it in the header (`Idempotency-Key: uuid`).

Server flow:
```
1. Receive request with idempotency key
2. Check Redis/DB: has this key been processed before?
   YES → return the stored response (don't process again)
   NO  → process payment → store (key → response) in Redis with TTL → return response
```

```java
String key = request.getHeader("Idempotency-Key");
String cached = redis.get("payment:" + key);
if (cached != null) return deserialize(cached); // return same response

PaymentResult result = processPayment(request);
redis.set("payment:" + key, serialize(result), Duration.ofDays(1));
return result;
```

This means even if the client retries (network timeout, user double-clicks), the payment fires exactly once.

---

**Q5. What happens if two users update the same record simultaneously?**

**Lost update problem.** User A reads record (balance=100), User B reads same record (balance=100), both subtract 10, both write 90 — one update is lost.

Two solutions:

**Optimistic Locking** (no DB lock held — better for low-contention):
```java
@Entity
public class Account {
    @Version
    private int version; // Spring Data JPA manages this
}
// If version mismatches on update → OptimisticLockException → retry
```

**Pessimistic Locking** (DB row locked while one user holds it — better for high-contention financial data):
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT a FROM Account a WHERE a.id = :id")
Account findByIdForUpdate(@Param("id") Long id);
// Row is locked until transaction commits
```

**Interview answer:** "For payment/financial records I'd use pessimistic locking — the cost of a conflict is too high. For lower-stakes data like profile updates I'd use optimistic locking with a version column and let clients retry on conflict."

---

**Q6. How does Spring Boot manage database connections using HikariCP?**

HikariCP is the default connection pool since Spring Boot 2.x. It maintains a **pool of pre-created DB connections** that are reused across requests.

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10      # max connections in pool
      minimum-idle: 5            # keep at least 5 alive even when idle
      connection-timeout: 30000  # wait 30s for a connection before throwing
      idle-timeout: 600000       # close idle connections after 10min
      max-lifetime: 1800000      # force-close connections after 30min (prevents stale)
```

Flow: request arrives → borrow a connection from pool → execute query → **return** connection to pool (not close). If pool is exhausted, request waits up to `connection-timeout`, then throws `SQLTimeoutException`.

HikariCP is fastest because it uses a lock-free design and keeps connections warm.

---

**Q7. What are the most common causes of connection pool exhaustion?**

1. **Connection not returned** — forgot to close ResultSet/Statement, or exception thrown before `connection.close()`. Fix: always use try-with-resources.

2. **Long-running transactions** — `@Transactional` method holds a connection for its entire duration. A slow external API call inside a transaction = connection held idle for seconds.

3. **Pool too small for load** — `maximum-pool-size=10` but 50 concurrent requests. Fix: increase pool size (but don't go crazy — DB also has a max connection limit).

4. **Lambda + RDS without connection pooler** — each Lambda invocation creates a new connection. 1000 concurrent Lambdas = 1000 connections. Fix: use RDS Proxy as a pooler in front of the DB.

5. **Deadlocks** — threads waiting on each other holding connections. Fix: consistent lock ordering, reduce transaction scope.

---

**Q8. How would you secure REST APIs using Spring Security and JWT?**

Flow:
```
1. POST /login  → validate username+password → generate JWT → return to client
2. Client stores JWT (httpOnly cookie or Authorization header)
3. Every subsequent request: Authorization: Bearer <token>
4. Spring Security filter intercepts → validates JWT signature + expiry
5. Extracts user/roles → sets SecurityContext → request proceeds
```

```java
// Filter that runs on every request
public class JwtAuthFilter extends OncePerRequestFilter {
    protected void doFilterInternal(HttpServletRequest req, ...) {
        String token = extractToken(req); // from Authorization header
        if (token != null && jwtUtil.isValid(token)) {
            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                    jwtUtil.getUsername(token), null, jwtUtil.getRoles(token));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(req, res);
    }
}
```

JWT is stateless — no session stored on server. Good for microservices and Lambda (no sticky sessions needed). Tradeoff: can't invalidate a token before expiry — use short TTL (15min) + refresh tokens.

---

**Q9. What's the difference between @PathVariable and @RequestParam?**

```java
// @PathVariable — part of the URL path itself
@GetMapping("/accounts/{accountId}")
public Account get(@PathVariable String accountId)
// Called as: GET /accounts/abc-123

// @RequestParam — query string parameter
@GetMapping("/accounts")
public List<Account> list(@RequestParam(required = false) String type)
// Called as: GET /accounts?type=SMA
```

Rule of thumb: PathVariable for **identity** (which resource), RequestParam for **filtering/options** (how to return it).

---

**Q10. How would you improve a slow Spring Data JPA query?**

Step by step:
1. **Log the SQL** — `spring.jpa.show-sql=true` — see what query JPA actually generates
2. **Run EXPLAIN** on the query — check if it's doing a full table scan
3. **Add index** on the WHERE/JOIN column
4. **Fix N+1** — use `@EntityGraph` or `JOIN FETCH` to eager-load associations
5. **Use projections** — don't fetch entire entity if you only need 2 fields:
   ```java
   interface AccountSummary { String getId(); String getName(); }
   List<AccountSummary> findAllProjectedBy(); // SELECT id, name only
   ```
6. **Use native query** for complex aggregations JPA can't express efficiently
7. **Pagination** — never load 100k rows into memory: `Pageable pageable`
8. **Cache** — if result is stable, cache with `@Cacheable`

---

**Q11. What is the N+1 query problem, and how do you fix it?**

Fetch a list of orders → loop and fetch each order's user separately = 1 + N queries.

```java
// WRONG — triggers N+1
List<Order> orders = orderRepo.findAll(); // 1 query
orders.forEach(o -> o.getUser().getName()); // N lazy queries

// FIX 1 — JOIN FETCH
@Query("SELECT o FROM Order o JOIN FETCH o.user")
List<Order> findAllWithUser();

// FIX 2 — @EntityGraph
@EntityGraph(attributePaths = {"user"})
List<Order> findAll();
```

Both produce one SQL with a JOIN instead of N separate SELECTs.

---

**Q12. How would you debug a Spring Boot application that's slow only in production?**

1. **Check slow query logs** — is the DB slow? Queries that run in 5ms in dev take 2s in prod with real data volume.
2. **Thread dump** — `kill -3 <pid>` or Actuator `/actuator/threaddump` — are threads blocked waiting for locks or connections?
3. **Heap dump** — is GC running constantly? High GC pause = memory pressure.
4. **Connection pool metrics** — `/actuator/metrics/hikaricp.connections.pending` — are requests queuing for a DB connection?
5. **Enable slow request logging** — log requests that take >500ms with their full context.
6. **Check external calls** — is a third-party API slow in prod? Wrap in circuit breaker.
7. **Compare data volume** — prod has 10M rows, dev has 100. A missing index shows up only at scale.
8. **Distributed tracing** — Zipkin/Jaeger shows which service/span is slow across microservices.

---

**Q13. What's the difference between synchronous and asynchronous processing in Spring Boot?**

**Synchronous:** caller waits until processing is done. Simple. If processing takes 5s, HTTP connection held for 5s.

**Asynchronous:** caller gets response immediately, processing happens in background.

```java
// @Async — runs in a separate thread pool
@Async
public CompletableFuture<String> sendEmail(String to) {
    emailService.send(to); // runs in background
    return CompletableFuture.completedFuture("sent");
}
```

Must enable with `@EnableAsync` on a config class.

**When async:** sending emails, generating reports, calling slow external APIs, anything where the user doesn't need to wait for the result.

**Async via message queue (better for reliability):** instead of `@Async` (which loses work if server crashes), put work on SQS/Kafka and have a consumer process it. Work survives crashes.

---

**Q14. How would you prevent duplicate Kafka message processing?**

Kafka can deliver the same message more than once (at-least-once delivery). Three strategies:

1. **Idempotent consumer** — make the operation safe to run twice:
   - Insert: use `INSERT IGNORE` or `ON DUPLICATE KEY UPDATE`
   - Store a processed message ID, skip if already seen:
   ```java
   if (processedIds.contains(message.getId())) return; // skip duplicate
   processedIds.add(message.getId());
   process(message);
   ```

2. **Exactly-once with transactions** — Kafka + DB transaction: consume message and write to DB atomically. If DB write fails, message is not committed as consumed.

3. **Deduplication table** — store `(topic, partition, offset)` in DB. Before processing, check if already processed.

```java
@KafkaListener(topics = "payments")
public void consume(PaymentEvent event) {
    if (dedupeRepo.existsByEventId(event.getId())) return;
    processPayment(event);
    dedupeRepo.save(new ProcessedEvent(event.getId()));
}
```

---

**Q15. What's the purpose of Spring Boot Actuator, and which endpoints do you use most?**

Actuator exposes **production-ready monitoring endpoints** over HTTP without writing any code.

Enable: `management.endpoints.web.exposure.include=*`

Most used in real work:

| Endpoint | What it shows |
|---|---|
| `/actuator/health` | App up/down, DB status, disk space — used by load balancer health checks |
| `/actuator/metrics` | JVM memory, GC, HTTP request counts, HikariCP pool stats |
| `/actuator/env` | All resolved config properties (debug config issues) |
| `/actuator/threaddump` | All thread states — debug deadlocks/blocked threads |
| `/actuator/loggers` | Change log level at runtime without restart |
| `/actuator/info` | App version, git commit hash |

**Always secure Actuator endpoints** — expose only `/health` publicly, require admin role for the rest.

---

**Q16. How would you upload large files without causing memory issues?**

Default: Spring reads entire file into memory → `OutOfMemoryError` for large files.

Fix — **stream the file** directly to storage without buffering in memory:

```java
@PostMapping("/upload")
public ResponseEntity<String> upload(@RequestParam MultipartFile file) throws IOException {
    // Stream directly to S3 — never loads full file into heap
    s3Client.putObject(PutObjectRequest.builder()
        .bucket("my-bucket")
        .key(file.getOriginalFilename())
        .build(),
        RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    return ResponseEntity.ok("uploaded");
}
```

Also configure:
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 500MB
      max-request-size: 500MB
      file-size-threshold: 2KB  # write to disk beyond this, not memory
```

For very large files: use **S3 pre-signed URL** — client uploads directly to S3, bypassing your server entirely. Best approach.

---

**Q17. What's the difference between BeanFactory and ApplicationContext?**

| | BeanFactory | ApplicationContext |
|---|---|---|
| Bean creation | Lazy (created on first request) | Eager (all singletons created at startup) |
| Features | Basic DI only | BeanFactory + events, i18n, AOP, `@Async`, `@Scheduled`, `@Transactional` |
| Use in production | Almost never | Always |

`ApplicationContext` is the full-featured container. `BeanFactory` is the low-level interface it extends. You'll never use `BeanFactory` directly in a Spring Boot app — `ApplicationContext` is always used.

---

**Q18. How would you trace a request across multiple Spring Boot microservices?**

Use **distributed tracing** — each request gets a unique `traceId`. Each service adds a `spanId` for its portion of work. Both are propagated in HTTP headers.

**Spring Cloud Sleuth** (now **Micrometer Tracing**) does this automatically:
- Injects `X-B3-TraceId` and `X-B3-SpanId` headers into every outgoing HTTP call
- Logs automatically include `[traceId, spanId]`
- Send traces to **Zipkin** or **Jaeger** for visualization

```
Request → Service A (traceId=abc, spanId=1)
              → Service B (traceId=abc, spanId=2)
                    → DB query (traceId=abc, spanId=3)
```

In Zipkin you search by `traceId=abc` and see the full waterfall of every service call and how long each took.

**Without tracing:** you grep logs across 5 services trying to match timestamps. With tracing: one search, full picture.

---

**Q19. What steps would you take before scaling a Spring Boot application?**

1. **Profile first** — don't scale blind. Find the actual bottleneck (CPU? DB? memory? network?)
2. **Fix the obvious** — N+1 queries, missing indexes, large objects held in memory
3. **Add caching** — Redis for hot reads. Often eliminates scaling need entirely.
4. **Add read replicas** — route reads to replicas, writes to primary
5. **Tune the connection pool** — right-size HikariCP for your load
6. **Make the app stateless** — sessions in Redis not in-memory, so any instance can handle any request
7. **Add a load balancer** — only now add more app instances (horizontal scale)
8. **Check external dependencies** — is a downstream service the real bottleneck?
9. **Set up autoscaling** — AWS ECS/EKS can scale instances based on CPU/request count

**Interview line:** "Scaling is the last step, not the first. I fix the code and queries before adding machines — hardware is expensive, query optimizations are free."

---

**Q20. If a production issue is reported but there are no exceptions in the logs, what's your debugging approach?**

No exception = not a crash. Could be: slow response, wrong data, logic bug, silent failure.

Step by step:

1. **Check response times** — is the API slow? Metrics + slow request logs
2. **Check INFO/DEBUG logs** — exception was caught and swallowed somewhere
   ```java
   catch (Exception e) { } // ← worst anti-pattern, search for empty catch blocks
   ```
3. **Check external dependencies** — is a downstream API returning 200 but with wrong data?
4. **Check DB data** — did a write silently not persist? Transaction rolled back silently?
5. **Reproduce with real production data** — copy prod DB snapshot to staging
6. **Thread dump** — are threads stuck (deadlock, waiting on a lock)?
7. **Check conditional logic** — a feature flag, a null check, a wrong env variable causing wrong branch
8. **Enable DEBUG logging temporarily** — `POST /actuator/loggers/com.yourpackage` with `{"configuredLevel":"DEBUG"}` — no restart needed
9. **Correlate with deploys/config changes** — did the issue start right after a deploy? Check git diff.
10. **Ask the user for exact steps** — "no error" usually means they're not hitting the same code path you're testing

**Interview line:** "My first assumption when there's no exception is that an exception was swallowed somewhere. I search for empty catch blocks or catch blocks that only log at DEBUG level."
