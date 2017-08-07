package pl.edu.agh.idziak.asw.visualizer.gui.drawing.subspace;

import javafx.scene.canvas.GraphicsContext;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.DrawConstants;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Created by Tomasz on 01.10.2016.
 */
public class SubspaceCellHorizontalStripeDrawer extends AbstractSubspaceCellDrawer {

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

    @Override
    protected void safelyDrawCell(Graphics2D gc) {
        double spaceBetweenLines = (double) (getRight() - getLeft()) / LINES_IN_BETWEEN;

        gc.setColor(getAwtColor());
        gc.setStroke(new BasicStroke(1f));

        for (int i = 1; i < LINES_IN_BETWEEN; i++) {
            double y = getTop() + spaceBetweenLines * i;
            gc.draw(new Line2D.Double(getLeft(), y, getRight(), y));
        }
    }

}
