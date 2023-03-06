package gui;

import gui.filehandler.Forest;
import main.Plant;
import utils.MusicPlayer;
import utils.Settings;
import utils.Utils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

public class JOutput extends JPanel {
    private final JOutput[] outputs;
    private final Forest forest;
    private JList<PlantItem> plantsList;

    // Gui components
    private JPanel startPanel, controlPanel, statsPanel;
    private JButton controlButton;
    private JLabel runTimeLabel, bLabel, dLabel, aLabel, plantsNumberLabel, graphSize;
    private JProgressBar progressBar;
    private JGraph graph;
    private JPanel sideBar;
    private Color[] colors;
    private DefaultListModel<PlantItem> plantsItemList, plantsItemListAnimation;
    private final MusicPlayer musicPlayer;
    private final Settings settings;

    // ComboBox
    private final String[] comboBoxData =
            {"10 Seconds", "15 Seconds", "30 Seconds", "60 Seconds", "90 Seconds", "2 Minutes", "5 Minutes", "10 Minutes"};
    private JComboBox<String> timeCombo;

    // JTabbedPane
    private final JTabbedPane tabbedPane;

    // Fonts
    private final Font labelFont = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    private final Font label2Font = new Font(Font.SANS_SERIF, Font.ITALIC, 11);
    private final Font label3Font = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
    private final Font editorFont = new Font("Consolas", Font.BOLD, 12);
    private final Font buttonFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
    private static final int COLOR_ANIMATION_MS = 250;


    private static class PlantItem {
        private Color color;
        private final String value;
        private int number;

        public PlantItem(Color color, String value, int number) {
            this.color = color;
            this.value = value;
            this.number = number;
        }

        @Override
        public String toString() {
            return value;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }

    private class PlantItemRenderer extends JLabel implements ListCellRenderer<PlantItem> {
        private final Color bg;

        public PlantItemRenderer() {
            setFont(editorFont);
            setOpaque(true);
            bg = getBackground();
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends PlantItem> list, PlantItem plantItem, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            if (isSelected) {
                setBackground(list.getSelectionBackground());
            } else {
                setBackground(bg);
            }
            setForeground(plantItem.color);

            setBorder(new EmptyBorder(5, 2, 2, 2));
            String number = String.valueOf(plantItem.getNumber());
            if (number.length() >= 5) {
                number = number.substring(0, number.length() - 3) + "k";
            }
            setText(plantItem.value + " x " + number);
            return this;
        }
    }

    public JOutput(Forest forest, JOutput[] outputs, JTabbedPane tabbedPane, MusicPlayer musicPlayer, Settings settings) {
        this.forest = forest;
        this.outputs = outputs;
        this.tabbedPane = tabbedPane;
        this.musicPlayer = musicPlayer;
        this.settings = settings;
        initPanel();
    }

    // Initializes the panel
    private void initPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(getStats(), BorderLayout.WEST);
        add(getAnimation(), BorderLayout.CENTER);

        // Reacts to tab changes
        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorRemoved(AncestorEvent event) {
                // looks if animating panel is opened?
                for (Component c : sideBar.getComponents()) {
                    if (c == controlPanel) {
                        if (graph.isAnimating()) {
                            toggleAnimation();
                        }
                        break;
                    }
                }
            }

