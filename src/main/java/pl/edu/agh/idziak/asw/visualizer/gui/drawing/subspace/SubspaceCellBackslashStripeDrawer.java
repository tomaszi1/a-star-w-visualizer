package pl.edu.agh.idziak.asw.visualizer.gui.drawing.subspace;

import javafx.scene.canvas.GraphicsContext;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.DrawConstants;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Created by Tomasz on 01.10.2016.
 */
public class SubspaceCellBackslashStripeDrawer extends AbstractSubspaceCellDrawer {

    private static final int LINES_IN_BETWEEN = 6;

    @Override
    protected void safelyDrawCell(GraphicsContext gc) {
        double spaceBetweenLines = (double) (getRight() - getLeft()) / LINES_IN_BETWEEN;

        gc.setStroke(getColor());
        gc.setLineDashes(10d, 10d);
        gc.setLineWidth(DrawConstants.SUBSPACE_LINES_WIDTH);
        for (int i = 0; i < LINES_IN_BETWEEN; i++) {
            double y1 = getTop() + spaceBetweenLines * (i + 1);
            double x2 = getRight() - spaceBetweenLines * (i + 1);
            gc.strokeLine(getLeft(), y1, x2, getBottom());
        }

        gc.strokeLine(getLeft(), getTop(), getRight(), getBottom());

        for (int i = 0; i < LINES_IN_BETWEEN; i++) {
            double y2 = getBottom() - spaceBetweenLines * (i + 1);
            double x1 = getLeft() + spaceBetweenLines * (i + 1);
            gc.strokeLine(x1, getTop(), getRight(), y2);
        }
    }

    @Override
    protected void safelyDrawCell(Graphics2D gc) {
        double spaceBetweenLines = (double) (getRight() - getLeft()) / LINES_IN_BETWEEN;

        gc.setColor(getAwtColor());
        gc.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5f, new float[]{5f}, 0f));

        for (int i = 0; i < LINES_IN_BETWEEN; i++) {
            double y1 = getTop() + spaceBetweenLines * (i + 1);
            double x2 = getRight() - spaceBetweenLines * (i + 1);
            gc.draw(new Line2D.Double(getLeft(), y1, x2, getBottom()));
        }

        gc.draw(new Line2D.Double(getLeft(), getTop(), getRight(), getBottom()));

        for (int i = 0; i < LINES_IN_BETWEEN; i++) {
            double y2 = getBottom() - spaceBetweenLines * (i + 1);
            double x1 = getLeft() + spaceBetweenLines * (i + 1);
            gc.draw(new Line2D.Double(x1, getTop(), getRight(), y2));
        }
    }

}
