# Factory Pattern

## What is the Factory Pattern?

The **Factory Pattern** is a creational design pattern that provides an interface for creating objects without specifying their exact concrete classes. Instead of calling `new` directly, you delegate object creation to a factory method or class.

It answers the question: *"Who is responsible for creating objects?"*

---

## Why Use the Factory Pattern?

- **Decouples** object creation from usage
- **Centralizes** creation logic — easy to modify in one place
- **Supports Open/Closed Principle** — add new types without changing existing code
- **Hides complexity** of instantiation from the caller
- **Easier testing** — factories can be swapped with test doubles

---

## Types of Factory Patterns

| Type | Description |
|------|-------------|
| **Simple Factory** | A single class with a static method that creates objects |
| **Factory Method** | Subclasses decide which class to instantiate |
| **Abstract Factory** | Creates families of related objects |

---

## 1. Simple Factory

A utility class with a static method that returns different concrete types based on input. The caller only knows the interface — not which class was created.

**Limitation:** Every time a new type is added, the factory class must be modified — violates the Open/Closed Principle.

---

### Example 1: Shape Factory

```java
// Product interface
interface Shape {
    double area();
    double perimeter();
}

// Concrete Products
class Circle implements Shape {
    private double radius;
    Circle(double radius) { this.radius = radius; }

    public double area()      { return Math.PI * radius * radius; }
    public double perimeter() { return 2 * Math.PI * radius; }
}

class Rectangle implements Shape {
    private double width, height;
    Rectangle(double width, double height) { this.width = width; this.height = height; }

    public double area()      { return width * height; }
    public double perimeter() { return 2 * (width + height); }
}

class Triangle implements Shape {
    private double a, b, c;
    Triangle(double a, double b, double c) { this.a = a; this.b = b; this.c = c; }

    public double area() {
        double s = (a + b + c) / 2;
        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }
    public double perimeter() { return a + b + c; }
}

class Square implements Shape {
    private double side;
    Square(double side) { this.side = side; }

    public double area()      { return side * side; }
    public double perimeter() { return 4 * side; }
}

// Simple Factory
class ShapeFactory {
    public static Shape create(String type, double... args) {
        switch (type.toLowerCase()) {
            case "circle":    return new Circle(args[0]);
            case "rectangle": return new Rectangle(args[0], args[1]);
            case "triangle":  return new Triangle(args[0], args[1], args[2]);
            case "square":    return new Square(args[0]);
            default: throw new IllegalArgumentException("Unknown shape: " + type);
        }
    }
}

// Usage
public class Main {
    public static void main(String[] args) {
        Shape circle = ShapeFactory.create("circle", 5);
        Shape rect   = ShapeFactory.create("rectangle", 4, 6);
        Shape square = ShapeFactory.create("square", 3);

        System.out.printf("Circle area:    %.2f%n", circle.area());    // 78.54
        System.out.printf("Rectangle area: %.2f%n", rect.area());      // 24.00
        System.out.printf("Square area:    %.2f%n", square.area());    // 9.00
    }
}
```

---

### Example 2: Vehicle Factory

```java
// Product interface
interface Vehicle {
    void start();
    void stop();
    int getPassengerCapacity();
}

// Concrete Products
class Car implements Vehicle {
    public void start()               { System.out.println("Car engine started — vroom!"); }
    public void stop()                { System.out.println("Car engine stopped."); }
    public int getPassengerCapacity() { return 5; }
}

class Bike implements Vehicle {
    public void start()               { System.out.println("Bike engine started — braaap!"); }
    public void stop()                { System.out.println("Bike engine stopped."); }
    public int getPassengerCapacity() { return 2; }
}

class Truck implements Vehicle {
    public void start()               { System.out.println("Truck engine started — rumble!"); }
    public void stop()                { System.out.println("Truck engine stopped."); }
    public int getPassengerCapacity() { return 3; }
}

class Bus implements Vehicle {
    public void start()               { System.out.println("Bus engine started — whoosh!"); }
    public void stop()                { System.out.println("Bus engine stopped."); }
    public int getPassengerCapacity() { return 50; }
}

// Simple Factory
class VehicleFactory {
    public static Vehicle create(String type) {
        switch (type.toLowerCase()) {
            case "car":   return new Car();
            case "bike":  return new Bike();
            case "truck": return new Truck();
            case "bus":   return new Bus();
            default: throw new IllegalArgumentException("Unknown vehicle: " + type);
        }
    }
}

// Usage
public class Main {
    public static void main(String[] args) {
        String[] types = {"car", "bike", "truck", "bus"};
        for (String type : types) {
            Vehicle v = VehicleFactory.create(type);
            v.start();
            System.out.println("Capacity: " + v.getPassengerCapacity());
            v.stop();
            System.out.println();
        }
    }
}
```

**Output:**
```
Car engine started — vroom!
Capacity: 5
Car engine stopped.

Bike engine started — braaap!
Capacity: 2
...
```

---

### Example 3: Payment Method Factory

```java
// Product interface
interface PaymentMethod {
    boolean processPayment(double amount);
    String getMethodName();
}

// Concrete Products
class CreditCard implements PaymentMethod {
    private String cardNumber;
    CreditCard(String cardNumber) { this.cardNumber = cardNumber; }

    public boolean processPayment(double amount) {
        System.out.printf("[CREDIT CARD] Charged $%.2f to card ending %s%n",
            amount, cardNumber.substring(cardNumber.length() - 4));
        return true;
    }
    public String getMethodName() { return "Credit Card"; }
}

class DebitCard implements PaymentMethod {
    private String cardNumber;
    DebitCard(String cardNumber) { this.cardNumber = cardNumber; }

    public boolean processPayment(double amount) {
        System.out.printf("[DEBIT CARD] Debited $%.2f from card ending %s%n",
            amount, cardNumber.substring(cardNumber.length() - 4));
        return true;
    }
    public String getMethodName() { return "Debit Card"; }
}

class PayPal implements PaymentMethod {
    private String email;
    PayPal(String email) { this.email = email; }

    public boolean processPayment(double amount) {
        System.out.printf("[PAYPAL] Sent $%.2f via PayPal account: %s%n", amount, email);
        return true;
    }
    public String getMethodName() { return "PayPal"; }
}

class UPI implements PaymentMethod {
    private String upiId;
    UPI(String upiId) { this.upiId = upiId; }

    public boolean processPayment(double amount) {
        System.out.printf("[UPI] Transferred $%.2f to UPI ID: %s%n", amount, upiId);
        return true;
    }
    public String getMethodName() { return "UPI"; }
}

// Simple Factory
class PaymentFactory {
    public static PaymentMethod create(String type, String identifier) {
        switch (type.toLowerCase()) {
            case "credit": return new CreditCard(identifier);
            case "debit":  return new DebitCard(identifier);
            case "paypal": return new PayPal(identifier);
            case "upi":    return new UPI(identifier);
            default: throw new IllegalArgumentException("Unknown payment method: " + type);
        }
    }
}

// Usage
public class Main {
    public static void main(String[] args) {
        PaymentMethod pm1 = PaymentFactory.create("credit", "1234567890123456");
        PaymentMethod pm2 = PaymentFactory.create("paypal", "user@example.com");
        PaymentMethod pm3 = PaymentFactory.create("upi",    "user@okbank");

        pm1.processPayment(99.99);
        pm2.processPayment(49.50);
        pm3.processPayment(150.00);
    }
}
```

