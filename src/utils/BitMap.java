package utils;

import forms.Circle;
import forms.Intersections;
import forms.Square;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class BitMap {
    // distance von (0,0,0) bis (255,255,255)
    private static final double maxLength = Math.sqrt(3 * 255 * 255);

    private BitMap() {
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        graphics2D.setColor(Color.WHITE);
        graphics2D.fillRect(0, 0, targetWidth, targetHeight);
        graphics2D.drawImage(resultingImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    public static Color[][] get(String imagePath, int width, int height) {
        try {
            BufferedImage image = resizeImage(ImageIO.read(new File(imagePath)), width, height);

            Color[][] bitmap = new Color[width][height];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    // get pixel color
                    bitmap[x][y] = new Color(image.getRGB(x, y), false);
                }
            }
            return bitmap;
        } catch (IOException e) {
            throw new RuntimeException("The image could not be resized.");
        }
    }

    public static double getColorDifference(Color c1, Color c2) {
        // euklidischer Abstand
        double result = Math.pow((c1.getRed() - c2.getRed()), 2) + Math.pow(c1.getGreen() - c2.getGreen(), 2) +
                Math.pow(c1.getBlue() - c2.getBlue(), 2);
        result = Math.sqrt(result);
        // Normierung -> 0 bis 1
        result = result / maxLength;
        return result;
    }

    public static boolean isLegalCirclePixel(Color[][] bitmap, Color color, Circle circle, double genauigkeit, double plantArea) {
        final double x2 = circle.centerX() + circle.radius();
        final double y2 = circle.centerY() + circle.radius();
        double area = 0;

        for (double x = circle.centerX() - circle.radius(); x < x2; x += genauigkeit) {
            for (double y = circle.centerY() - circle.radius(); y < y2; y+= genauigkeit) {
                Square s = new Square(x, y, 1);
                double curArea = Intersections.getIntersectionArea(s, circle);

                if (curArea > 0) {
                    Color c = bitmap[(int) x][(int) y];
                    // wenn ein fast gleiches Pixel gescannt wird (difference 0-1)
                    if (getColorDifference(c, color) <= 0.2) {
                        area += curArea;
                    }
                }
            }
        }
        // get the area in percentage which is filled with the same color
        return area / plantArea >= 0.25;
    }
}