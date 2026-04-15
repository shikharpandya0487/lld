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
  abstract_factory/
    interfaces.py      ← Abstract products + Abstract factory
    light_theme.py     ← Concrete Light family
    dark_theme.py      ← Concrete Dark family
    app.py             ← Client code
    main.py            ← Entry point / factory selector
```

---

### Step 1 — Abstract Products & Abstract Factory (`interfaces.py`)

```python
from abc import ABC, abstractmethod


# ── Abstract Products ──────────────────────────────────────────────────────────

class Button(ABC):
    """Every Button variant must be renderable and clickable."""

    @abstractmethod
    def render(self) -> str:
        """Return a string describing how the button looks."""
        ...

    @abstractmethod
    def on_click(self) -> str:
        """Return the button's click feedback."""
        ...


class Checkbox(ABC):
    """Every Checkbox variant must be renderable and toggleable."""

    @abstractmethod
    def render(self) -> str:
        ...

    @abstractmethod
    def toggle(self) -> str:
        ...


# ── Abstract Factory ───────────────────────────────────────────────────────────

class UIFactory(ABC):
    """
    Declares creation methods for each distinct product in the widget family.
    Concrete factories implement this to return products that belong to the
    same theme/family so they always look consistent together.
    """

    @abstractmethod
    def create_button(self) -> Button:
        ...

    @abstractmethod
    def create_checkbox(self) -> Checkbox:
        ...
```

**What is happening here?**
- `Button` and `Checkbox` are **abstract products** — they define the contract every concrete widget must honour.
- `UIFactory` is the **abstract factory** — it declares one creation method per product type. It never instantiates anything itself.

---

### Step 2 — Light Theme Family (`light_theme.py`)

```python
from interfaces import Button, Checkbox, UIFactory


class LightButton(Button):
    def render(self) -> str:
        return "[Light Button] White background, dark text border"

    def on_click(self) -> str:
        return "[Light Button] Ripple effect in light grey"


class LightCheckbox(Checkbox):
    def render(self) -> str:
        return "[Light Checkbox] White box with thin dark border"

    def toggle(self) -> str:
        return "[Light Checkbox] Dark checkmark appears inside white box"


class LightThemeFactory(UIFactory):
    """Concrete factory — produces the entire Light widget family."""

    def create_button(self) -> Button:
        return LightButton()

    def create_checkbox(self) -> Checkbox:
        return LightCheckbox()
```

---

### Step 3 — Dark Theme Family (`dark_theme.py`)

```python
from interfaces import Button, Checkbox, UIFactory


class DarkButton(Button):
    def render(self) -> str:
        return "[Dark Button] Charcoal background, white glowing border"

    def on_click(self) -> str:
        return "[Dark Button] Ripple effect in electric blue"


class DarkCheckbox(Checkbox):
    def render(self) -> str:
        return "[Dark Checkbox] Dark box with neon border"

    def toggle(self) -> str:
        return "[Dark Checkbox] White checkmark glows on dark background"


class DarkThemeFactory(UIFactory):
    """Concrete factory — produces the entire Dark widget family."""

    def create_button(self) -> Button:
        return DarkButton()

    def create_checkbox(self) -> Checkbox:
        return DarkCheckbox()
```

---

### Step 4 — Client / Application (`app.py`)

```python
from interfaces import UIFactory, Button, Checkbox


class Application:
    """
    The client code. It knows NOTHING about LightButton or DarkCheckbox.
    It only talks to the abstract interfaces: UIFactory, Button, Checkbox.
    Swap the factory at startup and the whole app changes theme.
    """

    def __init__(self, factory: UIFactory) -> None:
        # The factory is injected — the app never calls a concrete constructor.
        self._button: Button = factory.create_button()
        self._checkbox: Checkbox = factory.create_checkbox()

    def render_ui(self) -> None:
        print(self._button.render())
        print(self._checkbox.render())

    def interact(self) -> None:
        print(self._button.on_click())
        print(self._checkbox.toggle())
```

**Key insight:** `Application` is completely decoupled from every concrete class. Its constructor receives a `UIFactory` — dependency injection in action.

---

### Step 5 — Entry Point (`main.py`)

```python
import os
from interfaces import UIFactory
from light_theme import LightThemeFactory
from dark_theme import DarkThemeFactory
from app import Application


