package pl.edu.agh.idziak.asw.visualizer.testing.benchmark;

import org.openjdk.jmh.annotations.*;
import pl.edu.agh.idziak.asw.astar.SortingPreference;
import pl.edu.agh.idziak.asw.impl.AlgorithmType;

import java.nio.file.Paths;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Tomasz on 03/05/2017.
 */
@State(Scope.Benchmark)
public class ASWStandardBenchmark {
    public static final String PARAM_FILE_PATH = "filePath";
    public static final String PARAM_TEST_CASE_ID = "testCaseId";

    @Param("nope")
    private String filePath;

    @Param("-1")
    private int testCaseId;

    private ASWTestSuite aswTestSuite;

    @Setup
    public void setup() {
        checkNotNull(filePath);
        checkNotNull(testCaseId);
        aswTestSuite = new ASWTestSuite(Paths.get(filePath));
    }

    @TearDown
    public void teardown() {
        System.out.println(aswTestSuite.getStats());
    }

    @Benchmark
    public void runASW() {
        aswTestSuite.runTestCase(testCaseId, AlgorithmType.ASW, SortingPreference.NONE);
    }

    @Benchmark
    public void runASWHigherGScorePreference() {
        aswTestSuite.runTestCase(testCaseId, AlgorithmType.ASW, SortingPreference.PREFER_HIGHER_G_SCORE);
    }

    @Benchmark
    public void runAStar() {
        aswTestSuite.runTestCase(testCaseId, AlgorithmType.ASTAR_ONLY, SortingPreference.NONE);
    }

    @Benchmark
    public void runAStarHigherGScorePreference() {
        aswTestSuite.runTestCase(testCaseId, AlgorithmType.ASTAR_ONLY, SortingPreference.PREFER_HIGHER_G_SCORE);
    }

    // @Benchmark
    public void runWavefrontOnly() {
        aswTestSuite.runTestCase(testCaseId, AlgorithmType.WAVEFRONT, SortingPreference.NONE);
    }
}
