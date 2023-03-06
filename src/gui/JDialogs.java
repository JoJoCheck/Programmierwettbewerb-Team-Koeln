package gui;

import gui.filehandler.*;
import utils.MusicPlayer;
import utils.Settings;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JDialogs {
    private JLabel progressLabel;
    private JSlider volumeSlider;
    private JCheckBox playAnimationCheckBox;
    private JProgressBar progressBar;
    private Forest[] forests;
    private final Settings settings;
    private final Gui gui;
    private final JFrame frame;
    private final Image checkImage, xImage;
    private boolean acceptedDirs = false;
    private File tempInputFolder, tempOutputFolder;
    private final MusicPlayer musicPlayer;

    // provides dialogs
    public JDialogs(Settings settings, Gui gui, JFrame frame, MusicPlayer musicPlayer) {
        this.settings = settings;
        this.gui = gui;
        this.frame = frame;
        this.checkImage = new ImageIcon("images/gui/check.png").getImage();
        this.xImage = new ImageIcon("images/gui/x.png").getImage();
        this.tempInputFolder = settings.getInputFolder();
        this.tempOutputFolder = settings.getOutputFolder();
        this.musicPlayer = musicPlayer;
    }

    // -------------------- Load Output dialog --------------------

    // Updates the progressbar in the dialog
    public void setLoadingProgress(int files, int totalFiles) {
        int progress = files * 100 / totalFiles;
        progressBar.setValue(progress);
        progressLabel.setText("Files: " + files + " / " + totalFiles);
    }

    // Gets the outputs and the progress is updated in the dialog
    public Forest[] getOutputDialog() {
        //boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // boolean exitOnFailure;
        // dialog
        JDialog dialog = new JDialog(frame, "Load Message", true);
        dialog.setIconImage(frame.getIconImage());
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                gui.closeApplication(dialog);
            }
        });

        // panel
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // loadingLabel
        JLabel loadingLabel = new JLabel("Data is loading...");
        panel.add(loadingLabel, BorderLayout.NORTH);

        // progressPanel
        JPanel progressPanel = new JPanel(new BorderLayout(0, 2));

        // progressbar
        progressBar = new JProgressBar(1, 100);
        progressPanel.add(progressBar, BorderLayout.NORTH);

        // progressLabel
        progressLabel = new JLabel();
        progressLabel.setHorizontalAlignment(SwingConstants.HORIZONTAL);
        progressPanel.add(progressLabel, BorderLayout.SOUTH);

        // adding progressPanel and panel to dialog
        panel.add(progressPanel, BorderLayout.SOUTH);
        dialog.add(panel);

        // pack dialog
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);

        // Starts a new thread and reads the output folder
        Thread thread = new Thread(() -> {
            try {
                FolderHandler folderHandler = new FolderHandler(settings.getInputFolder(), settings.getOutputFolder(), this);
                forests = folderHandler.getForests();
                dialog.dispose();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        });
        thread.start();
        dialog.setVisible(true);

        // wait for the thread to finish
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Thread was interrupted.");
        }
        return forests;
    }

    // -------------------- Settings dialog --------------------
    private static class ImageLabel extends JLabel {
        private Image image;

        protected void paintComponent(Graphics g) {
            if (image != null) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        }

        public void setImage(Image image) {
            this.image = image;
            this.repaint();
        }
    }

    // Create a directories dialog with input and output textFields
    public boolean createDirectoriesDialog(String dialogTitle, boolean start) {
        acceptedDirs = true;
        FolderInfo inputInfo = FolderHandler.checkFolder(settings.getInputFolder(), FolderType.INPUT);
        FolderInfo outputInfo = FolderHandler.checkFolder(settings.getOutputFolder(), FolderType.OUTPUT);

        boolean validInputFolder = inputInfo.folderMessage() == FolderMessage.VALID_FOLDER;
        boolean validOutputFolder = outputInfo.folderMessage() == FolderMessage.VALID_FOLDER;

        // dialog
        JDialog dialog = new JDialog(frame, dialogTitle, true);
        dialog.setIconImage(frame.getIconImage());
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                if (start) {
                    if (!validInputFolder || !validOutputFolder) System.exit(0);
                    else gui.closeApplication(dialog);
                } else {
                    acceptedDirs = false;
                    dialog.dispose();
                }
            }
        });

        // pane
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // inputsPanel
        JPanel inputsPanel = new JPanel(new GridBagLayout());

        // GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 0, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // inputInfoLabel
        ImageLabel inputInfoLabel = new ImageLabel();
        inputInfoLabel.setPreferredSize(new Dimension(15, 15));
        inputsPanel.add(inputInfoLabel, gbc);
        if (validInputFolder) {
            inputInfoLabel.setImage(checkImage);
        } else inputInfoLabel.setImage(xImage);

        // gbc
        gbc.gridx++;
        gbc.insets = new Insets(0, 0, 0, 0);

        JLabel inputFilesLabel = new JLabel();
        inputFilesLabel.setPreferredSize(new Dimension(50, 15));
        inputFilesLabel.setText("Files: " + (validInputFolder ? FolderHandler.countFiles(settings.getInputFolder(), FolderType.INPUT) : "0"));
        inputsPanel.add(inputFilesLabel, gbc);

        // gbc
        gbc.gridx++;
        gbc.insets = new Insets(0, 0, 0, 15);

        // inputField
        JTextField inputField = new JTextField(30);
        if (validInputFolder) {
            inputField.setText(settings.getInputFolder().getAbsolutePath());
        }
        inputField.putClientProperty("JTextField.placeholderText", "Input folder path");
        addFocusListener(dialog, inputInfoLabel, inputFilesLabel, inputField, FolderType.INPUT);
        inputsPanel.add(inputField, gbc);

        // gbc
        gbc.gridx++;
        gbc.insets = new Insets(0, 0, 0, 0);

        // inputButton
        JButton inputButton = new JButton("Input");
        addPressedListener(dialog, inputInfoLabel, inputFilesLabel, inputField, inputButton, FolderType.INPUT);
        inputsPanel.add(inputButton, gbc);

        // gbc
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 5, 5);

        // outputInfoLabel
        ImageLabel outputInfoLabel = new ImageLabel();
        outputInfoLabel.setPreferredSize(new Dimension(15, 15));
        inputsPanel.add(outputInfoLabel, gbc);
        if (validOutputFolder) {
            outputInfoLabel.setImage(checkImage);
        } else outputInfoLabel.setImage(xImage);

        // gbc
        gbc.gridx++;
        gbc.insets = new Insets(5, 0, 5, 0);

        JLabel outputFilesLabel = new JLabel();
        outputFilesLabel.setPreferredSize(new Dimension(50, 15));
        outputFilesLabel.setText("Files: " + (validOutputFolder ? FolderHandler.countFiles(settings.getOutputFolder(), FolderType.OUTPUT) : "0"));
        inputsPanel.add(outputFilesLabel, gbc);

        // gbc
        gbc.insets = new Insets(5, 0, 5, 15);
        gbc.gridx++;

        // outputField
        JTextField outputField = new JTextField(30);
        if (validOutputFolder) {
            outputField.setText(settings.getOutputFolder().getAbsolutePath());
        }
        outputField.putClientProperty("JTextField.placeholderText", "Output folder path");
        addFocusListener(dialog, outputInfoLabel, outputFilesLabel, outputField, FolderType.OUTPUT);
        inputsPanel.add(outputField, gbc);

        // gbc
        gbc.gridx++;
        gbc.insets = new Insets(5, 0, 5, 0);

        // inputButton
        JButton outputButton = new JButton("Output");
        addPressedListener(dialog, outputInfoLabel, outputFilesLabel, outputField, outputButton, FolderType.OUTPUT);
        inputsPanel.add(outputButton, gbc);

        // Checkbox
        JCheckBox checkBoxAskAgain = new JCheckBox("Do you want to be asked the next time again?");
        if (settings.isStartPopupEnabled()) {
            // gbc
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.insets = new Insets(10, 0, 0, 0);
            gbc.gridwidth = 4;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.WEST;

            // checkBoxAskAgain
            checkBoxAskAgain.setSelected(true);
            inputsPanel.add(checkBoxAskAgain, gbc);
        }

        // buttonsPanel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonsPanel.setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, Color.GRAY),
                new EmptyBorder(10, 0, 0, 0)));

        // okButton
        JButton acceptButton = new JButton("Accept");
        acceptButton.addActionListener(e -> accept(dialog, settings.isStartPopupEnabled(), settings.isRandomColorsEnabled(),
                settings.isSoundEnabled(), settings.getVolume()));
        buttonsPanel.add(acceptButton);

        // cancelButton
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            if (start) gui.closeApplication(dialog);
            else {
                acceptedDirs = false;
                dialog.dispose();
            }
        });
        buttonsPanel.add(cancelButton);

        // adding to dialog
        panel.add(inputsPanel, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        dialog.add(panel);

        // pack
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);

        // Set carets of textFields
        inputField.setCaretPosition(inputField.getText().length());
        outputField.setCaretPosition(outputField.getText().length());

        new Thread(() -> {
            if (inputInfo.folderMessage() == FolderMessage.ERRORS_IN_FILE &&
                    outputInfo.folderMessage() == FolderMessage.ERRORS_IN_FILE) {
                inputInfo.errorInfos().set(0, "Input Folder Files Errors:\n" + inputInfo.errorInfos().get(0));
                outputInfo.errorInfos().set(0, "Output Folder Files Errors:\n" + outputInfo.errorInfos().get(0));

                List<String> exceptions = new ArrayList<>(inputInfo.errorInfos());
                exceptions.add("=".repeat(50));
                exceptions.addAll(outputInfo.errorInfos());

                tempInputFolder = null;
                tempOutputFolder = null;
                showMessageDialog(dialog, (String) FolderMessage.getMessage(inputInfo.folderMessage(), null),
                        exceptions);
            } else if (inputInfo.folderMessage() == FolderMessage.ERRORS_IN_FILE) {
                tempInputFolder = null;
                showMessageDialog(dialog, (String) FolderMessage.getMessage(inputInfo.folderMessage(), inputInfo.folderType()),
                        inputInfo.errorInfos());
            } else if (outputInfo.folderMessage() == FolderMessage.ERRORS_IN_FILE) {
                tempOutputFolder = null;
                showMessageDialog(dialog, (String) FolderMessage.getMessage(outputInfo.folderMessage(), outputInfo.folderType()),
                        outputInfo.errorInfos());
            }
        }).start();
        // makes the dialog visible
        acceptButton.requestFocusInWindow();
        dialog.setVisible(true);
        return acceptedDirs;
    }

    public void createSettingsDialog() {
        // dialog
        JDialog dialog = new JDialog(frame, "Settings", true);
        dialog.setIconImage(frame.getIconImage());
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                musicPlayer.setVolume(settings.getVolume());
                dialog.dispose();
            }
        });

        // pane
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // popupLabel
        JLabel popupLabel = new JLabel("Opening Popup Directory at start:");
        panel.add(popupLabel, gbc);

        // gbc
        gbc.insets = new Insets(0, 20, 0, 5);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx++;

        // checkBox
        JCheckBox popupCheckBox = new JCheckBox();
        popupCheckBox.setSelected(settings.isStartPopupEnabled());
        panel.add(popupCheckBox, gbc);

        // gbc
        gbc.insets = new Insets(5, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy++;

        // popupLabel
        JLabel colorsLabel = new JLabel("Randomize Circles Color:");
        panel.add(colorsLabel, gbc);

        // gbc
        gbc.insets = new Insets(5, 20, 0, 5);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx++;

        // checkBox
        JCheckBox colorCheckBox = new JCheckBox();
        colorCheckBox.setSelected(settings.isRandomColorsEnabled());
        panel.add(colorCheckBox, gbc);

        // gbc
        gbc.insets = new Insets(10, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;

        // soundPanel
        JPanel soundPanel = getSoundPanel();
        panel.add(soundPanel, gbc);

        // gbc
        gbc.gridy++;

        // buttonsPanel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonsPanel.setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, Color.GRAY),
                new EmptyBorder(10, 0, 0, 0)));

        // cancelButton
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            musicPlayer.setVolume(settings.getVolume());
            dialog.dispose();
        });

        // okButton
        JButton acceptButton = new JButton("Accept");
        acceptButton.addActionListener(e -> {
            boolean oldValue = settings.isRandomColorsEnabled();
            accept(dialog, popupCheckBox.isSelected(), colorCheckBox.isSelected(), playAnimationCheckBox.isSelected(), volumeSlider.getValue());
            if (colorCheckBox.isSelected() != oldValue) {
                gui.refreshCircleColors();
            }
        });

        buttonsPanel.add(acceptButton);
        buttonsPanel.add(cancelButton);
        panel.add(buttonsPanel, gbc);

        // add to dialog
        dialog.add(panel);

        // pack and set dialog visible
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private JPanel getSoundPanel() {
        // soundPanel
        JPanel soundPanel = new JPanel(new GridBagLayout());
        soundPanel.setBorder(new CompoundBorder(new TitledBorder("Sound Settings"),
                new EmptyBorder(10, 10, 10, 10)));

        // GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // playAnimationLabel
        JLabel playAnimationLabel = new JLabel("Play animation sound:");
        soundPanel.add(playAnimationLabel, gbc);

        // gbc
        gbc.insets = new Insets(0, 10, 0, 0);
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 2;

        // playAnimationCheckBox
        playAnimationCheckBox = new JCheckBox();
        playAnimationCheckBox.setSelected(settings.isSoundEnabled());
        soundPanel.add(playAnimationCheckBox, gbc);

        // gbc
        gbc.insets = new Insets(5, 0, 0, 0);
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++;

        // volumeLabel
        JLabel volumeTextLabel = new JLabel("Volume:");
        soundPanel.add(volumeTextLabel, gbc);

        // gbc
        gbc.insets = new Insets(5, 10, 0, 0);
        gbc.gridx++;

        // volumeSlider
        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, settings.getVolume());
        soundPanel.add(volumeSlider, gbc);

        // gbc
        gbc.insets = new Insets(5, 0, 0, 3);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.gridx++;

        // valueLabel
        JLabel volumeLabel = new JLabel(String.valueOf(settings.getVolume()));
        soundPanel.add(volumeLabel, gbc);
        updateValue(playAnimationCheckBox, volumeTextLabel, volumeSlider, volumeLabel);

        // Listener
        volumeSlider.addChangeListener(e -> {
            musicPlayer.setVolume(volumeSlider.getValue());
            volumeLabel.setText(String.valueOf(volumeSlider.getValue()));
        });
        playAnimationCheckBox.addActionListener(e -> updateValue(playAnimationCheckBox, volumeTextLabel,
                volumeSlider, volumeLabel));

        return soundPanel;
    }

    private void updateValue(JCheckBox checkBox, JLabel volumeTextLabel, JSlider volumeSlider, JLabel volumeLabel) {
        if (checkBox.isSelected()) {
            volumeTextLabel.setEnabled(true);
            volumeSlider.setEnabled(true);
            volumeLabel.setEnabled(true);
        } else {
            volumeTextLabel.setEnabled(false);
            volumeSlider.setEnabled(false);
            volumeLabel.setEnabled(false);
        }
    }

    // Focus Listener for the input and output fields
    private void addFocusListener(Component parent, ImageLabel imageLabel, JLabel filesLabel, JTextField textField, FolderType folderType) {
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                File folder = new File(textField.getText());

                // if no change was made, return the method
                File oldFolder = folderType == FolderType.INPUT ? tempInputFolder : tempOutputFolder;
                if ((oldFolder == null && textField.getText().length() == 0) ||
                        (oldFolder != null && oldFolder.getAbsolutePath().equals(folder.getAbsolutePath()))) {
                    return;
                }

                // check if the folder is valid
                FolderInfo folderInfo = FolderHandler.checkFolder(folder, folderType);
                if (folderInfo.folderMessage() == FolderMessage.VALID_FOLDER) {
                    // change temporary folder
                    if (folderType == FolderType.INPUT) tempInputFolder = folder;
                    else tempOutputFolder = folder;

                    textField.setText(folder.getAbsolutePath());
                    imageLabel.setImage(checkImage);
                    filesLabel.setText("Files: " + FolderHandler.countFiles(folder, folderType));
                } else {
                    if (oldFolder == null) textField.setText("");
                    else textField.setText(oldFolder.getAbsolutePath());

                    if (folderInfo.folderMessage() == FolderMessage.ERRORS_IN_FILE) {
                        showMessageDialog(parent, (String) FolderMessage.getMessage(folderInfo.folderMessage(), folderInfo.folderType()),
                                folderInfo.errorInfos());
                        return;
                    }
                    JOptionPane.showMessageDialog(parent, FolderMessage.getMessage(folderInfo.folderMessage(), folderInfo.folderType()),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Button Listener for the input and output buttons
    private void addPressedListener(Component parent, ImageLabel imageLabel, JLabel filesLabel, JTextField textField, JButton button,
                                    FolderType folderType) {
        button.addActionListener(e -> {
            File folder = getFolderByFileChooser(parent, folderType);
            // fileChooser was canceled
            if (folder == null) return;

            // check if the folder is valid
            FolderInfo folderInfo = FolderHandler.checkFolder(folder, folderType);
            if (folderInfo.folderMessage() == FolderMessage.VALID_FOLDER) {
                // change temporary folder
                if (folderType == FolderType.INPUT) tempInputFolder = folder;
                else tempOutputFolder = folder;

                textField.setText(folder.getAbsolutePath());
                imageLabel.setImage(checkImage);
                filesLabel.setText("Files: " + FolderHandler.countFiles(folder, folderType));
                return;
            }
            if (folderInfo.folderMessage() == FolderMessage.ERRORS_IN_FILE) {
                showMessageDialog(parent, (String) FolderMessage.getMessage(folderInfo.folderMessage(), folderInfo.folderType()),
                        folderInfo.errorInfos());
                return;
            }
            JOptionPane.showMessageDialog(parent, FolderMessage.getMessage(folderInfo.folderMessage(), folderInfo.folderType()),
                    "Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    private void showMessageDialog(Component parent, String errorMessage, List<String> errorInfos) {
        JLabel label = new JLabel(errorMessage);
        label.setBorder(new EmptyBorder(0, 5, 5, 5));
        JTextArea textArea = new JTextArea(10, 40);
        textArea.setLineWrap(true);
        if (errorInfos != null) {
            for (String errorInfo : errorInfos) {
                textArea.append(errorInfo + "\n\n");
            }
        }
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(parent, new Object[]{label, scrollPane}, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // method for accept button: applies the new settings when directories are broken
    private void accept(Dialog dialog, boolean askAgain, boolean randomColorsEnabled, boolean soundEnabled, int volume) {
        if (check(dialog)) {
            // save settings
            try {
                settings.setStartPopupEnabled(askAgain);
                settings.setRandomColorsEnabled(randomColorsEnabled);
                settings.setInputFolder(tempInputFolder);
                settings.setOutputFolder(tempOutputFolder);
                settings.setSoundEnabled(soundEnabled);
                settings.setVolume(volume);
                settings.saveSettings();
            } catch (IOException ex) {
                throw new RuntimeException("Saving settings file failed: " + ex.getMessage());
            }
            dialog.dispose();
        }
    }

    // Checks if the input and output folder are correct
    public boolean check(Component parent) {
        FolderInfo folderInfo = FolderHandler.check(tempInputFolder, tempOutputFolder);
        if (folderInfo.folderMessage() == FolderMessage.VALID_FOLDER) {
            return true;
        }
        JOptionPane.showMessageDialog(parent, FolderMessage.getMessage(folderInfo.folderMessage(), folderInfo.folderType()),
                "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    // Loads a folder
    private File getFolderByFileChooser(Component parent, FolderType folderType) {
        final JFileChooser chooser = new JFileChooser();
        String type = folderType.toString().toLowerCase();
        chooser.setDialogTitle("Choose an " + type + " folder in your project directory");
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileHidingEnabled(false);
        chooser.setCurrentDirectory(new File(FolderHandler.projectPath));
        chooser.setVisible(true);

        final int result = chooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsoluteFile();
        }
        return null;
    }
}