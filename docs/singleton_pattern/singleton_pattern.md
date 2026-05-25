# Singleton Design Pattern

## What is Singleton?

The **Singleton Pattern** ensures that a class has **only one instance** throughout the entire application, and provides a **global point of access** to that instance.

Think of it like the President of a country — there can only be one at a time, and everyone refers to the same person.

---

## Why Use Singleton?

| Problem | Singleton Solves It By |
|---|---|
| Multiple DB connections waste resources | Sharing one connection object |
| Inconsistent config across the app | One shared config object |
| Multiple loggers writing to the same file conflict | One logger instance |

---

## Core Structure

```java
public class Singleton {
    // 1. Private static instance (only one lives here)
    private static Singleton instance;

    // 2. Private constructor (nobody can do "new Singleton()")
    private Singleton() {}

    // 3. Public static method to get the single instance
    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
```

**Key rules:**
- Constructor is `private` — prevents external instantiation
- Instance is `static` — belongs to the class, not any object
- `getInstance()` is `static` — accessible without an object

---

## Example 1: Logger

### Real-world analogy
A diary that your whole family shares. Everyone writes in the same diary, not separate ones.

### Problem without Singleton
```java
// Without Singleton - BAD
class Logger {
    public void log(String message) {
        System.out.println("[LOG] " + message);
    }
}

// Different parts of your app create different loggers
Logger logger1 = new Logger(); // in AuthService
Logger logger2 = new Logger(); // in PaymentService
Logger logger3 = new Logger(); // in OrderService

// Each one is separate — no shared history, wastes memory
```

### Solution with Singleton
```java
public class Logger {
    private static Logger instance;
    private int logCount = 0; // shared state across the whole app

    // Private constructor
    private Logger() {
        System.out.println("Logger created!");
    }

    // Global access point
    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void log(String message) {
        logCount++;
        System.out.println("[LOG #" + logCount + "] " + message);
    }

    public int getLogCount() {
        return logCount;
    }
}
```

### Usage
```java
public class Main {
    public static void main(String[] args) {
        Logger.getInstance().log("User logged in");   // LOG #1
        Logger.getInstance().log("Payment processed"); // LOG #2
        Logger.getInstance().log("Order placed");      // LOG #3

        System.out.println("Total logs: " + Logger.getInstance().getLogCount()); // 3

        // Prove it's the same instance
        Logger a = Logger.getInstance();
        Logger b = Logger.getInstance();
        System.out.println(a == b); // true — same object!
    }
}
```

### Output
```
Logger created!
[LOG #1] User logged in
[LOG #2] Payment processed
[LOG #3] Order placed
Total logs: 3
true
```

---

## Example 2: App Configuration

### Real-world analogy
An app's Settings page. No matter which screen you open settings from, you're editing the same settings — not a copy per screen.

### The Singleton
```java
public class AppConfig {
    private static AppConfig instance;

    // Configuration values
    private String appName;
    private String dbUrl;
    private int maxConnections;

    private AppConfig() {
        // Load config once (imagine reading from a file/env)
        this.appName = "MyShoppingApp";
        this.dbUrl = "jdbc:mysql://localhost:3306/shop";
        this.maxConnections = 10;
        System.out.println("Config loaded from file!");
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    public String getAppName()      { return appName; }
    public String getDbUrl()        { return dbUrl; }
    public int getMaxConnections()  { return maxConnections; }
}
```

### Usage
```java
public class UserService {
    public void showInfo() {
        // No need to pass config around — just call getInstance()
        AppConfig config = AppConfig.getInstance();
        System.out.println("App: " + config.getAppName());
        System.out.println("DB: " + config.getDbUrl());
    }
}

public class OrderService {
    public void showInfo() {
        AppConfig config = AppConfig.getInstance(); // Same instance!
        System.out.println("Max connections: " + config.getMaxConnections());
    }
}

public class Main {
    public static void main(String[] args) {
        new UserService().showInfo();
        new OrderService().showInfo();
        // Config file is only read ONCE — efficient!
    }
}
```

### Output
```
Config loaded from file!
App: MyShoppingApp
DB: jdbc:mysql://localhost:3306/shop
Max connections: 10
```

---

## Example 3: Database Connection Pool

