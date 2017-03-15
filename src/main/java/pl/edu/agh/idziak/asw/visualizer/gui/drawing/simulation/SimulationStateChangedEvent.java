package pl.edu.agh.idziak.asw.visualizer.gui.drawing.simulation;

/**
 * Created by Tomasz on 14.03.2017.
 */
public class SimulationStateChangedEvent {

    private Simulation simulation;

    SimulationStateChangedEvent(Simulation simulation) {this.simulation = simulation;}

    public Simulation getSimulation() {
        return simulation;
    }
}
