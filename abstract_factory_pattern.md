# Abstract Factory Pattern

## What Is It?

The **Abstract Factory** is a creational design pattern that provides an interface for creating **families of related or dependent objects** without specifying their concrete classes.

Think of it as a "factory of factories." Where a simple Factory Method creates one type of object, an Abstract Factory creates an entire suite of related objects that are designed to work together.

---

## Real-Life Examples

### 1. UI Theme Kits (Light / Dark Mode)
A design system ships two themes — Light and Dark. Each theme must produce a consistent set of widgets: `Button`, `Checkbox`, `TextInput`. You cannot mix a Dark button with a Light checkbox — they must come from the same family. An Abstract Factory (`ThemeFactory`) ensures every widget is created from the same theme.

### 2. Cross-Platform GUI Frameworks
A desktop app targets both Windows and macOS. Each platform has its own native `Window`, `Dialog`, and `ScrollBar`. The factory (`OSFactory`) picks the right family at startup so the rest of the app never has to know which OS it is running on.

### 3. Database Driver Ecosystems
An ORM needs to work with MySQL and PostgreSQL. Each vendor provides its own `Connection`, `QueryBuilder`, and `Transaction` objects. A `DatabaseFactory` returns the right family so the business logic never touches vendor-specific code.

### 4. Vehicle Manufacturing Lines
A car plant can produce **Sedan** or **SUV** families. Each family needs an `Engine`, `Chassis`, and `Transmission` that are engineered to work together. The `VehicleFactory` guarantees no mismatched parts.

---

## Code Example: Cross-Platform UI Widgets

We will build a mini UI framework that supports two themes — **Light** and **Dark** — each producing a `Button` and a `Checkbox`.

### Folder Structure (conceptual)

```
src/
  abstractfactory/
    Button.java              ← Abstract product
    Checkbox.java            ← Abstract product
    UIFactory.java           ← Abstract factory interface
    LightButton.java         ← Concrete Light product
    LightCheckbox.java       ← Concrete Light product
    LightThemeFactory.java   ← Concrete Light factory
    DarkButton.java          ← Concrete Dark product
    DarkCheckbox.java        ← Concrete Dark product
    DarkThemeFactory.java    ← Concrete Dark factory
    Application.java         ← Client code
    Main.java                ← Entry point / factory selector
```

---

### Step 1 — Abstract Products & Abstract Factory

#### `Button.java`

```java
// Abstract Product — every Button variant must honour this contract
public interface Button {
    String render();    // Describes how the button looks
    String onClick();   // Describes the button's click feedback
}
```

#### `Checkbox.java`

```java
// Abstract Product — every Checkbox variant must honour this contract
public interface Checkbox {
    String render();    // Describes how the checkbox looks
    String toggle();    // Describes the toggle behaviour
}
```

#### `UIFactory.java`

```java
/**
 * Abstract Factory — declares one creation method per product type.
 * Concrete factories implement this interface to return products
 * that belong to the same theme/family, guaranteeing visual consistency.
 */
public interface UIFactory {
    Button   createButton();
    Checkbox createCheckbox();
}
```

**What is happening here?**
- `Button` and `Checkbox` are **abstract products** — they define the contract every concrete widget must honour.
- `UIFactory` is the **abstract factory** — it declares one creation method per product type. It never instantiates anything itself.

---

### Step 2 — Light Theme Family

#### `LightButton.java`

```java
public class LightButton implements Button {

    @Override
    public String render() {
        return "[Light Button] White background, dark text border";
    }

    @Override
    public String onClick() {
        return "[Light Button] Ripple effect in light grey";
    }
}
```

#### `LightCheckbox.java`

```java
public class LightCheckbox implements Checkbox {

    @Override
    public String render() {
        return "[Light Checkbox] White box with thin dark border";
    }

    @Override
    public String toggle() {
        return "[Light Checkbox] Dark checkmark appears inside white box";
    }
}
```

#### `LightThemeFactory.java`

```java
// Concrete Factory — produces the entire Light widget family
public class LightThemeFactory implements UIFactory {

    @Override
    public Button createButton() {
        return new LightButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new LightCheckbox();
    }
}
```

---

### Step 3 — Dark Theme Family

#### `DarkButton.java`

```java
public class DarkButton implements Button {

    @Override
    public String render() {
        return "[Dark Button] Charcoal background, white glowing border";
    }

    @Override
    public String onClick() {
        return "[Dark Button] Ripple effect in electric blue";
    }
}
```

#### `DarkCheckbox.java`

```java
public class DarkCheckbox implements Checkbox {

    @Override
    public String render() {
        return "[Dark Checkbox] Dark box with neon border";
    }

    @Override
    public String toggle() {
        return "[Dark Checkbox] White checkmark glows on dark background";
    }
}
```

