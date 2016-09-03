package edu.agh.idziak.asw.visualizer;

import com.google.common.collect.ImmutableList;
import edu.agh.idziak.asw.visualizer.testing.grid2d.io.EntityDTO;
import edu.agh.idziak.asw.visualizer.testing.grid2d.io.TestsJsonMapper;
import edu.agh.idziak.asw.visualizer.testing.grid2d.io.TestCaseDTO;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Tomasz on 27.08.2016.
 */
public class SimpleTest {

    @Test
    public void test1() throws IOException {
        TestsJsonMapper testsJsonMapper = new TestsJsonMapper();
        TestCaseDTO testCsae = TestCaseDTO.newBuilder().entities(ImmutableList.of(EntityDTO.newBuilder()
                .col(1).row(2).targetCol(5).targetRow(3).build())).stateSpace(new int[][]{
                {1, 1, 1},
                {1, 1, 1},
                {1, 1, 1}
        }).build();
    }

}