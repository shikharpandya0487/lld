# Behavioral Design Patterns — Teaching Objects How to Talk to Each Other

---

## The Analogy

Think of a busy restaurant. The **chef** cooks. The **waiter** takes orders. The **cashier** handles payment. The **manager** coordinates if things go wrong. None of them do each other's jobs, but they all communicate in structured, predictable ways. Every time a customer places an order, a specific sequence of conversations happens — the waiter notifies the kitchen, the kitchen notifies the waiter when the food is ready, and so on.

Behavioral patterns are exactly this: they define **how objects communicate and coordinate with each other**. They answer the question: "Who tells who to do what, and how?"

---

## What Problem Do Behavioral Patterns Solve?

Without any structure, every object in your program starts calling every other object directly. Object A calls B, B calls C, C calls A back, and now you have a tangled web that nobody can follow. When requirements change — say, you need to add a new step to a workflow — you have to hunt through ten different files to figure out where to make the change.

Behavioral patterns solve this by giving each communication scenario a name, a structure, and a clear set of responsibilities. They make workflows readable, flexible, and safe to extend.

---

## What ARE Behavioral Patterns?

**Behavioral patterns** are a category of design patterns that deal with **how objects interact and distribute responsibility** among themselves.

There are two other categories you may have seen:
- **Creational patterns** — deal with *how objects are created* (Factory, Builder, Singleton)
- **Structural patterns** — deal with *how objects are composed* (Adapter, Bridge, Composite, Decorator, Facade)
- **Behavioral patterns** — deal with *how objects communicate and collaborate*

The 11 core behavioral patterns from the Gang of Four book are:

| Pattern | One-Line Purpose |
|---|---|
| **Strategy** | Swap algorithms at runtime without changing the caller |
| **Observer** | Notify many objects when one object changes state |
| **Command** | Package a request as an object to support undo/redo/queuing |
| **Template Method** | Define the skeleton of an algorithm; let subclasses fill in steps |
| **Iterator** | Traverse a collection without knowing its internal structure |
| **Chain of Responsibility** | Pass a request along a chain until something handles it |
| **State** | Change an object's behavior when its internal state changes |
| **Mediator** | Centralize communication between many objects |
| **Memento** | Save and restore an object's state (undo) |
| **Visitor** | Add new operations to objects without modifying them |
| **Interpreter** | Define a grammar and interpret sentences in that grammar |

We will cover the 8 most commonly used ones below — each with a real-world analogy, the problem it solves, and a Java example.

---

## 1. Strategy — "Swap the Algorithm"

### Analogy
A GPS app offers you three routes: fastest, shortest, and avoid tolls. The app does not change — only the routing algorithm does. Each algorithm is a **strategy** the app can use interchangeably.

### What Problem It Solves
You have a class that needs to do the same kind of work (e.g., sort, pay, log) but in different ways. Without Strategy, you write a giant `if-else` inside that class. Every new way of doing the work means editing that class again — fragile and risky.

### BAD Example — Without Strategy

```java
// BAD — the Checkout class must change every time a new payment method is added
class Checkout {
    public void pay(String method, double amount) {
        if (method.equals("credit")) {
            System.out.println("Paid ₹" + amount + " via Credit Card");
        } else if (method.equals("upi")) {
            System.out.println("Paid ₹" + amount + " via UPI");
        } else if (method.equals("paypal")) {
            System.out.println("Paid ₹" + amount + " via PayPal");
        }
        // Adding "crypto" means editing THIS method. Risky every time.
    }
}
```

### GOOD Example — With Strategy

```java
interface PaymentStrategy {
    void pay(double amount);
}

class CreditCardPayment implements PaymentStrategy {
    public void pay(double amount) { System.out.println("Paid ₹" + amount + " via Credit Card"); }
}

class UpiPayment implements PaymentStrategy {
    public void pay(double amount) { System.out.println("Paid ₹" + amount + " via UPI"); }
}

class Checkout {
    private PaymentStrategy strategy;
    public Checkout(PaymentStrategy strategy) { this.strategy = strategy; }
    public void pay(double amount) { strategy.pay(amount); } // no if-else
}

// Usage
class StrategyMain {
    public static void main(String[] args) {
        new Checkout(new UpiPayment()).pay(499);
        // Output: Paid ₹499.0 via UPI
    }
}
```

