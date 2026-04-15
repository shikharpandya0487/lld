package singleton_factory_practise.CentralLoggingSystem;

public class Main {
    public static void main(String[]args){


        LoggerFactory factory = LoggerFactory.getInstance();

        // Basic usage
        Logger console = factory.getLogger(LoggerType.CONSOLE);
        console.log("INFO", "Server started on port 8080");
        // [CONSOLE] INFO: Server started on port 8080

        Logger file = factory.getLogger(LoggerType.FILE);
        file.log("WARN", "Disk usage above 80%");
        // [FILE] Writing to file: WARN - Disk usage above 80%

        Logger db = factory.getLogger(LoggerType.DATABASE);
        db.log("ERROR", "Connection pool exhausted");
        // [DB] Inserting log to DB: ERROR - Connection pool exhausted

        // Verify singleton: both references must be the same object
        LoggerFactory anotherRef = LoggerFactory.getInstance();
        System.out.println(factory == anotherRef);   // true

        // Verify cache: same type → same logger object
        Logger console2 = factory.getLogger(LoggerType.CONSOLE);
        System.out.println(console == console2);     // true

        // Bonus: broadcast to all loggers
        System.out.println("--- logAll ---");
        factory.getAllLogs("DEBUG", "Health check passed");
    }
}
