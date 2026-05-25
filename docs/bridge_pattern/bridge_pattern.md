# Bridge Pattern

## Intent

The Bridge pattern **decouples an abstraction from its implementation so that the two can vary independently**. Instead of building a monolithic class hierarchy that tries to cover every combination, you split the class into two separate hierarchies — one for the abstraction, one for the implementation — and connect them via composition.

Also known as: **Handle/Body**

---

## The Problem It Solves

### Without Bridge — The Explosion Problem

Imagine you are building a `Shape` system. Shapes can be `Circle` or `Square`. Each shape can be rendered using `OpenGL` or `DirectX`. Without Bridge, the naive approach is to create a class for every combination:

```
CircleOpenGL
CircleDirectX
SquareOpenGL
SquareDirectX
```

Add a third renderer (`Vulkan`) and a third shape (`Triangle`) — now you have **9 classes**. Add more and it grows as `M × N`. This is the **Cartesian product explosion**.

### With Bridge

You split the two dimensions into separate hierarchies and compose them:

```
Abstraction (Shape)          Implementor (Renderer)
─────────────────            ──────────────────────
Circle  ──────────────────►  OpenGLRenderer
Square                       DirectXRenderer
                             VulkanRenderer
```

Adding a new shape costs **1 class**. Adding a new renderer costs **1 class**. Total classes grow as `M + N` instead of `M × N`.

---

## Structure

```
Abstraction                     <<interface>>
+ implementor: Implementor  ──► Implementor
+ operation()                   + operationImpl()
      |                               |
      | extends                       | implements
      |                               |
RefinedAbstraction          ConcreteImplementorA
+ operation()               ConcreteImplementorB
```

### Participants

| Role                    | Description                                                                 |
|-------------------------|-----------------------------------------------------------------------------|
| `Abstraction`           | Defines the high-level control logic; holds a reference to the Implementor  |
| `RefinedAbstraction`    | Extends Abstraction with more specific behavior                             |
| `Implementor`           | Interface declaring the low-level operations used by the Abstraction        |
| `ConcreteImplementor`   | Provides a concrete implementation of the Implementor interface             |
| `Client`                | Works only with the Abstraction; the Implementor is injected at runtime     |

---

## Key Insight — Abstraction vs Implementation

- **Abstraction** = the high-level "what" — the part the client interacts with.
- **Implementation** = the low-level "how" — the part doing the actual work.
- The abstraction **delegates** to the implementor; it does not inherit from it.

> Prefer composition over inheritance — Bridge is a textbook application of this principle.

---

## Example 1: Shape + Renderer (the Classic)

```java
// Implementor interface
interface Renderer {
    void renderCircle(double radius);
    void renderSquare(double side);
}

// Concrete Implementors
class OpenGLRenderer implements Renderer {
    @Override
    public void renderCircle(double radius) {
        System.out.println("OpenGL: Drawing circle with radius " + radius);
    }

    @Override
    public void renderSquare(double side) {
        System.out.println("OpenGL: Drawing square with side " + side);
    }
}

class DirectXRenderer implements Renderer {
    @Override
    public void renderCircle(double radius) {
        System.out.println("DirectX: Drawing circle with radius " + radius);
    }

    @Override
    public void renderSquare(double side) {
        System.out.println("DirectX: Drawing square with side " + side);
    }
}

// Abstraction
abstract class Shape {
    protected Renderer renderer;   // the bridge

    protected Shape(Renderer renderer) {
        this.renderer = renderer;
    }

    abstract void draw();
    abstract void resize(double factor);
}

// Refined Abstractions
class Circle extends Shape {
    private double radius;

    public Circle(Renderer renderer, double radius) {
        super(renderer);
        this.radius = radius;
    }

    @Override
    public void draw() {
        renderer.renderCircle(radius);
    }

    @Override
    public void resize(double factor) {
        radius *= factor;
    }
}

class Square extends Shape {
    private double side;

    public Square(Renderer renderer, double side) {
        super(renderer);
        this.side = side;
    }

    @Override
    public void draw() {
        renderer.renderSquare(side);
    }

    @Override
    public void resize(double factor) {
        side *= factor;
    }
}

// Client
public class BridgeDemo {
    public static void main(String[] args) {
        Renderer openGL = new OpenGLRenderer();
        Renderer directX = new DirectXRenderer();

        Shape circle = new Circle(openGL, 5.0);
        Shape square = new Square(directX, 3.0);

        circle.draw();      // OpenGL: Drawing circle with radius 5.0
        square.draw();      // DirectX: Drawing square with side 3.0

        // Swap renderer at runtime — no class change needed
        Shape anotherCircle = new Circle(directX, 7.0);
        anotherCircle.draw(); // DirectX: Drawing circle with radius 7.0
    }
}
```

