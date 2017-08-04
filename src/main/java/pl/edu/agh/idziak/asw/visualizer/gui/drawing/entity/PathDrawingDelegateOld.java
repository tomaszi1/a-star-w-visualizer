package pl.edu.agh.idziak.asw.visualizer.gui.drawing.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveState;
import pl.edu.agh.idziak.asw.impl.grid2d.GridEntityState;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.DrawConstants;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.GridParams;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Created by Tomasz on 23.02.2017.
 */
public class PathDrawingDelegateOld  {

    private GridParams gridParams;
    private Iterator<Color> colorIterator;
    private Map<Integer, Integer> overlappingLinesInRowCountdown;
    private Map<Integer, Integer> numOfOverlappingLinesInRow;
    private Map<Integer, Integer> overlappingLinesInColumnCountdown;
    private Map<Integer, Integer> numOfOverlappingLinesInColumn;

    public PathDrawingDelegateOld(GridParams gridParams) {
        this.gridParams = gridParams;
        newColorIterator();
        overlappingLinesInRowCountdown = Maps.newHashMap();
        overlappingLinesInColumnCountdown = Maps.newHashMap();
    }

    private void newColorIterator() {
        colorIterator = Iterables.cycle(DrawConstants.COLORS).iterator();
    }

    public void drawPaths(List<GridCollectiveState> collectivePath, GraphicsContext gc) {

        gc.save();
        gc.setLineWidth(DrawConstants.PATH_LINE_WIDTH);
        newColorIterator();

        countOverlappingLinesVonNeumann(collectivePath);
        drawAllPathsVonNeumann(gc, collectivePath);

        gc.restore();
    }

    private void drawAllPathsMoore(GraphicsContext gc, List<GridCollectiveState> collectivePath) {
        if (collectivePath.isEmpty()) {
            return;
        }

        int collectiveStateSize = collectivePath.get(0).getEntityStates().size();

    }

    private void drawAllPathsVonNeumann(GraphicsContext gc, List<GridCollectiveState> collectivePath) {
        int entitiesCount = collectivePath.get(0).getEntityStates().size();

        for (int entityIndex = 0; entityIndex < entitiesCount; entityIndex++) {
            Color nextColor = colorIterator.next();
            gc.setStroke(nextColor);
            gc.setFill(nextColor);
            List<GridEntityState> entityPath = getPathForEntityWithoutStart(collectivePath, entityIndex);

            GridEntityState secondLastState = null;
            GridEntityState lastState = collectivePath.get(0).getEntityStates().get(entityIndex);
            int lastStatePosX = placeNewLineInColumn(lastState);
            int lastStatePosY = placeNewLineInRow(lastState);
            for (GridEntityState newState : entityPath) {
                boolean entityStaysInTheSameRow = isRowEqual(lastState, newState);
                boolean entityStaysInTheSameColumn = isColumnEqual(lastState, newState);

                if (isEntityReversingDirection(secondLastState, lastState, newState)) {
                    if (entityStaysInTheSameRow) {
                        int newStatePosX = placeNewLineInColumn(newState);
                        int newStatePosY = placeNewLineInRow(newState);
                        drawArrowedLine(gc, lastStatePosX, lastStatePosY, lastStatePosX, newStatePosY);
                        drawArrowedLine(gc, lastStatePosX, newStatePosY, newStatePosX, newStatePosY);
                        lastStatePosX = newStatePosX;
                        lastStatePosY = newStatePosY;
                    } else if (entityStaysInTheSameColumn) {
                        int newStatePosX = placeNewLineInColumn(newState);
                        int newStatePosY = placeNewLineInRow(newState);
                        drawArrowedLine(gc, lastStatePosX, lastStatePosY, newStatePosX, lastStatePosY);
                        drawArrowedLine(gc, newStatePosX, lastStatePosY, newStatePosX, newStatePosY);
                        lastStatePosX = newStatePosX;
                        lastStatePosY = newStatePosY;
                    }
                } else if (entityStaysInTheSameRow && entityStaysInTheSameColumn) {
                    double stopDotSize = gridParams.getStopDotSize();
                    gc.fillOval(lastStatePosX - stopDotSize / 2, lastStatePosY - stopDotSize / 2, stopDotSize, stopDotSize);
                } else if (entityStaysInTheSameRow) {
                    int newStatePosX = placeNewLineInColumn(newState);
                    drawArrowedLine(gc, lastStatePosX, lastStatePosY, newStatePosX, lastStatePosY);
                    lastStatePosX = newStatePosX;
                } else if (entityStaysInTheSameColumn) {
                    int newStatePosY = placeNewLineInRow(newState);
                    drawArrowedLine(gc, lastStatePosX, lastStatePosY, lastStatePosX, newStatePosY);
                    lastStatePosY = newStatePosY;
                }

                secondLastState = lastState;
                lastState = newState;
            }
        }
    }

