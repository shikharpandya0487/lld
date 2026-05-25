package decorator_pattern.notifier;

public class NotifierDecorator extends BaseNotifier{
    private final Notifier notifier_obj;

    public NotifierDecorator(Notifier notifier) {
        this.notifier_obj = notifier;
    }

    @Override
    public void send(String message) {
        notifier_obj.send(message);
    }
}
