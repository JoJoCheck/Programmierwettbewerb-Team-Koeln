package forms;

import main.Point;

import java.util.ArrayList;
import java.util.List;

public class Intersections {
    private static boolean isSquareFullyInCircle(Square square, Circle circle) {
        double x1 = square.x();
        double x2 = x1 + square.size();
        double y1 = square.y();
        double y2 = y1 + square.size();
        double r = circle.radius();

        // left bottom
        if (Point.distance(x1, y1, circle.centerX(), circle.centerY()) > r) return false;

        // left top
        if (Point.distance(x1, y2, circle.centerX(), circle.centerY()) > r) return false;

        // right bottom
        if (Point.distance(x2, y1, circle.centerX(), circle.centerY()) > r) return false;

        // right top
        return Point.distance(x2, y2, circle.centerX(), circle.centerY()) <= r;
    }

    private static List<Point> getIntersections(Square square, Circle circle) {
        double x1 = square.x();
        double x2 = square.x() + square.size();
        double y1 = square.y();
        double y2 = square.y() + square.size();
        double cx = circle.centerX();
        double cy = circle.centerY();
        double r = circle.radius();
        List<Point> points = new ArrayList<>();

        if (!((x1 >= cx + r && x2 >= cx + r) || (x1 <= cx - r && x2 <= cx - r))) {
            searchX(circle, y1, x1, x2, points);
            searchX(circle, y2, x1, x2, points);
        }

        if (!((y1 >= cy + r && y2 >= cy + r) || (y1 <= cy - r && y2 <= cy - r))) {
            searchY(circle, x1, y1, y2, points);
            searchY(circle, x2, y1, y2, points);
        }

        return points;
    }

    private static void searchY(Circle circle, double x, double y1, double y2, List<Point> points) {
        double[] yResults = circle.getY(x);
        for (double y : yResults) {
            Point p = new Point(x, y);
            if (y >= y1 && y <= y2 && circle.isInside(x, y) && !points.contains(p)) {
                points.add(p);
            }
        }
    }

    private static void searchX(Circle circle, double y, double x1, double x2, List<Point> points) {
        double[] xResults = circle.getX(y);
        for (double x : xResults) {
            Point p = new Point(x, y);
            if (x >= x1 && x <= x2 && circle.isInside(x, y) && !points.contains(p)) {
                points.add(p);
            }
        }
    }

    public static double getIntersectionArea(Square square, Circle circle) {
        if (isSquareFullyInCircle(square, circle)) return square.getArea();
        List<Point> intersections = getIntersections(square, circle);
        if (intersections.size() == 2) {
            List<Point> points = getPointsInCircle(circle, square, intersections);
            return new Polygon(points).getArea() + circle.getSegmentArea(
                    circle.getAlpha(intersections.get(0), intersections.get(1)));
        }
        if (intersections.size() == 4) return circle.getArea();
        return 0;
    }

    private static List<Point> getPointsInCircle(Circle circle, Square square, List<Point> intersections) {
        List<Point> points = new ArrayList<>(intersections);
        Point[] squarePoints = {new Point(square.x(), square.y()),
                new Point(square.x(), square.y() + square.size()),
                new Point(square.x() + square.size(), square.y()),
                new Point(square.x() + square.size(), square.y() + square.size())};

        for (Point p : squarePoints) {
            if (!points.contains(p) && circle.isInside(p.getX(), p.getY())) points.add(p);
        }
        return points;
    }
}
