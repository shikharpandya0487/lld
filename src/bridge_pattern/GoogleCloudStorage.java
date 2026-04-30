package bridge_pattern;

public class GoogleCloudStorage implements storage {
    @Override
    public void connect() {
        System.out.println("Connecting to Google Cloud Storage...");
    }

    @Override
    public void disconnect() {
        System.out.println("Disconnecting from Google Cloud Storage...");
    }

    @Override
    public void write(String fileName, String content) {
        System.out.println("Writing file '" + fileName + "' to Google Cloud Storage with content:\n" + content);
    }

    @Override
    public String getServerName() {
        return "Google Cloud Storage";
    }

}
