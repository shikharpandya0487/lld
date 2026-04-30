---
name: review-my-code
description: Review Java code the user pastes and give detailed beginner-friendly feedback covering SOLID violations, applicable design patterns, and line-specific actionable suggestions.
---

# Skill: /review-my-code

## Purpose
Review Java code the user pastes and give detailed, beginner-friendly feedback covering:
1. Which SOLID principles are violated (if any) and exactly where
2. Which design pattern could improve the code (if applicable)
3. Line-specific, actionable suggestions in plain English

## Trigger
User types: `/review-my-code` followed by a paste of Java code.

Alternatively the user may mention a file path (e.g. "review src/ocp/Cart.java") — in that case, read the file before reviewing.

## What Claude Must Do

### Step 1 — Read and Understand the Code
Before writing anything, read the full code. Identify:
- All classes and their responsibilities
- All dependencies between classes
- Any use of `new ConcreteClass()` inside business logic
- Any fat interfaces or multi-responsibility classes
- Any inheritance hierarchies that may violate LSP

If the code is incomplete (stubs only), say: "I can see the structure but not the full implementation — I'll review what's visible and note where I need more context."

### Step 2 — Output the Review Using This Exact Structure

```
## Code Review

### Summary
[2–3 sentences. What does this code do? What is its overall quality?
Use encouraging language — start with a positive observation before any critique.]

---

### SOLID Principle Check

| Principle | Status | Finding |
|---|---|---|
| S — Single Responsibility | ✓ / ⚠ / ✗ | [One sentence] |
| O — Open/Closed           | ✓ / ⚠ / ✗ | [One sentence] |
| L — Liskov Substitution   | ✓ / ⚠ / ✗ | [One sentence] |
| I — Interface Segregation | ✓ / ⚠ / ✗ | [One sentence] |
| D — Dependency Inversion  | ✓ / ⚠ / ✗ | [One sentence] |

Legend: ✓ Respected  ⚠ Partially respected  ✗ Violated  — Not applicable

---

### Issues Found

[For each issue:]

#### Issue {N} — {Principle or Pattern} — {Severity: Minor / Moderate / Major}

**What's wrong:**
[Plain English. Quote the specific class name or method. Explain the consequence — what breaks, what becomes hard, what risk is introduced.]

**Problematic code:**
```java
// relevant excerpt from the user's code
```

**Suggested fix:**
```java
// corrected version with comments explaining each change
```

**Why this fix works:**
[1–2 sentences connecting the fix back to the principle or pattern.]

---

### Design Pattern Opportunities

[If a pattern would help:]

#### Suggested Pattern: {Pattern Name}
**Why:** [One sentence — what specific problem does this pattern address?]
**Where to apply:** [Class name / method / layer]
**Quick sketch:**
```java
// Minimal interface/class sketch — not a full implementation
```

[If no pattern is needed: "No structural pattern is needed here — the code is appropriately simple for its purpose."]

---

### What You Did Well
[2–4 bullet points. Specific praise referencing exact classes or decisions.]

---

### Priority Fix List
[Numbered list of what to fix first, in order of impact.]
1. [Most critical]
2. ...
```

### Step 3 — Offer a Follow-Up
Always end with:

> "Want me to show the fully refactored version? Or would you prefer to try the fixes yourself first and call `/review-my-code` again?"

## Severity Definitions

| Severity | Meaning |
|---|---|
| **Major** | Will cause bugs, crashes, or impossible-to-test code in a real system |
| **Moderate** | Will cause maintainability pain as the codebase grows |
| **Minor** | Style or naming issue; does not affect correctness but reduces readability |

## Java-Specific Review Checklist

Always check for these Java-specific SOLID violations:

| Java Anti-Pattern | Principle Violated | What to Check |
|---|---|---|
| `new ConcreteClass()` inside a service/business class | DIP | Is it a DB, logger, mailer, or other infrastructure? |
| `if/else if` chain on type strings or enums for behaviour | OCP | Can adding a new class avoid the new `case`? |
| Method throws `UnsupportedOperationException` in a subclass | LSP | Should this class even extend the parent? |
| Interface with 6+ methods, some implemented as `throw new UnsupportedOperationException()` | ISP | Split into smaller focused interfaces |
| One class with `save()`, `send()`, `calculate()`, and `print()` | SRP | Each verb group = a candidate for its own class |
| Constructor with 5+ parameters | — | Suggest Builder pattern |
| Subclass that silently does nothing in an override | LSP | Empty body is as dangerous as an exception |
| `static` mutable fields used as global state | SRP + DIP | Likely needs Singleton or DI |

## Tone Rules
- Never lead with what is wrong — always open with the Summary (which must contain a positive observation)
- Pair every criticism with the fix immediately
- Use "this makes it harder to…" instead of "this is wrong because…"
- If the code is genuinely excellent, say so explicitly and explain what makes it good
- If there are more than 5 issues, focus on the top 3 most impactful and say: "Fix these first and let's look again."

## What Claude Must NOT Do
- Do not rewrite the entire codebase — suggest targeted changes only
- Do not invent violations that are not present (mark ✓ and move on when a principle is respected)
- Do not review code that was not provided — ask the user to paste it
- Do not narrate what the user's code does line by line — focus on design quality
- Do not mark a valid alternative implementation as wrong