---

### Example 4: Animal Factory

```java
// Product interface
interface Animal {
    void speak();
    void move();
    String getName();
}

// Concrete Products
class Dog implements Animal {
    public void speak()      { System.out.println("Dog: Woof! Woof!"); }
    public void move()       { System.out.println("Dog: Running on four legs."); }
    public String getName()  { return "Dog"; }
}

class Cat implements Animal {
    public void speak()      { System.out.println("Cat: Meow!"); }
    public void move()       { System.out.println("Cat: Stealthily padding around."); }
    public String getName()  { return "Cat"; }
}

class Bird implements Animal {
    public void speak()      { System.out.println("Bird: Tweet! Tweet!"); }
    public void move()       { System.out.println("Bird: Flapping wings and flying."); }
    public String getName()  { return "Bird"; }
}

class Fish implements Animal {
    public void speak()      { System.out.println("Fish: ..."); }
    public void move()       { System.out.println("Fish: Swimming silently."); }
    public String getName()  { return "Fish"; }
}

// Simple Factory
class AnimalFactory {
    public static Animal create(String type) {
        switch (type.toLowerCase()) {
            case "dog":  return new Dog();
            case "cat":  return new Cat();
            case "bird": return new Bird();
            case "fish": return new Fish();
            default: throw new IllegalArgumentException("Unknown animal: " + type);
        }
    }
}

// Usage
public class Main {
    public static void main(String[] args) {
        String[] animals = {"dog", "cat", "bird", "fish"};
        for (String type : animals) {
            Animal a = AnimalFactory.create(type);
            System.out.println("=== " + a.getName() + " ===");
            a.speak();
            a.move();
        }
    }
}
```

---

## 2. Factory Method Pattern

### What Problem Does It Solve?

Imagine you are writing code that sends notifications. You start simple:

```java
// You write this first
class OrderService {
    public void placeOrder() {
        // Send a notification when order is placed
        EmailSender email = new EmailSender();  // directly creating the object
        email.send("Order placed!");
    }
}
```

This works — but now the product team says: *"We also need SMS notifications. And next month, push notifications."*

Your `OrderService` is now forced to know about `EmailSender`, `SMSSender`, `PushSender` — every time a new channel is added, you open `OrderService` and change it. That is the problem.

**The Factory Method Pattern solves this by saying:**
> "Don't let `OrderService` decide *what* to create. Instead, give that responsibility to a subclass."

---

### The Core Idea (Plain English)

You have a **parent class** that does the main work (e.g., "send a notification"). But it has **one step it deliberately leaves blank** — the step where an object is created. That blank step is the **Factory Method**.

Each **child class** fills in that blank differently:
- `EmailService` fills it in by creating an `EmailNotification`
- `SMSService` fills it in by creating an `SMSNotification`
- etc.

The parent class never has to change. You just add new child classes.

---

### Visual Diagram

```
                    ┌─────────────────────────────────┐
                    │   NotificationService (abstract) │
                    │─────────────────────────────────│
                    │  + notify(message)               │  ← does the main work
                    │  # createNotification()  ???     │  ← Factory Method (blank!)
                    └─────────────────────────────────┘
                                    ▲
               ┌────────────────────┼────────────────────┐
               │                    │                    │
  ┌────────────────┐   ┌────────────────┐   ┌────────────────┐
  │  EmailService  │   │   SMSService   │   │  PushService   │
  │────────────────│   │────────────────│   │────────────────│
  │ createNotif()  │   │ createNotif()  │   │ createNotif()  │
  │ → Email object │   │ → SMS object   │   │ → Push object  │
  └────────────────┘   └────────────────┘   └────────────────┘
```

---

### The 4 Moving Parts (Always the Same)

Every Factory Method example has exactly these 4 parts:

| Part | What it is | Example |
|------|-----------|---------|
| **Product interface** | What all created objects must be able to do | `Notification` with `send()` |
| **Concrete Products** | The actual objects being created | `EmailNotification`, `SMSNotification` |
| **Creator (abstract)** | Parent class with the factory method left blank | `NotificationService` |
| **Concrete Creators** | Child classes that fill in the blank | `EmailService`, `SMSService` |

---

### Example 1: Notification System

This is the simplest example to understand the pattern. Read the comments carefully — they explain *why* each piece exists.

