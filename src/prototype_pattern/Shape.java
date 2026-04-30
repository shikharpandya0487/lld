package prototype_pattern;

// Example 1: Shape Cloning
public abstract class Shape implements Cloneable {
    protected String color;
    protected String type;

    public Shape(String color) {
        this.color = color;
    }

    public abstract double area();

    @Override
    public Shape clone() {  // subclasses return Shape; callers cast to concrete type
        try {
            return (Shape) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return type + " [color=" + color + ", area=" + area() + "]";
    }
}
