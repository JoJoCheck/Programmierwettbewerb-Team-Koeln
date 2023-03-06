package gui.filehandler;

import gui.JDialogs;
import main.Area;
import main.Plant;
import main.Point;
import utils.Utils;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class FolderHandler {
    private final Forest[] forests;
    private final List<File> inputs;
    private final List<File> outputs;
    public static final String projectPath = System.getProperty("user.dir");

    public FolderHandler(File inputFolder, File outputFolder, JDialogs dialog) throws IOException {
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();

        init(inputFolder, outputFolder);
        forests = new Forest[inputs.size()];
        readData(dialog);
    }

    // reads the input and output files and saves it into the lists and proofs the folder
    public void init(File inputFolder, File outputFolder) {
        File[] files = inputFolder.listFiles();
        assert files != null;
        Arrays.sort(files);
        for (File file : files) {
            if (file.getName().endsWith(".txt")) {
                inputs.add(file);
            }
        }

        files = outputFolder.listFiles();
        assert files != null;
        Arrays.sort(files);
        for (File file : files) {
            if (file.getName().endsWith(".txt.out")) {
                outputs.add(file);
            }
        }
    }

    // runs through the files and reads the data of the input and output files
    public void readData(JDialogs dialog) throws IOException {
        for (int i = 0; i < forests.length; i++) {
            forests[i] = new Forest();

            // calculates...
            readInput(inputs.get(i), forests[i]);
            readOutput(outputs.get(i), forests[i]);

            final int count = i + 1;
            SwingUtilities.invokeLater(() -> dialog.setLoadingProgress(count, forests.length));
        }
    }

    // checks the input of the output file
    private static List<String> checkOutput(File outputFile) {
        if (outputFile == null || !outputFile.isFile()) {
            throw new IllegalArgumentException((outputFile != null ? outputFile.getAbsolutePath() : "This") + " is not a file!");
        }

        List<String> exceptions = new ArrayList<>();
        Scanner scanner;
        try {
            scanner = new Scanner(outputFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // ab erster Zeile: Pflanzen
        int count = 0;
        while (scanner.hasNextLine()) {
            count++;
            String line = scanner.nextLine().replaceAll(" +", " ");
            String[] values = line.split(" ");
            if (values.length > 4) {
                exceptions.add("To many statements (" + values.length + "/4) " +
                        "in line " + count + " in the output file " + getRelativePath(outputFile));
                continue;
            } else if (values.length < 4) {
                exceptions.add("Missing statements (" + values.length + "/4) " +
                        "in line " + count + " in the output file " + getRelativePath(outputFile));
                continue;
            }

            try {
                Double.parseDouble(values[0]);
                Double.parseDouble(values[1]);
                Double.parseDouble(values[2]);
                Integer.parseInt(values[3]);
            } catch (NumberFormatException e) {
                exceptions.add("Reading the numbers failed in line " + count + " in the output file " +
                        getRelativePath(outputFile));
            }
        }
        scanner.close();

        return exceptions;
    }

    // reads the data of an output file
    private void readOutput(File outputFile, Forest forest) throws IOException {
        Scanner scanner = new Scanner(outputFile);
        // ab erster Zeile: Pflanzen
        List<Area> areas = new ArrayList<>();
        int count = 0;
        while (scanner.hasNextLine()) {
            count++;
            String line = scanner.nextLine().replaceAll(" +", " ");
            String[] values = line.split(" ");
            if (values.length > 4) {
                throw new IOException("To many statements (" + values.length + "/4) " +
                        "in line " + count + " in the output file " + outputFile.getAbsolutePath());
            } else if (values.length < 4) {
                throw new IOException("Missing statements (" + values.length + "/4) " +
                        "in line " + count + " in the output file " + outputFile.getAbsolutePath());
            }

            try {
                double x = Double.parseDouble(values[0]);
                double y = Double.parseDouble(values[1]);
                Double.parseDouble(values[2]);
                int id = Integer.parseInt(values[3]);

                Plant plant = Utils.getPlantWithOriginNumber(id, forest.getPlants());
                areas.add(new Area(new Point(x, y), plant));
            } catch (NumberFormatException e) {
                throw new IOException("Reading the numbers failed in line " + count + " in the output file " +
                        outputFile.getAbsolutePath());
            }
        }
        forest.setAreas(areas);

        // save results
        double[] values = Utils.results(forest.getWidth(), forest.getHeight(), forest.getPlants(), areas);
        forest.setDiversity(values[0]);
        forest.setArea(values[1]);
        forest.setB(values[2]);
        forest.setScore(Utils.getScore(values[2]));

        scanner.close();
    }

    // checks the input of the input file
    private static List<String> checkInput(File inputFile) {
        if (inputFile == null || !inputFile.isFile()) {
            throw new IllegalArgumentException((inputFile != null ? inputFile.getAbsolutePath() : "This") + " is not a file!");
        }

        Scanner scanner;
        try {
            scanner = new Scanner(inputFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        List<String> exceptions = new ArrayList<>();

        // erste Zeile: Name des Gebietes
        if (!scanner.hasNextLine()) {
            exceptions.add("The forest name is missing in the input file " + getRelativePath(inputFile) + ".");
            return exceptions;
        }
        scanner.nextLine();

        // zweite Zeile: Breite, Tiefe
        if (!scanner.hasNextLine()) {
            exceptions.add("The forest sizes are missing in the input file " + getRelativePath(inputFile) + ".");
            return exceptions;
        }

        try {
            scanner.nextInt();
            scanner.nextInt();
        } catch (InputMismatchException e) {
            exceptions.add("Could not read the size of the forest in the input file " + getRelativePath(inputFile) + ".");
            return exceptions;
        }
        if (scanner.hasNextLine()) scanner.nextLine();

        // ab dritter Zeile: Pflanzen
        List<String> list = new ArrayList<>();
        while (scanner.hasNextLine()) {
            list.add(scanner.nextLine());
        }

        for (int i = 0; i < list.size(); i++) {
            String[] values = list.get(i).split(" ");
            if (values.length > 2) {
                exceptions.add("To many statements (" + values.length + "/2) " +
                        "in line " + (i + 3) + " in the input file " + getRelativePath(inputFile));
                continue;
            } else if (values.length < 2) {
                exceptions.add("Missing statements (" + values.length + "/2) " +
                        "in line " + (i + 3) + " in the input file " + getRelativePath(inputFile));
                continue;
            }

            try {
                Double.parseDouble(values[0]);
            } catch (NumberFormatException e) {
                exceptions.add("Reading the radius failed in line " + (i + 3) + " in the input file " +
                        getRelativePath(inputFile));
            }
        }
        scanner.close();
        return exceptions;
    }

    // reads the data of an input files
    private void readInput(File inputFile, Forest forest) throws IOException {
        Scanner scanner = new Scanner(inputFile);

        // Name der Datei
        forest.setFileName(inputFile.getName().split("\\.")[0]);

        // erste Zeile: Name des Gebietes
        if (!scanner.hasNextLine()) {
            throw new IOException("The forest name is missing in the input file " +
                    inputFile.getAbsolutePath() + ".");
        }
        forest.setAreaName(scanner.nextLine());

        // zweite Zeile: Breite, Tiefe
        if (!scanner.hasNextLine()) {
            throw new IOException("The forest sizes are missing in the input file " +
                    inputFile.getAbsolutePath() + ".");
        }
        try {
            forest.setWidth(scanner.nextInt());
            forest.setHeight(scanner.nextInt());
        } catch (InputMismatchException e) {
            throw new IOException("Could not read the size of the forest in the input file " +
                    inputFile.getAbsolutePath() + ".");
        }
        if (scanner.hasNextLine()) scanner.nextLine();

        // ab dritter Zeile: Pflanzen
        List<String> list = new ArrayList<>();
        while (scanner.hasNextLine()) {
            list.add(scanner.nextLine());
        }

        Plant[] plants = new Plant[list.size()];
        for (int i = 0; i < list.size(); i++) {
            String[] values = list.get(i).split(" ");
            if (values.length > 2) {
                throw new IOException("To many statements (" + values.length + "/2) " +
                        "in line " + (i + 3) + " in the input file " + inputFile.getAbsolutePath());
            } else if (values.length < 2) {
                throw new IOException("Missing statements (" + values.length + "/2) " +
                        "in line " + (i + 3) + " in the input file " + inputFile.getAbsolutePath());
            }

            try {
                plants[i] = new Plant(Double.parseDouble(values[0]), values[1], i);
            } catch (NumberFormatException e) {
                throw new IOException("Reading the radius failed in line " + (i + 3) + " in the input file " +
                        inputFile.getAbsolutePath());
            }
        }
        Utils.sortByRadiusAndType(plants);
        forest.setPlants(plants);
        scanner.close();
    }

    // Returns the forests and their results
    public Forest[] getForests() {
        return forests;
    }

    // ---------------------static methods------------------------------------


    // Checks whether the folder has the right fileTypes, folders will be ignored
    // Returns an error, when the folderType is BOTH
    public static FolderInfo checkFolder(File folder, FolderType folderType) {
        // Checks whether the folder is unequal to null
        if (folder == null) return new FolderInfo(folderType, FolderMessage.NULL_FOLDER, null);
        // Checks whether the folder exists
        if (!folder.exists()) return new FolderInfo(folderType, FolderMessage.NO_EXISTING_FOLDER, null);
        // Checks whether the file is a folder
        if (!folder.isDirectory()) return new FolderInfo(folderType, FolderMessage.NOT_A_FOLDER, null);
        // Checks whether the folder is in the project
        if (!folder.getAbsolutePath().startsWith(projectPath)) return new FolderInfo(folderType,
                FolderMessage.NOT_IN_PROJECT_FOLDER, null);

        File[] files = folder.listFiles();
        // Checks whether the folder exists
        if (files == null) return new FolderInfo(folderType, FolderMessage.NO_EXISTING_FOLDER, null);
        // Checks whether the folder has files
        if (countFiles(folder, folderType) == 0)
            return new FolderInfo(folderType, FolderMessage.NO_FILES_IN_FOLDER, null);

        boolean wrongFolder = false;
        List<String> exceptions = new ArrayList<>();

        // checks whether the files are correct
        String extension = folderType == FolderType.INPUT ? ".txt" : ".txt.out";
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(extension)) {
                List<String> listOfExceptions = folderType == FolderType.INPUT ? checkInput(file) : checkOutput(file);
                if (listOfExceptions.size() != 0) {
                    exceptions.addAll(listOfExceptions);
                    wrongFolder = true;
                }
            }
        }

        if (wrongFolder) {
            return new FolderInfo(folderType, FolderMessage.ERRORS_IN_FILE, exceptions);
        }
        return new FolderInfo(folderType, FolderMessage.VALID_FOLDER, null);
    }

    // Checks whether the input and output folders are valid folders
    public static FolderInfo check(File inputFolder, File outputFolder) {
        // checks if the input and output folders are valid folders
        FolderInfo info;
        if ((info = checkFolder(inputFolder, FolderType.INPUT)).folderMessage() != FolderMessage.VALID_FOLDER)
            return info;
        if ((info = checkFolder(outputFolder, FolderType.OUTPUT)).folderMessage() != FolderMessage.VALID_FOLDER)
            return info;

        // counts the number of files in the input and output folders
        int inputs = countFiles(inputFolder, FolderType.INPUT);
        int outputs = countFiles(outputFolder, FolderType.OUTPUT);

        // checks if the input and output folders have the same number of files
        return inputs == outputs ? new FolderInfo(null, FolderMessage.VALID_FOLDER, null) :
                new FolderInfo(null, FolderMessage.DIFFERENT_FILE_NUMBERS, null);
    }

    // Returns a path relative to the project folder
    public static String getRelativePath(File file) {
        return file.getAbsolutePath().replace(projectPath + File.separator, "");
    }

    // Counts the number of files in a folder, folders will be ignored
    public static int countFiles(File folder, FolderType folderType) {
        if (folder == null) return 0;
        File[] files = folder.listFiles();
        if (files == null) return 0;

        // count number of files
        int count = 0;
        String extension = folderType == FolderType.INPUT ? ".txt" : ".txt.out";
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(extension)) count++;
        }
        return count;
    }
}