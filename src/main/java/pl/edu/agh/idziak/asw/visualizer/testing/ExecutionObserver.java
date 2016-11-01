package pl.edu.agh.idziak.asw.visualizer.testing;

import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.TestCase;

/**
 * Created by Tomasz on 23.10.2016.
 */
public interface ExecutionObserver {
    void executionFailed(Throwable e);
    void executionSucceeded(TestCase testCase);
}
