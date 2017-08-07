package pl.edu.agh.idziak.asw.visualizer.gui.drawing.subspace;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.DrawingUtils;

import java.awt.*;

/**
 * Created by Tomasz on 01.10.2016.
 */
public abstract class AbstractSubspaceCellDrawer {
    private int top;
    private int bottom;
    private int left;
    private int right;
    private Color color;

    public final void drawCell(GraphicsContext gc) {
        gc.save();
        this.safelyDrawCell(gc);
        gc.restore();
    }

    protected abstract void safelyDrawCell(GraphicsContext gc);

    public final void setBounds(int top, int left, int bottom, int right) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getTop() {
        return top;
    }

    public int getBottom() {
        return bottom;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public Color getColor() {
        return color.deriveColor(1d, 1d, 1d, 1d);
    }

    public java.awt.Color getAwtColor() {
        return DrawingUtils.toAwtColor(getColor());
    }

    public void drawCell(Graphics2D gc) {
        gc.setClip(getLeft(), getTop(), getRight() - getLeft(), getBottom() - getTop());
        safelyDrawCell(gc);
    }

    protected abstract void safelyDrawCell(Graphics2D gc);
}
