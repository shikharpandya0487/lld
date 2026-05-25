# SOLID Principles — A Complete Beginner's Guide

SOLID is an acronym for five design principles that make object-oriented software easier to understand, maintain, and extend. They were introduced by Robert C. Martin (Uncle Bob).

| Letter | Principle | One-line summary |
|--------|-----------|-----------------|
| **S** | Single Responsibility Principle | A class should have only one reason to change |
| **O** | Open/Closed Principle | Open for extension, closed for modification |
| **L** | Liskov Substitution Principle | Subtypes must be substitutable for their base types |
| **I** | Interface Segregation Principle | No client should be forced to depend on methods it does not use |
| **D** | Dependency Inversion Principle | Depend on abstractions, not on concrete implementations |

---

## S — Single Responsibility Principle (SRP)

### What it means
A class should have **one, and only one, reason to change**. In other words, each class should do exactly one thing and do it well.

### Why it matters
When a class handles multiple responsibilities, a change in one area can accidentally break another. It also makes testing harder because you have to set up the whole class just to test one small piece.

### Bad Example — violating SRP

```java
// BAD: This class does TOO MANY things
public class Book {
    private String title;
    private String author;

    // Responsibility 1: Book data
    public String getTitle() { return title; }
    public String getAuthor() { return author; }

    // Responsibility 2: Searching — has nothing to do with Book data
    public Book searchByTitle(String title) { /* DB query logic */ return null; }

    // Responsibility 3: Printing — also unrelated to Book data
    public void printBookDetails() { System.out.println(title + " by " + author); }

    // Responsibility 4: Saving — yet another concern mixed in
    public void saveToDatabase() { /* DB save logic */ }
}
```

This class has **four reasons to change**: if the book's fields change, if the search logic changes, if the print format changes, or if the database changes. Any one of those changes forces you to open and modify this single class.

### Good Example — applying SRP

This is exactly how this project (`src/srp/library_system/`) is structured:

```java
// Responsibility 1: Holds book data ONLY
public class Book {
    private String title;
    private String author;
    private String isbn;

    public Book(String title, String author, String isbn) { ... }
    public String getTitle()  { return title;  }
    public String getAuthor() { return author; }
    public String getIsbn()   { return isbn;   }
}

// Responsibility 2: Registering books ONLY
public class BookRegistration {
    public void registerBook(Book book) {
        // Saves to DB, validates, etc.
    }
}

// Responsibility 3: Searching books ONLY
public class BookSearchService {
    public Book retrieveBookByTitle(String title) {
        // Query logic
        return null;
    }
}

// Responsibility 4: Printing / displaying documents ONLY
public class DocumentPrinterService {
    public void printBookDetails(Book book) {
        System.out.println(book.getTitle() + " by " + book.getAuthor());
    }
}

// Responsibility 5: Coordinating user interactions ONLY
public class LibraryController {
    private BookRegistration    bookRegistration;
    private BookSearchService   bookSearchService;

    public void manageLibrary() {
        // Reads user input, delegates to services
    }
}
```

Now each class has **exactly one reason to change**.

### Real-Life Example 1 — E-commerce Order System

```java
// BAD — one god class
public class Order {
    public void calculateTotal()    { /* pricing logic    */ }
    public void saveToDatabase()    { /* DB write         */ }
    public void sendEmailReceipt()  { /* email sending    */ }
    public void generateInvoicePDF(){ /* PDF generation   */ }
    public void applyDiscount()     { /* discount rules   */ }
}

// GOOD — split by responsibility
public class Order              { /* just order data & total */ }
public class OrderRepository    { /* DB read/write           */ }
public class OrderEmailService  { /* email receipt           */ }
public class InvoiceGenerator   { /* PDF generation          */ }
public class DiscountService    { /* discount rules          */ }
```

**Real-world trigger:** Your marketing team changes the email template. With the bad design you risk breaking the PDF logic or DB save. With the good design you touch only `OrderEmailService`.

### Real-Life Example 2 — User Authentication

```java
// BAD
public class User {
    public void login()           { /* auth logic     */ }
    public void saveProfile()     { /* DB logic       */ }
    public void sendWelcomeEmail(){ /* email logic    */ }
    public void logActivity()     { /* logging logic  */ }
}

// GOOD
public class User                 { /* user data model        */ }
public class AuthService          { /* login / session        */ }
public class UserRepository       { /* DB persistence         */ }
public class UserNotificationSvc  { /* emails / SMS           */ }
public class ActivityLogger       { /* audit trail            */ }
```

---

## O — Open/Closed Principle (OCP)

### What it means
Software entities (classes, modules, functions) should be **open for extension but closed for modification**. You should be able to add new behaviour without changing existing, tested code.

### Why it matters
Every time you modify existing code you risk introducing bugs. OCP lets you add features by writing new code (extending), not by editing old code.

### Bad Example — violating OCP

```java
// BAD: every new database type requires modifying this class
public class CartStorageService {
    public void save(Cart cart, String dbType) {
        if (dbType.equals("SQL")) {
            // SQL-specific save logic
        } else if (dbType.equals("MongoDB")) {
            // Mongo-specific save logic
        }
        // Adding Cassandra? You must MODIFY this method — risky!
    }
}
```

### Good Example — applying OCP

This is how `src/ocp/` is structured in this project:

```java
// Abstraction (the "closed" contract)
public interface DBPersistent {
    void save(Product product);
}

// Extension 1 — SQL (open for extension)
public class SQLDb implements DBPersistent {
    @Override
    public void save(Product product) {
        System.out.println("Saving to SQL: " + product.getName());
    }
}

// Extension 2 — MongoDB (open for extension)
public class MongoDb implements DBPersistent {
    @Override
    public void save(Product product) {
        System.out.println("Saving to MongoDB: " + product.getName());
    }
}

// Adding Cassandra? Just create a new class — ZERO modification to existing code
public class CassandraDb implements DBPersistent {
    @Override
    public void save(Product product) {
        System.out.println("Saving to Cassandra: " + product.getName());
    }
}

// Cart depends on the abstraction, not the concrete type
public class Cart {
    private DBPersistent db;

    public Cart(DBPersistent db) {
        this.db = db;
    }

    public void saveProduct(Product product) {
        db.save(product);
    }
}
```

