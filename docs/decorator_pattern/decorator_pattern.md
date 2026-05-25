# Decorator Pattern — Add Behaviour to Objects Without Changing Their Class

---

## The Analogy

Think of a plain coffee at a café. You start with a basic espresso. Then you ask for milk — the barista wraps it into a latte. Then you ask for caramel syrup — the barista wraps the latte with a caramel drizzle. Each "wrapping" adds something new without throwing away the original coffee. The Decorator pattern works exactly the same way: you take an existing object and wrap it inside another object that adds new behaviour.

Real-world mapping:
- **Espresso** = the original object (the "component")
- **Milk / Caramel** = the decorators (wrappers that add behaviour)
- **The final drink** = the decorated object (original + all the add-ons, stacked)

---

## What Problem Does It Solve?

Imagine you have a `TextEditor` class that can display plain text. Now you want versions that can display **bold** text, *italic* text, underlined text, and combinations like bold-italic-underlined. If you use inheritance (subclassing), you end up creating a separate class for every possible combination: `BoldText`, `ItalicText`, `BoldItalicText`, `BoldUnderlinedText`, `ItalicUnderlinedText`… the list explodes.

Every time you add one new style, you double the number of classes you need. This is called a **class explosion** — your codebase becomes unmanageable. The Decorator pattern solves this by letting you stack behaviours at runtime, not at compile time. You write one small class per feature and compose them freely.

---

## The Formal Definition

> "Attach additional responsibilities to an object dynamically. Decorators provide a flexible alternative to subclassing for extending functionality." — Gang of Four

Plain English: instead of creating a new subclass every time you want to add a feature, you wrap the original object inside a new object that adds the feature. The wrapper looks like the original (same interface), so calling code never knows the difference.

---

## The Core Structure (Java)

```java
// 1. The Component — defines the contract that both the real object and all decorators share
interface Component {
    String operate();
}

// 2. The Concrete Component — the real object, the base that gets decorated
class ConcreteComponent implements Component {
    @Override
    public String operate() {
        return "base result";
    }
}

// 3. The Base Decorator — holds a reference to a Component (real OR another decorator)
//    This is what makes stacking possible.
class BaseDecorator implements Component {
    protected Component wrapped;  // the object being wrapped

    public BaseDecorator(Component wrapped) {
        this.wrapped = wrapped;  // inject the object to decorate
    }

    @Override
    public String operate() {
        return wrapped.operate();  // delegate to the wrapped object by default
    }
}

// 4. Concrete Decorators — each one adds ONE specific behaviour
class ConcreteDecoratorA extends BaseDecorator {
    public ConcreteDecoratorA(Component wrapped) {
        super(wrapped);
    }

    @Override
    public String operate() {
        return "A(" + super.operate() + ")";  // add behaviour before/after the delegate call
    }
}

class ConcreteDecoratorB extends BaseDecorator {
    public ConcreteDecoratorB(Component wrapped) {
        super(wrapped);
    }

    @Override
    public String operate() {
        return "B(" + super.operate() + ")";
    }
}

// 5. Client — stacks decorators freely at runtime
class Main {
    public static void main(String[] args) {
        Component base = new ConcreteComponent();           // plain object
        Component withA = new ConcreteDecoratorA(base);    // wrap with A
        Component withAB = new ConcreteDecoratorB(withA);  // wrap with B on top of A

        System.out.println(withAB.operate());
        // Output: B(A(base result))
    }
}
```

---

## Example 1 — BAD: Without the Pattern

**Trigger:** You have a simple notification system. You need to send plain notifications. Then you need SMS notifications. Then email notifications. Then SMS + email together.

```java
// BAD — Using inheritance to cover every combination of notification type.
// Every new channel forces you to create more subclasses.

class Notifier {
    public void send(String message) {
        System.out.println("Sending notification: " + message);
    }
}

class SMSNotifier extends Notifier {
    @Override
    public void send(String message) {
        super.send(message);
        System.out.println("Sending SMS: " + message);
    }
}

class EmailNotifier extends Notifier {
    @Override
    public void send(String message) {
        super.send(message);
        System.out.println("Sending Email: " + message);
    }
}

// What if someone needs BOTH SMS and Email?
// You are forced to create yet another class:
class SMSAndEmailNotifier extends Notifier {
    @Override
    public void send(String message) {
        super.send(message);
        System.out.println("Sending SMS: " + message);
        System.out.println("Sending Email: " + message);
    }
}

// What if you add Slack? WhatsApp? Push notifications?
// You now need: SlackNotifier, WhatsAppNotifier,
//               SMSAndSlackNotifier, EmailAndSlackNotifier,
//               SMSAndEmailAndSlackNotifier ... this never ends.
// This is a CLASS EXPLOSION.

class Main {
    public static void main(String[] args) {
        Notifier n = new SMSAndEmailNotifier();
        n.send("Your order has shipped!");
    }
}
// Output:
// Sending notification: Your order has shipped!
// Sending SMS: Your order has shipped!
// Sending Email: Your order has shipped!
```