```java
// ─────────────────────────────────────────────
// PART 1: Product Interface
// This defines what every notification type MUST be able to do.
// The parent class (NotificationService) only ever talks to this
// interface — it never knows if it's Email, SMS, or Push.
// ─────────────────────────────────────────────
interface Notification {
    void send(String message);
}


// ─────────────────────────────────────────────
// PART 2: Concrete Products
// These are the actual objects that do the real work.
// Each one implements Notification differently.
// ─────────────────────────────────────────────
class EmailNotification implements Notification {
    private String emailAddress;

    EmailNotification(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void send(String message) {
        System.out.println("[EMAIL] Sent to: " + emailAddress);
        System.out.println("        Message: " + message);
    }
}

class SMSNotification implements Notification {
    private String phoneNumber;

    SMSNotification(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void send(String message) {
        System.out.println("[SMS] Sent to: " + phoneNumber);
        System.out.println("      Message: " + message);
    }
}

class PushNotification implements Notification {
    private String deviceToken;

    PushNotification(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public void send(String message) {
        System.out.println("[PUSH] Device: " + deviceToken);
        System.out.println("       Message: " + message);
    }
}

class SlackNotification implements Notification {
    private String channel;

    SlackNotification(String channel) {
        this.channel = channel;
    }

    public void send(String message) {
        System.out.println("[SLACK] Channel: #" + channel);
        System.out.println("        Message: " + message);
    }
}


// ─────────────────────────────────────────────
// PART 3: Creator (abstract parent class)
// This class does the main work in notify().
// But notice: it calls createNotification() which it does NOT implement.
// That blank is intentionally left for child classes to fill in.
// ─────────────────────────────────────────────
abstract class NotificationService {

    // THE FACTORY METHOD — the whole pattern lives here.
    // It is abstract, meaning: "I don't know what to create.
    // My child class will decide."
    protected abstract Notification createNotification();

    // This method does the actual work.
    // It calls createNotification() without caring what comes back —
    // as long as it's a Notification, it can call send() on it.
    public void notify(String message) {
        Notification notification = createNotification(); // child decides what this is
        notification.send(message);                       // parent uses it
    }
}


// ─────────────────────────────────────────────
// PART 4: Concrete Creators (child classes)
// Each child class fills in the blank by overriding createNotification().
// Nothing else changes — just that one method.
// ─────────────────────────────────────────────
class EmailService extends NotificationService {
    private String email;

    EmailService(String email) { this.email = email; }

    // Filling in the blank: "create an EmailNotification"
    @Override
    protected Notification createNotification() {
        return new EmailNotification(email);
    }
}

class SMSService extends NotificationService {
    private String phone;

    SMSService(String phone) { this.phone = phone; }

    // Filling in the blank: "create an SMSNotification"
    @Override
    protected Notification createNotification() {
        return new SMSNotification(phone);
    }
}

class PushService extends NotificationService {
    private String token;

    PushService(String token) { this.token = token; }

    @Override
    protected Notification createNotification() {
        return new PushNotification(token);
    }
}

class SlackService extends NotificationService {
    private String channel;

    SlackService(String channel) { this.channel = channel; }

    @Override
    protected Notification createNotification() {
        return new SlackNotification(channel);
    }
}


// ─────────────────────────────────────────────
// USAGE
// Notice: sendOrderAlert() accepts NotificationService — not EmailService.
// It doesn't know or care which channel is being used.
// ─────────────────────────────────────────────
public class Main {

    // This method works for ALL notification types — present and future.
    // You never need to change this method to add a new channel.
    static void sendOrderAlert(NotificationService service) {
        service.notify("Your order #1042 has been shipped!");
    }

    public static void main(String[] args) {
        sendOrderAlert(new EmailService("alice@example.com"));
        System.out.println();
        sendOrderAlert(new SMSService("+1-555-0100"));
        System.out.println();
        sendOrderAlert(new PushService("device-token-xyz"));
        System.out.println();
        sendOrderAlert(new SlackService("order-updates"));
    }
}
```

**Output:**
```
[EMAIL] Sent to: alice@example.com
        Message: Your order #1042 has been shipped!

[SMS] Sent to: +1-555-0100
      Message: Your order #1042 has been shipped!

[PUSH] Device: device-token-xyz
       Message: Your order #1042 has been shipped!

[SLACK] Channel: #order-updates
        Message: Your order #1042 has been shipped!
```

**Adding a new channel tomorrow (e.g., WhatsApp):**
```java
// Step 1: Create the product
class WhatsAppNotification implements Notification {
    private String number;
    WhatsAppNotification(String number) { this.number = number; }
    public void send(String message) {
        System.out.println("[WHATSAPP] To: " + number + " | " + message);
    }
}

// Step 2: Create the creator
class WhatsAppService extends NotificationService {
    private String number;
    WhatsAppService(String number) { this.number = number; }
    protected Notification createNotification() {
        return new WhatsAppNotification(number);
    }
}

// Step 3: Use it — sendOrderAlert() already handles it, no changes needed!
sendOrderAlert(new WhatsAppService("+91-9999999999"));
```

Zero changes to existing code. That is the power of Factory Method.

---

### Example 2: Document Parser

Your app needs to read different file types (PDF, Word, Excel, CSV). Each file type is parsed differently, but the steps around parsing (open file → parse → show results) are always the same.

```java
import java.util.List;
import java.util.Arrays;

// PART 1: Product Interface
// Every parser must be able to do these two things.
interface DocumentParser {
    List<String> parse(String filePath);  // read the file and return its content
    String getFormat();                   // just for display purposes
}

// PART 2: Concrete Products — each knows how to parse its own format
class PDFParser implements DocumentParser {
    public List<String> parse(String filePath) {
        System.out.println("  Reading PDF bytes, decoding font tables...");
        return Arrays.asList("Introduction", "Chapter 1: Getting Started", "Conclusion");
    }
    public String getFormat() { return "PDF"; }
}

class WordParser implements DocumentParser {
    public List<String> parse(String filePath) {
        System.out.println("  Unzipping .docx, reading XML nodes...");
        return Arrays.asList("Paragraph 1: Hello World", "Paragraph 2: Details here");
    }
    public String getFormat() { return "Word (DOCX)"; }
}

class ExcelParser implements DocumentParser {
    public List<String> parse(String filePath) {
        System.out.println("  Reading spreadsheet cells row by row...");
        return Arrays.asList("Row 1: Name, Age, City", "Row 2: Alice, 30, Delhi");
    }
    public String getFormat() { return "Excel (XLSX)"; }
}

class CSVParser implements DocumentParser {
    public List<String> parse(String filePath) {
        System.out.println("  Splitting lines by comma delimiter...");
        return Arrays.asList("name,age,city", "Alice,30,Delhi", "Bob,25,Mumbai");
    }
    public String getFormat() { return "CSV"; }
}


// PART 3: Creator (abstract)
// process() always does the same steps: create parser → parse → display.
// The only thing that changes is WHICH parser is created — that is the factory method.
abstract class DocumentProcessor {

    // Factory Method — left blank for child classes
    protected abstract DocumentParser createParser();

    // Main logic — same for every file type
    public void process(String filePath) {
        System.out.println("Opening file: " + filePath);

        DocumentParser parser = createParser();  // child decides which parser
        System.out.println("Using parser: " + parser.getFormat());

        List<String> content = parser.parse(filePath);
        System.out.println("Found " + content.size() + " sections:");
        for (String section : content) {
            System.out.println("  → " + section);
        }
        System.out.println();
    }
}


// PART 4: Concrete Creators — each creates its own parser
class PDFProcessor extends DocumentProcessor {
    @Override
    protected DocumentParser createParser() { return new PDFParser(); }
}

class WordProcessor extends DocumentProcessor {
    @Override
    protected DocumentParser createParser() { return new WordParser(); }
}

class ExcelProcessor extends DocumentProcessor {
    @Override
    protected DocumentParser createParser() { return new ExcelParser(); }
}

class CSVProcessor extends DocumentProcessor {
    @Override
    protected DocumentParser createParser() { return new CSVParser(); }
}


// Usage
public class Main {
    public static void main(String[] args) {
        // Map each file to the right processor
        DocumentProcessor[] processors = {
            new PDFProcessor(),
            new WordProcessor(),
            new ExcelProcessor(),
            new CSVProcessor()
        };
        String[] files = { "report.pdf", "contract.docx", "sales.xlsx", "users.csv" };

        for (int i = 0; i < processors.length; i++) {
            processors[i].process(files[i]);
        }
    }
}
```

