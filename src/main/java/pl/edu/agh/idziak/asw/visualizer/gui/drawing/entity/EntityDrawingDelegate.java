package pl.edu.agh.idziak.asw.visualizer.gui.drawing.entity;

import com.google.common.collect.Iterables;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveState;
import pl.edu.agh.idziak.asw.impl.grid2d.GridEntityState;
import pl.edu.agh.idziak.asw.impl.grid2d.GridInputPlan;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.DrawConstants;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.GridParams;
import pl.edu.agh.idziak.asw.visualizer.gui.root.GraphicsWrapper;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.Entity;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.TestCase;

import java.util.Iterator;

/**
 * Created by Tomasz on 23.02.2017.
 */
public class EntityDrawingDelegate {

    private Iterator<Color> colorIterator;

    private void newColorIterator() {
        colorIterator = Iterables.cycle(DrawConstants.COLORS).iterator();
    }

    private GridParams gridParams;

    public EntityDrawingDelegate(GridParams gridParams) {
        this.gridParams = gridParams;
        newColorIterator();
    }

    public void drawEntities(GraphicsWrapper gc, TestCase testCase) {
        newColorIterator();
        GridInputPlan inputPlan = testCase.getInputPlan();

        GridCollectiveState currentState;
        if (testCase.getActiveSimulation() == null) {
            currentState = inputPlan.getInitialCollectiveState();
        } else {
            currentState = testCase.getActiveSimulation().getCurrentState();
        }
        GridCollectiveState targetState = inputPlan.getTargetCollectiveState();

        for (Object entity : inputPlan.getEntities()) {
            Color color = colorIterator.next();

            GridEntityState currentEntityState = inputPlan.getStateForEntity(currentState, entity);
            GridEntityState targetEntityState = inputPlan.getStateForEntity(targetState, entity);

            drawInitialEntityState(gc, currentEntityState, entity);
            // drawTargetEntityState(targetEntityState, gc);
        }

    }

    private void drawTargetEntityState(GridEntityState state, GraphicsContext gc) {
        gc.save();
        int leftX = getTopPosForIndex(state.getCol());
        int topY = getTopPosForIndex(state.getRow());

        gc.setLineWidth(4.0);

        double entityRectOffset = (getCellWidth() - getEntityTargetWidth()) / 2;
        gc.strokeRect(leftX + entityRectOffset, topY + entityRectOffset, getEntityTargetWidth(), getEntityTargetWidth());

        gc.restore();
    }

    private double getEntityTargetWidth() {
        return gridParams.getEntityTargetWidth();
    }

    private int getCellWidth() {
        return gridParams.getCellWidth();
    }

    private int getTopPosForIndex(Integer col) {
        return gridParams.getTopPosForIndex(col);
    }

    private void drawInitialEntityState(GraphicsWrapper gw, GridEntityState state, Object entity) {

        if (gw.isSwingGraphics()) {

        } else {
            GraphicsContext gc = gw.getJavafxGraphics();
            gc.save();
            // gc.setStroke(color);
            // gc.setFill(color);
            int leftX = getCellWidth() * state.getCol();
            int topY = getCellWidth() * state.getRow();

            double entityRectOffset = (getCellWidth() - getEntityWidth()) / 2;
            gc.fillRect(leftX + entityRectOffset, topY + entityRectOffset, getEntityWidth(), getEntityWidth());

            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);

            if (entity instanceof Entity) {
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font(DrawConstants.ENTITY_ID_FONT_SIZE));
                gc.fillText(
                        ((Entity) entity).getLetter(),
                        leftX + getCellWidth() / 2,
                        topY + getCellWidth() / 2
                );
            }

            gc.restore();
        }
    }

    private double getEntityWidth() {
        return gridParams.getEntityWidth();
    }


}
