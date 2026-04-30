# Composite Pattern — Treat individual objects and groups of objects the same way

---

## The Analogy

Think of a company's org chart. The CEO manages Vice Presidents. Each VP manages Managers. Each Manager manages individual Employees. When the company calculates total salary, it doesn't care whether it's talking to one person or a whole department — it just says "give me your total cost" and the answer comes back correctly.

- **Individual Employee** → a **Leaf** node (has no children, does the real work)
- **Department / Team** → a **Composite** node (contains other nodes, delegates work down)
- **"Give me your total cost"** → the **common operation** defined in the shared interface

---

## What Problem Does It Solve?

Imagine you are building a file system. You have files and folders. A folder can contain files *and* other folders. Without a pattern, your code ends up with `if (isFile) { ... } else if (isFolder) { ... }` checks everywhere. Every time you add a new operation (delete, calculate size, print path), you must duplicate that if-else logic. The calling code needs to know whether it is dealing with a file or a folder — that knowledge leaks everywhere and the code becomes fragile and hard to extend.

The Composite pattern removes that burden. Both files and folders implement the same interface, so the caller treats them identically.

---

## The Formal Definition

> "Compose objects into tree structures to represent part-whole hierarchies. Composite lets clients treat individual objects and compositions of objects uniformly."
> — *Gang of Four, Design Patterns (1994)*

In plain English: define one interface, let simple objects and container objects both implement it, and let containers delegate operations to their children automatically.

---

## The Core Structure (Java)

```java
// The shared contract — both leaves and composites implement this
interface Component {
    void operation();
}

// Leaf — a single, indivisible object (no children)
class Leaf implements Component {
    @Override
    public void operation() {
        System.out.println("Leaf does work");
    }
}

// Composite — a container that holds other Components (leaves OR other composites)
class Composite implements Component {
    private List<Component> children = new ArrayList<>();

    public void add(Component c)    { children.add(c); }
    public void remove(Component c) { children.remove(c); }

    @Override
    public void operation() {
        // Delegates to every child — it does NOT care if child is Leaf or Composite
        for (Component child : children) {
            child.operation();
        }
    }
}
```

**Key roles:**

| Role | Who plays it | Responsibility |
|---|---|---|
| `Component` | Interface | Declares the shared operation |
| `Leaf` | Concrete class | Does the real work, has no children |
| `Composite` | Concrete class | Stores children, delegates operation to them |

---

## Example 1 — BAD: Without the Pattern

**Trigger:** You are building a file system browser. Files and folders both need a `printSize()` operation.

```java
// BAD — the caller must constantly check types with instanceof
// Every new operation you add will need the same if-else block again and again

class File {
    String name;
    int size;
    File(String name, int size) { this.name = name; this.size = size; }
}

class Folder {
    String name;
    List<Object> contents = new ArrayList<>(); // mixed: File or Folder
    Folder(String name) { this.name = name; }
    void add(Object item) { contents.add(item); }
}

class FileSystemPrinter {
    void printSize(Object item) {
        // PROBLEM: you have to know what type you're dealing with
        if (item instanceof File) {
            File f = (File) item;
            System.out.println(f.name + " = " + f.size + " KB");
        } else if (item instanceof Folder) {
            Folder folder = (Folder) item;
            System.out.println("[Folder] " + folder.name);
            for (Object child : folder.contents) {
                printSize(child); // recurse — but still with the same ugly instanceof check
            }
        }
        // If you add a new type (e.g. Shortcut), you must come back HERE and add another else-if
    }
}

// Usage
class Main {
    public static void main(String[] args) {
        File readme = new File("README.txt", 10);
        File photo  = new File("photo.jpg",  500);
        Folder docs = new Folder("Documents");
        docs.add(readme);
        docs.add(photo);

        FileSystemPrinter printer = new FileSystemPrinter();
        printer.printSize(docs);
        // Output:
        // [Folder] Documents
        // README.txt = 10 KB
        // photo.jpg = 500 KB
    }
}
```

**What breaks:** Every time you add a new operation (`deleteAll`, `calculateTotalSize`, `searchByName`), you must copy the `instanceof` chain. Add a new node type like `Shortcut` and you must update every method in every class that ever touched these objects.

---

## Example 2 — GOOD: With the Composite Pattern Applied