**Key takeaway:** The renderer is injected — you can mix any shape with any renderer freely. Adding `VulkanRenderer` later requires zero changes to the shape classes.

---

## Example 2: Notification System — Channel + Urgency

### Scenario

A notification system must send alerts through different **channels** (Email, SMS, Slack) at different **urgency levels** (Info, Warning, Critical). Each urgency level formats the message differently, but the actual delivery is handled by the channel.

```java
// Implementor — message delivery channel
interface MessageChannel {
    void send(String recipient, String message);
}

class EmailChannel implements MessageChannel {
    @Override
    public void send(String recipient, String message) {
        System.out.println("[EMAIL -> " + recipient + "] " + message);
    }
}

class SMSChannel implements MessageChannel {
    @Override
    public void send(String recipient, String message) {
        System.out.println("[SMS -> " + recipient + "] " + message);
    }
}

class SlackChannel implements MessageChannel {
    @Override
    public void send(String recipient, String message) {
        System.out.println("[SLACK @" + recipient + "] " + message);
    }
}

// Abstraction — notification urgency level
abstract class Notification {
    protected MessageChannel channel;
    protected String recipient;

    protected Notification(MessageChannel channel, String recipient) {
        this.channel = channel;
        this.recipient = recipient;
    }

    abstract void notify(String message);
}

// Refined Abstractions
class InfoNotification extends Notification {
    public InfoNotification(MessageChannel channel, String recipient) {
        super(channel, recipient);
    }

    @Override
    public void notify(String message) {
        channel.send(recipient, "[INFO] " + message);
    }
}

class WarningNotification extends Notification {
    public WarningNotification(MessageChannel channel, String recipient) {
        super(channel, recipient);
    }

    @Override
    public void notify(String message) {
        channel.send(recipient, "[WARNING] ⚠ " + message);
    }
}

class CriticalNotification extends Notification {
    public CriticalNotification(MessageChannel channel, String recipient) {
        super(channel, recipient);
    }

    @Override
    public void notify(String message) {
        // Critical alerts go out twice — once as-is, once with escalation prefix
        channel.send(recipient, "[CRITICAL] 🚨 " + message);
        channel.send(recipient, "[CRITICAL] 🚨 ESCALATION: " + message);
    }
}

// Client
public class NotificationDemo {
    public static void main(String[] args) {
        MessageChannel email = new EmailChannel();
        MessageChannel sms = new SMSChannel();
        MessageChannel slack = new SlackChannel();

        Notification info    = new InfoNotification(email, "admin@company.com");
        Notification warning = new WarningNotification(slack, "devops-team");
        Notification critical = new CriticalNotification(sms, "+1-800-555-0199");

        info.notify("Scheduled maintenance in 30 minutes.");
        warning.notify("Disk usage at 85%.");
        critical.notify("Database is unreachable!");
    }
}
```

**Output:**
```
[EMAIL -> admin@company.com] [INFO] Scheduled maintenance in 30 minutes.
[SLACK @devops-team] [WARNING] ⚠ Disk usage at 85%.
[SMS -> +1-800-555-0199] [CRITICAL] 🚨 Database is unreachable!
[SMS -> +1-800-555-0199] [CRITICAL] 🚨 ESCALATION: Database is unreachable!
```

