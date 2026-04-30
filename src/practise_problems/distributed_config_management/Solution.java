package practise_problems.distributed_config_management;

import java.util.Map;
import java.util.Set;

interface Config{
    String getConfigId();
}

class FileConfig implements Config{
    private final String Id;
    private final String fileFormat;
    private final String extension;
    private final String content;

    private FileConfig(FileBuilder builder)
    {
        this.Id = builder.Id;
        this.fileFormat = builder.fileFormat;
        this.extension = builder.extension;
        this.content = builder.content;
    }

    public static class FileBuilder{
        private String Id;
        private String fileFormat;
        private String extension;
        private String content;

        public FileBuilder setId(String id)
        {
            this.Id = id;
            return this;
        }

        public FileBuilder setFileFormat(String fileFormat)
        {
            this.fileFormat = fileFormat;
            return this;
        }

        public FileBuilder setExtension(String extension)
        {
            this.extension = extension;
            return this;
        }

        public FileBuilder setContent(String content)
        {
            this.content = content;
            return this;
        }

        public FileConfig build()
        {
            return new FileConfig(this);
        }
    }

    @Override
    public String getConfigId() {
        return Id;
    }
}


class EnvConfig implements Config{
    private final String Id;
    private final String caseSensitive;
    private final String stripPrefix;



    private EnvConfig(EnvBuilder builder)
    {
        this.Id = builder.Id;
        this.caseSensitive = builder.caseSensitive;
        this.stripPrefix = builder.stripPrefix;
    }

    public static class EnvBuilder{
        private String Id;
        private String caseSensitive;
        private String stripPrefix;

        public EnvBuilder setId(String id)
        {
            this.Id = id;
            return this;
        }

        public EnvBuilder setCaseSensitive(String caseSensitive)
        {
            this.caseSensitive = caseSensitive;
            return this;
        }

        public EnvBuilder setStripPrefix(String stripPrefix)
        {
            this.stripPrefix = stripPrefix;
            return this;
        }

        public EnvConfig build()
        {
            return new EnvConfig(this);
        }
    }

    @Override
    public String getConfigId() {
        return Id;
    }
}


class RemoteConfig implements Config{
    private final String Id;
    private final String url;
    private final int timeout;
    private final String authToken;
    
    private RemoteConfig(RemoteBuilder builder)
    {
        this.Id = builder.Id;
        this.url = builder.url;
        this.timeout = builder.timeout;
        this.authToken = builder.authToken;
    }

    public static class RemoteBuilder{
        private String Id;
        private String url;
        private int timeout;
        private String authToken;

        public RemoteBuilder setId(String id)
        {
            this.Id = id;
            return this;
        }

        public RemoteBuilder setUrl(String url)
        {
            this.url = url;
            return this;
        }

        public RemoteBuilder setTimeout(int timeout)
        {
            this.timeout = timeout;
            return this;
        }

        public RemoteBuilder setAuthToken(String authToken)
        {
            this.authToken = authToken;
            return this;
        }

        public RemoteConfig build()
        {
            if(this.url == null || this.url.isEmpty())
            {
                throw new IllegalArgumentException("URL cannot be null or empty");
            }
            if(this.timeout <= 0)
            {
                throw new IllegalArgumentException("Timeout must be a positive integer");
            }

            return new RemoteConfig(this);
        }
    }

    @Override
    public String getConfigId() {
        return Id;
    }
}



class ConfigManager {
    private static ConfigManager instance;
    private Set<Config> configs;

    public static ConfigManager getInstance()
    {
        if(instance==null)
        {
            synchronized (ConfigManager.class)
            {
                if(instance==null)
                {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    public void addConfigSource(Config config){
        configs.add(config);
    }

    public void removeConfigSourceById(String configId){
        configs.removeIf(config -> config.getConfigId().equals(configId));
    }    
}


class ConfigSourceFactory {

    private static final String CONFIG_SOURCE = "CONFIG_SOURCE";
    public static Config createConfigSource(String type, Map<String, String> properties) {
        switch (type) {
            case "FILE":
                return new FileConfig.FileBuilder()
                        .setId(properties.get("id"))
                        .setFileFormat(properties.get("fileFormat"))
                        .setExtension(properties.get("extension"))
                        .setContent(properties.get("content"))
                        .build();
            case "ENV":
                return new EnvConfig.EnvBuilder()
                        .setId(properties.get("id"))
                        .setCaseSensitive(properties.get("caseSensitive"))
                        .setStripPrefix(properties.get("stripPrefix"))
                        .build();
            case "REMOTE":
                return new RemoteConfig.RemoteBuilder()
                        .setId(properties.get("id"))
                        .setUrl(properties.get("url"))
                        .setTimeout(Integer.parseInt(properties.get("timeout")))
                        .setAuthToken(properties.get("authToken"))
                        .build();
            default:
                throw new IllegalArgumentException("Invalid config source type: " + type);
        }
    }
}

public class Solution {
    public ConfigManager configManager;

    public Config createObj(ConfigSourceFactory factory, String CONFIG_SOURCE, Map<String, String> properties)
    {
        return factory.createConfigSource(CONFIG_SOURCE, properties);
    }

    public static void main(String[] args) {
        
        // properties 
        Map<String, String> fileConfigProperties = Map.of(
            "id", "fileConfig1",
            "fileFormat", "JSON",
            "extension", ".json",
            "content", "{\"key\":\"value\"}"
        );
        Map<String, String> envConfigProperties = Map.of(
            "id", "envConfig1",
            "caseSensitive", "true",
            "stripPrefix", "APP_"
        );

        Map<String, String> remoteConfigProperties = Map.of(
            "id", "remoteConfig1",
            "url", "https://config-service.example.com/config",
            "timeout", "5000",
            "authToken", "abcdef123456"
        );

        ConfigSourceFactory factory = new ConfigSourceFactory();

        Config fileConfig= factory.createConfigSource("FILE", fileConfigProperties);
        Config envConfig = factory.createConfigSource("ENV", envConfigProperties);
        Config remoteConfig = factory.createConfigSource("REMOTE", remoteConfigProperties);

        ConfigManager configManager = ConfigManager.getInstance();
        configManager.addConfigSource(fileConfig);
        configManager.addConfigSource(envConfig);
        configManager.addConfigSource(remoteConfig);

        


    }


}
