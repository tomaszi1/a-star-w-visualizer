package pl.edu.agh.idziak.asw.visualizer.testing;

import javafx.beans.value.ObservableObjectValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.edu.agh.idziak.asw.OutputPlan;
import pl.edu.agh.idziak.asw.grid2d.G2DCollectiveState;
import pl.edu.agh.idziak.asw.grid2d.G2DEntityState;
import pl.edu.agh.idziak.asw.grid2d.G2DPlanner;
import pl.edu.agh.idziak.asw.grid2d.G2DStateSpace;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.TestCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Tomasz on 04.09.2016.
 */
public class TestExecutor {

    private ObservableObjectValue<TestCase> activeTestCase;
    private G2DPlanner g2DPlanner = new G2DPlanner();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public TestExecutor(ObservableObjectValue<TestCase> activeTestCase) {
        this.activeTestCase = activeTestCase;
    }

    void executeTest() {
        TestCase testCase = activeTestCase.get();
        if (testCase == null)
            return;

        executorService.execute(new CalculatePlanTask(testCase));
    }

    private class CalculatePlanTask extends Task<TestCase> {
        private TestCase testCase;
        private OutputPlan<G2DStateSpace, G2DCollectiveState, G2DEntityState, Integer, Double> outputPlan;

        public CalculatePlanTask(TestCase testCase) {
            this.testCase = testCase;
        }

        @Override protected TestCase call() throws Exception {
            outputPlan = g2DPlanner.calculatePlan(testCase.getInputPlan());
            return testCase;
        }

        @Override protected void succeeded() {
            testCase.outputPlanProperty().set(outputPlan);
        }
    }
}