**Key takeaway:** 3 urgency levels × 3 channels = 9 combinations, handled by just **6 classes** (3 + 3). Each axis extends independently — adding `PushNotificationChannel` touches nothing except one new implementor class.

---

## Example 3: Device + Remote Control

### Scenario

A universal remote abstraction should work with different devices (TV, Radio). The remote's feature set (basic vs advanced) is independent of the device it controls.

```java
// Implementor — the device being controlled
interface Device {
    boolean isEnabled();
    void enable();
    void disable();
    int getVolume();
    void setVolume(int volume);
    int getChannel();
    void setChannel(int channel);
    String getName();
}

class TV implements Device {
    private boolean on = false;
    private int volume = 30;
    private int channel = 1;

    @Override public boolean isEnabled() { return on; }
    @Override public void enable()       { on = true;  System.out.println("TV is ON"); }
    @Override public void disable()      { on = false; System.out.println("TV is OFF"); }
    @Override public int getVolume()     { return volume; }
    @Override public void setVolume(int v) {
        volume = Math.max(0, Math.min(100, v));
        System.out.println("TV volume: " + volume);
    }
    @Override public int getChannel()    { return channel; }
    @Override public void setChannel(int c) {
        channel = c;
        System.out.println("TV channel: " + channel);
    }
    @Override public String getName()    { return "Samsung TV"; }
}

class Radio implements Device {
    private boolean on = false;
    private int volume = 50;
    private int channel = 100;  // FM frequency × 10

    @Override public boolean isEnabled() { return on; }
    @Override public void enable()       { on = true;  System.out.println("Radio is ON"); }
    @Override public void disable()      { on = false; System.out.println("Radio is OFF"); }
    @Override public int getVolume()     { return volume; }
    @Override public void setVolume(int v) {
        volume = Math.max(0, Math.min(100, v));
        System.out.println("Radio volume: " + volume);
    }
    @Override public int getChannel()    { return channel; }
    @Override public void setChannel(int c) {
        channel = c;
        System.out.printf("Radio frequency: %.1f FM%n", c / 10.0);
    }
    @Override public String getName()    { return "Sony Radio"; }
}

// Abstraction — the remote control
class RemoteControl {
    protected Device device;

    public RemoteControl(Device device) {
        this.device = device;
    }

    public void togglePower() {
        if (device.isEnabled()) device.disable();
        else                    device.enable();
    }

    public void volumeUp()   { device.setVolume(device.getVolume() + 10); }
    public void volumeDown() { device.setVolume(device.getVolume() - 10); }
    public void channelUp()  { device.setChannel(device.getChannel() + 1); }
    public void channelDown(){ device.setChannel(device.getChannel() - 1); }
}

// Refined Abstraction — adds mute and favourite channel
class AdvancedRemote extends RemoteControl {
    private int savedVolume = -1;

    public AdvancedRemote(Device device) {
        super(device);
    }

    public void mute() {
        if (savedVolume == -1) {
            savedVolume = device.getVolume();
            device.setVolume(0);
            System.out.println("[MUTED]");
        } else {
            device.setVolume(savedVolume);
            savedVolume = -1;
            System.out.println("[UNMUTED]");
        }
    }

    public void printStatus() {
        System.out.printf("Device: %s | On: %s | Volume: %d | Channel: %d%n",
            device.getName(), device.isEnabled(), device.getVolume(), device.getChannel());
    }
}

// Client
public class RemoteDemo {
    public static void main(String[] args) {
        Device tv = new TV();
        AdvancedRemote remote = new AdvancedRemote(tv);

        remote.togglePower();  // TV is ON
        remote.volumeUp();     // TV volume: 40
        remote.mute();         // TV volume: 0  [MUTED]
        remote.mute();         // TV volume: 40 [UNMUTED]
        remote.channelUp();    // TV channel: 2
        remote.printStatus();  // Device: Samsung TV | On: true | Volume: 40 | Channel: 2

        System.out.println("--- Switching to Radio ---");

        Device radio = new Radio();
        RemoteControl basicRemote = new RemoteControl(radio);
        basicRemote.togglePower();  // Radio is ON
        basicRemote.channelUp();    // Radio frequency: 10.1 FM
    }
}
```

