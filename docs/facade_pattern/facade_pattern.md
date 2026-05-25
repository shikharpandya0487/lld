## Facade — one interface to simplify many subsystems

### The Analogy
A Facade is like a universal remote for a home theater. You press one button and the remote handles the TV, sound system, and lights for you. In code, the Facade hides many subsystem calls behind one simple interface.

### What Problem Does It Solve?
Without a Facade, every caller must know the exact order and details of multiple subsystems. That means every change in process or new feature forces updates in many places. A Facade makes the system easier to use and safer to change by giving clients a single entry point.

### The Formal Definition
A Facade is a structural pattern that provides a simplified interface to a group of related classes. In plain terms, it means: create one wrapper object that hides the complexity of several smaller components and exposes a straightforward API.

### The Core Structure (Java)
```java
// Abstraction for the Facade itself
public interface HomeSystemFacade {
    void startMovieNight();
}

// Subsystems each have their own responsibilities
public class TV {
    public void turnOn() { System.out.println("TV is on"); }
    public void setInput(String input) { System.out.println("TV input set to " + input); }
}

public class SoundSystem {
    public void turnOn() { System.out.println("Sound system is on"); }
    public void setVolume(int level) { System.out.println("Volume set to " + level); }
}

public class Lights {
    public void dim(int percent) { System.out.println("Lights dimmed to " + percent + "%"); }
}

// Facade coordinates the subsystems and hides the sequence
public class EntertainmentFacade implements HomeSystemFacade {
    private final TV tv;
    private final SoundSystem sound;
    private final Lights lights;

    public EntertainmentFacade(TV tv, SoundSystem sound, Lights lights) {
        this.tv = tv;
        this.sound = sound;
        this.lights = lights;
    }

    @Override
    public void startMovieNight() {
        tv.turnOn();
        tv.setInput("HDMI1");
        sound.turnOn();
        sound.setVolume(20);
        lights.dim(20);
    }
}
```

---

### Example 1 — BAD: Without the Pattern
```java
public class PaymentService {
    public void chargeCard(String cardNumber, double amount) {
        System.out.println("Charging card " + cardNumber + " for $" + amount);
    }
}

public class InventoryService {
    public void reserveItem(String itemId) {
        System.out.println("Reserving item " + itemId);
    }
}

public class NotificationService {
    public void sendReceipt(String email) {
        System.out.println("Sending receipt to " + email);
    }
}

public class Checkout {
    public static void main(String[] args) {
        PaymentService payment = new PaymentService();
        InventoryService inventory = new InventoryService();
        NotificationService notification = new NotificationService();

        // BAD — calling code knows the exact order and details
        payment.chargeCard("4111111111111111", 99.99);
        inventory.reserveItem("BOOK-123");
        notification.sendReceipt("customer@example.com");
    }
}
```

Comments:
- The caller must know all three services and the exact process.
- If order processing changes, every checkout flow must be updated.
- This is fragile when new subsystems are added, such as loyalty points or taxes.

### Example 2 — GOOD: With the Pattern Applied
```java
public class OrderFacade {
    private final PaymentService payment;
    private final InventoryService inventory;
    private final NotificationService notification;

    public OrderFacade(PaymentService payment, InventoryService inventory, NotificationService notification) {
        this.payment = payment;
        this.inventory = inventory;
        this.notification = notification;
    }

    public void placeOrder(String cardNumber, double amount, String itemId, String email) {
        payment.chargeCard(cardNumber, amount);
        inventory.reserveItem(itemId);
        notification.sendReceipt(email);
    }
}

public class CheckoutWithFacade {
    public static void main(String[] args) {
        OrderFacade orderFacade = new OrderFacade(
            new PaymentService(),
            new InventoryService(),
            new NotificationService()
        );

        orderFacade.placeOrder("4111111111111111", 99.99, "BOOK-123", "customer@example.com");
        // Output:
        // Charging card 4111111111111111 for $99.99
        // Reserving item BOOK-123
        // Sending receipt to customer@example.com
    }
}
```

Why it works:
- The client only calls `placeOrder()`.
- The Facade hides the subsystem details and sequence.
- Adding a new step later changes only `OrderFacade`, not all callers.

---

### Example 3 — Home Entertainment System
Your living room remote should start the movie, set the sound, and dim the lights without the user controlling each device.

