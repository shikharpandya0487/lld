# OOP Concepts You Must Know (Beyond the Basics)

> **Assumed knowledge:** Interfaces, abstract classes, inheritance, polymorphism, encapsulation — you already use these in Singleton, Factory, and Abstract Factory patterns.  
> **This file covers:** The deeper Java OOP mechanics that show up constantly in design patterns, LLD interviews, and real codebases.

---

## 1. Composition vs Inheritance

### Inheritance ("is-a")
A subclass extends a parent class and **inherits** its behaviour.

```java
class Animal {
    public void breathe() { System.out.println("breathing"); }
}

class Dog extends Animal {
    public void bark() { System.out.println("woof"); }
}
```

Problems with over-using inheritance:
- Tight coupling — changing the parent class breaks all children.
- Java has no multiple inheritance of classes, so you paint yourself into a corner.
- Subclass inherits **everything**, even things it doesn't need.

### Composition ("has-a")
Instead of extending, you hold a **reference** to another object and delegate to it.

```java
class Engine {
    public void start() { System.out.println("engine started"); }
}

class Car {
    private final Engine engine;    // Car HAS-A Engine

    public Car(Engine engine) { this.engine = engine; }

    public void start() { engine.start(); }  // delegation
}
```

**Why prefer composition?**
- Swap implementations at runtime by injecting a different object.
- No accidental inheritance of unwanted behaviour.
- Works across class hierarchies — `Car` can hold any `Engine` implementation.

> **Rule of thumb:** "Favour composition over inheritance" — GoF, SOLID (Open/Closed), and every senior engineer you will meet.

---

## 2. Immutability

An **immutable object** cannot be changed after construction. Its state is fixed for its entire lifetime.

### How to make a class immutable in Java

```java
public final class Money {               // (1) final — cannot be subclassed

    private final String currency;       // (2) all fields are final
    private final double amount;

    public Money(String currency, double amount) {
        this.currency = currency;
        this.amount   = amount;
    }

    // (3) Only getters — no setters
    public String getCurrency() { return currency; }
    public double getAmount()   { return amount; }

    // (4) "Modification" returns a NEW object — original is untouched
    public Money add(Money other) {
        if (!this.currency.equals(other.currency))
            throw new IllegalArgumentException("Currency mismatch");
        return new Money(currency, this.amount + other.amount);
    }

    @Override public String toString() { return amount + " " + currency; }
}
```

```java
Money a = new Money("INR", 100);
Money b = new Money("INR", 50);
Money c = a.add(b);   // a and b are untouched; c is a brand new object
System.out.println(a); // 100.0 INR
System.out.println(c); // 150.0 INR
```

**Why immutability matters:**
- Thread-safe by default — multiple threads can read the same object with zero synchronisation.
- Easier to reason about — no surprise mutations from somewhere else.
- Safe as Map keys and Set elements (their hash never changes).

> **Design patterns that depend on immutability:** Builder (the Product is immutable), Singleton (shared state must not mutate), Value Objects in DDD.

---

## 3. Static Nested Classes vs Inner Classes

This distinction is crucial for the Builder pattern and many others.

### Inner class (non-static)

```java
public class Outer {
    private int x = 10;

    class Inner {               // NON-static inner class
        void show() {
            System.out.println(x);  // can access Outer's instance fields
        }
    }
}

// To instantiate:
Outer outer = new Outer();
Outer.Inner inner = outer.new Inner();  // requires an Outer instance
```

### Static nested class

```java
public class Outer {
    private static int y = 20;
    private int x = 10;

    static class StaticNested {   // STATIC nested class
        void show() {
            System.out.println(y);  // can only access Outer's STATIC members
            // System.out.println(x); ← COMPILE ERROR — x is not static
        }
    }
}

// To instantiate:
Outer.StaticNested nested = new Outer.StaticNested();  // No Outer instance needed
```

### Why Builder uses a static nested class

