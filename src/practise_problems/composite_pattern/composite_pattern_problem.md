# Practice Problem — Composite Pattern

## Scenario

You are building a **hospital management system**. A hospital is divided into Departments (e.g. Cardiology, Neurology). Each department has Doctors. Some departments also contain Sub-Departments (e.g. Cardiology contains Pediatric Cardiology). The hospital administrator needs to run a single `showDetails()` call on any node — whether it is one doctor or the entire hospital — and get a complete, indented breakdown.

---

## Your Task

1. Create a `HospitalComponent` interface with a single method: `void showDetails(String indent)`.
2. Create a `Doctor` class (the **Leaf**) that implements `HospitalComponent`. It should store the doctor's name and specialisation and print them when `showDetails()` is called.
3. Create a `Department` class (the **Composite**) that implements `HospitalComponent`. It should store a department name, maintain a list of `HospitalComponent` children, expose an `add(HospitalComponent component)` method, and delegate `showDetails()` to all its children.
4. Demonstrate a two-level hierarchy in `main()`: one top-level `Department` (the hospital), two child `Department`s, and at least one `Doctor` in each child department.
5. Call `showDetails("")` on the top-level hospital object and verify the output is properly indented.

---

## Starter Code

```java
import java.util.ArrayList;
import java.util.List;

// Step 1 — The shared contract
interface HospitalComponent {
    void showDetails(String indent);
}

// Step 2 — Leaf
class Doctor implements HospitalComponent {
    private String name;
    private String specialisation;

    Doctor(String name, String specialisation) {
        // TODO: implement
    }

    @Override
    public void showDetails(String indent) {
        // TODO: implement
    }
}

// Step 3 — Composite
class Department implements HospitalComponent {
    private String name;
    private List<HospitalComponent> components = new ArrayList<>();

    Department(String name) {
        // TODO: implement
    }

    public void add(HospitalComponent component) {
        // TODO: implement
    }

    @Override
    public void showDetails(String indent) {
        // TODO: implement — print department name, then delegate to each child
    }
}

// Step 4 — Usage
class Main {
    public static void main(String[] args) {
        // Build the tree here
        // e.g. Department hospital = new Department("City Hospital");
        //      Department cardiology = new Department("Cardiology");
        //      cardiology.add(new Doctor(...));
        //      hospital.add(cardiology);
        //      hospital.showDetails("");
    }
}
```

---

## Constraints

- Java 11+ syntax
- No external libraries
- `main()` must build the full tree and call `showDetails("")` on the root
- The `Doctor` class must **not** have an `add()` method — leaves have no children

---

## Hint (read only if stuck)

> The `Department.showDetails()` method should print its own name first, then loop through its `components` list and call `showDetails(indent + "  ")` on each one — the indentation grows deeper at each level automatically.

---

---

# Code Review — Composite Pattern

## What You Got Right ✓

1. **`HospitalComponent` is a clean, minimal interface.** It only declares `showdetails()` — the one operation shared by both leaf and composite. `add()` and `remove()` are correctly placed only on `Department`, not on the interface.

2. **`Doctor` is a proper Leaf.** It stores its own data (`name`, `specialization`), has no children, and does the real work directly in `showdetails()`. No `add()` method anywhere near it.

3. **`Department` delegates correctly.** The `for` loop in `showdetails()` calls `component.showdetails()` on every child without any `instanceof` check. This is the heart of the pattern working correctly.

4. **`HospitalManager.main()` demonstrates nesting.** A two-level tree — hospital → departments → doctors — is built and `showdetails()` is called on the root. The hierarchy is clear and runs correctly.

---

## Issues to Fix ✗

### Issue 1 — Missing indentation in `showdetails()`

The method signature `void showdetails()` has no `indent` parameter. All output prints at the same column, making it impossible to visually see the tree depth.

**Corrected version:**

```java
// In the interface
public interface HospitalComponent {
    void showDetails(String indent);
}

// In Doctor
@Override
public void showDetails(String indent) {
    System.out.println(indent + "Doctor: " + name + ", Specialization: " + specialization);
}

// In Department
@Override
public void showDetails(String indent) {
    System.out.println(indent + "Department: " + name);
    for (HospitalComponent component : components) {
        component.showDetails(indent + "  "); // two more spaces at each level
    }
}

// In main
hospital.showDetails("");
```

Expected output:
```
Department: City Hospital
  Department: Cardiology
    Doctor: Dr. Smith, Specialization: Cardiologist
  Department: Neurology
    Doctor: Dr. Johnson, Specialization: Neurologist
```

### Issue 2 — Method name casing: `showdetails` → `showDetails`

Java convention is camelCase for method names. Rename to `showDetails` everywhere (interface + both classes).

---

## Pattern Checklist

| Structural Requirement | Status |
|---|---|
| Shared interface with the common operation | ✓ Present |
| Leaf implements interface, no children | ✓ Present |
| Composite implements interface, holds `List<Component>` | ✓ Present |
| Composite delegates operation to children (no `instanceof`) | ✓ Present |
| `add()`/`remove()` only on Composite, not on interface | ✓ Present |
| Indentation to visualise tree depth | ✗ Missing |
| Composite can hold other Composites (tested in `main`) | ✓ Present |

---

## Stretch Goals

1. **Add a Sub-Department.** Create a `PediatricCardiology` department, add a doctor to it, then add it inside `Cardiology`. Call `hospital.showDetails("")` and verify a three-level tree prints correctly — this proves the recursion works at arbitrary depth.

2. **Test `removeComponent` in `main`.** Add a doctor, call `showDetails("")`, remove the doctor, and call `showDetails("")` again. Verify the doctor disappears.