### Real-world analogy
A parking lot with limited spots. Everyone shares the same lot — you don't build a new parking lot for each driver.

### The Singleton
```java
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private String connectionUrl;
    private boolean isConnected;

    private DatabaseConnection() {
        this.connectionUrl = "jdbc:mysql://localhost:3306/mydb";
        this.isConnected = false;
        connect();
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection(); // expensive operation — only once!
        }
        return instance;
    }

    private void connect() {
        // Simulate expensive connection setup
        System.out.println("Connecting to database... (expensive operation)");
        this.isConnected = true;
    }

    public void query(String sql) {
        if (isConnected) {
            System.out.println("Executing: " + sql);
        }
    }

    public boolean isConnected() {
        return isConnected;
    }
}
```

### Usage
```java
public class Main {
    public static void main(String[] args) {
        // All services share one connection — no repeated expensive setup
        DatabaseConnection db1 = DatabaseConnection.getInstance();
        db1.query("SELECT * FROM users");

        DatabaseConnection db2 = DatabaseConnection.getInstance();
        db2.query("SELECT * FROM orders");

        System.out.println("Same connection? " + (db1 == db2)); // true
    }
}
```

### Output
```
Connecting to database... (expensive operation)
Executing: SELECT * FROM users
Executing: SELECT * FROM orders
Same connection? true
```

**Without Singleton**, each service would open its own DB connection — extremely wasteful and can exhaust DB connection limits.

---

## Thread Safety in Singleton

This is one of the most critical and commonly misunderstood aspects of the Singleton pattern. In single-threaded apps the basic implementation works fine, but in multi-threaded environments (web servers, background workers, parallel tasks) it can silently break — creating multiple instances.

---

### The Race Condition Problem

The naive `getInstance()` has a **check-then-act** vulnerability:

```java
// BROKEN in multi-threaded environments
public static Singleton getInstance() {
    if (instance == null) {        // ← Both threads pass this check
        instance = new Singleton(); // ← Both threads create an instance
    }
    return instance;
}
```

Here is exactly what goes wrong on a two-core machine:

```
Timeline:
─────────────────────────────────────────────────────────────
Thread A (Core 1)              Thread B (Core 2)
─────────────────────────────────────────────────────────────
reads instance → null
                               reads instance → null
creates instance A
                               creates instance B  ← second instance!
returns instance A
                               returns instance B
─────────────────────────────────────────────────────────────
Result: two different Singleton objects exist in memory
```

**Why this is dangerous:**
- Each thread holds a *different* object — shared state is no longer shared
- A logger built this way loses log entries; a config object may have different values per thread
- The bug is **non-deterministic** — it only appears under timing conditions that are hard to reproduce in tests

---

### Approach 1: Eager Initialization

Create the instance when the class is loaded, before any thread can call `getInstance()`.

```java
public class EagerSingleton {
    // Created immediately at class load time — JVM guarantees this is thread-safe
    private static final EagerSingleton INSTANCE = new EagerSingleton();

    private EagerSingleton() {}

    public static EagerSingleton getInstance() {
        return INSTANCE;   // no null check needed — always initialized
    }
}
```

**How it works:** The JVM executes static field initializers exactly once during class loading, and class loading itself is thread-safe. No explicit synchronization needed.

| | |
|---|---|
| **Thread safe?** | Yes — guaranteed by JVM class loading |
| **Lazy?** | No — created even if never used |
| **Performance** | Best (no locking ever) |
| **Use when** | Instance is always needed and cheap to create |

**Drawback:** If the constructor is expensive (opens a DB connection, reads a large file) and the Singleton might not be needed in every run, you're paying that cost upfront for nothing.

---

### Approach 2: Synchronized Method

Add `synchronized` to `getInstance()` so only one thread can enter it at a time.

```java
public class SynchronizedSingleton {
    private static SynchronizedSingleton instance;

    private SynchronizedSingleton() {}

    // synchronized = only one thread can execute this at a time
    public static synchronized SynchronizedSingleton getInstance() {
        if (instance == null) {
            instance = new SynchronizedSingleton();
        }
        return instance;
    }
}
```

**How it works:** `synchronized` acquires the class-level monitor lock before entering the method. Other threads block at the door until the lock is released.