**Output:**
```
Opening file: report.pdf
Using parser: PDF
  Reading PDF bytes, decoding font tables...
Found 3 sections:
  → Introduction
  → Chapter 1: Getting Started
  → Conclusion

Opening file: contract.docx
Using parser: Word (DOCX)
  Unzipping .docx, reading XML nodes...
Found 2 sections:
  ...
```

---

### Example 3: Logger

Your app needs to log messages. In development you print to the console. In production you write to a file. In some services you store logs in a database or ship them to a cloud service.

The logging *logic* (info/warn/error) is always identical — only the *destination* changes. Factory Method handles this cleanly.

```java
// PART 1: Product Interface
interface Logger {
    void writeLog(String level, String message);
}

// PART 2: Concrete Products — each writes to a different destination
class ConsoleLogger implements Logger {
    public void writeLog(String level, String message) {
        // Prints directly to the terminal
        System.out.println("[" + level + "] " + message);
    }
}

class FileLogger implements Logger {
    private String filePath;

    FileLogger(String filePath) { this.filePath = filePath; }

    public void writeLog(String level, String message) {
        // In real code: use FileWriter. Here we simulate it.
        System.out.println("[FILE → " + filePath + "] [" + level + "] " + message);
    }
}

class DatabaseLogger implements Logger {
    private String tableName;

    DatabaseLogger(String tableName) { this.tableName = tableName; }

    public void writeLog(String level, String message) {
        // In real code: run an INSERT query
        System.out.println("[DB → " + tableName + "] INSERT: level=" + level + ", msg='" + message + "'");
    }
}

class CloudLogger implements Logger {
    private String serviceUrl;

    CloudLogger(String serviceUrl) { this.serviceUrl = serviceUrl; }

    public void writeLog(String level, String message) {
        // In real code: HTTP POST to the logging service
        System.out.println("[CLOUD → " + serviceUrl + "] " + level + ": " + message);
    }
}


// PART 3: Creator (abstract)
// The methods info(), warn(), error() are always the same.
// The factory method createLogger() decides where the logs go.
abstract class AppLogger {

    // Factory Method
    protected abstract Logger createLogger();

    // These methods are the same no matter which destination is used
    public void info(String message)  { createLogger().writeLog("INFO",    message); }
    public void warn(String message)  { createLogger().writeLog("WARNING", message); }
    public void error(String message) { createLogger().writeLog("ERROR",   message); }
}


// PART 4: Concrete Creators
class ConsoleAppLogger extends AppLogger {
    @Override
    protected Logger createLogger() { return new ConsoleLogger(); }
}

class FileAppLogger extends AppLogger {
    private String path;
    FileAppLogger(String path) { this.path = path; }

    @Override
    protected Logger createLogger() { return new FileLogger(path); }
}

class DatabaseAppLogger extends AppLogger {
    private String table;
    DatabaseAppLogger(String table) { this.table = table; }

    @Override
    protected Logger createLogger() { return new DatabaseLogger(table); }
}

class CloudAppLogger extends AppLogger {
    private String url;
    CloudAppLogger(String url) { this.url = url; }

    @Override
    protected Logger createLogger() { return new CloudLogger(url); }
}


// Usage
public class Main {
    public static void main(String[] args) {

        // In development: log to console
        AppLogger devLogger = new ConsoleAppLogger();
        devLogger.info("Server started on port 8080");
        devLogger.warn("Memory usage above 70%");
        devLogger.error("Null pointer in UserService");
        System.out.println();

        // In production: log to a file
        AppLogger prodLogger = new FileAppLogger("/var/log/app.log");
        prodLogger.info("Server started on port 8080");
        prodLogger.warn("Memory usage above 70%");
        System.out.println();

        // In enterprise: log to database
        AppLogger dbLogger = new DatabaseAppLogger("system_logs");
        dbLogger.error("Payment gateway timeout");
        System.out.println();

        // In cloud: ship logs to external service
        AppLogger cloudLogger = new CloudAppLogger("https://logs.myservice.io");
        cloudLogger.info("Deployment successful");
    }
}
```

**Output:**
```
[INFO] Server started on port 8080
[WARNING] Memory usage above 70%
[ERROR] Null pointer in UserService

[FILE → /var/log/app.log] [INFO] Server started on port 8080
[FILE → /var/log/app.log] [WARNING] Memory usage above 70%

[DB → system_logs] INSERT: level=ERROR, msg='Payment gateway timeout'

[CLOUD → https://logs.myservice.io] INFO: Deployment successful
```

> **Key insight:** To switch from console logging to file logging, you only change one word in main: `new ConsoleAppLogger()` → `new FileAppLogger(...)`. All log calls remain identical.

---

### Example 4: Transport / Delivery

A logistics app ships orders. The shipping logic (calculate cost, print receipt) is the same — only the transport mode differs.

