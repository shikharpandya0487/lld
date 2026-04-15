# Problem: Centralized Logging System

## Background

You are building a backend service that needs a centralized logging system. The system must support multiple log destinations (console, file, database), but the component responsible for creating loggers must be a single shared instance across the entire application — you cannot afford to have multiple factories spawning loggers inconsistently.

## Requirements

### 1. LoggerFactory (Singleton)
- There must be **exactly one** `LoggerFactory` instance in the application.
- It must be thread-safe (safe for concurrent access).
- It must **not** allow direct instantiation (no `new LoggerFactory()`).
- Provide a static `getInstance()` method to access it.

### 2. Logger (Factory Pattern)
The `LoggerFactory` must produce loggers based on a `LoggerType` enum:

| Type       | Behavior                                      |
|------------|-----------------------------------------------|
| `CONSOLE`  | Prints: `[CONSOLE] <level>: <message>`        |
| `FILE`     | Prints: `[FILE] Writing to file: <message>`   |
| `DATABASE` | Prints: `[DB] Inserting log to DB: <message>` |

Each logger must implement a common `Logger` interface:
```java
interface Logger {
    void log(String level, String message);
}
```

### 3. LoggerFactory behavior
- `getLogger(LoggerType type)` — returns the appropriate logger.
- If an unknown/unsupported type is passed, throw an `IllegalArgumentException`.

## Constraints
- Do not use any external libraries.
- Use enums for `LoggerType`.
- The singleton must work correctly even if `getInstance()` is called from multiple threads simultaneously.

## Example Usage
```java
LoggerFactory factory = LoggerFactory.getInstance();

Logger console = factory.getLogger(LoggerType.CONSOLE);
console.log("INFO", "Server started on port 8080");
// Output: [CONSOLE] INFO: Server started on port 8080

Logger file = factory.getLogger(LoggerType.FILE);
file.log("WARN", "Disk usage above 80%");
// Output: [FILE] Writing to file: WARN - Disk usage above 80%

Logger db = factory.getLogger(LoggerType.DATABASE);
db.log("ERROR", "Connection pool exhausted");
// Output: [DB] Inserting log to DB: ERROR - Connection pool exhausted
```

## Bonus
- Cache the logger instances inside the factory so the same `LoggerType` always returns the same logger object (makes the factory also act as a registry).
- Add a `logAll(String level, String message)` method on the factory that logs to all registered logger types at once.

---

## Solution

### Design Breakdown

Before writing code, map the requirements to patterns:

| Requirement | Pattern Used |
|---|---|
| Exactly one `LoggerFactory` | **Singleton** |
| Thread-safe singleton | Double-checked locking + `volatile` |
| Produce different loggers by type | **Factory Method** |
| Same type → same logger object | **Registry / Cache** (bonus) |

---

### Step 1 — Logger Interface & Implementations

```java
// Logger.java
interface Logger {
    void log(String level, String message);
}
```

```java
// ConsoleLogger.java
class ConsoleLogger implements Logger {
    @Override
    public void log(String level, String message) {
        System.out.println("[CONSOLE] " + level + ": " + message);
    }
}
```

```java
// FileLogger.java
class FileLogger implements Logger {
    @Override
    public void log(String level, String message) {
        System.out.println("[FILE] Writing to file: " + level + " - " + message);
    }
}
```

```java
// DatabaseLogger.java
class DatabaseLogger implements Logger {
    @Override
    public void log(String level, String message) {
        System.out.println("[DB] Inserting log to DB: " + level + " - " + message);
    }
}
```

---

### Step 2 — LoggerType Enum

```java
// LoggerType.java
enum LoggerType {
    CONSOLE,
    FILE,
    DATABASE
}
```

---

### Step 3 — LoggerFactory (Singleton + Factory + Registry)

```java
// LoggerFactory.java
import java.util.EnumMap;
import java.util.Map;

class LoggerFactory {

    // volatile ensures visibility of the fully constructed instance across threads
    private static volatile LoggerFactory instance;

    // Cache: same type → same logger object (Registry pattern)
    private final Map<LoggerType, Logger> loggerCache = new EnumMap<>(LoggerType.class);

    // Private constructor prevents direct instantiation
    private LoggerFactory() {
        // Pre-populate the registry at construction time
        loggerCache.put(LoggerType.CONSOLE,  new ConsoleLogger());
        loggerCache.put(LoggerType.FILE,     new FileLogger());
        loggerCache.put(LoggerType.DATABASE, new DatabaseLogger());
    }

    // Double-checked locking: safe and efficient under concurrency
    public static LoggerFactory getInstance() {
        if (instance == null) {                          // First check (no lock) — fast path
            synchronized (LoggerFactory.class) {
                if (instance == null) {                  // Second check (with lock) — safe path
                    instance = new LoggerFactory();
                }
            }
        }
        return instance;
    }

    // Factory method: returns the correct logger for the given type
    public Logger getLogger(LoggerType type) {
        Logger logger = loggerCache.get(type);
        if (logger == null) {
            throw new IllegalArgumentException("Unsupported logger type: " + type);
        }
        return logger;
    }

    // Bonus: log to all registered loggers at once
    public void logAll(String level, String message) {
        for (Logger logger : loggerCache.values()) {
            logger.log(level, message);
        }
    }
}
```

---

### Step 4 — Main / Demo

