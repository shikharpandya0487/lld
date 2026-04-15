package singleton_factory_practise.CentralLoggingSystem;

import java.util.EnumMap;
import java.util.Map;

public class LoggerFactory {
    private static LoggerFactory instance;
    private Map<LoggerType,Logger> loggerCache=new EnumMap<LoggerType,Logger>(LoggerType.class);
    private LoggerFactory(){
            loggerCache.put(LoggerType.CONSOLE,new ConsoleLogger());
            loggerCache.put(LoggerType.FILE,new FileLogger());
            loggerCache.put(LoggerType.DATABASE,new DatabaseLogger());
    }

    public static LoggerFactory getInstance(){
        if(instance==null){
            synchronized (LoggerFactory.class) 
            {
                if(instance==null)
                {
                    instance=new LoggerFactory();
                }
            }
        }

        return instance;
    }

    public Logger getLogger(LoggerType loggerType){
        Logger logger=loggerCache.get(loggerType);

        if(logger==null){
            throw new IllegalArgumentException("Invalid logger type: " + loggerType);
        }

        return logger;
    }


    public void getAllLogs(String level,String message){
        for(Logger logger:loggerCache.values()){
            logger.log(level,message);
        }
    }
}
