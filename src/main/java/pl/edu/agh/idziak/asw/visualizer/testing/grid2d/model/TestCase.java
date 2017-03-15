package pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model;

import pl.edu.agh.idziak.asw.impl.ExtendedOutputPlan;
import pl.edu.agh.idziak.asw.impl.grid2d.G2DCollectiveState;
import pl.edu.agh.idziak.asw.impl.grid2d.G2DInputPlan;
import pl.edu.agh.idziak.asw.impl.grid2d.G2DStateSpace;
import pl.edu.agh.idziak.asw.visualizer.GlobalEventBus;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.simulation.NewSimulationEvent;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.simulation.Simulation;

/**
 * Created by Tomasz on 27.08.2016.
 */
public class TestCase {

    private G2DInputPlan inputPlan;
    private String name;
    private boolean sparseDefinition;
    private Simulation simulation;
    private ExtendedOutputPlan<G2DStateSpace, G2DCollectiveState> outputPlan;

    public TestCase(String name, G2DInputPlan inputPlan, boolean sparseDefinition) {
        this.name = name;
        this.inputPlan = inputPlan;
        this.sparseDefinition = sparseDefinition;
    }

    public G2DInputPlan getInputPlan() {
        return inputPlan;
    }

    public void publishNewSimulationEvent() {
        GlobalEventBus.INSTANCE.get().post(new NewSimulationEvent(simulation));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSparseDefinition() {
        return sparseDefinition;
    }

    public Simulation getActiveSimulation() {
        return simulation;
    }

    public void setOutputPlan(ExtendedOutputPlan<G2DStateSpace, G2DCollectiveState> outputPlan) {
        simulation = new Simulation(inputPlan, outputPlan.getOutputPlan());
        this.outputPlan = outputPlan;
        publishNewSimulationEvent();
    }

    public ExtendedOutputPlan<G2DStateSpace, G2DCollectiveState> getExtendedOutputPlan() {
        return outputPlan;
    }
}
