# Strategy Pattern — Swap Algorithms Without Touching the Code That Uses Them

## The Analogy

Think of a GPS navigation app. You type in a destination and it offers you three routes: fastest, shortest, or avoid tolls. You pick one and the app calculates accordingly. The app itself does not change — only the **routing algorithm** you chose changes. In code terms: the GPS app is the **Context** (the class that needs a behavior), and each routing algorithm is a **Strategy** (a swappable implementation of that behavior).

---

## What Problem Does It Solve?

Imagine you are building a payment system. At first you only support credit cards. So you write an `if` block inside your `checkout()` method. Then your boss asks you to add PayPal. You add another `if`. Then comes UPI. Then crypto. Now your `checkout()` method has six nested `if-else` branches, each containing payment logic.

Every time a new payment method is added, you crack open the same class, risk breaking existing logic, and re-test everything. Your class has become a dumping ground. It knows too much. It is fragile. And it is impossible to test each payment method in isolation. The Strategy pattern solves this by pulling each algorithm (payment method) out into its own separate class and letting the calling code swap between them without any `if-else` at all.

---

## The Formal Definition

> "Define a family of algorithms, encapsulate each one, and make them interchangeable. Strategy lets the algorithm vary independently from the clients that use it." — *Design Patterns: Elements of Reusable Object-Oriented Software* (Gang of Four)

In plain English: instead of writing a big `if-else` to pick a behavior, you put each behavior in its own class. Then you pass the class you want into the code that needs it.

---

## The Core Structure (Java)

```java
// STEP 1 — Define the Strategy interface (the "contract" all algorithms must follow)
interface Strategy {
    void execute();
}

// STEP 2 — Create concrete strategies (the actual algorithm classes)
class ConcreteStrategyA implements Strategy {
    public void execute() { System.out.println("Running Strategy A"); }
}

class ConcreteStrategyB implements Strategy {
    public void execute() { System.out.println("Running Strategy B"); }
}

// STEP 3 — The Context holds a Strategy and delegates work to it
class Context {
    private Strategy strategy; // <-- holds whichever strategy is active

    // Constructor injection: the strategy is given from outside, not hardcoded
    public Context(Strategy strategy) {
        this.strategy = strategy;
    }

    // Allow swapping at runtime
    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public void performAction() {
        strategy.execute(); // delegates to whichever strategy is currently set
    }
}

// USAGE
class Main {
    public static void main(String[] args) {
        Context ctx = new Context(new ConcreteStrategyA());
        ctx.performAction(); // Output: Running Strategy A

        ctx.setStrategy(new ConcreteStrategyB());
        ctx.performAction(); // Output: Running Strategy B
    }
}
```

**Key roles:**

| Role | What It Is | Who Fills It |
|---|---|---|
| Strategy | The interface/contract | `Strategy` interface |
| Concrete Strategy | The actual algorithm | `ConcreteStrategyA`, `ConcreteStrategyB` |
| Context | The class that uses the strategy | `Context` class |
| Client | The code that picks a strategy | `main()` method |

---

## Example 1 — BAD: Without the Pattern

A sorting dashboard that uses `if-else` to pick a sort algorithm. Every new algorithm means editing the same fragile method.

```java
// BAD — one giant method knows about every sorting algorithm
// Problem: adding "radix sort" means editing THIS class, risking breakage of bubble/merge
class Sorter {

    // This method does too many things.
    // Every new algorithm = new branch = more risk.
    public void sort(int[] data, String algorithmType) {
        if (algorithmType.equals("bubble")) {
            // bubble sort logic
            System.out.println("Sorting with Bubble Sort");
            // ... 20 lines of code ...
        } else if (algorithmType.equals("merge")) {
            // merge sort logic
            System.out.println("Sorting with Merge Sort");
            // ... 30 lines of code ...
        } else if (algorithmType.equals("quick")) {
            // quick sort logic
            System.out.println("Sorting with Quick Sort");
            // ... 25 lines of code ...
        } else {
            throw new IllegalArgumentException("Unknown algorithm: " + algorithmType);
        }
        // What happens when you add "radix"? You edit this class again.
        // What if a new developer misses the else-if chain? Silent bug.
    }
}

class BadMain {
    public static void main(String[] args) {
        Sorter sorter = new Sorter();
        sorter.sort(new int[]{5, 2, 8, 1}, "bubble");
        sorter.sort(new int[]{5, 2, 8, 1}, "merge");
        // If you pass "radix" — it throws at RUNTIME. The compiler won't catch it.
    }
}
```

**What breaks:**
- The `Sorter` class must change every time a new algorithm is added — this violates the **Open/Closed Principle** (open for extension, closed for modification).
- You cannot test Bubble Sort without the class knowing about Merge Sort too.
- A typo like `"bubbel"` causes a runtime crash that could have been a compile-time error.

---

## Example 2 — GOOD: With the Strategy Pattern Applied

