package pl.edu.agh.idziak.asw.visualizer.testing;

import javafx.beans.value.ObservableObjectValue;
import pl.edu.agh.idziak.asw.OutputPlan;
import pl.edu.agh.idziak.asw.grid2d.G2DCollectiveState;
import pl.edu.agh.idziak.asw.grid2d.G2DEntityState;
import pl.edu.agh.idziak.asw.grid2d.G2DPlanner;
import pl.edu.agh.idziak.asw.grid2d.G2DStateSpace;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.TestCase;

/**
 * Created by Tomasz on 04.09.2016.
 */
public class TestExecutor {

    private ObservableObjectValue<TestCase> activeTestCase;
    private G2DPlanner g2DPlanner = new G2DPlanner();

    public TestExecutor(ObservableObjectValue<TestCase> activeTestCase) {
        this.activeTestCase = activeTestCase;
    }

    void executeTest() {
        TestCase testCase = activeTestCase.get();
        if (testCase == null)
            return;
        OutputPlan<G2DStateSpace, G2DCollectiveState, G2DEntityState, Integer, Double> outputPlan =
                g2DPlanner.calculatePlan(testCase.getInputPlan());
        testCase.outputPlanProperty().set(outputPlan);
    }
}