### Real-Life Example 1 — Payment Gateway

```java
// Abstraction
public interface PaymentProcessor {
    void processPayment(double amount);
}

// Each payment method is a new class, never touching the others
public class CreditCardProcessor implements PaymentProcessor { ... }
public class PayPalProcessor      implements PaymentProcessor { ... }
public class CryptoProcessor      implements PaymentProcessor { ... }   // new — no old code touched

// Checkout uses the interface
public class Checkout {
    private PaymentProcessor processor;
    public Checkout(PaymentProcessor processor) { this.processor = processor; }
    public void pay(double amount) { processor.processPayment(amount); }
}
```

**Real-world trigger:** Business adds UPI payments. You just write `UpiProcessor implements PaymentProcessor` — the `Checkout` class is untouched and still fully tested.

### Real-Life Example 2 — Notification System

```java
public interface NotificationSender {
    void send(String message, String recipient);
}

public class EmailNotification   implements NotificationSender { ... }
public class SMSNotification     implements NotificationSender { ... }
public class PushNotification    implements NotificationSender { ... }  // added later, no old code changed
public class WhatsAppNotification implements NotificationSender { ... } // added even later

public class NotificationService {
    private List<NotificationSender> senders;

    public NotificationService(List<NotificationSender> senders) {
        this.senders = senders;
    }

    public void notifyAll(String message, String recipient) {
        for (NotificationSender s : senders) s.send(message, recipient);
    }
}
```

---

## L — Liskov Substitution Principle (LSP)

### The Simple Analogy First

Imagine you have a TV remote. You know it can:
- Turn the TV on/off
- Change the channel
- Adjust the volume

Now your old remote breaks and you get a new one. You **expect** the new remote to do the same things. You don't expect the "Volume Up" button to suddenly turn the TV off. The new remote is a **substitute** for the old one — it keeps all the same promises.

LSP says the same thing about classes: **a subclass must keep all the promises made by the parent class**.

---

### The Formal Definition (in plain English)

> *"If S is a subtype of T, then objects of type T may be replaced with objects of type S without breaking the program."*
> — Barbara Liskov, 1987

Break it down:
- **T** = the parent class (e.g. `Bird`)
- **S** = the child/subclass (e.g. `Penguin`)
- **"replaced without breaking"** = every piece of code that works with `Bird` must still work correctly when handed a `Penguin`

---

### The Core Idea — "Is-A" vs "Behaves-Like-A"

In school we learn: *"if Penguin IS-A Bird, then Penguin should extend Bird."*

LSP adds an important second condition: *"Penguin must also BEHAVE-LIKE-A Bird in every way the parent promises."*

A Penguin **is** a bird biologically. But in code, if your `Bird` class promises the ability to fly and a `Penguin` cannot fly, then `Penguin` **should NOT extend Bird** — because it cannot keep that promise.

> Rule of thumb: Inheritance in code is about **behaviour contracts**, not real-world taxonomy.

---

### Violation Type 1 — Throwing an Exception for an Inherited Method

This is the most common LSP violation for beginners.

```java
// Parent makes a promise: "all Birds can fly"
public class Bird {
    public void fly() {
        System.out.println("Flying through the sky!");
    }
}

// Subclass BREAKS that promise
public class Penguin extends Bird {
    @Override
    public void fly() {
        // Penguin can't fly, so we throw an exception
        throw new UnsupportedOperationException("Penguins can't fly!");
    }
}
```

Now look at how this breaks things downstream:

```java
public class ZooSimulator {

    // This method accepts any Bird — it trusts the Bird contract
    public void exerciseBird(Bird bird) {
        System.out.println("Exercising bird...");
        bird.fly(); // CRASH at runtime if bird is a Penguin!
    }

    public static void main(String[] args) {
        ZooSimulator zoo = new ZooSimulator();

        Bird eagle   = new Eagle();
        Bird penguin = new Penguin();   // looks fine — it's a Bird, right?

        zoo.exerciseBird(eagle);    // works fine
        zoo.exerciseBird(penguin);  // RUNTIME CRASH — UnsupportedOperationException!
    }
}
```

The caller `ZooSimulator` had no idea it was going to crash. It trusted `Bird`'s contract. `Penguin` silently broke that contract.

**How to spot this violation:** If a subclass throws `UnsupportedOperationException`, `IllegalStateException`, or leaves a method body empty in an override — it is almost certainly an LSP violation.

---

### The Fix — Redesign the Hierarchy Around Behaviour

```java
// Step 1: Create a base class that only promises what ALL animals can do
public abstract class Animal {
    public abstract void eat();
    public abstract void breathe();
    public abstract void move();   // all animals move (walk, swim, fly — doesn't matter)
}

// Step 2: Create a separate interface for flying ability
public interface Flyable {
    void fly();
}

// Step 3: Eagle extends Animal AND implements Flyable
public class Eagle extends Animal implements Flyable {
    @Override public void eat()    { System.out.println("Eagle eating a fish");   }
    @Override public void breathe(){ System.out.println("Eagle breathing");       }
    @Override public void move()   { System.out.println("Eagle gliding");         }
    @Override public void fly()    { System.out.println("Eagle soaring at 3000m");}
}

// Step 4: Penguin extends Animal but does NOT implement Flyable
public class Penguin extends Animal {
    @Override public void eat()    { System.out.println("Penguin eating krill");  }
    @Override public void breathe(){ System.out.println("Penguin breathing");     }
    @Override public void move()   { System.out.println("Penguin waddling");      }
    // No fly() here — Penguin never promised it could fly!
}

// Step 5: The zoo simulator is now type-safe
public class ZooSimulator {

    public void exerciseAnimal(Animal animal) {
        animal.eat();    // safe — all Animals can eat
        animal.move();   // safe — all Animals can move
    }

    public void makeItFly(Flyable flyingThing) {
        flyingThing.fly(); // safe — only Flyable things can be passed here
    }

    public static void main(String[] args) {
        ZooSimulator zoo = new ZooSimulator();

        Eagle   eagle   = new Eagle();
        Penguin penguin = new Penguin();

        zoo.exerciseAnimal(eagle);    // works perfectly
        zoo.exerciseAnimal(penguin);  // works perfectly

        zoo.makeItFly(eagle);         // works perfectly
        // zoo.makeItFly(penguin);    // COMPILE ERROR — caught at compile time, not runtime!
    }
}
```

