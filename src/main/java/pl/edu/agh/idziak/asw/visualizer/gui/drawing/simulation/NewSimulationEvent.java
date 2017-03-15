package pl.edu.agh.idziak.asw.visualizer.gui.drawing.simulation;

/**
 * Created by Tomasz on 14.03.2017.
 */
public class NewSimulationEvent {

    private Simulation simulation;

    public NewSimulationEvent(Simulation simulation) {
        this.simulation = simulation;
    }

    public Simulation getSimulation() {
        return simulation;
    }
}