            @Override
            public void ancestorAdded(AncestorEvent event) {
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });
    }

    // Returns the stats panel
    public JPanel getStats() {
        statsPanel = new JPanel(new GridBagLayout());
        statsPanel.setPreferredSize(new Dimension(215, Integer.MAX_VALUE));
        statsPanel.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 10), new CompoundBorder(
                new TitledBorder(new MatteBorder(2, 2, 2, 2, Color.GRAY), "Stats"),
                new EmptyBorder(10, 10, 10, 10))));

        // GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ---------------------------Top part---------------------------

        // dValueLabel
        JLabel dValueLabel = new JLabel("D = " + Utils.formattedDouble(forest.getDiversity(),
                8, true, true, true));
        dValueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dValueLabel.setForeground(Color.WHITE);
        dValueLabel.setFont(labelFont);
        statsPanel.add(dValueLabel, gbc);

        // gbc
        gbc.gridy++;

        // dLabel
        JLabel dLabel = new JLabel("[Diversity]");
        dLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dLabel.setForeground(Color.WHITE);
        dLabel.setFont(label2Font);
        statsPanel.add(dLabel, gbc);

        // gbc
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 0, 0);

        // aValueLabel
        JLabel aValueLabel = new JLabel("A = " + Utils.formattedDouble(forest.getArea(),
                8, true, true, true));
        aValueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        aValueLabel.setForeground(Color.WHITE);
        aValueLabel.setFont(labelFont);
        statsPanel.add(aValueLabel, gbc);

        // gbc
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 10, 0);

        // aLabel
        JLabel aLabel = new JLabel("[Area Covered]");
        aLabel.setHorizontalAlignment(SwingConstants.CENTER);
        aLabel.setForeground(Color.WHITE);
        aLabel.setFont(label2Font);
        statsPanel.add(aLabel, gbc);

        // gbc
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.BOTH;

        // ---------------------------Center part---------------------------

        // plantsList in animation panel
        plantsItemListAnimation = new DefaultListModel<>();
        plantsItemList = new DefaultListModel<>();

        plantsList = new JList<>(plantsItemList);
        fillList(plantsList.getBackground());

        plantsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        plantsList.setCellRenderer(new PlantItemRenderer());
        statsPanel.add(new JScrollPane(plantsList), gbc);

        // gbc
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // forestNameLabel
        JLabel forestNameLabel = new JLabel(forest.getAreaName());
        aLabel.setHorizontalAlignment(SwingConstants.CENTER);
        forestNameLabel.setForeground(Color.WHITE);
        forestNameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        statsPanel.add(forestNameLabel, gbc);

        // gbc
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);

        // plantsNumLabel
        JLabel plantsNumLabel = new JLabel("Plants: " + Utils.formattedInteger(forest.getAreas().size()));
        plantsNumLabel.setForeground(Color.WHITE);
        plantsNumLabel.setFont(label2Font);
        statsPanel.add(plantsNumLabel, gbc);

        // gbc
        gbc.gridy++;

        // plantsTypesLabel
        JLabel plantsTypesLabel = new JLabel("Plant Types: " + Utils.formattedInteger(forest.getPlants().length));
        plantsTypesLabel.setForeground(Color.WHITE);
        plantsTypesLabel.setFont(label2Font);
        statsPanel.add(plantsTypesLabel, gbc);

        // gbc
        gbc.gridy++;

        // areaLabel
        JLabel areaLabel = new JLabel("Area: " + Utils.formattedInteger(forest.getWidth()) + " x " +
                Utils.formattedInteger(forest.getHeight()));
        areaLabel.setForeground(Color.WHITE);
        areaLabel.setFont(label2Font);
        statsPanel.add(areaLabel, gbc);

        return statsPanel;
    }

    public boolean containsStats() {
        for (Component c : getComponents()) {
            if (c.equals(statsPanel)) {
                return true;
            }
        }
        return false;
    }

    public boolean toggleStats() {
        boolean contains = containsStats();
        if (contains) remove(statsPanel);
        else add(statsPanel, BorderLayout.WEST);
        revalidate();
        repaint();

        return !contains;
    }

    private void fillList(Color bgColor) {
        final Plant[] plants = forest.getPlants();
        boolean decimalPoints = false;

        // calculates the max chars needed for each column
        int[] max = new int[2];
        for (Plant plant : plants) {
            String radius;
            if (plant.getRadius() % 1 != 0) {
                radius = Utils.formattedDouble(plant.getRadius(), 2, true, true, false);
                if (!decimalPoints) decimalPoints = true;
            } else radius = Utils.formattedInteger((int) plant.getRadius());

            if (max[0] < radius.length()) {
                max[0] = radius.length();
            }
            String type = plant.getType().length() > 10 ? plant.getType().substring(0, 7) + "..." : plant.getType();
            if (max[1] < type.length()) {
                max[1] = type.length();
            }
        }

        colors = new Color[plants.length];
        int[] plantsNumbers = Utils.getPlantsNumbers(plants, forest.getAreas());
        int range = Math.min(plants.length, 20);
        for (int i = 0; i < plants.length; i++) {
            Plant plant = plants[i];
            colors[i] = Utils.getRandomColor(i, range, bgColor, settings.isRandomColorsEnabled());

            String plantRadius;
            if (decimalPoints) {
                plantRadius = Utils.formattedDouble(plant.getRadius(), 2, true, true, false);
            } else plantRadius = Utils.formattedInteger((int) plant.getRadius());

            String element = " ".repeat(max[0] - plantRadius.length()) + plantRadius + " ";
            String type = plant.getType().length() > 10 ? plant.getType().substring(0, 7) + "..." : plant.getType();
            element += type + " ".repeat(max[1] - type.length());
            plantsItemList.addElement(new PlantItem(colors[i], element, plantsNumbers[i]));
            plantsItemListAnimation.addElement(new PlantItem(colors[i], element, 0));
        }
    }

    public void refreshColors() {
        final Color bgColor = plantsList.getBackground();
        final int range = plantsItemListAnimation.size();
        for (int i = 0; i < range; i++) {
            colors[i] = Utils.getRandomColor(i, range, bgColor, settings.isRandomColorsEnabled());
            plantsItemList.getElementAt(i).color = colors[i];
            plantsItemListAnimation.getElementAt(i).color = colors[i];
        }
        revalidate();
        repaint();
    }

    private Timer timer;
    private boolean colorAnimating;

    public void toggleColorAnimating() {
        colorAnimating = !colorAnimating;
    }

    public void removeColorAnimation() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void runColorAnimation() {
        if (colorAnimating && timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    synchronized (this) {
                        SwingUtilities.invokeLater(() -> refreshColors());
                    }
                }
            }, 0, COLOR_ANIMATION_MS);
        }
    }

    public boolean isColorAnimating() {
        return colorAnimating;
    }

    public JPanel getAnimation() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Title
        String title = "B = " + Utils.formattedDouble(forest.getB(), 8, true, true, true)
                + " (~" + Utils.formattedInteger(forest.getScore()) + " Score)";
        JLabel titleLabel = new JLabel(title);
        titleLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // ------------------------Animation panel------------------------
        JPanel animationPanel = new JPanel(new BorderLayout());
        animationPanel.setBorder(new CompoundBorder(new TitledBorder(
                new MatteBorder(2, 2, 2, 2, Color.GRAY), "Animation"),
                new EmptyBorder(0, 10, 10, 10)));

        // Graph size
        graphSize = new JLabel();
        setGraphSize(0, 0, 0);
        graphSize.setBorder(new EmptyBorder(0, 0, 5, 0));
        graphSize.setFont(label3Font);
        graphSize.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (graph.isZooming()) setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (graph.isZooming() && graphSize.contains(e.getPoint())) {
                    graph.resetZoom();
                }
            }
        });
        animationPanel.add(graphSize, BorderLayout.NORTH);

        JScrollPane sp = new JScrollPane();
        graph = new JGraph(forest.getWidth(), forest.getHeight(), forest.getPlants(), forest.getAreas(), colors, this, sp);
        sp.setViewportView(graph);
        graph.addMouseWheelListener(e -> {
            if (e.isControlDown()) {
                int rotation = e.getWheelRotation();
                if (rotation == 1 || rotation == -1) {
                    graph.zoom(rotation < 0);
                }
            } else {
                JScrollBar vBar = sp.getVerticalScrollBar();
                JScrollBar hBar = sp.getHorizontalScrollBar();

                if (vBar.isShowing() || hBar.isShowing()) {
                    JScrollBar bar;
                    if (!vBar.isShowing()) bar = hBar;
                    else if (!hBar.isShowing()) bar = vBar;
                    else bar = e.isShiftDown() ? sp.getHorizontalScrollBar() : sp.getVerticalScrollBar();

                    if (bar != null) {
                        if (e.getWheelRotation() > 0) {
                            bar.setValue(bar.getValue() + bar.getUnitIncrement());
                        } else bar.setValue(bar.getValue() - bar.getUnitIncrement());
                    }
                }
            }
        });
        sp.getHorizontalScrollBar().setUnitIncrement(25);
        sp.getVerticalScrollBar().setUnitIncrement(25);
        sp.setBorder(null);
        animationPanel.add(sp, BorderLayout.CENTER);

        initSideBar();
        animationPanel.add(sideBar, BorderLayout.EAST);
        mainPanel.add(animationPanel, BorderLayout.CENTER);
        return mainPanel;
    }

    private void initSideBar() {
        sideBar = new JPanel(new BorderLayout());
        sideBar.setBorder(new CompoundBorder(
                new CompoundBorder(new EmptyBorder(0, 10, 0, 0),
                        new MatteBorder(0, 2, 0, 0, Color.WHITE)),
                new EmptyBorder(0, 10, 0, 0)));

        initStartPanel();
        initControlPanel();
        sideBar.add(startPanel, BorderLayout.CENTER);
    }

    public void setGraphSize(int width, int height, double zoom) {
        if (graphSize != null) graphSize.setText(width + " x " + height + " (" + (int) (zoom * 100) + "%)");
    }

    private void initStartPanel() {
        // start panel
        startPanel = new JPanel(new GridBagLayout());
        startPanel.setPreferredSize(new Dimension(180, Integer.MAX_VALUE));

        // GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // timeCombo
        timeCombo = new JComboBox<>(comboBoxData);
        timeCombo.setForeground(Color.WHITE);
        timeCombo.addItemListener(e -> {
            if (tabbedPane.getSelectedComponent() == this && e.getStateChange() == ItemEvent.SELECTED) {
                int index = timeCombo.getSelectedIndex();
                for (JOutput o : outputs) {
                    if (o != JOutput.this) {
                        o.setComboBoxIndex(index);
                    }
                }
            }
        });
        timeCombo.setFont(buttonFont);

        // startButton
        JButton startButton = new JButton("Play");
        startButton.setForeground(Color.WHITE);
        startButton.setFont(buttonFont);
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (startButton.contains(e.getPoint())) activateAnimation();
            }
        });
        startPanel.add(startButton, gbc);

        // gbc
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 0, 0);

        // timeLabel
        JLabel timeLabel = new JLabel("Time:");
        timeLabel.setHorizontalAlignment(JLabel.CENTER);
        timeLabel.setFont(label2Font);
        timeLabel.setForeground(Color.WHITE);
        startPanel.add(timeLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.NORTH;

        startPanel.add(timeCombo, gbc);
    }


    // Panel while animation is running
    private void initControlPanel() {
        // start panel
        controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setPreferredSize(new Dimension(180, Integer.MAX_VALUE));

        // GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // controlButton
        controlButton = new JButton("Stop");
        controlButton.setForeground(Color.WHITE);
        controlButton.setFont(buttonFont);
        controlButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (controlButton.contains(e.getPoint())) {
                    if (controlButton.getText().equals("Restart")) {
                        if (settings.isSoundEnabled()) {
                            musicPlayer.setTime(0);
                            musicPlayer.play();
                        }
                        runColorAnimation();
                        controlButton.setText("Stop");
                        setB(0);
                        setD(1);
                        setA(0);
                        setPlants(0);
                        graph.restart();
                    } else {
                        toggleAnimation();
                    }
                }
            }
        });
        controlPanel.add(controlButton, gbc);

        // gbc
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);

        // runTimeLabel
        runTimeLabel = new JLabel();
        runTimeLabel.setForeground(Color.WHITE);
        runTimeLabel.setFont(labelFont);
        runTimeLabel.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 2, 0, Color.GRAY),
                new EmptyBorder(10, 0, 10, 0)
        ));
        setRunTime(0);
        controlPanel.add(runTimeLabel, gbc);

        // gbc
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 0, 0);

        // bLabel
        bLabel = new JLabel();
        bLabel.setFont(labelFont);
        bLabel.setForeground(Color.WHITE);
        setB(0);
        controlPanel.add(bLabel, gbc);

        // gbc
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);

        // dLabel
        dLabel = new JLabel();
        dLabel.setFont(label3Font);
        dLabel.setForeground(Color.WHITE);
        setD(1);
        controlPanel.add(dLabel, gbc);

        // gbc
        gbc.gridy++;

        // aLabel
        aLabel = new JLabel();
        aLabel.setFont(label3Font);
        aLabel.setForeground(Color.WHITE);
        setA(0);
        controlPanel.add(aLabel, gbc);

        // gbc
        gbc.gridy++;
        gbc.weighty = 0.5;
        gbc.insets = new Insets(10, 0, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTH;

        // list
        JList<PlantItem> list = new JList<>(plantsItemListAnimation);
        list.setCellRenderer(new PlantItemRenderer());
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        controlPanel.add(new JScrollPane(list), gbc);

        // gbc
        gbc.gridy++;
        gbc.weighty = 0;
        gbc.insets = new Insets(10, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.SOUTH;

        // plantsNumberLabel
        plantsNumberLabel = new JLabel();
        plantsNumberLabel.setFont(labelFont);
        plantsNumberLabel.setBorder(new CompoundBorder(new MatteBorder(2, 0, 0, 0, Color.GRAY),
                new EmptyBorder(10, 0, 0, 0)));
        controlPanel.add(plantsNumberLabel, gbc);

        // gbc
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 15, 0);

        // plantsNumberLabel
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        controlPanel.add(progressBar, gbc);
        setPlants(0);

        // gbc
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);

        // cancelButton
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(buttonFont);
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (cancelButton.contains(e.getPoint())) deactivateAnimation();
            }
        });
        controlPanel.add(cancelButton, gbc);
    }

    public void setComboBoxIndex(int index) {
        timeCombo.setSelectedIndex(index);
    }

    public void activateAnimation() {
        Object comboBoxItem = timeCombo.getSelectedItem();
        if (comboBoxItem == null) {
            throw new IllegalArgumentException("Please select a time interval");
        }
        String[] values = comboBoxItem.toString().split(" ");
        long millis;
        if (values[1].equals("Seconds")) {
            millis = Long.parseLong(values[0]) * 1000;
        } else millis = Long.parseLong(values[0]) * 1000 * 60;
        setRunTime(millis);

        if (settings.isSoundEnabled()) {
            musicPlayer.setTime(0);
            musicPlayer.play();
        }
        runColorAnimation();
        graph.startAnimation(millis);
        sideBar.add(controlPanel);
        sideBar.remove(startPanel);
        sideBar.repaint();
    }

    public void deactivateAnimation() {
        musicPlayer.stop();
        removeColorAnimation();
        graph.cancelAnimation();
        controlButton.setText("Stop");
        sideBar.add(startPanel);
        sideBar.remove(controlPanel);
        sideBar.repaint();
    }

    private void toggleAnimation() {
        if (controlButton.getText().equals("Stop")) {
            controlButton.setText("Continue");
            musicPlayer.stop();
            removeColorAnimation();
        } else {
            controlButton.setText("Stop");
            if (settings.isSoundEnabled()) {
                musicPlayer.setTime(graph.getRunningMillis());
                musicPlayer.play();
            }
            runColorAnimation();
        }
        graph.toggleAnimation();
    }

    public void finishAnimation() {
        controlButton.setText("Restart");
        musicPlayer.stop();
        removeColorAnimation();
    }

    public void setRunTime(long millis) {
        long minutes = millis / 1000 / 60;
        long seconds = millis / 1000 % 60;
        String time;
        if (minutes > 10) {
            time = minutes + ":" + seconds;
        } else if (minutes > 0) {
            time = "0" + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
        } else {
            time = seconds + "." + (millis % 1000 / 100);
        }
        runTimeLabel.setText("Time: " + time);
    }

    public void addNumber(Plant plant) {
        PlantItem item = plantsItemListAnimation.getElementAt(plant.getIndex());
        item.setNumber(item.getNumber() + 1);
        sideBar.revalidate();
        sideBar.repaint();
    }

    public void setB(double b) {
        bLabel.setText("B = " + Utils.formattedDouble(b, 8, true, true, false));
    }

    public void setD(double d) {
        dLabel.setText("D = " + Utils.formattedDouble(d, 8, true, true, false));
    }

    public void setA(double a) {
        aLabel.setText("A = " + Utils.formattedDouble(a, 8, true, true, false));
    }

    public void setPlants(int plantsNumber) {
        plantsNumberLabel.setText("Plants: " + plantsNumber + "/" + forest.getAreas().size());
        if (forest.getAreas().size() > 0) {
            int progress = plantsNumber * 100 / forest.getAreas().size();
            progressBar.setValue(progress);
        }
    }

    public void clearElements() {
        for (int i = 0; i < plantsItemListAnimation.size(); i++) {
            plantsItemListAnimation.getElementAt(i).setNumber(0);
        }
    }

    public void initGraphSize() {
        if (graph != null) {
            graph.initZoom();
        }
    }
}