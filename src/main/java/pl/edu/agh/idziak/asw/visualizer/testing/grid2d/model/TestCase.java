package pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model;

import javafx.beans.property.SimpleObjectProperty;
import pl.edu.agh.idziak.asw.OutputPlan;
import pl.edu.agh.idziak.asw.grid2d.G2DCollectiveState;
import pl.edu.agh.idziak.asw.grid2d.G2DEntityState;
import pl.edu.agh.idziak.asw.grid2d.G2DInputPlan;
import pl.edu.agh.idziak.asw.grid2d.G2DStateSpace;

/**
 * Created by Tomasz on 27.08.2016.
 */
public class TestCase {
    private G2DInputPlan inputPlan;
    private SimpleObjectProperty<OutputPlan<G2DStateSpace, G2DCollectiveState, G2DEntityState, Integer, Double>> outputPlan;
    private String name;
    private boolean lightDefinition;

    public TestCase(String name, G2DInputPlan inputPlan, boolean lightDefinition) {
        this.name = name;
        this.inputPlan = inputPlan;
        this.lightDefinition = lightDefinition;
        this.outputPlan = new SimpleObjectProperty<>();
    }

    public G2DInputPlan getInputPlan() {
        return inputPlan;
    }

    public SimpleObjectProperty<OutputPlan<G2DStateSpace, G2DCollectiveState, G2DEntityState, Integer, Double>> outputPlanProperty() {
        return outputPlan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLightDefinition() {
        return lightDefinition;
    }
}
