---
name: quiz
description: Generate 4-5 beginner-friendly quiz questions (MCQ + spot-the-bug) on a design pattern or SOLID principle, then evaluate the user's answers with detailed explanations.
---

# Skill: quiz

## Purpose
Test the user's understanding of a design pattern or SOLID principle through a mix of multiple-choice and "what's wrong with this code?" questions. Immediate, detailed answer explanations reinforce learning far better than passive reading.

## Trigger
User types: `/quiz <pattern-or-principle>`

Examples:
- `/quiz Singleton`
- `/quiz OCP`
- `/quiz Builder`
- `/quiz LSP`
- `/quiz Factory`

## What Claude Must Do

### Phase 1 — Generate the Quiz

Output exactly 5 questions using this mix:

| Question # | Type | What it tests |
|---|---|---|
| Q1 | Multiple Choice (concept) | Core definition / intent of the pattern |
| Q2 | Multiple Choice (analogy) | Real-world mapping — which scenario matches the pattern? |
| Q3 | Spot the Bug | "What's wrong with this Java code?" — identify the violation |
| Q4 | Fill in the Blank | Complete the missing line(s) in a Java code snippet |
| Q5 | Multiple Choice (when to use) | Choosing the right pattern for a given situation |

#### Question Format

```
## Quiz — {Pattern / Principle}

Answer all 5 questions, then paste your answers (e.g. "1-B, 2-A, 3-see below, 4-see below, 5-C") and I'll explain each one.

---

### Q1 — Concept (Multiple Choice)
[Question stem]

A) [Option]
B) [Option]
C) [Option]
D) [Option]

---

### Q2 — Real-World Analogy (Multiple Choice)
Which of the following real-world situations best describes the {Pattern} pattern?

A) [Analogy]
B) [Analogy]
C) [Analogy]
D) [Analogy]

---

### Q3 — Spot the Bug
What is wrong with the following Java code? Describe the problem in 1–2 sentences.

```java
[~15–25 lines of Java with a clear but non-obvious violation]
```

---

### Q4 — Fill in the Blank
Complete the missing lines marked with `// ???` to correctly apply the {Pattern} pattern.

```java
[Code snippet with 1–3 missing lines marked with // ???]
```

---

### Q5 — When to Use (Multiple Choice)
You are building a {scenario}. Which approach is most appropriate?

A) [Option — wrong approach]
B) [Option — correct approach]
C) [Option — wrong approach]
D) [Option — overcomplicated approach]

---

> When you're ready, reply with your answers!
```

### Phase 2 — Wait for the User's Answers

After posting the quiz, say:

> "Take your time — there's no rush. When you're ready, paste your answers and I'll go through each one."

Do NOT reveal any answers before the user replies.

### Phase 3 — Evaluate and Explain

When the user sends answers, evaluate every question using this structure:

```
## Quiz Results — {Pattern / Principle}

### Your Score: {X} / 5

---

### Q1 — {Correct / Incorrect}
**Your answer:** {what they said}
**Correct answer:** {letter + full text}
**Explanation:** [3–5 sentences. Explain WHY this is the right answer. If they got it wrong, explain what made the wrong option tempting and how to distinguish it from the right one.]

---

[Repeat for Q2–Q5]

---

### Summary

| Question | Result | Key Takeaway |
|---|---|---|
| Q1 — Concept | ✓ / ✗ | [One sentence] |
| Q2 — Analogy | ✓ / ✗ | [One sentence] |
| Q3 — Spot the Bug | ✓ / ✗ | [One sentence] |
| Q4 — Fill in Blank | ✓ / ✗ | [One sentence] |
| Q5 — When to Use | ✓ / ✗ | [One sentence] |

---