**Key takeaway:** `AdvancedRemote` doesn't know or care what device it controls — it delegates all device-specific behavior. A `SmartRemote` with voice control could extend `RemoteControl` without touching `TV` or `Radio` at all.

---

## Example 4: Report Generation — Format + Data Source

### Scenario

Reports can be formatted as **PDF** or **HTML**. Data can come from a **Database** or a **CSV file**. These are two independent dimensions.

```java
// Implementor — where data comes from
interface DataSource {
    String[][] fetchData();
    String[] getHeaders();
}

class DatabaseSource implements DataSource {
    @Override
    public String[] getHeaders() {
        return new String[]{"ID", "Name", "Revenue"};
    }

    @Override
    public String[][] fetchData() {
        return new String[][]{
            {"1", "Product A", "$12,000"},
            {"2", "Product B", "$8,500"},
            {"3", "Product C", "$21,000"}
        };
    }
}

class CsvSource implements DataSource {
    private String filePath;

    public CsvSource(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String[] getHeaders() {
        // In reality you'd parse the CSV; simulated here
        return new String[]{"Region", "Sales", "Target"};
    }

    @Override
    public String[][] fetchData() {
        return new String[][]{
            {"North", "45,000", "50,000"},
            {"South", "38,000", "40,000"},
            {"East",  "52,000", "48,000"}
        };
    }
}

// Abstraction — report format
abstract class Report {
    protected DataSource dataSource;

    protected Report(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    abstract void generate(String title);
}

// Refined Abstractions
class HtmlReport extends Report {
    public HtmlReport(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void generate(String title) {
        String[] headers = dataSource.getHeaders();
        String[][] rows  = dataSource.fetchData();

        System.out.println("<html><body>");
        System.out.println("<h1>" + title + "</h1>");
        System.out.println("<table border='1'>");

        System.out.print("<tr>");
        for (String h : headers) System.out.print("<th>" + h + "</th>");
        System.out.println("</tr>");

        for (String[] row : rows) {
            System.out.print("<tr>");
            for (String cell : row) System.out.print("<td>" + cell + "</td>");
            System.out.println("</tr>");
        }

        System.out.println("</table></body></html>");
    }
}

class PdfReport extends Report {
    public PdfReport(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void generate(String title) {
        String[] headers = dataSource.getHeaders();
        String[][] rows  = dataSource.fetchData();

        System.out.println("=== PDF REPORT: " + title + " ===");
        System.out.println(String.join(" | ", headers));
        System.out.println("-".repeat(40));
        for (String[] row : rows) {
            System.out.println(String.join(" | ", row));
        }
        System.out.println("=".repeat(40));
    }
}

// Client
public class ReportDemo {
    public static void main(String[] args) {
        DataSource db  = new DatabaseSource();
        DataSource csv = new CsvSource("q1_sales.csv");

        Report htmlDbReport  = new HtmlReport(db);
        Report pdfCsvReport  = new PdfReport(csv);
        Report htmlCsvReport = new HtmlReport(csv);  // mix freely

        System.out.println("--- HTML from DB ---");
        htmlDbReport.generate("Product Revenue Report");

        System.out.println("\n--- PDF from CSV ---");
        pdfCsvReport.generate("Q1 Regional Sales");

        System.out.println("\n--- HTML from CSV ---");
        htmlCsvReport.generate("Q1 Regional Sales (HTML)");
    }
}
```

**Key takeaway:** `HtmlReport` and `PdfReport` do not know where their data comes from. `DatabaseSource` and `CsvSource` do not know how the data will be rendered. Any combination works out of the box.

---

## Example 5: Payment Processing — Method + Currency

### Scenario

