package pl.edu.agh.idziak.asw.visualizer.gui.root;

import pl.edu.agh.idziak.asw.grid2d.G2DEntityState;
import pl.edu.agh.idziak.asw.grid2d.G2DStateSpace;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.Entity;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.TestCase;
import javafx.beans.value.ObservableObjectValue;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Created by Tomasz on 28.08.2016.
 */
public class GridCanvasController {

    private static final int CELL_WIDTH = 40;
    private static final int ENTITY_WIDTH = CELL_WIDTH * 2 / 3;
    private Canvas canvas;
    private TestCase lastTestCase;

    public GridCanvasController(Canvas canvas, ObservableObjectValue<TestCase>
            testCaseObjectProperty) {
        this.canvas = canvas;
        testCaseObjectProperty.addListener((observable, oldValue, newValue) -> drawTestCase(newValue));
    }

    public void repaint() {
        drawTestCase(lastTestCase);
    }

    private void drawTestCase(TestCase testCase) {
        lastTestCase = testCase;
        GraphicsContext gc = canvas.getGraphicsContext2D();

        int cellWidth = CELL_WIDTH;

        if (testCase == null) {
            return;
        }

        G2DStateSpace stateSpace = testCase.getInputPlan().getStateSpace();
        int rows = stateSpace.getRows();
        int cols = stateSpace.getCols();

        canvas.setWidth(cellWidth * cols);
        canvas.setHeight(cellWidth * rows);
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);


        for (int curRow = 0; curRow <= rows; curRow++) {
            int yPos = curRow * cellWidth;
            gc.strokeLine(0, yPos, cols * cellWidth, yPos);
        }

        for (int curCol = 0; curCol <= cols; curCol++) {
            int xPos = curCol * cellWidth;
            gc.strokeLine(xPos, 0, xPos, rows * cellWidth);
        }

        drawEntities(gc, testCase);
    }

    private static void drawEntities(GraphicsContext gc, TestCase testCase) {
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

    private static void drawTargetEntityState(Object entity, G2DEntityState state, GraphicsContext gc) {
        gc.save();
        int leftX = CELL_WIDTH * state.getCol();
        int topY = CELL_WIDTH * state.getRow();
        /*gc.beginPath();
        gc.rect(leftX, topY, leftX + CELL_WIDTH, topY + CELL_WIDTH);
        gc.closePath();
        gc.clip();*/

        gc.setFill(Color.LIGHTBLUE);
        int entityRectOffset = (CELL_WIDTH - ENTITY_WIDTH) / 2;
        gc.fillRect(leftX + entityRectOffset, topY + entityRectOffset, ENTITY_WIDTH, ENTITY_WIDTH);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        if (entity instanceof Entity) {
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font(20));
            gc.fillText(
                    ((Entity) entity).getId().toString(),
                    leftX + CELL_WIDTH / 2,
                    topY + CELL_WIDTH / 2
            );
        }

        gc.restore();
    }

    private static void drawInitialEntityState(Object entity, G2DEntityState state, GraphicsContext gc) {
        gc.save();
        int leftX = CELL_WIDTH * state.getCol();
        int topY = CELL_WIDTH * state.getRow();
        /*gc.beginPath();
        gc.rect(leftX, topY, leftX + CELL_WIDTH, topY + CELL_WIDTH);
        gc.closePath();
        gc.clip();*/

        gc.setFill(Color.ORANGE);
        int entityRectOffset = (CELL_WIDTH - ENTITY_WIDTH) / 2;
        gc.fillRect(leftX + entityRectOffset, topY + entityRectOffset, ENTITY_WIDTH, ENTITY_WIDTH);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        if (entity instanceof Entity) {
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font(20));
            gc.fillText(
                    ((Entity) entity).getId().toString(),
                    leftX + CELL_WIDTH / 2,
                    topY + CELL_WIDTH / 2
            );
        }

        gc.restore();
    }

    private void debugFillCanvas(GraphicsContext gc) {
        canvas.getGraphicsContext2D().setFill(Color.GREEN);
        canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

}
