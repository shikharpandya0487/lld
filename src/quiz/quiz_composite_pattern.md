# Quiz — Composite Pattern

Answer all 5 questions, then paste your answers (e.g. "1-B, 2-A, 3-see below, 4-see below, 5-C") and I'll explain each one.

---

## Q1 — Concept (Multiple Choice)

What is the primary intent of the Composite pattern?

A) To wrap an object with additional behaviour at runtime without changing its class  
B) To compose objects into tree structures so that individual objects and groups of objects can be treated identically  
C) To define a family of algorithms and make them interchangeable  
D) To ensure only one instance of a class exists across the entire application  

---

## Q2 — Real-World Analogy (Multiple Choice)

Which of the following real-world situations best describes the Composite pattern?

A) A TV remote that sends the same "power" signal regardless of which brand of TV is connected  
B) A restaurant menu where a "Combo Meal" contains individual items, and both the combo and each item have a displayed price  
C) A factory assembly line where different workers specialise in one step of production  
D) A loyalty card that stamps once per visit, regardless of how many items were purchased  

---

## Q3 — Spot the Bug

What is wrong with the following Java code? Describe the problem in 1–2 sentences.

```java
// What is wrong here?
interface DocumentComponent {
    void render();
    void add(DocumentComponent component);
    void remove(DocumentComponent component);
}

class TextBlock implements DocumentComponent {
    private String content;

    TextBlock(String content) { this.content = content; }

    @Override
    public void render() {
        System.out.println("Text: " + content);
    }

    @Override
    public void add(DocumentComponent component) {
        throw new UnsupportedOperationException("TextBlock cannot have children");
    }

    @Override
    public void remove(DocumentComponent component) {
        throw new UnsupportedOperationException("TextBlock cannot have children");
    }
}

class Section implements DocumentComponent {
    private List<DocumentComponent> children = new ArrayList<>();

    @Override
    public void render() {
        for (DocumentComponent c : children) c.render();
    }

    @Override
    public void add(DocumentComponent component) { children.add(component); }

    @Override
    public void remove(DocumentComponent component) { children.remove(component); }
}
```

---

## Q4 — Fill in the Blank

Complete the missing lines marked with `// ???` to correctly apply the Composite pattern.

```java
interface OrgComponent {
    void showDetails(String indent);
}

class Employee implements OrgComponent {
    private String name;
    private String role;

    Employee(String name, String role) {
        this.name = name;
        this.role = role;
    }

    @Override
    public void showDetails(String indent) {
        System.out.println(indent + name + " [" + role + "]");
    }
}

class Team implements OrgComponent {
    private String teamName;
    private List<OrgComponent> members = new ArrayList<>();

    Team(String teamName) { this.teamName = teamName; }

    public void add(OrgComponent component) {
        // ???
    }

    @Override
    public void showDetails(String indent) {
        System.out.println(indent + "== Team: " + teamName + " ==");
        for (OrgComponent member : members) {
            // ???
        }
    }
}
```

---

## Q5 — When to Use (Multiple Choice)

You are building a discount system for an e-commerce platform. A `SingleItem` has a fixed price. A `Bundle` contains multiple items (and possibly other bundles) and its price is the sum of all children's prices. Which approach is most appropriate?

A) Create a `Bundle` class that holds a `List<SingleItem>` — bundles can only contain individual items, not other bundles  
B) Define a `PricedComponent` interface with `getPrice()`, implement it in both `SingleItem` (leaf) and `Bundle` (composite that delegates to children), allowing bundles to contain other bundles  
C) Add an `isBundle` boolean flag to `SingleItem` and check it everywhere you calculate prices  
D) Use the Decorator pattern to wrap `SingleItem` objects with a `BundleDecorator` that sums prices  