#### `DarkThemeFactory.java`

```java
// Concrete Factory — produces the entire Dark widget family
public class DarkThemeFactory implements UIFactory {

    @Override
    public Button createButton() {
        return new DarkButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new DarkCheckbox();
    }
}
```

---

### Step 4 — Client / Application (`Application.java`)

```java
/**
 * Client code. Knows NOTHING about LightButton or DarkCheckbox.
 * It only talks to the abstract interfaces: UIFactory, Button, Checkbox.
 * Swap the factory at construction time and the whole app changes theme.
 */
public class Application {

    private final Button   button;
    private final Checkbox checkbox;

    // The factory is injected — this class never calls a concrete constructor.
    public Application(UIFactory factory) {
        this.button   = factory.createButton();
        this.checkbox = factory.createCheckbox();
    }

    public void renderUI() {
        System.out.println(button.render());
        System.out.println(checkbox.render());
    }

    public void interact() {
        System.out.println(button.onClick());
        System.out.println(checkbox.toggle());
    }
}
```

**Key insight:** `Application` is completely decoupled from every concrete class. Its constructor receives a `UIFactory` — dependency injection in action.

---

### Step 5 — Entry Point (`Main.java`)

```java
/**
 * The ONE place in the entire codebase that references a concrete factory.
 * In a real app the choice comes from a config file, system property,
 * or environment variable — never from business logic.
 */
public class Main {

    private static UIFactory getFactory() {
        String theme = System.getenv("APP_THEME");   // e.g. export APP_THEME=dark
        if ("dark".equalsIgnoreCase(theme)) {
            return new DarkThemeFactory();
        }
        return new LightThemeFactory();              // default
    }

    public static void main(String[] args) {
        UIFactory   factory = getFactory();
        Application app     = new Application(factory);

        System.out.println("=== Rendering UI ===");
        app.renderUI();

        System.out.println("\n=== User Interacts ===");
        app.interact();
    }
}
```

**Sample output — Light theme (default / `APP_THEME=light`):**

```
=== Rendering UI ===
[Light Button] White background, dark text border
[Light Checkbox] White box with thin dark border

=== User Interacts ===
[Light Button] Ripple effect in light grey
[Light Checkbox] Dark checkmark appears inside white box
```

**Sample output — Dark theme (`APP_THEME=dark`):**

```
=== Rendering UI ===
[Dark Button] Charcoal background, white glowing border
[Dark Checkbox] Dark box with neon border

=== User Interacts ===
[Dark Button] Ripple effect in electric blue
[Dark Checkbox] White checkmark glows on dark background
```

---

## Detailed Code Walkthrough

```
┌─────────────────────────────────────────────────┐
│               <<interface>>                     │
│                 UIFactory                       │
│  + createButton()  : Button                    │
│  + createCheckbox(): Checkbox                  │
└──────────────┬──────────────────────────────────┘
               │ implements
   ┌───────────┴────────────┐
   │                        │
   ▼                        ▼
LightThemeFactory      DarkThemeFactory
  createButton()         createButton()
  createCheckbox()       createCheckbox()
   │                        │
   │ creates                │ creates
   ▼                        ▼
LightButton            DarkButton
LightCheckbox          DarkCheckbox
   │                        │
   └──────────┬─────────────┘
              │ both implement
   ┌──────────┴──────────────┐
   │  <<interface>> Button   │
   │  <<interface>> Checkbox │
   └─────────────────────────┘
                 ▲
                 │ depends on (only abstractions)
           Application
```

| Layer | Role |
|---|---|
| `UIFactory` | Abstract Factory — declares the "menu" of what can be created |
| `LightThemeFactory` / `DarkThemeFactory` | Concrete Factories — implement the menu for each family |
| `Button` / `Checkbox` | Abstract Products — define what a widget must do |
| `LightButton`, `DarkButton`, etc. | Concrete Products — the actual implementations |
| `Application` | Client — consumes only abstract types, never concrete ones |
| `Main.java` | Configuration Point — the one place that decides which family to use |

### Why is `Main.java` the only place with a concrete reference?
Because the factory choice is a **configuration decision**, not a business logic decision. The rest of the app never needs to know which theme is active. This is the pattern's central promise.

---

## Advantages

| # | Advantage | Explanation |
|---|---|---|
| 1 | **Product consistency** | All products from one factory are designed to work together. You can never accidentally mix a Dark button with a Light checkbox. |
| 2 | **Easy family swap** | Changing the entire product family is a single-line change in `Main.java`. No other code needs touching. |
| 3 | **Isolated concrete classes** | Client code (`Application`) never imports or references any concrete class. |
| 4 | **Adding a new family is low-risk** | To add a "High Contrast" theme, create `HighContrastButton`, `HighContrastCheckbox`, and `HighContrastThemeFactory`. Existing code is untouched. |
| 5 | **Promotes testability** | You can inject a `MockUIFactory` in tests that returns stub widgets, enabling fast unit tests without real UI. |

