package pl.edu.agh.idziak.asw.visualizer.gui.drawing.entity;

import javafx.scene.canvas.GraphicsContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveState;
import pl.edu.agh.idziak.asw.impl.grid2d.GridDeviationSubspace;
import pl.edu.agh.idziak.asw.impl.grid2d.GridEntityState;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.GridParams;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.subspace.SubspaceCellDrawingDelegate;
import pl.edu.agh.idziak.asw.wavefront.DeviationSubspacePlan;

import java.awt.*;
import java.util.List;

import static pl.edu.agh.idziak.asw.visualizer.gui.drawing.DrawingUtils.clipRect;

public class DeviationSubspaceDrawingDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(DeviationSubspaceDrawingDelegate.class);
    private SubspaceCellDrawingDelegate subspaceCellDrawingDelegate;
    private GridParams gridParams;

    public DeviationSubspaceDrawingDelegate( GridParams gridParams) {
        this.subspaceCellDrawingDelegate = new SubspaceCellDrawingDelegate();
        this.gridParams = gridParams;
    }

    public void drawSubspacePlans(GraphicsContext gc, List<DeviationSubspacePlan<GridCollectiveState>> plans) {
        subspaceCellDrawingDelegate.resetState();
        plans.forEach(plan -> drawSingleDevSubspacePlan(gc, plan));
    }

    public void drawSubspacePlans(Graphics2D gc, List<DeviationSubspacePlan<GridCollectiveState>> plans) {
        subspaceCellDrawingDelegate.resetState();
        plans.forEach(plan -> drawSingleDevSubspacePlan(gc, plan));
    }


    public void drawSingleDevSubspacePlan(GraphicsContext gc, DeviationSubspacePlan<GridCollectiveState> devSubspacePlan) {
        if (devSubspacePlan.getDeviationSubspace() instanceof GridDeviationSubspace) {
            LOG.debug("Drawing subspace");
            ((GridDeviationSubspace) devSubspacePlan.getDeviationSubspace())
                    .getContainedEntityStates()
                    .forEach(entityState -> drawDevSubspaceSquare(gc, entityState));
        }
        subspaceCellDrawingDelegate.switchPattern();
    }

    public void drawSingleDevSubspacePlan(Graphics2D gc, DeviationSubspacePlan<GridCollectiveState> devSubspacePlan) {
        if (devSubspacePlan.getDeviationSubspace() instanceof GridDeviationSubspace) {
            LOG.debug("Drawing subspace");
            ((GridDeviationSubspace) devSubspacePlan.getDeviationSubspace())
                    .getContainedEntityStates()
                    .forEach(entityState -> drawDevSubspaceSquare(gc, entityState));
        }
        subspaceCellDrawingDelegate.switchPattern();
    }

    private void drawDevSubspaceSquare(GraphicsContext gc, GridEntityState state) {
        gc.save();
        int topY = gridParams.getTopPosForIndex(state.getRow());
        int bottomY = gridParams.getTopPosForIndex(state.getRow() + 1);
        int leftX = gridParams.getTopPosForIndex(state.getCol());
        int rightX = gridParams.getTopPosForIndex(state.getCol() + 1);

        clipRect(gc, leftX + 1, topY - 1, gridParams.getCellWidth() - 2, gridParams.getCellWidth() - 2);

        subspaceCellDrawingDelegate.setCellBounds(topY, leftX, bottomY, rightX);
        subspaceCellDrawingDelegate.drawSubspaceCell(gc);
        gc.restore();
    }
    private void drawDevSubspaceSquare(Graphics2D gc, GridEntityState state) {
        int topY = gridParams.getTopPosForIndex(state.getRow());
        int bottomY = gridParams.getTopPosForIndex(state.getRow() + 1);
        int leftX = gridParams.getTopPosForIndex(state.getCol());
        int rightX = gridParams.getTopPosForIndex(state.getCol() + 1);

        gc.setClip(leftX, topY, gridParams.getCellWidth(), gridParams.getCellWidth());

        subspaceCellDrawingDelegate.setCellBounds(topY, leftX, bottomY, rightX);
        subspaceCellDrawingDelegate.drawSubspaceCell(gc);
    }
}