**When to reach for it:** You see 3+ `if-else` branches picking a behavior, and new variants are expected over time.

---

## 2. Observer — "Notify Everyone Who Cares"

### Analogy
You subscribe to a YouTube channel. Every time the creator uploads a video, you get a notification. You did not ask the creator to remember you personally — you **subscribed** to a list. The creator broadcasts once; everyone on the list receives it. The creator = **Subject**. You = **Observer**.

### What Problem It Solves
One object changes state (a product goes back in stock, a new message arrives, a sensor reads a new value). Many other objects need to react. Without Observer, the source object must know about every single object that cares — tightly coupling them all together and making the list impossible to maintain.

### BAD Example — Without Observer

```java
// BAD — the Stock class is directly coupled to every system that cares about it
class Stock {
    private int quantity;
    private EmailNotifier emailNotifier = new EmailNotifier();
    private SmsNotifier smsNotifier = new SmsNotifier();
    private AppNotifier appNotifier = new AppNotifier();

    public void restock(int qty) {
        this.quantity += qty;
        // If you add a "WhatsApp notifier", you must edit THIS class
        emailNotifier.notify("Stock updated");
        smsNotifier.notify("Stock updated");
        appNotifier.notify("Stock updated");
    }
}
```

### GOOD Example — With Observer

```java
import java.util.ArrayList;
import java.util.List;

// Observer contract: anything that wants notifications must implement this
interface StockObserver {
    void update(String productName, int newQuantity);
}

// Subject: holds the list of observers and notifies them
class StockTracker {
    private List<StockObserver> observers = new ArrayList<>();
    private int quantity;
    private String productName;

    public StockTracker(String productName) { this.productName = productName; }

    public void subscribe(StockObserver observer) { observers.add(observer); }
    public void unsubscribe(StockObserver observer) { observers.remove(observer); }

    public void restock(int qty) {
        this.quantity += qty;
        notifyAll(qty); // broadcasts to all registered observers
    }

    private void notifyAll(int qty) {
        for (StockObserver observer : observers) {
            observer.update(productName, qty);
        }
    }
}

// Concrete observers — each reacts in its own way
class EmailNotifier implements StockObserver {
    public void update(String product, int qty) {
        System.out.println("Email: " + product + " restocked with " + qty + " units.");
    }
}

class SmsNotifier implements StockObserver {
    public void update(String product, int qty) {
        System.out.println("SMS: " + product + " is available again!");
    }
}

class ObserverMain {
    public static void main(String[] args) {
        StockTracker tracker = new StockTracker("iPhone 15");
        tracker.subscribe(new EmailNotifier());
        tracker.subscribe(new SmsNotifier());
        tracker.restock(50);
        // Output:
        // Email: iPhone 15 restocked with 50 units.
        // SMS: iPhone 15 is available again!
    }
}
```

**When to reach for it:** One object's change must trigger reactions in multiple other objects, and you do not want the source to know who those other objects are.

---

## 3. Command — "Package a Request as an Object"

### Analogy
A TV remote control has buttons: Volume Up, Volume Down, Mute. Each button *represents an action*, not the TV itself. You can queue button presses, undo them (unmute), and even program new macros. The button = **Command object**. The TV = **Receiver**. You = **Invoker**.

### What Problem It Solves
You want to issue requests without knowing who will execute them, or you need to support undo, redo, or queuing of operations. Without Command, the code issuing the request is directly coupled to the code executing it — making undo and queuing nearly impossible.

### BAD Example — Without Command

```java
// BAD — the UI button is directly tied to the action; undo is impossible
class TextEditor {
    private StringBuilder text = new StringBuilder();

    public void onBoldButtonClick() {
        text.append("<b>"); // no way to undo this — it's already done
        System.out.println("Bold applied: " + text);
    }
    // Adding "undo" means ripping apart this entire class
}
```

### GOOD Example — With Command

