package pl.edu.agh.idziak.asw.visualizer.gui.drawing.entity;

import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveState;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveStateSpace;
import pl.edu.agh.idziak.asw.impl.grid2d.GridInputPlan;
import pl.edu.agh.idziak.asw.model.ASWOutputPlan;
import pl.edu.agh.idziak.asw.visualizer.gui.root.GraphicsWrapper;

public interface PathDrawingDelegate {
    void drawPaths(GraphicsWrapper gw, GridInputPlan inputPlan, ASWOutputPlan<GridCollectiveStateSpace, GridCollectiveState> outputPlan);
}
