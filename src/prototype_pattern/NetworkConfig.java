package prototype_pattern;

import java.util.ArrayList;
import java.util.List;

// Example 4: Network Configuration with copy-constructor style prototype
public class NetworkConfig implements Cloneable {
    private String host;
    private int port;
    private int timeout;
    private int maxConnections;
    private List<String> allowedIPs;
    private boolean sslEnabled;

    public NetworkConfig(String host, int port, int timeout, int maxConnections, boolean sslEnabled) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        this.maxConnections = maxConnections;
        this.sslEnabled = sslEnabled;
        this.allowedIPs = new ArrayList<>();
    }

    public void addAllowedIP(String ip) { allowedIPs.add(ip); }
    public void setHost(String host) { this.host = host; }
    public void setPort(int port) { this.port = port; }
    public void setMaxConnections(int max) { this.maxConnections = max; }

    @Override
    public NetworkConfig clone() {
        try {
            NetworkConfig cloned = (NetworkConfig) super.clone();
            cloned.allowedIPs = new ArrayList<>(this.allowedIPs);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "NetworkConfig{host=" + host + ", port=" + port
                + ", timeout=" + timeout + "ms, maxConn=" + maxConnections
                + ", ssl=" + sslEnabled + ", allowedIPs=" + allowedIPs + "}";
    }
}