The error is now caught **at compile time**, not as a nasty runtime crash in production.

---

### Violation Type 2 — The Rectangle / Square Problem (Classic)

This is the most famous LSP example in computer science textbooks.

**The setup:** A Square is mathematically a special Rectangle (all sides equal). So it seems natural to write `Square extends Rectangle`.

```java
public class Rectangle {
    protected int width;
    protected int height;

    public void setWidth(int w)  { this.width  = w; }
    public void setHeight(int h) { this.height = h; }

    public int getArea() { return width * height; }

    // Rectangle's contract / promise:
    // "I can have INDEPENDENT width and height"
}
```

```java
public class Square extends Rectangle {

    // A square's sides must always be equal,
    // so when you set one dimension, we force both to match
    @Override
    public void setWidth(int w)  { this.width = this.height = w; }  // forces height too!
    @Override
    public void setHeight(int h) { this.width = this.height = h; }  // forces width too!
}
```

**Why this violates LSP — the proof:**

```java
public class ShapeTest {

    // This method was written for Rectangle and trusts its contract
    public void testArea(Rectangle r) {
        r.setWidth(5);   // set width to 5
        r.setHeight(4);  // set height to 4 independently

        // Any reasonable person expects: 5 × 4 = 20
        int area = r.getArea();
        System.out.println("Expected: 20, Got: " + area);

        // With Rectangle: area = 20  ✓
        // With Square:    area = 16  ✗  (setHeight(4) also set width to 4!)
    }

    public static void main(String[] args) {
        ShapeTest test = new ShapeTest();

        test.testArea(new Rectangle()); // prints: Expected: 20, Got: 20  ✓
        test.testArea(new Square());    // prints: Expected: 20, Got: 16  ✗ — LSP broken!
    }
}
```

The method `testArea` was written to work with `Rectangle`. It gets a `Square` (which IS-A Rectangle by inheritance), and it silently produces wrong results. No crash, no error — just a wrong answer. These are the worst bugs to debug.

**The fix — use a common interface, drop the inheritance:**

```java
// A Shape can calculate its area — that's all
public interface Shape {
    int getArea();
}

// Rectangle has independent width and height
public class Rectangle implements Shape {
    private int width;
    private int height;

    public Rectangle(int width, int height) {
        this.width  = width;
        this.height = height;
    }

    public void setWidth(int w)  { this.width  = w; }
    public void setHeight(int h) { this.height = h; }

    @Override
    public int getArea() { return width * height; }
}

// Square manages its own single side — completely independent
public class Square implements Shape {
    private int side;

    public Square(int side) {
        this.side = side;
    }

    public void setSide(int s) { this.side = s; }

    @Override
    public int getArea() { return side * side; }
}

// Now the code is honest about what it expects
public class ShapeTest {

    public void printArea(Shape shape) {
        System.out.println("Area: " + shape.getArea()); // works for both, correctly
    }
}
```

---

### Violation Type 3 — Silently Doing Nothing (Empty Override)

Sometimes instead of throwing an exception, a subclass quietly does nothing. This is also an LSP violation — perhaps more dangerous because there's no crash to alert you.

```java
public class EmailSender {
    public void send(String to, String message) {
        System.out.println("Sending email to " + to + ": " + message);
        // ... actual SMTP logic
    }
}

// "Test" version that quietly does nothing
public class NoOpEmailSender extends EmailSender {
    @Override
    public void send(String to, String message) {
        // intentionally empty — no email sent
    }
}
```

```java
public class UserRegistrationService {
    private EmailSender emailSender;

    public UserRegistrationService(EmailSender sender) {
        this.emailSender = sender;
    }

    public void register(User user) {
        // ... save user to DB
        emailSender.send(user.getEmail(), "Welcome!");
        // If sender is NoOpEmailSender, user never gets their welcome email
        // and there's NO error, NO log — it just silently disappears
    }
}
```

**The correct approach** — use a proper abstraction, not inheritance:

```java
public interface EmailSender {
    void send(String to, String message);
}

public class SmtpEmailSender implements EmailSender {
    @Override
    public void send(String to, String message) {
        System.out.println("SMTP: Sending to " + to);
    }
}

// For testing — it's honest about what it is
public class FakeEmailSender implements EmailSender {
    public List<String> sentEmails = new ArrayList<>();

    @Override
    public void send(String to, String message) {
        sentEmails.add(to);  // records it so tests can verify
        System.out.println("[TEST] Fake email to: " + to);
    }
}
```

---

### Violation Type 4 — Strengthening Preconditions

A subclass that **demands more** from the caller than the parent promised also violates LSP.

```java
public class Discount {
    // Parent says: "Give me any positive amount"
    public double apply(double price) {
        if (price <= 0) throw new IllegalArgumentException("Price must be positive");
        return price * 0.9; // 10% off
    }
}

public class PremiumDiscount extends Discount {
    @Override
    public double apply(double price) {
        // Subclass ADDS an extra restriction the parent never had
        if (price < 100) {
            throw new IllegalArgumentException("Premium discount requires price >= 100");
        }
        return price * 0.8; // 20% off
    }
}
```