A payment gateway supports multiple **payment methods** (CreditCard, UPI, Crypto) and must process payments in different **currencies** (USD, EUR, INR). The formatting/conversion logic and the payment-method protocol are independent.

```java
// Implementor — the payment method (protocol-level)
interface PaymentMethod {
    void initiateTransaction(double amount, String currency);
    boolean confirm();
    String getMethodName();
}

class CreditCardPayment implements PaymentMethod {
    private String cardNumber;

    public CreditCardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public void initiateTransaction(double amount, String currency) {
        System.out.printf("Credit Card [%s]: Charging %.2f %s%n",
            cardNumber.substring(cardNumber.length() - 4), amount, currency);
    }

    @Override
    public boolean confirm() {
        System.out.println("Credit Card: OTP verified. Transaction approved.");
        return true;
    }

    @Override
    public String getMethodName() { return "Credit Card"; }
}

class UpiPayment implements PaymentMethod {
    private String upiId;

    public UpiPayment(String upiId) {
        this.upiId = upiId;
    }

    @Override
    public void initiateTransaction(double amount, String currency) {
        System.out.printf("UPI [%s]: Requesting %.2f %s%n", upiId, amount, currency);
    }

    @Override
    public boolean confirm() {
        System.out.println("UPI: PIN verified. Transaction approved.");
        return true;
    }

    @Override
    public String getMethodName() { return "UPI"; }
}

class CryptoPayment implements PaymentMethod {
    private String walletAddress;

    public CryptoPayment(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    @Override
    public void initiateTransaction(double amount, String currency) {
        System.out.printf("Crypto [%s]: Broadcasting %.6f BTC equivalent of %.2f %s%n",
            walletAddress.substring(0, 8) + "...", amount / 60000, amount, currency);
    }

    @Override
    public boolean confirm() {
        System.out.println("Crypto: 6 block confirmations received. Transaction approved.");
        return true;
    }

    @Override
    public String getMethodName() { return "Crypto"; }
}

// Abstraction — the currency / order context
abstract class Order {
    protected PaymentMethod paymentMethod;
    protected String targetCurrency;
    protected double exchangeRate;  // rate relative to USD

    protected Order(PaymentMethod paymentMethod, String targetCurrency, double exchangeRate) {
        this.paymentMethod = paymentMethod;
        this.targetCurrency = targetCurrency;
        this.exchangeRate = exchangeRate;
    }

    abstract void pay(double amountInUSD);
}

// Refined Abstractions — different currency contexts
class USDOrder extends Order {
    public USDOrder(PaymentMethod paymentMethod) {
        super(paymentMethod, "USD", 1.0);
    }

    @Override
    public void pay(double amountInUSD) {
        System.out.println("Processing USD order...");
        paymentMethod.initiateTransaction(amountInUSD, targetCurrency);
        paymentMethod.confirm();
    }
}

class INROrder extends Order {
    public INROrder(PaymentMethod paymentMethod) {
        super(paymentMethod, "INR", 83.5);
    }

    @Override
    public void pay(double amountInUSD) {
        double amountInINR = amountInUSD * exchangeRate;
        System.out.printf("Processing INR order (%.2f USD = %.2f INR)...%n", amountInUSD, amountInINR);
        paymentMethod.initiateTransaction(amountInINR, targetCurrency);
        paymentMethod.confirm();
    }
}

class EUROrder extends Order {
    public EUROrder(PaymentMethod paymentMethod) {
        super(paymentMethod, "EUR", 0.92);
    }

    @Override
    public void pay(double amountInUSD) {
        double amountInEUR = amountInUSD * exchangeRate;
        System.out.printf("Processing EUR order (%.2f USD = %.2f EUR)...%n", amountInUSD, amountInEUR);
        paymentMethod.initiateTransaction(amountInEUR, targetCurrency);
        paymentMethod.confirm();
    }
}

// Client
public class PaymentDemo {
    public static void main(String[] args) {
        PaymentMethod card   = new CreditCardPayment("4111111111111234");
        PaymentMethod upi    = new UpiPayment("user@okbank");
        PaymentMethod crypto = new CryptoPayment("1A2B3C4D5E6F7G8H");

        Order usdCardOrder  = new USDOrder(card);
        Order inrUpiOrder   = new INROrder(upi);
        Order eurCryptoOrder = new EUROrder(crypto);

        usdCardOrder.pay(100.00);
        System.out.println();
        inrUpiOrder.pay(50.00);
        System.out.println();
        eurCryptoOrder.pay(200.00);
    }
}
```

