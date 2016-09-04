package pl.edu.agh.idziak.asw.visualizer.gui.root;

import javafx.scene.canvas.Canvas;

/**
 * Created by Tomasz on 28.08.2016.
 */
public class ResizableCanvas extends Canvas {

    public ResizableCanvas() {
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    @Override
    public void resize(double width, double height)
    {
        super.setWidth(width);
        super.setHeight(height);
    }
}
