package pl.edu.agh.idziak.asw.visualizer.gui.drawing;

/**
 * Created by Tomasz on 23.02.2017.
 */
public class GridParams {

    private double scaleFactor = 1;

    public void scaleUp() {
        scaleFactor = scaleFactor * 5 / 4;
    }

    public void scaleDown() {
        if (getCellWidth() < 10) {
            return;
        }
        scaleFactor = scaleFactor * 4 / 5;
    }

    public int getTopPosForIndex(int index) {
        return index * getCellWidth();
    }

    public int getCellWidth() {
        return (int) (DrawConstants.NORMAL_CELL_WIDTH * scaleFactor);
    }

    public double getEntityTargetWidth() {
        return getCellWidth() * 4 / 5;
    }

    public double getEntityWidth() {
        return getCellWidth() * 2 / 3;
    }

    public int getCenterPosForIndex(int pos) {
        return getTopPosForIndex(pos) + getCellWidth() / 2;
    }

    public int getObstacleWidth() {
        return (int) (getCellWidth() * DrawConstants.OBSTACLE_TO_CELL_WIDTH_RATIO);
    }

    public double getStopDotSize() {
        return DrawConstants.ENTITY_STOP_DOT_SIZE * scaleFactor;
    }

    public double getPathPointCircleDiameter(int overlaps) {
        if (overlaps <= 1) {
            return getCellWidth() * 0.6;
        }
        if (overlaps <= 2) {
            return getCellWidth() * 0.5;
        }
        if (overlaps <= 4) {
            return getCellWidth() * 0.4;
        }
        if (overlaps <= 9) {
            return getCellWidth() * 0.3;
        }
        throw new IllegalStateException();
    }
}
