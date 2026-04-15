package singleton_factory_practise.CentralLoggingSystem;

public class ConsoleLogger implements Logger {
    @Override
    public void log(String level, String message) {
        System.out.println("[" + "CONSOLE" + "] " +"Level :"+ level + ", Message : " + message);
    }
}