The `Builder` class needs to create an instance of `Product` (the outer class). If `Builder` were a non-static inner class, you'd need a `Product` instance first — but that's exactly what you're trying to build. Static nested class breaks this circular dependency.

Also, `Builder` being static means it can access `Product`'s **private** constructor — because nested classes share private access with their enclosing class.

---

## 4. Method Chaining (Fluent Interface)

Method chaining = each method **returns `this`** so you can call the next method immediately on the same line.

```java
public class QueryBuilder {

    private String table;
    private String where;
    private int limit;

    public QueryBuilder from(String table) {
        this.table = table;
        return this;            // ← returns 'this' — same object
    }

    public QueryBuilder where(String condition) {
        this.where = condition;
        return this;
    }

    public QueryBuilder limit(int n) {
        this.limit = n;
        return this;
    }

    public String build() {
        return "SELECT * FROM " + table + " WHERE " + where + " LIMIT " + limit;
    }
}
```

```java
String sql = new QueryBuilder()
        .from("orders")
        .where("status = 'pending'")
        .limit(50)
        .build();
```

Without chaining you'd write:
```java
QueryBuilder qb = new QueryBuilder();
qb.from("orders");
qb.where("status = 'pending'");
qb.limit(50);
String sql = qb.build();
```

Both are identical in what they do — chaining is purely a readability win.

---

## 5. Covariant Return Types

When you override a method, you are allowed to **narrow** the return type (return a subtype instead of the parent type). This is called covariant return.

```java
class Animal {
    public Animal create() { return new Animal(); }
}

class Dog extends Animal {
    @Override
    public Dog create() {    // return type is Dog, not Animal — still valid
        return new Dog();
    }
}
```

**Where it matters in design patterns:**

In builder hierarchies, when a child builder overrides a parent builder's setters, it returns its own type (not the parent type) so chaining continues to work correctly.

```java
class VehicleBuilder {
    protected String color;

    public VehicleBuilder color(String c) { this.color = c; return this; }
}

class CarBuilder extends VehicleBuilder {
    private int doors;

    @Override
    public CarBuilder color(String c) {   // Covariant — returns CarBuilder
        super.color(c);
        return this;
    }

    public CarBuilder doors(int d) { this.doors = d; return this; }
}
```

```java
// Without covariant return, .color() would return VehicleBuilder,
// and .doors() would not be visible. With it, chaining works perfectly:
new CarBuilder().color("Red").doors(4);
```

---

## 6. Generics (Type Parameters)

Generics let you write code that works with **any type** while retaining compile-time type safety — no casting, no `ClassCastException` at runtime.

### Generic class

```java
public class Box<T> {           // T is a type parameter (placeholder)
    private T value;

    public Box(T value) { this.value = value; }
    public T get()      { return value; }
}

Box<String>  strBox = new Box<>("hello");
Box<Integer> intBox = new Box<>(42);

String s = strBox.get();   // No cast needed
Integer n = intBox.get();  // No cast needed
```

### Generic method

```java
public <T> List<T> repeat(T item, int times) {
    List<T> result = new ArrayList<>();
    for (int i = 0; i < times; i++) result.add(item);
    return result;
}

List<String>  words  = repeat("hello", 3);  // ["hello", "hello", "hello"]
List<Integer> nums   = repeat(7, 4);        // [7, 7, 7, 7]
```

### Bounded type parameters

```java
// T must be a Number or a subclass of Number
public <T extends Number> double sum(List<T> list) {
    double total = 0;
    for (T item : list) total += item.doubleValue();
    return total;
}

sum(List.of(1, 2, 3));         // works — Integer extends Number
sum(List.of(1.5, 2.5));        // works — Double extends Number
// sum(List.of("a", "b"));     // compile error — String does not extend Number
```

**Where generics appear in patterns:**
- Factory: `Factory<T>` that creates any product type
- Builder: `Builder<T>` for a fluent hierarchy
- Repository pattern: `Repository<T, ID>` (key LLD interview concept)