def get_factory() -> UIFactory:
    """
    In a real app this decision comes from a config file, OS setting,
    user preference, or environment variable.
    """
    theme = os.getenv("APP_THEME", "light").lower()
    if theme == "dark":
        return DarkThemeFactory()
    return LightThemeFactory()


if __name__ == "__main__":
    factory = get_factory()
    app = Application(factory)

    print("=== Rendering UI ===")
    app.render_ui()

    print("\n=== User Interacts ===")
    app.interact()
```

**Sample output — Light theme (`APP_THEME=light`):**

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
│  + create_button()  : Button                   │
│  + create_checkbox(): Checkbox                 │
└──────────────┬──────────────────────────────────┘
               │ implements
   ┌───────────┴────────────┐
   │                        │
   ▼                        ▼
LightThemeFactory      DarkThemeFactory
  create_button()        create_button()
  create_checkbox()      create_checkbox()
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
| `main.py` | Configuration Point — the one place that decides which family to use |

### Why is `main.py` the only place with a concrete reference?
Because the factory choice is a **configuration decision**, not a business logic decision. The rest of the app never needs to know which theme is active. This is the pattern's central promise.

---

## Advantages

| # | Advantage | Explanation |
|---|---|---|
| 1 | **Product consistency** | All products from one factory are designed to work together. You can never accidentally mix a Dark button with a Light checkbox. |
| 2 | **Easy family swap** | Changing the entire product family is a single-line change in `main.py`. No other code needs touching. |
| 3 | **Isolated concrete classes** | Client code (`Application`) never imports or references any concrete class. |
| 4 | **Adding a new family is low-risk** | To add a "High Contrast" theme, create `HighContrastButton`, `HighContrastCheckbox`, and `HighContrastFactory`. Existing code is untouched. |
| 5 | **Promotes testability** | You can inject a `MockUIFactory` in tests that returns stub widgets, enabling fast unit tests without real UI. |

---

## Disadvantages

| # | Disadvantage | Explanation |
|---|---|---|
| 1 | **High class count** | Each new product type (e.g., adding `Slider`) requires a new abstract product + one concrete class *per family*. Three themes × five widget types = 15 concrete classes minimum. |
| 2 | **Rigid product interface** | Adding a new product kind (`create_slider()`) means updating the abstract factory and *every* concrete factory, even those that do not need a slider. |
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

To add a "High Contrast" theme:
```python
# NEW FILE — existing files are not touched
class HighContrastButton(Button): ...
class HighContrastCheckbox(Checkbox): ...
class HighContrastFactory(UIFactory): ...
```
The `Application`, `UIFactory`, `Button`, and `Checkbox` classes are **not modified at all**.

---

### 3. Liskov Substitution Principle (LSP) ✅
`LightThemeFactory` and `DarkThemeFactory` are substitutable for `UIFactory` anywhere. `Application` receives a `UIFactory` and works identically regardless of which concrete factory is passed — the program's correctness does not change.

Similarly, `LightButton` is substitutable for `Button` and `DarkButton` is substitutable for `Button` — consumers of `Button` do not need to know which concrete type they hold.

---

### 4. Interface Segregation Principle (ISP) ✅
The abstract products (`Button`, `Checkbox`) are focused and minimal — each defines only the methods relevant to that widget type. No widget is forced to implement methods it does not need. The `UIFactory` interface is also focused: one method per product type.

If a factory does not need checkboxes, you would split the factory into finer-grained interfaces rather than implementing dummy `create_checkbox()` methods.

---

### 5. Dependency Inversion Principle (DIP) ✅ — The Core Principle
This is where Abstract Factory shines most.

**Without DIP (bad):**
```python
class Application:
    def __init__(self):
        self._button = LightButton()      # ← high-level module depends on detail
        self._checkbox = LightCheckbox()  # ← coupled, impossible to swap
```

**With DIP (good — what we built):**
```python
class Application:
    def __init__(self, factory: UIFactory):  # ← depends on abstraction
        self._button = factory.create_button()
        self._checkbox = factory.create_checkbox()
```

- High-level module (`Application`) depends on the **abstraction** (`UIFactory`, `Button`, `Checkbox`).
- Low-level modules (`LightButton`, `DarkCheckbox`) depend on the **same abstractions**.
- Neither depends on the other directly.

The concrete factory is **injected from outside**, keeping the dependency graph inverted and the application fully testable and swappable.

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
