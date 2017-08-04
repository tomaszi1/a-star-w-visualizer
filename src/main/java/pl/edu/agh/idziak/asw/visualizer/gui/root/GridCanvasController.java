package pl.edu.agh.idziak.asw.visualizer.gui.root;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.idziak.asw.impl.ExtendedOutputPlan;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveState;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveStateSpace;
import pl.edu.agh.idziak.asw.impl.grid2d.GridDeviationSubspace;
import pl.edu.agh.idziak.asw.impl.grid2d.GridEntityState;
import pl.edu.agh.idziak.asw.model.CollectivePath;
import pl.edu.agh.idziak.asw.visualizer.GlobalEventBus;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.DrawConstants;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.GridParams;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.entity.EntityDrawingDelegate;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.entity.PathDrawingDelegate;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.entity.PathDrawingDelegateImpl;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.simulation.NewSimulationEvent;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.simulation.Simulation;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.simulation.SimulationStateChangedEvent;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.subspace.SubspaceCellDrawingDelegate;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.TestCase;
import pl.edu.agh.idziak.asw.wavefront.DeviationSubspacePlan;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

/**
 * Created by Tomasz on 28.08.2016.
 */
public class GridCanvasController {

    private static final Logger LOG = LoggerFactory.getLogger(GridCanvasController.class);

    private Canvas canvas;
    private CanvasMouseEventsDispatcher canvasMouseEventsDispatcher;
    private TestCase currentTestCase;

    private GridParams gridParams = new GridParams();

    private final ChangeListener<ExtendedOutputPlan<GridCollectiveStateSpace, GridCollectiveState>> outputPlanChangeListener
            = (obs, oldVal, newVal) -> repaint();

    private PathDrawingDelegate pathDrawingDelegate;
    private EntityDrawingDelegate entityDrawingDelegate;
    private SubspaceCellDrawingDelegate subspaceCellDrawingDelegate;

    public GridCanvasController(Canvas canvas, ObservableObjectValue<TestCase>
            testCaseObjectProperty, CanvasMouseEventsDispatcher canvasMouseEventsDispatcher) {
        this.canvas = canvas;
        this.canvasMouseEventsDispatcher = canvasMouseEventsDispatcher;
        this.subspaceCellDrawingDelegate = new SubspaceCellDrawingDelegate();
        this.entityDrawingDelegate = new EntityDrawingDelegate(gridParams);
        this.pathDrawingDelegate = new PathDrawingDelegateImpl(gridParams);

        testCaseObjectProperty.addListener((observable, oldValue, newTestCase) -> {
            currentTestCase = newTestCase;
            drawCurrentTestCase();
        });
        GlobalEventBus.INSTANCE.get().register(new NewSimulationSubscriber());

        canvas.setOnMouseClicked(this::handleCanvasClicked);
    }

    public class NewSimulationSubscriber {

        @Subscribe
        public void newSimulation(NewSimulationEvent newSimulationEvent) {
            redrawIfSimulationForCurrentTestCase(newSimulationEvent.getSimulation());
        }

        @Subscribe
        public void simulationChanged(SimulationStateChangedEvent simulationStateChangedEvent) {
            redrawIfSimulationForCurrentTestCase(simulationStateChangedEvent.getSimulation());
        }

        private void redrawIfSimulationForCurrentTestCase(Simulation simulation) {
            if (currentTestCase.getActiveSimulation() == simulation) {
                drawCurrentTestCase();
            }
        }
    }

    private void handleCanvasClicked(MouseEvent event) {
        int colIndex = getIndexForPosition((int) event.getX());
        int rowIndex = getIndexForPosition((int) event.getY());
        canvasMouseEventsDispatcher.cellClicked(rowIndex, colIndex);
    }

    private void repaint() {
        drawCurrentTestCase();
    }

    private void drawCurrentTestCase() {
        LOG.info("Redrawing test case");
        GraphicsContext gc = canvas.getGraphicsContext2D();

        if (currentTestCase == null) {
            return;
        }

        drawStateSpace(gc, currentTestCase.getInputPlan().getStateSpace());
        drawDeviationZones(gc);
        drawPaths(gc);
        entityDrawingDelegate.drawEntities(gc, currentTestCase);
    }

    private void drawDeviationZones(GraphicsContext gc) {
        subspaceCellDrawingDelegate.resetState();

        if (currentTestCase.getActiveSimulation() == null) {
            return;
        }
        currentTestCase.getActiveSimulation()
                .getOutputPlan()
                .getDeviationSubspacePlans()
                .stream()
                .filter(plan -> plan.getDeviationSubspace() instanceof GridDeviationSubspace)
                .sorted(comparing(this::getSortingKeyForDevSubspacePlan))
                .forEachOrdered(devSubspacePlan -> drawSingleDevSubspacePlan(gc, devSubspacePlan));
    }

