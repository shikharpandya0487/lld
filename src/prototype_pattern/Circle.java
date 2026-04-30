package prototype_pattern;

public class Circle extends Shape {
    private double radius;

    public Circle(String color, double radius) {
        super(color);
        this.type = "Circle";
        this.radius = radius;
    }

    public void setRadius(double radius) { this.radius = radius; }
    public double getRadius() { return radius; }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}