**Output:**
```
Processing USD order...
Credit Card [1234]: Charging 100.00 USD
Credit Card: OTP verified. Transaction approved.

Processing INR order (50.00 USD = 4175.00 INR)...
UPI [user@okbank]: Requesting 4175.00 INR
UPI: PIN verified. Transaction approved.

Processing EUR order (200.00 USD = 184.00 EUR)...
Crypto [1A2B3C4...]: Broadcasting 0.003067 BTC equivalent of 184.00 EUR
Crypto: 6 block confirmations received. Transaction approved.
```

**Key takeaway:** The currency conversion logic lives in the `Order` hierarchy; the payment protocol lives in the `PaymentMethod` hierarchy. Each side evolves without affecting the other.

---

## Advantages

| Advantage                        | Explanation                                                                 |
|----------------------------------|-----------------------------------------------------------------------------|
| Open/Closed Principle            | New abstractions and implementors can be added without modifying each other |
| Single Responsibility Principle  | High-level logic (abstraction) and low-level detail (impl) stay separate    |
| Independent extensibility        | Each axis (shape/renderer, channel/urgency) grows on its own                |
| Runtime flexibility              | Implementors can be swapped at runtime via dependency injection              |
| Avoids class explosion           | `M + N` classes instead of `M × N`                                          |

---

## Disadvantages

| Disadvantage           | Explanation                                                                  |
|------------------------|------------------------------------------------------------------------------|
| Upfront complexity     | If you only have one dimension of variation, Bridge is overkill              |
| Indirection            | An extra layer of delegation makes the flow harder to trace at a glance      |
| Design foresight needed| You must identify the two independent dimensions early                       |

---

## Bridge vs Similar Patterns

| Pattern   | Similarity                          | Key Difference                                                  |
|-----------|-------------------------------------|-----------------------------------------------------------------|
| Adapter   | Both involve an interface + another class | Adapter fixes an **existing** incompatibility; Bridge is **designed upfront** to allow variation |
| Strategy  | Both use composition to delegate behavior | Strategy varies a **single behavior**; Bridge separates two whole **class hierarchies** |
| Decorator | Both use composition instead of inheritance | Decorator **adds** responsibilities; Bridge **separates** abstraction from implementation |
| Abstract Factory | Both can use an interface hierarchy | Abstract Factory creates objects; Bridge controls how abstraction and implementation collaborate |

---

## When to Use Bridge

- You have **two orthogonal dimensions** of variation (shape+renderer, channel+urgency, format+source).
- You anticipate that **both dimensions will grow** independently.
- You want to be able to **swap implementations at runtime**.
- You want to share an implementation across multiple abstractions.
- The class hierarchy is already getting unwieldy due to combinatorial growth.

## When NOT to Use Bridge

- You only have one dimension of variation — use plain polymorphism.
- You have a fixed, small set of combinations that will never change.
- The overhead of an extra layer of indirection is not justified.

---

## Real-World Java / Framework Examples

- `java.sql.DriverManager` + `java.sql.Driver` — `Connection` abstractions are bridged to vendor-specific JDBC drivers (MySQL, PostgreSQL, Oracle).
- AWT/Swing Peer Model — `Component` (abstraction) is bridged to platform-native peers (Win32, GTK, Cocoa).
- SLF4J — the logging abstraction is the bridge; Logback, Log4j, and java.util.logging are the implementors.
- Spring's `PlatformTransactionManager` — the transaction abstraction delegates to JDBC, JPA, or JTA implementors.

---

## Practice Problems

