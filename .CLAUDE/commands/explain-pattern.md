Explain the design pattern or SOLID principle: $ARGUMENTS

Follow this exact structure in your response:

1. **The Analogy** — 2–3 sentences using a real-world comparison. Map each real-world part to the code concept explicitly. Never start with the definition.

2. **What Problem Does It Solve?** — Plain English only, no code yet. Describe the pain a developer feels WITHOUT this pattern (3–5 sentences).

3. **The Formal Definition** — Quote or paraphrase the canonical definition, then immediately rephrase it in 1–2 plain sentences a beginner can understand.

4. **The Core Structure (Java)** — A minimal skeleton showing all the pattern's moving parts. Comment every class/method explaining its role (e.g. `// This is the Creator — it decides which product to make`).

5. **Example 1 — BAD: Without the Pattern** — Show the naive/broken approach. At the top of the class write `// BAD`. Include a comment block explaining what goes wrong and WHEN it breaks (runtime crash? compile error? silent wrong value?).

6. **Example 2 — GOOD: With the Pattern Applied** — Fix the bad example. Comment every structural decision explaining why it is done that way.

7. **Example 3, 4, 5 — Real-World Domains** — Three completely different domains (e.g. banking, food delivery, hospital, streaming, ride-sharing). For each: one trigger sentence ("Your manager says: add a new payment method..."), then working Java code, then one sentence on the outcome. Use `{ ... }` for method bodies only in these examples (after concept is established).

8. **How to Spot When to Use This Pattern** — A markdown table: `Signal / Code Smell` → `This pattern helps because...`

9. **Common Mistakes Beginners Make** — 2–4 bullet points. Each: mistake + the correct approach side by side.

10. **Quick Reference** — A summary table with columns: Intent | Key Classes/Roles | Java Keyword Hints | When to Use | When NOT to Use

**Java rules for all code:**
- Java 11+ syntax, no external libraries, `System.out.println()` for output
- Every example must have a `main()` or usage block
- Show expected output in `// Output:` comments
- Use `interface` for abstractions (not abstract classes) in primary examples
- Use constructor injection when showing Dependency Inversion
- Bold every new term on first use and define it immediately inline
- Never use the words: "simply", "just", "obviously", "trivially", "as you know"
- One idea per sentence throughout

If the pattern name is ambiguous (e.g. "Factory" could mean Simple Factory, Factory Method, or Abstract Factory), ask ONE clarifying question before generating anything.
