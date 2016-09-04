package edu.agh.idziak.asw.visualizer.testing.grid2d.model;

import edu.agh.idziak.asw.OutputPlan;
import edu.agh.idziak.asw.grid2d.G2DCollectiveState;
import edu.agh.idziak.asw.grid2d.G2DEntityState;
import edu.agh.idziak.asw.grid2d.G2DInputPlan;
import edu.agh.idziak.asw.grid2d.G2DStateSpace;

/**
 * Created by Tomasz on 27.08.2016.
 */
public class TestCase {
    private G2DInputPlan inputPlan;
    private OutputPlan<G2DStateSpace, G2DCollectiveState, G2DEntityState, Integer, Double> outputPlan;
    private String name;
    private boolean lightDefinition;

    public TestCase(String name, G2DInputPlan inputPlan, boolean lightDefinition) {
        this.name = name;
        this.inputPlan = inputPlan;
        this.lightDefinition = lightDefinition;
    }

    public void setOutputPlan(OutputPlan<G2DStateSpace, G2DCollectiveState, G2DEntityState, Integer, Double>
                                      outputPlan) {
        this.outputPlan = outputPlan;
    }

    public G2DInputPlan getInputPlan() {
        return inputPlan;
    }

    public OutputPlan<G2DStateSpace, G2DCollectiveState, G2DEntityState, Integer, Double> getOutputPlan() {
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
