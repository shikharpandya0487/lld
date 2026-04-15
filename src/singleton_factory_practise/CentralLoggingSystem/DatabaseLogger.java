package singleton_factory_practise.CentralLoggingSystem;

public class DatabaseLogger implements Logger {
    @Override
    public void log(String level, String message) {
        System.out.println("[" + "DATABASE" + "] " +"Level :"+ level + ", Message : " + message);
    }
}
