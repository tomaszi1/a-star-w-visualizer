package pl.edu.agh.idziak.asw.visualizer.testing.benchmark;

import com.google.common.base.Throwables;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Tomasz on 03/05/2017.
 */
public class BenchmarkRun {
    private Class<?> benchmarkClass;
    private boolean debugMode;
    private Map<String, String> args;
    private boolean profileStack;

    private BenchmarkRun() {
    }

    public static BenchmarkRun build() {
        return new BenchmarkRun();
    }

    private static Collection<RunResult> runBenchmark(BenchmarkRun benchmarkRun) {
        ChainedOptionsBuilder options = new OptionsBuilder()
                .include(benchmarkRun.benchmarkClass.getName())
                .shouldFailOnError(benchmarkRun.debugMode)
                .warmupTime(TimeValue.seconds(2))
                .warmupIterations(1)
                .measurementIterations(1)
                .measurementTime(TimeValue.seconds(30))
                .forks(benchmarkRun.debugMode ? 0 : 1)
                .threads(1)
                .measurementBatchSize(1)
                .warmupBatchSize(1)
                .mode(Mode.AverageTime)
                .timeout(TimeValue.seconds(30))
                .verbosity(VerboseMode.NORMAL)
                .shouldDoGC(true)
                .operationsPerInvocation(1);

        if (benchmarkRun.profileStack) {
            options.addProfiler(StackProfiler.class);
        }

        if (benchmarkRun.args != null) {
            benchmarkRun.args.forEach(options::param);
        }

        try {
            return new Runner(options.build()).run();
        } catch (Exception e) {
            Throwables.throwIfUnchecked(e);
            throw new RuntimeException(e);
        }
    }

    public BenchmarkRun benchmarkClass(Class<?> benchmarkClass) {
        this.benchmarkClass = benchmarkClass;
        return this;
    }

    public BenchmarkRun debugMode(boolean debugMode) {
        this.debugMode = debugMode;
        return this;
    }

    public BenchmarkRun args(Map<String, String> args) {
        this.args = args;
        return this;
    }

    public BenchmarkRun profileStack(boolean profileStack) {
        this.profileStack = profileStack;
        return this;
    }

    public Collection<RunResult> run() {
        checkNotNull(benchmarkClass);
        return runBenchmark(this);
    }
}
