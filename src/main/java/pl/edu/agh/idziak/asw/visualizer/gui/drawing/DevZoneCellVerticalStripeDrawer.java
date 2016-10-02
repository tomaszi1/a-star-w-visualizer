package pl.edu.agh.idziak.asw.visualizer.gui.drawing;

import javafx.scene.canvas.GraphicsContext;

/**
 * Created by Tomasz on 01.10.2016.
 */
public class DevZoneCellVerticalStripeDrawer extends DevZoneCellDrawer {

    @Override
    protected void safelyDrawCell(GraphicsContext gc) {
        int linesInBetween = 6;
        double spaceBetweenLines = (double) (getRight() - getLeft()) / linesInBetween;

        gc.setStroke(getColor());
        gc.setLineWidth(0.5);

        for (int i = 1; i < linesInBetween; i++) {
            double x = getLeft() + spaceBetweenLines * i;
            gc.strokeLine(x, getTop(), x, getBottom());
        }
    }


}
