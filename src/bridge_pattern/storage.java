package bridge_pattern;

public interface storage {
    void connect();
    void disconnect();
    void write(String fileName, String content);
    String getServerName();
}
