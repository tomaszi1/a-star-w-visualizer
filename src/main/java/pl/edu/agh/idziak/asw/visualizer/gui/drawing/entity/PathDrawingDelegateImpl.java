package pl.edu.agh.idziak.asw.visualizer.gui.drawing.entity;

import com.google.common.collect.Iterables;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveState;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveStateSpace;
import pl.edu.agh.idziak.asw.impl.grid2d.GridEntityState;
import pl.edu.agh.idziak.asw.impl.grid2d.GridInputPlan;
import pl.edu.agh.idziak.asw.model.ASWOutputPlan;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.DrawConstants;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.GridParams;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.Entity;

import java.util.Iterator;
import java.util.List;

public class PathDrawingDelegateImpl implements PathDrawingDelegate {

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
    public void drawPaths(GridInputPlan inputPlan, ASWOutputPlan<GridCollectiveStateSpace, GridCollectiveState> outputPlan, GraphicsContext gc) {
        List<GridCollectiveState> collectivePath = outputPlan.getCollectivePath().get();
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
        }
    }

    private void drawPathPoint(GraphicsContext gc, GridEntityState entState, int overlapsCount, int overlapIndex, String letter, int step) {
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

    private void drawPathPointLabel(GraphicsContext gc, double pathPointCircleDiameter, double pathPointCircleCenterX, double pathPointCircleCenterY, String letter, int step) {

    }

    private void drawPathPointCircle(GraphicsContext gc, double pathPointCircleDiameter, double pathPointCircleCenterX, double pathPointCircleCenterY) {
        gc.setFill(colorIterator.next());
        gc.setLineWidth(DrawConstants.PATH_POINT_CIRCLE_EDGE_WIDTH);
        gc.setStroke(Color.BLACK);

        double d = pathPointCircleDiameter;
        double x = pathPointCircleCenterX - d / 2;
        double y = pathPointCircleCenterY - d / 2;
        gc.fillOval(x, y, d, d);
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
