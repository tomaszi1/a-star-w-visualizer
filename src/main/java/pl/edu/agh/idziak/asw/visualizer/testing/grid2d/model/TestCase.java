package pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model;

import javafx.beans.property.SimpleObjectProperty;
import pl.edu.agh.idziak.asw.impl.ExtendedOutputPlan;
import pl.edu.agh.idziak.asw.impl.grid2d.G2DCollectiveState;
import pl.edu.agh.idziak.asw.impl.grid2d.G2DInputPlan;
import pl.edu.agh.idziak.asw.impl.grid2d.G2DStateSpace;

/**
 * Created by Tomasz on 27.08.2016.
 */
public class TestCase {
    private G2DInputPlan inputPlan;
    private SimpleObjectProperty<ExtendedOutputPlan<G2DStateSpace, G2DCollectiveState>> outputPlan;
    private String name;
    private boolean sparseDefinition;

    public TestCase(String name, G2DInputPlan inputPlan, boolean sparseDefinition) {
        this.name = name;
        this.inputPlan = inputPlan;
        this.sparseDefinition = sparseDefinition;
        this.outputPlan = new SimpleObjectProperty<>();
    }

    public G2DInputPlan getInputPlan() {
        return inputPlan;
    }

    public SimpleObjectProperty<ExtendedOutputPlan<G2DStateSpace, G2DCollectiveState>> outputPlanProperty() {
        return outputPlan;
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
}
