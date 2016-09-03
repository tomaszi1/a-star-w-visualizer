package edu.agh.idziak.asw.visualizer.gui.root;

import edu.agh.idziak.asw.grid2d.G2DStateSpace;
import edu.agh.idziak.asw.visualizer.testing.grid2d.model.TestCase;
import javafx.beans.value.ObservableObjectValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

/**
 * Created by Tomasz on 28.08.2016.
 */
public class CanvasController {

    private Canvas canvas;
    private TestCase lastTestCase;

    public CanvasController(Canvas canvas, AnchorPane canvasContainer, ObservableObjectValue<TestCase>
            testCaseObjectProperty) {
        this.canvas = canvas;
        canvasContainer.heightProperty().addListener((observable, oldValue, newValue) -> {
            drawTestCase(lastTestCase);
            canvas.setWidth(newValue.doubleValue());
        });
        canvasContainer.widthProperty().addListener((observable, oldValue, newValue) -> {
            drawTestCase(lastTestCase);
            canvas.setHeight(newValue.doubleValue());
        });
        testCaseObjectProperty.addListener((observable, oldValue, newValue) -> drawTestCase(newValue));
    }

    private void drawTestCase(TestCase testCase) {
        lastTestCase = testCase;
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.getGraphicsContext2D().setFill(Color.GREEN);
        canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (testCase == null) {
            return;
        }

        G2DStateSpace stateSpace = testCase.getInputPlan().getStateSpace();
        int rows = stateSpace.getRows();
        int cols = stateSpace.getCols();

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);

        for (int curRow = 0; curRow < rows; curRow++) {
            int yPos = curRow * 20;
            gc.strokeLine(0, yPos, cols * 20, yPos);
        }

        for (int curCol = 0; curCol < cols; curCol++) {
            int xPos = curCol * 20;
            gc.strokeLine(xPos, 0, xPos, rows * 20);
        }

    }

}
