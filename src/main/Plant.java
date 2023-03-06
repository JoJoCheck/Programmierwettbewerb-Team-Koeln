package main;

public class Plant {
    private final double radius;
    private final String type;
    private int index;
    private final int originNumber;

    public Plant(double radius, String name, int originNumber) {
        this.radius = radius;
        this.type = name;
        this.originNumber = originNumber;
        this.index = originNumber;
    }

    public double getRadius() {
        return radius;
    }

    public String getType() {
        return type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getOriginNumber() {
        return originNumber;
    }

    @Override
    public String toString() {
        return "#" + index + "-" + type + "-" + radius;
    }
}