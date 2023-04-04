package main;

import forms.Circle;
import utils.BitMap;
import utils.Utils;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static utils.Utils.getCleanTime;
import static utils.Utils.sortByRadiusAndType;

public class Algorithm {
    private static final double EPSILON = 1e-10;
    private List<Area> areas;
    private int width;
    private int height;
    private Plant[] plants;
    private double erster;
    private double zweiter;
    private double genauigkeit;
    private double blackList = 100000000;
    private String progressValue = "";


    /**
     * Creates a new Algorithm instance.
     * Last change: 18.51 Uhr, 09.02.2023 by Oliver
     */
    public Algorithm(String inputFilename, String outputFilename) throws IOException {
        System.out.println("processing " + inputFilename);
        areas = new ArrayList<>();

        // read input file
        readInput(inputFilename);
        sortByRadiusAndType(plants);

        // run algorithm and save output in a file
        long start = System.currentTimeMillis();
        runAlgorithm();
//        runImageAlgorithm();

        long end = System.currentTimeMillis() - start;
        writeOutput(outputFilename);

        System.out.println("saved " + inputFilename + " into " + outputFilename);
        double result = Utils.b(width, height, plants, areas);
        System.out.println("B: " + result + " (" + Utils.getScore(result) + " Score) -> time: " + getCleanTime(end));
    }

    /**
     * Reads the input file and saves the width, height of the map and an array of plants.
     * Sorts the plants by radius and type.
     * Last change: 18.20 Uhr, 09.02.2023 by Oliver
     */
    private void readInput(String inputFilename) throws IOException {
        Scanner scanner = new Scanner(new File(inputFilename));

        // erste Zeile: Name des Gebietes
        scanner.nextLine();

        // zweite Zeile: Breite, Tiefe
        width = scanner.nextInt();
        height = scanner.nextInt();
        scanner.nextLine();

        // ab dritter Zeile: Pflanzen
        List<String> list = new ArrayList<>();
        while (scanner.hasNextLine()) {
            list.add(scanner.nextLine());
        }

        plants = new Plant[list.size()];
        for (int i = 0; i < list.size(); i++) {
            String[] values = list.get(i).split(" ");
            double radius = Double.parseDouble(values[0]);
            String name = values[1];
            plants[i] = new Plant(radius, name, i);
        }
        scanner.close();
    }

    /**
     * Saves the areas of the map and their plant (x, y, radius, index) in a file
     * Last change: 18.10 Uhr, 09.02.2023 by Oliver
     */
    private void writeOutput(String outputFilename) throws IOException {
        try (FileWriter fileWriter = new FileWriter(outputFilename)) {
            for (Area area : areas) {
                Point p = area.getPoint();
                fileWriter.write(p.getX() + " " + p.getY() + " ");

                Plant plant = area.getPlant();
                fileWriter.write(plant.getRadius() + " " + plant.getOriginNumber());
                fileWriter.write(System.lineSeparator());
            }
        }
    }

    private void placePixelPlants(Color[][] bitmap, Plant plant, Color color) {
        genauigkeit = 1;
        final double plantArea = Math.PI * plant.getRadius() * plant.getRadius();
        for (double x = 0; x < width; x += genauigkeit) {
            System.out.print(x + " / " + width);
            for (double y = 0; y < height; y += genauigkeit) {
                if (isOutside(x, y, plant.getRadius()))
                    continue;

                Circle circle = new Circle(x, y, plant.getRadius());
                if (BitMap.isLegalCirclePixel(bitmap, color, circle, genauigkeit, plantArea)) {
                    Point p = new Point(x, height - y - 1);
                    if (isOutsideAnotherPlants(p, plant.getRadius())) {
                        areas.add(new Area(p, plant));
                    }
                }
            }
            System.out.println(" " + areas.size());
        }
    }

    private void runImageAlgorithm() {
        Color[][] bitmap = BitMap.get("images/bitmap/trump.jpg", width, height);
        System.out.println("Start!");
        Plant plant;

        plant = plants[1];
        placePixelPlants(bitmap, plant, Color.WHITE);
        System.out.println("=".repeat(50));
        System.out.println("End!");
    }

    /**
     * Looks what Algo the best is :D
     * Last change: 20:39 2023-02-08 by Anwenden
     */
    private void runAlgorithm() {
        /*
         * This builds step by step the Answer
         * The Best way for this by waves this make new List
         * Last Change by @Anwenden
         */
        List<List<Area>> lists = new ArrayList<>();
        List<Double> resultValues = new ArrayList<>();
        // this is for waves
        // 930, 5, 4, 3, 2, 1
        genauigkeit = 0.5;

        List<Integer> list = List.of(250,200,200,200,200,100,100,100,100,100,100,100,100,100,100);
        // dataOn == 0 -> die areas List wird nicht geleert
        // list = Stream.iterate(20, n -> n).limit(100).toList()
        // circlePacking(200);

        for (int z = 0; z < list.size(); z++) {
            int i = list.get(z);
            System.out.print(i + " value: ");
            long millis = System.currentTimeMillis();

            //run11();
            runSymmetricAlgorithm(i);

            // calculate the result
            lists.add(new ArrayList<>(areas));
            areas = getBestResult();

            // print the result
            long end = System.currentTimeMillis() - millis;
            int percentage = (int) ((z + 1) / (double) list.size() * 100);
            System.out.println(getResults() + " -> " + percentage + "% (" +
                    (z + 1) + "/" + list.size() + ") -> time: " + getCleanTime(end));
            resultValues.add(Utils.b(width, height, plants, areas));
        }
        // get best list
        double max = 0;
        int index = 0;
        for (int i = 0; i < resultValues.size(); i++) {
            if (resultValues.get(i) > max) {
                max = resultValues.get(i);
                index = i;
            }
        }
        areas = lists.get(index);
    }

