package pl.edu.agh.idziak.asw.visualizer.testing.benchmark;

import pl.edu.agh.idziak.asw.astar.SortingPreference;
import pl.edu.agh.idziak.asw.impl.AlgorithmType;
import pl.edu.agh.idziak.asw.impl.grid2d.*;
import pl.edu.agh.idziak.asw.visualizer.testing.AStarStateStore;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.io.DTOMapper;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.io.TestCaseDTO;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.io.TestLoader;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.TestCase;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Tomasz on 06/05/2017.
 */
public class ASWTestSuite {

    private final List<TestCase> testCases;
    private final AStarStateStore monitor;
    private TestLoader loader = new TestLoader();
    private Map<String, Integer> executions = new HashMap<>();

    private GridASWPlanner aswPlanner;
    private GridAStarPlanner aStarOnlyPlanner;
    private GridWavefrontOnlyPlanner wavefrontOnlyPlanner;

    public ASWTestSuite(Path path) {
        List<TestCaseDTO> testCaseDTOS = loadTestSuite(path);
        testCases = testCaseDTOS.stream().map(DTOMapper::dtoToInternal).collect(Collectors.toList());
        aswPlanner = new GridASWPlanner();
        aStarOnlyPlanner = new GridAStarPlanner();
        wavefrontOnlyPlanner = new GridWavefrontOnlyPlanner();
        monitor = new AStarStateStore();
        aswPlanner.setAStarCurrentStateMonitor(monitor);
        aStarOnlyPlanner.setAStarCurrentStateMonitor(monitor);
    }

    private List<TestCaseDTO> loadTestSuite(Path path) {
        try {
            return loader.openTestsFile(path.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void runTestCase(int id, AlgorithmType algorithmType, SortingPreference preference) {
        TestCase testCase = testCases.stream()
                .filter(tc -> tc.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Test with id " + id + " not found"));
        Timer timer = scheduleInterrupt();
        GridASWOutputPlan output;
        try {
            output = runAlgorithm(testCase, algorithmType, preference);
        } finally {
            testCase.getInputPlan().getCollectiveStateSpace().resetStateCache();
            timer.cancel();
        }
        String planSummary = PlanSummaryGenerator.getPlanSummary(testCase.getInputPlan(),output,monitor,preference);
        executions.compute(planSummary, (summary, count) -> count == null ? 1 : count + 1);
    }

    private Timer scheduleInterrupt() {
        Thread thread = Thread.currentThread();
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                thread.interrupt();
            }
        }, 60000);
        return timer;
    }

    private GridASWOutputPlan runAlgorithm(TestCase testCase, AlgorithmType algorithmType, SortingPreference preference) {
        if (preference == null) {
            preference = SortingPreference.NONE;
        }
        switch (algorithmType) {
            case ASW:
                aswPlanner.setAStarSortingPreference(preference);
                return aswPlanner.calculatePlan(testCase.getInputPlan());
            case ASTAR_ONLY:
                aStarOnlyPlanner.setAStarSortingPreference(preference);
                return aStarOnlyPlanner.calculatePlan(testCase.getInputPlan());
            case WAVEFRONT:
                return wavefrontOnlyPlanner.calculatePlan(testCase.getInputPlan());
        }
        throw new IllegalStateException();
    }

    public String getStats() {
        return executions.toString();
    }

}
