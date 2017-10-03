package pl.edu.agh.idziak.asw.visualizer.gui.drawing.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveState;
import pl.edu.agh.idziak.asw.impl.grid2d.GridEntityState;
import pl.edu.agh.idziak.asw.impl.grid2d.GridInputPlan;
import pl.edu.agh.idziak.asw.model.ASWOutputPlan;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.DrawConstants;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.DrawingUtils;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.GridParams;
import pl.edu.agh.idziak.asw.visualizer.gui.root.GraphicsWrapper;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.Entity;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PathDrawingDelegateImpl implements PathDrawingDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(PathDrawingDelegateImpl.class);
    private GridParams gridParams;
    private Iterator<Color> pathPointColorIterator;
    private Color currentColor;
    public static final boolean PRINT_NUMBERS = true;

    public PathDrawingDelegateImpl(GridParams gridParams) {
        this.gridParams = gridParams;
        resetPathColorIterator();
    }

    private void resetPathColorIterator() {
        pathPointColorIterator = Iterables.cycle(DrawConstants.COLORS).iterator();
    }

    @Override
    public void drawPaths(GraphicsWrapper gc, GridInputPlan inputPlan, ASWOutputPlan<GridCollectiveState> outputPlan) {
        List<GridCollectiveState> collectivePath;
        GridCollectiveState targetState = inputPlan.getTargetCollectiveState();
        if (outputPlan == null || outputPlan.getCollectivePath() == null) {
            collectivePath = ImmutableList.of(
                    inputPlan.getInitialCollectiveState(),
                    targetState);
            LOG.info("No path to draw");
        } else {
            collectivePath = outputPlan.getCollectivePath().get();
        }
        OverlapTracker overlapTracker = countOverlaps(collectivePath, targetState);

        Set<Integer> entityReachedTarget = new HashSet<>();

        int step = 1;
        for (GridCollectiveState colState : collectivePath) {
            resetPathColorIterator();
            for (int i = 0; i < inputPlan.getEntities().size(); i++) {
                currentColor = pathPointColorIterator.next();
                if (entityReachedTarget.contains(i)) {
                    continue;
                }
                GridEntityState entState = colState.getEntityStates().get(i);
                int overlapsCount = overlapTracker.getOverlapsAtPosition(entState.getRow(), entState.getCol());
                int overlapIndex = overlapsCount - 1 - overlapTracker.nextIndexAtPosition(entState.getRow(), entState.getCol());

                String letter = resolveEntityLetter(inputPlan, i);
                drawPathPoint(gc, entState, overlapsCount, overlapIndex, letter, step);
                if (entState.equals(targetState.getEntityStates().get(i))) {
                    entityReachedTarget.add(i);
                }
            }
            step++;
        }
    }

    private void drawPathPoint(GraphicsWrapper gc, GridEntityState entState, int overlapsCount, int overlapIndex, String letter, int step) {
        DrawConstants.PathPointPositionIndex pathPointIndex = DrawConstants.getPositionIndexForPathPoint(overlapIndex, overlapsCount);

        double columnWidth = (double) gridParams.getCellWidth() / pathPointIndex.colsCount;
        double pathPointCircleCenterX = gridParams.getTopPosForIndex(entState.getCol())
                + columnWidth * pathPointIndex.col
                + columnWidth / 2
                + pathPointIndex.offsetDirectionCol * gridParams.getCellWidth() * DrawConstants.PATH_POINT_OFFSET_FACTOR;

        double rowWidth = (double) gridParams.getCellWidth() / pathPointIndex.rowsCount;
        double pathPointCircleCenterY = gridParams.getTopPosForIndex(entState.getRow())
                + rowWidth * pathPointIndex.row
                + rowWidth / 2
                + pathPointIndex.offsetDirectionRow * gridParams.getCellWidth() * DrawConstants.PATH_POINT_OFFSET_FACTOR;

        double pathPointCircleDiameter = gridParams.getPathPointCircleDiameter(overlapsCount);
        drawPathPointCircle(gc, pathPointCircleDiameter, pathPointCircleCenterX, pathPointCircleCenterY);
        drawPathPointLabel(gc, pathPointCircleDiameter, pathPointCircleCenterX, pathPointCircleCenterY, letter, step);
    }

    private void drawPathPointLabel(GraphicsWrapper gc, double pathPointCircleDiameter, double pathPointCircleCenterX, double pathPointCircleCenterY, String letter, int step) {
        Graphics2D g = gc.getSwingGraphics();
        String text = PRINT_NUMBERS? letter + step : letter;
        g.setClip(new Ellipse2D.Double(pathPointCircleCenterX - pathPointCircleDiameter / 2, pathPointCircleCenterY - pathPointCircleDiameter / 2, pathPointCircleDiameter, pathPointCircleDiameter));
        int maxFittingFontSize = DrawingUtils.getMaxFittingFontSize(g, DrawConstants.DEFAULT_FONT, text, (int) (pathPointCircleDiameter * 0.9), (int) pathPointCircleDiameter);
        g.setFont(DrawConstants.DEFAULT_FONT.deriveFont((float) maxFittingFontSize));
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        float x = (float) (pathPointCircleCenterX - metrics.stringWidth(text) / 2);
        float y = (float) (pathPointCircleCenterY - metrics.getHeight() / 2 + metrics.getAscent());
        g.drawString(text, x, y);
    }

    private void drawPathPointCircle(GraphicsWrapper gw, double pathPointCircleDiameter, double pathPointCircleCenterX, double pathPointCircleCenterY) {
        if (gw.isSwingGraphics()) {
            Graphics2D g = gw.getSwingGraphics();
            g.setStroke(new BasicStroke((float) DrawConstants.PATH_POINT_CIRCLE_EDGE_WIDTH));

            double d = pathPointCircleDiameter;
            double x = (pathPointCircleCenterX - d / 2);
            double y = (pathPointCircleCenterY - d / 2);
            Ellipse2D.Double circle = new Ellipse2D.Double(x, y, d, d);
            g.setClip(new Rectangle.Double(x - 1, y - 1, d + 2, d + 2));
            g.setPaint(DrawingUtils.toAwtColor(currentColor));
            g.fill(circle);
            g.setColor(DrawingUtils.toAwtColor(Color.BLACK));
            g.draw(circle);
        } else {
            GraphicsContext gc = gw.getJavafxGraphics();
            gc.setFill(pathPointColorIterator.next());
            gc.setLineWidth(DrawConstants.PATH_POINT_CIRCLE_EDGE_WIDTH);
            gc.setStroke(Color.BLACK);

            double d = pathPointCircleDiameter;
            double x = pathPointCircleCenterX - d / 2;
            double y = pathPointCircleCenterY - d / 2;
            gc.fillOval(x, y, d, d);
        }
    }

    private String resolveEntityLetter(GridInputPlan inputPlan, int i) {
        return ((Entity) inputPlan.getEntities().get(i)).getLetter();
    }

    private OverlapTracker countOverlaps(List<GridCollectiveState> collectivePath, GridCollectiveState targetCollectiveState) {
        OverlapTracker overlapTracker = new OverlapTracker();

        for (GridCollectiveState colState : collectivePath) {
            List<GridEntityState> entityStates = colState.getEntityStates();
            for (int i = 0; i < entityStates.size(); i++) {
                GridEntityState entState = entityStates.get(i);
                if (!targetCollectiveState.getEntityStates().get(i).equals(entState)) {
                    overlapTracker.addPathPointInPosition(entState.getRow(), entState.getCol());
                }
            }
        }

        targetCollectiveState.getEntityStates().forEach(entState ->
                overlapTracker.addPathPointInPosition(entState.getRow(), entState.getCol()));

        overlapTracker.initCountdown();
        return overlapTracker;
    }
}
