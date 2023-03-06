package main;

public class Area {
    private final Point point;
    private final Plant plant;

    public Area(Point point, Plant plant) {
        this.point = point;
        this.plant = plant;
    }

    public Point getPoint() {
        return point;
    }

    public Plant getPlant() {
        return plant;
    }
}
