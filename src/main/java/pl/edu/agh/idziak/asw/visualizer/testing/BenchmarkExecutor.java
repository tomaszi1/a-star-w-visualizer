package pl.edu.agh.idziak.asw.visualizer.testing;

import com.google.common.collect.ImmutableMap;
import org.openjdk.jmh.results.RunResult;
import pl.edu.agh.idziak.asw.visualizer.testing.benchmark.ASWStandardBenchmark;
import pl.edu.agh.idziak.asw.visualizer.testing.benchmark.BenchmarkRun;

import java.util.Collection;

/**
 * Created by Tomasz on 03/05/2017.
 */
public class BenchmarkExecutor {

    public void startBenchmark(String filePath, int id) {
        Collection<RunResult> results = BenchmarkRun.build()
                .benchmarkClass(ASWStandardBenchmark.class)
                .debugMode(false)
                .profileStack(false)
                .args(ImmutableMap.of(
                        ASWStandardBenchmark.PARAM_FILE_PATH, filePath,
                        ASWStandardBenchmark.PARAM_TEST_CASE_ID, String.valueOf(id)
                )).run();

        // results.forEach(runResult -> runResult.getBenchmarkResults().forEach(benchmarkResult -> {
        //     benchmarkResult.getIterationResults().forEach(iterationResult -> {
        //         System.out.println(iterationResult.getMetadata());
        //         System.out.println(iterationResult.getPrimaryResult());
        //         System.out.println(iterationResult.getScoreUnit());
        //     });
        // }));
    }
}
