package gui;

import gui.filehandler.Forest;
import utils.Utils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

public class JResult extends JPanel {
    private final Forest[] forests;
    private final JTabbedPane tabbedPane;
    private JComboBox<?> sortByComboBox;
    private JPanel scoresTablePanel;
    private final Font labelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
    private final Font comboBoxFont = new Font(Font.SANS_SERIF, Font.BOLD, 11);
    private boolean showTableWithPercentage;
    private Color contrastColor;

    private enum SortBy {
        SCORE, DIVERSITY, AREA;

        // Get a SortBy element by its name
        public static SortBy get(String value) {
            return valueOf(value.toUpperCase());
        }
    }

    private static class HeaderRenderer extends JLabel implements TableCellRenderer {
        public HeaderRenderer() {
            setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            setForeground(Color.WHITE);
            setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (column > 0) {
                setBorder(new CompoundBorder(new MatteBorder(0, 2, 0, 0, Color.WHITE),
                        new EmptyBorder(5, 5, 5, 5)));
            } else {
                setBorder(new EmptyBorder(5, 5, 5, 5));
            }
            setText(value.toString());
            return this;
        }

    }

    public JResult(Forest[] forests, JTabbedPane tabbedPane) {
        contrastColor = UIManager.getColor("Actions.Blue");
        if (contrastColor == null) contrastColor = Color.CYAN;

        this.forests = forests;
        this.tabbedPane = tabbedPane;
        initPanel();
    }

    // Initializes the panel
    private void initPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.add(getResultTable(), BorderLayout.NORTH);
        resultsPanel.add(getScoreRanking(), BorderLayout.CENTER);
        add(resultsPanel, BorderLayout.CENTER);

