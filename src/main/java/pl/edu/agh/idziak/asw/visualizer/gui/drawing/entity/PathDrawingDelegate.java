package pl.edu.agh.idziak.asw.visualizer.gui.drawing.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import pl.edu.agh.idziak.asw.impl.grid2d.G2DCollectiveState;
import pl.edu.agh.idziak.asw.impl.grid2d.G2DEntityState;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.DrawConstants;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.GridParams;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Created by Tomasz on 23.02.2017.
 */
public class PathDrawingDelegate {

    private GridParams gridParams;
    private Iterator<Color> colorIterator;
    private Map<Integer, Integer> overlappingLinesInRowCountdown;
    private Map<Integer, Integer> numOfOverlappingLinesInRow;
    private Map<Integer, Integer> overlappingLinesInColumnCountdown;
    private Map<Integer, Integer> numOfOverlappingLinesInColumn;

    public PathDrawingDelegate(GridParams gridParams) {
        this.gridParams = gridParams;
        newColorIterator();
        overlappingLinesInRowCountdown = Maps.newHashMap();
        overlappingLinesInColumnCountdown = Maps.newHashMap();
    }

    private void newColorIterator() {
        colorIterator = Iterables.cycle(DrawConstants.COLORS).iterator();
    }


    public void drawPaths(List<G2DCollectiveState> collectivePath, GraphicsContext gc) {

        gc.save();
        gc.setLineWidth(1);
        newColorIterator();

        countOverlappingLines(collectivePath);

        Set<?> entities = collectivePath.get(0).getEntityStates().keySet();

        drawAllPaths(gc, collectivePath, entities);

        gc.restore();
    }

    private void drawAllPaths(GraphicsContext gc, List<G2DCollectiveState> collectivePath, Set<?> entities) {

        for (Object entity : entities) {
            Color nextColor = colorIterator.next();
            gc.setStroke(nextColor);
            gc.setFill(nextColor);
            List<G2DEntityState> entityPath = getPathForEntityWithoutStart(collectivePath, entity);

            G2DEntityState secondLastState = null;
            G2DEntityState lastState = collectivePath.get(0).getStateForEntity(entity);
            int lastStatePosX = placeNewLineInColumn(lastState);
            int lastStatePosY = placeNewLineInRow(lastState);
            for (G2DEntityState newState : entityPath) {
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

        int arrowSize = 3;
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

    private static List<G2DEntityState> getPathForEntityWithoutStart(List<G2DCollectiveState> collectivePath, Object entity) {
        return collectivePath.stream()
                             .skip(1)
                             .map(collectiveState -> collectiveState
                                     .getStateForEntity(entity))
                             .collect(Collectors.toList());
    }

    private static boolean isEntityReversingDirection(G2DEntityState secondLastState, G2DEntityState lastState, G2DEntityState newState) {
        return secondLastState != null &&
                ((isColumnEqual(secondLastState, newState)
                        && !isColumnEqual(lastState, newState))
                        || (isRowEqual(secondLastState, newState)
                        && !isRowEqual(lastState, newState)));
    }

    private static boolean isRowEqual(G2DEntityState state1, G2DEntityState state2) {
        return state1.getRow().equals(state2.getRow());
    }

    private static boolean isColumnEqual(G2DEntityState state1, G2DEntityState state2) {
        return state1.getCol().equals(state2.getCol());
    }

    private int placeNewLineInColumn(G2DEntityState lastState) {
        Integer column = lastState.getCol();
        Integer newLineIndex = overlappingLinesInColumnCountdown.compute(column, (col, counter) -> counter - 1);
        return gridParams.getTopPosForIndex(column)
                + gridParams.getCellWidth() / (numOfOverlappingLinesInColumn.get(column) + 1) * (newLineIndex + 1);
    }

    private int placeNewLineInRow(G2DEntityState lastState) {
        Integer row = lastState.getRow();
        Integer newLineIndex = overlappingLinesInRowCountdown.compute(row, (col, counter) -> counter - 1);
        return gridParams.getTopPosForIndex(row)
                + gridParams.getCellWidth() / (numOfOverlappingLinesInRow.get(row) + 1) * (newLineIndex + 1);
    }

    private void countOverlappingLines(List<G2DCollectiveState> collectivePath) {
        overlappingLinesInColumnCountdown.clear();
        overlappingLinesInRowCountdown.clear();

        Set<?> entities = collectivePath.get(0).getEntityStates().keySet();

        for (Object entity : entities) {
            List<G2DEntityState> entityPath = getPathForEntityWithoutStart(collectivePath, entity);

            G2DEntityState secondLastState = null;
            G2DEntityState lastState = collectivePath.get(0).getStateForEntity(entity);
            overlappingLinesInColumnCountdown.compute(lastState.getCol(), getIncrementValueFunction());
            overlappingLinesInRowCountdown.compute(lastState.getRow(), getIncrementValueFunction());

            for (G2DEntityState newState : entityPath) {
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