```
Thread A calls getInstance() → acquires lock → creates instance → releases lock
Thread B calls getInstance() → waits for lock → acquires lock → instance != null → returns it
```

| | |
|---|---|
| **Thread safe?** | Yes |
| **Lazy?** | Yes — created on first call |
| **Performance** | Poor at scale — every `getInstance()` call serializes through the lock |
| **Use when** | getInstance() is called rarely, or simplicity > performance |

**Drawback:** After the instance is created, the lock is still acquired on every call — unnecessary overhead in hot code paths. Under heavy load (e.g., 10,000 req/sec on a web server), this becomes a bottleneck.

---

### Approach 3: Double-Checked Locking (DCL)

Only synchronize during the creation phase. Once created, reads skip the lock entirely.

```java
public class DCLSingleton {
    // volatile is MANDATORY here — explains why below
    private static volatile DCLSingleton instance;

    private DCLSingleton() {}

    public static DCLSingleton getInstance() {
        if (instance == null) {                  // Check 1: no lock (fast path for 99.99% of calls)
            synchronized (DCLSingleton.class) {  // Lock: only when instance might not exist
                if (instance == null) {          // Check 2: re-verify after acquiring lock
                    instance = new DCLSingleton();
                }
            }
        }
        return instance;
    }
}
```

**Why two null checks?**

```
Without the second check:
─────────────────────────────────────────────────────────────
Thread A                         Thread B
─────────────────────────────────────────────────────────────
passes Check 1 (null)
                                 passes Check 1 (null)
acquires lock
creates instance
releases lock
                                 acquires lock (Thread A is done)
                                 (no second check) → creates ANOTHER instance ← Bug!
─────────────────────────────────────────────────────────────
```

The second check inside the lock prevents this.

**Why `volatile`?**

Without `volatile`, the JVM or CPU can **reorder instructions** for performance. Object creation (`new DCLSingleton()`) is actually three steps:
1. Allocate memory
2. Initialize the object (run constructor)
3. Assign the reference to `instance`

A JVM can legally reorder this to: allocate → assign reference → initialize. If Thread B reads `instance` after step 2 but before step 3, it sees a non-null but **partially constructed object** — and proceeds to use a broken instance.

`volatile` prevents this reordering by establishing a **happens-before relationship**: any write to `instance` is fully visible to any subsequent read.

```
Without volatile (dangerous):
Thread A: allocate → assign reference to instance → [Thread B reads here!] → initialize
Thread B: sees instance != null → uses uninitialized object → crashes or corrupts data
```

| | |
|---|---|
| **Thread safe?** | Yes — when `volatile` is present |
| **Lazy?** | Yes |
| **Performance** | Excellent — lock only on first call |
| **Use when** | Lazy init + high-concurrency environment |

---

### Approach 4: Initialization-on-Demand Holder (Recommended)

Exploits the JVM's own class loading guarantee — no `synchronized`, no `volatile` needed.

```java
public class HolderSingleton {
    private HolderSingleton() {}

    // Inner class is not loaded until getInstance() is first called
    private static class Holder {
        static final HolderSingleton INSTANCE = new HolderSingleton();
        //           ↑ static initializer runs once, thread-safely, when Holder is loaded
    }

    public static HolderSingleton getInstance() {
        return Holder.INSTANCE;   // triggers Holder class loading on first call only
    }
}
```

**How it works — step by step:**

```
First call to getInstance():
  1. JVM sees Holder class is not yet loaded
  2. JVM loads Holder class — acquires class initialization lock internally
  3. Static field INSTANCE = new HolderSingleton() runs exactly once
  4. JVM releases class initialization lock
  5. INSTANCE is returned

All subsequent calls to getInstance():
  1. Holder is already loaded
  2. INSTANCE is returned directly — zero locking, zero null checks
```

The JVM specification (JSL §12.4) **guarantees** class initialization is executed exactly once and is visible to all threads. This is not a trick — it is a deliberate language guarantee.

| | |
|---|---|
| **Thread safe?** | Yes — guaranteed by JVM spec |
| **Lazy?** | Yes — Holder only loads on first call |
| **Performance** | Best possible for lazy init (no locking after init) |
| **Use when** | Default choice for Java Singletons |

---

### Approach 5: Enum Singleton (Serialization-Safe)

