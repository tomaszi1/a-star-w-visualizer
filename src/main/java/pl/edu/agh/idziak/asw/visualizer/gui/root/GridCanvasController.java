package pl.edu.agh.idziak.asw.visualizer.gui.root;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.idziak.asw.OutputPlan;
import pl.edu.agh.idziak.asw.grid2d.G2DCollectiveState;
import pl.edu.agh.idziak.asw.grid2d.G2DEntityState;
import pl.edu.agh.idziak.asw.grid2d.G2DStateSpace;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.devzone.DevZoneCellDrawingDelegate;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.Entity;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.TestCase;
import pl.edu.agh.idziak.common.UntypedTwoMapsIterator;
import pl.edu.agh.idziak.common.WalkingPairIterator;

import java.util.List;

/**
 * Created by Tomasz on 28.08.2016.
 */
public class GridCanvasController {
    private static final Logger LOG = LoggerFactory.getLogger(GridCanvasController.class);
    private static final int NORMAL_CELL_WIDTH = 40;
    private double scaleFactor = 1;
    private int entityWidth = getCellWidth() * 2 / 3;
    private Canvas canvas;
    private CanvasMouseEventsDispatcher canvasMouseEventsDispatcher;
    private TestCase currentTestCase;
    private final ChangeListener<OutputPlan<G2DStateSpace, G2DCollectiveState, G2DEntityState, Integer, Double>> outputPlanChangeListener
            = (obs, oldVal, newVal) -> repaint();
    private DevZoneCellDrawingDelegate devZoneCellDrawingDelegate;

    public GridCanvasController(Canvas canvas, ObservableObjectValue<TestCase>
            testCaseObjectProperty, CanvasMouseEventsDispatcher canvasMouseEventsDispatcher) {
        this.canvas = canvas;
        this.canvasMouseEventsDispatcher = canvasMouseEventsDispatcher;
        devZoneCellDrawingDelegate = new DevZoneCellDrawingDelegate();

        testCaseObjectProperty.addListener((observable, oldValue, newTestCase) -> {
            if (currentTestCase != null) {
                currentTestCase.outputPlanProperty().removeListener(outputPlanChangeListener);
            }
            drawTestCase(newTestCase);
            currentTestCase.outputPlanProperty().addListener(outputPlanChangeListener);
        });

        canvas.setOnMouseClicked(this::handleCanvasClicked);
    }

    private void handleCanvasClicked(MouseEvent event) {
        int colIndex = getIndexForPosition((int) event.getX());
        int rowIndex = getIndexForPosition((int) event.getY());
        canvasMouseEventsDispatcher.cellClicked(rowIndex, colIndex);
    }

    private int getIndexForPosition(int pos) {
        return pos / getCellWidth();
    }

    private void repaint() {
        drawTestCase(currentTestCase);
    }

    private void drawTestCase(TestCase testCase) {
        LOG.info("Redrawing test case");
        currentTestCase = testCase;
        GraphicsContext gc = canvas.getGraphicsContext2D();

        if (testCase == null) {
            return;
        }

        G2DStateSpace stateSpace = testCase.getInputPlan().getStateSpace();

        drawStateSpace(gc, stateSpace);

        if (currentTestCase.outputPlanProperty().isNotNull().get()) {
            drawOutputPlan(gc);
        }
        drawEntities(gc, testCase);
        drawDeviationZones(gc, testCase);
    }

    private void drawDeviationZones(GraphicsContext gc, TestCase testCase) {
        if (testCase.outputPlanProperty().isNull().get()) {
            return;
        }

        devZoneCellDrawingDelegate.resetState();

        testCase.outputPlanProperty()
                .get()
                .getDeviationZonePlans()
                .forEach(devZonePlan -> {
                    devZoneCellDrawingDelegate.switchPattern();
                    devZonePlan.getDeviationZone()
                            .getStates()
                            .forEach(entityState -> drawDevZoneState(gc, entityState));
                });
    }

    private void drawDevZoneState(GraphicsContext gc, G2DEntityState state) {
        gc.save();
        int topY = getTopPosForIndex(state.getRow());
        int bottomY = getTopPosForIndex(state.getRow() + 1);
        int leftX = getTopPosForIndex(state.getCol());
        int rightX = getTopPosForIndex(state.getCol() + 1);

        clipRect(gc, leftX + 1, topY - 1, getCellWidth() - 2, getCellWidth() - 2);

        devZoneCellDrawingDelegate.setCellBounds(topY, leftX, bottomY, rightX);
        devZoneCellDrawingDelegate.drawDevZone(gc);
        gc.restore();
    }