```java
// STEP 1 — One interface that every sorting algorithm must implement
interface SortStrategy {
    void sort(int[] data);
}

// STEP 2 — Each algorithm lives in its own class. Clean. Testable. Isolated.
class BubbleSort implements SortStrategy {
    public void sort(int[] data) {
        // real bubble sort would go here
        System.out.println("Sorting with Bubble Sort");
    }
}

class MergeSort implements SortStrategy {
    public void sort(int[] data) {
        System.out.println("Sorting with Merge Sort");
    }
}

class QuickSort implements SortStrategy {
    public void sort(int[] data) {
        System.out.println("Sorting with Quick Sort");
    }
}

// To add RadixSort later: create a new class. Touch NOTHING else.
class RadixSort implements SortStrategy {
    public void sort(int[] data) {
        System.out.println("Sorting with Radix Sort");
    }
}

// STEP 3 — Sorter (the Context) just holds a strategy and calls it
class Sorter {
    private SortStrategy strategy; // the active algorithm

    public Sorter(SortStrategy strategy) {
        this.strategy = strategy; // injected from outside — no hardcoding
    }

    public void setStrategy(SortStrategy strategy) {
        this.strategy = strategy; // swap at runtime if needed
    }

    public void sort(int[] data) {
        strategy.sort(data); // delegate to whichever strategy is active
    }
}

class GoodMain {
    public static void main(String[] args) {
        int[] data = {5, 2, 8, 1, 9};

        Sorter sorter = new Sorter(new BubbleSort());
        sorter.sort(data); // Output: Sorting with Bubble Sort

        sorter.setStrategy(new QuickSort());
        sorter.sort(data); // Output: Sorting with Quick Sort

        // Later, someone adds RadixSort. They just create a new class.
        // Nobody touches Sorter. Nobody touches BubbleSort. No regression risk.
        sorter.setStrategy(new RadixSort());
        sorter.sort(data); // Output: Sorting with Radix Sort
    }
}
```

---

## Example 3 — E-Commerce: Payment Methods

Your online store starts with credit card payments only. Then PayPal, UPI, and crypto are requested. Without Strategy, your checkout method becomes a wall of `if-else`.

```java
// Each payment method is its own strategy
interface PaymentStrategy {
    void pay(double amount);
}

class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;
    public CreditCardPayment(String cardNumber) { this.cardNumber = cardNumber; }

    public void pay(double amount) {
        System.out.println("Paid ₹" + amount + " via Credit Card ending in " + cardNumber.substring(cardNumber.length() - 4));
    }
}

class PayPalPayment implements PaymentStrategy {
    private String email;
    public PayPalPayment(String email) { this.email = email; }

    public void pay(double amount) {
        System.out.println("Paid ₹" + amount + " via PayPal account: " + email);
    }
}

class UpiPayment implements PaymentStrategy {
    private String upiId;
    public UpiPayment(String upiId) { this.upiId = upiId; }

    public void pay(double amount) {
        System.out.println("Paid ₹" + amount + " via UPI ID: " + upiId);
    }
}

// The shopping cart is the Context — it delegates payment to the strategy
class ShoppingCart {
    private PaymentStrategy paymentStrategy;

    public ShoppingCart(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public void checkout(double totalAmount) {
        System.out.println("Processing checkout...");
        paymentStrategy.pay(totalAmount); // no if-else anywhere
        System.out.println("Order confirmed!");
    }
}

class PaymentMain {
    public static void main(String[] args) {
        // User chooses UPI at checkout
        ShoppingCart cart = new ShoppingCart(new UpiPayment("shikhar@upi"));
        cart.checkout(1499.00);
        // Output:
        // Processing checkout...
        // Paid ₹1499.0 via UPI ID: shikhar@upi
        // Order confirmed!

        // User switches to credit card for the next order
        ShoppingCart cart2 = new ShoppingCart(new CreditCardPayment("4111111111111234"));
        cart2.checkout(599.00);
        // Output:
        // Processing checkout...
        // Paid ₹599.0 via Credit Card ending in 1234
        // Order confirmed!
    }
}
```

---

## Example 4 — Logging System: Log Level Strategies

Your app logs messages. In production you want only errors. In development you want every debug line. Without Strategy you have `if (env == "prod")` scattered everywhere.

```java
interface LogStrategy {
    void log(String message);
}

class DebugLogger implements LogStrategy {
    public void log(String message) {
        System.out.println("[DEBUG] " + message);
    }
}

class ErrorOnlyLogger implements LogStrategy {
    public void log(String message) {
        // Ignores anything that doesn't start with ERROR
        if (message.startsWith("ERROR")) {
            System.out.println("[ERROR] " + message);
        }
    }
}

class SilentLogger implements LogStrategy {
    public void log(String message) {
        // Intentionally does nothing — used in unit tests to suppress output
    }
}

class AppLogger {
    private LogStrategy strategy;

    public AppLogger(LogStrategy strategy) {
        this.strategy = strategy;
    }

    public void log(String message) {
        strategy.log(message); // delegates to the active log strategy
    }
}

class LoggingMain {
    public static void main(String[] args) {
        // In development: see everything
        AppLogger devLogger = new AppLogger(new DebugLogger());
        devLogger.log("User clicked button");            // Output: [DEBUG] User clicked button
        devLogger.log("ERROR: DB connection failed");   // Output: [DEBUG] ERROR: DB connection failed

        // In production: errors only
        AppLogger prodLogger = new AppLogger(new ErrorOnlyLogger());
        prodLogger.log("User clicked button");           // (no output — silently ignored)
        prodLogger.log("ERROR: DB connection failed");  // Output: [ERROR] ERROR: DB connection failed

        // In tests: total silence
        AppLogger testLogger = new AppLogger(new SilentLogger());
        testLogger.log("Anything"); // (no output)
    }
}
```