```java
import java.util.ArrayList;
import java.util.List;

// Step 1 — Define the shared contract
interface FileSystemComponent {
    void printSize(String indent);
}

// Step 2 — Leaf: a real file with no children
class File implements FileSystemComponent {
    private String name;
    private int sizeKb;

    File(String name, int sizeKb) {
        this.name   = name;
        this.sizeKb = sizeKb;
    }

    @Override
    public void printSize(String indent) {
        // Leaf does the real work directly
        System.out.println(indent + name + " = " + sizeKb + " KB");
    }
}

// Step 3 — Composite: a folder that can hold files OR other folders
class Folder implements FileSystemComponent {
    private String name;
    private List<FileSystemComponent> children = new ArrayList<>();

    Folder(String name) { this.name = name; }

    public void add(FileSystemComponent component) {
        children.add(component);
    }

    @Override
    public void printSize(String indent) {
        System.out.println(indent + "[Folder] " + name);
        // Delegate to every child — no instanceof check needed ever again
        for (FileSystemComponent child : children) {
            child.printSize(indent + "  ");
        }
    }
}

// Usage — the caller only ever sees FileSystemComponent
class Main {
    public static void main(String[] args) {
        File readme = new File("README.txt", 10);
        File photo  = new File("photo.jpg",  500);
        File report = new File("report.pdf", 200);

        Folder docs    = new Folder("Documents");
        Folder images  = new Folder("Images");
        Folder root    = new Folder("Root");

        images.add(photo);
        docs.add(readme);
        docs.add(report);

        root.add(docs);
        root.add(images);

        root.printSize("");
    }
}

// Output:
// [Folder] Root
//   [Folder] Documents
//     README.txt = 10 KB
//     report.pdf = 200 KB
//   [Folder] Images
//     photo.jpg = 500 KB
```

**What changed:** The caller (`main`) never uses `instanceof`. Adding a new node type (`Shortcut`) means implementing `FileSystemComponent` once — no existing code changes.

---

## Example 3 — UI Component Tree (Frontend Rendering)

**Trigger:** You are building a UI framework. A `Panel` can contain `Button`s and other `Panel`s. You need to render the whole tree with one call.

```java
interface UIComponent {
    void render(String indent);
}

class Button implements UIComponent {
    private String label;
    Button(String label) { this.label = label; }

    @Override
    public void render(String indent) {
        System.out.println(indent + "[Button] " + label);
    }
}

class Panel implements UIComponent {
    private String name;
    private List<UIComponent> children = new ArrayList<>();

    Panel(String name) { this.name = name; }

    public void add(UIComponent c) { children.add(c); }

    @Override
    public void render(String indent) {
        System.out.println(indent + "[Panel] " + name);
        for (UIComponent child : children) {
            child.render(indent + "  ");
        }
    }
}

class Main {
    public static void main(String[] args) {
        Panel toolbar = new Panel("Toolbar");
        toolbar.add(new Button("Save"));
        toolbar.add(new Button("Open"));

        Panel sidebar = new Panel("Sidebar");
        sidebar.add(new Button("Home"));
        sidebar.add(new Button("Settings"));

        Panel screen = new Panel("Screen");
        screen.add(toolbar);
        screen.add(sidebar);

        screen.render("");
    }
}

// Output:
// [Panel] Screen
//   [Panel] Toolbar
//     [Button] Save
//     [Button] Open
//   [Panel] Sidebar
//     [Button] Home
//     [Button] Settings
```

---

## Example 4 — Company Org Chart (Salary Calculation)

**Trigger:** HR needs to calculate the total salary under any employee — whether they are a single individual or a department head with hundreds of reports.

```java
interface Employee {
    int getSalary();
    void print(String indent);
}

class Developer implements Employee {
    private String name;
    private int salary;

    Developer(String name, int salary) { this.name = name; this.salary = salary; }

    @Override public int getSalary()             { return salary; }
    @Override public void print(String indent)   { System.out.println(indent + name + " ($" + salary + ")"); }
}

class Manager implements Employee {
    private String name;
    private int baseSalary;
    private List<Employee> reports = new ArrayList<>();

    Manager(String name, int baseSalary) { this.name = name; this.baseSalary = baseSalary; }

    public void addReport(Employee e) { reports.add(e); }

    @Override
    public int getSalary() {
        int total = baseSalary;
        for (Employee e : reports) total += e.getSalary(); // recursive delegation
        return total;
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "[Manager] " + name + " ($" + getSalary() + " total)");
        for (Employee e : reports) e.print(indent + "  ");
    }
}

class Main {
    public static void main(String[] args) {
        Developer alice = new Developer("Alice", 90_000);
        Developer bob   = new Developer("Bob",   85_000);
        Developer carol = new Developer("Carol", 95_000);

        Manager teamLead = new Manager("Dave", 110_000);
        teamLead.addReport(alice);
        teamLead.addReport(bob);

        Manager cto = new Manager("Eve", 150_000);
        cto.addReport(teamLead);
        cto.addReport(carol);

        cto.print("");
        System.out.println("Total engineering cost: $" + cto.getSalary());
    }
}

// Output:
// [Manager] Eve ($530000 total)
//   [Manager] Dave ($285000 total)
//     Alice ($90000)
//     Bob ($85000)
//   Carol ($95000)
// Total engineering cost: $530000
```

