package forms;

import main.Point;

public record Circle(double centerX, double centerY, double radius) {
    public double getArea() {
        return Math.PI * radius * radius;
    }

    public double getSectorArea(double degrees) {
        return getArea() * (degrees / 360);
    }

    public double getSegmentArea(double degrees) {
        // Calculating area of sector
        double sectorArea = getSectorArea(degrees);

        // Calculating area of triangle
        double triangleArea = 1. / 2 * (radius * radius) * Math.sin((degrees * Math.PI) / 180);

        return sectorArea - triangleArea;
    }

    public double getAlpha(Point p1, Point p2) {
        return getAlpha(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }
    public double getAlpha(double x1, double y1, double x2, double y2) {
        double relX1 = x1 - centerX;
        double relY1 = y1 - centerY;
        double relX2 = x2 - centerX;
        double relY2 = y2 - centerY;

        // r[0..*] -> Richtungsvektor
        // cos^(-1) alpha = (r1 * r2) / (|r1| * |r2|)
        double numerator = relX1 * relX2 + relY1 * relY2;
        double denominator = Math.sqrt((relX1 * relX1 + relY1 * relY1) * (relX2 * relX2 + relY2 * relY2));
        return Math.toDegrees(Math.acos(Math.abs(numerator / denominator)));
    }

    public boolean isInside(double x, double y) {
        // Kreisgleichung: (x - xM)^2 + (y - yM)^2 = radius^2
        return Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY)) <= Math.sqrt(radius * radius);
    }

    public double[] getY(double x) {
        // Kreisgleichung: (x - xM)^2 + (y - yM)^2 = radius^2
        // y^2 + p(-2yM) + q(-radius^2 + (x - xM)^2)
        // y1,2 = -p / 2 +- sqrt((p / 2)^2 - q)
        double p = -2 * centerY;
        double q = (x - centerX) * (x - centerX) + centerY * centerY - radius * radius;
        return getPQFormula(p, q);
    }

    public double[] getX(double y) {
        // Kreisgleichung: (x - xM)^2 + (y - yM)^2 = radius^2
        // x^2 + p(-2xM) - q(radius^2 + (y - yM)^2)
        // x1,2 = -p / 2 +- sqrt((p / 2)^2 - q)
        double p = -2 * centerY;
        double q = centerX * centerX + (y - centerY) * (y - centerY) -radius * radius;
        return getPQFormula(p, q);
    }

    private double[] getPQFormula(double p, double q) {
        double pHalf = p / 2;
        // Radikant
        double radical = pHalf * pHalf - q;
        // no solution
        if (radical < 0) return new double[0];
        // one solution
        if (radical == 0) return new double[]{-pHalf};
        // two solutions
        double sqrtValue = Math.sqrt(radical);
        return new double[]{-pHalf - sqrtValue, -pHalf + sqrtValue};
    }
}
