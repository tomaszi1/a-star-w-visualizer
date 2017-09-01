package pl.edu.agh.idziak.asw.visualizer.gui.drawing.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveStateSpace;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.DrawConstants;
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
        gc.setLineWidth(2);

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

        g.setStroke(new BasicStroke(1.5f));

        for (int curRow = 0; curRow <= rows; curRow++) {
            int yPos = gridParams.getTopPosForIndex(curRow);
            g.draw(new Line2D.Double(gridParams.getTopPosForIndex(0), yPos, gridParams.getTopPosForIndex(cols), yPos));
        }

        for (int curCol = 0; curCol <= cols; curCol++) {
            int xPos = gridParams.getTopPosForIndex(curCol);
            g.draw(new Line2D.Double(xPos, gridParams.getTopPosForIndex(0), xPos, gridParams.getTopPosForIndex(rows)));
        }

        drawNumbers(g, rows, cols);
    }

    private void drawNumbers(Graphics2D g, int rows, int cols) {

        g.setFont(DrawConstants.DEFAULT_FONT.deriveFont(24f));
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        float x = (float) (gridParams.getLabelCenterOffset() - metrics.stringWidth("1") / 2);
        for (int i = 0; i < rows; i++) {
            float y = (float) (gridParams.getTopPosForIndex(i)
                    + gridParams.getCellWidth() / 2
                    - metrics.getHeight() / 2
                    + metrics.getAscent());
            g.drawString(String.valueOf(i + 1), x, y);
        }

        float y = (float) (gridParams.getLabelCenterOffset()
                - metrics.getHeight() / 2
                + metrics.getAscent());

        for (int i = 0; i < cols; i++) {
            x = (float) (gridParams.getTopPosForIndex(i)
                    + gridParams.getCellWidth() / 2
                    - metrics.stringWidth("1") / 2);
            g.drawString(String.valueOf(i + 1), x, y);
        }
    }

}