```java
// Caller follows the parent's rules (price > 0), but PremiumDiscount crashes it
public void checkout(Discount discount, double price) {
    double finalPrice = discount.apply(price);  // crashes if PremiumDiscount and price = 50
}
```

The caller respected the parent's contract (`price > 0`). The subclass invented a new rule the caller didn't know about. That is LSP broken.

**Fix:** The subclass should accept everything the parent accepted (or more), never less.

```java
public class PremiumDiscount extends Discount {
    @Override
    public double apply(double price) {
        if (price <= 0) throw new IllegalArgumentException("Price must be positive"); // same rule as parent
        if (price < 100) return price * 0.9; // fall back to regular discount gracefully
        return price * 0.8; // 20% off for large amounts
    }
}
```

---

### Real-Life Example — Bank Accounts

```java
// BAD design
public class BankAccount {
    protected double balance;

    public void deposit(double amount)  { balance += amount; }
    public void withdraw(double amount) { balance -= amount; }
    public double getBalance()          { return balance; }
}

// A Fixed Deposit account cannot be withdrawn from before maturity
public class FixedDepositAccount extends BankAccount {
    @Override
    public void withdraw(double amount) {
        // Breaks the parent's promise
        throw new UnsupportedOperationException("Cannot withdraw from Fixed Deposit before maturity!");
    }
}

// This teller method works with any BankAccount — until it gets a FixedDeposit
public void processTellerRequest(BankAccount account, double amount) {
    account.withdraw(amount); // CRASH with FixedDepositAccount!
}
```

**The fix — model the hierarchy honestly:**

```java
// Base class only promises what ALL accounts can do
public abstract class BankAccount {
    protected double balance;

    public void deposit(double amount) { balance += amount; }
    public double getBalance()         { return balance; }
    // No withdraw() here — not all accounts support it
}

// Separate interface for accounts that support withdrawal
public interface Withdrawable {
    void withdraw(double amount);
}

// Savings account can be withdrawn
public class SavingsAccount extends BankAccount implements Withdrawable {
    @Override
    public void withdraw(double amount) {
        if (amount > balance) throw new IllegalArgumentException("Insufficient funds");
        balance -= amount;
    }
}

// Fixed Deposit — no withdrawal, and it's honest about it
public class FixedDepositAccount extends BankAccount {
    private java.time.LocalDate maturityDate;

    public void withdrawAtMaturity(double amount) {
        // Only possible after maturity — a different, explicitly named operation
        balance -= amount;
    }
}

// Teller method now explicitly says it needs a Withdrawable account
public void processTellerWithdrawal(Withdrawable account, double amount) {
    account.withdraw(amount); // 100% safe — only Withdrawable accounts can be passed
}
```

---

### Real-Life Example — Vehicle Accelerator

```java
// BAD
public class Vehicle {
    public void accelerate() {
        System.out.println("Vehicle speeding up");
    }
}

public class BrokenVehicle extends Vehicle {
    @Override
    public void accelerate() {
        throw new RuntimeException("Engine is broken, cannot accelerate!");
    }
}

public class TrafficController {
    public void startRace(List<Vehicle> vehicles) {
        for (Vehicle v : vehicles) {
            v.accelerate(); // CRASH if any vehicle is a BrokenVehicle
        }
    }
}
```

```java
// GOOD — model intent correctly
public interface Drivable {
    void accelerate();
    void brake();
}

public class Car implements Drivable {
    @Override public void accelerate() { System.out.println("Car accelerating"); }
    @Override public void brake()      { System.out.println("Car braking");      }
}

public class ElectricCar implements Drivable {
    @Override public void accelerate() { System.out.println("Electric car silently accelerating"); }
    @Override public void brake()      { System.out.println("Electric car braking + regenerating"); }
}

// BrokenVehicle simply does not implement Drivable
// It would have its own type: RepairShopVehicle, for example

public class TrafficController {
    public void startRace(List<Drivable> vehicles) {
        for (Drivable v : vehicles) {
            v.accelerate(); // 100% safe
        }
    }
}
```

---

### How to Detect an LSP Violation — Checklist

Ask yourself these questions about your subclass:

| Question | If YES → likely LSP violation |
|---|---|
| Does the subclass throw `UnsupportedOperationException` in any override? | Yes |
| Does the subclass leave an overridden method body completely empty? | Yes |
| Does the subclass add stricter input validation than the parent? | Yes |
| Does swapping parent → subclass make existing unit tests fail? | Yes |
| Does the calling code use `instanceof` to check the type before calling a method? | Yes |
| Does the subclass return a narrower range of values than the parent promised? | Yes |

---

### The `instanceof` Anti-Pattern — A Dead Giveaway

If you ever see code like this, it is almost always a sign that LSP has been broken somewhere upstream:

```java
// BAD — the caller should never need to check what type it has
public void exerciseBird(Bird bird) {
    if (bird instanceof Penguin) {
        // can't fly, do something different
        ((Penguin) bird).swim();
    } else {
        bird.fly();
    }
}
```

The whole point of polymorphism is that the caller should not need to know the specific type. If they do, the hierarchy is wrongly designed.

---

### Summary of LSP in One Diagram

```
Parent Class / Interface
    └── Makes PROMISES (method signatures + expected behaviour)
            │
            ├── Subclass A — keeps ALL promises     ✓  (LSP respected)
            ├── Subclass B — keeps ALL promises     ✓  (LSP respected)
            └── Subclass C — throws exception on
                             one inherited method   ✗  (LSP violated)

Any code written for the Parent must work correctly
with Subclass A, B, and C — no surprises, no crashes.
```

