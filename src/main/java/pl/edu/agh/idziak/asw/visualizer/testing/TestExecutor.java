package pl.edu.agh.idziak.asw.visualizer.testing;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.concurrent.Task;
import pl.edu.agh.idziak.asw.OutputPlan;
import pl.edu.agh.idziak.asw.grid2d.G2DCollectiveState;
import pl.edu.agh.idziak.asw.grid2d.G2DEntityState;
import pl.edu.agh.idziak.asw.grid2d.G2DPlanner;
import pl.edu.agh.idziak.asw.grid2d.G2DStateSpace;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.TestCase;
import pl.edu.agh.idziak.common.Statistics;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Tomasz on 04.09.2016.
 */
public class TestExecutor {

    private final ObservableObjectValue<TestCase> activeTestCase;
    private final G2DPlanner g2DPlanner = new G2DPlanner();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final SimpleObjectProperty<Statistics> statistics;
    private ExecutionObserver executionObserver;

    public TestExecutor(ObservableObjectValue<TestCase> activeTestCase, ExecutionObserver executionObserver) {
        this.activeTestCase = activeTestCase;
        this.executionObserver = executionObserver;
        statistics = new SimpleObjectProperty<>();
    }

    void executeTest() {
        TestCase testCase = activeTestCase.get();
        if (testCase == null)
            return;
        TestExecutionTask testExecutionTask = new TestExecutionTask(testCase);
        executorService.submit(testExecutionTask);
    }

    public ObservableObjectValue<Statistics> statisticsProperty() {
        return statistics;
    }

    private class TestExecutionTask extends Task<Void> {
        private final TestCase testCase;
        private OutputPlan<G2DStateSpace, G2DCollectiveState, G2DEntityState, Integer, Double> outputPlan;

        private TestExecutionTask(TestCase testCase) {
            this.testCase = testCase;
        }

        @Override protected Void call() throws Exception {
            outputPlan = g2DPlanner.calculatePlan(testCase.getInputPlan());
            return null;
        }

        @Override protected void succeeded() {
            testCase.outputPlanProperty().set(outputPlan);
            statistics.set(g2DPlanner.getStatistics());
            executionObserver.executionSucceeded(testCase);
        }

        @Override protected void failed() {
            executionObserver.executionFailed(getException());
        }
    }
}