---

## 7. The `final` Keyword

`final` has one core idea: **"this cannot be changed after this point."** What "changed" means depends on what you apply it to.

| `final` on | Means |
|---|---|
| **variable / field** | Assigned once, never reassigned (reference lock, not object lock) |
| **method** | Cannot be overridden in any subclass |
| **class** | Cannot be extended — no subclasses allowed |

---

### 7a. `final` Variable / Field

The variable can only be **assigned once**. After that, reassignment is a compile error.

#### Local variable
```java
final int x = 10;
x = 20;  // COMPILE ERROR — cannot assign a value to final variable x
```

#### Instance field — must be set in the constructor
```java
public class Circle {
    private final double radius;

    public Circle(double radius) {
        this.radius = radius;  // assigned exactly once, here
    }

    public void scale() {
        this.radius = this.radius * 2;  // COMPILE ERROR
    }
}
```

If you declare `final` but never assign it, that's also a compile error:
```java
private final double radius;  // never assigned in constructor → COMPILE ERROR
```

#### Important nuance — `final` ≠ immutable for objects

`final` only locks the **reference**, not the **object the reference points to**.

```java
final List<String> names = new ArrayList<>();
names = new ArrayList<>();      // COMPILE ERROR — cannot reassign the reference
names.add("Alice");             // FINE — the list itself is still mutable
names.clear();                  // FINE — still the same list object
```

The reference `names` is locked to one `ArrayList` instance. But that instance's internal state can still change freely.

---

### 7b. `final` Method

A `final` method **cannot be overridden** by any subclass. The implementation is locked.

```java
class Logger {
    public final void log(String message) {
        System.out.println("[LOG] " + message);
    }
}

class FancyLogger extends Logger {
    @Override
    public void log(String message) {  // COMPILE ERROR — cannot override final method
        System.out.println("[FANCY] " + message);
    }
}
```

#### When would you actually use this?

When a method encodes a **critical invariant** that subclasses must not break.

```java
class BankAccount {
    private double balance;

    // This logic must never be altered by a subclass
    public final void transfer(BankAccount target, double amount) {
        this.balance   -= amount;
        target.balance += amount;
        auditLog(amount);   // compliance — cannot be skipped
    }
}
```

A subclass can extend `BankAccount` and add new features, but can never override `transfer()` and silently skip the audit log.

#### Subclass can still call it — just not override it
```java
class SavingsAccount extends BankAccount {
    public void saveAndTransfer(BankAccount target, double amount) {
        super.transfer(target, amount);  // FINE — calling is allowed
    }
}
```

---

### 7c. `final` Class

A `final` class **cannot be subclassed at all**. No `extends` is allowed.

```java
public final class Money {
    private final double amount;
    private final String currency;

    public Money(double amount, String currency) {
        this.amount   = amount;
        this.currency = currency;
    }
}

class SpecialMoney extends Money {  // COMPILE ERROR — cannot inherit from final class Money
}
```

#### Why would you seal a class?

**Reason 1 — Immutability guarantee**

If `Money` is not `final`, someone can subclass it and add a mutable field:
```java
class EvilMoney extends Money {
    public double secretAmount;  // mutable — breaks the contract
}
```
Making `Money` `final` closes this loophole permanently.

**Reason 2 — Security**

`java.lang.String` is `final`. If it weren't, someone could subclass `String`, override `equals()` with malicious logic, and pass their fake `String` wherever a real one is expected.

**Reason 3 — You are done designing the hierarchy**

Sometimes you explicitly do not want others extending your class because you cannot reason about what a subclass might do.

---

### How the Three Uses Relate

```
final class   → nobody can extend this class
final method  → nobody can override this specific method (subclassing is still allowed)
final field   → this reference/value is set once and never reassigned
```

