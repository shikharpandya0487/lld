# Brain-Teaser: Distributed Configuration Management System (Singleton + Factory + Builder)

## Problem Statement

Design a **centralized configuration management system** for a microservices application. The system must:

1. **Maintain a single source of truth** for all configurations across the app lifetime.
2. Support multiple **config source types**: `FileConfig`, `EnvConfig`, and `RemoteConfig` (e.g., fetched from a URL).
3. Each config source type has **different initialization parameters** — file needs a path, remote needs a URL + timeout + retry count, env needs a namespace prefix.
4. Once a config is built and registered, it must be **immutable**.
5. The system should allow fetching a config source **by type** without the caller knowing the concrete class.

**The twist:** The `ConfigManager` (Singleton) holds a registry of config sources. But config sources are created via a `ConfigSourceFactory`, and each config source is constructed using its own **Builder**. The factory must decide *which builder to use* based on a source type string passed at runtime.

**Your task:** Design and implement the class structure. Then answer:
- How do you prevent someone from calling `new ConfigManager()` in a multithreaded environment?
- What happens if `RemoteConfig` builder is called without setting the URL — how do you enforce required fields?
- Where exactly do Singleton, Factory, and Builder interact — can you draw the call chain?

---

## Hints

> **Hint 1 — Singleton:** Think about *when* the instance is created. Lazy initialization needs `synchronized` or a holder class to be thread-safe in Java.

> **Hint 2 — Factory:** The factory's job is just to pick the right builder and trigger the build — it shouldn't set fields itself. What should it return — a built object or a builder?

> **Hint 3 — Builder:** Distinguish between *required* and *optional* fields. Required fields go in the **builder's constructor**, optional ones get setter-style methods. This forces compile-time safety.

> **Hint 4 — The tricky part:** `ConfigManager` is a Singleton, but `ConfigSourceFactory` — should it also be a Singleton, or a stateless utility? Think about what state, if any, it holds.

---

Start with the `ConfigSource` interface and work outward. Good luck!
