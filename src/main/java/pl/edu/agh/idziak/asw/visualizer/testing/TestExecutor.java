package pl.edu.agh.idziak.asw.visualizer.testing;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.idziak.asw.astar.SortingPreference;
import pl.edu.agh.idziak.asw.common.Statistics;
import pl.edu.agh.idziak.asw.impl.AlgorithmType;
import pl.edu.agh.idziak.asw.impl.grid2d.*;
import pl.edu.agh.idziak.asw.visualizer.testing.benchmark.PlanSummaryGenerator;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.TestCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tomasz on 04.09.2016.
 */
public class TestExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(TestExecutor.class);

    private final ObservableObjectValue<TestCase> activeTestCase;
    private final SimpleObjectProperty<Statistics> statistics;
    private ExecutorService executorService;
    private final ExecutionObserver executionObserver;

    private final GridASWPlanner gridASWPlanner;
    private final GridAStarPlanner gridAStarPlanner;
    private final GridWavefrontOnlyPlanner gridWavefrontOnlyPlanner;

    public TestExecutor(ObservableObjectValue<TestCase> activeTestCase, ExecutionObserver executionObserver) {
        this.activeTestCase = activeTestCase;
        this.executionObserver = executionObserver;
        statistics = new SimpleObjectProperty<>();
        gridASWPlanner = new GridASWPlanner();
        gridAStarPlanner = new GridAStarPlanner();
        gridWavefrontOnlyPlanner = new GridWavefrontOnlyPlanner();
        gridAStarPlanner.setAStarSortingPreference(SortingPreference.PREFER_HIGHER_G_SCORE);
        gridASWPlanner.setAStarSortingPreference(SortingPreference.NONE);

        createExecutorService();
    }

    private void createExecutorService() {
        executorService = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("algorithmExecutionThread")
                .build());
    }

    public void stopRunningTest() {
        executorService.shutdownNow();
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    void invokeTestInNewThread(AlgorithmType algorithmType, Runnable callback) {
        TestCase testCase = activeTestCase.get();
        if (testCase == null)
            return;
        TestExecutionTask testExecutionTask = new TestExecutionTask(testCase, algorithmType,callback);
        if(executorService.isShutdown()){
            createExecutorService();
        }
        executorService.submit(testExecutionTask);
    }

    private GridASWOutputPlan executeTestWithGivenStrategy(GridInputPlan inputPlan, AlgorithmType algorithmType) {
        switch (algorithmType) {
            case ASW:
                return gridASWPlanner.calculatePlan(inputPlan);
            case ASTAR_ONLY:
                return gridAStarPlanner.calculatePlan(inputPlan);
            case WAVEFRONT:
                return gridWavefrontOnlyPlanner.calculatePlan(inputPlan);
        }
        throw new IllegalStateException();
    }

    ObservableObjectValue<Statistics> statisticsProperty() {
        return statistics;
    }

    private class TestExecutionTask extends Task<Void> {

        private final TestCase testCase;
        private AlgorithmType algorithmType;
        private Runnable callback;
        private GridASWOutputPlan outputPlan;

        private TestExecutionTask(TestCase testCase, AlgorithmType algorithmType, Runnable callback) {
            this.testCase = testCase;
            this.algorithmType = algorithmType;
            this.callback = callback;
        }

        @Override
        protected Void call() throws Exception {
            testCase.getInputPlan().getCollectiveStateSpace().resetStateCache();
            AStarStateStore monitor = setupMonitor();
            outputPlan = executeTestWithGivenStrategy(testCase.getInputPlan(), algorithmType);
            LOG.info("Plan summary: " + PlanSummaryGenerator.getPlanSummary(testCase.getInputPlan(), outputPlan, monitor, null));
            return null;
        }

        @Override
        protected void succeeded() {
            LOG.info("Test executed, path: {}", outputPlan);
            testCase.setOutputPlan(outputPlan);
            executionObserver.executionSucceeded(testCase);
            callback.run();
        }

        @Override
        protected void failed() {
            executionObserver.executionFailed(getException());
        }
    }

    private AStarStateStore setupMonitor() {
        AStarStateStore monitor = new AStarStateStore();
        gridASWPlanner.setAStarCurrentStateMonitor(monitor);
        gridAStarPlanner.setAStarCurrentStateMonitor(monitor);
        return monitor;
    }

    public GridASWPlanner getGridASWPlanner() {
        return gridASWPlanner;
    }

    public GridAStarPlanner getGridAStarPlanner() {
        return gridAStarPlanner;
    }

    public GridWavefrontOnlyPlanner getGridWavefrontOnlyPlanner() {
        return gridWavefrontOnlyPlanner;
    }


}