    private String getSortingKeyForDevSubspacePlan(DeviationSubspacePlan<GridCollectiveState> plan) {
        checkArgument(GridDeviationSubspace.class.isInstance(plan.getDeviationSubspace()));
        return ((GridDeviationSubspace) plan.getDeviationSubspace()).getContainedEntityStates()
                .stream()
                .sorted(comparing(GridEntityState::toString))
                .collect(toList())
                .toString();
    }

    private void drawSingleDevSubspacePlan(GraphicsContext gc, DeviationSubspacePlan<GridCollectiveState> devSubspacePlan) {
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
        int topY = getTopPosForIndex(state.getRow());
        int bottomY = getTopPosForIndex(state.getRow() + 1);
        int leftX = getTopPosForIndex(state.getCol());
        int rightX = getTopPosForIndex(state.getCol() + 1);

        clipRect(gc, leftX + 1, topY - 1, getCellWidth() - 2, getCellWidth() - 2);

        subspaceCellDrawingDelegate.setCellBounds(topY, leftX, bottomY, rightX);
        subspaceCellDrawingDelegate.drawDeviationSubspace(gc);
        gc.restore();
    }

    private static void clipRect(GraphicsContext gc, int x, int y, int width, int height) {
        gc.beginPath();
        gc.rect(x, y, x + width, y + height);
        gc.closePath();
        gc.clip();
    }

    private void drawStateSpace(GraphicsContext gc, GridCollectiveStateSpace stateSpace) {
        int rows = stateSpace.getRows();
        int cols = stateSpace.getCols();

        canvas.setWidth(getCellWidth() * cols);
        canvas.setHeight(getCellWidth() * rows);
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawGrid(gc, rows, cols);

        drawObstacles(gc, stateSpace);
    }

    private void drawGrid(GraphicsContext gc, int rows, int cols) {
        gc.save();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);

        for (int curRow = 0; curRow <= rows; curRow++) {
            int yPos = curRow * getCellWidth();
            gc.strokeLine(0, yPos, cols * getCellWidth(), yPos);
        }

        for (int curCol = 0; curCol <= cols; curCol++) {
            int xPos = curCol * getCellWidth();
            gc.strokeLine(xPos, 0, xPos, rows * getCellWidth());
        }
        gc.restore();
    }

    private void drawObstacles(GraphicsContext gc, GridCollectiveStateSpace stateSpace) {
        gc.save();
        gc.setFill(DrawConstants.OBSTACLE_COLOR);

        int[][] gridArray = stateSpace.getGridIntegerArray();

        int obstacleWidth = getObstacleWidth();
        int offset = (getCellWidth() - obstacleWidth) / 2;

        for (int i = 0; i < gridArray.length; i++) {
            for (int j = 0; j < gridArray[i].length; j++) {
                if (gridArray[i][j] > 0) {
                    int yPos = getTopPosForIndex(i) + offset;
                    int xPos = getTopPosForIndex(j) + offset;
                    gc.fillRect(xPos, yPos, obstacleWidth, obstacleWidth);
                }
            }
        }
        gc.restore();
    }

    private void drawPaths(GraphicsContext gc) {
        Simulation activeSimulation = currentTestCase.getActiveSimulation();
        if (activeSimulation == null) {
            return;
        }
        if (activeSimulation.isReset()) {
            CollectivePath<GridCollectiveState> collectivePath = activeSimulation.getOutputPlan().getCollectivePath();
            if (collectivePath != null) {
                pathDrawingDelegate.drawPaths(activeSimulation.getInputPlan(), activeSimulation.getOutputPlan(), gc);
            } else {
                LOG.info("No path to draw");
            }
        } else {
            GridCollectiveState nextState = activeSimulation.getEffectiveNextState();
            if (nextState != null) {
                ImmutableList<GridCollectiveState> nextStepPathFragment = ImmutableList.of(
                        activeSimulation.getCurrentState(),
                        nextState
                );
            }
        }
    }

    private int getTopPosForIndex(int index) {
        return gridParams.getTopPosForIndex(index);
    }

    private int getCellWidth() {
        return gridParams.getCellWidth();
    }

    private int getObstacleWidth() {
        return gridParams.getObstacleWidth();
    }

    private int getIndexForPosition(int pos) {
        return pos / gridParams.getCellWidth();
    }

    public void scaleDown() {
        gridParams.scaleDown();
        repaint();
    }

    public void scaleUp() {
        gridParams.scaleUp();
        repaint();
    }

    public WritableImage snapshotCanvas() {
        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        return canvas.snapshot(null, writableImage);
    }
}
