---
name: explain-pattern
description: Explain any design pattern or SOLID principle to a Java beginner using the structured format defined in CLAUDE.md. The user provides a pattern/principle name and Claude generates a full
---

# Skill: explain-pattern

## Purpose
Explain any design pattern or SOLID principle to a Java beginner using the structured format defined in CLAUDE.md. The user provides a pattern/principle name and Claude generates a full, self-contained explanation.

## Trigger
User types: `/explain-pattern <name>`

Examples:
- `/explain-pattern Builder`
- `/explain-pattern Factory`
- `/explain-pattern Liskov Substitution Principle`
- `/explain-pattern Adapter`

## What Claude Must Do

### Step 1 — Parse the input
Extract the pattern or principle name from the argument. If the argument is ambiguous (e.g. "factory" could mean Simple Factory or Factory Method or Abstract Factory), ask one clarifying question before proceeding.

### Step 2 — Generate the explanation using this exact structure

```
## {Pattern Name} — {one-line tagline}

### The Analogy
[2–3 sentence real-world analogy. Map each real-world part to the code concept explicitly.]

### What Problem Does It Solve?
[Plain English. No code yet. Describe the pain the developer feels WITHOUT this pattern. 3–5 sentences.]

### The Formal Definition
[Quote or paraphrase the canonical definition. Then rephrase in 1–2 plain sentences.]

### The Core Structure (Java)
[Minimal skeleton showing the pattern's moving parts with comments explaining each piece.]

---

### Example 1 — BAD: Without the Pattern
[Show the naive/broken approach. Include a comment block explaining exactly what goes wrong and when.]

### Example 2 — GOOD: With the Pattern Applied
[Fix the bad example using the pattern. Comment every structural decision.]

---

### Example 3 — {Real-World Domain}
[Completely different domain from Example 2. Show trigger sentence, code, and outcome.]

### Example 4 — {Real-World Domain}
[Another domain. Follow same format.]

### Example 5 — {Real-World Domain}
[Another domain. Follow same format.]

---

### How to Spot When to Use This Pattern
[Checklist table: "Signal / Code Smell" → "This pattern helps because..."]

### Common Mistakes Beginners Make
[2–4 bullet points. Each one: mistake description + the correct approach.]

### Quick Reference
[Summary table: Intent | Key Classes/Roles | Java Keyword Hints | When to Use | When NOT to Use]
```

### Step 3 — Java-specific rules for code
- All code must compile with Java 11+
- Use `System.out.println()` for output — no external libraries
- Every example must have a usage block (`main()` or a comment showing how to instantiate)
- Show expected output in an `// Output:` comment when helpful
- Use `interface` for the abstraction role — not abstract classes in primary examples
- Constructor injection when demonstrating Dependency Inversion
- Do NOT use `{ ... }` placeholders in the primary examples (Examples 1–2); they are acceptable in Examples 3–5 for brevity after the concept is established

### Step 4 — Tone and language rules
- Start with the analogy — never with the definition
- Bold every new term on first use and define it immediately
- Never use: "simply", "just", "obviously", "trivially", "as you know"
- One idea per sentence
- Write as if explaining to a smart friend who has never studied design patterns

## What Claude Must NOT Do
- Do not skip the BAD example
- Do not collapse Examples 3–5 into one combined snippet
- Do not use UML as the primary explanation (code first, always)
- Do not assume the user knows what "polymorphism", "coupling", "abstraction", or "instantiation" mean — define on first use
- Do not add unnecessary boilerplate (package declarations, import lists) unless the import is part of the lesson

## Output Format
Markdown. All code in fenced ` ```java ``` ` blocks. Headings, tables, and bold text as specified above.