**The one-sentence rule:**
> A child class should be able to stand in for its parent anywhere the parent is used, and the program should still behave correctly.

---

## I — Interface Segregation Principle (ISP)

### What it means
**No client should be forced to depend on methods it does not use.** It is better to have many small, focused interfaces than one large, fat interface.

### Why it matters
A fat interface forces implementing classes to provide empty or throwing stubs for methods they don't need. This is misleading, noisy, and error-prone.

### Bad Example — violating ISP

```java
// FAT interface — forces every implementor to handle ALL methods
public interface Worker {
    void work();
    void eat();
    void sleep();
    void attendMeeting();
    void submitReport();
}

// A Robot doesn't eat or sleep — forced to fake it
public class Robot implements Worker {
    @Override public void work()           { System.out.println("Robot working"); }
    @Override public void eat()            { /* does nothing — bad! */            }
    @Override public void sleep()          { /* does nothing — bad! */            }
    @Override public void attendMeeting()  { /* robots don't meet */              }
    @Override public void submitReport()   { System.out.println("Robot reporting");}
}
```

### Good Example — applying ISP

```java
// Split into focused interfaces
public interface Workable   { void work();           }
public interface Eatable    { void eat();            }
public interface Sleepable  { void sleep();          }
public interface Reportable { void submitReport();   }
public interface Meetable   { void attendMeeting();  }

// Human implements only what humans do
public class HumanWorker implements Workable, Eatable, Sleepable, Reportable, Meetable {
    @Override public void work()          { System.out.println("Human working");  }
    @Override public void eat()           { System.out.println("Human eating");   }
    @Override public void sleep()         { System.out.println("Human sleeping"); }
    @Override public void submitReport()  { System.out.println("Submitting report"); }
    @Override public void attendMeeting() { System.out.println("In meeting");     }
}

// Robot implements only what robots do
public class Robot implements Workable, Reportable {
    @Override public void work()         { System.out.println("Robot working");   }
    @Override public void submitReport() { System.out.println("Robot reporting"); }
    // No fake eat() or sleep() needed!
}
```

### Real-Life Example 1 — Printers and Scanners

```java
// BAD: Not all devices can do everything
public interface MultiFunctionDevice {
    void print(Document doc);
    void scan(Document doc);
    void fax(Document doc);
    void photocopy(Document doc);
}

// A basic printer has to fake scan/fax/photocopy
public class BasicPrinter implements MultiFunctionDevice {
    @Override public void print(Document doc)     { /* actual print logic */ }
    @Override public void scan(Document doc)      { throw new UnsupportedOperationException(); }
    @Override public void fax(Document doc)       { throw new UnsupportedOperationException(); }
    @Override public void photocopy(Document doc) { throw new UnsupportedOperationException(); }
}

// GOOD: segregated interfaces
public interface Printable   { void print(Document doc);     }
public interface Scannable   { void scan(Document doc);      }
public interface Faxable     { void fax(Document doc);       }
public interface Photocopiable { void photocopy(Document doc); }

public class BasicPrinter         implements Printable                               { ... }
public class Photocopier          implements Printable, Scannable, Photocopiable     { ... }
public class AllInOnePrinter      implements Printable, Scannable, Faxable, Photocopiable { ... }
```

### Real-Life Example 2 — Media Player

```java
// BAD
public interface MediaPlayer {
    void playAudio();
    void playVideo();
    void displaySubtitles();
    void record();
}

// GOOD
public interface AudioPlayer  { void playAudio();         }
public interface VideoPlayer  { void playVideo();         }
public interface SubtitleAware { void displaySubtitles(); }
public interface Recorder     { void record();            }

public class MP3Player  implements AudioPlayer                            { ... }
public class VideoApp   implements AudioPlayer, VideoPlayer, SubtitleAware { ... }
public class DVR        implements AudioPlayer, VideoPlayer, Recorder      { ... }
```

---

## D — Dependency Inversion Principle (DIP)

### The Simple Analogy First

Think about a **power socket** on the wall. Your laptop, phone charger, and lamp all plug into the same socket. The socket doesn't know or care which device you plug in. Every device just agrees to follow the same standard (the plug shape and voltage).

- The **socket** = high-level module (your business logic)
- The **devices** = low-level modules (database, email service, logger)
- The **plug standard** = the abstraction (the interface)

If the socket was hardwired directly to your laptop, you could never plug in your phone. That's what tightly-coupled code looks like. DIP says: **always go through the plug standard (interface) — never hardwire**.

---

### The Formal Definition (in plain English)

> *"High-level modules should not depend on low-level modules. Both should depend on abstractions. Abstractions should not depend on details. Details should depend on abstractions."*
> — Robert C. Martin

Break it down:

| Term | Meaning |
|---|---|
| **High-level module** | Business logic — the "what" of your app (e.g. `OrderService`, `UserRegistration`) |
| **Low-level module** | Infrastructure details — the "how" (e.g. `MySQLDatabase`, `SmtpEmailSender`) |
| **Abstraction** | An interface or abstract class that defines a contract |
| **"Depend on abstractions"** | Your business logic references interfaces, not concrete classes |

---

### Understanding "Dependency" — What Does It Mean?

A **dependency** is anything your class needs in order to do its job.

```java
public class OrderService {
    // OrderService DEPENDS ON MySQLDatabase to save orders
    private MySQLDatabase database = new MySQLDatabase();
}
```

Here `OrderService` cannot function without `MySQLDatabase`. It has a dependency on it.

The problem: `OrderService` directly **creates** and **names** the concrete class. It is now handcuffed to MySQL. You cannot swap it for MongoDB, or for a fake in-memory database during testing, without editing `OrderService` itself.

---

### The Two Rules of DIP, Side by Side