### What to Do Next
[Based on score:]
- 5/5 → "Excellent! You've got a solid grasp of {Pattern}. Ready for `/practise {Pattern}`?"
- 3–4/5 → "Good progress. Review: [name the specific topic they got wrong]. Then try `/practise {Pattern}`."
- 0–2/5 → "Let's revisit the basics. Try `/explain-pattern {Pattern}` again, focus on [specific section], then come back for another `/quiz`."
```

## Question Writing Rules

### For MCQ Questions
- All four options must be plausible — never include an obviously silly distractor
- Exactly one option must be unambiguously correct
- Wrong options should represent common beginner misconceptions, not random noise
- Avoid trick questions based on wording — the test is conceptual understanding, not reading comprehension

### For Q3 — Spot the Bug
- The violation must be clearly present but not instantly obvious (a beginner should need to think)
- The code must be 15–25 lines — long enough to be realistic, short enough to read quickly
- Include all necessary context (the full class, not just the method)
- The bug must be a SOLID or pattern violation — not a syntax error or NullPointerException
- Label the class with a comment like `// What is wrong here?` at the top

### For Q4 — Fill in the Blank
- Provide enough surrounding context that the user understands what they're completing
- Missing lines must be the structurally significant ones (the pattern's key mechanism)
- Mark missing lines with `// ???` on its own line
- Maximum 3 missing lines

## Pattern-Specific Question Ideas

| Pattern | Good Q3 Bug Topics | Good Q4 Fill-in Topics |
|---|---|---|
| Singleton | Missing `private` constructor, non-static instance, no thread safety | `volatile` keyword, double-checked lock second null check |
| Factory | `if/else` on type inside client code, `new` in caller | The `return` statement in the factory method |
| Abstract Factory | Cross-family product mix, concrete factory named in client | Factory field declaration type (interface, not concrete) |
| Builder | Constructor with 6 params, no `build()` method, mutable returned object | `return this` in setter, `build()` method body |
| Prototype | Shallow copy of a mutable field (reference copied, not cloned) | `super.clone()` call, field deep-copy line |
| Adapter | Target interface method missing in adapter, wrong delegation | `adaptee.oldMethod()` call inside the adapter |
| SRP | God class with save + send + calculate | New class name for extracted responsibility |
| OCP | `instanceof` check or `if/else` on type string | New class implementing the interface |
| LSP | `UnsupportedOperationException` in subclass override | Fixed hierarchy — no override, separate interface |
| ISP | Fat interface with empty stub implementations | Correct interface to implement for the specific class |
| DIP | `new ConcreteClass()` inside business logic | Constructor parameter type (interface, not concrete) |

## Grading for Q3 and Q4 (Open-Ended)
- Q3 is correct if the user identifies the core structural problem (not necessarily the exact wording)
- Q3 is partially correct (0.5 points) if they identify the symptom but not the root cause
- Q4 is correct if the missing line(s) are functionally right — exact syntax not required
- Always show the model answer for Q3 and Q4 even when the user gets them right

## Tone Rules
- Start Phase 3 with the score in a neutral, encouraging tone — not "Great!" or "Oh no"
- For wrong answers: explain what the correct answer means, not just what it is
- For correct answers: reinforce with one extra insight they may not have considered
- The "What to Do Next" section must always offer a concrete next action

## What Claude Must NOT Do
- Do not reveal answers in Phase 1 — not even hints
- Do not give partial answers in Phase 2 (waiting phase)
- Do not write questions where two options are equally defensible
- Do not recycle questions from a previous `/quiz` call in the same conversation
- Do not write a Q3 bug that is a syntax error — it must be a design violation

---

## Mandatory File Save (MUST DO — not optional)

All quiz output is saved to a single markdown file that is updated across both phases.

### Phase 1 — Save the Quiz Questions

Immediately after generating the quiz, save it to:

- **Path:** `c:\Extras\LLD\SOLID\quiz_<name_snake_case>.md`
- **Example:** `/quiz Composite Pattern` → `quiz_composite_pattern.md`

### Phase 3 — Save the Results

Immediately after generating the evaluation, **append** the full results section to the same file:

- Read the existing `quiz_<name_snake_case>.md`, append a `---` separator, then append the complete Phase 3 output (score, per-question explanations, summary table, what to do next).

### Rules for Both Phases
1. Save every section — all 5 questions with full options, and (in Phase 3) every explanation, the summary table, and the next-steps recommendation.
2. If the file already exists (e.g. user retries the quiz), overwrite it in Phase 1 with the new questions.
3. Do not wait for the user to ask — save immediately after generating each phase's output.
