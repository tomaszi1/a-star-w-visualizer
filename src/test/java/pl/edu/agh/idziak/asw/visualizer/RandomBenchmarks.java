package pl.edu.agh.idziak.asw.visualizer;

import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveState;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveStateSpace;
import pl.edu.agh.idziak.asw.impl.grid2d.NeighborhoodType;
import pl.edu.agh.idziak.asw.visualizer.testing.benchmark.BenchmarkRun;

@State(Scope.Benchmark)
public class RandomBenchmarks {

    private GridCollectiveStateSpace stateSpace;
    private GridCollectiveState state;

    @Test
    public void test1() throws Exception {
        BenchmarkRun.build()
                .debugMode(true)
                .benchmarkClass(this.getClass())
                .profileStack(true)
                .run();

    }

    @Setup
    public void setupBenchmark() {
        stateSpace = new GridCollectiveStateSpace(new byte[7][7]);
        stateSpace.setNeighborhood(NeighborhoodType.MOORE);
        state = stateSpace.collectiveStateFrom(new byte[]{3, 3, 5, 5, 1, 1, 2, 2});
    }

    @Benchmark
    public void benchmark1() {
        stateSpace.getNeighborStatesOf(state);
    }
}
