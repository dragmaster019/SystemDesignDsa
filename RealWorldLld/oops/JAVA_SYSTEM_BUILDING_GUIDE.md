# Java: Everything You Need to Build a Real Working System

You already know: 4 OOP pillars, interface vs abstract class (see `NotificationService.java`).
This covers the rest, in the order you'll actually need it.

---

## 1. Collections — how you store groups of objects

```java
List<String> names = new ArrayList<>();   // ordered, allows duplicates, index access
Set<String> uniqueIds = new HashSet<>();  // no duplicates, no order guarantee
Map<String, Integer> stock = new HashMap<>(); // key -> value lookup
Queue<String> jobs = new LinkedList<>();  // FIFO — process in order added

stock.put("apple", 10);
stock.get("apple");              // 10
stock.getOrDefault("banana", 0); // 0, no exception

for (Map.Entry<String, Integer> e : stock.entrySet()) {
    System.out.println(e.getKey() + " = " + e.getValue());
}
```

**When to use which:**
- `List` — you need order or duplicates (orders in a queue, a cart's items)
- `Set` — you need "does this already exist" checks fast (seen user IDs)
- `Map` — you need to look something up by key (userId -> User, productId -> Product)
- `Queue`/`Deque` — FIFO/LIFO processing (job queue, undo stack)

Your `NotificationDispatcher` already uses `List<Notification>` as a queue — that's collections in action.

---

## 2. Generics — write one class/method that works for any type, safely

```java
class Box<T> {
    private T value;
    public void put(T value) { this.value = value; }
    public T get() { return value; }
}

Box<String> b1 = new Box<>();
b1.put("hello");

Box<Integer> b2 = new Box<>();
b2.put(42);
```

Without generics you'd use `Object` and cast everywhere — generics catch type mistakes at
compile time instead of crashing at runtime. `List<Notification>` in your dispatcher is generics.

---

## 3. Exceptions — handling things that go wrong

```java
class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}

class InventoryService {
    private final Map<String, Integer> stock = new HashMap<>(); //40

    public void reserve(String productId, int qty) { // 1- 5 x headphone 5 piece 
        int available = stock.getOrDefault(productId, 0);
        if (qty > available) { // 5 > 40
            throw new InsufficientStockException(
                productId + " has only " + available + " left, requested " + qty);
        }
        stock.put(productId, available - qty); // 40 - 5
    }
}

// caller:
try {
    inventoryService.reserve("sku-1", 5);
} catch (InsufficientStockException e) {
    System.out.println("Order failed: " + e.getMessage());
}
```

**Rule:** create custom exceptions for business errors (`InsufficientStockException`,
`UserNotFoundException`) instead of throwing generic `RuntimeException` — it makes the
calling code's `catch` blocks meaningful

---

## 4. Design Patterns — reusable solutions to common problems

### Factory — create objects without the caller knowing which class

```java
class NotificationFactory {
    public static Notification create(String channel, String recipient, String message) {
        return switch (channel) {
            case "sms" -> new SmsNotification(recipient, message);
            case "email" -> new EmailNotification(recipient, message);
            case "push" -> new PushNotification(recipient, message);
            default -> throw new IllegalArgumentException("Unknown channel: " + channel);
        };
    }
}

// caller doesn't need to know about SmsNotification/EmailNotification classes at all:
Notification n = NotificationFactory.create("sms", "+91...", "Hello");
```

### Strategy — swap an algorithm/behavior at runtime

```java
interface PaymentStrategy {
    void pay(double amount);
}

class CreditCardPayment implements PaymentStrategy {
    public void pay(double amount) { System.out.println("Paid " + amount + " via card"); }
}

class UpiPayment implements PaymentStrategy {
    public void pay(double amount) { System.out.println("Paid " + amount + " via UPI"); }
}

class Checkout {
    private PaymentStrategy strategy;
    public Checkout(PaymentStrategy strategy) { this.strategy = strategy; }
    public void completeOrder(double amount) { strategy.pay(amount); }
}

new Checkout(new UpiPayment()).completeOrder(499.0);
```

### Singleton — exactly one instance, shared everywhere

```java
class AppConfig {
    private static final AppConfig INSTANCE = new AppConfig();
    private final Map<String, String> settings = new HashMap<>();

    private AppConfig() {} // can't be constructed from outside

    public static AppConfig getInstance() { return INSTANCE; }

    public String get(String key) { return settings.get(key); }
    public void set(String key, String value) { settings.put(key, value); }
}

AppConfig.getInstance().set("env", "production");
```

### Observer — notify multiple listeners when something happens

```java
interface OrderListener {
    void onOrderPlaced(String orderId);
}

class Order {
    private final List<OrderListener> listeners = new ArrayList<>();

    public void subscribe(OrderListener listener) { listeners.add(listener); }

    public void place(String orderId) {
        for (OrderListener l : listeners) {
            l.onOrderPlaced(orderId); // every subscriber reacts independently
        }
    }
}

Order order = new Order();
order.subscribe(id -> System.out.println("Send email for order " + id));
order.subscribe(id -> System.out.println("Notify warehouse for order " + id));
order.place("ORD-123");
```

### Builder — construct objects with many optional fields cleanly

```java
class User {
    private final String name;
    private final String email;
    private final String phone;

    private User(Builder b) {
        this.name = b.name; this.email = b.email; this.phone = b.phone;
    }

    static class Builder {
        private String name;
        private String email;
        private String phone;

        public Builder name(String name) { this.name = name; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public User build() { return new User(this); }
    }
}

User u = new User.Builder().name("Sarthak").email("s@x.com").build();
```

---

## 5. SOLID — principles that decide how you split classes/interfaces

- **S**ingle Responsibility — a class should do one thing (`InventoryService` only manages stock, not payments)
- **O**pen/Closed — add new behavior by adding new classes (new `PaymentStrategy`), not editing existing ones
- **L**iskov Substitution — a subclass must be usable anywhere its parent is expected without breaking things
- **I**nterface Segregation — many small interfaces (`Retryable`, `Payable`) beat one giant interface forcing unused methods
- **D**ependency Inversion — depend on interfaces (`PaymentStrategy`), not concrete classes (`UpiPayment`), so you can swap implementations

You already used Dependency Inversion in `Checkout` — it depends on `PaymentStrategy`, not a specific payment class.

---

## 6. Persistence — making data survive past `main()`

Simplest version (in-memory "database" using a `Map`, good enough for an LLD project):

```java
class InMemoryUserRepository {
    private final Map<String, User> users = new HashMap<>();

    public void save(User user) { users.put(user.getId(), user); }
    public User findById(String id) { return users.get(id); }
}
```

Real version: same shape, but backed by a file or JDBC connection instead of a `HashMap`.
The point — code that *uses* the repository (e.g. `OrderService`) never changes when you
swap `InMemoryUserRepository` for a real database one, **if** both implement the same
interface (`UserRepository`). That's Dependency Inversion again.

---

## How to actually practice this

Take one existing folder (`CabBooking` or `ecommerce`) and add, in order:
1. A custom exception for one failure case
2. A `Factory` for creating one family of objects
3. A `Strategy` for one piece of swappable behavior (pricing, payment)
4. A repository interface + in-memory implementation for persistence

That's a genuinely "real" system at LLD scale — not toy code, the same shapes production
Java services use, just without a database/network attached.
