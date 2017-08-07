package pl.edu.agh.idziak.asw.visualizer.gui.drawing.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveStateSpace;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.GridParams;

import java.awt.*;
import java.awt.geom.Line2D;

public class GridDrawingDelegate {

    private GridParams gridParams;

    public GridDrawingDelegate(GridParams gridParams) {
        this.gridParams = gridParams;
    }

    public void drawGrid(GraphicsContext gc, GridCollectiveStateSpace stateSpace) {
        int rows = stateSpace.getRows();
        int cols = stateSpace.getCols();
        gc.save();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);

        for (int curRow = 0; curRow <= rows; curRow++) {
            int yPos = curRow * gridParams.getCellWidth();
            gc.strokeLine(0, yPos, cols * gridParams.getCellWidth(), yPos);
        }

        for (int curCol = 0; curCol <= cols; curCol++) {
            int xPos = curCol * gridParams.getCellWidth();
            gc.strokeLine(xPos, 0, xPos, rows * gridParams.getCellWidth());
        }
        gc.restore();
    }

    public void drawGrid(Graphics2D g, GridCollectiveStateSpace stateSpace) {
        int rows = stateSpace.getRows();
        int cols = stateSpace.getCols();

        g.setStroke(new BasicStroke(1f));

        for (int curRow = 0; curRow <= rows; curRow++) {
            int yPos = curRow * gridParams.getCellWidth();
            g.draw(new Line2D.Double(0, yPos, cols * gridParams.getCellWidth(), yPos));
        }

        for (int curCol = 0; curCol <= cols; curCol++) {
            int xPos = curCol * gridParams.getCellWidth();
            g.draw(new Line2D.Double(xPos, 0, xPos, rows * gridParams.getCellWidth()));
        }
    }

}
