package pl.edu.agh.idziak.asw.visualizer.gui.drawing.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveState;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveStateSpace;
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
import java.util.Iterator;
import java.util.List;

public class PathDrawingDelegateImpl implements PathDrawingDelegate {

    private static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 28);
    private GridParams gridParams;
    private Iterator<Color> colorIterator;

    public PathDrawingDelegateImpl(GridParams gridParams) {
        this.gridParams = gridParams;
        resetPathColorIterator();
    }

    private void resetPathColorIterator() {
        colorIterator = Iterables.cycle(DrawConstants.COLORS).iterator();
    }

    @Override
    public void drawPaths(GraphicsWrapper gc, GridInputPlan inputPlan, ASWOutputPlan<GridCollectiveStateSpace, GridCollectiveState> outputPlan) {
        List<GridCollectiveState> collectivePath;
        if (outputPlan == null) {
            collectivePath = ImmutableList.of(
                    inputPlan.getInitialCollectiveState(),
                    inputPlan.getTargetCollectiveState());
        } else {
            collectivePath = outputPlan.getCollectivePath().get();
        }
        OverlapTracker overlapTracker = countOverlaps(collectivePath);

        int step = 1;
        for (GridCollectiveState colState : collectivePath) {
            resetPathColorIterator();
            for (int i = 0; i < inputPlan.getEntities().size(); i++) {
                GridEntityState entState = colState.getEntityStates().get(i);
                int overlapsCount = overlapTracker.getOverlapsAtPosition(entState.getRow(), entState.getCol());
                int overlapIndex = overlapsCount - 1 - overlapTracker.nextIndexAtPosition(entState.getRow(), entState.getCol());

                String letter = resolveEntityLetter(inputPlan, i);
                drawPathPoint(gc, entState, overlapsCount, overlapIndex, letter, step);
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
        String text = letter + step;
        g.setClip(new Ellipse2D.Double(pathPointCircleCenterX - pathPointCircleDiameter / 2, pathPointCircleCenterY - pathPointCircleDiameter / 2, pathPointCircleDiameter, pathPointCircleDiameter));
        int maxFittingFontSize = DrawingUtils.getMaxFittingFontSize(g, DEFAULT_FONT, text, (int) (pathPointCircleDiameter * 0.9), (int) pathPointCircleDiameter);
        g.setFont(DEFAULT_FONT.deriveFont((float) maxFittingFontSize));
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
            g.setPaint(DrawingUtils.toAwtColor(colorIterator.next()));
            g.fill(circle);
            g.setColor(DrawingUtils.toAwtColor(Color.BLACK));
            g.draw(circle);
        } else {
            GraphicsContext gc = gw.getJavafxGraphics();
            gc.setFill(colorIterator.next());
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

    private OverlapTracker countOverlaps(List<GridCollectiveState> collectivePath) {
        OverlapTracker overlapTracker = new OverlapTracker();

        for (GridCollectiveState colState : collectivePath) {
            for (GridEntityState entState : colState.getEntityStates()) {
                overlapTracker.addPathPointInPosition(entState.getRow(), entState.getCol());
            }
        }

        overlapTracker.initCountdown();
        return overlapTracker;
    }
}
