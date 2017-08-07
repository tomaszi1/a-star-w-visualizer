package pl.edu.agh.idziak.asw.visualizer.gui.root;

import javafx.scene.canvas.GraphicsContext;

import java.awt.*;

public class GraphicsWrapper {

    private Graphics2D swingGraphics;
    private GraphicsContext javafxGraphics;

    public GraphicsWrapper(Graphics2D swingGraphics) {
        this.swingGraphics = swingGraphics;
    }

    public GraphicsWrapper(GraphicsContext javafxGraphics) {
        this.javafxGraphics = javafxGraphics;
    }

    public Graphics2D getSwingGraphics() {
        return swingGraphics;
    }

    public GraphicsContext getJavafxGraphics() {
        return javafxGraphics;
    }

    public boolean isSwingGraphics() {
        return swingGraphics != null;
    }
}
