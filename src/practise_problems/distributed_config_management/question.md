# Practice Problem: Notification Dispatch System (Singleton + Factory + Builder)

## Background

You are building a notification service for a backend application. The system must support multiple
notification channels — Email, SMS, and Push. A central dispatcher manages all notification sending,
and it must be a single shared instance across the application.

Each notification type has different fields: Email needs a subject and body, SMS just needs a message,
and Push needs a title, body, and a device token. Once a notification is built, it should be immutable.

---

## Requirements

### 1. NotificationDispatcher (Singleton)

- Exactly **one** instance must exist across the application.
- Must be **thread-safe**.
- No direct instantiation via `new NotificationDispatcher()`.
- Provide a static `getInstance()` method.

---

### 2. Notification (Builder Pattern)

Each notification type has its own builder. Required fields go in the builder's constructor —
optional fields get setter-style chaining methods. Calling `build()` returns an immutable object.

| Type    | Required Fields        | Optional Fields      |
|---------|------------------------|----------------------|
| `EMAIL` | `to`, `subject`        | `body`, `cc`         |
| `SMS`   | `phoneNumber`          | `message`            |
| `PUSH`  | `deviceToken`, `title` | `body`               |

---

### 3. NotificationFactory (Factory Pattern)

The dispatcher uses a `NotificationFactory` to get the right builder based on a `NotificationType` enum.

- `getBuilder(NotificationType type)` — returns the appropriate builder.
- Throws `IllegalArgumentException` for unsupported types.

---

### 4. NotificationDispatcher behavior

- `send(Notification notification)` — dispatches the notification and prints output.
- Output format per type:

| Type    | Output format                                          |
|---------|--------------------------------------------------------|
| `EMAIL` | `[EMAIL] To: <to> | Subject: <subject> | Body: <body>` |
| `SMS`   | `[SMS] To: <phone> | Message: <message>`               |
| `PUSH`  | `[PUSH] Device: <token> | Title: <title> | Body: <body>`|

---

## Example Usage

```java
NotificationDispatcher dispatcher = NotificationDispatcher.getInstance();
NotificationFactory factory = new NotificationFactory();

Notification email = factory.getBuilder(NotificationType.EMAIL)
    .required("user@example.com", "Welcome!")
    .body("Thanks for signing up.")
    .cc("admin@example.com")
    .build();

dispatcher.send(email);
// Output: [EMAIL] To: user@example.com | Subject: Welcome! | Body: Thanks for signing up.

Notification sms = factory.getBuilder(NotificationType.SMS)
    .required("+919999999999")
    .message("Your OTP is 4821")
    .build();

dispatcher.send(sms);
// Output: [SMS] To: +919999999999 | Message: Your OTP is 4821

Notification push = factory.getBuilder(NotificationType.PUSH)
    .required("device-token-xyz", "New Message")
    .body("You have 3 unread messages.")
    .build();

dispatcher.send(push);
// Output: [PUSH] Device: device-token-xyz | Title: New Message | Body: You have 3 unread messages.
```

---

## Questions to Answer

1. How do you make `NotificationDispatcher` thread-safe without locking on every `getInstance()` call?
2. What happens if someone calls `build()` without setting required fields — how does your design prevent it?
3. Should `NotificationFactory` be a Singleton or a stateless utility class? Justify your choice.
4. Trace the call chain from `dispatcher.send(...)` back to where the object was created — which pattern kicks in at each step?

---

## Hints

> **Hint 1 — Singleton:** Use the initialization-on-demand holder idiom (static inner class) for
> lazy, thread-safe singleton without synchronized overhead.

> **Hint 2 — Builder:** Put required fields in the builder's constructor, not as chained setters.
> This way the compiler enforces them — you can't call `.build()` without them.

> **Hint 3 — Factory:** The factory returns a *builder*, not a built object. The caller fills in
> optional fields before calling `.build()`. The factory itself holds no state — it's a stateless utility.

> **Hint 4 — Putting it together:** `Dispatcher` (Singleton) → calls `Factory` → gets a `Builder` →
> caller configures it → `.build()` produces an immutable `Notification` → `Dispatcher.send()` handles it.

---

## Bonus

- Add a `sendAll(String level, Notification notification)` method on the dispatcher that broadcasts
  to all registered channels at once (similar to `logAll` in the logger problem).
- Cache builder instances inside the factory so the same type always gets the same builder class
  (but not the same builder instance — why?).
