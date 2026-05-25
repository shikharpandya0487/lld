# Adapter Pattern

## Intent

The Adapter pattern **converts the interface of a class into another interface that clients expect**. It allows two incompatible interfaces to work together without modifying the source code of either.

Also known as: **Wrapper**

---

## The Problem It Solves

Imagine you have an existing class that does exactly what you need, but its interface (method names, signatures) doesn't match what your client code expects. You can't change the existing class (it's a third-party library, or it's used elsewhere). You also can't change the client code. The Adapter sits in between and translates.

```
Client  --->  [Target Interface]  --->  Adapter  --->  Adaptee (existing class)
```

---

## Structure

```
<<interface>>
  Target
  + request()
      |
      |  implements
      |
  Adapter                    Adaptee
  - adaptee: Adaptee   ---->  + specificRequest()
  + request()
      internally calls adaptee.specificRequest()
```

### Participants
| Role       | Description                                                      |
|------------|------------------------------------------------------------------|
| `Target`   | The interface that the client expects/uses                       |
| `Adaptee`  | The existing class with an incompatible interface                |
| `Adapter`  | Wraps the `Adaptee` and implements the `Target` interface        |
| `Client`   | Works only with the `Target` interface                           |

---

## Two Flavors of Adapter

### 1. Object Adapter (preferred — uses composition)
The Adapter **holds a reference** to the Adaptee and delegates calls.

### 2. Class Adapter (uses multiple inheritance — not possible in Java directly)
The Adapter **extends** the Adaptee and implements the Target. In Java, this is done by extending the Adaptee class and implementing the Target interface simultaneously.

---

## Example 1: Legacy Payment Gateway

### Scenario
Your application uses a `PaymentProcessor` interface with a `pay(double amount)` method. You want to integrate a legacy payment system `OldPaymentGateway` that has a `makePayment(int amountInCents)` method.

```java
// Target interface — what your client code expects
interface PaymentProcessor {
    void pay(double amountInDollars);
}

// Adaptee — the legacy class you cannot modify
class OldPaymentGateway {
    public void makePayment(int amountInCents) {
        System.out.println("Legacy gateway processing payment of " + amountInCents + " cents");
    }
}

// Adapter — bridges the gap
class PaymentAdapter implements PaymentProcessor {
    private OldPaymentGateway oldGateway;

    public PaymentAdapter(OldPaymentGateway oldGateway) {
        this.oldGateway = oldGateway;
    }

    @Override
    public void pay(double amountInDollars) {
        int amountInCents = (int) (amountInDollars * 100);
        oldGateway.makePayment(amountInCents);
    }
}

// Client code
public class PaymentClient {
    public static void main(String[] args) {
        PaymentProcessor processor = new PaymentAdapter(new OldPaymentGateway());
        processor.pay(49.99); // Output: Legacy gateway processing payment of 4999 cents
    }
}
```

**Key takeaway:** The client only knows about `PaymentProcessor.pay()`. The unit conversion and delegation happen transparently inside the adapter.

---

## Example 2: Media Player — Supporting Multiple Formats

### Scenario
A `MediaPlayer` interface supports only `.mp3` files. You have an `AdvancedMediaPlayer` that can play `.mp4` and `.vlc` files. Build an adapter so the basic `MediaPlayer` can play advanced formats.

```java
// Target interface
interface MediaPlayer {
    void play(String audioType, String fileName);
}

// Adaptee interface
interface AdvancedMediaPlayer {
    void playMp4(String fileName);
    void playVlc(String fileName);
}

// Concrete Adaptees
class Mp4Player implements AdvancedMediaPlayer {
    @Override
    public void playMp4(String fileName) {
        System.out.println("Playing MP4 file: " + fileName);
    }

    @Override
    public void playVlc(String fileName) { /* not supported */ }
}

class VlcPlayer implements AdvancedMediaPlayer {
    @Override
    public void playMp4(String fileName) { /* not supported */ }

    @Override
    public void playVlc(String fileName) {
        System.out.println("Playing VLC file: " + fileName);
    }
}

// Adapter
class MediaAdapter implements MediaPlayer {
    private AdvancedMediaPlayer advancedPlayer;

    public MediaAdapter(String audioType) {
        if (audioType.equalsIgnoreCase("mp4")) {
            advancedPlayer = new Mp4Player();
        } else if (audioType.equalsIgnoreCase("vlc")) {
            advancedPlayer = new VlcPlayer();
        }
    }

    @Override
    public void play(String audioType, String fileName) {
        if (audioType.equalsIgnoreCase("mp4")) {
            advancedPlayer.playMp4(fileName);
        } else if (audioType.equalsIgnoreCase("vlc")) {
            advancedPlayer.playVlc(fileName);
        }
    }
}

// Concrete Target implementation
class AudioPlayer implements MediaPlayer {
    @Override
    public void play(String audioType, String fileName) {
        if (audioType.equalsIgnoreCase("mp3")) {
            System.out.println("Playing MP3 file: " + fileName);
        } else if (audioType.equalsIgnoreCase("mp4") || audioType.equalsIgnoreCase("vlc")) {
            MediaAdapter adapter = new MediaAdapter(audioType);
            adapter.play(audioType, fileName);
        } else {
            System.out.println("Unsupported format: " + audioType);
        }
    }
}

// Client
public class MediaClient {
    public static void main(String[] args) {
        AudioPlayer player = new AudioPlayer();
        player.play("mp3", "song.mp3");     // Playing MP3 file: song.mp3
        player.play("mp4", "video.mp4");    // Playing MP4 file: video.mp4
        player.play("vlc", "movie.vlc");    // Playing VLC file: movie.vlc
        player.play("avi", "clip.avi");     // Unsupported format: avi
    }
}
```

