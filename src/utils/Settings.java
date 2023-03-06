package utils;

import gui.filehandler.FolderHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Settings {
    private File inputFolder, outputFolder;
    private final File saveFolder, saveFile;
    private boolean startPopupEnabled;
    private boolean soundEnabled;
    private int volume;
    private boolean randomColorsEnabled;

    public Settings() {
        // initializes folder
        saveFolder = new File("data").getAbsoluteFile();
        saveFile = new File("data/settings.txt").getAbsoluteFile();
        startPopupEnabled = true;
        soundEnabled = false;
        volume = 50;
        randomColorsEnabled = true;
    }

    // Reads the settings from the settings file
    public void readSettings() throws IOException {
        // create settings file if it does not exist
        boolean created = createSettingsFile();
        if (created) {
            return;
        }

        // read settings file
        Scanner scanner = new Scanner(saveFile);
        List<String> lines = new ArrayList<>();
        while (scanner.hasNextLine()) {
            lines.add(scanner.nextLine());
        }

        if (lines.size() >= 6 && (lines.get(5).equalsIgnoreCase("true") ||
                lines.get(5).equalsIgnoreCase("false"))) {
            randomColorsEnabled = Boolean.parseBoolean(lines.get(5));
        } else randomColorsEnabled = true;

        if (lines.size() >= 5) {
            try {
                volume = Integer.parseInt(lines.get(4));
            } catch (NumberFormatException e) {
                volume = 50;
            }
        } else volume = 50;

        if (lines.size() >= 4 && (lines.get(3).equalsIgnoreCase("true") ||
                lines.get(3).equalsIgnoreCase("false"))) {
            soundEnabled = Boolean.parseBoolean(lines.get(3));
        } else soundEnabled = false;

        if (lines.size() >= 3) outputFolder = new File(lines.get(2)).getAbsoluteFile();
        else outputFolder = null;

        if (lines.size() >= 2) inputFolder = new File(lines.get(1)).getAbsoluteFile();
        else inputFolder = null;

        if (lines.size() >= 1 && (lines.get(0).equalsIgnoreCase("true") ||
                lines.get(0).equalsIgnoreCase("false"))) {
            startPopupEnabled = Boolean.parseBoolean(lines.get(0));
        } else startPopupEnabled = true;

        scanner.close();
    }

    // Saves settings to the settings file
    public void saveSettings() throws IOException {
        // create settings file if it does not exist
        createSettingsFile();

        FileWriter fileWriter = new FileWriter(saveFile);
        fileWriter.write(String.valueOf(startPopupEnabled));
        fileWriter.write(System.lineSeparator());

        // Saves the files with relative paths to the project
        fileWriter.write(FolderHandler.getRelativePath(inputFolder));
        fileWriter.write(System.lineSeparator());
        fileWriter.write(FolderHandler.getRelativePath(outputFolder));
        fileWriter.write(System.lineSeparator());

        // save sound settings
        fileWriter.write(String.valueOf(soundEnabled));
        fileWriter.write(System.lineSeparator());
        fileWriter.write(String.valueOf(volume));
        fileWriter.write(System.lineSeparator());

        // save random colors settings
        fileWriter.write(String.valueOf(randomColorsEnabled));

        fileWriter.close();
    }

    // Creates a new settings file in the data folder
    private boolean createSettingsFile() throws IOException {
        boolean created = false;
        if (!saveFolder.exists()) {
            created = true;
            if (!saveFolder.mkdir())
                throw new RuntimeException("Could not create folder: " + saveFolder.getAbsolutePath());
        }

        if (!saveFile.exists()) {
            if (!saveFile.createNewFile())
                throw new RuntimeException("Could not create file: " + saveFile.getAbsolutePath());
        }
        return created;
    }

    public void setStartPopupEnabled(boolean startPopupEnabled) {
        this.startPopupEnabled = startPopupEnabled;
    }

    public boolean isStartPopupEnabled() {
        return startPopupEnabled;
    }

    public File getInputFolder() {
        return inputFolder;
    }

    public void setInputFolder(File inputFolder) {
        this.inputFolder = inputFolder;
    }

    public File getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(File outputFolder) {
        this.outputFolder = outputFolder;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public boolean isRandomColorsEnabled() {
        return randomColorsEnabled;
    }

    public void setRandomColorsEnabled(boolean randomColorsEnabled) {
        this.randomColorsEnabled = randomColorsEnabled;
    }
}