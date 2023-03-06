package utils;

import java.awt.*;

public class Luminance {
    public static double getContrastRatio(Color c1, Color c2) {
        double l1 = luminance(c1);
        double l2 = luminance(c2);
        double max = Math.max(l1, l2);
        double min = Math.min(l1, l2);

        return (max + 0.05) / (min + 0.05);
    }

    public static double luminance(Color c) {
        double[] rgb = {c.getRed() / 255., c.getGreen() / 255., c.getBlue() / 255.};
        for (int i = 0; i < rgb.length; i++) {
            if (rgb[i] <= 0.04045) rgb[i] = rgb[i] / 12.92;
            else rgb[i] = Math.pow(((rgb[i] + 0.055) / 1.055), 2.4);
        }

        return 0.2126 * rgb[0] + 0.7152 * rgb[1] + 0.0722 * rgb[2];
    }
}