**Key takeaway:** `AudioPlayer` doesn't need to know anything about `Mp4Player` or `VlcPlayer`. The adapter abstracts the complexity of picking and using the right advanced player.

---

## Example 3: Temperature Sensor — Unit Conversion

### Scenario
Your monitoring system works with a `TemperatureSensor` interface that returns Celsius. You have a third-party American sensor `AmericanSensor` that only reports Fahrenheit.

```java
// Target interface — the system expects Celsius
interface TemperatureSensor {
    double getTemperatureCelsius();
}

// Adaptee — third-party sensor that only speaks Fahrenheit
class AmericanSensor {
    public double getTemperatureFahrenheit() {
        return 98.6; // Simulated reading
    }
}

// Adapter
class AmericanSensorAdapter implements TemperatureSensor {
    private AmericanSensor sensor;

    public AmericanSensorAdapter(AmericanSensor sensor) {
        this.sensor = sensor;
    }

    @Override
    public double getTemperatureCelsius() {
        double fahrenheit = sensor.getTemperatureFahrenheit();
        return (fahrenheit - 32) * 5.0 / 9.0;
    }
}

// Monitoring system — only works with TemperatureSensor
class MonitoringSystem {
    private TemperatureSensor sensor;

    public MonitoringSystem(TemperatureSensor sensor) {
        this.sensor = sensor;
    }

    public void displayTemperature() {
        System.out.printf("Current temperature: %.2f°C%n", sensor.getTemperatureCelsius());
    }
}

// Client
public class SensorClient {
    public static void main(String[] args) {
        AmericanSensor americanSensor = new AmericanSensor();
        TemperatureSensor adapter = new AmericanSensorAdapter(americanSensor);
        MonitoringSystem monitor = new MonitoringSystem(adapter);
        monitor.displayTemperature(); // Current temperature: 37.00°C
    }
}
```

**Key takeaway:** The monitoring system is completely decoupled from the physical sensor type. Swapping in any other foreign sensor just requires a new adapter.

---

## Example 4: Sorting Algorithm — Adapting a Third-Party Sorter

### Scenario
Your application uses a `Sorter` interface with `sort(int[] arr)`. You have a third-party library `ThirdPartySorter` with a method `performSort(Integer[] arr)`. Adapt it.

```java
import java.util.Arrays;

// Target interface
interface Sorter {
    void sort(int[] arr);
}

// Adaptee — third-party class, uses Integer[] and returns a new sorted array
class ThirdPartySorter {
    public Integer[] performSort(Integer[] arr) {
        Arrays.sort(arr);
        return arr;
    }
}

// Adapter
class SorterAdapter implements Sorter {
    private ThirdPartySorter thirdPartySorter;

    public SorterAdapter(ThirdPartySorter thirdPartySorter) {
        this.thirdPartySorter = thirdPartySorter;
    }

    @Override
    public void sort(int[] arr) {
        // Convert int[] to Integer[]
        Integer[] boxed = new Integer[arr.length];
        for (int i = 0; i < arr.length; i++) boxed[i] = arr[i];

        Integer[] sorted = thirdPartySorter.performSort(boxed);

        // Copy results back into the original int[]
        for (int i = 0; i < arr.length; i++) arr[i] = sorted[i];
    }
}

// Client
public class SortClient {
    public static void main(String[] args) {
        int[] data = {5, 2, 8, 1, 9, 3};

        Sorter sorter = new SorterAdapter(new ThirdPartySorter());
        sorter.sort(data);

        System.out.println(Arrays.toString(data)); // [1, 2, 3, 5, 8, 9]
    }
}
```

