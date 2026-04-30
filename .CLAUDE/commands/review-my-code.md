Review the following Java code for SOLID principle violations and design pattern opportunities. If no code is pasted yet, ask the user to paste their code or provide a file path.

Code to review: $ARGUMENTS

---

Produce the review using this exact structure:

## Code Review

### Summary
2–3 sentences. What does this code do and what is its overall quality? Start with a positive observation before any critique.

---

### SOLID Principle Check

| Principle | Status | Finding |
|---|---|---|
| S — Single Responsibility | ✓ / ⚠ / ✗ | One sentence |
| O — Open/Closed | ✓ / ⚠ / ✗ | One sentence |
| L — Liskov Substitution | ✓ / ⚠ / ✗ | One sentence |
| I — Interface Segregation | ✓ / ⚠ / ✗ | One sentence |
| D — Dependency Inversion | ✓ / ⚠ / ✗ | One sentence |

Legend: ✓ Respected  ⚠ Partially respected  ✗ Violated  — Not applicable

---

### Issues Found

For each issue use this sub-structure:

#### Issue {N} — {Principle or Pattern} — {Severity: Minor / Moderate / Major}

**What's wrong:** Plain English. Name the specific class or method. Explain the consequence — what breaks, what becomes hard, what risk is introduced.

**Problematic code:**
```java
// relevant excerpt
```

**Suggested fix:**
```java
// corrected version with comments explaining each change
```

**Why this fix works:** 1–2 sentences connecting the fix back to the principle.

---

### Design Pattern Opportunities

If a pattern would help, for each suggestion:
- **Suggested Pattern:** name
- **Why:** one sentence on what specific problem it solves
- **Where to apply:** class/method/layer
- **Quick sketch:** minimal interface/class outline in a java block

If no pattern is needed, say: "No structural pattern is needed here — the code is appropriately simple for its purpose."

---

### What You Did Well
2–4 bullet points. Specific praise referencing exact classes or decisions. Never generic.

---

### Priority Fix List
Numbered, ordered by impact (most critical first).

---

Always end with:
> "Want me to show the fully refactored version? Or would you prefer to try the fixes yourself first and run `/review-my-code` again?"

---

**Java-specific things to always check:**

| Anti-Pattern | Principle | What to look for |
|---|---|---|
| `new ConcreteClass()` inside a service | DIP | Is it a DB, logger, mailer, or other infrastructure? |
| `if/else if` chain on type strings | OCP | Can a new class avoid the new `case`? |
| `UnsupportedOperationException` in subclass | LSP | Should this class even extend the parent? |
| Interface with 6+ methods, some throwing exceptions | ISP | Split into smaller focused interfaces |
| Class with save() + send() + calculate() + print() | SRP | Each verb group = its own class |
| Constructor with 5+ parameters | — | Suggest Builder pattern |
| Empty method body override | LSP | Silently does nothing = as dangerous as an exception |
| `static` mutable fields as global state | SRP+DIP | Needs Singleton or DI |

**Severity guide:**
- Major = causes bugs, crashes, or untestable code
- Moderate = causes maintenance pain as codebase grows
- Minor = style/naming issue, does not affect correctness

**Tone rules:** Never lead with criticism. Pair every issue with its fix immediately. Use "this makes it harder to…" not "this is wrong because…". If more than 5 issues exist, surface only the top 3 and say "fix these first and let's look again."
