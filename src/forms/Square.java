package forms;

public record Square(double x, double y, double size) {
    public double getArea() {
        return size * size;
    }
}