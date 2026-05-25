# Builder Pattern

## What Is It?

The **Builder** is a creational design pattern that lets you construct complex objects **step by step**. Instead of a constructor that takes 10 parameters (most of which you may not need), you use a dedicated `Builder` object that only sets the fields you care about — and produces the final object when you are ready.

The finished object is typically **immutable**: once built, it cannot be modified.

---

## The Problem It Solves

Imagine a `User` class with many optional fields:

```java
// Telescoping constructor anti-pattern — which arg is which??
User u = new User("Alice", "alice@mail.com", 28, "India", null, null, true, false);
```

Problems with this:
- Hard to read — no idea what `null`, `true`, `false` mean at the call site.
- Adding a new field breaks every existing call site.
- Optional fields force you to pass `null` everywhere.

The Builder pattern fixes all three problems.

---

## Core Structure

```
Director (optional)         ← orchestrates a fixed build sequence
    │
    ▼
Builder (interface)         ← declares setXxx() methods + build()
    │
    ▼
ConcreteBuilder             ← accumulates state, returns the Product
    │
    ▼
Product                     ← the immutable object you actually wanted
```

In Java the Director is usually **skipped** — the client calls the builder methods directly in a fluent chain.

---

## Key Java Concepts Used

| Concept | Role in Builder |
|---|---|
| **Static nested class** | `Builder` lives inside the `Product` so they share private access |
| **Method chaining** | Each setter returns `this` (the builder), enabling `builder.setA().setB().build()` |
| **Immutability** | `Product` fields are `final`; the constructor is `private` |
| **Copy constructor** | `new Builder(existingProduct)` to create a pre-populated builder |

---

## Example 1 — Pizza Order (classic warm-up)

A pizza has one required field (size) and many optional toppings/extras.

```java
public class Pizza {

    // ── Final fields make the product immutable ──────────────────────────
    private final String size;          // REQUIRED
    private final String crustType;     // optional
    private final boolean extraCheese;  // optional
    private final boolean mushrooms;    // optional
    private final boolean pepperoni;    // optional
    private final String sauce;         // optional

    // Private constructor — only the inner Builder can call this
    private Pizza(Builder b) {
        this.size         = b.size;
        this.crustType    = b.crustType;
        this.extraCheese  = b.extraCheese;
        this.mushrooms    = b.mushrooms;
        this.pepperoni    = b.pepperoni;
        this.sauce        = b.sauce;
    }

    @Override
    public String toString() {
        return size + " pizza | crust=" + crustType
             + " | extraCheese=" + extraCheese
             + " | mushrooms="   + mushrooms
             + " | pepperoni="   + pepperoni
             + " | sauce="       + sauce;
    }

    // ── Static nested Builder ────────────────────────────────────────────
    public static class Builder {

        private final String size;      // Required — set in Builder constructor
        private String crustType    = "thin";
        private boolean extraCheese = false;
        private boolean mushrooms   = false;
        private boolean pepperoni   = false;
        private String sauce        = "tomato";

        public Builder(String size) {   // Only required field goes here
            this.size = size;
        }

        // Each setter returns 'this' → enables fluent chaining
        public Builder crustType(String crust)    { this.crustType    = crust;   return this; }
        public Builder extraCheese(boolean val)   { this.extraCheese  = val;     return this; }
        public Builder mushrooms(boolean val)     { this.mushrooms    = val;     return this; }
        public Builder pepperoni(boolean val)     { this.pepperoni    = val;     return this; }
        public Builder sauce(String sauce)        { this.sauce        = sauce;   return this; }

        public Pizza build() { return new Pizza(this); }
    }
}
```

**Client code:**

```java
Pizza margherita = new Pizza.Builder("Medium")
        .crustType("thick")
        .extraCheese(true)
        .sauce("pesto")
        .build();

Pizza meatLover = new Pizza.Builder("Large")
        .pepperoni(true)
        .extraCheese(true)
        .build();

System.out.println(margherita);
System.out.println(meatLover);
```

Output:
```
Medium pizza | crust=thick | extraCheese=true | mushrooms=false | pepperoni=false | sauce=pesto
Large pizza  | crust=thin  | extraCheese=true | mushrooms=false | pepperoni=true  | sauce=tomato
```

**What you gain:** every call site is self-documenting — you can read what each argument means without checking the constructor signature.

---

