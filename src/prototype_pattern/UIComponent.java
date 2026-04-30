package prototype_pattern;

import java.util.ArrayList;
import java.util.List;

// Example 5: UI Component tree (composite + prototype)
public class UIComponent implements Cloneable {
    private String type;       // "Button", "Panel", "TextField"
    private String id;
    private int x, y, width, height;
    private String style;
    private List<UIComponent> children;

    public UIComponent(String type, String id, int x, int y, int w, int h, String style) {
        this.type = type;
        this.id = id;
        this.x = x; this.y = y;
        this.width = w; this.height = h;
        this.style = style;
        this.children = new ArrayList<>();
    }

    public void addChild(UIComponent child) { children.add(child); }
    public void setId(String id) { this.id = id; }
    public void moveTo(int x, int y) { this.x = x; this.y = y; }

    // Deep clone — recursively clones all children
    @Override
    public UIComponent clone() {
        try {
            UIComponent cloned = (UIComponent) super.clone();
            cloned.children = new ArrayList<>();
            for (UIComponent child : this.children) {
                cloned.children.add(child.clone());
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return type + "#" + id + " at (" + x + "," + y + ") "
                + width + "x" + height + " style=" + style
                + (children.isEmpty() ? "" : " children=" + children);
    }
}