```
WITHOUT DIP                          WITH DIP
─────────────────────────────────    ─────────────────────────────────
OrderService                         OrderService
    │                                    │
    │ directly creates & uses            │ depends on (interface)
    ▼                                    ▼
MySQLDatabase                        Database  (interface)
                                         ▲            ▲
                                         │            │
                                    MySQLDatabase   MongoDatabase
```

- **Without DIP:** `OrderService` → `MySQLDatabase` (arrow points to a concrete class)
- **With DIP:** `OrderService` → `Database` ← `MySQLDatabase` (both point to the interface)

The dependency arrow is **inverted** — that is literally where the name comes from.

---

### Violation — The Tightly-Coupled Code

```java
// Low-level class — infrastructure detail
public class MySQLDatabase {
    public void save(String data) {
        System.out.println("Saving to MySQL: " + data);
        // imagine real JDBC / Hibernate code here
    }
    public String find(int id) {
        return "MySQL record #" + id;
    }
}

// High-level class — business logic
public class OrderService {

    // Problem 1: hardcoded to a specific class
    private MySQLDatabase database = new MySQLDatabase();
    //                               ^^^^^^^^^^^^^^^^^^^
    //                               OrderService creates its own dependency

    public void placeOrder(String orderData) {
        System.out.println("Validating order...");
        System.out.println("Calculating price...");
        database.save(orderData); // tightly coupled to MySQL
    }

    public String getOrder(int id) {
        return database.find(id);  // still tightly coupled
    }
}
```

**What happens when requirements change?**

```java
// Your manager says: "We are moving from MySQL to MongoDB."
// You now have to open OrderService and change it — a class that
// contains critical business logic — just to swap a storage detail.
// Every time you touch OrderService, you risk breaking the business rules.

// Your tech lead says: "Write unit tests for OrderService."
// You can't, because OrderService instantiates a real MySQLDatabase inside.
// You'd need a live database just to test the business logic. That's painful.
```

---

### The Fix — Step by Step

**Step 1:** Define the abstraction (interface) that captures what the high-level module needs.

```java
// The contract — what OrderService needs from any storage
public interface OrderRepository {
    void save(String orderData);
    String findById(int id);
}
```

**Step 2:** Make the low-level modules implement the abstraction.

```java
// Low-level module 1 — MySQL detail
public class MySQLOrderRepository implements OrderRepository {
    @Override
    public void save(String orderData) {
        System.out.println("[MySQL] Saving order: " + orderData);
    }
    @Override
    public String findById(int id) {
        return "[MySQL] Order #" + id;
    }
}

// Low-level module 2 — MongoDB detail
public class MongoOrderRepository implements OrderRepository {
    @Override
    public void save(String orderData) {
        System.out.println("[MongoDB] Saving order: " + orderData);
    }
    @Override
    public String findById(int id) {
        return "[MongoDB] Order #" + id;
    }
}

// Low-level module 3 — In-memory (useful for tests)
public class InMemoryOrderRepository implements OrderRepository {
    private java.util.Map<Integer, String> store = new java.util.HashMap<>();
    private int nextId = 1;

    @Override
    public void save(String orderData) {
        store.put(nextId++, orderData);
        System.out.println("[InMemory] Order saved");
    }
    @Override
    public String findById(int id) {
        return store.getOrDefault(id, "Not found");
    }
}
```

**Step 3:** High-level module depends on the interface, receives its dependency from outside.

```java
public class OrderService {

    // Depends on the INTERFACE — not on any concrete class
    private OrderRepository repository;

    // Dependency is given FROM OUTSIDE — this is called Dependency Injection
    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public void placeOrder(String orderData) {
        System.out.println("Validating order...");
        System.out.println("Calculating price...");
        repository.save(orderData); // works with ANY implementation
    }

    public String getOrder(int id) {
        return repository.findById(id);
    }
}
```

**Step 4:** Wire everything together at the entry point (main or a config class).

```java
public class Main {
    public static void main(String[] args) {

        // Swap the implementation here — OrderService never changes
        OrderRepository repo = new MySQLOrderRepository();
        // OrderRepository repo = new MongoOrderRepository();   // one-line swap
        // OrderRepository repo = new InMemoryOrderRepository();// for testing

        OrderService service = new OrderService(repo);
        service.placeOrder("Order #1001 - 2 x Laptop");
        System.out.println(service.getOrder(1));
    }
}
```

---

### Dependency Injection — The Mechanism Behind DIP

DIP is the **principle** (what to do). **Dependency Injection (DI)** is the **pattern** (how to do it). DI is how you give a class its dependencies from the outside instead of letting it create them.

There are three styles of injection:

#### Style 1 — Constructor Injection (recommended)

```java
public class OrderService {
    private final OrderRepository repository; // final = immutable after construction

    // Dependency provided at object creation time
    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }
}

// Usage
OrderService service = new OrderService(new MySQLOrderRepository());
```

**Best for:** mandatory dependencies that must exist for the class to work at all.

---

#### Style 2 — Setter Injection

```java
public class OrderService {
    private OrderRepository repository;

    // Dependency can be set (or changed) after construction
    public void setRepository(OrderRepository repository) {
        this.repository = repository;
    }

    public void placeOrder(String data) {
        if (repository == null) throw new IllegalStateException("Repository not set!");
        repository.save(data);
    }
}

// Usage
OrderService service = new OrderService();
service.setRepository(new MongoOrderRepository());
```

**Best for:** optional dependencies, or when you need to swap implementations at runtime.

---

#### Style 3 — Interface Injection (less common)

```java
public interface RepositoryAware {
    void setRepository(OrderRepository repository);
}

public class OrderService implements RepositoryAware {
    private OrderRepository repository;

    @Override
    public void setRepository(OrderRepository repository) {
        this.repository = repository;
    }
}
```

**Best for:** frameworks that manage injection automatically (like Spring).

---

### Why Testing Becomes Easy with DIP

One of the biggest practical benefits of DIP is **testability**.

