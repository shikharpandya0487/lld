package prototype_pattern;

public class Main {
    public static void main(String[] args) {

        // ── Example 1: Shape Cloning ──────────────────────────────────────────
        System.out.println("=== Example 1: Shape Cloning ===");
        Circle c1 = new Circle("Red", 5.0);
        Circle c2 = (Circle) c1.clone();
        c2.setRadius(10.0);
        // c1 radius unchanged; c2 is independent
        System.out.println("Original : " + c1);
        System.out.println("Cloned   : " + c2);

        Rectangle r1 = new Rectangle("Blue", 4, 6);
        Rectangle r2 = (Rectangle) r1.clone();
        System.out.println("Original : " + r1);
        System.out.println("Cloned   : " + r2);

        // ── Example 2: Game Character (deep copy) ─────────────────────────────
        System.out.println("\n=== Example 2: Game Character ===");
        GameCharacter warrior = new GameCharacter("Warrior", 100, 30);
        warrior.addItem("Sword");
        warrior.addItem("Shield");

        GameCharacter warriorClone = warrior.clone();
        warriorClone.setName("Elite Warrior");
        warriorClone.setHealth(150);
        warriorClone.addItem("Magic Ring");  // does NOT affect original

        System.out.println("Original : " + warrior);
        System.out.println("Cloned   : " + warriorClone);

        // ── Example 3: Document Template Registry ─────────────────────────────
        System.out.println("\n=== Example 3: Document Template Registry ===");
        DocumentTemplate invoiceTemplate = new DocumentTemplate(
                "Invoice", "ACME Corp", "Page {n}");
        invoiceTemplate.addMetadata("version", "1.0");

        DocumentTemplate.Registry registry = new DocumentTemplate.Registry();
        registry.register("invoice", invoiceTemplate);

        // Each call returns a fresh independent clone
        DocumentTemplate doc1 = registry.get("invoice");
        doc1.setTitle("Invoice #001");
        doc1.setBodyContent("Order: Laptop x2");

        DocumentTemplate doc2 = registry.get("invoice");
        doc2.setTitle("Invoice #002");
        doc2.setBodyContent("Order: Monitor x1");

        System.out.println("Doc1: " + doc1);
        System.out.println("Doc2: " + doc2);
        System.out.println("Template unchanged: " + invoiceTemplate);

        // ── Example 4: Network Configuration ─────────────────────────────────
        System.out.println("\n=== Example 4: Network Config ===");
        NetworkConfig baseConfig = new NetworkConfig("localhost", 8080, 3000, 100, false);
        baseConfig.addAllowedIP("192.168.1.1");
        baseConfig.addAllowedIP("10.0.0.1");

        NetworkConfig prodConfig = baseConfig.clone();
        prodConfig.setHost("prod.example.com");
        prodConfig.setPort(443);
        prodConfig.setMaxConnections(500);
        prodConfig.addAllowedIP("203.0.113.5");  // prod-only IP

        System.out.println("Base : " + baseConfig);
        System.out.println("Prod : " + prodConfig);

        // ── Example 5: UI Component Tree ─────────────────────────────────────
        System.out.println("\n=== Example 5: UI Component Tree ===");
        UIComponent loginForm = new UIComponent("Panel", "loginForm", 0, 0, 400, 300, "card");
        loginForm.addChild(new UIComponent("TextField", "username", 10, 10, 200, 40, "input"));
        loginForm.addChild(new UIComponent("TextField", "password", 10, 60, 200, 40, "input-password"));
        loginForm.addChild(new UIComponent("Button",    "submit",   10, 110, 100, 40, "btn-primary"));

        // Clone the entire form tree for a registration page
        UIComponent registerForm = loginForm.clone();
        registerForm.setId("registerForm");
        registerForm.moveTo(500, 0);  // place it elsewhere on screen

        System.out.println("Login Form    : " + loginForm);
        System.out.println("Register Form : " + registerForm);
    }
}