```java
// PART 1: Product Interface
interface Transport {
    void deliver(String origin, String destination);
    double costPerKm();        // cost per km (per kg)
    String getModeName();
}

// PART 2: Concrete Products
class TruckTransport implements Transport {
    public void deliver(String origin, String destination) {
        System.out.println("Truck driving from " + origin + " → " + destination);
    }
    public double costPerKm() { return 0.05; }  // cheapest on short distances
    public String getModeName() { return "Road (Truck)"; }
}

class TrainTransport implements Transport {
    public void deliver(String origin, String destination) {
        System.out.println("Train departing " + origin + " → " + destination);
    }
    public double costPerKm() { return 0.03; }  // economical for long distances
    public String getModeName() { return "Rail (Train)"; }
}

class AirTransport implements Transport {
    public void deliver(String origin, String destination) {
        System.out.println("Flight taking off from " + origin + " → " + destination);
    }
    public double costPerKm() { return 0.20; }  // fastest but expensive
    public String getModeName() { return "Air (Plane)"; }
}

class ShipTransport implements Transport {
    public void deliver(String origin, String destination) {
        System.out.println("Cargo ship sailing from " + origin + " → " + destination);
    }
    public double costPerKm() { return 0.01; }  // slowest but cheapest for heavy loads
    public String getModeName() { return "Sea (Ship)"; }
}


// PART 3: Creator (abstract)
// shipOrder() always: picks transport → delivers → prints receipt.
// Only createTransport() changes between subclasses.
abstract class LogisticsCompany {

    // Factory Method — child class fills this in
    protected abstract Transport createTransport();

    // The main shipping workflow — identical for every transport mode
    public void shipOrder(String from, String to, double weightKg, double distanceKm) {
        Transport transport = createTransport();    // step 1: get transport (child decides)

        System.out.println("─────────────────────────────────");
        System.out.println("Mode    : " + transport.getModeName());
        transport.deliver(from, to);               // step 2: deliver

        double cost = transport.costPerKm() * weightKg * distanceKm;
        System.out.printf("Weight  : %.0f kg%n", weightKg);
        System.out.printf("Distance: %.0f km%n", distanceKm);
        System.out.printf("Cost    : $%.2f%n", cost);    // step 3: receipt
        System.out.println("─────────────────────────────────\n");
    }
}


// PART 4: Concrete Creators — one per transport mode
class RoadLogistics extends LogisticsCompany {
    @Override
    protected Transport createTransport() { return new TruckTransport(); }
}

class RailLogistics extends LogisticsCompany {
    @Override
    protected Transport createTransport() { return new TrainTransport(); }
}

class AirLogistics extends LogisticsCompany {
    @Override
    protected Transport createTransport() { return new AirTransport(); }
}

class SeaLogistics extends LogisticsCompany {
    @Override
    protected Transport createTransport() { return new ShipTransport(); }
}


// Usage
public class Main {
    public static void main(String[] args) {
        // Same order, four different transport modes
        // Notice: shipOrder() call is IDENTICAL for all — only the object changes
        LogisticsCompany road = new RoadLogistics();
        LogisticsCompany rail = new RailLogistics();
        LogisticsCompany air  = new AirLogistics();
        LogisticsCompany sea  = new SeaLogistics();

        road.shipOrder("Mumbai", "Pune",  200, 150);
        rail.shipOrder("Mumbai", "Delhi", 500, 1400);
        air.shipOrder("Delhi",   "Dubai", 50,  2200);
        sea.shipOrder("Chennai", "Singapore", 5000, 3400);
    }
}
```

**Output:**
```
─────────────────────────────────
Mode    : Road (Truck)
Truck driving from Mumbai → Pune
Weight  : 200 kg
Distance: 150 km
Cost    : $1500.00
─────────────────────────────────

─────────────────────────────────
Mode    : Rail (Train)
Train departing Mumbai → Delhi
Weight  : 500 kg
Distance: 1400 km
Cost    : $21000.00
─────────────────────────────────

─────────────────────────────────
Mode    : Air (Plane)
Flight taking off from Delhi → Dubai
Weight  : 50 kg
Distance: 2200 km
Cost    : $22000.00
─────────────────────────────────

─────────────────────────────────
Mode    : Sea (Ship)
Cargo ship sailing from Chennai → Singapore
Weight  : 5000 kg
Distance: 3400 km
Cost    : $170000.00
─────────────────────────────────
```

---

### Quick Recap

```
BEFORE Factory Method:
  OrderService → knows about EmailSender, SMSSender, PushSender
  Adding a new channel = modify OrderService

AFTER Factory Method:
  OrderService → knows only about NotificationService
  Adding a new channel = create a new subclass, touch nothing else
```

The pattern has **two rules:**
1. The parent class has one method it doesn't implement (`createXxx()` — the factory method)
2. Each child class implements that one method to return a different object

Everything else — the workflow, the business logic, the calling code — stays the same.

---

## 3. Abstract Factory Pattern

Creates **families of related objects** without specifying their concrete classes. All products from one factory are guaranteed to be compatible with each other.

Think of it as: *a factory that produces other factories, each responsible for a consistent product family.*

### Structure

```
AbstractFactory
  └── createProductA() → AbstractProductA
  └── createProductB() → AbstractProductB

ConcreteFactory1 → creates ProductA1 + ProductB1  (Family 1)
ConcreteFactory2 → creates ProductA2 + ProductB2  (Family 2)
```

---

### Example 1: UI Component Factory (Cross-Platform)

```java
// Abstract Products
interface Button   { void render(); void onClick(); }
interface Checkbox { void render(); void toggle(); }
interface TextField { void render(); String getValue(); }

// Windows Family
class WindowsButton implements Button {
    public void render()   { System.out.println("  [Windows Button] rendered"); }
    public void onClick()  { System.out.println("  [Windows Button] clicked — Win32 event fired"); }
}
class WindowsCheckbox implements Checkbox {
    private boolean checked = false;
    public void render()   { System.out.println("  [Windows Checkbox] ☐ rendered"); }
    public void toggle()   { checked = !checked; System.out.println("  [Windows Checkbox] " + (checked ? "☑" : "☐")); }
}
class WindowsTextField implements TextField {
    public void render()          { System.out.println("  [Windows TextField] _____________"); }
    public String getValue()      { return "Windows input"; }
}

// Mac Family
class MacButton implements Button {
    public void render()   { System.out.println("  (Mac Button) rendered"); }
    public void onClick()  { System.out.println("  (Mac Button) clicked — Cocoa event fired"); }
}
class MacCheckbox implements Checkbox {
    private boolean checked = false;
    public void render()   { System.out.println("  (Mac Checkbox) ○ rendered"); }
    public void toggle()   { checked = !checked; System.out.println("  (Mac Checkbox) " + (checked ? "●" : "○")); }
}
class MacTextField implements TextField {
    public void render()          { System.out.println("  (Mac TextField) _____________"); }
    public String getValue()      { return "Mac input"; }
}

// Linux Family
class LinuxButton implements Button {
    public void render()   { System.out.println("  <Linux Button> rendered"); }
    public void onClick()  { System.out.println("  <Linux Button> clicked — GTK signal emitted"); }
}
class LinuxCheckbox implements Checkbox {
    private boolean checked = false;
    public void render()   { System.out.println("  <Linux Checkbox> [ ] rendered"); }
    public void toggle()   { checked = !checked; System.out.println("  <Linux Checkbox> " + (checked ? "[x]" : "[ ]")); }
}
class LinuxTextField implements TextField {
    public void render()          { System.out.println("  <Linux TextField> _____________"); }
    public String getValue()      { return "Linux input"; }
}

// Abstract Factory
interface UIFactory {
    Button    createButton();
    Checkbox  createCheckbox();
    TextField createTextField();
}

// Concrete Factories
class WindowsUIFactory implements UIFactory {
    public Button    createButton()    { return new WindowsButton(); }
    public Checkbox  createCheckbox()  { return new WindowsCheckbox(); }
    public TextField createTextField() { return new WindowsTextField(); }
}

class MacUIFactory implements UIFactory {
    public Button    createButton()    { return new MacButton(); }
    public Checkbox  createCheckbox()  { return new MacCheckbox(); }
    public TextField createTextField() { return new MacTextField(); }
}

class LinuxUIFactory implements UIFactory {
    public Button    createButton()    { return new LinuxButton(); }
    public Checkbox  createCheckbox()  { return new LinuxCheckbox(); }
    public TextField createTextField() { return new LinuxTextField(); }
}

// Client — never mentions Windows/Mac/Linux
class LoginForm {
    private Button    submitBtn;
    private Checkbox  rememberMe;
    private TextField usernameField;

    LoginForm(UIFactory factory) {
        submitBtn     = factory.createButton();
        rememberMe    = factory.createCheckbox();
        usernameField = factory.createTextField();
    }

    void render() {
        System.out.println("=== Login Form ===");
        usernameField.render();
        rememberMe.render();
        submitBtn.render();
        submitBtn.onClick();
        System.out.println();
    }
}

// Usage
public class Main {
    public static void main(String[] args) {
        UIFactory[] factories = {
            new WindowsUIFactory(), new MacUIFactory(), new LinuxUIFactory()
        };
        for (UIFactory factory : factories) {
            new LoginForm(factory).render();
        }
    }
}
```

