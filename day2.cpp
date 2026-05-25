// 🍔 Zomato / Swiggy Food Delivery

// Vector → restaurant menu items
// Queue → orders waiting to be picked up
// Priority Queue → premium/pro members' orders jump ahead
// Deque → delivery agent picks nearest order from either side
// Stack → order edit history (undo item removal)
// Map → orderId → order status


// OOLD round + system design questions: write code and explain your design choices. Focus on data structures, algorithms, and scalability. Consider edge cases and performance implications.  


// 🍔 Swiggy Food Delivery System
// Entities to model
// MenuItem  { itemId, name, price, category }
// Order     { orderId, userId, items[], status, isPremium }
// Driver    { driverId, name, location, isAvailable }

// Core Operations
// Vector<MenuItem> — Restaurant Menu

// addItem(name, price, category) — add item to menu
// getMenu() — return full menu
// getByCategory(category) — filter items by category (e.g. "Drinks", "Main")
// removeItem(itemId) — remove item from menu


// Queue — Order Queue (FIFO)

// placeOrder(orderId, userId, items[], isPremium=false) — customer places order
// nextOrder() — kitchen picks next order to prepare
// pendingCount() — how many orders waiting


// Priority Queue — Premium Order Lane

// placePremiumOrder(orderId, userId, items[]) — Swiggy One members jump ahead
// processNext() — always process highest priority order first
// Priority = isPremium first, then order timestamp


// Deque — Driver Delivery Queue

// assignToDriver(orderId) — push to back normally
// urgentReassign(orderId) — push to front (driver cancelled, reassign fast)
// driverPicksUp() — driver pops from front
// returnUndelivered(orderId) — push back to front if delivery failed


// Stack — Order Edit History

// editOrder(orderId, change) — customer modifies order, push change
// undoLastEdit(orderId) — customer changes mind, revert last edit
// getEditHistory(orderId) — full change log


// Map<orderId, Order> — Order Tracker

// trackOrder(orderId) — get current status of order
// updateStatus(orderId, status) — update to "Preparing", "Out for delivery", "Delivered"
// getActiveOrders() — all orders not yet delivered
// getUserOrders(userId) — all orders by a user


// Example Flow
// addItem("Burger", 120, "Main")         → itemId: I001
// addItem("Coke", 40, "Drinks")          → itemId: I002

// placeOrder("O001", "U01", [I001, I002], isPremium=false)
// placePremiumOrder("O002", "U02", [I001])   // jumps ahead

// processNext()          → O002 (premium first)
// nextOrder()            → O001

// assignToDriver("O001")
// urgentReassign("O003") // driver cancelled, push to front
// driverPicksUp()        → O003

// editOrder("O001", "Add extra cheese")
// undoLastEdit("O001")   // never mind

// trackOrder("O001")     → "Preparing"
// updateStatus("O001", "Out for delivery")

// What to implement

// All 6 modules with listed methods
// One SwiggySystem class that ties everything together
// Edge cases — empty queue, undo on no edits, track unknown orderId, getByCategory with no matches


// Go ahead and write your solution, I'll review design + correctness + complexity!

// 🍔 Swiggy Food Delivery System (C++ translation of day2.java)

#include <iostream>
#include <vector>
#include <string>
#include <queue>
#include <deque>
#include <unordered_map>
#include <map>
#include <algorithm>
#include <cstdio>
#include <strings.h>
#include <chrono>
#include <thread>

using namespace std;

class MenuItem {
    string itemId;
    string name;
    double price;
    string category;
public:
    MenuItem() = default;
    MenuItem(const string &id, const string &n, double p, const string &c) {
        itemId = id;
        name = n;
        price = p;
        category = c;
    }

    const string& id() const { return itemId; }
    const string& getName() const { return name; }
    double getPrice() const { return price; }
    const string& getCategory() const { return category; }

    void print() const {
        cout << "[" << itemId << "] " << name << " \u20B9" << (long)price << " (" << category << ")";
    }
};

