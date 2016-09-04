package pl.edu.agh.idziak.asw.visualizer.gui.root;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

/**
 * Created by Tomasz on 28.08.2016.
 */
public class SpacePane extends Pane {
    private Canvas canvas = new Canvas();

    public SpacePane() {
        getChildren().add(canvas);
    }

    @Override
    protected void layoutChildren() {
        final int top = (int) snappedTopInset();
        final int right = (int) snappedRightInset();
        final int bottom = (int) snappedBottomInset();
        final int left = (int) snappedLeftInset();
        final int w = (int) getWidth() - left - right;
        final int h = (int) getHeight() - top - bottom;
        canvas.setLayoutX(left);
        canvas.setLayoutY(top);
        if (w != canvas.getWidth() || h != canvas.getHeight()) {
            canvas.setWidth(w);
            canvas.setHeight(h);
        }
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