```java
// Without DIP — you MUST have a real database to test OrderService
// That means: DB server running, tables created, data seeded = slow and fragile

// With DIP — inject a fake in tests, no real DB needed
public class OrderServiceTest {

    @Test
    public void testPlaceOrder_savesOrderToRepository() {

        // Arrange: create a fake (in-memory) repository
        InMemoryOrderRepository fakeRepo = new InMemoryOrderRepository();
        OrderService service = new OrderService(fakeRepo);

        // Act
        service.placeOrder("Order #42");

        // Assert: check the fake repo directly
        String saved = fakeRepo.findById(1);
        assert saved.equals("Order #42");

        System.out.println("Test passed!");
    }
}
```

No database. No network. No setup. The test runs in milliseconds.

---

### Real-Life Example 1 — Logging Service

Without DIP, your services are hardwired to print to console or write to a specific log file. With DIP, you can swap to a cloud logging service in production without touching business logic.

```java
// Abstraction
public interface Logger {
    void info(String message);
    void error(String message, Exception e);
}

// Implementation 1 — development
public class ConsoleLogger implements Logger {
    @Override public void info(String message)  {
        System.out.println("[INFO]  " + message);
    }
    @Override public void error(String message, Exception e) {
        System.out.println("[ERROR] " + message + " — " + e.getMessage());
    }
}

// Implementation 2 — write to a file
public class FileLogger implements Logger {
    private String logFilePath;
    public FileLogger(String logFilePath) { this.logFilePath = logFilePath; }

    @Override public void info(String message)  { /* append to file */ }
    @Override public void error(String message, Exception e) { /* append to file */ }
}

// Implementation 3 — cloud (e.g. Datadog, Splunk)
public class CloudLogger implements Logger {
    @Override public void info(String message)  { /* send to cloud API */ }
    @Override public void error(String message, Exception e) { /* send to cloud API */ }
}

// High-level business service — doesn't know or care which logger it gets
public class PaymentService {
    private Logger logger;

    public PaymentService(Logger logger) {
        this.logger = logger;
    }

    public void processPayment(String userId, double amount) {
        logger.info("Processing payment for user " + userId + ", amount: $" + amount);
        try {
            // ... payment gateway call
            logger.info("Payment successful for user " + userId);
        } catch (Exception e) {
            logger.error("Payment failed for user " + userId, e);
        }
    }
}

// Wiring
public class AppConfig {
    public static void main(String[] args) {
        // In dev: log to console
        Logger logger = new ConsoleLogger();

        // In production: log to cloud — just change this one line
        // Logger logger = new CloudLogger();

        PaymentService paymentService = new PaymentService(logger);
        paymentService.processPayment("user_007", 149.99);
    }
}
```

---

### Real-Life Example 2 — Notification System

Your app sends order confirmations. Today it's email. Tomorrow the product team adds SMS. Next quarter, push notifications.

```java
// Abstraction
public interface NotificationSender {
    void send(String recipient, String subject, String body);
}

// Implementation 1 — Email via SMTP
public class EmailNotificationSender implements NotificationSender {
    @Override
    public void send(String recipient, String subject, String body) {
        System.out.println("[EMAIL] To: " + recipient + " | Subject: " + subject);
        // ... SMTP logic
    }
}

// Implementation 2 — SMS via Twilio
public class SmsNotificationSender implements NotificationSender {
    @Override
    public void send(String recipient, String subject, String body) {
        System.out.println("[SMS] To: " + recipient + " | Message: " + body);
        // ... Twilio API call
    }
}

// Implementation 3 — Push notification
public class PushNotificationSender implements NotificationSender {
    @Override
    public void send(String recipient, String subject, String body) {
        System.out.println("[PUSH] To: " + recipient + " | Title: " + subject);
        // ... Firebase/APNs call
    }
}

// High-level service — depends on the abstraction
public class OrderConfirmationService {
    private NotificationSender notificationSender;

    public OrderConfirmationService(NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
    }

    public void confirmOrder(String customerContact, String orderId) {
        System.out.println("Processing confirmation for order: " + orderId);
        notificationSender.send(
            customerContact,
            "Order Confirmed: " + orderId,
            "Your order has been placed successfully."
        );
    }
}

// Wiring — the only place that knows which concrete class to use
public class Main {
    public static void main(String[] args) {
        // Swap notification channel without touching OrderConfirmationService
        NotificationSender sender = new EmailNotificationSender();
        // NotificationSender sender = new SmsNotificationSender();
        // NotificationSender sender = new PushNotificationSender();

        OrderConfirmationService confirmationService =
            new OrderConfirmationService(sender);

        confirmationService.confirmOrder("alice@example.com", "ORD-9911");
    }
}
```

---

### Real-Life Example 3 — This Project's `src/ocp/`

Your own project already applies DIP in `src/ocp/`:

```java
// The abstraction — both high and low level depend on this
public interface DBPersistent {
    void save(Product product);
}

// Low-level detail 1
public class SQLDb implements DBPersistent {
    @Override
    public void save(Product product) {
        System.out.println("Saving to SQL DB: " + product.getName());
    }
}

// Low-level detail 2
public class MongoDb implements DBPersistent {
    @Override
    public void save(Product product) {
        System.out.println("Saving to MongoDB: " + product.getName());
    }
}

// High-level Cart class — depends on DBPersistent interface, not on SQL or Mongo
public class Cart {
    private DBPersistent db;  // <-- interface, not a concrete class

    public Cart(DBPersistent db) {
        this.db = db;         // injected from outside
    }

    public void saveProduct(Product product) {
        db.save(product);     // works with any DB
    }
}

// Wiring
Cart cart = new Cart(new SQLDb());    // use SQL
Cart cart = new Cart(new MongoDb());  // use MongoDB — Cart.java untouched
```

---

### Common Mistake — DIP Does NOT Mean "Always Use Interfaces Everywhere"

