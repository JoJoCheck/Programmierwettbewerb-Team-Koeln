package gui;

import main.Area;
import main.Plant;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class JGraph extends JPanel {
    private static final int PADDING = 10;
    private static final int INSETS = 30;
    private static final int HATCH_LENGTH = 5;
    private final int fieldWidth;
    private final int fieldHeight;
    private final List<Area> animationAreas;
    private final List<Area> areas;
    private final Color[] colors;
    private final JOutput parent;
    private final JScrollPane sp;
    private final Plant[] plants;
    private boolean isAnimationRunning, isAnimationPaused;
    private long millis, runningMillis;
    private Timer timer;
    private boolean adjustAutomatically;
    private double minZoom;
    private double zoomFactor;

    public JGraph(int fieldWidth, int fieldHeight, Plant[] plants, List<Area> areas, Color[] colors, JOutput parent, JScrollPane sp) {
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        this.plants = plants;
        this.areas = areas;
        this.colors = colors;
        this.parent = parent;
        this.sp = sp;
        this.animationAreas = new ArrayList<>();
        this.adjustAutomatically = true;
        this.clearElements();
        sp.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                minZoom = getMinZoom();
                resetZoom();
            }
        });
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        BufferedImage image = new BufferedImage(fieldWidth, fieldHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D imageG2 = (Graphics2D) image.getGraphics();
        imageG2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        imageG2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        imageG2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        int xOrigin = INSETS + PADDING, yOrigin = PADDING;
        double leftWidth = getWidth() - INSETS - PADDING * 2;
        double leftHeight = getHeight() - INSETS - PADDING * 2;
        double zoom = Math.min(leftWidth / fieldWidth, leftHeight / fieldHeight);

        xOrigin += (int) ((leftWidth - fieldWidth * zoom) / 2);
        yOrigin += (int) ((leftHeight - fieldHeight * zoom) / 2);

        double totalWidth = fieldWidth * zoom;
        double totalHeight = fieldHeight * zoom;
        SwingUtilities.invokeLater(() -> parent.setGraphSize((int) totalWidth, (int) totalHeight, zoom));

        if (getWidth() > INSETS + PADDING * 2 && getHeight() > INSETS + PADDING * 2) {
            // draw the graph
            g2.setColor(Color.WHITE);
            g2.drawRect(xOrigin - 1, yOrigin - 1, (int) totalWidth + 1, (int) totalHeight + 1);

            int Y_HATCH_COUNT;
            if (totalHeight > 100) Y_HATCH_COUNT = (int) (totalHeight / 50);
            else Y_HATCH_COUNT = 1;
            while (fieldHeight % Y_HATCH_COUNT != 0) {
                Y_HATCH_COUNT--;
            }

            int X_HATCH_COUNT;
            if (totalWidth >= 100) X_HATCH_COUNT = (int) (totalWidth / 50);
            else X_HATCH_COUNT = 1;
            while (fieldWidth % X_HATCH_COUNT != 0) {
                X_HATCH_COUNT--;
            }

            // hatch marks for y-axis.
            for (int i = 1; i < Y_HATCH_COUNT; i++) {
                int x1 = xOrigin - HATCH_LENGTH;
                int y0 = yOrigin + (int) ((i / (double) Y_HATCH_COUNT) * totalHeight);
                g2.drawLine(xOrigin - 2, y0, x1 - 2, y0);
            }

            // hatch marks for x-axis.
            for (int i = 1; i < X_HATCH_COUNT; i++) {
                int x0 = xOrigin + (int) ((i / (double) X_HATCH_COUNT) * totalWidth);
                int y0 = (int) ((yOrigin + totalHeight));
                int y1 = y0 + HATCH_LENGTH;
                g2.drawLine(x0, y0 + 2, x0, y1 + 2);
            }

            FontMetrics fm = g.getFontMetrics();
            g.setFont(new Font("Consolas", Font.BOLD, 11));

            // hatch marks numbers for y-axis.
            for (int i = 0; i < Y_HATCH_COUNT; i++) {
                String text = "" + (int) Math.round((1 - i / (double) Y_HATCH_COUNT) * fieldHeight);
                int xPos = xOrigin - 35;
                int yPos = yOrigin + (int) ((i / (double) Y_HATCH_COUNT) * totalHeight) + 3;
                g2.drawString(text, xPos, yPos);
            }

            // hatch marks numbers for x-axis.
            for (int i = 0; i <= X_HATCH_COUNT; i++) {
                String text = "" + (int) Math.round((i / (double) X_HATCH_COUNT) * fieldWidth);
                int xPos = xOrigin + (int) ((i / (double) X_HATCH_COUNT) * totalWidth) - fm.stringWidth(text) / 2;
                int yPos = (int) ((yOrigin + totalHeight)) + 20;
                g2.drawString(text, xPos, yPos);
            }

            // draws all circles
            List<Area> list = isAnimationRunning ? animationAreas : areas;
            for (int i = list.size() - 1; i >= 0; i--) {
                synchronized (colors) {
                    g2.setColor(colors[areas.get(i).getPlant().getIndex()]);

                    double radius = areas.get(i).getPlant().getRadius() * zoom;
                    int diameter = radius < 0.5 ? 1 : (int) (radius * 2);

                    int x = xOrigin + (int) (areas.get(i).getPoint().getX() * zoom - radius);
                    int y = yOrigin + (int) ((fieldHeight - areas.get(i).getPoint().getY()) * zoom - radius);
                    if (diameter > 1) g2.fillOval(x, y, diameter, diameter);
                    else g2.drawLine(x, y, x, y);
                }
            }
        }
    }

    public double getMinZoom() {
        int border = adjustAutomatically ? 0 : sp.getVerticalScrollBar().getWidth() * 2;
        double leftWidth = sp.getWidth() - INSETS + PADDING * 2 - border;
        double leftHeight = sp.getHeight() - INSETS + PADDING * 2 - border;
        return Math.min(leftWidth / fieldWidth, leftHeight / fieldHeight);
    }

    public void setZoomFactor(double factor) {
        adjustAutomatically = false;
        double newFactor = zoomFactor + factor;

        boolean update = false;
        if (factor > 0) {
            if (newFactor < 5) update = true;
            else if (newFactor != zoomFactor && zoomFactor != 5) update = true;

            if (update) {
                if (newFactor >= 5) zoomFactor = 5;
                else zoomFactor = newFactor;
                setPreferredSize(new Dimension((int) (fieldWidth * zoomFactor) + INSETS + PADDING * 2,
                        (int) (fieldHeight * zoomFactor) + INSETS + PADDING * 2));
            }
        } else {
            if (newFactor > minZoom) update = true;
            else if (newFactor != zoomFactor && zoomFactor != minZoom) update = true;

            if (update) {
                if (newFactor <= minZoom) resetZoom();
                else {
                    zoomFactor = newFactor;
                    setPreferredSize(new Dimension((int) (fieldWidth * zoomFactor) + INSETS + PADDING * 2,
                            (int) (fieldHeight * zoomFactor) + INSETS + PADDING * 2));
                }
            } else adjustAutomatically = true;
        }

        revalidate();
    }

    public void resetZoom() {
        zoomFactor = minZoom;
        adjustAutomatically = true;
        setPreferredSize(null);
        revalidate();
    }

    public boolean isZooming() {
        return !adjustAutomatically;
    }

    public void zoom(boolean zoomIn) {
        if (zoomIn) setZoomFactor(0.1);
        else setZoomFactor(-0.1);
    }

    public void initZoom() {
        zoomFactor = minZoom = getMinZoom();
    }

    public void startAnimation(long millis) {
        this.millis = millis;
        this.isAnimationRunning = true;

        int period = areas.size() > 10_000 ? 100 : 10;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!isAnimationPaused) {
                    runningMillis += period;

                    if (runningMillis >= millis) runningMillis = millis;
                    double percentageTime = (double) runningMillis / (double) millis;

                    final int actSize = animationAreas.size();
                    final int numberElements = (int) Math.round(percentageTime * areas.size());

                    List<Plant> changedPlantsList = new ArrayList<>();
                    for (int i = actSize; i < numberElements; i++) {
                        Area area = areas.get(i);
                        animationAreas.add(area);
                        changedPlantsList.add(area.getPlant());
                    }

                    // results
                    double[] results = Utils.results(fieldWidth, fieldHeight, plants, animationAreas);
                    SwingUtilities.invokeLater(() -> {
                        parent.setRunTime(millis - runningMillis);
                        parent.setD(results[0]);
                        parent.setA(results[1]);
                        parent.setB(results[2]);
                        parent.setPlants(animationAreas.size());
                        for (Plant plant : changedPlantsList) {
                            parent.addNumber(plant);
                        }
                        repaint();
                    });

                    if (runningMillis >= millis) {
                        isAnimationRunning = false;
                        timer.cancel();
                        parent.finishAnimation();
                    }
                }
            }
        }, 0, period);
    }

    public void toggleAnimation() {
        isAnimationPaused = !isAnimationPaused;
        if (isAnimationPaused) {
            timer.cancel();
        } else {
            startAnimation(millis);
        }
    }

    public boolean isAnimating() {
        return isAnimationRunning && !isAnimationPaused;
    }

    public void cancelAnimation() {
        isAnimationRunning = false;
        isAnimationPaused = false;
        this.runningMillis = 0;
        timer.cancel();
        clearElements();
    }

    public long getRunningMillis() {
        return runningMillis;
    }

    private void clearElements() {
        parent.clearElements();
        animationAreas.clear();
    }

    public void restart() {
        cancelAnimation();
        startAnimation(millis);
    }
}