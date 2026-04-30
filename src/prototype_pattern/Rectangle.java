package prototype_pattern;

public class Rectangle extends Shape {
    private double width, height;

    public Rectangle(String color, double width, double height) {
        super(color);
        this.type = "Rectangle";
        this.width = width;
        this.height = height;
    }

    public void setWidth(double w) { this.width = w; }
    public void setHeight(double h) { this.height = h; }

    @Override
    public double area() {
        return width * height;
    }
}