## Example 2 — HTTP Request Builder

Real-world HTTP clients (like OkHttp, Java's `HttpRequest`) use the Builder pattern because a request has a required URL and many optional headers, body, timeout, method, etc.

```java
public class HttpRequest {

    private final String url;           // REQUIRED
    private final String method;
    private final String body;
    private final Map<String, String> headers;
    private final int timeoutMs;
    private final boolean followRedirects;

    private HttpRequest(Builder b) {
        this.url             = b.url;
        this.method          = b.method;
        this.body            = b.body;
        this.headers         = Collections.unmodifiableMap(new HashMap<>(b.headers));
        this.timeoutMs       = b.timeoutMs;
        this.followRedirects = b.followRedirects;
    }

    @Override
    public String toString() {
        return method + " " + url
             + " | headers=" + headers
             + " | body="    + body
             + " | timeout=" + timeoutMs + "ms"
             + " | redirect=" + followRedirects;
    }

    public static class Builder {

        private final String url;
        private String method          = "GET";
        private String body            = null;
        private Map<String, String> headers = new HashMap<>();
        private int timeoutMs          = 5000;
        private boolean followRedirects = true;

        public Builder(String url) { this.url = url; }

        public Builder method(String method)             { this.method          = method;  return this; }
        public Builder body(String body)                 { this.body            = body;    return this; }
        public Builder header(String key, String value)  { this.headers.put(key, value);   return this; }
        public Builder timeoutMs(int ms)                 { this.timeoutMs       = ms;      return this; }
        public Builder followRedirects(boolean follow)   { this.followRedirects = follow;  return this; }

        public HttpRequest build() {
            // Validate before creating the object
            if (url == null || url.isBlank())
                throw new IllegalStateException("URL is required");
            if (body != null && method.equals("GET"))
                throw new IllegalStateException("GET requests cannot have a body");
            return new HttpRequest(this);
        }
    }
}
```

**Client code:**

```java
HttpRequest getReq = new HttpRequest.Builder("https://api.example.com/users")
        .header("Authorization", "Bearer token123")
        .header("Accept", "application/json")
        .timeoutMs(3000)
        .build();

HttpRequest postReq = new HttpRequest.Builder("https://api.example.com/users")
        .method("POST")
        .header("Content-Type", "application/json")
        .body("{\"name\":\"Alice\"}")
        .followRedirects(false)
        .build();

System.out.println(getReq);
System.out.println(postReq);
```

**Key insight:** The `build()` method is the right place to add **validation logic** — it prevents you from constructing an illegal state like a GET request with a body. The object is never created in an invalid state.

---

## Example 3 — SQL Query Builder

ORMs like Hibernate and JOOQ internally use a query builder. Here is a simplified version of how one works.

```java
public class SqlQuery {

    private final String table;
    private final List<String> columns;
    private final String whereClause;
    private final String orderBy;
    private final int limit;
    private final int offset;

    private SqlQuery(Builder b) {
        this.table       = b.table;
        this.columns     = Collections.unmodifiableList(new ArrayList<>(b.columns));
        this.whereClause = b.whereClause;
        this.orderBy     = b.orderBy;
        this.limit       = b.limit;
        this.offset      = b.offset;
    }

    public String toSql() {
        String cols = columns.isEmpty() ? "*" : String.join(", ", columns);
        StringBuilder sql = new StringBuilder("SELECT " + cols + " FROM " + table);
        if (whereClause != null) sql.append(" WHERE ").append(whereClause);
        if (orderBy     != null) sql.append(" ORDER BY ").append(orderBy);
        if (limit > 0)           sql.append(" LIMIT ").append(limit);
        if (offset > 0)          sql.append(" OFFSET ").append(offset);
        return sql.toString();
    }

    public static class Builder {

        private final String table;
        private List<String> columns = new ArrayList<>();
        private String whereClause   = null;
        private String orderBy       = null;
        private int limit            = 0;
        private int offset           = 0;

        public Builder(String table) { this.table = table; }

        public Builder select(String... cols)  { columns.addAll(Arrays.asList(cols)); return this; }
        public Builder where(String clause)    { this.whereClause = clause;           return this; }
        public Builder orderBy(String col)     { this.orderBy     = col;              return this; }
        public Builder limit(int n)            { this.limit       = n;                return this; }
        public Builder offset(int n)           { this.offset      = n;                return this; }

        public SqlQuery build() {
            if (table == null || table.isBlank())
                throw new IllegalStateException("Table name is required");
            return new SqlQuery(this);
        }
    }
}
```

**Client code:**

```java
SqlQuery listUsers = new SqlQuery.Builder("users")
        .select("id", "name", "email")
        .where("active = true")
        .orderBy("name ASC")
        .limit(10)
        .offset(20)
        .build();

SqlQuery allProducts = new SqlQuery.Builder("products")
        .build();  // SELECT * FROM products

System.out.println(listUsers.toSql());
System.out.println(allProducts.toSql());
```

Output:
```
SELECT id, name, email FROM users WHERE active = true ORDER BY name ASC LIMIT 10 OFFSET 20
SELECT * FROM products
```

---

## Example 4 — User Registration Form

When a new user signs up, some details (name, email) are mandatory. Others (phone, bio, avatar, address) are optional and can be filled in later.

```java
public class UserProfile {

    // Required
    private final String username;
    private final String email;
    private final String passwordHash;

    // Optional
    private final String fullName;
    private final String phoneNumber;
    private final String bio;
    private final String avatarUrl;
    private final String country;
    private final boolean emailVerified;

    private UserProfile(Builder b) {
        this.username      = b.username;
        this.email         = b.email;
        this.passwordHash  = b.passwordHash;
        this.fullName      = b.fullName;
        this.phoneNumber   = b.phoneNumber;
        this.bio           = b.bio;
        this.avatarUrl     = b.avatarUrl;
        this.country       = b.country;
        this.emailVerified = b.emailVerified;
    }

    @Override
    public String toString() {
        return "UserProfile{username='" + username + "', email='" + email
             + "', country='" + country + "', verified=" + emailVerified + "}";
    }

    public static class Builder {

        // Required
        private final String username;
        private final String email;
        private final String passwordHash;

        // Optional defaults
        private String fullName      = null;
        private String phoneNumber   = null;
        private String bio           = null;
        private String avatarUrl     = "default_avatar.png";
        private String country       = "Unknown";
        private boolean emailVerified = false;

        // All three required fields go into the Builder constructor
        public Builder(String username, String email, String passwordHash) {
            if (username == null || email == null || passwordHash == null)
                throw new IllegalArgumentException("username, email, passwordHash are required");
            this.username     = username;
            this.email        = email;
            this.passwordHash = passwordHash;
        }

        public Builder fullName(String name)       { this.fullName      = name;   return this; }
        public Builder phoneNumber(String phone)   { this.phoneNumber   = phone;  return this; }
        public Builder bio(String bio)             { this.bio           = bio;    return this; }
        public Builder avatarUrl(String url)       { this.avatarUrl     = url;    return this; }
        public Builder country(String country)     { this.country       = country; return this; }
        public Builder emailVerified(boolean v)    { this.emailVerified = v;      return this; }

        public UserProfile build() { return new UserProfile(this); }
    }
}
```

**Client code:**

```java
// Minimal signup — only required fields
UserProfile guest = new UserProfile.Builder("alice99", "alice@mail.com", "hashed_pw_xyz")
        .build();

// Full profile — filled in after OAuth login
UserProfile fullProfile = new UserProfile.Builder("bob_dev", "bob@work.com", "hashed_pw_abc")
        .fullName("Bob Smith")
        .phoneNumber("+91-9999999999")
        .bio("Senior engineer | coffee addict")
        .country("India")
        .avatarUrl("https://cdn.example.com/avatars/bob.png")
        .emailVerified(true)
        .build();

System.out.println(guest);
System.out.println(fullProfile);
```

---

## Example 5 — Gaming PC Configuration

A PC builder website lets customers configure their machine. CPU and RAM are required; GPU, storage, cooling, and extras are optional.

```java
public class GamingPC {

    private final String cpu;           // REQUIRED
    private final int ramGb;            // REQUIRED
    private final String gpu;
    private final int storageGb;
    private final String coolingSystem;
    private final boolean rgbLighting;
    private final boolean wifiCard;
    private final String operatingSystem;
    private final int powerSupplyWatts;

    private GamingPC(Builder b) {
        this.cpu             = b.cpu;
        this.ramGb           = b.ramGb;
        this.gpu             = b.gpu;
        this.storageGb       = b.storageGb;
        this.coolingSystem   = b.coolingSystem;
        this.rgbLighting     = b.rgbLighting;
        this.wifiCard        = b.wifiCard;
        this.operatingSystem = b.operatingSystem;
        this.powerSupplyWatts = b.powerSupplyWatts;
    }

    @Override
    public String toString() {
        return "GamingPC {\n"
             + "  CPU:   " + cpu             + "\n"
             + "  RAM:   " + ramGb           + " GB\n"
             + "  GPU:   " + gpu             + "\n"
             + "  SSD:   " + storageGb       + " GB\n"
             + "  Cool:  " + coolingSystem   + "\n"
             + "  RGB:   " + rgbLighting     + "\n"
             + "  WiFi:  " + wifiCard        + "\n"
             + "  OS:    " + operatingSystem + "\n"
             + "  PSU:   " + powerSupplyWatts + "W\n"
             + "}";
    }

    public static class Builder {

        private final String cpu;
        private final int    ramGb;

        private String gpu             = "Integrated Graphics";
        private int    storageGb       = 512;
        private String coolingSystem   = "Stock Cooler";
        private boolean rgbLighting    = false;
        private boolean wifiCard       = false;
        private String operatingSystem = "No OS";
        private int    powerSupplyWatts = 450;

        public Builder(String cpu, int ramGb) {
            this.cpu   = cpu;
            this.ramGb = ramGb;
        }

        public Builder gpu(String gpu)                 { this.gpu              = gpu;    return this; }
        public Builder storageGb(int gb)               { this.storageGb        = gb;     return this; }
        public Builder coolingSystem(String c)         { this.coolingSystem    = c;      return this; }
        public Builder rgbLighting(boolean rgb)        { this.rgbLighting      = rgb;    return this; }
        public Builder wifiCard(boolean wifi)          { this.wifiCard         = wifi;   return this; }
        public Builder operatingSystem(String os)      { this.operatingSystem  = os;     return this; }
        public Builder powerSupplyWatts(int watts)     { this.powerSupplyWatts = watts;  return this; }

        public GamingPC build() { return new GamingPC(this); }
    }
}
```

**Client code:**

```java
// Budget build
GamingPC budget = new GamingPC.Builder("AMD Ryzen 5 5600", 16)
        .storageGb(1024)
        .operatingSystem("Windows 11 Home")
        .build();

// Beast build
GamingPC beast = new GamingPC.Builder("Intel Core i9-14900K", 64)
        .gpu("NVIDIA RTX 4090")
        .storageGb(4096)
        .coolingSystem("360mm AIO Liquid Cooler")
        .rgbLighting(true)
        .wifiCard(true)
        .operatingSystem("Windows 11 Pro")
        .powerSupplyWatts(1000)
        .build();

System.out.println(budget);
System.out.println(beast);
```

---

## Example 6 — Email Notification Builder

Email systems like SendGrid or JavaMail use builder-style APIs. An email has a required from/to/subject and optional CC, BCC, attachments, HTML body, reply-to, etc.

```java
public class Email {

    private final String from;              // REQUIRED
    private final List<String> to;          // REQUIRED (at least one)
    private final String subject;           // REQUIRED
    private final String textBody;
    private final String htmlBody;
    private final List<String> cc;
    private final List<String> bcc;
    private final String replyTo;
    private final List<String> attachments;
    private final boolean highPriority;

    private Email(Builder b) {
        this.from        = b.from;
        this.to          = Collections.unmodifiableList(new ArrayList<>(b.to));
        this.subject     = b.subject;
        this.textBody    = b.textBody;
        this.htmlBody    = b.htmlBody;
        this.cc          = Collections.unmodifiableList(new ArrayList<>(b.cc));
        this.bcc         = Collections.unmodifiableList(new ArrayList<>(b.bcc));
        this.replyTo     = b.replyTo;
        this.attachments = Collections.unmodifiableList(new ArrayList<>(b.attachments));
        this.highPriority = b.highPriority;
    }

    @Override
    public String toString() {
        return "Email {\n"
             + "  From:     " + from         + "\n"
             + "  To:       " + to           + "\n"
             + "  CC:       " + cc           + "\n"
             + "  BCC:      " + bcc          + "\n"
             + "  Subject:  " + subject      + "\n"
             + "  ReplyTo:  " + replyTo      + "\n"
             + "  Priority: " + (highPriority ? "HIGH" : "Normal") + "\n"
             + "  Attach:   " + attachments  + "\n"
             + "}";
    }

    public static class Builder {

        private final String from;
        private final List<String> to;
        private final String subject;

        private String textBody     = null;
        private String htmlBody     = null;
        private List<String> cc     = new ArrayList<>();
        private List<String> bcc    = new ArrayList<>();
        private String replyTo      = null;
        private List<String> attachments = new ArrayList<>();
        private boolean highPriority     = false;

        public Builder(String from, List<String> to, String subject) {
            if (to == null || to.isEmpty())
                throw new IllegalArgumentException("At least one recipient required");
            this.from    = from;
            this.to      = to;
            this.subject = subject;
        }

        public Builder textBody(String body)       { this.textBody     = body;           return this; }
        public Builder htmlBody(String html)       { this.htmlBody     = html;           return this; }
        public Builder cc(String... emails)        { cc.addAll(Arrays.asList(emails));   return this; }
        public Builder bcc(String... emails)       { bcc.addAll(Arrays.asList(emails));  return this; }
        public Builder replyTo(String email)       { this.replyTo      = email;          return this; }
        public Builder attach(String filePath)     { attachments.add(filePath);          return this; }
        public Builder highPriority(boolean val)   { this.highPriority = val;            return this; }

        public Email build() {
            if (textBody == null && htmlBody == null)
                throw new IllegalStateException("Email must have either a text or HTML body");
            return new Email(this);
        }
    }
}
```

**Client code:**

```java
Email welcome = new Email.Builder(
            "no-reply@myapp.com",
            List.of("alice@mail.com"),
            "Welcome to MyApp!")
        .htmlBody("<h1>Welcome Alice!</h1><p>Your account is ready.</p>")
        .textBody("Welcome Alice! Your account is ready.")
        .build();

Email invoice = new Email.Builder(
            "billing@myapp.com",
            List.of("bob@company.com"),
            "Invoice #1042 — April 2026")
        .cc("accounts@company.com", "cfo@company.com")
        .bcc("audit@myapp.com")
        .htmlBody("<p>Please find your invoice attached.</p>")
        .textBody("Please find your invoice attached.")
        .attach("/invoices/invoice_1042.pdf")
        .replyTo("support@myapp.com")
        .highPriority(true)
        .build();

System.out.println(welcome);
System.out.println(invoice);
```

---

## Copy Builder — Modifying an Existing Object

Because the product is **immutable**, the standard way to "update" it is to make a copy with some fields changed. You do this by adding a constructor on the Builder that accepts an existing product:

```java
// Inside GamingPC.Builder, add:
public Builder(GamingPC existing) {
    this.cpu              = existing.cpu;
    this.ramGb            = existing.ramGb;
    this.gpu              = existing.gpu;
    this.storageGb        = existing.storageGb;
    this.coolingSystem    = existing.coolingSystem;
    this.rgbLighting      = existing.rgbLighting;
    this.wifiCard         = existing.wifiCard;
    this.operatingSystem  = existing.operatingSystem;
    this.powerSupplyWatts = existing.powerSupplyWatts;
}
```

```java
// Start from the budget build and upgrade only the GPU
GamingPC upgraded = new GamingPC.Builder(budget)
        .gpu("NVIDIA RTX 3060")
        .storageGb(2048)
        .build();
```

The original `budget` object is untouched — you get a new object with only the GPU and storage changed.

---

## Optional: Using a Director

A `Director` class hard-codes a specific build recipe and hides the builder from the client. Useful when you offer predefined configurations:

```java
public class PCConfigurator {

    // Director — builds a preset, client never touches the builder directly
    public GamingPC buildOfficePC() {
        return new GamingPC.Builder("Intel Core i5-13400", 16)
                .storageGb(512)
                .operatingSystem("Windows 11 Pro")
                .build();
    }

    public GamingPC buildBudgetGamingPC() {
        return new GamingPC.Builder("AMD Ryzen 5 5600", 16)
                .gpu("NVIDIA RTX 3060")
                .storageGb(1024)
                .coolingSystem("Aftermarket Air Cooler")
                .operatingSystem("Windows 11 Home")
                .powerSupplyWatts(650)
                .build();
    }
}
```

```java
PCConfigurator configurator = new PCConfigurator();
GamingPC office  = configurator.buildOfficePC();
GamingPC gaming  = configurator.buildBudgetGamingPC();
```

---

## When Should You Use the Builder Pattern?

### Use Builder when…

#### 1. You have 4+ parameters, especially optional ones

The moment a constructor grows beyond 3–4 arguments, readability collapses. If several are optional (and you'd otherwise pass `null`), Builder is the right fix.

```java
// Bad — what does false, null, true mean at the call site?
Report r = new Report("Q1 Sales", "alice", false, null, true, "PDF");

// Good — every argument is self-documenting
Report r = new Report.Builder("Q1 Sales", "alice")
        .format("PDF")
        .includeCharts(true)
        .build();
```

---

#### 2. The object must be immutable but has many optional fields

If you want `final` fields (no setters), a constructor with 10 parameters is the only alternative to Builder — and that's unacceptable. Builder gives you both: immutability *and* a readable creation API.

---

#### 3. You need validation before the object exists

`build()` is the natural checkpoint. An invalid object is never created.

```java
// build() rejects the object if required state is missing
Email e = new Email.Builder("from@x.com", recipients, "Subject")
        // forgot to call .htmlBody() or .textBody()
        .build();  // ← throws IllegalStateException: "must have a body"
```

Compare this with a setter-based approach, where you can create an `Email` with no body and only discover the problem later when you try to send it.

---

#### 4. You want to create multiple similar variants of an object

```java
// Template — base configuration
GamingPC.Builder base = new GamingPC.Builder("AMD Ryzen 5 5600", 16)
        .operatingSystem("Windows 11 Home")
        .storageGb(1024);

// Two variants that share the base but differ on GPU
GamingPC entry = base.gpu("RX 6600").build();
GamingPC mid   = base.gpu("RX 7700 XT").powerSupplyWatts(750).build();
```

---

#### 5. Object construction spans multiple steps or assembles sub-parts

When building a `Report` that must first collect data, then apply a template, then attach charts — a Builder can accumulate each piece as it becomes available, rather than forcing you to have everything ready at `new` time.

---

### Do NOT use Builder when…

| Situation | Better alternative |
|---|---|
| 1–3 params, all required | Plain constructor — Builder is overkill |
| You need polymorphism / subclasses | Factory Method or Abstract Factory |
| Object state changes after creation | Regular class with setters |
| Params are positional and obvious | Constructor is clearer (`new Point(x, y)`) |

**Counter-example — don't reach for Builder here:**

```java
// Only 2 required params, nothing optional — a constructor is cleaner
public class Point {
    private final int x;
    private final int y;
    public Point(int x, int y) { this.x = x; this.y = y; }
}

// A Builder here adds noise with zero benefit
Point p = new Point.Builder().x(3).y(4).build();  // unnecessary
```

---

### Quick decision checklist

Ask yourself these questions in order:

1. **Does my object have 4+ constructor params?** → lean toward Builder
2. **Are several of those params optional?** → strong signal for Builder
3. **Should the object be immutable?** → Builder pairs naturally with immutability
4. **Do I need to validate cross-field constraints before the object is created?** → put them in `build()`
5. **Will I create multiple near-identical variants?** → Builder (possibly with a Director for presets)

If you answered **yes** to 2 or more of these, Builder is likely the right choice.

---

## Builder vs Constructor vs Factory — When to Pick Which

| Situation | Best choice |
|---|---|
| 1–3 params, all required | Plain constructor |
| Fixed family of related objects | Abstract Factory |
| One method, one type, hide subclass | Factory Method |
| 4+ params, many optional | **Builder** |
| Object construction is complex / multi-step | **Builder** |
| Object should be immutable after creation | **Builder** |
| Same build process, different representations | **Builder + Director** |

---

## Summary

| Concept | Detail |
|---|---|
| **Pattern type** | Creational |
| **Key role** | `Builder` (inner static class) |
| **Product** | Immutable — `private` constructor, `final` fields |
| **Method chaining** | Each setter returns `this` |
| **Validation** | Done inside `build()` before the object is created |
| **Copy pattern** | `new Builder(existingProduct)` → modify → `build()` |
| **Director** | Optional wrapper that hard-codes a build recipe |
| **Real-world usage** | `HttpRequest.newBuilder()`, Lombok `@Builder`, JOOQ `SelectQuery`, Guava `ImmutableList.builder()` |