    private static void clipRect(GraphicsContext gc, int x, int y, int width, int height) {
        gc.beginPath();
        gc.rect(x, y, x + width, y + height);
        gc.closePath();
        gc.clip();
    }

    private int getTopPosForIndex(int index) {
        return index * getCellWidth();
    }

    private void drawStateSpace(GraphicsContext gc, G2DStateSpace stateSpace) {
        int rows = stateSpace.getRows();
        int cols = stateSpace.getCols();

        canvas.setWidth(getCellWidth() * cols);
        canvas.setHeight(getCellWidth() * rows);
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);

        for (int curRow = 0; curRow <= rows; curRow++) {
            int yPos = curRow * getCellWidth();
            gc.strokeLine(0, yPos, cols * getCellWidth(), yPos);
        }

        for (int curCol = 0; curCol <= cols; curCol++) {
            int xPos = curCol * getCellWidth();
            gc.strokeLine(xPos, 0, xPos, rows * getCellWidth());
        }
    }

    private void drawOutputPlan(GraphicsContext gc) {
        List<G2DCollectiveState> collectivePath = currentTestCase.outputPlanProperty().get().getCollectivePath().get();

        WalkingPairIterator<G2DCollectiveState> it = new WalkingPairIterator<>(collectivePath);
        while (it.hasNext()) {
            it.next();
            drawPathFragments(it.getFirst(), it.getSecond(), gc);
        }
    }

    private void drawPathFragments(G2DCollectiveState first, G2DCollectiveState second, GraphicsContext gc) {
        UntypedTwoMapsIterator<G2DEntityState> it =
                new UntypedTwoMapsIterator<>(first.getEntityStates(), second.getEntityStates());

        while (it.hasNext()) {
            it.next();
            drawPathFragment(it.getFirstValue(), it.getSecondValue(), gc);
        }
    }

    private void drawPathFragment(G2DEntityState first, G2DEntityState second, GraphicsContext gc) {
        gc.save();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeLine(getCenterPosForIndex(first.getCol()),
                getCenterPosForIndex(first.getRow()),
                getCenterPosForIndex(second.getCol()),
                getCenterPosForIndex(second.getRow()));
        gc.restore();
    }

    private int getCenterPosForIndex(int pos) {
        return getTopPosForIndex(pos) + getCellWidth() / 2;
    }

    private void drawEntities(GraphicsContext gc, TestCase testCase) {
        testCase.getInputPlan()
                .getInitialCollectiveState()
                .getEntityStates()
                .entrySet()
                .forEach(entry -> drawInitialEntityState(entry.getKey(), entry.getValue(), gc));
        testCase.getInputPlan()
                .getTargetCollectiveState()
                .getEntityStates()
                .entrySet()
                .forEach(entry -> drawTargetEntityState(entry.getKey(), entry.getValue(), gc));
    }

    private void drawTargetEntityState(Object entity, G2DEntityState state, GraphicsContext gc) {
        gc.save();
        int leftX = getTopPosForIndex(state.getCol());
        int topY = getTopPosForIndex(state.getRow());

        gc.setFill(Color.LIGHTBLUE);
        int entityRectOffset = (getCellWidth() - entityWidth) / 2;
        gc.fillRect(leftX + entityRectOffset, topY + entityRectOffset, entityWidth, entityWidth);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        if (entity instanceof Entity) {
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font(20));
            gc.fillText(
                    ((Entity) entity).getId().toString(),
                    leftX + getCellWidth() / 2,
                    topY + getCellWidth() / 2
            );
        }

        gc.restore();
    }

    private void drawInitialEntityState(Object entity, G2DEntityState state, GraphicsContext gc) {
        gc.save();
        int leftX = getCellWidth() * state.getCol();
        int topY = getCellWidth() * state.getRow();

        gc.setFill(Color.ORANGE);
        int entityRectOffset = (getCellWidth() - entityWidth) / 2;
        gc.fillRect(leftX + entityRectOffset, topY + entityRectOffset, entityWidth, entityWidth);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        if (entity instanceof Entity) {
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font(20));
            gc.fillText(
                    ((Entity) entity).getId().toString(),
                    leftX + getCellWidth() / 2,
                    topY + getCellWidth() / 2
            );
        }

        gc.restore();
    }

    void scaleUp() {
        scaleFactor = scaleFactor * 5 / 4;
        repaint();
    }

    void scaleDown() {
        if (getCellWidth() < 10) {
            return;
        }
        scaleFactor = scaleFactor * 4 / 5;
        repaint();
    }

    private int getCellWidth() {
        return (int) (NORMAL_CELL_WIDTH * scaleFactor);
    }
}