        add(getScoresPanel(), BorderLayout.WEST);
    }

    // Returns a panel with a table of the scores
    private JPanel getScoresPanel() {
        scoresTablePanel = new JPanel(new BorderLayout());
        scoresTablePanel.setBorder(new CompoundBorder(
                new EmptyBorder(0, 0, 0, 10),
                new CompoundBorder(new TitledBorder(new MatteBorder(2, 2, 2, 2, Color.GRAY),
                        "Score Calculation"), new EmptyBorder(10, 10, 10, 10))));

        // score values
        double[][] scores = Utils.getScores();
        String[][] tableValues = new String[scores.length][];
        for (int i = 0; i < scores.length; i++) {
            String[] values = new String[scores[i].length];
            values[0] = Utils.formattedDouble(scores[i][0], 2, true, false, true);
            values[1] = String.valueOf((int) scores[i][1]);
            tableValues[i] = values;
        }

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(new EmptyBorder(0, 0, 0, 5));
        // table
        String[] tableHeader = {"B-Value", "Score"};
        JTable table = new JTable(tableValues, tableHeader) {
            private final Color borderColor = Color.WHITE;

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }

            @Override
            public JComponent prepareRenderer(TableCellRenderer renderer, int row, int column) {
                JComponent c = (JComponent) super.prepareRenderer(renderer, row, column);
                int border = 1;
                Insets insets = new Insets(0, border, border, 0);
                if (column == getColumnCount() - 1) insets.right = border;
                c.setBorder(new MatteBorder(insets, borderColor));
                return c;
            }
        };
        table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

        DefaultTableCellRenderer tableRenderer = new DefaultTableCellRenderer();
        tableRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(tableRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(tableRenderer);
        table.getTableHeader().setDefaultRenderer(new HeaderRenderer());

        // add table to panel
        table.getTableHeader().setBorder(new MatteBorder(2, 2, 2, 2, Color.WHITE));
        tablePanel.add(table.getTableHeader(), BorderLayout.NORTH);
        tablePanel.add(table, BorderLayout.CENTER);

        JScrollPane sp = new JScrollPane(tablePanel);
        sp.getVerticalScrollBar().setUnitIncrement(10);
        sp.setBorder(new EmptyBorder(0, 0, 0, 0));
        sp.setPreferredSize(new Dimension(180, Integer.MAX_VALUE));
        scoresTablePanel.add(sp, BorderLayout.CENTER);
        return scoresTablePanel;
    }

    public boolean containsScoreCalculation() {
        for (Component c : getComponents()) {
            if (c.equals(scoresTablePanel)) {
                return true;
            }
        }
        return false;
    }

    public boolean toggleScoreCalculation() {
        boolean contains = containsScoreCalculation();
        if (contains) remove(scoresTablePanel);
        else add(scoresTablePanel, BorderLayout.WEST);
        revalidate();
        repaint();

        return !contains;
    }

    // Returns a panel with a combo box for sorting the tables
    private Component[] getSortByPanel() {
        // sortTableByPanel
        JPanel sortByPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));

        // sortByLabel
        JLabel sortByLabel = new JLabel("Sort by:");
        sortByLabel.setFont(labelFont);
        sortByLabel.setBorder(new EmptyBorder(0, 0, 0, 5));
        sortByPanel.add(sortByLabel);

        // values
        String[] values = new String[SortBy.values().length];
        for (int i = 0; i < values.length; i++) {
            values[i] = Utils.capitalize(String.valueOf(SortBy.values()[i]));
        }

        // sortByComboBox
        JComboBox<String> sortByComboBox = new JComboBox<>(values);
        sortByComboBox.setFont(comboBoxFont);
        sortByComboBox.setSelectedItem(String.valueOf(SortBy.SCORE));
        sortByPanel.add(sortByComboBox);

        // return panel and combo box
        return new Component[]{sortByPanel, sortByComboBox};
    }

    // Returns the table with the results
    private JPanel getResultTable() {
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(new CompoundBorder(new TitledBorder(
                new MatteBorder(2, 2, 2, 2, Color.WHITE), "Result Table"),
                new EmptyBorder(0, 10, 10, 10)));

        // resultControlPanel
        JPanel resultControlPanel = new JPanel(new BorderLayout());
        resultControlPanel.setBorder(new CompoundBorder(
                new EmptyBorder(0, 0, 5, 0),
                new CompoundBorder(new MatteBorder(0, 0, 2, 0, Color.WHITE),
                        new EmptyBorder(0, 0, 5, 0))));

        // showPercentageCheckBox
        JCheckBox showPercentageCheckBox = new JCheckBox("Show percentage");
        showPercentageCheckBox.setSelected(showTableWithPercentage);
        resultControlPanel.add(showPercentageCheckBox, BorderLayout.CENTER);

        // add sortByPanel
        Component[] components = getSortByPanel();
        JPanel sortByPanel = (JPanel) components[0];
        sortByComboBox = (JComboBox<?>) components[1];
        resultControlPanel.add(sortByPanel, BorderLayout.EAST);

        // add to resultPanel
        resultPanel.add(resultControlPanel, BorderLayout.NORTH);

        // table
        JPanel tablePanel = new JPanel(new BorderLayout());
        updateTable(SortBy.SCORE, tablePanel, resultPanel);

        // update table
        sortByComboBox.addActionListener(e -> {
            String selectedItem = (String) sortByComboBox.getSelectedItem();
            if (selectedItem != null) {
                SortBy sortBy = SortBy.get(selectedItem);
                updateTable(sortBy, tablePanel, resultPanel);
            }
        });
        showPercentageCheckBox.addActionListener(e -> {
            String selectedItem = (String) sortByComboBox.getSelectedItem();
            if (selectedItem != null) {
                showTableWithPercentage = !showTableWithPercentage;
                SortBy sortBy = SortBy.get(selectedItem);
                updateTable(sortBy, tablePanel, resultPanel);
            }
        });
        return resultPanel;
    }

    // updates the table with the results
    private void updateTable(SortBy sortBy, JPanel tablePanel, JPanel resultPanel) {
        // remove all
        tablePanel.removeAll();
        resultPanel.remove(tablePanel);

        // score table
        String[][] tableValues = {getAverageTotal(false, sortBy), getAverageTotal(true, sortBy),
                getMaxMin(true, sortBy), getMaxMin(false, sortBy)};
        String[] tableHeader;
        if (sortBy.equals(SortBy.SCORE)) {
            tableHeader = new String[]{"TYPE", "SCORE", "B [VALUE]", "FOREST"};
        } else if (sortBy.equals(SortBy.DIVERSITY)) {
            tableHeader = new String[]{"TYPE", "D [VALUE]", "FOREST"};
        } else tableHeader = new String[]{"TYPE", "A [VALUE]", "FOREST"};

        // table
        JTable table = new JTable(tableValues, tableHeader) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        table.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        table.setForeground(Color.WHITE);

        // add table to panel
        tablePanel.add(table.getTableHeader(), BorderLayout.NORTH);
        tablePanel.add(table, BorderLayout.CENTER);
        resultPanel.add(tablePanel, BorderLayout.CENTER);

        // repaint
        table.revalidate();
        table.repaint();
    }

    // Returns the score ranking
    private JPanel getScoreRanking() {
        // rankingPanel
        JPanel rankingPanel = new JPanel(new BorderLayout());
        rankingPanel.setBorder(new CompoundBorder(
                new EmptyBorder(10, 0, 0, 0),
                new CompoundBorder(new TitledBorder(
                        new MatteBorder(2, 2, 2, 2, Color.WHITE), "Score Ranking"),
                        new EmptyBorder(5, 10, 10, 10))));

        // listPane
        JPanel listPane = new JPanel();
        listPane.setLayout(new GridBagLayout());

        // scrollPane
        JScrollPane sp = new JScrollPane(listPane);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sp.setBorder(new EmptyBorder(0, 0, 0, 0));
        sp.getVerticalScrollBar().setUnitIncrement(10);
        rankingPanel.add(sp, BorderLayout.CENTER);

        // update ranking
        updateRanking(listPane, SortBy.SCORE, sp);
        sortByComboBox.addActionListener(e -> {
            String selectedItem = (String) sortByComboBox.getSelectedItem();
            if (selectedItem != null) {
                SortBy sortBy = SortBy.get(selectedItem);
                updateRanking(listPane, sortBy, sp);
            }
        });

        return rankingPanel;
    }

    private void updateRanking(JPanel listPane, SortBy sortBy, JScrollPane sp) {
        listPane.removeAll();

        // make ranking
        Forest[] sortedForests = sorted(sortBy);
        final int lenForests = sortedForests.length;

        // GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Calculates chars max length of each column
        final int[] maxLen = {0, 0};
        for (int i = lenForests - 1; i >= 0; i--) {
            String name = sortedForests[i].getFileName();
            String score = Utils.formattedInteger(sortedForests[i].getScore());
            if (maxLen[0] < name.length()) {
                maxLen[0] = name.length();
            }
            if (maxLen[1] < score.length()) {
                maxLen[1] = score.length();
            }
        }

        for (int i = lenForests - 1; i >= 0; i--) {
            Forest forest = sortedForests[i];

            // index.
            String index = String.valueOf(lenForests - i);
            if (!sortBy.equals(SortBy.SCORE)) {
                Forest[] forestSortedByScore = sorted(SortBy.SCORE);
                for (int j = lenForests - 1; j >= 0; j--) {
                    if (forestSortedByScore[j].getFileName().equals(forest.getFileName())) {
                        index = String.valueOf(lenForests - j);
                        break;
                    }
                }
            }
            index = " ".repeat(String.valueOf(lenForests).length() - index.length()) + index + ".";

            // name
            String name = forest.getFileName();
            name += " ".repeat(maxLen[0] - name.length());

            // score
            String score = Utils.formattedInteger(forest.getScore());
            score = " ".repeat(maxLen[1] - score.length()) + "(~" + score + " Score)";

            // other values
            String b = Utils.formattedDouble(forest.getB(), 5, true, true, true);
            String d = Utils.formattedDouble(forest.getDiversity(), 5, true, true, true);
            String a = Utils.formattedDouble(forest.getArea(), 5, true, true, true);

            JLabel scoreLabel = getRankingRow(index, name, score, b, d, a, sortBy);
            listPane.add(scoreLabel, gbc);
            gbc.gridy++;
        }

        listPane.revalidate();
        listPane.repaint();
        sp.getVerticalScrollBar().setValue(0);
    }

    // Return a list item
    private JLabel getRankingRow(String index, String name, String score, String b, String d, String a, SortBy
            sortBy) {
        String sep = "<font color='" + Utils.colorToHex(contrastColor) + "'> | </font>";
        String sb = "<html><pre><font face='Consolas' size='3.5'>" + index + " " + name + sep;
        if (sortBy.equals(SortBy.SCORE)) {
            sb += "<font color='white'>B=" + b + " " + score + "</font>" + sep + "D=" + d + sep + "A=" + a + "</font></pre></html>";
        } else if (sortBy.equals(SortBy.DIVERSITY)) {
            sb += "<font color='white'>D=" + d + "</font>" + sep + "B=" + b + " " + score + sep + "A=" + a + "</font></pre></html>";
        } else {
            sb += "<font color='white'>A=" + a + "</font>" + sep + "B=" + b + " " + score + sep + "D=" + d + "</font></pre></html>";
        }

        JLabel scoreLabel = new JLabel(sb);
        scoreLabel.setBorder(new EmptyBorder(2, 5, 2, 5));
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        scoreLabel.setFont(new Font("Consolas", Font.BOLD, 14));
        scoreLabel.setOpaque(true);
        scoreLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                scoreLabel.setForeground((Color) UIManager.get("List.selectionForeground"));
                scoreLabel.setBackground((Color) UIManager.get("List.selectionBackground"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                scoreLabel.setForeground((Color) UIManager.get("List.foreground"));
                scoreLabel.setBackground((Color) UIManager.get("List.background"));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (scoreLabel.contains(e.getPoint())) setTabTo(name);
                }
            }
        });
        return scoreLabel;
    }

    // Sets the tabbed pane to the tab with the specified name
    private void setTabTo(String name) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equals(name)) {
                tabbedPane.setSelectedIndex(i);
                return;
            }
        }
        throw new IllegalArgumentException("Tab not found");
    }

    // Returns a copied sorted forests by score/diversity/area
    private Forest[] sorted(SortBy sortBy) {
        Forest[] sorted = new Forest[forests.length];
        System.arraycopy(forests, 0, sorted, 0, forests.length);

        if (sortBy.equals(SortBy.SCORE)) {
            Arrays.sort(sorted, Comparator.comparing(Forest::getB));
        } else if (sortBy.equals(SortBy.DIVERSITY)) {
            Arrays.sort(sorted, Comparator.comparing(Forest::getDiversity));
        } else Arrays.sort(sorted, Comparator.comparing(Forest::getArea));

        return sorted;
    }

    // Returns the maximum/minimum of score/diversity/area
    private String[] getMaxMin(boolean max, SortBy sortBy) {
        String[] result = sortBy.equals(SortBy.SCORE) ? new String[4] : new String[3];
        result[0] = max ? "Max" : "Min";

        // optional
        Optional<Forest> optional;
        if (sortBy.equals(SortBy.SCORE)) {
            optional = max ? Arrays.stream(forests).max(Comparator.comparing(Forest::getScore)) :
                    Arrays.stream(forests).min(Comparator.comparing(Forest::getScore));
        } else if (sortBy.equals(SortBy.DIVERSITY)) {
            optional = max ? Arrays.stream(forests).max(Comparator.comparing(Forest::getDiversity)) :
                    Arrays.stream(forests).min(Comparator.comparing(Forest::getDiversity));
        } else {
            optional = max ? Arrays.stream(forests).max(Comparator.comparing(Forest::getArea)) :
                    Arrays.stream(forests).min(Comparator.comparing(Forest::getArea));
        }

        if (optional.isPresent()) {
            Forest forest = optional.get();
            if (sortBy.equals(SortBy.SCORE)) {
                result[1] = getTableItem(String.valueOf(forest.getScore()), contrastColor, (int) Math.round(forest.getB() * 100));
                result[2] = getTableItem(Utils.formattedDouble(forest.getB(), 5, true, true,
                        true), contrastColor, (int) Math.round(forest.getB() * 100));
                result[3] = forest.getFileName();
            } else {
                if (sortBy.equals(SortBy.DIVERSITY))
                    result[1] = getTableItem(Utils.formattedDouble(forest.getDiversity(), 5, true,
                            true, true), contrastColor, (int) Math.round(forest.getDiversity() * 100));
                else
                    result[1] = getTableItem(Utils.formattedDouble(forest.getArea(), 5, true, true,
                            true), contrastColor, (int) Math.round(forest.getArea() * 100));
                result[2] = forest.getFileName();
            }
        } else {
            String temp = Utils.formattedDouble(0, 5, true, true, true);
            if (sortBy.equals(SortBy.SCORE)) {
                result[1] = getTableItem("0", contrastColor, 0);
                result[2] = getTableItem(temp, contrastColor, 0);
                result[3] = "[" + forests.length + " forests]";
            } else {
                result[1] = getTableItem(temp, contrastColor, 0);
                result[2] = "[" + forests.length + " forests]";
            }
        }
        return result;
    }

    // Returns the total/average of score/diversity/area
    private String[] getAverageTotal(boolean average, SortBy sortBy) {
        String[] result = sortBy.equals(SortBy.SCORE) ? new String[4] : new String[3];
        result[0] = average ? "Average" : "Total";

        if (sortBy.equals(SortBy.SCORE)) {
            double score = 0;
            double b = 0;
            for (Forest forest : forests) {
                score += forest.getScore();
                b += forest.getB();
            }

            int percentage = (int) Math.round(b / forests.length * 100);
            if (average && forests.length > 0) {
                score = score / forests.length;
                b = b / forests.length;
                result[1] = getTableItem(Utils.formattedDouble(score, 2, false, true, true),
                        contrastColor, percentage);
            } else {
                result[1] = getTableItem(Utils.formattedInteger((int) Math.round(score)),
                        contrastColor, percentage);
            }

            result[2] = getTableItem(Utils.formattedDouble(b, 5, true, true, true),
                    contrastColor, percentage);
            result[3] = "[" + forests.length + " forests]";
        } else {
            double value = 0;
            if (sortBy.equals(SortBy.DIVERSITY)) for (Forest forest : forests) value += forest.getDiversity();
            else for (Forest forest : forests) value += forest.getArea();

            int percentage = (int) Math.round(value / forests.length * 100);
            if (average && forests.length > 0) {
                value = value / forests.length;
            }

            result[1] = getTableItem(Utils.formattedDouble(value, 5, true, true, true),
                    contrastColor, percentage);
            result[2] = "[" + forests.length + " forests]";
        }
        return result;
    }

    private String getTableItem(String string, Color color, int percentage) {
        if (showTableWithPercentage) {
            return "<html>" + string + "<font color='" + Utils.colorToHex(color) + "'> (" + percentage + " %)</font></html>";
        }
        return "<html>" + string + "</html>";
    }
}