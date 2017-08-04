package pl.edu.agh.idziak.asw.visualizer.gui.drawing.subspace;

import javafx.scene.canvas.GraphicsContext;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.DrawConstants;

/**
 * Created by Tomasz on 01.10.2016.
 */
public class SubspaceCellHorizontalStripeDrawer extends SubspaceCellDrawer {

    private static final int LINES_IN_BETWEEN = 8;

    @Override
    protected void safelyDrawCell(GraphicsContext gc) {
        double spaceBetweenLines = (double) (getRight() - getLeft()) / LINES_IN_BETWEEN;

        gc.setStroke(getColor());
        gc.setLineWidth(DrawConstants.SUBSPACE_LINES_WIDTH);

        for (int i = 1; i < LINES_IN_BETWEEN; i++) {
            double y = getTop() + spaceBetweenLines * i;
            gc.strokeLine(getLeft(), y, getRight(), y);
        }
    }

}
