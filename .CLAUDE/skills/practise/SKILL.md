---
name: practise
description: Generate a hands-on Java coding challenge for a design pattern or SOLID principle the user has studied, then review their submitted solution with beginner-friendly feedback.
---

# Skill: practise

## Purpose
Give the user a hands-on coding challenge based on a design pattern or SOLID principle they have studied. The user writes the solution; Claude then reviews it. Active recall through coding is the highest-ROI learning activity for beginners.

## Trigger
User types: `/practise <pattern-or-principle>`

Examples:
- `/practise Builder`
- `/practise Factory`
- `/practise SRP`
- `/practise Adapter`
- `/practise OCP`

## What Claude Must Do

### Phase 1 — Generate the Problem

When the user invokes the skill, output a problem statement using this exact structure:

```
## Practice Problem — {Pattern / Principle}

### Scenario
[2–3 sentences describing a realistic situation. Use a domain different from the examples
in the main explanation (so the user cannot copy-paste). Choose from: food delivery,
hospital management, ride-sharing, streaming platform, school management, hotel booking.]

### Your Task
[Numbered list of exactly what the user must implement. Be specific about class names,
method signatures, and expected behaviour. 3–5 tasks.]

### Starter Code
[Provide a minimal Java skeleton — just class/interface names and method stubs with
TODO comments. No implementation. This gives the user a scaffold without giving away
the solution.]

### Constraints
- Use Java 11+ syntax
- No external libraries
- Must include a `main()` method that demonstrates the feature
- [Any pattern-specific constraint, e.g. "the constructor must be private" for Singleton]

### Hint (read only if stuck)
> [One sentence that points toward the right structural decision without giving the
> answer away. Wrap in a blockquote so the user can skip it easily.]
```

### Phase 2 — Wait for the User's Solution

After posting the problem, say exactly:

> "Take your time. Paste your solution when you're ready and I'll review it."

Do NOT provide the solution unprompted. Wait for the user to reply with their code.

### Phase 3 — Review the User's Solution

When the user pastes their code, review it using this structure:

```
## Code Review — {Pattern / Principle}

### What You Got Right ✓
[List 2–4 specific things done correctly. Reference exact class/method names from
their code. Be specific — not "good structure" but "your Builder correctly returns
`this` from each setter, enabling method chaining".]

### Issues to Fix ✗
[For each issue:
- Quote or name the specific line/class
- Explain WHY it is wrong in plain English
- Show the corrected version in a ```java block
- Explain what the fix achieves

Number each issue. If there are no issues, say so clearly and move to Stretch Goals.]

### Pattern Checklist
[Table: each structural requirement of the pattern | ✓ Present / ✗ Missing / ⚠ Partial]

### Stretch Goals (optional next step)
[1–2 small extensions to make the solution more complete. Examples: add thread safety
to Singleton, add a second ConcreteCreator to Factory, add a reset() to Builder.]
```

## Difficulty Scaling by Pattern

| Pattern / Principle | Recommended Domain | Key constraint to test |
|---|---|---|
| SRP | Hospital management | Split a god class into focused services |
| OCP | Streaming platform | Add new content type without touching existing code |
| LSP | Ride-sharing | Ensure subclass does not throw on inherited method |
| ISP | Smart home devices | Split fat interface; robot/device implements subset |
| DIP | School management | Business logic must not name any concrete infrastructure class |
| Singleton | Hotel booking | Thread-safe; prove same instance with `==` check |
| Factory | Food delivery | Add new food type without modifying the factory caller |
| Abstract Factory | Theme UI (dark/light) | Swap entire family of objects with one line change |
| Builder | Resume generator | At least 5 optional fields; chaining; `build()` validates required fields |
| Prototype | Game character cloning | `clone()` must produce independent copy (deep clone) |
| Adapter | Legacy payment gateway | Wrap old interface behind new standard interface |
| Bridge | Device + remote combos | Abstraction and implementation vary independently |

## Java-Specific Rules for Starter Code
- Provide real method signatures with correct return types — not pseudocode
- Include `// TODO: implement` comments inside method stubs
- Do NOT add `System.out.println` in stubs — let the user decide the output
- The `main()` stub must show the expected usage pattern (e.g. `new Pizza.Builder("Large")...build()`) but leave the implementation empty

## Tone During Review
- Lead with what they got right — beginners need confidence before corrections
- Never say "wrong" alone — always pair it with the specific fix
- If the solution is completely off-pattern, ask one guiding question rather than rewriting everything: "What should happen if someone calls `new X()` directly?"
- Celebrate correct solutions explicitly: "This is exactly how a Factory should be structured."

## What Claude Must NOT Do
- Do not give the full solution in Phase 1
- Do not skip Phase 1 and jump straight to a solution
- Do not review the code before the user submits it
- Do not use a domain already used in the `/explain-pattern` output for the same pattern
- Do not mark something as wrong if it is an equally valid implementation (e.g. Holder pattern vs Enum for Singleton)