DIP is about **the direction of dependencies**, not about wrapping everything in interfaces for the sake of it.

```java
// Overkill — this String utility has no business being behind an interface
public interface StringHelper {
    String toUpperCase(String s);
}
public class StringHelperImpl implements StringHelper { ... }

// Just use the utility directly — there is no meaningful variation here
public class ReportService {
    public String formatTitle(String title) {
        return title.toUpperCase(); // direct use is perfectly fine
    }
}
```

Apply DIP where there is genuine variability:
- Different environments (dev vs prod vs test)
- Multiple implementations (SQL vs NoSQL, Email vs SMS)
- External services that can be swapped or mocked

---

### How to Detect a DIP Violation — Checklist

| Question | If YES → likely DIP violation |
|---|---|
| Does the class use `new ConcreteClass()` inside for a collaborator? | Yes |
| Is a low-level class name (e.g. `MySQLDatabase`) mentioned inside a business class? | Yes |
| Is it impossible to test the class without a real database / email server / network? | Yes |
| Does changing the storage or notification technology require editing business logic? | Yes |
| Does the class import infrastructure packages (`java.sql`, `javax.mail`) in a service layer? | Yes |

---

### Before and After — Mental Model

```
BEFORE DIP (tightly coupled)

OrderService ──creates──▶ MySQLDatabase
     │                         │
     └── if you change DB ─────┘
         you must change OrderService too


AFTER DIP (loosely coupled)

OrderService ──depends on──▶ OrderRepository (interface)
                                    ▲             ▲
                                    │             │
                             MySQLOrderRepo   MongoOrderRepo

OrderService never changes when you swap the database.
The concrete classes change — but that's their job.
```

---

### Summary of DIP in One Paragraph

Your business logic should never hardwire itself to a specific tool, database, or service. Instead, it should declare what it needs through an interface, and let the caller (or a configuration class) provide the real implementation. This means you can swap implementations, test in isolation, and extend the system without ever touching the business rules — which is exactly the goal of good software design.

> **One-sentence rule:**
> Write your business classes to talk to interfaces. Let someone else decide which real class backs that interface.

---

## How the Principles Work Together — Complete Case Study

**Scenario:** An online bookstore.

```
BookStore/
├── model/
│   └── Book.java              ← SRP: only holds data
├── repository/
│   ├── BookRepository.java    ← abstraction (DIP + OCP)
│   ├── SqlBookRepository.java ← concrete impl
│   └── MongoBookRepository.java
├── service/
│   └── BookService.java       ← high-level logic (DIP: depends on interface)
├── notification/
│   ├── Notifiable.java        ← ISP: small focused interface
│   ├── EmailNotifier.java
│   └── SMSNotifier.java
└── discount/
    ├── DiscountStrategy.java  ← OCP: add new strategies without modifying BookService
    ├── SeasonalDiscount.java
    └── MemberDiscount.java
```

```java
// SRP: Book only holds data
public class Book {
    private String title, author, isbn;
    private double price;
    // getters only
}

// OCP + DIP: repository is an abstraction
public interface BookRepository {
    void save(Book book);
    Book findByIsbn(String isbn);
}

// OCP: extend by adding new repo, not modifying existing service
public class SqlBookRepository  implements BookRepository { ... }
public class MongoBookRepository implements BookRepository { ... }

// ISP: notification is a focused interface
public interface Notifiable {
    void notify(String recipient, String message);
}
public class EmailNotifier implements Notifiable { ... }
public class SMSNotifier   implements Notifiable { ... }

// OCP: add discount types without modifying BookService
public interface DiscountStrategy {
    double apply(double price);
}
public class SeasonalDiscount implements DiscountStrategy { ... }
public class MemberDiscount   implements DiscountStrategy { ... }

// DIP: BookService depends on abstractions, not concrete classes
public class BookService {
    private BookRepository    repo;
    private Notifiable        notifier;
    private DiscountStrategy  discount;

    public BookService(BookRepository repo,
                       Notifiable notifier,
                       DiscountStrategy discount) {
        this.repo     = repo;
        this.notifier = notifier;
        this.discount = discount;
    }

    public void purchaseBook(String isbn, String buyerEmail) {
        Book book = repo.findByIsbn(isbn);
        double finalPrice = discount.apply(book.getPrice());
        // process payment...
        notifier.notify(buyerEmail, "You bought " + book.getTitle());
    }
}
```

Every SOLID principle is at work:
- **SRP** — `Book`, `BookService`, `BookRepository`, `Notifiable` each have one job.
- **OCP** — add new DBs, notifiers, or discounts without touching `BookService`.
- **LSP** — `SqlBookRepository` and `MongoBookRepository` are interchangeable.
- **ISP** — `Notifiable` is small; nothing is forced to implement irrelevant methods.
- **DIP** — `BookService` depends on interfaces, not on `SqlBookRepository` or `EmailNotifier`.

---

## Quick Reference — When to Apply Each Principle

| Smell / Trigger | Principle to apply |
|---|---|
| A class handles DB, emailing, and UI all at once | SRP |
| You keep editing an existing class to add a new type | OCP |
| A subclass throws `UnsupportedOperationException` | LSP |
| A class implements an interface but leaves many methods empty | ISP |
| A class uses `new ConcreteClass()` inside for its dependencies | DIP |
| Tests are hard to write because the class creates its own collaborators | DIP |
| Adding a new feature breaks unrelated existing tests | SRP / OCP |

---

## Summary

```
S — One class, one job.
O — Add new behaviour by writing new code, not editing old code.
L — Subclasses must keep all promises made by the parent.
I — Keep interfaces small and focused; don't force unused methods.
D — Depend on interfaces, inject concrete objects from outside.
```

These principles are guidelines, not laws. Apply them where they reduce complexity and improve maintainability. Over-engineering a tiny script with five layers of abstraction is worse than a practical, readable solution that bends a rule or two.