### Problem 1 — Messaging App: Platform × Encryption
**Context:** A messaging app must send messages over different **transport platforms** (WebSocket, REST API, MQTT). Messages can be encrypted with different **encryption schemes** (AES, RSA, No encryption). Both dimensions will grow as the product matures.

**Task:** Design and implement the Bridge. The `Message` abstraction should have `send(String content, String recipient)`. Encrypted content must be handed to the platform for delivery. Show at least three combinations in a `main` method.

**Hint:** `EncryptionEngine` is your implementor interface. `MessageSender` is your abstraction. `AESEncryption`, `RSAEncryption`, `NoEncryption` are concrete implementors. `WebSocketSender`, `RestApiSender`, `MQTTSender` are refined abstractions.

---

### Problem 2 — E-Commerce: Discount × Tax Strategy
**Context:** An e-commerce platform applies **discounts** (FlatDiscount, PercentageDiscount, BuyOneGetOne) and **taxes** (GST, VAT, TaxExempt) to orders. Discounts and tax regimes are updated frequently and independently (new countries, new promotions).

**Task:** Model `Order` as the abstraction with `calculateFinalPrice(double basePrice)`. `TaxStrategy` is the implementor. Show that you can combine any discount with any tax without a combinatorial class explosion.

**Expected:** 3 discount types × 3 tax types should be handled by 6 classes + 1 abstract base (not 9 concrete classes).

---

### Problem 3 — Content Platform: Content Type × Storage Backend
**Context:** A content platform stores different types of content: **Articles**, **Videos**, and **Podcasts**. Each content type can be stored in different backends: **LocalFileSystem**, **S3**, and **Azure Blob Storage**. Upload/download protocols differ per backend, but all content types share the same high-level operations (`upload`, `download`, `delete`).

**Task:** Implement the Bridge. The `Content` hierarchy handles metadata and the content-specific payload structure. The `StorageBackend` hierarchy handles the actual I/O. Demonstrate uploading a `Video` to `S3` and downloading a `Podcast` from `Azure Blob`.

**Stretch:** Add a `CDNContent` refined abstraction that, after uploading via the bridge, also calls a `purgeCache()` on a simulated CDN.

---

### Problem 4 — Game Engine: Character × Rendering Engine
**Context:** A game engine has different **character types** (Warrior, Mage, Archer) each with their own `attack()`, `defend()`, and `specialMove()` logic. Characters must be rendered using different **engines** (OpenGL, Vulkan, Software). The rendering engine is chosen at startup based on the user's hardware; the character type is chosen during gameplay.

**Task:** Implement the Bridge so that rendering (drawing the character sprite, animating attacks) is fully delegated to the `RenderingEngine` implementor. The character's `attack()` method should perform the game logic then call `renderer.drawAttackAnimation(String characterName, String attackType)`.

**Expected output example:**
```
[Vulkan] Warrior performs SLASH attack animation.
[OpenGL] Mage performs FIREBALL attack animation.
[Vulkan] Archer performs ARROW_RAIN attack animation.
```

---

### Problem 5 — Logging Framework: Log Level × Output Target
**Context:** Build a mini logging framework. Log messages have a **severity level** (Debug, Info, Error). Each log entry can be written to different **output targets** (Console, File, RemoteServer). The urgency level controls formatting (e.g., Error prepends a stack trace placeholder); the output target controls delivery.

**Task:** Implement the Bridge. `Logger` is the abstraction; `LogTarget` is the implementor. Ensure:
1. `DebugLogger` only forwards to the target if a debug flag is enabled.
2. `ErrorLogger` appends `[STACK TRACE OMITTED]` to every message before forwarding.
3. `FileLogger` (implementor) writes output with a timestamp prefix.
4. Demonstrate that swapping from `ConsoleTarget` to `FileTarget` at runtime for an existing `InfoLogger` requires zero code changes to `InfoLogger`.

**Stretch:** Add a `MultiTargetLogger` that fans out a single log call to a `List<LogTarget>`.