**What goes wrong:** Every new combination of channels requires a new class. With 4 channels you could need up to 15 subclasses. You cannot choose channels at runtime — the combination is baked in at compile time.

---

## Example 2 — GOOD: With the Decorator Pattern Applied

```java
// GOOD — Each channel is a decorator. Stack them freely at runtime.

// Step 1: The Component interface — every notifier agrees on this contract
interface Notifier {
    void send(String message);
}

// Step 2: The base (concrete component) — the simplest real notifier
class BaseNotifier implements Notifier {
    @Override
    public void send(String message) {
        System.out.println("Sending base notification: " + message);
    }
}

// Step 3: The abstract decorator — holds a reference to any Notifier
//         (could be BaseNotifier OR another decorator)
abstract class NotifierDecorator implements Notifier {
    protected Notifier wrapped;  // the object we are adding behaviour to

    public NotifierDecorator(Notifier wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void send(String message) {
        wrapped.send(message);  // always delegate first, then add own behaviour
    }
}

// Step 4: Concrete decorators — one class per channel, nothing more

class SMSDecorator extends NotifierDecorator {
    public SMSDecorator(Notifier wrapped) {
        super(wrapped);
    }

    @Override
    public void send(String message) {
        super.send(message);                               // delegate
        System.out.println("Sending SMS: " + message);   // add SMS behaviour
    }
}

class EmailDecorator extends NotifierDecorator {
    public EmailDecorator(Notifier wrapped) {
        super(wrapped);
    }

    @Override
    public void send(String message) {
        super.send(message);
        System.out.println("Sending Email: " + message);
    }
}

class SlackDecorator extends NotifierDecorator {
    public SlackDecorator(Notifier wrapped) {
        super(wrapped);
    }

    @Override
    public void send(String message) {
        super.send(message);
        System.out.println("Sending Slack message: " + message);
    }
}

// Step 5: Client — stack whichever channels you need, at runtime
class Main {
    public static void main(String[] args) {
        // Only SMS
        Notifier smsOnly = new SMSDecorator(new BaseNotifier());
        smsOnly.send("Your order has shipped!");
        System.out.println("---");

        // SMS + Email
        Notifier smsAndEmail = new EmailDecorator(new SMSDecorator(new BaseNotifier()));
        smsAndEmail.send("Payment confirmed!");
        System.out.println("---");

        // SMS + Email + Slack
        Notifier all = new SlackDecorator(new EmailDecorator(new SMSDecorator(new BaseNotifier())));
        all.send("Server is down!");
    }
}

// Output:
// Sending base notification: Your order has shipped!
// Sending SMS: Your order has shipped!
// ---
// Sending base notification: Payment confirmed!
// Sending SMS: Payment confirmed!
// Sending Email: Payment confirmed!
// ---
// Sending base notification: Server is down!
// Sending SMS: Server is down!
// Sending Email: Server is down!
// Sending Slack message: Server is down!
```

**What changed:** You write exactly 3 decorator classes for 3 channels. Any combination works automatically by stacking. Adding a new channel (WhatsApp) means adding exactly one new class — nothing else changes.

---

## Example 3 — Text Formatting System

**Trigger:** You are building a document editor. Text can be plain, bold, italic, underlined — or any combination. You need to render the final formatted string.