class Order {
    string orderId;
    string userId;
    string status;
    vector<string> items;
    bool premiumFlag;
    unsigned long long timestamp; // for ordering in PQ
public:
    Order() = default;
    Order(const string &oid, const string &uid, const vector<string> &it, bool premium) {
        orderId = oid;
        userId = uid;
        status = "Placed";
        items = it;
        premiumFlag = premium;
        timestamp = chrono::steady_clock::now().time_since_epoch().count();
    }

    const string& id() const { return orderId; }
    const string& getUserId() const { return userId; }
    const string& getStatus() const { return status; }
    void setStatus(const string &s) { status = s; }
    const vector<string>& getItems() const { return items; }
    bool isPremium() const { return premiumFlag; }
    unsigned long long getTimestamp() const { return timestamp; }

    void print() const {
        cout << "Order[" << orderId << "] user=" << userId << " status=" << status
             << " premium=" << (premiumFlag ? "true" : "false") << " items=[";
        for (size_t i = 0; i < items.size(); ++i) {
            if (i) cout << ", ";
            cout << items[i];
        }
        cout << "]";
    }
};

// Module 1: RestaurantMenu
class RestaurantMenu {
    vector<MenuItem> menu;
    int counter = 1;
public:
    string addItem(const string &name, double price, const string &category) {
        char buf[16];
        snprintf(buf, sizeof(buf), "I%03d", counter++);
        string id(buf);
        menu.emplace_back(id, name, price, category);
        return id;
    }

    const vector<MenuItem>& getMenu() const { return menu; }

    vector<MenuItem> getByCategory(const string &category) const {
        vector<MenuItem> res;
        for (const auto &m : menu)
            if (strcasecmp(m.getCategory().c_str(), category.c_str()) == 0)
                res.push_back(m);
        return res;
    }

    bool removeItem(const string &itemId) {
        auto it = remove_if(menu.begin(), menu.end(), [&](const MenuItem &m){ return m.id() == itemId; });
        if (it == menu.end()) return false;
        menu.erase(it, menu.end());
        return true;
    }
};

// Module 2: OrderQueue (FIFO)
class OrderQueue {
    queue<Order> q;
public:
    void placeOrder(const Order &o) { q.push(o); }
    Order nextOrder() {
        if (q.empty()) { cout << "  [OrderQueue] Empty." << endl; return Order(); }
        Order res = q.front();
        q.pop();
        return res;
    }
    int pendingCount() const { return (int)q.size(); }
};

// Module 3: PremiumOrderLane (priority)
struct OrderCompare {
    bool operator()(const Order &a, const Order &b) const {
        if (a.isPremium() != b.isPremium()) return (!a.isPremium() && b.isPremium());
        return a.getTimestamp() > b.getTimestamp(); // earlier timestamp => higher priority
    }
};

class PremiumOrderLane {
    priority_queue<Order, vector<Order>, OrderCompare> pq;
public:
    void placePremiumOrder(const Order &o) { pq.push(o); }
    Order processNext() {
        if (pq.empty()) { cout << "  [PremiumLane] Empty." << endl; return Order(); }
        Order res = pq.top();
        pq.pop();
        return res;
    }
    int pendingCount() const { return (int)pq.size(); }
};

// Module 4: DriverDeliveryQueue (deque)
class DriverDeliveryQueue {
    deque<string> dq;
public:
    void assignToDriver(const string &orderId) { dq.push_back(orderId); }
    void urgentReassign(const string &orderId) { dq.push_front(orderId); }
    void returnUndelivered(const string &orderId) { dq.push_front(orderId); }
    string driverPicksUp() {
        if (dq.empty()) { cout << "  [DriverQueue] Empty." << endl; return string(); }
        string id = dq.front(); dq.pop_front(); return id;
    }
    int size() const { return (int)dq.size(); }
};

// Module 5: OrderEditHistory (stack per order)
class OrderEditHistory {
    unordered_map<string, vector<string>> stacks;
public:
    void editOrder(const string &orderId, const string &change) {
        stacks[orderId].push_back(change);
    }
    string undoLastEdit(const string &orderId) {
        auto it = stacks.find(orderId);
        if (it == stacks.end() || it->second.empty()) {
            cout << "  [EditHistory] No edits to undo for " << orderId << endl;
            return string();
        }
        string last = it->second.back();
        it->second.pop_back();
        return last;
    }
    vector<string> getEditHistory(const string &orderId) const {
        auto it = stacks.find(orderId);
        if (it == stacks.end() || it->second.empty()) return {};
        // return most-recent-first to match stack order
        vector<string> rev = it->second;
        reverse(rev.begin(), rev.end());
        return rev;
    }
};