    private static void drawArrowedLine(GraphicsContext gc, int x1, int y1, int x2, int y2) {
        gc.strokeLine(x1, y1, x2, y2);

        int arrowSize = DrawConstants.PATH_ARROW_SIZE;
        if (x1 == x2) {
            int midY = (y1 + y2) / 2;
            if (y2 > y1) {
                gc.strokeLine(x1, midY, x1 - arrowSize, midY - arrowSize);
                gc.strokeLine(x1, midY, x1 + arrowSize, midY - arrowSize);
            } else {
                gc.strokeLine(x1, midY, x1 - arrowSize, midY + arrowSize);
                gc.strokeLine(x1, midY, x1 + arrowSize, midY + arrowSize);
            }
        } else if (y1 == y2) {
            int midX = (x1 + x2) / 2;
            if (x2 > x1) {
                gc.strokeLine(midX, y1, midX - arrowSize, y1 - arrowSize);
                gc.strokeLine(midX, y1, midX - arrowSize, y1 + arrowSize);
            } else {
                gc.strokeLine(midX, y1, midX + arrowSize, y1 - arrowSize);
                gc.strokeLine(midX, y1, midX + arrowSize, y1 + arrowSize);
            }
        }


    }

    private static List<GridEntityState> getPathForEntityWithoutStart(List<GridCollectiveState> collectivePath, int entityIndex) {
        return collectivePath.stream()
                .skip(1)
                .map(collectiveState -> collectiveState.getEntityStates().get(entityIndex))
                .collect(Collectors.toList());
    }

    private static boolean isEntityReversingDirection(GridEntityState secondLastState, GridEntityState lastState, GridEntityState newState) {
        return secondLastState != null &&
                ((isColumnEqual(secondLastState, newState)
                        && !isColumnEqual(lastState, newState))
                        || (isRowEqual(secondLastState, newState)
                        && !isRowEqual(lastState, newState)));
    }

    private static boolean isRowEqual(GridEntityState state1, GridEntityState state2) {
        return state1.getRow() == state2.getRow();
    }

    private static boolean isColumnEqual(GridEntityState state1, GridEntityState state2) {
        return state1.getCol() == state2.getCol();
    }

    private int placeNewLineInColumn(GridEntityState lastState) {
        Integer column = lastState.getCol();
        Integer newLineIndex = overlappingLinesInColumnCountdown.compute(column, (col, counter) -> counter - 1);
        return gridParams.getTopPosForIndex(column)
                + gridParams.getCellWidth() / (numOfOverlappingLinesInColumn.get(column) + 1) * (newLineIndex + 1);
    }

    private int placeNewLineInRow(GridEntityState lastState) {
        Integer row = lastState.getRow();
        Integer newLineIndex = overlappingLinesInRowCountdown.compute(row, (col, counter) -> counter - 1);
        return gridParams.getTopPosForIndex(row)
                + gridParams.getCellWidth() / (numOfOverlappingLinesInRow.get(row) + 1) * (newLineIndex + 1);
    }

    private void countOverlappingLinesVonNeumann(List<GridCollectiveState> collectivePath) {
        overlappingLinesInColumnCountdown.clear();
        overlappingLinesInRowCountdown.clear();

        int entitiesCount = collectivePath.get(0).getEntityStates().size();

        for (int entityIndex = 0; entityIndex < entitiesCount; entityIndex++) {
            List<GridEntityState> entityPath = getPathForEntityWithoutStart(collectivePath, entityIndex);

            GridEntityState secondLastState = null;
            GridEntityState lastState = collectivePath.get(0).getEntityStates().get(entityIndex);
            overlappingLinesInColumnCountdown.compute(lastState.getCol(), getIncrementValueFunction());
            overlappingLinesInRowCountdown.compute(lastState.getRow(), getIncrementValueFunction());

            for (GridEntityState newState : entityPath) {
                boolean entityStaysInTheSameRow = isRowEqual(lastState, newState);
                boolean entityStaysInTheSameColumn = isColumnEqual(lastState, newState);

                if (isEntityReversingDirection(secondLastState, lastState, newState)) {
                    overlappingLinesInRowCountdown.compute(newState.getRow(), getIncrementValueFunction());
                    overlappingLinesInColumnCountdown.compute(newState.getCol(), getIncrementValueFunction());
                } else if (entityStaysInTheSameRow && !entityStaysInTheSameColumn) {
                    overlappingLinesInColumnCountdown.compute(newState.getCol(), getIncrementValueFunction());
                } else if (entityStaysInTheSameColumn && !entityStaysInTheSameRow) {
                    overlappingLinesInRowCountdown.compute(newState.getRow(), getIncrementValueFunction());
                }

                secondLastState = lastState;
                lastState = newState;
            }
        }

        numOfOverlappingLinesInRow = ImmutableMap.copyOf(overlappingLinesInRowCountdown);
        numOfOverlappingLinesInColumn = ImmutableMap.copyOf(overlappingLinesInColumnCountdown);
    }

    private static BiFunction<Integer, Integer, Integer> getIncrementValueFunction() {
        return (k, v) -> (v == null) ? 1 : v + 1;
    }

}
