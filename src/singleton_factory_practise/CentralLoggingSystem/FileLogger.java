package singleton_factory_practise.CentralLoggingSystem;

public class FileLogger implements Logger {
    @Override
    public void log(String level, String message) {
        System.out.println("[" + "FILE" + "] " +"Level :"+ level + ", Message : " + message);
    }

}
