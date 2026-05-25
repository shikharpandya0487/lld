package decorator_pattern.notifier;

public class SmsNotifierDecorator extends NotifierDecorator {

    public SmsNotifierDecorator(Notifier notifier) {
        super(notifier);
    }

    @Override
    public void send(String message) {
        super.send(message);
        System.out.println("Adding SMS-specific functionality: " + message);
    }
}