They compose:
- An **immutable class** uses all three: `final class`, all fields `final`, and no setters.
- A **template method** might use `final` on the template method itself but leave the abstract hook methods open.

---

### Real Examples from the JDK

| JDK class / member | What is `final` | Why |
|---|---|---|
| `java.lang.String` | The class | Security + immutability guarantee |
| `java.lang.Integer` | The class | Value semantics, no subclass corruption |
| `Object.getClass()` | The method | Must always return the true runtime class |
| Every field in `String` | Fields | Immutability — hash cannot change |

---

## 8. The `this()` and `super()` Constructor Calls

### `this()` — delegating to another constructor in the same class

```java
public class Rectangle {
    private final int width;
    private final int height;

    public Rectangle(int width, int height) {
        this.width  = width;
        this.height = height;
    }

    // Convenience constructor — delegates to the full one
    public Rectangle(int side) {
        this(side, side);   // calls Rectangle(int, int)
    }
}
```

### `super()` — calling the parent constructor

```java
class Animal {
    String name;
    Animal(String name) { this.name = name; }
}

class Dog extends Animal {
    String breed;

    Dog(String name, String breed) {
        super(name);          // MUST be the first statement — initialises Animal part
        this.breed = breed;
    }
}
```

> **Rule:** If you do not call `super()` explicitly, Java inserts a `super()` (no-arg) call automatically. If the parent has no no-arg constructor, you **must** call `super(...)` explicitly or it won't compile.

---

## 9. Object Cloning — Shallow vs Deep Copy

### Shallow copy

Copies the object's primitive fields **by value**, but object-reference fields **by reference**. Both the original and the copy point to the same nested object.

```java
class Address {
    String city;
    Address(String city) { this.city = city; }
}

class Person {
    String name;
    Address address;

    Person(String name, Address address) {
        this.name    = name;
        this.address = address;
    }

    // Shallow copy constructor
    Person(Person other) {
        this.name    = other.name;
        this.address = other.address;  // ← SAME object reference!
    }
}

Person alice = new Person("Alice", new Address("Delhi"));
Person copy  = new Person(alice);

copy.address.city = "Mumbai";
System.out.println(alice.address.city); // "Mumbai" — Alice was also changed!
```

### Deep copy

Copies everything recursively — nested objects are also cloned.

```java
// Deep copy constructor
Person(Person other) {
    this.name    = other.name;
    this.address = new Address(other.address.city);  // ← new object
}

Person alice = new Person("Alice", new Address("Delhi"));
Person copy  = new Person(alice);

copy.address.city = "Mumbai";
System.out.println(alice.address.city); // "Delhi" — Alice is safe
```

> **In the Builder pattern's copy-builder** (where you do `new Builder(existingProduct)`), always decide whether you need a shallow or deep copy of collection/reference fields.

---

## 10. Varargs (Variable Arguments)

Varargs lets a method accept **zero or more arguments** of the same type without the caller wrapping them in an array.

```java
public void sendTo(String from, String... recipients) {
    System.out.println("From: " + from);
    for (String r : recipients) {
        System.out.println("To: " + r);
    }
}

// All valid:
sendTo("alice@mail.com");
sendTo("alice@mail.com", "bob@mail.com");
sendTo("alice@mail.com", "bob@mail.com", "carol@mail.com");
```

Internally, `recipients` is just a `String[]`. You can also pass an array directly:

```java
String[] team = {"bob@mail.com", "carol@mail.com"};
sendTo("alice@mail.com", team);
```

> **Rules:** Only one varargs param is allowed per method, and it must be the **last** parameter.

---

## 11. Enums as First-Class Types

Java enums are full classes — they can have fields, constructors, and methods.