```java
// Main.java
public class Main {
    public static void main(String[] args) {

        LoggerFactory factory = LoggerFactory.getInstance();

        // Basic usage
        Logger console = factory.getLogger(LoggerType.CONSOLE);
        console.log("INFO", "Server started on port 8080");
        // [CONSOLE] INFO: Server started on port 8080

        Logger file = factory.getLogger(LoggerType.FILE);
        file.log("WARN", "Disk usage above 80%");
        // [FILE] Writing to file: WARN - Disk usage above 80%

        Logger db = factory.getLogger(LoggerType.DATABASE);
        db.log("ERROR", "Connection pool exhausted");
        // [DB] Inserting log to DB: ERROR - Connection pool exhausted

        // Verify singleton: both references must be the same object
        LoggerFactory anotherRef = LoggerFactory.getInstance();
        System.out.println(factory == anotherRef);   // true

        // Verify cache: same type → same logger object
        Logger console2 = factory.getLogger(LoggerType.CONSOLE);
        System.out.println(console == console2);     // true

        // Bonus: broadcast to all loggers
        System.out.println("--- logAll ---");
        factory.logAll("DEBUG", "Health check passed");
    }
}
```

**Expected output:**
```
[CONSOLE] INFO: Server started on port 8080
[FILE] Writing to file: WARN - Disk usage above 80%
[DB] Inserting log to DB: ERROR - Connection pool exhausted
true
true
--- logAll ---
[CONSOLE] DEBUG: Health check passed
[FILE] Writing to file: DEBUG - Health check passed
[DB] Inserting log to DB: DEBUG - Health check passed
```

---

### Why Double-Checked Locking?

```
Thread A                         Thread B
─────────────────────────────────────────────────────
getInstance() called
instance == null → true (ok)
                                 getInstance() called
                                 instance == null → true (ok)
enters synchronized block
instance == null → true (ok)
creates new LoggerFactory()
                                 blocks on synchronized
sets instance
exits synchronized
                                 enters synchronized
                                 instance == null → false  ← second check saves us
                                 returns existing instance
```

Without the second `null` check inside the lock, both threads would create a new `LoggerFactory`. Without `volatile`, the JVM could reorder writes and Thread B might see a partially constructed object.

---

### Full Class Diagram

```
         «interface»
           Logger
        ┌──────────┐
        │ +log()   │
        └──────────┘
             △
    ┌────────┼────────┐
    │        │        │
ConsoleLogger FileLogger DatabaseLogger


LoggerType  ←── LoggerFactory (Singleton)
«enum»              │
CONSOLE             │ loggerCache: Map<LoggerType, Logger>
FILE                │ ─────────────────────────────────
DATABASE            │ -LoggerFactory()
                    │ +getInstance(): LoggerFactory
                    │ +getLogger(type): Logger
                    │ +logAll(level, msg): void
```

---

## Practice Questions

### Beginner

**Q1 — Database Connection Pool (Singleton)**
Design a `ConnectionPool` that:
- Is a singleton holding a fixed number of `Connection` objects (e.g., 5).
- Provides `getConnection()` (blocks if none available) and `releaseConnection(conn)`.
- Is thread-safe.

**Q2 — Shape Factory**
Create a `ShapeFactory` with a `createShape(ShapeType type)` method that returns `Circle`, `Rectangle`, or `Triangle`, each implementing a `Shape` interface with `draw()` and `area(double... dims)`. Throw `IllegalArgumentException` for unknown types.

---

### Intermediate

**Q3 — Notification Service (Singleton + Factory)**
Build a `NotificationService` (singleton) with a factory method `getNotifier(Channel channel)` where `Channel` is `EMAIL | SMS | PUSH`. Each notifier implements:
```java
interface Notifier {
    void send(String recipient, String message);
}
```
Add a `broadcast(String message, List<String> recipients)` that sends via all channels simultaneously using threads.

**Q4 — Payment Gateway (Factory + Strategy)**
Design a `PaymentProcessor` with:
- `PaymentStrategy` interface: `pay(double amount)`, `refund(double amount)`.
- Implementations: `CreditCardPayment`, `UPIPayment`, `WalletPayment`.
- A singleton `PaymentGateway` that produces the right strategy via `getProcessor(PaymentMethod method)` and caches instances.

**Q5 — Cache Manager (Singleton + Factory)**
Create a singleton `CacheManager` that supports:
- `getCache(CacheType type)` → returns `InMemoryCache`, `RedisCache` (stub), or `FileCache`.
- Each implements `Cache`: `put(key, value)`, `get(key)`, `evict(key)`.
- `CacheType.IN_MEMORY` uses a `HashMap` internally; `CacheType.FILE` writes to a temp file.

---

### Advanced

**Q6 — Plugin-based Logger (Open/Closed Principle)**
Extend the original problem so that new logger types can be registered at runtime without modifying `LoggerFactory`:
```java
factory.registerLogger(LoggerType.CUSTOM, new SlackLogger());
```
The factory should be open for extension (register new types) but closed for modification (no `switch`/`if-else` chains).

**Q7 — Thread-Safe Configuration Manager**
Design a `ConfigManager` (singleton) that:
- Loads key-value config from a file on first `getInstance()` call.
- Supports `get(key)`, `set(key, value)`, and `reload()`.
- Multiple threads can read concurrently; writes require exclusive access (`ReadWriteLock`).
- Factory method `getConfig(Env env)` returns configs scoped to `DEV | STAGING | PROD`.

**Q8 — Event Bus (Singleton + Observer + Factory)**
Build a singleton `EventBus` where:
- Publishers call `publish(EventType type, Object payload)`.
- Subscribers register with `subscribe(EventType type, EventHandler handler)`.
- `EventHandlerFactory` creates the right handler based on `EventType` (e.g., `ORDER_PLACED → OrderHandler`, `PAYMENT_FAILED → AlertHandler`).
- Delivery is async (use `ExecutorService`).
