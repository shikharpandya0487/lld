# Structural Design Patterns

## What Are Structural Design Patterns?

Structural design patterns deal with **how objects and classes are composed to form larger structures**. They focus on simplifying the structure by identifying relationships between entities — making it easier to build flexible, efficient, and maintainable systems.

Think of structural patterns as the "blueprint for assembling components." Just like how LEGO bricks can be combined in different ways to build complex structures, structural patterns define **how to assemble objects and classes** so that the whole is greater than the sum of its parts.

### Key Characteristics
- They work at the level of **class and object composition**
- They help ensure that when one part of a system changes, the **entire structure doesn't need to change**
- They promote **loose coupling** and **code reuse**
- They are primarily concerned with **interfaces and abstract classes** to build relationships

---

## The 7 Structural Design Patterns

### 1. Adapter Pattern
- **Intent:** Converts the interface of a class into another interface that clients expect. It allows classes with incompatible interfaces to work together.
- **Analogy:** A power adapter that lets you plug a US device into a European socket.
- **Use when:**
  - You want to use an existing class, but its interface doesn't match what you need
  - You want to create a reusable class that cooperates with unrelated or unforeseen classes
- **Example:** Adapting a legacy XML-based payment system to work with a new JSON-based interface
- **Key participants:** `Target`, `Adapter`, `Adaptee`, `Client`

---

### 2. Bridge Pattern
- **Intent:** Decouples an abstraction from its implementation so that the two can vary independently.
- **Analogy:** A TV remote (abstraction) that works with any TV brand (implementation).
- **Use when:**
  - You want to avoid a permanent binding between abstraction and implementation
  - Both abstraction and implementation should be extensible via subclassing
  - You have a class hierarchy that is growing in two independent dimensions
- **Example:** A `Shape` abstraction (Circle, Square) with separate rendering implementations (OpenGL, DirectX)
- **Key participants:** `Abstraction`, `RefinedAbstraction`, `Implementor`, `ConcreteImplementor`

---

### 3. Composite Pattern
- **Intent:** Composes objects into tree structures to represent part-whole hierarchies. Lets clients treat individual objects and compositions uniformly.
- **Analogy:** A file system where folders contain files and other folders — yet both are treated as "file system items."
- **Use when:**
  - You want to represent part-whole hierarchies of objects
  - You want clients to be able to ignore the difference between compositions and individual objects
- **Example:** A UI component tree where `Panel` contains `Button`s and other `Panel`s, but all are rendered via the same `render()` method
- **Key participants:** `Component`, `Leaf`, `Composite`

---

### 4. Decorator Pattern
- **Intent:** Attaches additional responsibilities to an object dynamically. Provides a flexible alternative to subclassing for extending functionality.
- **Analogy:** Adding toppings to a pizza — each topping "decorates" the base pizza without changing its core identity.
- **Use when:**
  - You want to add responsibilities to individual objects dynamically without affecting others
  - Extension by subclassing is impractical due to a large number of potential combinations
- **Example:** Java I/O streams — `BufferedReader` decorates `FileReader` to add buffering capability
- **Key participants:** `Component`, `ConcreteComponent`, `Decorator`, `ConcreteDecorator`

---

### 5. Facade Pattern
- **Intent:** Provides a simplified interface to a complex subsystem. It doesn't hide the subsystem but makes it easier to use.
- **Analogy:** A hotel concierge — you ask them to "arrange a car," and they handle all the underlying coordination.
- **Use when:**
  - You want to provide a simple interface to a complex body of code
  - You want to decouple client code from subsystem internals
  - You want to layer your subsystems
- **Example:** A `HomeTheaterFacade` that simplifies starting a movie (turns on projector, dims lights, starts sound system) into one `watchMovie()` call
- **Key participants:** `Facade`, `Subsystem classes`

---

### 6. Flyweight Pattern
- **Intent:** Uses sharing to support a large number of fine-grained objects efficiently. Reduces memory usage by sharing common state among many objects.
- **Analogy:** In a forest simulation, each "tree" object shares the same texture and color data (intrinsic state) but has its own position (extrinsic state).
- **Use when:**
  - An application uses a large number of objects that have high memory cost
  - Most object state can be made extrinsic (passed in from outside)
  - Many groups of objects can be replaced by relatively few shared objects
- **Example:** A text editor that shares `Character` objects for each letter of the alphabet rather than creating millions of separate instances
- **Key participants:** `Flyweight`, `ConcreteFlyweight`, `FlyweightFactory`, `Client`

---

### 7. Proxy Pattern
- **Intent:** Provides a surrogate or placeholder for another object to control access to it.
- **Analogy:** A credit card is a proxy for a bank account — it controls and mediates access to your actual funds.
- **Use when:**
  - You need a remote proxy (represents an object in a different address space)
  - You need a virtual proxy (lazy initialization of expensive objects)
  - You need a protection proxy (access control)
  - You need a logging/caching proxy
- **Example:** A `ProxyImage` that only loads the real image from disk when `display()` is actually called (lazy loading)
- **Key participants:** `Subject`, `RealSubject`, `Proxy`

---

## Quick Comparison Table

| Pattern   | Intent                              | Relationship Type     |
|-----------|-------------------------------------|-----------------------|
| Adapter   | Convert incompatible interfaces     | Class/Object wrapping |
| Bridge    | Separate abstraction & impl         | Composition           |
| Composite | Part-whole tree hierarchies         | Tree structure        |
| Decorator | Add behavior dynamically            | Wrapping chain        |
| Facade    | Simplify complex subsystem          | Unified interface     |
| Flyweight | Share state for memory efficiency   | Object pooling/sharing|
| Proxy     | Control access to an object         | Surrogate/wrapper     |

---

## When to Choose a Structural Pattern?

| Situation                                              | Pattern to Use |
|--------------------------------------------------------|----------------|
| Two incompatible interfaces need to work together      | Adapter        |
| Abstraction and implementation need to scale separately| Bridge         |
| Tree-like hierarchy of objects needed                  | Composite      |
| Need to add behavior without modifying the class       | Decorator      |
| Complex system needs a clean entry point               | Facade         |
| Thousands of similar objects are eating memory         | Flyweight      |
| Need to control or intercept access to an object       | Proxy          |