```java
public enum Planet {
    MERCURY(3.303e+23, 2.4397e6),
    VENUS  (4.869e+24, 6.0518e6),
    EARTH  (5.976e+24, 6.37814e6);

    private final double mass;    // kg
    private final double radius;  // metres

    Planet(double mass, double radius) {
        this.mass   = mass;
        this.radius = radius;
    }

    double surfaceGravity() {
        final double G = 6.67300E-11;
        return G * mass / (radius * radius);
    }

    double surfaceWeight(double otherMass) {
        return otherMass * surfaceGravity();
    }
}
```

```java
double earthWeight = 75.0;
double mass = earthWeight / Planet.EARTH.surfaceGravity();

for (Planet p : Planet.values()) {
    System.out.printf("Weight on %s = %.2f%n", p, p.surfaceWeight(mass));
}
```

**Enums in design patterns:**
- Singleton: `enum Singleton { INSTANCE; }` — the simplest thread-safe Singleton in Java.
- Strategy: one enum constant per strategy.
- Factory: `switch` on an enum to pick the right factory.

---

## 12. Access Modifiers — Full Picture

| Modifier | Same class | Same package | Subclass (diff package) | Everywhere |
|---|---|---|---|---|
| `private` | ✅ | ❌ | ❌ | ❌ |
| *(package-private / default)* | ✅ | ✅ | ❌ | ❌ |
| `protected` | ✅ | ✅ | ✅ | ❌ |
| `public` | ✅ | ✅ | ✅ | ✅ |

**Key points for design patterns:**

- `private` constructor → enforces Singleton or forces use of a factory/builder.
- `protected` constructor → allows subclassing (Template Method pattern) but blocks direct instantiation.
- Package-private → useful for keeping helper classes out of the public API.

---

## 13. Dependency Injection (DI)

DI is not a GoF pattern — it is an OOP technique. Instead of a class **creating** its dependencies, they are **provided** (injected) from outside.

### Without DI (tightly coupled)

```java
class OrderService {
    private PaymentGateway gateway = new StripeGateway();  // hard-coded

    void placeOrder(Order o) { gateway.charge(o.total()); }
}
```

### With DI (loosely coupled)

```java
class OrderService {
    private final PaymentGateway gateway;   // depends on abstraction

    // Constructor injection — the caller decides which gateway to use
    public OrderService(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    void placeOrder(Order o) { gateway.charge(o.total()); }
}

// In production:
OrderService svc = new OrderService(new StripeGateway());

// In tests:
OrderService svc = new OrderService(new MockPaymentGateway());
```

DI works naturally with **interfaces** (the abstraction you already know). It is the reason every major framework (Spring, Guice) exists.

---

## 14. Object Equality — `equals()` and `hashCode()`

By default, `==` and `equals()` check **reference equality** (same memory address). For value objects (like `Money`, `User`), you usually want **structural equality**.

```java
public class Money {
    private final double amount;
    private final String currency;

    // ... constructor and getters

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;                          // same reference
        if (!(o instanceof Money)) return false;             // different type
        Money other = (Money) o;
        return Double.compare(amount, other.amount) == 0
            && currency.equals(other.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
}
```

```java
Money a = new Money(100, "INR");
Money b = new Money(100, "INR");

System.out.println(a == b);       // false — different objects
System.out.println(a.equals(b));  // true  — same value
```

**The contract:**
- If `a.equals(b)` is `true`, then `a.hashCode() == b.hashCode()` **must** also be `true`.
- Violating this breaks `HashMap`, `HashSet`, and any hash-based collection.

---

## Quick Reference — Pattern to Concept Mapping

| Design Pattern | Key OOP Concepts Used |
|---|---|
| Singleton | `private` constructor, `static`, `final`, Enum |
| Factory Method | Polymorphism, interfaces, `protected` constructor |
| Abstract Factory | Interface hierarchies, composition |
| **Builder** | Static nested class, method chaining, immutability, `final` fields |
| Strategy | Composition, interfaces, DI |
| Template Method | Inheritance, `abstract` methods, `final` methods |
| Observer | Interfaces, collections, loose coupling |
| Decorator | Composition, interfaces, delegation |
