package pl.edu.agh.idziak.asw.visualizer.testing;

import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.TestCase;

/**
 * Created by Tomasz on 14.03.2017.
 */
public class ActiveTestCaseChangeEvent {

    private TestCase testCase;

    public ActiveTestCaseChangeEvent(TestCase testCase) {this.testCase = testCase;}

    public TestCase getTestCase() {
        return testCase;
    }
}
