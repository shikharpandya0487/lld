package prototype_pattern;

import java.util.HashMap;
import java.util.Map;

// Example 3: Document Template Registry (Prototype Registry pattern)
public class DocumentTemplate implements Cloneable {
    private String title;
    private String header;
    private String footer;
    private String bodyContent;
    private Map<String, String> metadata;

    public DocumentTemplate(String title, String header, String footer) {
        this.title = title;
        this.header = header;
        this.footer = footer;
        this.bodyContent = "";
        this.metadata = new HashMap<>();
    }

    public void setBodyContent(String body) { this.bodyContent = body; }
    public void setTitle(String title) { this.title = title; }
    public void addMetadata(String key, String value) { metadata.put(key, value); }

    @Override
    public DocumentTemplate clone() {
        try {
            DocumentTemplate cloned = (DocumentTemplate) super.clone();
            cloned.metadata = new HashMap<>(this.metadata);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "Document[title=" + title + ", header=" + header
                + ", footer=" + footer + ", body=" + bodyContent
                + ", meta=" + metadata + "]";
    }

    // --- Prototype Registry ---
    public static class Registry {
        private final Map<String, DocumentTemplate> templates = new HashMap<>();

        public void register(String key, DocumentTemplate template) {
            templates.put(key, template);
        }

        public DocumentTemplate get(String key) {
            DocumentTemplate tpl = templates.get(key);
            if (tpl == null) throw new IllegalArgumentException("No template: " + key);
            return tpl.clone();
        }
    }
}