---

## Disadvantages

| # | Disadvantage | Explanation |
|---|---|---|
| 1 | **High class count** | Each new product type (e.g., adding `Slider`) requires a new interface + one concrete class *per family*. Three themes × five widget types = 15 concrete classes minimum. |
| 2 | **Rigid product interface** | Adding a new product kind (`createSlider()`) means updating `UIFactory` and *every* concrete factory, even those that do not need a slider. |
| 3 | **Overkill for simple cases** | If you only have one product type or one family, a simple Factory Method or even a plain constructor is cleaner. |
| 4 | **Indirection overhead** | Understanding the code requires tracing through multiple layers of abstraction, which increases cognitive load for newcomers. |

---

## SOLID Principles Covered

### 1. Single Responsibility Principle (SRP) ✅
Each class has exactly one reason to change:
- `LightThemeFactory` changes only if the Light product creation logic changes.
- `LightButton` changes only if the Light button's appearance/behaviour changes.
- `Application` changes only if the app's rendering orchestration changes.

No class is doing too many jobs.

---

### 2. Open/Closed Principle (OCP) ✅
The system is **open for extension, closed for modification**.

To add a "High Contrast" theme, you only create new files:

```java
// NEW FILES — existing files are not touched at all
public class HighContrastButton   implements Button   { ... }
public class HighContrastCheckbox implements Checkbox { ... }
public class HighContrastFactory  implements UIFactory { ... }
```

The `Application`, `UIFactory`, `Button`, and `Checkbox` types are **not modified at all**.

---

### 3. Liskov Substitution Principle (LSP) ✅
`LightThemeFactory` and `DarkThemeFactory` are substitutable for `UIFactory` anywhere:

```java
UIFactory factory = new LightThemeFactory(); // works
UIFactory factory = new DarkThemeFactory();  // also works — Application never knows the difference
Application app = new Application(factory);
```

`Application` receives a `UIFactory` reference and works identically regardless of which concrete factory is passed — the program's correctness does not change.

Similarly, `LightButton` is substitutable for `Button` anywhere a `Button` is expected.

---

### 4. Interface Segregation Principle (ISP) ✅
The abstract products (`Button`, `Checkbox`) are focused and minimal — each defines only the methods relevant to that widget type. No widget is forced to implement methods it does not need.

```java
// Button only knows about button concerns
public interface Button {
    String render();
    String onClick();
}

// Checkbox only knows about checkbox concerns
public interface Checkbox {
    String render();
    String toggle();
}
```

If a factory does not need checkboxes, you would split `UIFactory` into finer-grained interfaces rather than forcing factories to implement a dummy `createCheckbox()`.

---

### 5. Dependency Inversion Principle (DIP) ✅ — The Core Principle
This is where Abstract Factory shines most.

**Without DIP (bad):**
```java
public class Application {
    private LightButton   button;    // ← high-level module depends on a detail
    private LightCheckbox checkbox;  // ← coupled, impossible to swap

    public Application() {
        this.button   = new LightButton();
        this.checkbox = new LightCheckbox();
    }
}
```

**With DIP (good — what we built):**
```java
public class Application {
    private final Button   button;    // ← depends on abstraction
    private final Checkbox checkbox;  // ← depends on abstraction

    public Application(UIFactory factory) {   // ← factory injected, not hardcoded
        this.button   = factory.createButton();
        this.checkbox = factory.createCheckbox();
    }
}
```

- High-level module (`Application`) depends on the **abstraction** (`UIFactory`, `Button`, `Checkbox`).
- Low-level modules (`LightButton`, `DarkCheckbox`) implement those same abstractions.
- Neither depends on the other directly.

The concrete factory is **injected from outside** (`Main.java`), keeping the dependency graph inverted and the application fully testable and swappable.

---

## When to Use Abstract Factory

- You need to enforce that a set of related objects are always used together (product families).
- You want to switch between multiple product families without changing client code.
- You want to decouple client code from the concrete classes it uses.
- Your system must support multiple configurations or platforms that share the same interface but differ in implementation.

## When NOT to Use

- You only have a single product type — use Factory Method instead.
- The products in your system do not form natural "families" — the pattern adds unnecessary complexity.
- The number of product types is likely to grow frequently — each addition forces changes across all factory classes.

---

## Summary

```
Abstract Factory  =  A contract (UIFactory)
                     that guarantees you get a
                     consistent family of objects (Button + Checkbox)
                     without ever knowing which family you are in.
```

The pattern trades a higher class count for **strong consistency guarantees, easy family swapping, and deep decoupling** — a trade-off that pays off whenever you manage multiple coherent product families.