---

## Example 5 — Ride-Hailing App: Fare Calculation Strategies

A taxi app charges differently for standard rides, surge pricing at peak hours, and flat corporate rates. The `RideBooking` context stays the same; only the strategy changes.

```java
interface FareStrategy {
    double calculateFare(double distanceKm);
}

class StandardFare implements FareStrategy {
    public double calculateFare(double distanceKm) {
        double fare = 20 + (distanceKm * 12); // base + per km
        System.out.println("Standard fare for " + distanceKm + " km = ₹" + fare);
        return fare;
    }
}

class SurgeFare implements FareStrategy {
    private double surgeMultiplier;
    public SurgeFare(double surgeMultiplier) { this.surgeMultiplier = surgeMultiplier; }

    public double calculateFare(double distanceKm) {
        double fare = (20 + (distanceKm * 12)) * surgeMultiplier;
        System.out.println("Surge fare (x" + surgeMultiplier + ") for " + distanceKm + " km = ₹" + fare);
        return fare;
    }
}

class CorporateFlatFare implements FareStrategy {
    private double flatRatePerKm;
    public CorporateFlatFare(double flatRatePerKm) { this.flatRatePerKm = flatRatePerKm; }

    public double calculateFare(double distanceKm) {
        double fare = distanceKm * flatRatePerKm;
        System.out.println("Corporate flat fare for " + distanceKm + " km = ₹" + fare);
        return fare;
    }
}

class RideBooking {
    private FareStrategy fareStrategy;

    public RideBooking(FareStrategy fareStrategy) {
        this.fareStrategy = fareStrategy;
    }

    public void confirmRide(double distanceKm) {
        System.out.println("Booking confirmed.");
        fareStrategy.calculateFare(distanceKm);
    }
}

class RideMain {
    public static void main(String[] args) {
        // Normal ride
        new RideBooking(new StandardFare()).confirmRide(10);
        // Output: Booking confirmed.
        //         Standard fare for 10.0 km = ₹140.0

        // Peak hour surge
        new RideBooking(new SurgeFare(2.5)).confirmRide(10);
        // Output: Booking confirmed.
        //         Surge fare (x2.5) for 10.0 km = ₹350.0

        // Corporate employee with flat rate
        new RideBooking(new CorporateFlatFare(8)).confirmRide(10);
        // Output: Booking confirmed.
        //         Corporate flat fare for 10.0 km = ₹80.0
    }
}
```

---

## How to Spot When to Use This Pattern

| Signal / Code Smell | Why Strategy Helps |
|---|---|
| A method has 3+ `if-else` or `switch` branches choosing a behavior | Each branch becomes a separate strategy class |
| Adding a new variant forces editing an existing class | New variant = new class only, nothing else changes |
| You can't unit-test one algorithm without the whole class | Each strategy class is independently testable |
| The same behavior is needed in multiple classes | Extract it into a shared strategy — no copy-paste |
| Behavior needs to change at runtime (e.g., based on user choice) | Call `setStrategy()` to swap live |

---

## Common Mistakes Beginners Make

- **Using Strategy for only 2 options.** If you only ever have `if (x) A else B`, a simple boolean parameter might be cleaner. Strategy pays off at 3+ variants or when variants grow over time.

- **Putting state inside the strategy class.** A strategy should ideally be stateless — it performs a calculation and returns a result. If you store data inside it, you can no longer safely reuse the same instance across multiple contexts. Pass data as method parameters instead.

- **Forgetting to inject the strategy.** Beginners sometimes call `new ConcreteStrategyA()` inside the Context constructor. That defeats the purpose — the Context must receive the strategy from outside (via constructor or setter) so the caller controls which one is used.

- **Creating a Strategy interface for every single method.** Strategy is for a **family of interchangeable algorithms** doing the same job. It is not meant to wrap every single method in a class.

---

## Quick Reference

| | Details |
|---|---|
| **Intent** | Define a family of algorithms, encapsulate each, make them interchangeable |
| **Key Roles** | `Strategy` (interface), `ConcreteStrategy` (implementation), `Context` (uses strategy) |
| **Java Hints** | `interface` for Strategy, constructor/setter injection in Context, no `instanceof` or `if-else` in Context |
| **When to Use** | Multiple variants of the same algorithm; variants grow over time; need runtime swapping |
| **When NOT to Use** | Only 1–2 variants with no likelihood of growth; algorithm never changes at runtime |
