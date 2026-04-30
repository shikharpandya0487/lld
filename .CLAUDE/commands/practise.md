Generate a hands-on Java coding challenge for: $ARGUMENTS

---

## PHASE 1 — Post the Problem

Output the problem using this exact structure:

### Practice Problem — {Pattern / Principle}

**Scenario** — 2–3 sentences describing a realistic situation. Use a domain NOT used in the standard explanation examples. Choose from: food delivery, hospital management, ride-sharing, streaming platform, school management, hotel booking.

**Your Task** — A numbered list of exactly 3–5 things to implement. Be specific: name the classes, method signatures, and expected behaviour.

**Starter Code** — A Java skeleton with class/interface names and method stubs marked `// TODO: implement`. Do NOT implement anything. Show the expected usage in `main()` as a stub (e.g. `new Pizza.Builder("Large").addCheese().build()` but leave it blank).

**Constraints:**
- Java 11+ syntax, no external libraries
- Must include a working `main()` that demonstrates the feature
- Any pattern-specific constraint (e.g. "constructor must be private" for Singleton)

**Hint** (read only if stuck):
> One sentence pointing toward the right structural decision — no answer, just direction.

---

After posting the problem, say exactly:
> "Take your time. Paste your solution when you're ready and I'll review it."

Do NOT provide the solution. Wait for the user to reply with their code.

---

## PHASE 2 — When the User Pastes Their Code, Review It

Use this structure:

### What You Got Right ✓
2–4 specific things done correctly. Reference exact class/method names from their code. Be specific — not "good structure" but "your Builder correctly returns `this` from each setter, enabling method chaining".

### Issues to Fix ✗
For each issue:
- Name the specific class or method
- Explain WHY it is wrong in plain English (what breaks? what becomes hard?)
- Show the corrected version in a `java` code block
- Explain what the fix achieves in 1–2 sentences

Number each issue. If there are none, say so clearly and go straight to Stretch Goals.

### Pattern Checklist
A table: each structural requirement of the pattern | ✓ Present / ✗ Missing / ⚠ Partial

### Stretch Goals
1–2 small optional extensions (e.g. "add thread safety", "add a second ConcreteCreator", "make build() validate required fields").

---

**Tone rules:**
- Lead with what they got right — confidence before corrections
- Never say "wrong" without immediately pairing it with the fix
- If the solution is completely off-pattern, ask ONE guiding question rather than rewriting everything
- Celebrate correct solutions explicitly: "This is exactly how a Factory should be structured."

**Domain guide by pattern:**
- SRP → hospital management | OCP → streaming platform | LSP → ride-sharing
- ISP → smart home devices | DIP → school management | Singleton → hotel booking
- Factory → food delivery | Abstract Factory → theme UI | Builder → resume generator
- Prototype → game character cloning | Adapter → legacy payment gateway | Bridge → device + remote combos