```java
// The contract every text formatter must honour
interface Text {
    String render();
}

// Plain text — the base component
class PlainText implements Text {
    private String content;

    public PlainText(String content) {
        this.content = content;
    }

    @Override
    public String render() {
        return content;  // returns the raw string, no formatting
    }
}

// Base decorator
abstract class TextDecorator implements Text {
    protected Text wrapped;

    public TextDecorator(Text wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String render() {
        return wrapped.render();
    }
}

// Each decorator wraps the rendered string in an HTML tag
class BoldDecorator extends TextDecorator {
    public BoldDecorator(Text wrapped) { super(wrapped); }

    @Override
    public String render() {
        return "<b>" + super.render() + "</b>";
    }
}

class ItalicDecorator extends TextDecorator {
    public ItalicDecorator(Text wrapped) { super(wrapped); }

    @Override
    public String render() {
        return "<i>" + super.render() + "</i>";
    }
}

class UnderlineDecorator extends TextDecorator {
    public UnderlineDecorator(Text wrapped) { super(wrapped); }

    @Override
    public String render() {
        return "<u>" + super.render() + "</u>";
    }
}

class Main {
    public static void main(String[] args) {
        Text plain = new PlainText("Hello World");
        System.out.println(plain.render());
        // Output: Hello World

        Text bold = new BoldDecorator(new PlainText("Hello World"));
        System.out.println(bold.render());
        // Output: <b>Hello World</b>

        // Bold + Italic + Underline stacked
        Text fancy = new UnderlineDecorator(
                         new ItalicDecorator(
                             new BoldDecorator(
                                 new PlainText("Hello World"))));
        System.out.println(fancy.render());
        // Output: <u><i><b>Hello World</b></i></u>
    }
}
```

---

## Example 4 — Java I/O Streams (How Java Itself Uses Decorator)

**Trigger:** This is actually the most famous real-world use of the Decorator pattern — it is baked into Java's own standard library. Every time you use `BufferedReader`, you are using a Decorator.

```java
// Java's InputStream hierarchy IS the Decorator pattern.
// InputStream is the Component interface.
// FileInputStream is the Concrete Component (reads raw bytes from a file).
// BufferedInputStream is a Decorator (adds buffering — faster reads).
// DataInputStream is another Decorator (adds reading ints, longs, booleans).

import java.io.*;

class IODecoratorDemo {
    public static void main(String[] args) throws Exception {
        // Raw file reading — slow, byte by byte (the base component)
        InputStream raw = new FileInputStream("data.txt");

        // Wrap with buffering (Decorator 1) — reads in large chunks, much faster
        InputStream buffered = new BufferedInputStream(raw);

        // Wrap with data types (Decorator 2) — lets you read int, double, etc.
        DataInputStream typed = new DataInputStream(buffered);

        // The call chain when you read:
        // typed.readInt() → asks buffered → buffered asks raw → raw reads from disk
        // Each decorator adds one responsibility, stacked on the previous.

        // This is EXACTLY the Decorator pattern:
        //   new DataInputStream(           <- Decorator 2
        //       new BufferedInputStream(   <- Decorator 1
        //           new FileInputStream("data.txt")  <- Base Component
        //       )
        //   )
    }
}
```

**Key insight:** Java's designers faced the exact same "class explosion" problem. A naive approach would have needed `BufferedFileInputStream`, `DataFileInputStream`, `BufferedDataFileInputStream`, etc. Instead, they used Decorator — you compose the features you need at runtime.

---

## Example 5 — E-Commerce Pricing System

**Trigger:** Your store sells products. Some products have a discount applied. Some have tax added. Some have both. Sometimes there's a loyalty bonus on top. These rules change per order, per user, per region.