**Key takeaway:** The boxing/unboxing and type conversion is cleanly hidden inside the adapter. The client works with a plain `int[]` throughout.

---

## Example 5: Notification Service — SMS vs Email

### Scenario
Your application sends notifications via an `NotificationSender` interface using `sendNotification(String message)`. You want to plug in an existing `SMSService` (with `sendSMS(String phone, String text)`) and `EmailService` (with `sendEmail(String to, String subject, String body)`) without changing either.

```java
// Target interface
interface NotificationSender {
    void sendNotification(String message);
}

// Adaptee 1 — existing SMS service
class SMSService {
    private String phoneNumber;

    public SMSService(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void sendSMS(String phone, String text) {
        System.out.println("SMS to " + phone + ": " + text);
    }
}

// Adaptee 2 — existing Email service
class EmailService {
    private String emailAddress;

    public EmailService(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void sendEmail(String to, String subject, String body) {
        System.out.println("Email to " + to + " | Subject: " + subject + " | Body: " + body);
    }
}

// Adapter for SMS
class SMSAdapter implements NotificationSender {
    private SMSService smsService;
    private String phoneNumber;

    public SMSAdapter(SMSService smsService, String phoneNumber) {
        this.smsService = smsService;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void sendNotification(String message) {
        smsService.sendSMS(phoneNumber, message);
    }
}

// Adapter for Email
class EmailAdapter implements NotificationSender {
    private EmailService emailService;
    private String emailAddress;

    public EmailAdapter(EmailService emailService, String emailAddress) {
        this.emailService = emailService;
        this.emailAddress = emailAddress;
    }

    @Override
    public void sendNotification(String message) {
        emailService.sendEmail(emailAddress, "Notification", message);
    }
}

// Alert system — only depends on NotificationSender
class AlertSystem {
    private List<NotificationSender> senders;

    public AlertSystem(List<NotificationSender> senders) {
        this.senders = senders;
    }

    public void alert(String message) {
        for (NotificationSender sender : senders) {
            sender.sendNotification(message);
        }
    }
}

// Client
import java.util.List;

public class NotificationClient {
    public static void main(String[] args) {
        SMSService sms = new SMSService("+1-800-555-0199");
        EmailService email = new EmailService("user@example.com");

        NotificationSender smsAdapter = new SMSAdapter(sms, "+1-800-555-0199");
        NotificationSender emailAdapter = new EmailAdapter(email, "user@example.com");

        AlertSystem alertSystem = new AlertSystem(List.of(smsAdapter, emailAdapter));
        alertSystem.alert("Server CPU usage exceeded 95%!");

        // Output:
        // SMS to +1-800-555-0199: Server CPU usage exceeded 95%!
        // Email to user@example.com | Subject: Notification | Body: Server CPU usage exceeded 95%!
    }
}
```

**Key takeaway:** The `AlertSystem` fires the same `sendNotification()` call for both SMS and Email. Adding a new channel (Slack, Push) only requires a new adapter — zero changes to existing code. This demonstrates the **Open/Closed Principle** working hand-in-hand with the Adapter pattern.

---

## Advantages

| Advantage                        | Explanation                                                           |
|----------------------------------|-----------------------------------------------------------------------|
| Single Responsibility Principle  | Conversion logic is isolated in the adapter class                     |
| Open/Closed Principle            | New adapters can be introduced without breaking existing client code  |
| Reusability                      | Existing (even third-party) classes can be reused without modification|
| Loose Coupling                   | Client depends only on the Target interface, not the Adaptee          |

## Disadvantages

| Disadvantage             | Explanation                                                            |
|--------------------------|------------------------------------------------------------------------|
| Added complexity         | Increases the number of classes in the codebase                        |
| Overhead                 | An extra layer of delegation for every call                            |
| Can mask bad design      | Over-use of adapters can indicate a deeper architecture mismatch       |

---

## Adapter vs Decorator vs Facade

| Pattern   | Purpose                                    | Changes interface? |
|-----------|--------------------------------------------|--------------------|
| Adapter   | Make incompatible interfaces compatible    | Yes                |
| Decorator | Add new behavior to an existing object     | No                 |
| Facade    | Simplify a complex subsystem               | No (new simpler one)|

---

## Real-World Java Examples

- `java.io.InputStreamReader` — adapts `InputStream` (byte stream) to `Reader` (character stream)
- `java.io.OutputStreamWriter` — adapts `OutputStream` to `Writer`
- `java.util.Arrays.asList()` — adapts a plain array to a `List`
- Spring's `HandlerAdapter` — adapts different types of controllers to a uniform handler interface
