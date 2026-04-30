Compare the two design patterns or SOLID principles: $ARGUMENTS

If either name is ambiguous (e.g. "Factory" could mean Simple Factory or Factory Method), ask ONE clarifying question before proceeding.

---

Generate the comparison using this exact structure:

## {PatternA} vs {PatternB} — What's the Difference?

### The One-Line Difference
A single sentence capturing the sharpest distinction. Example: "Factory decides WHICH object to create; Builder decides HOW to assemble a complex object step by step."

---

### Quick Comparison Table

| Dimension | {PatternA} | {PatternB} |
|---|---|---|
| Intent | ... | ... |
| What it controls | ... | ... |
| Key class / role | ... | ... |
| Output | ... | ... |
| When to use | ... | ... |
| When NOT to use | ... | ... |
| Java keyword hint | ... | ... |

---

### {PatternA} — Explained With Code

**Analogy:** 2–3 sentences. Real-world comparison specific to PatternA. Map each real-world part to a code concept.

```java
// Full working Java example of PatternA
// Include main() showing usage
// Output: (expected output as comment)
```

---

### {PatternB} — Explained With Code

**Analogy:** 2–3 sentences. Different real-world domain from PatternA's analogy.

```java
// Full working Java example of PatternB
// Include main() showing usage
// Output: (expected output as comment)
```

---

### Same Problem — Two Different Solutions

Pick ONE realistic scenario and solve it TWICE. This is the most important section — it shows the exact moment a developer would choose one over the other.

**Scenario:** 2–3 sentences describing the problem.

**Solved with {PatternA}:**
```java
// Solution using PatternA
```
- **Why PatternA fits:** 1–2 sentences
- **What PatternA cannot do here:** 1 sentence (its limitation)

**Solved with {PatternB}:**
```java
// Solution using PatternB
```
- **Why PatternB fits:** 1–2 sentences
- **What PatternB cannot do here:** 1 sentence (its limitation)

---

### The Decision Flowchart

```
Ask yourself:
│
├── [The one question that separates these two patterns]
│       │
│       ├── YES → Use {PatternA}
│       │         Example: [one-line real use case]
│       │
│       └── NO  → Use {PatternB}
│                 Example: [one-line real use case]
```

---

### Common Beginner Mistake
1–2 sentences on the most frequent confusion. Then: "The fix: remember that {PatternA} is about X, while {PatternB} is about Y."

---

### Quick Recall Card

**{PatternA} in 3 bullets:**
- ...
- ...
- ...

**{PatternB} in 3 bullets:**
- ...
- ...
- ...

---

**Rules:**
- Both code examples must be self-contained and runnable with Java 11+
- Use the same domain in the "Same Problem" section — do not switch domains mid-comparison
- Label every class role: `// ConcreteCreator`, `// Product`, `// Builder`, etc.
- Never say one pattern is "better" — always frame as "fits when…"
- Do not write one example significantly longer than the other
