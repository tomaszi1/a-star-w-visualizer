package pl.edu.agh.idziak.asw.visualizer.gui.root;

import com.google.common.eventbus.Subscribe;
import javafx.beans.value.ObservableObjectValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.swing.JSVGCanvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveState;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveStateSpace;
import pl.edu.agh.idziak.asw.impl.grid2d.GridDeviationSubspace;
import pl.edu.agh.idziak.asw.impl.grid2d.GridEntityState;
import pl.edu.agh.idziak.asw.model.ASWOutputPlan;
import pl.edu.agh.idziak.asw.visualizer.GlobalEventBus;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.GridParams;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.entity.*;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.simulation.NewSimulationEvent;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.simulation.Simulation;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.simulation.SimulationStateChangedEvent;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.TestCase;
import pl.edu.agh.idziak.asw.wavefront.DeviationSubspacePlan;

import javax.swing.*;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

/**
 * Created by Tomasz on 28.08.2016.
 */
public class GridCanvasController {

    private static final Logger LOG = LoggerFactory.getLogger(GridCanvasController.class);

    private Canvas canvas;
    private JSVGCanvas svgCanvas;
    private TestCase currentTestCase;

    private GridParams gridParams = new GridParams();

    private PathDrawingDelegate pathDrawingDelegate;
    private EntityDrawingDelegate entityDrawingDelegate;
    private ObstacleDrawingDelegate obstacleDrawingDelegate;
    private GridDrawingDelegate gridDrawingDelegate;
    private DeviationSubspaceDrawingDelegate deviationSubspaceDrawingDelegate;
    private boolean svgMode;
    private SVGGraphics2D svgGraphics;

    public GridCanvasController(Canvas canvas, ObservableObjectValue<TestCase> testCaseObjectProperty) {
        this.canvas = canvas;
        this.svgCanvas = new JSVGCanvas();
        if (canvas == null) {
            svgMode = true;
        }

        // drawers
        this.entityDrawingDelegate = new EntityDrawingDelegate(gridParams);
        this.pathDrawingDelegate = new PathDrawingDelegateImpl(gridParams);
        this.obstacleDrawingDelegate = new ObstacleDrawingDelegate(gridParams);
        this.gridDrawingDelegate = new GridDrawingDelegate(gridParams);
        this.deviationSubspaceDrawingDelegate = new DeviationSubspaceDrawingDelegate(gridParams);

        // action listeners
        testCaseObjectProperty.addListener((observable, oldValue, newTestCase) -> {
            currentTestCase = newTestCase;
            drawCurrentTestCase();
        });
        GlobalEventBus.INSTANCE.get().register(new NewSimulationSubscriber());
    }

    public JSVGCanvas getSvgCanvas() {
        return svgCanvas;
    }

    public boolean isSvgMode() {
        return svgMode;
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

    private void repaint() {
        drawCurrentTestCase();
    }

    private void drawCurrentTestCase() {
        LOG.info("Redrawing test case");

        if (currentTestCase == null) {
            return;
        }

        if (svgMode) {
            SwingUtilities.invokeLater(this::drawSwingCanvas);
        } else {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            drawStateSpace(gc, currentTestCase.getInputPlan().getStateSpace());
            drawDeviationSubspaces(gc);
            drawPaths(new GraphicsWrapper(gc));
            // entityDrawingDelegate.drawEntities(gc, currentTestCase);
        }
    }

    private void drawSwingCanvas() {
        SVGDocument svgDoc = (SVGDocument) SVGDOMImplementation.getDOMImplementation().createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
        SVGGraphics2D g = new SVGGraphics2D(SVGGeneratorContext.createDefault(svgDoc), true);
        int cols = currentTestCase.getInputPlan().getStateSpace().getCols();
        int rows = currentTestCase.getInputPlan().getStateSpace().getRows();
        g.setSVGCanvasSize(new Dimension(gridParams.getTopPosForIndex(cols) + 1, gridParams.getTopPosForIndex(rows) + 1));

        drawStateSpace(g, currentTestCase.getInputPlan().getStateSpace());
        drawDeviationSubspaces(g);
        drawPaths(new GraphicsWrapper(g));

        Element root = svgDoc.getDocumentElement();
        g.getRoot(root);
        svgCanvas.setSVGDocument(svgDoc);
    }

    private void drawPaths(GraphicsWrapper g) {
        Simulation activeSimulation = currentTestCase.getActiveSimulation();
        ASWOutputPlan<GridCollectiveStateSpace, GridCollectiveState> outputPlan;
        if (activeSimulation != null) {
            outputPlan = activeSimulation.getOutputPlan();
        } else {
            outputPlan = null;
        }
        pathDrawingDelegate.drawPaths(g, currentTestCase.getInputPlan(), outputPlan);
    }

    private void drawDeviationSubspaces(Graphics2D g) {
        if (currentTestCase.getActiveSimulation() == null) {
            return;
        }

        List<DeviationSubspacePlan<GridCollectiveState>> plans = getSortedDeviationSubspaces();

        deviationSubspaceDrawingDelegate.drawSubspacePlans(g, plans);
    }

    private void drawDeviationSubspaces(GraphicsContext gc) {
        if (currentTestCase.getActiveSimulation() == null) {
            return;
        }

        List<DeviationSubspacePlan<GridCollectiveState>> plans = getSortedDeviationSubspaces();

        deviationSubspaceDrawingDelegate.drawSubspacePlans(gc, plans);
    }


    private List<DeviationSubspacePlan<GridCollectiveState>> getSortedDeviationSubspaces() {
        return currentTestCase.getActiveSimulation()
                .getOutputPlan()
                .getDeviationSubspacePlans()
                .stream()
                .filter(plan -> plan.getDeviationSubspace() instanceof GridDeviationSubspace)
                .sorted(comparing(GridCanvasController::getSortingKeyForDevSubspacePlan))
                .collect(toList());
    }

    private static String getSortingKeyForDevSubspacePlan(DeviationSubspacePlan<GridCollectiveState> plan) {
        checkArgument(GridDeviationSubspace.class.isInstance(plan.getDeviationSubspace()));
        return ((GridDeviationSubspace) plan.getDeviationSubspace()).getContainedEntityStates()
                .stream()
                .sorted(comparing(GridEntityState::toString))
                .collect(toList())
                .toString();
    }

    private void drawStateSpace(GraphicsContext gc, GridCollectiveStateSpace stateSpace) {
        setupCanvas(gc, stateSpace);
        gridDrawingDelegate.drawGrid(gc, stateSpace);
    }

    private void drawStateSpace(Graphics2D g, GridCollectiveStateSpace stateSpace) {
        gridDrawingDelegate.drawGrid(g, stateSpace);
        obstacleDrawingDelegate.drawObstacles(new GraphicsWrapper(g), stateSpace);
    }

    private void setupCanvas(GraphicsContext gc, GridCollectiveStateSpace stateSpace) {
        int rows = stateSpace.getRows();
        int cols = stateSpace.getCols();

        canvas.setWidth(gridParams.getTopPosForIndex(cols + 1));
        canvas.setHeight(gridParams.getTopPosForIndex(rows + 1));
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private int getCellWidth() {
        return gridParams.getCellWidth();
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

    public byte[] snapshotCanvasSvg() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            TransformerFactory.newInstance()
                    .newTransformer()
                    .transform(new DOMSource(svgCanvas.getSVGDocument()),
                            new StreamResult(out));
            return out.toByteArray();
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

}
