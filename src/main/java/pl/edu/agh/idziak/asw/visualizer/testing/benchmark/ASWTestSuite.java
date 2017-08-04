package pl.edu.agh.idziak.asw.visualizer.testing.benchmark;

import pl.edu.agh.idziak.asw.impl.AlgorithmType;
import pl.edu.agh.idziak.asw.impl.grid2d.*;
import pl.edu.agh.idziak.asw.model.ASWOutputPlan;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.io.DTOMapper;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.io.TestCaseDTO;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.TestCase;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.io.TestLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Created by Tomasz on 06/05/2017.
 */
public class ASWTestSuite {

    private final List<TestCase> testCases;
    private TestLoader loader = new TestLoader();

    private GridASWPlanner aswPlanner = new GridASWPlanner();
    private GridAStarOnlyPlanner aStarOnlyPlanner = new GridAStarOnlyPlanner();
    private GridWavefrontOnlyPlanner wavefrontOnlyPlanner = new GridWavefrontOnlyPlanner();

    public ASWTestSuite(Path path)  {
        List<TestCaseDTO> testCaseDTOS = loadTestSuite(path);
        testCases = testCaseDTOS.stream().map(DTOMapper::dtoToInternal).collect(Collectors.toList());
    }

    private List<TestCaseDTO> loadTestSuite(Path path)  {
        try {
            return loader.openTestsFile(path.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void runTestCase(int id, AlgorithmType algorithmType) {
        TestCase testCase = testCases.stream()
                .filter(tc -> tc.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Test with id " + id + " not found"));
        runAlgorithm(testCase, algorithmType);
    }

    private ASWOutputPlan<GridCollectiveStateSpace, GridCollectiveState> runAlgorithm(TestCase testCase, AlgorithmType algorithmType) {
        switch (algorithmType) {
            case ASW:
                return aswPlanner.calculatePlan(testCase.getInputPlan());
            case ASTAR_ONLY:
                return aStarOnlyPlanner.calculatePlan(testCase.getInputPlan());
            case WAVEFRONT:
                return wavefrontOnlyPlanner.calculatePlan(testCase.getInputPlan());
        }
        return null;
    }
}
