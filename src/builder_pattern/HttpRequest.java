package builder_pattern;

import java.util.Map;

public class HttpRequest {
    private final String url;
    private final String method;
    private final String body;
    private final Map<String,String> headers;
    private final int timeoutMs;
    private final boolean followRedirects;

    private HttpRequest(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.body = builder.body;
        this.headers = builder.headers;
        this.timeoutMs = builder.timeoutMs;
        this.followRedirects = builder.followRedirects;
    }
    

    public String getUrl() {
        return url;
    }


    public String getMethod() {
        return method;
    }


    public String getBody() {
        return body;
    }


    public Map<String, String> getHeaders() {
        return headers;
    }


    public int getTimeoutMs() {
        return timeoutMs;
    }


    public boolean isFollowRedirects() {
        return followRedirects;
    }


    public static class Builder {
        private String url;
        private String method = "GET";
        private String body = "";
        private Map<String,String> headers;
        private int timeoutMs = 5000;
        private boolean followRedirects = true;
    

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setMethod(String method) {
            this.method = method;
            return this;
        }

        public Builder setBody(String body) {
            this.body = body;
            return this;
        }

        public Builder setHeaders(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder setTimeoutMs(int timeoutMs) {
            this.timeoutMs = timeoutMs;
            return this;
        }

        public Builder setFollowRedirects(boolean followRedirects) {
            this.followRedirects = followRedirects;
            return this;
        }

        public HttpRequest build() {
            if (url == null || url.isEmpty()) {
                throw new IllegalStateException("URL cannot be null or empty");
            }
            return new HttpRequest(this);
        }
    }
}


class Main {
    public static void main(String[] args) {
        HttpRequest request = new HttpRequest.Builder()
                .setUrl("https://api.example.com/data")
                .setMethod("POST")
                .setBody("{\"key\":\"value\"}")
                .setTimeoutMs(10000)
                .setFollowRedirects(false)
                .build();

        System.out.println("URL: " + request.getUrl());
        System.out.println("Method: " + request.getMethod());
        System.out.println("Body: " + request.getBody());
        System.out.println("Timeout: " + request.getTimeoutMs() + "ms");
        System.out.println("Follow Redirects: " + request.isFollowRedirects());
    }
}
