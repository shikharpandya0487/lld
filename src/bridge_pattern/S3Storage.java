package bridge_pattern;

public class S3Storage implements storage {
    @Override
    public void connect() {
        System.out.println("Connecting to Amazon S3 Storage...");
    }

    @Override
    public void disconnect() {
        System.out.println("Disconnecting from Amazon S3 Storage...");
    }

    @Override
    public void write(String fileName, String content) {
        System.out.println("Writing file '" + fileName + "' to Amazon S3 Storage with content:\n" + content);
    }

    @Override
    public String getServerName() {
        return "Amazon S3 Storage";
    }

}