```java
public class TVDevice {
    public void powerOn() { System.out.println("TV on"); }
    public void setHdmiInput() { System.out.println("TV set to HDMI"); }
}

public class SoundDevice {
    public void powerOn() { System.out.println("Sound system on"); }
    public void setSurroundMode() { System.out.println("Surround sound enabled"); }
}

public class LightsDevice {
    public void dim() { System.out.println("Lights dimmed"); }
}

public class HomeTheaterFacade {
    private final TVDevice tv;
    private final SoundDevice sound;
    private final LightsDevice lights;

    public HomeTheaterFacade(TVDevice tv, SoundDevice sound, LightsDevice lights) {
        this.tv = tv;
        this.sound = sound;
        this.lights = lights;
    }

    public void startMovieMode() {
        tv.powerOn();
        tv.setHdmiInput();
        sound.powerOn();
        sound.setSurroundMode();
        lights.dim();
    }
}

public class HomeTheaterApp {
    public static void main(String[] args) {
        HomeTheaterFacade facade = new HomeTheaterFacade(
            new TVDevice(),
            new SoundDevice(),
            new LightsDevice()
        );
        facade.startMovieMode();
    }
}
```

Outcome: one method call starts the whole entertainment experience.

### Example 4 — Travel Booking System
Your travel app should book flight, hotel, and car rental with one request, instead of forcing the user to call three systems.

```java
public class FlightBooking {
    public void bookFlight(String route) { System.out.println("Flight booked: " + route); }
}

public class HotelBooking {
    public void reserveRoom(String hotelName) { System.out.println("Hotel reserved: " + hotelName); }
}

public class CarRental {
    public void rentCar(String carType) { System.out.println("Car rented: " + carType); }
}

public class TravelFacade {
    private final FlightBooking flight;
    private final HotelBooking hotel;
    private final CarRental car;

    public TravelFacade(FlightBooking flight, HotelBooking hotel, CarRental car) {
        this.flight = flight;
        this.hotel = hotel;
        this.car = car;
    }

    public void bookVacation(String route, String hotelName, String carType) {
        flight.bookFlight(route);
        hotel.reserveRoom(hotelName);
        car.rentCar(carType);
    }
}

public class TravelApp {
    public static void main(String[] args) {
        TravelFacade facade = new TravelFacade(
            new FlightBooking(),
            new HotelBooking(),
            new CarRental()
        );
        facade.bookVacation("NYC to Paris", "Paris Inn", "Compact");
    }
}
```

Outcome: the app user sees one booking action, while the Facade manages three services.

### Example 5 — Document Editor Startup
In a document editor, a Facade can hide text formatting, image loading, and persistence behind a single `DocumentEditorFacade`. This matches the repo idea in `src/document_editor/DocEditor.java` where multiple components work together.

```java
public class TextService {
    public void loadText(String doc) { System.out.println("Loading text for " + doc); }
}

public class ImageService {
    public void loadImages(String doc) { System.out.println("Loading images for " + doc); }
}

public class PersistenceService {
    public void connectDb() { System.out.println("Connecting to document database"); }
}

public class DocumentEditorFacade {
    private final TextService textService;
    private final ImageService imageService;
    private final PersistenceService persistenceService;

    public DocumentEditorFacade(TextService textService, ImageService imageService, PersistenceService persistenceService) {
        this.textService = textService;
        this.imageService = imageService;
        this.persistenceService = persistenceService;
    }

    public void openDocument(String documentName) {
        persistenceService.connectDb();
        textService.loadText(documentName);
        imageService.loadImages(documentName);
        System.out.println("Document " + documentName + " is ready to edit");
    }
}

public class DocumentEditorApp {
    public static void main(String[] args) {
        DocumentEditorFacade editor = new DocumentEditorFacade(
            new TextService(),
            new ImageService(),
            new PersistenceService()
        );
        editor.openDocument("DesignNotes.docx");
    }
}
```

Outcome: one operation prepares the editor, while the Facade keeps the startup sequence hidden.

---

### How to Spot When to Use This Pattern
| Signal / Code Smell | This pattern helps because... |
|---|---|
| Many classes are used together in the same sequence | Facade groups them into one clear workflow. |
| Clients repeat the same setup calls | Facade removes duplicate plumbing code. |
| Users need a simple API over a complex subsystem | Facade exposes a minimal entry point. |
| A system has many small helper classes | Facade hides the internal structure from clients. |

### Common Mistakes Beginners Make
- Writing the Facade as a new complex class, instead of a thin wrapper around existing subsystems.
- Letting client code call subsystems directly and also through the Facade, which duplicates responsibility.
- Using Facade to add business logic instead of using it only to simplify orchestration.
- Building the Facade with hard-coded dependencies instead of injecting subsystem objects.

### Quick Reference
| Intent | Key Classes/Roles | Java Keyword Hints | When to Use | When NOT to Use |
|---|---|---|---|---|
| Simplify many subsystems behind one API | Facade, Subsystem classes | `interface`, `class`, `new` | When clients keep seeing the same multi-step process | When a single class is already simple enough |
| Reduce coupling between client and subsystem details | Facade, Client, Subsystem | constructor injection | When a system has multiple collaborators | When you need direct access to every subsystem method |
| Centralize operation order and setup | Facade, Subsystems | `new`, `public void` | When order matters across components | When each caller should remain responsible for its own workflow |
