package pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model;

import com.google.common.base.MoreObjects;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveState;
import pl.edu.agh.idziak.asw.impl.grid2d.GridInputPlan;
import pl.edu.agh.idziak.asw.impl.grid2d.GridStateSpace;
import pl.edu.agh.idziak.asw.model.ASWOutputPlan;
import pl.edu.agh.idziak.asw.visualizer.GlobalEventBus;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.simulation.NewSimulationEvent;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.simulation.Simulation;

/**
 * Created by Tomasz on 27.08.2016.
 */
public class TestCase {

    private GridInputPlan inputPlan;
    private String name;
    private Integer id;
    private Simulation simulation;

    public TestCase(String name, GridInputPlan inputPlan, Integer id) {
        this.name = name;
        this.inputPlan = inputPlan;
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public GridInputPlan getInputPlan() {
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

    public Simulation getActiveSimulation() {
        return simulation;
    }

    public void setOutputPlan(ASWOutputPlan<GridStateSpace, GridCollectiveState> outputPlan) {
        simulation = new Simulation(inputPlan, outputPlan);
        publishNewSimulationEvent();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("id", id)
                .toString();
    }
}
