package pl.edu.agh.idziak.asw.visualizer.gui.drawing;

/**
 * Created by Tomasz on 23.02.2017.
 */
public class GridParams {

    private static final int LABEL_SPACE_OFFSET = 30;
    private double scaleFactor = 1;
    private int deviationSubspaceMinOrder = 0;
    private int deviationSubspaceMaxOrder = 1000;

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
        return index * getCellWidth() + ((int) (LABEL_SPACE_OFFSET * scaleFactor));
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

    public int getDeviationSubspaceMinOrder(){
        return deviationSubspaceMinOrder;
    }

    public double getPathPointCircleDiameter(int overlaps) {
        if (overlaps <= 2) {
            return getCellWidth() * 0.4;
        }
        if (overlaps <= 4) {
            return getCellWidth() * 0.4;
        }
        if (overlaps <= 9) {
            return getCellWidth() * 0.33;
        }
        if (overlaps <= 12) {
            return getCellWidth() * 0.25;
        }
        throw new IllegalStateException();
    }


    public int getLabelCenterOffset() {
        return LABEL_SPACE_OFFSET / 2;
    }

    public int getDeviationSubspaceMaxOrder() {
        return deviationSubspaceMaxOrder;
    }

    public void setDeviationSubspaceMinOrder(int deviationSubspaceMinOrder) {
        this.deviationSubspaceMinOrder = deviationSubspaceMinOrder;
    }

    public void setDeviationSubspaceMaxOrder(int deviationSubspaceMaxOrder) {
        this.deviationSubspaceMaxOrder = deviationSubspaceMaxOrder;
    }
}
