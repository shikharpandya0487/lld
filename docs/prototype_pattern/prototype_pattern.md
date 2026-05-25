# Prototype Design Pattern

## What Is It?

The **Prototype** pattern is a **creational design pattern** that lets you create new objects by **cloning an existing object** (the prototype) instead of constructing them from scratch via `new`.

> "Specify the kinds of objects to create using a prototypical instance, and create new objects by copying this prototype."
> — Gang of Four

---

## The Core Problem It Solves

Sometimes creating an object is **expensive or complex**:
- Heavy database/network initialization
- Deeply nested configuration
- Many constructor parameters with inter-dependencies

Instead of repeating that expensive setup, you:
1. Create **one fully-configured object** (the prototype).
2. **Clone it** whenever you need a similar object.
3. Tweak only the parts that differ.

---

## Structure

```
        «interface»
        Prototype
        ──────────
        + clone() : Prototype
             ▲
             │ implements
    ┌────────┴────────┐
    │  ConcreteProto  │
    │ ─────────────── │
    │ - field1        │
    │ - field2        │
    │ + clone()       │◄──── Client clones this
    └─────────────────┘
```

### Key Participants

| Participant        | Role |
|--------------------|------|
| **Prototype**      | Interface / abstract class declaring `clone()` |
| **ConcretePrototype** | Implements `clone()` — shallow or deep |
| **Client**         | Calls `clone()` instead of `new ConcretePrototype()` |
| **Registry** *(optional)* | Stores named prototypes; returns clones on demand |

---

## Shallow Copy vs Deep Copy

| | Shallow Copy | Deep Copy |
|---|---|---|
| **Primitives** | Copied by value ✓ | Copied by value ✓ |
| **Object references** | Shares the same reference ⚠️ | New copy of the referenced object ✓ |
| **When to use** | Object has no mutable reference fields | Object contains mutable collections / nested objects |

In Java, `super.clone()` gives a **shallow copy**. For fields that are mutable objects (e.g., `List`, `Map`), you must **manually deep-copy** them inside `clone()`.

---

## Java's `Cloneable` Interface

Java provides a built-in mechanism:

```java
public class MyClass implements Cloneable {
    @Override
    public MyClass clone() {
        try {
            return (MyClass) super.clone(); // shallow copy from Object
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
```

> **Note:** `Cloneable` is a marker interface. `Object.clone()` throws `CloneNotSupportedException` if the class doesn't implement it.

---

## Example 1 — Shape Cloning (Basic Prototype)

**Use case:** Copy geometric shapes and modify the clone independently.

**Files:** [Shape.java](src/prototype_pattern/Shape.java) · [Circle.java](src/prototype_pattern/Circle.java) · [Rectangle.java](src/prototype_pattern/Rectangle.java)

```java
// Abstract prototype
public abstract class Shape implements Cloneable {
    protected String color;

    @Override
    public Shape clone() {
        try { return (Shape) super.clone(); }
        catch (CloneNotSupportedException e) { throw new RuntimeException(e); }
    }
}

// Client usage
Circle c1 = new Circle("Red", 5.0);
Circle c2 = (Circle) c1.clone();  // independent copy
c2.setRadius(10.0);               // c1 radius unchanged
```

**Why Prototype here?** You avoid re-specifying `color`, `type`, and other shared attributes every time you need a similar shape.

---

## Example 2 — Game Character (Deep Copy)

**Use case:** Spawn enemy variants from a base template. The `inventory` list must be deep-copied so clones don't share items.

**File:** [GameCharacter.java](src/prototype_pattern/GameCharacter.java)

```java
public class GameCharacter implements Cloneable {
    private String name;
    private List<String> inventory;  // mutable — needs deep copy

    @Override
    public GameCharacter clone() {
        try {
            GameCharacter cloned = (GameCharacter) super.clone();
            cloned.inventory = new ArrayList<>(this.inventory); // deep copy
            return cloned;
        } catch (CloneNotSupportedException e) { throw new RuntimeException(e); }
    }
}

// Usage
GameCharacter warrior = new GameCharacter("Warrior", 100, 30);
warrior.addItem("Sword");

GameCharacter elite = warrior.clone();
elite.setName("Elite Warrior");
elite.addItem("Magic Ring"); // warrior's inventory NOT affected
```

**Key insight:** Without `new ArrayList<>(this.inventory)`, both `warrior` and `elite` would share the same list — mutations in one would corrupt the other.

---

## Example 3 — Document Template Registry

**Use case:** Pre-configure invoice / report / letter templates once; hand out fresh clones on demand.

**File:** [DocumentTemplate.java](src/prototype_pattern/DocumentTemplate.java)

