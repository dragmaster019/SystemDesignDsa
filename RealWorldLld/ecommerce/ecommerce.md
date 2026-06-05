# E-Commerce System — LLD Complete Guide

> **Purpose of this document**: Teach you how to approach any LLD from scratch — using this e-commerce system as the worked example. Every data structure and design pattern choice is explained with the "why", not just the "what".

---

## Table of Contents

1. [How to Start Any LLD — The Mental Framework](#1-how-to-start-any-lld--the-mental-framework)
2. [Step-by-step: Applying the Framework to E-Commerce](#2-step-by-step-applying-the-framework-to-e-commerce)
3. [Complete Class Diagram (Text)](#3-complete-class-diagram-text)
4. [Entity Lifecycle — State Machines](#4-entity-lifecycle--state-machines)
5. [Design Patterns Used](#5-design-patterns-used)
6. [SOLID Principles — Applied](#6-solid-principles--applied)
7. [Data Structures — Why Each Was Chosen](#7-data-structures--why-each-was-chosen)
8. [Full Flow Walkthrough](#8-full-flow-walkthrough)
9. [How to Compile and Run](#9-how-to-compile-and-run)
10. [How to Extend This Design](#10-how-to-extend-this-design)

---

## 1. How to Start Any LLD — The Mental Framework

Every time you get a new LLD problem (parking lot, cab booking, food delivery, etc.), follow these 7 steps in order. Never jump to code immediately.

---

### Step 1 — Identify the ACTORS (who uses the system?)

Actors are the human roles that interact with the system. They are NOT entities in your code — they tell you what SERVICES you'll need.

**Ask**: "Who does what in this system?"

| Actor | What they do |
|-------|-------------|
| User | Browses products, manages cart, places orders, tracks delivery |
| Dealer | Adds products, manages stock, accepts/ships orders |
| Delivery Partner | Picks up packages, updates delivery status |

---

### Step 2 — Identify the ENTITIES (what are the nouns?)

Read the requirements and underline every noun. Each important noun becomes a class (Model).

**Question to ask**: "What things exist in this system that have state and identity?"

For e-commerce:
- User, Dealer, Product, Cart, CartItem, Order, Delivery

**How to spot a real entity vs. a fake one**:
- Real entity: has its own ID, has lifecycle states, multiple actors interact with it
- Fake entity: just a field on another object (e.g., "address" is NOT a separate entity here)

---

### Step 3 — Define LIFECYCLES (what states can each entity be in?)

Every important entity goes through states. Model them as enums.

**Question to ask**: "What are all the possible states this entity can be in?"

Then draw arrows: which states can transition to which?

```
Order:   CART → PLACED → ACCEPTED → PACKED → SHIPPED → OUT_FOR_DELIVERY → DELIVERED
                                                      ↘ CANCELLED / RETURNED

Delivery: PENDING → ASSIGNED → PICKED_UP → OUT_FOR_DELIVERY → DELIVERED
```

If you can't draw the state machine, you don't understand the system yet. Draw it BEFORE writing any code.

---

### Step 4 — Map RELATIONSHIPS (who owns what?)

For each entity, ask: "Does this entity belong to another? Does it reference another by ID?"

Use IDs for references (like a database foreign key), NOT direct object references. This keeps your data in a central store.

```
Product   → belongs to Dealer    (dealerId in Product)
Cart      → belongs to User      (userId in Cart)
CartItem  → belongs to Cart      (part of Cart's item map)
Order     → belongs to User      (userId in Order)
Order     → references Dealer    (dealerId in Order)
Delivery  → belongs to Order     (orderId in Delivery)
Order     → references Delivery  (deliveryId in Order, set after shipping)
```

---

### Step 5 — Design SERVICES (what are the verbs / operations?)

Services are the "doers". Each service owns one entity's lifecycle.

**Rule**: One service = one entity family. Never let one service do too much.

| Service | Owns | Key Operations |
|---------|------|----------------|
| UserService | User | register, browse, placeOrder (Facade) |
| DealerService | Dealer + Product | registerDealer, addProduct, acceptOrder, shipOrder |
| CartService | Cart + CartItem | addToCart, removeFromCart, validate, applyDiscount |
| OrderService | Order | createOrder, updateStatus, viewHistory |
| DeliveryService | Delivery | createDelivery, updateStatus |

---

### Step 6 — Choose DATA STRUCTURES (what queries does each entity need?)

This is where most people go wrong — they use ArrayList for everything. Instead, ask:

**"What queries do I need to run on this collection?"**

| Query Needed | Best Structure | Why |
|-------------|---------------|-----|
| Find user/product/order by ID | `HashMap<Integer, Entity>` | O(1) lookup |
| Products sorted by price, range query | `TreeMap<Double, List<Integer>>` | O(log n) sorted access + subMap |
| Products in a category | `HashMap<String, List<Integer>>` | O(1) category lookup |
| Dealer processes orders oldest-first | `ArrayDeque` as FIFO queue | O(1) offer/poll |
| User's order history, most recent first | `ArrayDeque` with offerFirst | O(1) front-insert |
| Cart items in insertion order | `LinkedHashMap<Integer, CartItem>` | O(1) + ordered iteration |

---

### Step 7 — Apply DESIGN PATTERNS (what complexity needs to be managed?)

Look at your design and ask these questions:

| Question | Pattern to Apply |
|----------|-----------------|
| Is construction of an object complex/multi-step? | **Builder** |
| Do multiple objects need to react to state changes? | **Observer** |
| Does the same operation need different algorithms? | **Strategy** |
| Should one entry point hide complexity from the caller? | **Facade** |
| Should only one instance of a class exist? | **Singleton** |
| Does object creation logic need to be centralized? | **Factory** |

---

## 2. Step-by-step: Applying the Framework to E-Commerce

### Requirements (from the original LLD notes)

> "User goes to site, views product, adds to cart, checks out. Order goes to dealer, dealer packs and ships. Delivery partner marks delivered. Edge case: if dealer marks out-of-stock, don't show on homepage."

### Applying Step 1-7

**Actors identified**: User, Dealer, Delivery Partner
**Entities identified**: User, Dealer, Product, Cart, CartItem, Order, Delivery
**Lifecycles**: OrderStatus (9 states), DeliveryStatus (5 states), PaymentStatus (4 states)
**Relationships**: mapped via IDs in each model class
**Services**: 5 services, each with clear ownership
**Data structures**: chosen per query pattern (see Section 7)
**Patterns**: Builder (Order), Observer (notifications), Strategy (pricing), Facade (UserService), Singleton (DataStore)

---

## 3. Complete Class Diagram (Text)

```
┌─────────────────────────────────────────────────────────────────┐
│                         MODEL LAYER                             │
├─────────────┬──────────────┬─────────────┬──────────────────────┤
│   User      │   Dealer     │   Product   │   CartItem           │
│─────────────│──────────────│─────────────│──────────────────────│
│ userId      │ dealerId     │ productId   │ productId            │
│ name        │ companyName  │ name        │ quantity             │
│ phoneNumber │ productIds   │ category    │ priceAtAddTime ◄─────┤
│ shippingAddr│              │ price       │   (price snapshot)   │
│ cartId ─────┼──────────────┼─────────────┼──────────────────────┤
│             │              │ stockCount  │   Cart               │
│             │              │ dealerId    │──────────────────────│
│             │              │ isAvailable │ cartId               │
│             │              │             │ userId               │
│             │              │             │ items: LinkedHashMap │
│             │              │             │   <productId,CartItem>│
│             │              │             │ pricingStrategy      │
├─────────────┴──────────────┴─────────────┴──────────────────────┤
│   Order (built via OrderBuilder)   │   Delivery                 │
│────────────────────────────────────│────────────────────────────│
│ orderId                            │ deliveryId                 │
│ userId                             │ orderId                    │
│ dealerId                           │ deliveryPartnerId          │
│ cartSnapshot: Map<pid, CartItem>   │ status: DeliveryStatus     │
│ status: OrderStatus                │ estimatedDelivery          │
│ deliveryId                         │                            │
│ createdAt                          │                            │
└────────────────────────────────────┴────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                       SERVICE LAYER                             │
│                                                                 │
│  UserService (FACADE)                                           │
│   ├── CartService        ← manages Cart + CartItem              │
│   └── OrderService       ← manages Order lifecycle             │
│                                                                 │
│  DealerService                                                  │
│   ├── OrderService       ← shared instance                     │
│   └── DeliveryService    ← creates Delivery on shipment        │
│                                                                 │
│  DeliveryService                                                │
│   └── OrderService       ← mirrors delivery status to order    │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    PATTERN LAYER                                │
│                                                                 │
│  DataStore (Singleton)   ← all services read/write here        │
│  OrderBuilder (Builder)  ← used by OrderService                │
│  PricingStrategy (Strategy) ← plugged into Cart               │
│  OrderObserver (Observer)   ← plugged into OrderService        │
└─────────────────────────────────────────────────────────────────┘
```

---

## 4. Entity Lifecycle — State Machines

### Order Status Flow

```
                  ┌─────────┐
        (user adds│  CART   │  ← implicit (items in cart, not yet placed)
         to cart) └────┬────┘
                       │ placeOrder()
                  ┌────▼────┐
                  │ PLACED  │  ← order created, dealer notified
                  └────┬────┘
                       │ acceptNextOrder()
                  ┌────▼────┐
                  │ACCEPTED │
                  └────┬────┘
                       │ (auto on accept)
                  ┌────▼────┐
                  │ PACKED  │
                  └────┬────┘
                       │ shipOrder()
                  ┌────▼────┐
                  │SHIPPED  │  ← Delivery entity created here
                  └────┬────┘
                       │ delivery → OUT_FOR_DELIVERY
              ┌────────▼──────────┐
              │ OUT_FOR_DELIVERY  │
              └────────┬──────────┘
                       │ delivery → DELIVERED
                  ┌────▼─────┐
                  │DELIVERED │  ← terminal state ✓
                  └──────────┘
                  
         At any point before SHIPPED:
                  ┌──────────┐
                  │CANCELLED │  ← terminal state ✗
                  └──────────┘
```

### Delivery Status Flow

```
PENDING → ASSIGNED → PICKED_UP → OUT_FOR_DELIVERY → DELIVERED
                                       │
                                  (mirrors to Order status)
```

---

## 5. Design Patterns Used

### Pattern 1: Singleton — `DataStore.java`

**Category**: Creational

**Problem it solves**: All 5 services need to read and write the same data. Without Singleton, each service would create its own DataStore and they'd have different data — a User added in UserService wouldn't exist in OrderService.

**Real-world analogy**: A database connection — your entire application shares ONE connection pool, not one per class.

**How it's implemented**:
```java
public class DataStore {
    private static DataStore instance;

    private DataStore() {}   // private constructor — blocks new DataStore()

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();   // created only once
        }
        return instance;
    }
}
```

**How every service uses it**:
```java
// Every service gets the SAME instance
private DataStore dataStore = DataStore.getInstance();
```

**When to use Singleton**: When exactly one instance must coordinate access to shared state (config, cache, DB, in-memory store).

---

### Pattern 2: Builder — `OrderBuilder.java`

**Category**: Creational

**Problem it solves**: Order has 6 required fields. Without Builder:
```java
// Unreadable — what does "1717000000L" mean here?
new Order(3, 1, 2, cartSnapshot, OrderStatus.PLACED, 1717000000L);
```

With Builder:
```java
// Self-documenting, validated at build()
new OrderBuilder()
    .setOrderId(orderId)
    .setUserId(userId)
    .setDealerId(dealerId)
    .setCartSnapshot(cartSnapshot)
    .setStatus(OrderStatus.PLACED)
    .setCreatedAt(System.currentTimeMillis())
    .build();
```

**Real-world analogy**: Filling out an order form section-by-section and clicking "Submit". The form validates everything before accepting it.

**Key benefit**: The `build()` method validates — if userId or dealerId is 0, or cart is empty, it throws before creating a broken Order object.

**When to use Builder**: Object has 4+ fields, some optional, and order of setting matters OR you need validation before construction.

---

### Pattern 3: Observer — `observer/` package

**Category**: Behavioral

**Problem it solves**: When an order status changes, MULTIPLE parties need to know:
- User: "Your order has shipped!"
- Dealer: "Order #3 status updated"
- (Future) Email service, SMS service, Push notification service

**Without Observer** (wrong way):
```java
// OrderService.updateStatus — hardcoded, violates Open/Closed
public void updateStatus(int orderId, OrderStatus status) {
    order.setStatus(status);
    sendUserNotification(userId, status);    // ← coupled
    sendDealerNotification(dealerId, status); // ← coupled
    sendEmail(userId, status);               // ← have to modify this class every time
}
```

**With Observer** (right way):
```java
// OrderService — doesn't know WHO is listening
private List<OrderObserver> observers = new ArrayList<>();

public void registerObserver(OrderObserver observer) {
    observers.add(observer);
}

private void notifyObservers(int orderId, OrderStatus status, int userId, int dealerId) {
    for (OrderObserver observer : observers) {
        observer.onOrderStatusChange(orderId, status, userId, dealerId);
    }
}
```

**Adding email notifications later** — zero changes to OrderService:
```java
// Just register a new observer in Main.java
orderService.registerObserver(new EmailNotificationObserver());
orderService.registerObserver(new SMSNotificationObserver());
```

**The Interface** (Interface Segregation — only one method):
```java
public interface OrderObserver {
    void onOrderStatusChange(int orderId, OrderStatus newStatus, int userId, int dealerId);
}
```

**When to use Observer**: One event, multiple independent reactions. Classic examples: UI events, logging, notifications, audit trails.

---

### Pattern 4: Strategy — `strategy/` package

**Category**: Behavioral

**Problem it solves**: Cart pricing can change — regular price, 10% coupon, bulk discount, flash sale. Without Strategy:
```java
// CartService — violates Open/Closed, needs modification for every new pricing type
public double calculateTotal(Cart cart, String discountType) {
    if (discountType.equals("REGULAR")) { ... }
    else if (discountType.equals("COUPON_10")) { ... }
    else if (discountType.equals("BULK")) { ... }
    // add more ifs forever...
}
```

**With Strategy**:
```java
// PricingStrategy interface — CartService depends on the ABSTRACTION
public interface PricingStrategy {
    double calculate(Map<Integer, CartItem> items);
}

// Cart holds a strategy — can be swapped at runtime
public class Cart {
    private PricingStrategy pricingStrategy;

    public double calculateTotal() {
        return pricingStrategy.calculate(items);  // delegates to strategy
    }

    public void setPricingStrategy(PricingStrategy strategy) {
        this.pricingStrategy = strategy;  // swap at runtime
    }
}
```

**Adding a new discount type** — zero changes to CartService or Cart:
```java
// Just implement the interface
public class FlashSalePricingStrategy implements PricingStrategy {
    @Override
    public double calculate(Map<Integer, CartItem> items) { ... }
}

// Apply it
cart.setPricingStrategy(new FlashSalePricingStrategy());
```

**When to use Strategy**: Same operation, different algorithms, and you want to swap them at runtime. Classic examples: sorting algorithms, payment methods, shipping cost calculations.

---

### Pattern 5: Facade — `UserService.java`

**Category**: Structural

**Problem it solves**: Placing an order is not one action — it's 5:
1. Validate cart (CartService)
2. Deduct stock (Product objects)
3. Create order (OrderService → OrderBuilder)
4. Notify observers (happens inside OrderService)
5. Clear cart (CartService)

**Without Facade**, Main.java would look like:
```java
// Caller has to know the internals of 5 different classes — fragile
if (cartService.validateCart(userId)) {
    cartService.deductStock(userId);
    Order order = orderService.createOrder(userId, dealerId, cart.getItems());
    cart.getItems().clear();
    System.out.println("Order placed: " + order.getOrderId());
}
```

**With Facade**, Main.java is clean:
```java
Order order = userService.placeOrder(userId);  // one line, complexity hidden
```

**Where it lives**: `UserService.placeOrder()` — it's the Facade method.

**When to use Facade**: A sequence of operations from different subsystems that always go together. Classic examples: startup/shutdown sequences, checkout flows, authentication pipelines.

---

## 6. SOLID Principles — Applied

### S — Single Responsibility Principle

> Each class has exactly ONE reason to change.

| Class | Its ONE responsibility | What it does NOT do |
|-------|----------------------|---------------------|
| `CartService` | Manage cart state | Never creates orders |
| `OrderService` | Manage order lifecycle | Never touches cart |
| `DeliveryService` | Manage delivery tracking | Never creates orders |
| `DataStore` | Provide data access | Contains no business logic |
| `OrderBuilder` | Construct valid Order objects | Contains no business logic |

**Violation to avoid**: Don't put `createOrder()` inside `CartService` just because checkout follows cart. They are different responsibilities.

---

### O — Open/Closed Principle

> Classes should be open for extension, closed for modification.

**Where applied**: `PricingStrategy` and `OrderObserver` interfaces.

Adding a bulk discount requires ZERO changes to existing code:
```java
// New file — doesn't touch CartService at all
public class BulkDiscountStrategy implements PricingStrategy { ... }
```

Adding SMS notifications requires ZERO changes to OrderService:
```java
// New file — doesn't touch OrderService at all
public class SMSObserver implements OrderObserver { ... }
orderService.registerObserver(new SMSObserver());
```

---

### L — Liskov Substitution Principle

> Subtypes must be usable wherever their parent type is used.

**Where applied**: `RegularPricingStrategy` and `DiscountPricingStrategy` are both `PricingStrategy`. The Cart doesn't care which one it has — both work correctly.

```java
Cart cart = ...;
// These are interchangeable — Cart behaves correctly with either
cart.setPricingStrategy(new RegularPricingStrategy());
cart.setPricingStrategy(new DiscountPricingStrategy(10.0));
double total = cart.calculateTotal();  // works correctly with both
```

---

### I — Interface Segregation Principle

> Don't force classes to implement methods they don't need.

**Where applied**: `OrderObserver` has exactly ONE method. It doesn't have `onProductAdded()` or `onCartUpdated()` — observers that only care about orders don't get burdened with unrelated methods.

**What a fat interface would look like (wrong)**:
```java
// Bad — forces every observer to implement all 4, even if they only care about orders
public interface SystemObserver {
    void onOrderStatusChange(...);
    void onProductAdded(...);
    void onUserRegistered(...);
    void onCartUpdated(...);
}
```

---

### D — Dependency Inversion Principle

> Depend on abstractions, not concrete implementations.

**Where applied**:

```java
// CartService depends on PricingStrategy (interface), not RegularPricingStrategy (class)
public class Cart {
    private PricingStrategy pricingStrategy;  // ← abstraction
    // NOT: private RegularPricingStrategy strategy;  ← this would be wrong
}

// OrderService depends on OrderObserver (interface), not UserOrderObserver (class)
private List<OrderObserver> observers;  // ← abstraction
// NOT: private List<UserOrderObserver> observers;  ← this would be wrong
```

This means swapping the implementation (e.g., different pricing, different notification) requires ZERO changes to the classes that depend on it.

---

## 7. Data Structures — Why Each Was Chosen

### Core Lookup Maps — `HashMap<Integer, Entity>`

**Used for**: `users`, `dealers`, `products`, `orders`, `carts`, `deliveries`

**Why HashMap**: You almost always look up an entity by its ID. HashMap gives O(1) average time for both `get` and `put`.

**Why NOT ArrayList**: If you stored users in `ArrayList<User>`, finding a user by ID would require iterating through every element — O(n). With 1 million users, that's up to 1 million comparisons per lookup.

```
HashMap.get(userId)   → O(1)  ← jumps directly to the bucket
ArrayList linear scan → O(n)  ← checks every element
```

**Time complexity**:
| Operation | HashMap | ArrayList |
|-----------|---------|-----------|
| Find by ID | O(1) avg | O(n) |
| Insert | O(1) avg | O(1) amortized |
| Delete by ID | O(1) avg | O(n) |

---

### Price Range Index — `TreeMap<Double, List<Integer>>`

**Used for**: `priceIndex` in DataStore

**Why TreeMap**: Users want to browse "products under ₹2000". This is a **range query**. TreeMap keeps keys in sorted order (it's a Red-Black BST internally), so you can call:

```java
// O(log n + k) — get all products priced ≤ 2000.0
dataStore.getPriceIndex().headMap(2000.0, true)
```

**Why NOT HashMap**: HashMap has no concept of order. You can't ask a HashMap for "all keys less than X" — it would require iterating every entry: O(n).

**Why NOT ArrayList**: You'd have to sort it every time, or use binary search which requires the list to stay sorted (complex to maintain on inserts).

**Time complexity**:
| Operation | TreeMap | HashMap |
|-----------|---------|---------|
| Lookup by key | O(log n) | O(1) avg |
| Range query | O(log n + k) | O(n) — must scan all |
| Insert | O(log n) | O(1) avg |

**When to choose TreeMap over HashMap**: When you need sorted iteration OR range queries. Accept the O(log n) cost for richer query power.

---

### Cart Items — `LinkedHashMap<Integer, CartItem>`

**Used for**: `Cart.items`

**Why LinkedHashMap**: Cart has two requirements:
1. O(1) lookup by productId (to add or update quantity)
2. Iteration in insertion order (cart shows items as the user added them)

Regular `HashMap` gives O(1) but iteration order is undefined.
`TreeMap` gives sorted order but not insertion order, and costs O(log n).
`LinkedHashMap` gives BOTH: O(1) get/put AND iteration follows insertion order.

```java
// LinkedHashMap maintains a doubly-linked list alongside the hash table
// Iteration respects the order items were added to cart
cart.getItems().forEach((pid, item) -> {
    // items appear in the order: Earbuds, then Phone Case (as added)
});
```

**Time complexity**: Same as HashMap — O(1) get/put — with a small constant overhead for maintaining the linked list.

---

### Dealer Order Queue — `ArrayDeque<Integer>` (used as FIFO Queue)

**Used for**: `dealerOrderQueues` per dealer

**Why ArrayDeque**: Dealers must process orders in the order they were received — First In, First Out (FIFO). `ArrayDeque` used as a queue gives O(1) for both:
- `offer(orderId)` — enqueue a new order at the back
- `poll()` — dequeue the oldest order from the front

**Why NOT LinkedList**: Both implement `Deque`, but `ArrayDeque` uses a resizable circular array internally. It has better CPU cache performance (array elements are contiguous in memory) and no per-node object overhead.

**Why NOT ArrayList**: ArrayList used as a queue means `remove(0)` to dequeue the front — that's O(n) because every remaining element must shift left.

```
Correct:   ArrayDeque.offer() = O(1), ArrayDeque.poll() = O(1)
Wrong:     ArrayList.add()    = O(1), ArrayList.remove(0) = O(n) ← shifts all elements
```

---

### User Order History — `ArrayDeque<Integer>` (used as Deque)

**Used for**: `userOrderHistory` per user

**Why ArrayDeque with offerFirst**: The most recent order should appear at the top of the history. `ArrayDeque.offerFirst(orderId)` inserts at the front in O(1). When displaying history, iterating the deque naturally gives newest-first order.

**Alternative considered**: Stack (ArrayDeque can function as one) — but Deque is more flexible since we might want to iterate without popping.

---

### Observer List — `List<OrderObserver>`

**Used for**: `OrderService.observers`

**Why ArrayList (simple List)**: Observers are registered once at startup and rarely change. We just need to iterate through all of them on every status change — O(n) where n is the number of observers (typically 2-5). No lookup by ID needed, so HashMap would add unnecessary complexity.

---

### Summary Table

| Data Structure | Where Used | Key Query | Time Complexity |
|---------------|-----------|-----------|-----------------|
| `HashMap<Integer, Entity>` | Primary lookup maps | Find by ID | O(1) avg |
| `TreeMap<Double, List<Integer>>` | Price index | Range query (under ₹X) | O(log n + k) |
| `HashMap<String, List<Integer>>` | Category index | Find by category | O(1) avg |
| `LinkedHashMap<Integer, CartItem>` | Cart items | Lookup + ordered iteration | O(1) avg + ordered |
| `ArrayDeque` (Queue) | Dealer order queue | FIFO process orders | O(1) offer/poll |
| `ArrayDeque` (Deque) | User order history | Insert front + iterate | O(1) offerFirst |
| `ArrayList<OrderObserver>` | Observer list | Notify all | O(n observers) |

---

## 8. Full Flow Walkthrough

Below is the exact sequence of method calls when Alice places an order. Follow this top-to-bottom to understand how the layers interact.

```
STEP 1 — Dealer adds product
─────────────────────────────────────────────────────
dealerService.addProduct(dealerId, "Earbuds", "Electronics", 2499.0, 50)
  └─ DataStore.products.put(productId, product)          [HashMap put O(1)]
  └─ DataStore.priceIndex.computeIfAbsent(2499.0, ...).add(productId)  [TreeMap put O(log n)]
  └─ DataStore.categoryIndex.computeIfAbsent("Electronics", ...).add(productId)  [HashMap put O(1)]

STEP 2 — User adds to cart
─────────────────────────────────────────────────────
userService.addToCart(aliceId, productId=2, qty=1)
  └─ CartService.addToCart(aliceId, 2, 1)
      └─ DataStore.products.get(2)               [HashMap get O(1)] → Product found
      └─ CartService.getOrCreateCart(aliceId)
          └─ DataStore.users.get(aliceId)         [HashMap get O(1)] → User found
          └─ Cart not yet created → new Cart, DataStore.carts.put(cartId, cart)
      └─ Cart.items.put(productId=2, new CartItem(2, 1, 2499.0))   [LinkedHashMap put O(1)]
      └─ cart.calculateTotal() → PricingStrategy.calculate(items)  → 2499.0

STEP 3 — Apply discount
─────────────────────────────────────────────────────
userService.applyDiscount(aliceId, 10.0)
  └─ CartService.applyDiscount(aliceId, 10.0)
      └─ cart.setPricingStrategy(new DiscountPricingStrategy(10.0))  [Strategy SWAP]
      └─ cart.calculateTotal()  → 2499.0 * 0.9 = 2249.1

STEP 4 — Place order (Facade)
─────────────────────────────────────────────────────
userService.placeOrder(aliceId)   ← Facade entry point
  └─ CartService.validateCart(aliceId)
      └─ Check product stock >= quantity  → OK
  └─ DataStore.products.get(firstProductId).getDealerId()  → dealerId=1
  └─ Deduct stock: product.setStockCount(50 - 1)  → stock = 49
  └─ OrderService.createOrder(aliceId, dealerId=1, cartSnapshot)
      └─ OrderBuilder
              .setOrderId(nextOrderId)
              .setUserId(aliceId)
              .setDealerId(1)
              .setCartSnapshot(cartSnapshot)
              .setStatus(PLACED)
              .build()  → Order validated and created
      └─ DataStore.orders.put(orderId, order)                    [HashMap put O(1)]
      └─ DataStore.dealerOrderQueues.get(1).offer(orderId)       [ArrayDeque.offer O(1)]
      └─ DataStore.userOrderHistory.get(aliceId).offerFirst(orderId) [ArrayDeque.offerFirst O(1)]
      └─ notifyObservers(orderId, PLACED, aliceId, 1)
          └─ UserOrderObserver.onOrderStatusChange(...)  → "[USER #1] Order #1 is now: PLACED"
          └─ DealerOrderObserver.onOrderStatusChange(...) → "[DEALER #1] Order #1 updated to: PLACED"
  └─ cart.getItems().clear()  → cart emptied

STEP 5 — Dealer accepts order
─────────────────────────────────────────────────────
dealerService.acceptNextOrder(dealerId=1)
  └─ DataStore.dealerOrderQueues.get(1).poll()   [ArrayDeque.poll O(1)] → orderId=1
  └─ OrderService.updateStatus(1, ACCEPTED)
      └─ notifyObservers(...)  → "[USER #1] Order #1 is now: ACCEPTED"
  └─ OrderService.updateStatus(1, PACKED)
      └─ notifyObservers(...)  → "[USER #1] Order #1 is now: PACKED"

STEP 6 — Dealer ships order
─────────────────────────────────────────────────────
dealerService.shipOrder(dealerId=1, orderId=1)
  └─ OrderService.updateStatus(1, SHIPPED)
      └─ notifyObservers(...)  → "[USER #1] Order #1 is now: SHIPPED"
  └─ DeliveryService.createDelivery(orderId=1)
      └─ new Delivery(deliveryId=1, orderId=1)
      └─ DataStore.deliveries.put(1, delivery)    [HashMap put O(1)]
      └─ order.setDeliveryId(1)

STEP 7 — Delivery partner updates
─────────────────────────────────────────────────────
deliveryService.updateDeliveryStatus(1, PICKED_UP)
  └─ delivery.setStatus(PICKED_UP)
  └─ (no order status mirror for PICKED_UP)

deliveryService.updateDeliveryStatus(1, OUT_FOR_DELIVERY)
  └─ delivery.setStatus(OUT_FOR_DELIVERY)
  └─ OrderService.updateStatus(1, OUT_FOR_DELIVERY)
      └─ notifyObservers(...)  → "[USER #1] Order #1 is now: OUT_FOR_DELIVERY"

deliveryService.updateDeliveryStatus(1, DELIVERED)
  └─ delivery.setStatus(DELIVERED)
  └─ OrderService.updateStatus(1, DELIVERED)
      └─ notifyObservers(...)  → "[USER #1] Order #1 is now: DELIVERED"

FINAL STATE
─────────────────────────────────────────────────────
Order #1:    status = DELIVERED, deliveryId = 1
Delivery #1: status = DELIVERED
Product #2:  stockCount = 49 (was 50)
Cart:        empty (cleared after order placed)
UserHistory: [orderId=1] (most recent first in ArrayDeque)
```

---

## 9. How to Compile and Run

From the `ecommerce/` directory:

```bash
# Compile all Java files
javac -cp . Model/enums/*.java Model/*.java DataSet/*.java strategy/*.java observer/*.java builder/*.java Services/*.java Main.java

# Run
java -cp . Main
```

Expected output: A full step-by-step log showing product listings, cart operations, order creation with observer notifications at every status change, delivery updates, and final order history.

---

## 10. How to Extend This Design

### Add a Payment System

1. Create `Model/enums/PaymentStatus.java` (already exists: PENDING, SUCCESS, FAILED, REFUNDED)
2. Create `Model/Payment.java` — paymentId, orderId, amount, status, method (UPI/Card/COD)
3. Create `Services/PaymentService.java` — `initiatePayment()`, `confirmPayment()`, `refund()`
4. Modify `UserService.placeOrder()` to call `paymentService.initiatePayment()` before creating the order
5. Only create the Order if payment succeeds

### Add Product Search / Filter

Already partially supported via indexes. Add to `UserService`:
```java
// Combine category + price: category index (O(1)) then filter by price
public void searchProducts(String category, double maxPrice) {
    List<Integer> categoryProducts = dataStore.getCategoryIndex().get(category);
    categoryProducts.stream()
        .map(pid -> dataStore.getProducts().get(pid))
        .filter(p -> p.getPrice() <= maxPrice && p.isAvailable())
        .forEach(p -> System.out.println(p.getName() + " | ₹" + p.getPrice()));
}
```

### Add Product Reviews

1. Create `Model/Review.java` — reviewId, productId, userId, rating (int 1-5), comment, createdAt
2. Add `Map<Integer, List<Integer>> productReviews` in DataStore — productId → List of reviewIds
3. Create `Services/ReviewService.java` — only allow review if user has a DELIVERED order for that product

### Add Multiple Dealers Per Order (Cart Split)

Currently, we assume all cart items are from one dealer. To support multi-dealer carts:
```java
// In UserService.placeOrder():
// Group cart items by dealerId → create one Order per dealer
Map<Integer, List<CartItem>> byDealer = cart.getItems().values().stream()
    .collect(Collectors.groupingBy(item -> 
        dataStore.getProducts().get(item.getProductId()).getDealerId()));

byDealer.forEach((dealerId, items) -> {
    orderService.createOrder(userId, dealerId, items);
});
```

### Add Cancel / Return Order

Add to `UserService`:
```java
public void cancelOrder(int userId, int orderId) {
    Order order = dataStore.getOrders().get(orderId);
    // Can only cancel before SHIPPED
    if (order.getStatus().ordinal() >= OrderStatus.SHIPPED.ordinal()) {
        System.out.println("Cannot cancel — order already shipped.");
        return;
    }
    // Restore stock
    order.getCartSnapshot().forEach((pid, item) -> {
        Product p = dataStore.getProducts().get(pid);
        p.setStockCount(p.getStockCount() + item.getQuantity());
    });
    orderService.updateStatus(orderId, OrderStatus.CANCELLED);
}
```

---

## Quick Reference

### Which pattern for what problem?

```
"I have a complex object with many fields"          → Builder
"Multiple classes need to react to one event"       → Observer
"Same operation, different algorithms"              → Strategy
"One entry point should hide complexity"            → Facade
"Only one instance should ever exist"               → Singleton
"Create families of related objects"                → Abstract Factory
"Add behavior to objects without subclassing"       → Decorator
"One object's state determines behavior"            → State Pattern
"Decouple request sender from handler chain"        → Chain of Responsibility
```

### Which data structure for what query?

```
"Find by ID"                        → HashMap         O(1)
"Find by sorted key / range query"  → TreeMap         O(log n)
"Ordered by insertion"              → LinkedHashMap   O(1)
"Process in FIFO order"             → ArrayDeque (queue)  O(1)
"Most recent first"                 → ArrayDeque.offerFirst  O(1)
"Unique elements, fast lookup"      → HashSet         O(1)
"Priority order"                    → PriorityQueue   O(log n)
```

---

*This system was designed to be simple enough to understand completely and extensible enough to grow into a real production architecture.*
