package pl.edu.agh.idziak.asw.visualizer.gui.drawing;

import javafx.scene.canvas.GraphicsContext;

/**
 * Created by Tomasz on 01.10.2016.
 */
public class DevZoneCellBackslashStripeDrawer extends DevZoneCellDrawer {


    @Override
    protected void safelyDrawCell(GraphicsContext gc) {
        int linesInBetween = 4;
        double spaceBetweenLines = (double) (getRight() - getLeft()) / linesInBetween;

        gc.setStroke(getColor());
        gc.setLineWidth(0.5);
        for (int i = 0; i < linesInBetween; i++) {
            double y1 = getTop() + spaceBetweenLines * (i + 1);
            double x2 = getRight() - spaceBetweenLines * (i + 1);
            gc.strokeLine(getLeft(), y1, x2, getBottom());
        }

        gc.strokeLine(getLeft(), getTop(), getRight(), getBottom());

        for (int i = 0; i < linesInBetween; i++) {
            double y2 = getBottom() - spaceBetweenLines * (i + 1);
            double x1 = getLeft() + spaceBetweenLines * (i + 1);
            gc.strokeLine(x1, getTop(), getRight(), y2);
        }
    }


}
