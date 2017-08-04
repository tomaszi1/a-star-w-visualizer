package pl.edu.agh.idziak.asw.visualizer.gui.drawing.subspace;

import javafx.scene.canvas.GraphicsContext;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.DrawConstants;

/**
 * Created by Tomasz on 01.10.2016.
 */
public class SubspaceCellSlashStripeDrawer extends SubspaceCellDrawer {

    private static final int LINES_IN_BETWEEN = 6;

    @Override
    protected void safelyDrawCell(GraphicsContext gc) {
        double spaceBetweenLines = (double) (getRight() - getLeft()) / LINES_IN_BETWEEN;

        gc.setStroke(getColor());
        gc.setLineDashes(5d, 5d);
        gc.setLineWidth(DrawConstants.SUBSPACE_LINES_WIDTH);
        for (int i = 0; i < LINES_IN_BETWEEN; i++) {
            double y1 = getTop() + spaceBetweenLines * (i + 1);
            double x2 = getLeft() + spaceBetweenLines * (i + 1);
            gc.strokeLine(getLeft(), y1, x2, getTop());
        }

        for (int i = 0; i < LINES_IN_BETWEEN; i++) {
            double y2 = getTop() + spaceBetweenLines * (i + 1);
            double x1 = getLeft() + spaceBetweenLines * (i + 1);
            gc.strokeLine(x1, getBottom(), getRight(), y2);
        }
    }


}