---

### Example 2: Database Access Factory

Each database engine needs its own Connection, Command, and Transaction — they must all come from the same family.

```java
// Abstract Products
interface DBConnection {
    void open();
    void close();
}

interface DBCommand {
    void prepare(String sql);
    void execute();
}

interface DBTransaction {
    void begin();
    void commit();
    void rollback();
}

// MySQL Family
class MySQLConnection implements DBConnection {
    public void open()  { System.out.println("[MySQL] Connection opened on port 3306"); }
    public void close() { System.out.println("[MySQL] Connection closed"); }
}
class MySQLCommand implements DBCommand {
    private String sql;
    public void prepare(String sql) { this.sql = sql; System.out.println("[MySQL] Prepared: " + sql); }
    public void execute()           { System.out.println("[MySQL] Executed with InnoDB engine"); }
}
class MySQLTransaction implements DBTransaction {
    public void begin()    { System.out.println("[MySQL] BEGIN TRANSACTION"); }
    public void commit()   { System.out.println("[MySQL] COMMIT"); }
    public void rollback() { System.out.println("[MySQL] ROLLBACK"); }
}

// PostgreSQL Family
class PostgreSQLConnection implements DBConnection {
    public void open()  { System.out.println("[PostgreSQL] Connection opened on port 5432"); }
    public void close() { System.out.println("[PostgreSQL] Connection closed"); }
}
class PostgreSQLCommand implements DBCommand {
    private String sql;
    public void prepare(String sql) { this.sql = sql; System.out.println("[PostgreSQL] Prepared with $1 params: " + sql); }
    public void execute()           { System.out.println("[PostgreSQL] Executed with MVCC"); }
}
class PostgreSQLTransaction implements DBTransaction {
    public void begin()    { System.out.println("[PostgreSQL] BEGIN"); }
    public void commit()   { System.out.println("[PostgreSQL] COMMIT"); }
    public void rollback() { System.out.println("[PostgreSQL] ROLLBACK"); }
}

// SQLite Family
class SQLiteConnection implements DBConnection {
    private String path;
    SQLiteConnection(String path) { this.path = path; }
    public void open()  { System.out.println("[SQLite] Opened file: " + path); }
    public void close() { System.out.println("[SQLite] File closed"); }
}
class SQLiteCommand implements DBCommand {
    public void prepare(String sql) { System.out.println("[SQLite] Prepared: " + sql); }
    public void execute()           { System.out.println("[SQLite] Executed in-process"); }
}
class SQLiteTransaction implements DBTransaction {
    public void begin()    { System.out.println("[SQLite] BEGIN IMMEDIATE"); }
    public void commit()   { System.out.println("[SQLite] COMMIT"); }
    public void rollback() { System.out.println("[SQLite] ROLLBACK"); }
}

// Abstract Factory
interface DatabaseFactory {
    DBConnection  createConnection();
    DBCommand     createCommand();
    DBTransaction createTransaction();
}

// Concrete Factories
class MySQLFactory implements DatabaseFactory {
    public DBConnection  createConnection()  { return new MySQLConnection(); }
    public DBCommand     createCommand()     { return new MySQLCommand(); }
    public DBTransaction createTransaction() { return new MySQLTransaction(); }
}

class PostgreSQLFactory implements DatabaseFactory {
    public DBConnection  createConnection()  { return new PostgreSQLConnection(); }
    public DBCommand     createCommand()     { return new PostgreSQLCommand(); }
    public DBTransaction createTransaction() { return new PostgreSQLTransaction(); }
}

class SQLiteFactory implements DatabaseFactory {
    private String dbPath;
    SQLiteFactory(String dbPath) { this.dbPath = dbPath; }
    public DBConnection  createConnection()  { return new SQLiteConnection(dbPath); }
    public DBCommand     createCommand()     { return new SQLiteCommand(); }
    public DBTransaction createTransaction() { return new SQLiteTransaction(); }
}

// Client — works the same regardless of which DB engine is used
class UserRepository {
    private DatabaseFactory factory;

    UserRepository(DatabaseFactory factory) { this.factory = factory; }

    void saveUser(String name) {
        DBConnection  conn  = factory.createConnection();
        DBCommand     cmd   = factory.createCommand();
        DBTransaction txn   = factory.createTransaction();

        conn.open();
        txn.begin();
        cmd.prepare("INSERT INTO users (name) VALUES ('" + name + "')");
        cmd.execute();
        txn.commit();
        conn.close();
        System.out.println();
    }
}

// Usage
public class Main {
    public static void main(String[] args) {
        DatabaseFactory[] factories = {
            new MySQLFactory(),
            new PostgreSQLFactory(),
            new SQLiteFactory("./users.db")
        };

        for (DatabaseFactory factory : factories) {
            UserRepository repo = new UserRepository(factory);
            repo.saveUser("Alice");
        }
    }
}
```