// Module 6: OrderTracker
class OrderTracker {
    unordered_map<string, Order> orders;
public:
    void registerOrder(const Order &o) { orders[o.id()] = o; }
    Order* trackOrder(const string &orderId) {
        auto it = orders.find(orderId);
        if (it == orders.end()) { cout << "  [Tracker] Unknown orderId: " << orderId << endl; return nullptr; }
        return &it->second;
    }
    bool updateStatus(const string &orderId, const string &status) {
        auto it = orders.find(orderId);
        if (it == orders.end()) { cout << "  [Tracker] Cannot update — unknown orderId: " << orderId << endl; return false; }
        it->second.setStatus(status);
        return true;
    }
    vector<Order> getActiveOrders() const {
        vector<Order> res;
        for (const auto &p : orders) if (p.second.getStatus() != "Delivered") res.push_back(p.second);
        return res;
    }
    vector<Order> getUserOrders(const string &userId) const {
        vector<Order> res;
        for (const auto &p : orders) if (p.second.getUserId() == userId) res.push_back(p.second);
        return res;
    }
};

// SwiggySystem facade
class SwiggySystem {
    RestaurantMenu menu;
    OrderQueue orderQueue;
    PremiumOrderLane premiumLane;
    DriverDeliveryQueue driverQ;
    OrderEditHistory editHistory;
    OrderTracker tracker;
public:
    // Menu
    string addItem(const string &name, double price, const string &category) {
        string id = menu.addItem(name, price, category);
        cout << "  Added: " << id << " → " << name << endl;
        return id;
    }
    const vector<MenuItem>& getMenu() const { return menu.getMenu(); }
    vector<MenuItem> getByCategory(const string &cat) const { return menu.getByCategory(cat); }
    bool removeItem(const string &itemId) { return menu.removeItem(itemId); }

    // Regular order
    void placeOrder(const string &orderId, const string &userId, const vector<string> &items) {
        Order o(orderId, userId, items, false);
        orderQueue.placeOrder(o);
        tracker.registerOrder(o);
        cout << "  Placed regular: " << orderId << endl;
    }
    Order nextOrder() { return orderQueue.nextOrder(); }
    int pendingCount() const { return orderQueue.pendingCount(); }

    // Premium
    void placePremiumOrder(const string &orderId, const string &userId, const vector<string> &items) {
        Order o(orderId, userId, items, true);
        premiumLane.placePremiumOrder(o);
        tracker.registerOrder(o);
        cout << "  Placed premium: " << orderId << " (jumps ahead)" << endl;
    }
    Order processNext() { return premiumLane.processNext(); }

    // Driver queue
    void assignToDriver(const string &orderId) { driverQ.assignToDriver(orderId); }
    void urgentReassign(const string &orderId) { driverQ.urgentReassign(orderId); }
    string driverPicksUp() { return driverQ.driverPicksUp(); }
    void returnUndelivered(const string &orderId) { driverQ.returnUndelivered(orderId); }

    // Edit history
    void editOrder(const string &orderId, const string &change) { editHistory.editOrder(orderId, change); }
    string undoLastEdit(const string &orderId) { return editHistory.undoLastEdit(orderId); }
    vector<string> getEditHistory(const string &orderId) const { return editHistory.getEditHistory(orderId); }

    // Tracker
    Order* trackOrder(const string &orderId) { return tracker.trackOrder(orderId); }
    bool updateStatus(const string &orderId, const string &s) { return tracker.updateStatus(orderId, s); }
    vector<Order> getActiveOrders() const { return tracker.getActiveOrders(); }
    vector<Order> getUserOrders(const string &userId) const { return tracker.getUserOrders(userId); }
};