```java
// Registry stores named prototypes and returns clones
public static class Registry {
    private final Map<String, DocumentTemplate> templates = new HashMap<>();

    public void register(String key, DocumentTemplate template) {
        templates.put(key, template);
    }

    public DocumentTemplate get(String key) {
        return templates.get(key).clone(); // always a fresh copy
    }
}

// Usage
DocumentTemplate invoiceTpl = new DocumentTemplate("Invoice", "ACME", "Page {n}");
registry.register("invoice", invoiceTpl);

DocumentTemplate doc1 = registry.get("invoice"); // fresh clone
doc1.setTitle("Invoice #001");

DocumentTemplate doc2 = registry.get("invoice"); // another fresh clone
doc2.setTitle("Invoice #002");
// invoiceTpl is unchanged
```

**Why a Registry?** Centralises prototype management — clients only know a string key, not the concrete class.

---

## Example 4 — Network Configuration

**Use case:** Start from a validated base config and create environment-specific variants (dev, staging, prod).

**File:** [NetworkConfig.java](src/prototype_pattern/NetworkConfig.java)

```java
NetworkConfig base = new NetworkConfig("localhost", 8080, 3000, 100, false);
base.addAllowedIP("192.168.1.1");

NetworkConfig prod = base.clone();  // inherits all baseline settings
prod.setHost("prod.example.com");
prod.setPort(443);
prod.setMaxConnections(500);
prod.addAllowedIP("203.0.113.5");   // prod-only; base config unaffected
```

**Why Prototype here?** You get a validated, pre-tested baseline without repeating every field. Reduces misconfiguration risk.

---

## Example 5 — UI Component Tree (Composite + Prototype)

**Use case:** Clone a fully-laid-out form widget tree (panel → text fields → button) to reuse it in multiple places with minor tweaks.

**File:** [UIComponent.java](src/prototype_pattern/UIComponent.java)

```java
// Recursive deep clone — clones every child in the tree
@Override
public UIComponent clone() {
    try {
        UIComponent cloned = (UIComponent) super.clone();
        cloned.children = new ArrayList<>();
        for (UIComponent child : this.children) {
            cloned.children.add(child.clone()); // recursive
        }
        return cloned;
    } catch (CloneNotSupportedException e) { throw new RuntimeException(e); }
}

// Usage
UIComponent loginForm = new UIComponent("Panel", "loginForm", ...);
loginForm.addChild(new UIComponent("TextField", "username", ...));
loginForm.addChild(new UIComponent("Button", "submit", ...));

UIComponent registerForm = loginForm.clone(); // full tree cloned
registerForm.setId("registerForm");
registerForm.moveTo(500, 0);
```

**Why Prototype here?** Rebuilding a deep component tree node-by-node every time is tedious and error-prone. One `clone()` duplicates the entire structure.

---

## When to Use the Prototype Pattern

| Situation | Why Prototype Helps |
|-----------|---------------------|
| Object construction is expensive (I/O, computation) | Pay the cost once; clone for subsequent instances |
| You need many similar objects differing in only a few fields | Clone + tweak instead of full construction |
| The concrete class is unknown at compile time | Clients use `clone()` without knowing the type |
| You want to avoid a deep class hierarchy of factories | One prototype per variant vs. one factory per variant |

---

## When NOT to Use It

- When objects are simple and cheap to construct (`new Point(x, y)` — just use `new`).
- When the object has no meaningful "copy" semantics (e.g., holds a live database connection).
- When circular references make deep cloning dangerously complex (consider a copy-constructor instead).

---

## Advantages

- **Avoids expensive re-initialization** — clone is faster than re-running heavy setup.
- **Hides concrete types** — client code only calls `clone()`.
- **Easy variant creation** — set up one prototype, tweak clones for each variant.
- **Runtime flexibility** — prototypes can be registered and swapped at runtime.

## Disadvantages

- **Deep copy complexity** — circular references and mutable nested objects require careful manual cloning.
- **`Cloneable` in Java is awkward** — it's a marker interface; `clone()` is `protected` on `Object` and throws a checked exception.
- **Hidden state** — forgetting to deep-copy a field causes subtle shared-state bugs.

---

## Prototype vs Other Creational Patterns

| Pattern | How objects are created |
|---------|------------------------|
| **Factory Method** | Subclass decides which class to instantiate |
| **Abstract Factory** | Family of related objects via factory interface |
| **Builder** | Step-by-step construction of a complex object |
| **Prototype** | Clone an existing instance |
| **Singleton** | One shared instance |

---

## Quick Reference

```
Need a new object?
       │
       ▼
Is construction expensive / complex?
  Yes ──► Is there a good "base" instance already? 
              Yes ──► PROTOTYPE  (clone + tweak)
              No  ──► Builder / Factory
  No  ──► Just use `new`
```