```java
import java.util.Stack;

// Command interface: every action must implement execute() and undo()
interface TextCommand {
    void execute();
    void undo();
}

// Receiver: the object that actually does the work
class Document {
    private StringBuilder content = new StringBuilder();

    public void append(String text) { content.append(text); }
    public void removeLast(int count) { content.delete(content.length() - count, content.length()); }
    public String getContent() { return content.toString(); }
}

// Concrete command: knows how to do AND undo a specific action
class BoldCommand implements TextCommand {
    private Document doc;
    private static final String BOLD_TAG = "<b>";

    public BoldCommand(Document doc) { this.doc = doc; }

    public void execute() {
        doc.append(BOLD_TAG);
        System.out.println("Executed: " + doc.getContent());
    }

    public void undo() {
        doc.removeLast(BOLD_TAG.length());
        System.out.println("Undone:   " + doc.getContent());
    }
}

// Invoker: stores commands and triggers them (also manages undo history)
class EditorInvoker {
    private Stack<TextCommand> history = new Stack<>();

    public void run(TextCommand command) {
        command.execute();
        history.push(command); // save to history for undo
    }

    public void undo() {
        if (!history.isEmpty()) {
            history.pop().undo();
        }
    }
}

class CommandMain {
    public static void main(String[] args) {
        Document doc = new Document();
        EditorInvoker editor = new EditorInvoker();

        editor.run(new BoldCommand(doc));
        // Output: Executed: <b>

        editor.run(new BoldCommand(doc));
        // Output: Executed: <b><b>

        editor.undo();
        // Output: Undone:   <b>
    }
}
```

**When to reach for it:** You need undo/redo, request queuing, or want to decouple "who asks" from "who does."

---

## 4. Template Method — "The Recipe with Blank Steps"

### Analogy
A recipe book gives you a recipe for cake: (1) mix ingredients, (2) bake, (3) decorate. The *sequence* is fixed. But the decoration step is left blank — you can pipe frosting, add sprinkles, or place fruit. The recipe (superclass) defines the flow; you (subclass) fill in the blank step.

### What Problem It Solves
You have multiple classes that do the same overall process, but differ in just a few specific steps. Without Template Method, you copy-paste the whole process into every class and then maintain duplicates. Adding a new step to the process means editing every copy.

### BAD Example — Without Template Method

```java
// BAD — both classes copy-paste the same process; any change must be made in both
class PdfReportGenerator {
    public void generate() {
        System.out.println("Fetching data...");
        System.out.println("Parsing data as PDF format"); // only this line differs
        System.out.println("Saving report...");
        System.out.println("Sending email...");
    }
}

class CsvReportGenerator {
    public void generate() {
        System.out.println("Fetching data...");
        System.out.println("Parsing data as CSV format"); // only this line differs
        System.out.println("Saving report...");
        System.out.println("Sending email...");
    }
}
// What if you add a "notify Slack" step? You must edit BOTH classes.
```

### GOOD Example — With Template Method

```java
// The abstract class defines the fixed skeleton
abstract class ReportGenerator {

    // Template method — the fixed sequence, marked final so subclasses cannot reorder it
    public final void generate() {
        fetchData();
        parseData();   // <-- the blank step subclasses must fill in
        saveReport();
        sendEmail();
    }

    private void fetchData()  { System.out.println("Fetching data..."); }
    private void saveReport() { System.out.println("Saving report..."); }
    private void sendEmail()  { System.out.println("Sending report via email..."); }

    // The one step that varies — subclasses fill this in
    protected abstract void parseData();
}

class PdfReportGenerator extends ReportGenerator {
    protected void parseData() { System.out.println("Parsing data as PDF format"); }
}

class CsvReportGenerator extends ReportGenerator {
    protected void parseData() { System.out.println("Parsing data as CSV format"); }
}

class TemplateMain {
    public static void main(String[] args) {
        new PdfReportGenerator().generate();
        // Output:
        // Fetching data...
        // Parsing data as PDF format
        // Saving report...
        // Sending report via email...

        System.out.println("---");

        new CsvReportGenerator().generate();
        // Output:
        // Fetching data...
        // Parsing data as CSV format
        // Saving report...
        // Sending report via email...
    }
}
```

**When to reach for it:** Multiple classes share the same overall process but differ in specific steps. The sequence itself should not change.

---

## 5. Chain of Responsibility — "Pass It Down the Line"

### Analogy
When you submit a loan application at a bank, it first goes to a junior officer. If the amount is within their authority, they approve it. If not, it goes to the senior officer. If that is still not enough, it goes to the branch manager. The request travels down a **chain** until someone can handle it.

### What Problem It Solves
You have a request that might be handled by one of several handlers, but you do not know which one at the time of the request. Without Chain of Responsibility, the sender must know about every possible handler and call them in order — tight coupling and a mess when the chain changes.