---

### Example 3: Game World Factory (Fantasy vs Sci-Fi)

Each game theme has its own hero, weapon, and enemy — they must all match the same theme.

```java
// Abstract Products
interface Hero {
    String getName();
    void attack();
    int getHealth();
}

interface Weapon {
    String getWeaponName();
    int getDamage();
    void use();
}

interface Enemy {
    String getEnemyName();
    void spawn();
}

// Fantasy Family
class Knight implements Hero {
    public String getName()  { return "Knight"; }
    public void attack()     { System.out.println("Knight swings a broadsword!"); }
    public int getHealth()   { return 150; }
}
class Sword implements Weapon {
    public String getWeaponName() { return "Excalibur"; }
    public int getDamage()        { return 45; }
    public void use()             { System.out.println("Excalibur slices through armor!"); }
}
class Dragon implements Enemy {
    public String getEnemyName() { return "Dragon"; }
    public void spawn()          { System.out.println("A fearsome Dragon emerges from the cave!"); }
}

// Sci-Fi Family
class SpaceMarine implements Hero {
    public String getName()  { return "Space Marine"; }
    public void attack()     { System.out.println("Space Marine fires a plasma rifle!"); }
    public int getHealth()   { return 200; }
}
class PlasmaRifle implements Weapon {
    public String getWeaponName() { return "Plasma Rifle MK-7"; }
    public int getDamage()        { return 80; }
    public void use()             { System.out.println("Plasma Rifle blasts a burst of energy!"); }
}
class Cyborg implements Enemy {
    public String getEnemyName() { return "Cyborg"; }
    public void spawn()          { System.out.println("A deadly Cyborg teleports in!"); }
}

// Medieval Family
class Archer implements Hero {
    public String getName()  { return "Archer"; }
    public void attack()     { System.out.println("Archer nocks an arrow and fires!"); }
    public int getHealth()   { return 120; }
}
class Bow implements Weapon {
    public String getWeaponName() { return "Longbow"; }
    public int getDamage()        { return 35; }
    public void use()             { System.out.println("Longbow fires an arrow with deadly precision!"); }
}
class Orc implements Enemy {
    public String getEnemyName() { return "Orc"; }
    public void spawn()          { System.out.println("A savage Orc charges from the forest!"); }
}

// Cyberpunk Family
class Hacker implements Hero {
    public String getName()  { return "Hacker"; }
    public void attack()     { System.out.println("Hacker deploys a lethal virus!"); }
    public int getHealth()   { return 90; }
}
class HackingTool implements Weapon {
    public String getWeaponName() { return "Neural Spike"; }
    public int getDamage()        { return 60; }
    public void use()             { System.out.println("Neural Spike overloads enemy systems!"); }
}
class CorporateBot implements Enemy {
    public String getEnemyName() { return "Corporate Bot"; }
    public void spawn()          { System.out.println("A Corporate Security Bot locks on target!"); }
}

// Abstract Factory
interface GameWorldFactory {
    Hero   createHero();
    Weapon createWeapon();
    Enemy  createEnemy();
}

// Concrete Factories
class FantasyWorldFactory implements GameWorldFactory {
    public Hero   createHero()   { return new Knight(); }
    public Weapon createWeapon() { return new Sword(); }
    public Enemy  createEnemy()  { return new Dragon(); }
}

class SciFiWorldFactory implements GameWorldFactory {
    public Hero   createHero()   { return new SpaceMarine(); }
    public Weapon createWeapon() { return new PlasmaRifle(); }
    public Enemy  createEnemy()  { return new Cyborg(); }
}

class MedievalWorldFactory implements GameWorldFactory {
    public Hero   createHero()   { return new Archer(); }
    public Weapon createWeapon() { return new Bow(); }
    public Enemy  createEnemy()  { return new Orc(); }
}

class CyberpunkWorldFactory implements GameWorldFactory {
    public Hero   createHero()   { return new Hacker(); }
    public Weapon createWeapon() { return new HackingTool(); }
    public Enemy  createEnemy()  { return new CorporateBot(); }
}

// Client
class Game {
    private Hero   hero;
    private Weapon weapon;
    private Enemy  enemy;

    Game(GameWorldFactory factory) {
        hero   = factory.createHero();
        weapon = factory.createWeapon();
        enemy  = factory.createEnemy();
    }

    void play() {
        System.out.println("=== New Game ===");
        System.out.println("Hero: " + hero.getName() + " (HP: " + hero.getHealth() + ")");
        enemy.spawn();
        System.out.println("Weapon: " + weapon.getWeaponName() + " (DMG: " + weapon.getDamage() + ")");
        hero.attack();
        weapon.use();
        System.out.println();
    }
}

// Usage
public class Main {
    public static void main(String[] args) {
        GameWorldFactory[] worlds = {
            new FantasyWorldFactory(),
            new SciFiWorldFactory(),
            new MedievalWorldFactory(),
            new CyberpunkWorldFactory()
        };

        for (GameWorldFactory world : worlds) {
            new Game(world).play();
        }
    }
}
```

**Output:**
```
=== New Game ===
Hero: Knight (HP: 150)
A fearsome Dragon emerges from the cave!
Weapon: Excalibur (DMG: 45)
Knight swings a broadsword!
Excalibur slices through armor!

=== New Game ===
Hero: Space Marine (HP: 200)
A deadly Cyborg teleports in!
...
```

---

### Example 4: Cloud Provider Factory (AWS vs Azure vs GCP)

Each cloud provider has its own storage, compute, and messaging service — always used together.

