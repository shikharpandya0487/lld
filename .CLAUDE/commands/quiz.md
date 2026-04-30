Generate a 5-question quiz on this design pattern or SOLID principle: $ARGUMENTS

---

## PHASE 1 — Post the Quiz

Output exactly 5 questions using this mix — do NOT reveal any answers:

| Q# | Type | Tests |
|---|---|---|
| Q1 | Multiple Choice | Core definition / intent |
| Q2 | Multiple Choice | Real-world analogy matching |
| Q3 | Spot the Bug | Identify the SOLID/pattern violation in Java code |
| Q4 | Fill in the Blank | Complete the missing line(s) in a Java snippet |
| Q5 | Multiple Choice | Choosing the right pattern for a given situation |

---

### Quiz — {Pattern / Principle}

Answer all 5 questions, then paste your answers (e.g. "1-B, 2-A, 3-your explanation, 4-your code, 5-C") and I'll explain each one.

---

**Q1 — Concept (Multiple Choice)**
[Question about the core definition]

A) ...  B) ...  C) ...  D) ...

---

**Q2 — Real-World Analogy (Multiple Choice)**
Which real-world situation best describes the {Pattern} pattern?

A) ...  B) ...  C) ...  D) ...

---

**Q3 — Spot the Bug**
What is wrong with the following Java code? Describe the problem in 1–2 sentences.

```java
// What is wrong here?
[15–25 lines of Java with a clear but non-obvious SOLID/pattern violation]
```

---

**Q4 — Fill in the Blank**
Complete the missing lines (marked `// ???`) to correctly apply the {Pattern} pattern.

```java
[Code snippet with 1–3 lines marked // ???]
```

---

**Q5 — When to Use (Multiple Choice)**
You are building [scenario]. Which approach is most appropriate?

A) ...  B) ...  C) ...  D) ...

---

After posting the quiz, say:
> "Take your time — there's no rush. Paste your answers when ready and I'll go through each one."

Do NOT reveal any answers before the user replies.

---

## PHASE 2 — When the User Sends Answers, Evaluate Them

Use this structure:

### Quiz Results — {Pattern / Principle}

**Your Score: X / 5**

---

For each question:

**Q{N} — Correct ✓ / Incorrect ✗**
- Your answer: {what they said}
- Correct answer: {letter + full text}
- Explanation: 3–5 sentences. WHY this is right. If wrong, explain what made the wrong option tempting and how to distinguish it next time.

---

### Summary Table

| Question | Result | Key Takeaway |
|---|---|---|
| Q1 — Concept | ✓ / ✗ | One sentence |
| Q2 — Analogy | ✓ / ✗ | One sentence |
| Q3 — Spot the Bug | ✓ / ✗ | One sentence |
| Q4 — Fill in Blank | ✓ / ✗ | One sentence |
| Q5 — When to Use | ✓ / ✗ | One sentence |

### What to Do Next
- **5/5** → "You've got a solid grasp of {Pattern}. Try `/practise {Pattern}` next."
- **3–4/5** → "Good progress. Review [specific topic missed]. Then try `/practise {Pattern}`."
- **0–2/5** → "Let's revisit the basics. Run `/explain-pattern {Pattern}`, focus on [specific section], then come back for another `/quiz {Pattern}`."

---

**Question writing rules:**
- All 4 MCQ options must be plausible — no obviously silly distractors
- Q3 bug must be a design violation (SOLID or pattern) — NOT a syntax error or NPE
- Q3 code must be 15–25 lines, with full class context
- Q4 missing lines must be the pattern's key structural mechanism
- Mark Q4 blanks with `// ???` on its own line (max 3 blanks)
- Never write two options that are equally defensible

**Grading open-ended questions:**
- Q3: correct if user identifies the core structural problem (exact wording not required); 0.5 pts if they identify the symptom but not root cause
- Q4: correct if functionally right (exact syntax not required)
- Always show the model answer for Q3 and Q4 even when the user gets them right

**Tone:** Start results with the score in a neutral encouraging tone. For wrong answers: explain what the correct answer MEANS, not just what it is. For correct answers: add one extra insight.