    /**
     * Checks if the point is outside.
     */
    private boolean isOutside(double x, double y, double radius) {
        return (x - radius) < 0 || (x + radius) > width || (y - radius) < 0 || (y + radius) > height;
    }

    /**
     * Checks if the point is outside another plants.
     */
    private boolean isOutsideAnotherPlants(Point point, double radius) {
        for (Area a : areas) {
            if (point.distance(a.getPoint()) < radius + a.getPlant().getRadius()
                    - EPSILON) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the planting is possible.
     *
     * @param area the area to check
     * @return true if the planting is possible, false otherwise
     * Last change: 07.02.2023 by Oliver
     */
    private boolean checkPlanting(Area area) {
        // check if point is outside
        if (isOutside(area.getPoint().getX(), area.getPoint().getY(), area.getPlant().getRadius()))
            return false;

        // Checks if the plant is outside another plants
        return isOutsideAnotherPlants(area.getPoint(), area.getPlant().getRadius());
    }

    /**
     * This is a Symmetric way to make a good result
     * -> symmetric placement of plants
     * Written by JojoCheck
     * Last change: 13:12 2023-02-15 by H3RObtw
     */
    private void runSymmetricAlgorithm(int max) {
        long start = System.currentTimeMillis();
        progressValue = "";

        //fängt bei größtem baum an
        for (int i = plants.length - 1; i >= 0; i--) {
            printProgress(i, start);

            erster = plants[i].getRadius();
            zweiter = plants[i].getRadius();
            for (int j = 0; j < max; j++) {
                if (plants[i].getRadius() >= blackList) break;
                if (!placeCircle(plants[i])) {
                    break;
                }
                printProgress(i, start);
            }
        }
        System.out.print("\b".repeat(progressValue.length()));
    }

    private void printProgress(int index, long start) {
        System.out.print("\b".repeat(progressValue.length()));
        int percent = (int) ((plants.length - index) / (double) plants.length * 100);
        progressValue = percent + "% | " + getResults() + " | time: " + Utils.getCleanTime(System.currentTimeMillis() - start);
        System.out.print(progressValue);
    }

    private String getResults() {
        double[] values = Utils.results(width, height, plants, areas);
        return "B=" + Utils.formattedDouble(values[2], 5, true, false, false) +
                " | D=" + Utils.formattedDouble(values[0], 5, true, false, false) +
                " | A=" + Utils.formattedDouble(values[1], 5, true, false, false);
    }

    private List<Area> getBestResult() {
        if (areas.isEmpty()) return new ArrayList<>();
        double[] results = new double[areas.size()];

        List<Area> tempAreas = new ArrayList<>();
        for (int i = 0; i < areas.size(); i++) {
            Area area = areas.get(i);
            tempAreas.add(area);
            results[i] = Utils.b(width, height, plants, tempAreas);
        }

        double max = 0;
        int index = 0;
        for (int i = 0; i < results.length; i++) {
            if (results[i] > max) {
                max = results[i];
                index = i;
            }
        }

        List<Area> result = tempAreas.subList(0, index + 1);
        if (index < result.size() - 1) {
            areas.subList(index + 1, result.size()).clear();
        }
        return result;
    }

    private boolean placeCircle(Plant plant) {
        double width2 = width - plant.getRadius();
        double height2 = height - plant.getRadius();

        for (; erster <= width2; erster = erster + genauigkeit) {
            zweiter = plant.getRadius();
            for (; zweiter <= height2; zweiter = zweiter + genauigkeit) {
                Area a = new Area(new Point(erster, zweiter), plant);
                if (checkPlanting(a)) {
                    if (worthIt(a)) {
                        zweiter = zweiter + (plant.getRadius() * 2) - 1;
                        return true;
                    } else return false;
                }
            }
        }
        addBlacklist(plant);
        return false;
    }

    public boolean worthIt(Area area) {
        double test = Utils.b(width, height, plants, areas);
        areas.add(area);
        if (test < 0.5) return true;
        if (test <= Utils.b(width, height, plants, areas)) return true;
        else {
            areas.remove(area);
            return false;
        }
    }

    public void addBlacklist(Plant plant) {
        blackList = plant.getRadius();
    }

    public void run11() {
        double width2 = width - plants[0].getRadius();
        double height2 = height - plants[0].getRadius();
        erster = plants[0].getRadius();
        zweiter = plants[0].getRadius();

        for (int j = 0; j < 117; j++) {
            System.out.println(j);
            that:
            for (int i = 0; i < plants.length; i++) {
                for (; erster <= width2; erster = erster + genauigkeit) {
                    zweiter = plants[0].getRadius();
                    for (; zweiter <= height2; zweiter = zweiter + genauigkeit) {
                        Area a = new Area(new Point(erster, zweiter), plants[i]);
                        if (checkPlanting(a)) {
                            areas.add(a);
                            zweiter = zweiter + (plants[0].getRadius() * 2);
                            continue that;
                        }
                    }
                }
            }
        }
    }
}