```java
// Abstract Products
interface CloudStorage {
    void upload(String fileName, byte[] data);
    byte[] download(String fileName);
}

interface CloudCompute {
    String launchInstance(String instanceType);
    void terminateInstance(String instanceId);
}

interface CloudMessaging {
    void publishMessage(String topic, String message);
    String consumeMessage(String queue);
}

// AWS Family
class S3Storage implements CloudStorage {
    private String bucket;
    S3Storage(String bucket) { this.bucket = bucket; }
    public void upload(String fileName, byte[] data) {
        System.out.printf("[S3] Uploading %s (%d bytes) to s3://%s/%n", fileName, data.length, bucket);
    }
    public byte[] download(String fileName) {
        System.out.printf("[S3] Downloading s3://%s/%s%n", bucket, fileName);
        return new byte[0];
    }
}
class EC2Compute implements CloudCompute {
    public String launchInstance(String type) {
        String id = "i-" + (int)(Math.random() * 99999);
        System.out.printf("[EC2] Launched %s instance: %s%n", type, id);
        return id;
    }
    public void terminateInstance(String id) {
        System.out.printf("[EC2] Terminated instance: %s%n", id);
    }
}
class SQSMessaging implements CloudMessaging {
    public void publishMessage(String topic, String msg) {
        System.out.printf("[SQS] Published to %s: %s%n", topic, msg);
    }
    public String consumeMessage(String queue) {
        System.out.printf("[SQS] Consumed from queue: %s%n", queue);
        return "aws-message";
    }
}

// Azure Family
class BlobStorage implements CloudStorage {
    private String container;
    BlobStorage(String container) { this.container = container; }
    public void upload(String fileName, byte[] data) {
        System.out.printf("[Azure Blob] Uploading %s (%d bytes) to container '%s'%n", fileName, data.length, container);
    }
    public byte[] download(String fileName) {
        System.out.printf("[Azure Blob] Downloading %s from container '%s'%n", fileName, container);
        return new byte[0];
    }
}
class AzureVMCompute implements CloudCompute {
    public String launchInstance(String type) {
        String id = "vm-" + (int)(Math.random() * 99999);
        System.out.printf("[Azure VM] Launched %s: %s%n", type, id);
        return id;
    }
    public void terminateInstance(String id) {
        System.out.printf("[Azure VM] Deallocated: %s%n", id);
    }
}
class ServiceBusMessaging implements CloudMessaging {
    public void publishMessage(String topic, String msg) {
        System.out.printf("[Azure Service Bus] Sent to topic %s: %s%n", topic, msg);
    }
    public String consumeMessage(String queue) {
        System.out.printf("[Azure Service Bus] Received from queue: %s%n", queue);
        return "azure-message";
    }
}

// GCP Family
class GCSStorage implements CloudStorage {
    private String bucket;
    GCSStorage(String bucket) { this.bucket = bucket; }
    public void upload(String fileName, byte[] data) {
        System.out.printf("[GCS] Uploading %s (%d bytes) to gs://%s/%n", fileName, data.length, bucket);
    }
    public byte[] download(String fileName) {
        System.out.printf("[GCS] Downloading gs://%s/%s%n", bucket, fileName);
        return new byte[0];
    }
}
class GCECompute implements CloudCompute {
    public String launchInstance(String type) {
        String id = "gce-" + (int)(Math.random() * 99999);
        System.out.printf("[GCE] Started %s instance: %s%n", type, id);
        return id;
    }
    public void terminateInstance(String id) {
        System.out.printf("[GCE] Deleted instance: %s%n", id);
    }
}
class PubSubMessaging implements CloudMessaging {
    public void publishMessage(String topic, String msg) {
        System.out.printf("[GCP Pub/Sub] Published to %s: %s%n", topic, msg);
    }
    public String consumeMessage(String queue) {
        System.out.printf("[GCP Pub/Sub] Pulled from subscription: %s%n", queue);
        return "gcp-message";
    }
}

// Abstract Factory
interface CloudProviderFactory {
    CloudStorage   createStorage(String bucketOrContainer);
    CloudCompute   createCompute();
    CloudMessaging createMessaging();
}

// Concrete Factories
class AWSFactory implements CloudProviderFactory {
    public CloudStorage   createStorage(String bucket)    { return new S3Storage(bucket); }
    public CloudCompute   createCompute()                 { return new EC2Compute(); }
    public CloudMessaging createMessaging()               { return new SQSMessaging(); }
}

class AzureFactory implements CloudProviderFactory {
    public CloudStorage   createStorage(String container) { return new BlobStorage(container); }
    public CloudCompute   createCompute()                 { return new AzureVMCompute(); }
    public CloudMessaging createMessaging()               { return new ServiceBusMessaging(); }
}

class GCPFactory implements CloudProviderFactory {
    public CloudStorage   createStorage(String bucket)    { return new GCSStorage(bucket); }
    public CloudCompute   createCompute()                 { return new GCECompute(); }
    public CloudMessaging createMessaging()               { return new PubSubMessaging(); }
}

// Client — completely cloud-agnostic
class FileProcessingPipeline {
    private CloudStorage   storage;
    private CloudCompute   compute;
    private CloudMessaging messaging;

    FileProcessingPipeline(CloudProviderFactory factory) {
        storage   = factory.createStorage("my-app-bucket");
        compute   = factory.createCompute();
        messaging = factory.createMessaging();
    }

    void run(String filename) {
        System.out.println("--- Pipeline Start ---");
        storage.upload(filename, new byte[1024]);
        String instanceId = compute.launchInstance("standard-2cpu");
        messaging.publishMessage("file-events", "Processed: " + filename);
        compute.terminateInstance(instanceId);
        System.out.println("--- Pipeline End ---\n");
    }
}

// Usage — swap cloud provider by changing one line
public class Main {
    public static void main(String[] args) {
        CloudProviderFactory[] providers = {
            new AWSFactory(), new AzureFactory(), new GCPFactory()
        };

        for (CloudProviderFactory provider : providers) {
            new FileProcessingPipeline(provider).run("report.csv");
        }
    }
}
```

---

## Comparison Table

| Aspect | Simple Factory | Factory Method | Abstract Factory |
|--------|---------------|----------------|-----------------|
| **Who creates** | One static method | Subclass overrides method | Concrete factory class |
| **Extensibility** | Modify existing class | Add new subclass | Add new factory class |
| **Complexity** | Low | Medium | High |
| **Product variety** | One product type | One product type | Multiple related products |
| **Use when** | Few types, unlikely to change | Need subclass flexibility | Families of related objects |

---

## When to Use

- You don't know ahead of time which concrete class you need to instantiate
- The type of object depends on configuration, environment, or user input
- You want to centralize creation logic and swap implementations without touching client code
- You need to ensure a family of products is always used together (Abstract Factory)

## When NOT to Use

- Object creation is trivial and will never vary — don't add indirection for no benefit
- You only ever create one concrete type — a factory adds unnecessary layers

---

## Key Takeaways

1. **Simple Factory** — great starting point, but breaks OCP when you add types
2. **Factory Method** — lets subclasses control what gets instantiated; add types by adding subclasses, not modifying existing ones
3. **Abstract Factory** — coordinates creation of entire product families and guarantees consistency within a family
4. All three patterns **decouple** "what to create" from "how to use it"
