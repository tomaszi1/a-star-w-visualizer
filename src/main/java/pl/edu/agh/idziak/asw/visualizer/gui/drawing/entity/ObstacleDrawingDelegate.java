package pl.edu.agh.idziak.asw.visualizer.gui.drawing.entity;

import javafx.scene.canvas.GraphicsContext;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveStateSpace;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.DrawConstants;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.DrawingUtils;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.GridParams;
import pl.edu.agh.idziak.asw.visualizer.gui.root.GraphicsWrapper;

import java.awt.*;

public class ObstacleDrawingDelegate {
    private GridParams gridParams;

    public ObstacleDrawingDelegate(GridParams gridParams) {
        this.gridParams = gridParams;
    }


    public void drawObstacles(GraphicsWrapper gw, GridCollectiveStateSpace stateSpace) {
        if (gw.isSwingGraphics()) {
            Graphics2D g = gw.getSwingGraphics();
            drawSwing(g, stateSpace);
        } else {
            GraphicsContext gc = gw.getJavafxGraphics();
            drawJavafx(stateSpace, gc);
        }
    }

    private void drawSwing(Graphics2D g, GridCollectiveStateSpace stateSpace) {
        g.setPaint(DrawingUtils.toAwtColor(DrawConstants.OBSTACLE_COLOR));

        int[][] gridArray = stateSpace.getGridIntegerArray();

        int obstacleWidth = gridParams.getObstacleWidth();
        int offset = (gridParams.getCellWidth() - obstacleWidth) / 2;

        for (int i = 0; i < gridArray.length; i++) {
            for (int j = 0; j < gridArray[i].length; j++) {
                if (gridArray[i][j] > 0) {
                    int yPos = gridParams.getTopPosForIndex(i) + offset;
                    int xPos = gridParams.getTopPosForIndex(j) + offset;
                    g.setClip(xPos, yPos, obstacleWidth, obstacleWidth);
                    g.fillRect(xPos, yPos, obstacleWidth, obstacleWidth);
                }
            }
        }
    }

    private void drawJavafx(GridCollectiveStateSpace stateSpace, GraphicsContext gc) {
        gc.save();
        gc.setFill(DrawConstants.OBSTACLE_COLOR);

        int[][] gridArray = stateSpace.getGridIntegerArray();

        int obstacleWidth = gridParams.getObstacleWidth();
        int offset = (gridParams.getCellWidth() - obstacleWidth) / 2;

        for (int i = 0; i < gridArray.length; i++) {
            for (int j = 0; j < gridArray[i].length; j++) {
                if (gridArray[i][j] > 0) {
                    int yPos = gridParams.getTopPosForIndex(i) + offset;
                    int xPos = gridParams.getTopPosForIndex(j) + offset;
                    gc.fillRect(xPos, yPos, obstacleWidth, obstacleWidth);
                }
            }
        }
        gc.restore();
    }

}
