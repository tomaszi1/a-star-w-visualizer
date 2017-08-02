package pl.edu.agh.idziak.asw.visualizer.testing.benchmark;

import org.openjdk.jmh.annotations.*;
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

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void runAsw() {
        aswTestSuite.runTestCase(testCaseId, AlgorithmType.ASW);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void runAStar() {
        aswTestSuite.runTestCase(testCaseId, AlgorithmType.ASTAR_ONLY);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void runWavefrontOnly() {
        aswTestSuite.runTestCase(testCaseId, AlgorithmType.WAVEFRONT);
    }
}