```java
// The pricing contract
interface PriceCalculator {
    double getPrice();
}

// The base price — what the product actually costs
class BasePrice implements PriceCalculator {
    private double price;

    public BasePrice(double price) {
        this.price = price;
    }

    @Override
    public double getPrice() {
        return price;
    }
}

// Base decorator
abstract class PriceDecorator implements PriceCalculator {
    protected PriceCalculator wrapped;

    public PriceDecorator(PriceCalculator wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public double getPrice() {
        return wrapped.getPrice();
    }
}

// Adds 18% GST on top of whatever the wrapped price is
class GSTDecorator extends PriceDecorator {
    public GSTDecorator(PriceCalculator wrapped) { super(wrapped); }

    @Override
    public double getPrice() {
        double base = super.getPrice();
        double gst = base * 0.18;
        System.out.println("  + GST (18%): " + gst);
        return base + gst;
    }
}

// Subtracts a flat discount
class DiscountDecorator extends PriceDecorator {
    private double discountAmount;

    public DiscountDecorator(PriceCalculator wrapped, double discountAmount) {
        super(wrapped);
        this.discountAmount = discountAmount;
    }

    @Override
    public double getPrice() {
        double base = super.getPrice();
        System.out.println("  - Discount: " + discountAmount);
        return base - discountAmount;
    }
}

// Adds shipping cost
class ShippingDecorator extends PriceDecorator {
    private double shippingCost;

    public ShippingDecorator(PriceCalculator wrapped, double shippingCost) {
        super(wrapped);
        this.shippingCost = shippingCost;
    }

    @Override
    public double getPrice() {
        double base = super.getPrice();
        System.out.println("  + Shipping: " + shippingCost);
        return base + shippingCost;
    }
}

class Main {
    public static void main(String[] args) {
        System.out.println("=== Order 1: Base price only ===");
        PriceCalculator simple = new BasePrice(1000.0);
        System.out.println("Base: 1000.0");
        System.out.println("Final price: " + simple.getPrice());

        System.out.println("\n=== Order 2: With GST ===");
        PriceCalculator withGST = new GSTDecorator(new BasePrice(1000.0));
        System.out.println("Base: 1000.0");
        System.out.println("Final price: " + withGST.getPrice());

        System.out.println("\n=== Order 3: With discount + GST + shipping ===");
        PriceCalculator fullOrder = new ShippingDecorator(
                                       new GSTDecorator(
                                           new DiscountDecorator(
                                               new BasePrice(1000.0), 100.0
                                           )
                                       ), 50.0
                                   );
        System.out.println("Base: 1000.0");
        System.out.println("Final price: " + fullOrder.getPrice());
    }
}

// Output:
// === Order 1: Base price only ===
// Base: 1000.0
// Final price: 1000.0
//
// === Order 2: With GST ===
// Base: 1000.0
//   + GST (18%): 180.0
// Final price: 1180.0
//
// === Order 3: With discount + GST + shipping ===
// Base: 1000.0
//   - Discount: 100.0
//   + GST (18%): 162.0
//   + Shipping: 50.0
// Final price: 1112.0
```

---

## How to Spot When to Use This Pattern

| Signal / Code Smell | Why the Decorator Pattern Helps |
|---|---|
| You are creating subclasses for every combination of features | Decorators let you mix-and-match features at runtime — no more class explosion |
| You want to add behaviour to ONE specific object, not ALL objects of that class | Decorator wraps one instance; subclassing changes all instances of the subclass |
| You need to add/remove responsibilities at runtime (user preferences, feature flags) | Stack or unstack decorators dynamically — you cannot do this with inheritance |
| You have a chain of optional processing steps (middleware, filters, pipelines) | Each step is a decorator; chain them in any order |
| You find yourself copying the same method logic into many sibling subclasses | That shared logic belongs in a decorator, not duplicated in subclasses |

---

## Common Mistakes Beginners Make

- **Forgetting to delegate to `wrapped`:** Every decorator must call `super.operate()` (or `wrapped.operate()`) to pass the call down the chain. If you forget this, the base object never runs and you silently lose behaviour.

- **Making the decorator depend on a specific concrete class:** The decorator must hold a reference to the **interface** (`Notifier`, `Text`, `PriceCalculator`), not to `BaseNotifier` or `PlainText`. If you depend on the concrete class, you can only wrap that one class — not other decorators.

- **Confusing Decorator with Proxy:** Both wrap an object, but their intent differs. A **Proxy** controls access to the object (security, caching, lazy loading). A **Decorator** adds new behaviour. The structure looks almost identical — the difference is the purpose.

- **Stacking decorators in the wrong order:** The order matters. `new GSTDecorator(new DiscountDecorator(base))` gives a different result than `new DiscountDecorator(new GSTDecorator(base))` — the discount is applied before or after tax. Always think about which order makes business sense.

---

## Quick Reference

| Aspect | Detail |
|---|---|
| **Intent** | Add behaviour to an individual object at runtime without modifying its class |
| **Key Roles** | `Component` (interface), `ConcreteComponent` (real object), `BaseDecorator` (holds reference), `ConcreteDecorator` (adds one feature) |
| **Java Keyword Hints** | `implements` the same interface as the wrapped object; constructor takes the interface type |
| **When to Use** | Combinations of optional features; runtime add/remove of behaviour; open-closed extension |
| **When NOT to Use** | When the wrapping order does not matter AND features are always used together (just put them in one class); when you only have one feature to add (a subclass is simpler) |
| **Real Java Examples** | `BufferedInputStream`, `GZIPOutputStream`, `Collections.unmodifiableList()` |
| **Difference from Inheritance** | Inheritance is compile-time and applies to all instances of the subclass. Decorator is runtime and applies to one specific object. |
