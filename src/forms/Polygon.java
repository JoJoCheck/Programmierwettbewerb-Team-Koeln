package forms;

import main.Point;

import java.util.List;

public class Polygon {
    private final List<Point> points;

    public Polygon(List<Point> points)  {
        this.points = points;
        sort();
    }

    private Point findCenter() {
        double x = 0;
        double y = 0;
        for (Point p : points) {
            x += p.getX();
            y += p.getY();
        }
        return new Point(x / points.size(), y / points.size());
    }

    private void sort() {
        // get centroid
        Point center = findCenter();
        points.sort((a, b) -> {
            double a1 = (Math.toDegrees(Math.atan2(a.getX() - center.getX(), a.getY() - center.getY())) + 360) % 360;
            double a2 = (Math.toDegrees(Math.atan2(b.getX() - center.getX(), b.getY() - center.getY())) + 360) % 360;
            return (int) (a1 - a2);
        });
    }

    public double getArea() {
        if (points == null) return 0;

        // slicker algorithm
        double total = 0;
        for (int i = 0; i < points.size(); i++) {
            int j = (i + 1) % points.size();
            total += (points.get(i).getX() * points.get(j).getY()) -
                    (points.get(j).getX() * points.get(i).getY());
        }
        return Math.abs(total / 2);
    }
}