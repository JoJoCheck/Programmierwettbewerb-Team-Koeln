package utils;

import main.Area;
import main.Plant;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public final class Utils {
    // Don't let anyone instantiate this class.
    private Utils() {
    }

    public static int[] getPlantsNumbers(Plant[] plants, List<Area> areas) {
        int[] plantsNumbers = new int[plants.length];
        for (Area area : areas) plantsNumbers[area.getPlant().getIndex()]++;
        return plantsNumbers;
    }

    // calculates diversity of plants in the area. The result is between 0 and 1
    // @param n = number of plants in the area
    public static double diversity(int[] plantsNumbers, List<Area> areas) {
        // number of plants
        final double n = areas.size();
        if (n == 0) return 1;

        double d = 1;
        // sum
        for (int plantNumber : plantsNumbers) {
            // (ni / n) ^ 2
            d -= Math.pow(plantNumber / n, 2);
        }
        return d;
    }

    // calculates area coverage of plants in the area. The result is between 0 and 1
    public static double area(int width, int height, Plant[] plants, int[] plantsNumbers) {
        // sum of ni * PI * ri ^ 2
        double filledArea = 0;
        for (int i = 0; i < plants.length; i++) {
            filledArea += plantsNumbers[i] * Math.PI * Math.pow(plants[i].getRadius(), 2);
        }

        // filledArea / (width * height)
        return filledArea / (width * height);
    }

    // calculates B. The result is between 0 and 1
    public static double b(int width, int height, Plant[] plants, List<Area> areas) {
        int[] plantsNumbers = getPlantsNumbers(plants, areas);
        return diversity(plantsNumbers, areas) * area(width, height, plants, plantsNumbers);
    }

    // returns an array of results. The first element is the diversity,
    // the second is the area coverage and the third is the result.
    // @return an array of results
    public static double[] results(int width, int height, Plant[] plants, List<Area> areas) {
        int[] plantsNumbers = getPlantsNumbers(plants, areas);
        double diversity = diversity(plantsNumbers, areas);
        double areaCoverage = area(width, height, plants, plantsNumbers);
        double b = diversity * areaCoverage;
        return new double[]{diversity, areaCoverage, b};
    }

    public static String getCleanTime(long time) {
        long millis = time;
        long sec = millis / 1000 % 60;
        long min = millis / 1000 / 60 % 60;
        long hour = millis / 1000 / 60 / 60 % 24;
        millis = millis % 1000;

        StringBuilder builder = new StringBuilder();
        if (hour > 0) {
            builder.append(hour < 10 ? "0" : "").append(hour).append(":");
            builder.append(min < 10 ? "0" : "").append(min).append("h ");
            builder.append(sec < 10 ? "0" : "").append(sec).append("s");
        } else if (min > 0) {
            builder.append(min < 10 ? "0" : "").append(min).append(":");
            builder.append(sec < 10 ? "0" : "").append(sec).append("min");
        } else if (sec > 0) {
            if (sec < 10) builder.append(sec).append(".").append(millis / 100);
            else builder.append(sec);
            builder.append("s");

        } else {
            builder.append(millis).append("ms");
        }
        return builder.toString();
    }

    public static double[][] getScores() {
        return new double[][]{
                {0.94, 1185},
                {0.93, 1109},
                {0.92, 1038},
                {0.91, 973},
                {0.90, 912},
                {0.88, 802},
                {0.86, 707},
                {0.84, 625},
                {0.82, 553},
                {0.80, 490},
                {0.78, 435},
                {0.76, 387},
                {0.74, 345},
                {0.72, 308},
                {0.70, 275},
                {0.68, 247},
                {0.66, 222},
                {0.64, 199},
                {0.62, 180},
                {0.60, 162},
                {0.58, 147},
                {0.56, 133},
                {0.54, 121},
                {0.52, 110},
                {0.50, 100},
                {0.45, 80},
                {0.40, 65},
                {0.35, 53},
                {0.30, 44},
                {0.25, 37},
                {0.20, 31},
                {0, 0}
        };
    }

    // guessed score values
    public static int getScore(double b) {
        // B - Wert, Score
        double[][] scores = getScores();

        double score = 0;
        for (int i = 0; i < scores.length - 1; i++) {
            if (b <= scores[i][0] && b >= scores[i + 1][0]) {
                double bValue1 = scores[i + 1][0];
                double bValue2 = scores[i][0];

                double score1 = scores[i + 1][1];
                double score2 = scores[i][1];

                double difValues = bValue2 - bValue1;
                double difScores = score2 - score1;

                double percentage = 1 - (bValue2 - b) / difValues;
                score = score1 + percentage * difScores;
            }
        }
        return (int) Math.round(score);
    }


    // Get plant with the specified index
    public static Plant getPlantWithOriginNumber(int originNumber, Plant[] plants) {
        for (Plant plant : plants) {
            if (plant.getOriginNumber() == originNumber) return plant;
        }
        throw new IllegalArgumentException("Plant with origin number " + originNumber + " not found!");
    }

    // ---------------------------static methods----------------------------------------------------

    public static String capitalize(String str) {
        if (str == null) return null;
        if (str.length() == 0) return "";
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    // --------------------------------- get random color ---------------------------------
    // returns the hex code from the color
    public static String colorToHex(Color color) {
        String buf = Integer.toHexString(color.getRGB());
        return "#" + buf.substring(buf.length() - 6);
    }

    private static float getH(int iteration, int end) {
        int tempIteration = iteration % end;
        float fullRange = (float) (1. / end);
        float range = fullRange * 9 / 10; // 90%, start: 10%
        float h = (float) (Math.random() * range + (tempIteration / (double) end + (fullRange * 1 / 20)));
        if (((int) (iteration / (double) end)) % 2 != 0) {
            h = 1 - h;
        }
        return h;
    }

    public static Color getRandomColor(int iteration, int end, Color contrast, boolean random) {
        if (!random) {
            // tries hsb at first, then random colors
            float h = getH(iteration, end);
            float s = 1;
            float b = 1;
            Color c = Color.getHSBColor(h, s, b);
            if (haveContrast(c, contrast)) return c;

            for (float i = 0.8f; i >= 0.3; i -= 0.01) {
                s = i;
                c = Color.getHSBColor(h, s, b);
                if (haveContrast(c, contrast)) return c;
            }
            s = 1;
            for (float i = 0.8f; i >= 0.3; i -= 0.01) {
                b = i;
                c = Color.getHSBColor(h, s, b);
                if (haveContrast(c, contrast)) return c;
            }
        }

        // tries random colors
        return getContrastRandomColor(contrast);
    }

    private static Color getRandomColor() {
        int r = (int) (Math.random() * 256);
        int g = (int) (Math.random() * 256);
        int b = (int) (Math.random() * 256);
        return new Color(r, g, b);
    }

    private static Color getContrastRandomColor(Color contrast) {
        Color c;
        do {
            c = getRandomColor();
        } while (!haveContrast(c, contrast));
        return c;
    }

    private static boolean haveContrast(Color c1, Color c2) {
        double contrast = Luminance.getContrastRatio(c1, c2);
        return contrast >= 3;
    }

    // --------------------------------- get random color ---------------------------------


    // --------------------------------- get more readable number ---------------------------------

    public static String formattedDouble(double value, int precision, boolean fillZeros, boolean round, boolean moreReadable) {
        if (precision < 0) throw new IllegalArgumentException("Precision must be positive!");
        String string = String.valueOf(value);

        int point = string.indexOf('.');
        int afterCommaNumbers = string.length() - (point + 1);

        StringBuilder sb = new StringBuilder();
        StringBuilder afterComma = new StringBuilder();

        if (afterCommaNumbers < precision) {
            afterComma.append(string, point + 1, string.length());
            if (fillZeros) {
                afterComma.append("0".repeat(precision - afterCommaNumbers));
            }
        } else if (afterCommaNumbers > precision) {
            if (round) {
                long lastNumber = Long.parseLong(string.substring(point + 1, point + 2 + precision)) + 5;
                afterComma.append(string, point + 1, point + precision);
                afterComma.append(lastNumber / 10 % 10);
            } else {
                afterComma.append(string, point + 1, point + 1 + precision);
            }
        } else {
            afterComma.append(string, point + 1, string.length());
        }

        StringBuilder beforeComma = new StringBuilder();
        beforeComma.append(string, 0, point);
        if (moreReadable) {
            // beforeCommaString
            String beforeCommaString = getMoreReadableNumber(beforeComma.toString(), true);
            beforeComma.delete(0, beforeComma.length());
            beforeComma.append(beforeCommaString);

            // afterCommaString
            String afterCommaString = getMoreReadableNumber(afterComma.toString(), false);
            afterComma.delete(0, afterComma.length());
            afterComma.append(afterCommaString);
        }

        sb.append(beforeComma).append(",").append(afterComma);
        return sb.toString();
    }

    public static String formattedInteger(int number) {
        return getMoreReadableNumber(String.valueOf(number), true);
    }

    private static String getMoreReadableNumber(String number, boolean toLeft) {
        if (number.length() <= 3 || (number.length() == 4 && number.startsWith("-"))) {
            return number;
        }
        String clear = number.startsWith("-") ? number.substring(1) : number;
        StringBuilder result = new StringBuilder(number.startsWith("-") ? "-" : "");

        final int len = clear.length();
        if (toLeft) {
            final int insert = number.startsWith("-") ? 1 : 0;
            for (int i = len - 3; i >= 0; i -= 3) {
                if (i > 0) {
                    result.insert(insert, "." + clear.substring(i, i + 3));
                }
                if (i <= 3) {
                    result.insert(insert, clear.substring(0, i));
                }
            }
        } else {
            for (int i = 0; i <= len; i += 3) {
                if (i + 3 < len) {
                    result.append(clear, i, i + 3).append(".");
                } else {
                    result.append(clear, i, len);
                }
            }
        }
        return result.toString();
    }

    // --------------------------------- get more readable number ---------------------------------

    /**
     * Written by JojoCheck 20:52 2023-02-08
     * and changed by okOrange 10:39 2023-02-09 with comparator notation
     * this sorts the plants by radius
     * Be careful it changes plants!
     */
    public static void sortByRadiusAndType(Plant[] plants) {
        Arrays.sort(plants, Comparator.comparing(Plant::getRadius).thenComparing(Plant::getType));
        // updates the indizes of the plants
        for (int i = 0; i < plants.length; i++) {
            plants[i].setIndex(i);
        }
    }
}