Joshua Bloch's recommendation from *Effective Java*. Handles thread safety, serialization, and reflection attacks automatically.

```java
public enum EnumSingleton {
    INSTANCE;

    // Add your fields and methods here
    private int callCount = 0;

    public void doWork() {
        callCount++;
        System.out.println("Working... call #" + callCount);
    }

    public int getCallCount() {
        return callCount;
    }
}
```

**Usage:**
```java
EnumSingleton.INSTANCE.doWork();   // Working... call #1
EnumSingleton.INSTANCE.doWork();   // Working... call #2

// Prove it's the same instance
System.out.println(EnumSingleton.INSTANCE == EnumSingleton.INSTANCE); // true
```

**Why Enum is uniquely powerful:**

| Threat | Other Approaches | Enum |
|--------|-----------------|------|
| Multi-threading | Needs explicit handling | Safe by JVM |
| Serialization | Can create new instances on deserialization | Guaranteed single instance |
| Reflection attack | `setAccessible(true)` can bypass private constructor | JVM blocks this for enums |
| Clone attack | Need to override `clone()` | Enums cannot be cloned |

**Serialization problem with non-Enum Singletons:**
```java
// This creates a SECOND instance — Singleton contract broken!
ObjectOutputStream out = new ObjectOutputStream(...);
out.writeObject(MySingleton.getInstance());
out.close();

ObjectInputStream in = new ObjectInputStream(...);
MySingleton second = (MySingleton) in.readObject(); // new instance!
```
Enum is immune to this because the JVM handles enum deserialization specially.

| | |
|---|---|
| **Thread safe?** | Yes |
| **Lazy?** | No (loaded with class) |
| **Performance** | Best |
| **Use when** | Need serialization safety or want the most robust solution |

---

### Side-by-Side Comparison

```
Approach             | Thread Safe | Lazy | Lock on get | Serialization Safe
─────────────────────────────────────────────────────────────────────────────
Eager Initialization |     Yes     |  No  |     No      |        No
Synchronized Method  |     Yes     | Yes  |   Always    |        No
Double-Checked Lock  |     Yes     | Yes  |  Only once  |        No
Holder Pattern       |     Yes     | Yes  |     No      |        No
Enum                 |     Yes     |  No  |     No      |       Yes
```

---

### Which Should You Use?

```
Need lazy initialization?
├── No  → Eager Initialization or Enum
└── Yes → Need serialization safety?
          ├── Yes → Enum (add readResolve to non-enum, or just use Enum)
          └── No  → Holder Pattern  ← default Java choice
                    (or DCL if you're in an environment without inner classes)
```

**In practice:**
- **Java applications** → Holder Pattern or Enum
- **Spring / DI frameworks** → Let the framework manage it (it handles thread safety for you)
- **Android** → Holder Pattern (avoid Enum due to memory overhead on older Android)

---

## Common Mistakes

### Mistake 1: Forgetting `private` on constructor
```java
// WRONG — anyone can call new Singleton()
public Singleton() {}

// CORRECT
private Singleton() {}
```

### Mistake 2: Not using `static` on instance
```java
// WRONG — each object has its own "instance"
private Singleton instance;

// CORRECT
private static Singleton instance;
```

### Mistake 3: Cloning the Singleton
```java
// To prevent cloning from creating a second instance:
@Override
protected Object clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException("Use getInstance()");
}
```

---

## When TO Use Singleton

- Logging systems
- Configuration/Settings managers
- Database connection pools
- Cache managers
- Thread pools

## When NOT to Use Singleton

- When you need different configurations in different contexts (use Dependency Injection instead)
- When writing unit tests (Singletons make mocking hard)
- When the "single" constraint doesn't actually apply to your problem

---

## Quick Summary

```
Singleton = One instance + Private constructor + Static getInstance()

new MyClass()        ← BLOCKED (private constructor)
MyClass.getInstance() ← ALLOWED (returns the one instance)
```

| Feature | Detail |
|---|---|
| Pattern type | Creational |
| Instances allowed | Exactly 1 |
| Access | Via static `getInstance()` method |
| Constructor | `private` |
| Thread safety | Eager / Enum (no lock), Holder (lazy, no lock), DCL (lazy, lock once), synchronized (lazy, always locks) |
```