int main() {
    SwiggySystem swiggy;

    cout << "=== MENU SETUP ===" << endl;
    string i1 = swiggy.addItem("Burger", 120, "Main");
    string i2 = swiggy.addItem("Coke", 40, "Drinks");
    string i3 = swiggy.addItem("Pizza", 250, "Main");
    string i4 = swiggy.addItem("Fries", 80, "Snacks");

    cout << "\nFull menu:" << endl;
    for (const auto &m : swiggy.getMenu()) { cout << "  "; m.print(); cout << endl; }

    cout << "\nDrinks only:" << endl;
    for (const auto &m : swiggy.getByCategory("Drinks")) { cout << "  "; m.print(); cout << endl; }

    cout << "\nEdge — getByCategory('Desserts'):" << endl;
    cout << "  matches: " << swiggy.getByCategory("Desserts").size() << endl;

    cout << "\n=== ORDER PLACEMENT ===" << endl;
    swiggy.placeOrder("O001", "U01", {i1, i2});
    this_thread::sleep_for(chrono::microseconds(1));
    swiggy.placePremiumOrder("O002", "U02", {i3});
    swiggy.placeOrder("O003", "U01", {i4});

    cout << "\n=== PROCESSING ===" << endl;
    {
        Order p = swiggy.processNext();
        if (!p.id().empty()) cout << "  PremiumLane → " << p.id() << endl;
        else cout << "  PremiumLane → null" << endl;
    }

    {
        Order r = swiggy.nextOrder();
        if (!r.id().empty()) cout << "  OrderQueue  → " << r.id() << endl;
        else cout << "  OrderQueue  → null" << endl;
    }
    cout << "  Pending in queue: " << swiggy.pendingCount() << endl;

    cout << "\nEdge — processNext on empty lane:" << endl;
    swiggy.processNext();

    cout << "\n=== DRIVER QUEUE ===" << endl;
    swiggy.assignToDriver("O001");
    swiggy.assignToDriver("O002");
    swiggy.urgentReassign("O003");
    cout << "  picks up → " << swiggy.driverPicksUp() << endl;
    cout << "  picks up → " << swiggy.driverPicksUp() << endl;
    swiggy.returnUndelivered("O001");
    cout << "  picks up → " << swiggy.driverPicksUp() << endl;

    cout << "\nEdge — driverPicksUp on empty deque:" << endl;
    swiggy.driverPicksUp();
    swiggy.driverPicksUp();

    cout << "\n=== EDIT HISTORY ===" << endl;
    swiggy.editOrder("O001", "Add extra cheese");
    swiggy.editOrder("O001", "Remove onions");
    swiggy.editOrder("O001", "Add jalapeños");
    auto hist = swiggy.getEditHistory("O001");
    cout << "  history: [";
    for (size_t i=0;i<hist.size();++i){ if(i) cout<<", "; cout<<hist[i]; }
    cout << "]" << endl;

    string undone = swiggy.undoLastEdit("O001");
    cout << "  undid:   " << undone << endl;
    hist = swiggy.getEditHistory("O001");
    cout << "  history: [";
    for (size_t i=0;i<hist.size();++i){ if(i) cout<<", "; cout<<hist[i]; }
    cout << "]" << endl;

    cout << "\nEdge — undo on order with no edits:" << endl;
    swiggy.undoLastEdit("O002");

    cout << "\n=== ORDER TRACKER ===" << endl;
    swiggy.updateStatus("O001", "Preparing");
    swiggy.updateStatus("O002", "Out for delivery");
    swiggy.updateStatus("O003", "Delivered");

    if (auto tracked = swiggy.trackOrder("O001"))
        cout << "  O001 status: " << tracked->getStatus() << endl;

    cout << "\n  Active orders (not delivered):" << endl;
    for (const auto &o : swiggy.getActiveOrders()) { cout << "    "; o.print(); cout << endl; }

    cout << "\n  Orders by U01:" << endl;
    for (const auto &o : swiggy.getUserOrders("U01")) { cout << "    "; o.print(); cout << endl; }

    cout << "\nEdge — trackOrder / updateStatus with unknown id:" << endl;
    swiggy.trackOrder("O999");
    swiggy.updateStatus("O999", "Delivered");

    return 0;
}

