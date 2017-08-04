package pl.edu.agh.idziak.asw.visualizer.gui.drawing.entity;

import javafx.scene.canvas.GraphicsContext;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveState;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveStateSpace;
import pl.edu.agh.idziak.asw.impl.grid2d.GridInputPlan;
import pl.edu.agh.idziak.asw.model.ASWOutputPlan;

public interface PathDrawingDelegate {
    void drawPaths(GridInputPlan inputPlan, ASWOutputPlan<GridCollectiveStateSpace, GridCollectiveState> outputPlan, GraphicsContext gc);
}
