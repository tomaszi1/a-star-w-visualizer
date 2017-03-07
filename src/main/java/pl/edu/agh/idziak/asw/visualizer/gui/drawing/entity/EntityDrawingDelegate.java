package pl.edu.agh.idziak.asw.visualizer.gui.drawing.entity;

import com.google.common.collect.Iterables;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import pl.edu.agh.idziak.asw.impl.grid2d.G2DEntityState;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.DrawConstants;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.GridParams;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.Entity;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.TestCase;

import java.util.Iterator;
import java.util.Map;

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

    public void drawEntities(GraphicsContext gc, TestCase testCase) {
        gc.save();
        newColorIterator();

        Map<?, G2DEntityState> initialStates = testCase.getInputPlan().getInitialCollectiveState().getEntityStates();
        Map<?, G2DEntityState> targetStates = testCase.getInputPlan().getTargetCollectiveState().getEntityStates();

        for (Map.Entry<?, G2DEntityState> entry : initialStates.entrySet()) {
            Object entity = entry.getKey();

            Color color = colorIterator.next();
            gc.setStroke(color);
            gc.setFill(color);
            drawInitialEntityState(entity, entry.getValue(), gc);
            drawTargetEntityState(targetStates.get(entity), gc);
        }

        gc.restore();
    }

    private void drawTargetEntityState(G2DEntityState state, GraphicsContext gc) {
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

    private void drawInitialEntityState(Object entity, G2DEntityState state, GraphicsContext gc) {
        gc.save();
        int leftX = getCellWidth() * state.getCol();
        int topY = getCellWidth() * state.getRow();

        double entityRectOffset = (getCellWidth() - getEntityWidth()) / 2;
        gc.fillRect(leftX + entityRectOffset, topY + entityRectOffset, getEntityWidth(), getEntityWidth());

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        if (entity instanceof Entity) {
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font(20));
            gc.fillText(
                    ((Entity) entity).getId().toString(),
                    leftX + getCellWidth() / 2,
                    topY + getCellWidth() / 2
            );
        }

        gc.restore();
    }

    private double getEntityWidth() {
        return gridParams.getEntityWidth();
    }


}
