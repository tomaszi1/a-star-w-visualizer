package pl.edu.agh.idziak.asw.visualizer.testing;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.idziak.asw.common.Benchmark;
import pl.edu.agh.idziak.asw.common.Statistics;
import pl.edu.agh.idziak.asw.impl.AlgorithmType;
import pl.edu.agh.idziak.asw.impl.ExtendedOutputPlan;
import pl.edu.agh.idziak.asw.impl.grid2d.*;
import pl.edu.agh.idziak.asw.model.ImmutableASWOutputPlan;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.TestCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Tomasz on 04.09.2016.
 */
public class TestExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(TestExecutor.class);

    private final ObservableObjectValue<TestCase> activeTestCase;
    private final ExecutorService executorService =
            Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setDaemon(true).build());
    private final SimpleObjectProperty<Statistics> statistics;
    private ExecutionObserver executionObserver;

    private final G2DPlanner g2DPlanner = new G2DPlanner();
    private final G2DAStarPlanner g2DAStarPlanner = new G2DAStarPlanner();
    private final G2DWavefrontPlanner g2DWavefrontPlanner = new G2DWavefrontPlanner();

    public TestExecutor(ObservableObjectValue<TestCase> activeTestCase, ExecutionObserver executionObserver) {
        this.activeTestCase = activeTestCase;
        this.executionObserver = executionObserver;
        statistics = new SimpleObjectProperty<>();
    }

    void invokeTestInNewThread(AlgorithmType algorithmType) {
        TestCase testCase = activeTestCase.get();
        if (testCase == null)
            return;
        TestExecutionTask testExecutionTask = new TestExecutionTask(testCase, algorithmType);
        executorService.submit(testExecutionTask);
    }

    private ExtendedOutputPlan<G2DStateSpace, G2DCollectiveState> executeTestWithGivenStrategy(G2DInputPlan inputPlan, AlgorithmType algorithmType) {
        switch (algorithmType) {
            case ASW:
                return g2DPlanner.calculatePlanWithBenchmark(inputPlan);
            case ASTAR_ONLY:
                return g2DAStarPlanner.calculatePlanWithBenchmark(inputPlan);
            case WAVEFRONT:
                return g2DWavefrontPlanner.calculatePlanWithBenchmark(inputPlan);
        }
        return null;
    }

    public ObservableObjectValue<Statistics> statisticsProperty() {
        return statistics;
    }

    private static Statistics buildStats(Benchmark benchmark, ImmutableASWOutputPlan<G2DStateSpace, G2DCollectiveState> outputPlan) {
        Statistics statistics = new Statistics("Stats for " + benchmark.getAlgorithmType());
        statistics.putInfo("algorithmType", benchmark.getAlgorithmType().name());
        if (benchmark.getIterationCount() != null)
            statistics.putStat("a* iterations", benchmark.getIterationCount());
        if (benchmark.getMaxSizeOfOpenSet() != null)
            statistics.putStat("max size of a* open set", benchmark.getMaxSizeOfOpenSet());
        if (benchmark.getAStarCalculationTimeMs() != null)
            statistics.putStat("a* calc time millis", benchmark.getAStarCalculationTimeMs().intValue());
        if (benchmark.getDeviationZonesSearchTimeMs() != null)
            statistics.putStat("dev zone search time millis", benchmark.getDeviationZonesSearchTimeMs().intValue());
        if (benchmark.getWavefrontCalculationTimeMs() != null)
            statistics.putStat("wavefront calc time millis", benchmark.getWavefrontCalculationTimeMs().intValue());
        if (outputPlan.getCollectivePath().get() != null)
            statistics.putStat("path length", outputPlan.getCollectivePath().get().size());
        if (!outputPlan.getSubspacePlans().isEmpty())
            statistics.putStat("deviation zones count", outputPlan.getSubspacePlans().size());
        return statistics;
    }

    private class TestExecutionTask extends Task<Void> {

        private final TestCase testCase;
        private AlgorithmType algorithmType;
        private ExtendedOutputPlan<G2DStateSpace, G2DCollectiveState> outputPlan;

        private TestExecutionTask(TestCase testCase, AlgorithmType algorithmType) {
            this.testCase = testCase;
            this.algorithmType = algorithmType;
        }

        @Override protected Void call() throws Exception {
            outputPlan = executeTestWithGivenStrategy(testCase.getInputPlan(), algorithmType);
            return null;
        }

        @Override protected void succeeded() {
            LOG.info("Test executed, path: {}", outputPlan.getOutputPlan());
            testCase.outputPlanProperty().set(outputPlan);
            statistics.set(buildStats(outputPlan.getBenchmark(), outputPlan.getOutputPlan()));
            executionObserver.executionSucceeded(testCase);
        }

        @Override protected void failed() {
            executionObserver.executionFailed(getException());
        }
    }
}