### BAD Example — Without Chain of Responsibility

```java
// BAD — the Logger must decide which level handles what; adding "TRACE" means editing this
class Logger {
    public void log(String level, String message) {
        if (level.equals("INFO")) {
            System.out.println("[INFO] " + message);
        } else if (level.equals("WARN")) {
            System.out.println("[WARN] " + message);
            System.out.println("[WARN] Sending warning alert...");
        } else if (level.equals("ERROR")) {
            System.out.println("[ERROR] " + message);
            System.out.println("[ERROR] Sending error alert...");
            System.out.println("[ERROR] Writing to error log file...");
        }
    }
}
```

### GOOD Example — With Chain of Responsibility

```java
// Each handler knows what it can handle, and has a reference to the next in line
abstract class LogHandler {
    protected LogHandler next;

    public LogHandler setNext(LogHandler next) {
        this.next = next;
        return next; // allows chaining: info.setNext(warn).setNext(error)
    }

    public abstract void handle(String level, String message);

    protected void passToNext(String level, String message) {
        if (next != null) next.handle(level, message);
    }
}

class InfoHandler extends LogHandler {
    public void handle(String level, String message) {
        if (level.equals("INFO")) {
            System.out.println("[INFO] " + message);
        } else {
            passToNext(level, message); // not mine — pass it along
        }
    }
}

class WarnHandler extends LogHandler {
    public void handle(String level, String message) {
        if (level.equals("WARN")) {
            System.out.println("[WARN] " + message);
            System.out.println("[WARN] Sending warning alert...");
        } else {
            passToNext(level, message);
        }
    }
}

class ErrorHandler extends LogHandler {
    public void handle(String level, String message) {
        if (level.equals("ERROR")) {
            System.out.println("[ERROR] " + message);
            System.out.println("[ERROR] Sending error alert...");
            System.out.println("[ERROR] Writing to error log file...");
        } else {
            passToNext(level, message);
        }
    }
}

class ChainMain {
    public static void main(String[] args) {
        // Build the chain: INFO → WARN → ERROR
        InfoHandler info = new InfoHandler();
        info.setNext(new WarnHandler()).setNext(new ErrorHandler());

        info.handle("INFO", "User logged in");
        // Output: [INFO] User logged in

        info.handle("ERROR", "Database crashed");
        // Output: [ERROR] Database crashed
        //         [ERROR] Sending error alert...
        //         [ERROR] Writing to error log file...
    }
}
```

**When to reach for it:** A request may be handled by one of many handlers and the right handler is not known at compile time. Also useful for building middleware pipelines (HTTP filters, validation chains).

---

## 6. State — "Change Behavior When State Changes"

### Analogy
A traffic light is always the same physical object, but it behaves differently depending on its current state: Red means stop, Green means go, Yellow means slow down. The light does not become a different object when it changes — its **state** changes, and its behavior follows. In code, the `TrafficLight` class stays the same; the behavior is delegated to the current state object.

### What Problem It Solves
An object behaves differently based on its internal condition. Without State, you write huge `if (state == X) ... else if (state == Y) ...` blocks inside every method. Every new state means editing every method. Every method becomes unreadable.

### BAD Example — Without State

```java
// BAD — every method must check the current state string; adding "BLINKING" means editing all 3
class TrafficLight {
    private String state = "RED";

    public void pressButton() {
        if (state.equals("RED"))    state = "GREEN";
        else if (state.equals("GREEN")) state = "YELLOW";
        else if (state.equals("YELLOW")) state = "RED";
    }

    public void displaySignal() {
        if (state.equals("RED"))    System.out.println("STOP");
        else if (state.equals("GREEN")) System.out.println("GO");
        else if (state.equals("YELLOW")) System.out.println("SLOW DOWN");
    }
}
```

### GOOD Example — With State

