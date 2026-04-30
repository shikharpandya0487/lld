---
name: compare-patterns
description: Generate a detailed side-by-side comparison of two design patterns or SOLID principles to help a Java beginner understand when to use which one, with code examples for both.
---

# Skill: /compare-patterns

## Purpose
Beginners frequently confuse similar patterns (Factory vs Abstract Factory, Builder vs Constructor, Adapter vs Bridge, SRP vs ISP). This skill produces a structured, code-driven side-by-side comparison that makes the difference concrete and memorable.

## Trigger
User types: `/compare-patterns <PatternA> vs <PatternB>`

Examples:
- `/compare-patterns Factory vs Abstract Factory`
- `/compare-patterns Builder vs Constructor`
- `/compare-patterns Adapter vs Bridge`
- `/compare-patterns SRP vs ISP`
- `/compare-patterns Singleton vs Static Class`
- `/compare-patterns Prototype vs Builder`

## What Claude Must Do

### Step 1 — Parse the Input
Extract both names. If either name is ambiguous (e.g. "Factory" could mean Simple Factory or Factory Method), ask one clarifying question before proceeding.

### Step 2 — Generate the Comparison Using This Exact Structure

```
## {PatternA} vs {PatternB} — What's the Difference?

### The One-Line Difference
[Single sentence that captures the sharpest distinction between the two.
Example: "Factory decides WHICH object to create; Builder decides HOW to assemble a complex object."]

---

### Quick Comparison Table

| Dimension         | {PatternA}         | {PatternB}         |
|---|---|---|
| Intent            | ...                | ...                |
| What it controls  | ...                | ...                |
| Key class/role    | ...                | ...                |
| Output            | ...                | ...                |
| When to use       | ...                | ...                |
| When NOT to use   | ...                | ...                |
| Java keyword hint | ...                | ...                |

---

### {PatternA} — Explained With Code

#### Analogy
[2–3 sentences. Real-world analogy specific to PatternA.]

#### Code Example (Java)
```java
// Full working example of PatternA
// Include main() to show usage
// Include expected output in // Output: comment
```

---

### {PatternB} — Explained With Code

#### Analogy
[2–3 sentences. Real-world analogy specific to PatternB. Different domain from PatternA's analogy.]

#### Code Example (Java)
```java
// Full working example of PatternB
// Include main() to show usage
// Include expected output in // Output: comment
```

---

### Same Problem — Two Different Solutions

[Pick ONE realistic scenario and solve it TWICE — once with PatternA, once with PatternB.
This is the most valuable section: it shows the user the exact moment they would choose one over the other.]

#### Scenario
[2–3 sentences describing the problem.]

#### Solved With {PatternA}
```java
// Solution using PatternA
```
**Why PatternA fits here:** [1–2 sentences]
**What PatternA cannot do here:** [1 sentence — its limitation in this scenario]

#### Solved With {PatternB}
```java
// Solution using PatternB
```
**Why PatternB fits here:** [1–2 sentences]
**What PatternB cannot do here:** [1 sentence — its limitation in this scenario]

---

### The Decision Flowchart

```
Ask yourself:
│
├── [Key question that separates the two patterns]
│       │
│       ├── YES → Use {PatternA}
│       │         Example: [one-line real use case]
│       │
│       └── NO  → Use {PatternB}
│                 Example: [one-line real use case]
```

---

### Common Beginner Mistake
[1–2 sentences describing the most frequent confusion between these two patterns.
Then: "The fix: remember that {PatternA} is about X, while {PatternB} is about Y."]

---

### Quick Recall Card
[3 bullet points each — the fastest way to remember the difference]

**{PatternA} in 3 bullets:**
- ...
- ...
- ...

**{PatternB} in 3 bullets:**
- ...
- ...
- ...
```

## Commonly Confused Pairs (Reference)

| Pair | Key Distinguishing Question |
|---|---|
| Factory vs Abstract Factory | Creating one product type vs creating a family of related products |
| Builder vs Constructor | Simple fixed fields vs many optional fields assembled step by step |
| Builder vs Prototype | Build from scratch vs copy an existing object |
| Adapter vs Bridge | Making incompatible things work together vs designing for variation upfront |
| Singleton vs Static Class | Need state + lazy init + inheritance vs stateless utility methods only |
| SRP vs ISP | Splitting classes by responsibility vs splitting interfaces by client need |
| OCP vs DIP | Don't modify existing code to extend vs depend on interfaces not concrete classes |
| Decorator vs Inheritance | Adding behaviour at runtime vs compile time |

## Java-Specific Rules
- Both code examples must be self-contained and runnable
- Use the same domain theme across both examples in the "Same Problem" section — do not switch domains mid-comparison
- Show constructor injection where DIP is relevant
- Label every class role with a comment: `// ConcreteCreator`, `// Product`, `// Builder`, etc.
- `// Output:` comment required for both examples

## Tone Rules
- Open with the sharpest one-line distinction — the table and code fill in the details
- Never say one pattern is "better" — always frame in terms of "fits when…"
- The "Same Problem — Two Solutions" section must be genuinely symmetric: show each pattern's strength AND limitation

## What Claude Must NOT Do
- Do not compare more than two patterns at once
- Do not skip the "Same Problem" section — it is the most educational part
- Do not use the same analogy for both patterns
- Do not write one example significantly longer than the other
