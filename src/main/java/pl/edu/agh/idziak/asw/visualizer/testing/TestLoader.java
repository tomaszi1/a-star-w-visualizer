package pl.edu.agh.idziak.asw.visualizer.testing;

import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.io.TestCaseDTO;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.io.TestsJsonMapper;

import java.io.*;
import java.util.List;

/**
 * Created by Tomasz on 27.08.2016.
 */
public class TestLoader {

    private TestsJsonMapper testsJsonMapper;

    public TestLoader() {
        testsJsonMapper = new TestsJsonMapper();
    }

    public List<TestCaseDTO> openTestsFile(File file) throws IOException {
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        return testsJsonMapper.readTests(inputStream);
    }

    public void writeTestsFile(File file, List<TestCaseDTO> testCaseDTOs) throws IOException {
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
        testsJsonMapper.writeTests(outputStream, testCaseDTOs);
    }

}
