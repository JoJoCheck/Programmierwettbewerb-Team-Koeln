package gui;

import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkIJTheme;
import gui.filehandler.FolderHandler;
import gui.filehandler.FolderInfo;
import gui.filehandler.FolderMessage;
import gui.filehandler.Forest;
import utils.MusicPlayer;
import utils.Settings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class Gui {
    private JFrame frame;
    private JTabbedPane tabbedPane;
    private MusicPlayer musicPlayer;
    private JDialogs dialog;
    private final ImageIcon showIcon;
    private final ImageIcon closeIcon;
    private Settings settings;
    private boolean isFullScreen;
    private int oldState;
    private int oldWidth, oldHeight, oldX, oldY;

    // constructor
    public Gui() throws IOException {
        showIcon = getScaledIcon("images/gui/menubar/show.png");
        closeIcon = getScaledIcon("images/gui/menubar/close.png");
        initGui();
    }

    private ImageIcon getScaledIcon(String path) {
        Image image = new ImageIcon(path).getImage().getScaledInstance(16, 16,
                java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    // Initializes the gui
    private void initGui() throws IOException {
        try {
            UIManager.setLookAndFeel(new FlatAtomOneDarkIJTheme());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        UIManager.put("TitlePane.menuBarTitleGap", 100);
        UIManager.put("TitlePane.centerTitleIfMenuBarEmbedded", true);

        // Initializes the icon
        ImageIcon guiIcon = new ImageIcon("images/gui/icon.png");

        // create the frame
        frame = new JFrame("Optimierungsproblem");
        frame.setIconImage(guiIcon.getImage());
        frame.setMinimumSize(new Dimension(700, 500));
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        oldX = frame.getX();
        oldY = frame.getY();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    settings.saveSettings();
                } catch (IOException e2) {
                    throw new RuntimeException("Could not save the settings.");
                }
                super.windowClosing(e);
            }
        });
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (frame.getExtendedState() == JFrame.NORMAL) {
                    if (oldState != JFrame.NORMAL) {
                        oldState = frame.getExtendedState();
                        frame.setSize(oldWidth, oldHeight);
                        frame.setLocation(oldX, oldY);
                    } else {
                        oldWidth = frame.getWidth();
                        oldHeight = frame.getHeight();
                        oldX = frame.getX();
                        oldY = frame.getY();
                    }
                }
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                if (frame.getExtendedState() == JFrame.NORMAL) {
                    if (oldState != JFrame.NORMAL) {
                        oldState = frame.getExtendedState();
                    } else {
                        oldX = frame.getX();
                        oldY = frame.getY();
                    }
                }
            }
        });
        frame.addWindowStateListener(e -> oldState = e.getOldState());
        isFullScreen = false;

        // Build menubar
        JMenuBar bar = new JMenuBar();

        // fileMenu
        JMenu fileMenu = new JMenu("File");
        fileMenu.getPopupMenu().setBorder(new MatteBorder(1, 1, 1, 1, Color.GRAY));
        fileMenu.setMnemonic(KeyEvent.VK_F);

        // openItem
        JMenuItem dirItem = new JMenuItem("Directories...") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                super.paintComponent(g2);
            }
        };
        dirItem.setBorder(new EmptyBorder(5, 5, 5, 5));
        dirItem.addActionListener(e -> {
            boolean accept = dialog.createDirectoriesDialog("Change Directories", false);
            if (accept) {
                new Thread(() -> {
                    Forest[] forests = dialog.getOutputDialog();
                    SwingUtilities.invokeLater(() -> {
                        tabbedPane.removeAll();
                        initTabbedPane(forests);
                    });
                }).start();
            }
        });
        dirItem.setIcon(getScaledIcon("images/gui/menubar/open.png"));
        dirItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        fileMenu.add(dirItem);

        // settingsItem
        JMenuItem settingsItem = new JMenuItem("Settings") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                super.paintComponent(g2);
            }
        };
        settingsItem.setBorder(new EmptyBorder(5, 5, 5, 5));
        settingsItem.setIcon(getScaledIcon("images/gui/menubar/settings.png"));
        settingsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        settingsItem.addActionListener(e -> dialog.createSettingsDialog());
        fileMenu.add(settingsItem);
        fileMenu.add(new JPopupMenu.Separator());

        // refreshItem
        JMenuItem refreshFilesItem = new JMenuItem("Refresh Files") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                super.paintComponent(g2);
            }
        };
        refreshFilesItem.setBorder(new EmptyBorder(5, 5, 5, 5));
        refreshFilesItem.addActionListener(e -> {
            if (dialog.check(frame)) {
                new Thread(() -> {
                    Forest[] forests = dialog.getOutputDialog();
                    SwingUtilities.invokeLater(() -> {
                        tabbedPane.removeAll();
                        initTabbedPane(forests);
                    });
                }).start();
            }
        });
        refreshFilesItem.setIcon(getScaledIcon("images/gui/menubar/refresh.png"));
        refreshFilesItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        fileMenu.add(refreshFilesItem);

        // windowMenu
        JMenu windowMenu = new JMenu("Window");
        windowMenu.getPopupMenu().setBorder(new MatteBorder(1, 1, 1, 1, Color.GRAY));
        windowMenu.setMnemonic(KeyEvent.VK_W);

        // toggleItem
        JMenuItem toggleItem = new JMenuItem("Hide Stats") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                super.paintComponent(g2);
            }
        };
        toggleItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK));
        toggleItem.setBorder(new EmptyBorder(5, 5, 5, 5));
        toggleItem.addActionListener(e -> {
            Component c = tabbedPane.getSelectedComponent();
            if (c instanceof JOutput) {
                for (Component temp : tabbedPane.getComponents()) {
                    if (temp instanceof JOutput tempOutput) {
                        boolean contains = tempOutput.toggleStats();
                        toggleItem.setText((contains ? "Hide" : "Show") + " Stats");
                        toggleItem.setIcon(contains ? closeIcon : showIcon);
                    }
                }
            } else if (c instanceof JResult) {
                for (Component temp : tabbedPane.getComponents()) {
                    if (temp instanceof JResult tempResult) {
                        boolean contains = tempResult.toggleScoreCalculation();
                        toggleItem.setText((contains ? "Hide" : "Show") + " Score Calculation");
                        toggleItem.setIcon(contains ? closeIcon : showIcon);
                    }
                }
            }
        });
        toggleItem.setIcon(closeIcon);
        windowMenu.add(toggleItem);
        windowMenu.add(new JPopupMenu.Separator());

        // toggleFullScreenItem
        JMenuItem toggleFullScreenItem = new JMenuItem("Fullscreen");
        toggleFullScreenItem.setBorder(new EmptyBorder(5, 5, 5, 5));
        toggleFullScreenItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
        toggleFullScreenItem.addActionListener(e -> {
            toggleFullScreen();
            toggleFullScreenItem.setText(isFullScreen ? "Exit Fullscreen" : "Fullscreen");
        });
        windowMenu.add(toggleFullScreenItem);

        // extrasMenu
        JMenu extrasMenu = new JMenu("Extra");
        extrasMenu.getPopupMenu().setBorder(new MatteBorder(1, 1, 1, 1, Color.GRAY));
        extrasMenu.setMnemonic(KeyEvent.VK_E);

        // refreshColorsItem
        JMenuItem refreshColorsItem = new JMenuItem("Refresh Colors") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                super.paintComponent(g2);
            }
        };
        refreshColorsItem.setBorder(new EmptyBorder(5, 5, 5, 5));
        refreshColorsItem.addActionListener(e -> refreshTabCircleColors());
        refreshColorsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        refreshColorsItem.setIcon(getScaledIcon("images/gui/menubar/refresh.png"));
        extrasMenu.add(refreshColorsItem);
        extrasMenu.add(new JPopupMenu.Separator());

        // blinkItem
        JMenuItem blinkItem = new JMenuItem("Color Animation");
        blinkItem.setBorder(new EmptyBorder(5, 5, 5, 5));
        blinkItem.addActionListener(e -> {
            for (Component c : tabbedPane.getComponents()) {
                if (c instanceof JOutput output) {
                    output.toggleColorAnimating();
                }
            }
            Component c = tabbedPane.getSelectedComponent();
            if (c instanceof JOutput output) {
                blinkItem.setText((output.isColorAnimating() ? "Remove " : "") + "Color Animation");
            }
        });
        blinkItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        extrasMenu.add(blinkItem);

        // add menubar
        bar.add(fileMenu);
        bar.add(windowMenu);
        bar.add(extrasMenu);
        frame.setJMenuBar(bar);

        // init tabbed pane
        tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.addChangeListener(e -> {
            Component c = tabbedPane.getSelectedComponent();
            if (c instanceof JOutput output) {
                boolean contains = output.containsStats();
                toggleItem.setText((contains ? "Hide" : "Show") + " Stats");
                toggleItem.setIcon(contains ? closeIcon : showIcon);
                extrasMenu.setEnabled(true);
                blinkItem.setText((output.isColorAnimating() ? "Remove " : "") + "Color Animation");
            } else if (c instanceof JResult result) {
                boolean contains = result.containsScoreCalculation();
                toggleItem.setText((contains ? "Hide" : "Show") + " Score Calculation");
                toggleItem.setIcon(contains ? closeIcon : showIcon);
                extrasMenu.setEnabled(false);
            }
        });
        frame.add(tabbedPane, BorderLayout.CENTER);

        // read settings
        settings = new Settings();
        settings.readSettings();
        FolderInfo folderInfo = FolderHandler.check(settings.getInputFolder(), settings.getOutputFolder());

        // Initializes the musicPlayer
        musicPlayer = new MusicPlayer();
        musicPlayer.setVolume(settings.getVolume());

        // guiDialog
        dialog = new JDialogs(settings, this, frame, musicPlayer);
        // create file paths dialog
        if (settings.isStartPopupEnabled()) {
            dialog.createDirectoriesDialog("Choose Directories", true);
        } else if (folderInfo.folderMessage() != FolderMessage.VALID_FOLDER) {
            dialog.createDirectoriesDialog("Error! Choose Directories", true);
        }

        // Creates a load bar
        Forest[] forests = dialog.getOutputDialog();
        initTabbedPane(forests);

        // Makes the frame visible
        frame.setVisible(true);

        // init graphs
        for (int i = 0; i < forests.length; i++) {
            for (Component c : tabbedPane.getComponents()) {
                if (c instanceof JOutput output) {
                    output.initGraphSize();
                }
            }
        }
    }

    public void refreshTabCircleColors() {
        Component c = tabbedPane.getSelectedComponent();
        if (c instanceof JOutput) {
            ((JOutput) c).refreshColors();
        }
    }

    public void refreshCircleColors() {
        for (Component c : tabbedPane.getComponents()) {
            if (c instanceof JOutput) {
                ((JOutput) c).refreshColors();
            }
        }
    }

    private void initTabbedPane(Forest[] forests) {
        JOutput[] outputs = new JOutput[forests.length];

        // Panels will be added as tabs
        for (int i = 0; i < forests.length; i++) {
            JOutput output = new JOutput(forests[i], outputs, tabbedPane, musicPlayer, settings);
            outputs[i] = output;
            output.initGraphSize();
            tabbedPane.addTab(forests[i].getFileName(), output);
        }

        // add result panel to the frame
        JResult result = new JResult(forests, tabbedPane);
        tabbedPane.addTab("Result", result);
        tabbedPane.repaint();
    }

    public void closeApplication(Component parent) {
        int option = JOptionPane.showConfirmDialog(parent,
                "Do you want to close this app?", "Closing Message", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try {
                settings.saveSettings();
            } catch (IOException e) {
                throw new RuntimeException("Could not save the settings.");
            }
            System.exit(0);
        }
    }

    private void toggleFullScreen() {
        if (!isFullScreen) {
            frame.dispose();
            frame.setUndecorated(true);
            oldState = frame.getExtendedState();
            if (frame.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
            frame.setVisible(true);
        } else {
            frame.dispose();
            frame.setUndecorated(false);
            if (oldState == JFrame.NORMAL) {
                frame.setExtendedState(JFrame.NORMAL);
                frame.setSize(oldWidth, oldHeight);
                frame.setLocation(oldX, oldY);
            } else {
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
            frame.setVisible(true);
        }
        isFullScreen = !isFullScreen;
    }

    // starts the main activity
    public static void main(String[] args) throws IOException {
        new Gui();
    }
}