```java
// Each state is its own class — it owns the behavior for that state
interface LightState {
    void displaySignal();
    LightState next(); // returns the next state in the cycle
}

class RedState implements LightState {
    public void displaySignal() { System.out.println("RED — STOP"); }
    public LightState next() { return new GreenState(); }
}

class GreenState implements LightState {
    public void displaySignal() { System.out.println("GREEN — GO"); }
    public LightState next() { return new YellowState(); }
}

class YellowState implements LightState {
    public void displaySignal() { System.out.println("YELLOW — SLOW DOWN"); }
    public LightState next() { return new RedState(); }
}

// Context: holds the current state and delegates all behavior to it
class TrafficLight {
    private LightState currentState = new RedState();

    public void change() {
        currentState = currentState.next(); // transition to next state
    }

    public void display() {
        currentState.displaySignal(); // delegate to current state
    }
}

class StateMain {
    public static void main(String[] args) {
        TrafficLight light = new TrafficLight();
        light.display(); // RED — STOP
        light.change();
        light.display(); // GREEN — GO
        light.change();
        light.display(); // YELLOW — SLOW DOWN
        light.change();
        light.display(); // RED — STOP
    }
}
```

**When to reach for it:** An object's behavior changes dramatically based on its internal condition, and you have many `if (state == ...)` checks scattered across multiple methods.

---

## 7. Iterator — "Traverse Without Knowing the Insides"

### Analogy
You have a playlist of songs. You press "Next" to move to the next song. You do not care whether the playlist is stored as an array, a linked list, or a database query — the **Next button** (the iterator) gives you one song at a time in order. The playlist is the **collection**; the iterator is the **cursor** that moves through it.

### What Problem It Solves
You have different types of collections (arrays, trees, custom lists) and you want code that can loop through all of them the same way, without needing to know each collection's internal structure.

### BAD Example — Without Iterator

```java
// BAD — the client must know HOW each collection stores its data to loop through it
class SongPlaylist {
    String[] songs = {"Song A", "Song B", "Song C"};
}

class PlaylistMain {
    public static void main(String[] args) {
        SongPlaylist playlist = new SongPlaylist();
        // Client must know it's an array and use index-based access
        for (int i = 0; i < playlist.songs.length; i++) {
            System.out.println("Playing: " + playlist.songs[i]);
        }
        // If SongPlaylist changes to a LinkedList internally, this loop breaks.
    }
}
```

### GOOD Example — With Iterator

```java
// Iterator contract: a standard way to move through any collection
interface MusicIterator {
    boolean hasNext();
    String next();
}

// The collection creates its own iterator — client never touches the internals
class SongPlaylist {
    private String[] songs;
    private int count;

    public SongPlaylist(int size) { songs = new String[size]; }

    public void addSong(String song) { songs[count++] = song; }

    public MusicIterator createIterator() {
        return new PlaylistIterator();
    }

    // Inner class: knows the internal structure, hides it from outside
    private class PlaylistIterator implements MusicIterator {
        private int index = 0;
        public boolean hasNext() { return index < count; }
        public String next() { return songs[index++]; }
    }
}

class IteratorMain {
    public static void main(String[] args) {
        SongPlaylist playlist = new SongPlaylist(5);
        playlist.addSong("Bohemian Rhapsody");
        playlist.addSong("Hotel California");
        playlist.addSong("Stairway to Heaven");

        MusicIterator iterator = playlist.createIterator();
        while (iterator.hasNext()) {
            System.out.println("Playing: " + iterator.next());
        }
        // Output:
        // Playing: Bohemian Rhapsody
        // Playing: Hotel California
        // Playing: Stairway to Heaven
    }
}
```

**When to reach for it:** You want to give client code a uniform way to loop through different kinds of collections without exposing their internal data structures.

---

## 8. Memento — "Save and Restore State"

### Analogy
You are editing a document in Microsoft Word and you press Ctrl+Z. Word does not rewrite the document from scratch — it had saved a **snapshot** of the document before your last change and it restores that snapshot. The snapshot is the **Memento**. The document is the **Originator**. Word's undo history is the **Caretaker**.