---

## Example 5 — Menu System (Restaurant App)

**Trigger:** A restaurant app needs to display a menu. A menu has categories (Starters, Mains) and each category has items. The app must be able to print any subtree independently.

```java
interface MenuComponent {
    void display(String indent);
}

class MenuItem implements MenuComponent {
    private String name;
    private double price;

    MenuItem(String name, double price) { this.name = name; this.price = price; }

    @Override
    public void display(String indent) {
        System.out.println(indent + name + " — $" + price);
    }
}

class MenuCategory implements MenuComponent {
    private String categoryName;
    private List<MenuComponent> items = new ArrayList<>();

    MenuCategory(String categoryName) { this.categoryName = categoryName; }

    public void add(MenuComponent item) { items.add(item); }

    @Override
    public void display(String indent) {
        System.out.println(indent + "== " + categoryName + " ==");
        for (MenuComponent item : items) item.display(indent + "  ");
    }
}

class Main {
    public static void main(String[] args) {
        MenuCategory starters = new MenuCategory("Starters");
        starters.add(new MenuItem("Soup",         4.99));
        starters.add(new MenuItem("Garlic Bread",  3.49));

        MenuCategory mains = new MenuCategory("Mains");
        mains.add(new MenuItem("Pasta",   12.99));
        mains.add(new MenuItem("Burger",  10.99));

        MenuCategory fullMenu = new MenuCategory("Full Menu");
        fullMenu.add(starters);
        fullMenu.add(mains);

        fullMenu.display("");
    }
}

// Output:
// == Full Menu ==
//   == Starters ==
//     Soup — $4.99
//     Garlic Bread — $3.49
//   == Mains ==
//     Pasta — $12.99
//     Burger — $10.99
```

---

## How to Spot When to Use This Pattern

| Signal / Code Smell | This pattern helps because… |
|---|---|
| You write `if (x instanceof Leaf) { ... } else if (x instanceof Container) { ... }` | The interface removes the need to know the concrete type |
| You need to perform the same operation on a single item and a collection of items | Both implement the same interface, so one method call works on both |
| You have a tree structure (folders, org charts, menus, UI components) | Composite naturally models hierarchies without special-casing |
| Adding a new node type forces you to update many methods across many files | Composite isolates the new type behind the shared interface |
| You want to treat part and whole interchangeably | That is literally the pattern's definition |

---

## Common Mistakes Beginners Make

- **Making the Component interface too fat.** If you put `add()` and `remove()` on the `Component` interface, `Leaf` has to implement them even though it has no children. Keep the interface to the *shared operations* only; put `add`/`remove` only on `Composite`.

- **Forgetting that Composite can contain other Composites.** Beginners sometimes wire Composite to hold only Leaves. The whole power of the pattern is that a Composite holds *any* Component — including other Composites — enabling arbitrary depth.

- **Using the pattern for flat lists.** If your data is never nested (a simple list of items), Composite adds complexity for no gain. Use it only when you genuinely have a part-whole hierarchy.

- **Confusing Composite with Decorator.** Both wrap objects using the same interface. The difference: **Decorator** wraps exactly one object to add behaviour. **Composite** wraps many objects to delegate the same operation to all of them.

---

## Quick Reference

| | Detail |
|---|---|
| **Intent** | Compose objects into tree structures; treat individual objects and groups uniformly |
| **Key Roles** | `Component` (interface), `Leaf` (single object), `Composite` (container + delegation) |
| **Java Hints** | `interface`, `List<Component>`, `for` loop delegation, recursive calls |
| **When to Use** | Tree structures, part-whole hierarchies, same operation on single vs. collection |
| **When NOT to Use** | Flat lists, when the difference between leaf and composite matters to the caller, when the interface would become too large for leaves to implement meaningfully |