### What Problem It Solves
You need to implement undo functionality, checkpoints, or rollback. Without Memento, the object must expose all its internal data for the caller to snapshot manually — breaking **encapsulation** (the principle that an object's internals should stay private).

### BAD Example — Without Memento

```java
// BAD — to support undo, the client must expose and store internal fields manually
class TextEditor {
    public String text = ""; // forced public so the caller can save it
}

class BadUndoMain {
    public static void main(String[] args) {
        TextEditor editor = new TextEditor();
        String backup = editor.text; // client stores the state manually
        editor.text = "Hello World";
        System.out.println("Current: " + editor.text);
        editor.text = backup;        // restore — but now client knows editor's internals
        System.out.println("After undo: " + editor.text);
        // This breaks encapsulation: the editor's private data is exposed.
    }
}
```

### GOOD Example — With Memento

```java
// Memento: a snapshot of the editor's state — opaque to the outside world
class EditorMemento {
    private final String savedText; // private — only the editor can read this
    public EditorMemento(String text) { this.savedText = text; }
    public String getSavedText() { return savedText; }
}

// Originator: creates and restores its own mementos
class TextEditor {
    private String text = "";

    public void type(String words) { text += words; }
    public String getText() { return text; }

    public EditorMemento save() {
        return new EditorMemento(text); // packages current state into a memento
    }

    public void restore(EditorMemento memento) {
        text = memento.getSavedText(); // restores from the snapshot
    }
}

// Caretaker: holds the history of mementos (does NOT look inside them)
import java.util.Stack;

class UndoManager {
    private Stack<EditorMemento> history = new Stack<>();

    public void backup(EditorMemento memento) { history.push(memento); }

    public EditorMemento undo() {
        if (!history.isEmpty()) return history.pop();
        return null;
    }
}

class MementoMain {
    public static void main(String[] args) {
        TextEditor editor = new TextEditor();
        UndoManager undoManager = new UndoManager();

        undoManager.backup(editor.save()); // save empty state
        editor.type("Hello ");
        System.out.println("After typing: " + editor.getText());

        undoManager.backup(editor.save()); // save "Hello "
        editor.type("World");
        System.out.println("After typing: " + editor.getText());

        editor.restore(undoManager.undo()); // undo back to "Hello "
        System.out.println("After undo:   " + editor.getText());

        editor.restore(undoManager.undo()); // undo back to ""
        System.out.println("After undo:   " + editor.getText());

        // Output:
        // After typing: Hello
        // After typing: Hello World
        // After undo:   Hello
        // After undo:
    }
}
```

**When to reach for it:** You need undo, rollback, or checkpoint functionality without exposing an object's internals.

---

## How the Patterns Relate to Each Other

| If you need to... | Use this pattern |
|---|---|
| Choose between algorithms at runtime | **Strategy** |
| Notify many objects of a state change | **Observer** |
| Package and undo a request | **Command** |
| Reuse the same algorithm structure with different steps | **Template Method** |
| Loop through a collection uniformly | **Iterator** |
| Route a request through a processing pipeline | **Chain of Responsibility** |
| Change behavior when internal state changes | **State** |
| Save and restore state for undo | **Memento** |

---

## Common Beginner Confusions

| "These look similar..." | The real difference |
|---|---|
| **Strategy vs State** | Strategy is chosen by the *caller* and usually stays constant during a request. State transitions itself automatically based on events. |
| **Command vs Strategy** | Strategy swaps *how* to do something (algorithm). Command packages *what* to do (request) — including support for undo and queuing. |
| **Observer vs Chain of Responsibility** | Observer broadcasts to *all* listeners. Chain of Responsibility passes a request down a line until *one* handler accepts it. |
| **Template Method vs Strategy** | Template Method uses inheritance (subclass fills in steps). Strategy uses composition (you inject a different object). |

---

## Quick Reference — All 8 Behavioral Patterns

| Pattern | Key Roles | Core Mechanism | Typical Java Signal |
|---|---|---|---|
| **Strategy** | Strategy (interface), ConcreteStrategy, Context | Inject algorithm via constructor/setter | `if-else` chain choosing behavior |
| **Observer** | Subject, Observer (interface), ConcreteObserver | Subscribe/unsubscribe + broadcast | One object → many reactions |
| **Command** | Command (interface), ConcreteCommand, Invoker, Receiver | Execute + Undo packaged together | Need undo/redo or queuing |
| **Template Method** | Abstract class with final template method, Subclasses | `abstract` hook methods | Copy-pasted workflows with one step varying |
| **Chain of Responsibility** | Handler (abstract), ConcreteHandlers linked as a chain | Each handler either handles or passes | Multi-level approval, middleware pipeline |
| **State** | State (interface), ConcreteStates, Context | Context delegates to current state | Many `if (state == ...)` blocks |
| **Iterator** | Iterator (interface), ConcreteIterator, Collection | `hasNext()` + `next()` cursor | Looping over custom collections |
| **Memento** | Originator, Memento, Caretaker | Snapshot saved externally, restored on demand | Undo without exposing internals |
