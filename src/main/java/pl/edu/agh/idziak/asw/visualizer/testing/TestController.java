package pl.edu.agh.idziak.asw.visualizer.testing;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.idziak.asw.common.Statistics;
import pl.edu.agh.idziak.asw.impl.AlgorithmType;
import pl.edu.agh.idziak.asw.visualizer.GlobalEventBus;
import pl.edu.agh.idziak.asw.visualizer.gui.root.DialogDisplay;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.io.DTOMapper;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.io.TestCaseDTO;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.io.TestLoader;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.TestCase;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by Tomasz on 27.08.2016.
 */
public class TestController {

    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);
    private static final String LOG_MESSAGE_TEST_FAILED = "Test failed with an error";
    private TestLoader testLoader;

    private DialogDisplay dialogDisplay;
    private TestExecutor testExecutor;
    private File currentTestsFile;
    private BenchmarkExecutor benchmarkExecutor;

    private final ObservableList<TestCase> testCases;
    private final SimpleStringProperty activeTestFileNameProperty;
    private final SimpleObjectProperty<TestCase> activeTestCase;

    public TestController() {
        benchmarkExecutor = new BenchmarkExecutor();
        testLoader = new TestLoader();
        dialogDisplay = new DialogDisplay();
        testCases = FXCollections.observableArrayList();
        activeTestFileNameProperty = new SimpleStringProperty();
        activeTestCase = new SimpleObjectProperty<>();
        testExecutor = new TestExecutor(activeTestCaseProperty(), executionObserver);
    }

    public void executeActiveTest(AlgorithmType algorithmType, Runnable callback) {
        try {
            testExecutor.invokeTestInNewThread(algorithmType,callback);
        } catch (RuntimeException e) {
            dialogDisplay.displayException(LOG_MESSAGE_TEST_FAILED, e);
            LOG.error(LOG_MESSAGE_TEST_FAILED, e);
        }
    }

    public void loadTests(File newTestsFile) {
        try {
            unsafeLoadTests(newTestsFile);
        } catch (Exception e) {
            dialogDisplay.displayError("Could not load test cases: " + e.getMessage());
            LOG.error("Error while loading file", e);
        }
    }

    private void unsafeLoadTests(File newTestsFile) throws IOException {
        List<TestCaseDTO> testCaseDTOs = testLoader.openTestsFile(newTestsFile);
        List<TestCase> newTestCases = testCaseDTOs.stream().map(DTOMapper::dtoToInternal).collect(toList());
        this.testCases.setAll(newTestCases);
        currentTestsFile = newTestsFile;
        activeTestFileNameProperty.set(currentTestsFile.getName());
    }

    public ObservableList<TestCase> getTestCases() {
        return testCases;
    }


    public ObservableStringValue getActiveTestFileNameProperty() {
        return activeTestFileNameProperty;
    }

    public ObservableObjectValue<TestCase> activeTestCaseProperty() {
        return activeTestCase;
    }

    public void setActiveTestCase(TestCase testCase) {
        Preconditions.checkArgument(testCases.contains(testCase), "Given test case is not in current test set");
        activeTestCase.set(testCase);
        GlobalEventBus.INSTANCE.get().post(new ActiveTestCaseChangeEvent(testCase));
    }

    public void reloadTests() {
        loadTests(currentTestsFile);
    }

    public ObservableObjectValue<Statistics> statisticsProperty() {
        return testExecutor.statisticsProperty();
    }

    private final ExecutionObserver executionObserver = new ExecutionObserver() {
        @Override
        public void executionFailed(Throwable e) {
            if(!(Throwables.getRootCause(e) instanceof InterruptedException)){
                dialogDisplay.displayException("Test execution failed", e);
                LOG.error("Test exec failed", e);
            }
        }

        @Override
        public void executionSucceeded(TestCase testCase) {
        }
    };

    public void executeBenchmark() {
        if (activeTestCase.get() == null) {
            LOG.info("Null test case, no execution");
        }
        benchmarkExecutor.startBenchmark(currentTestsFile.getAbsolutePath(), activeTestCase.get().getId());
    }

    public TestExecutor getTestExecutor() {
        return testExecutor;
    }

    public void stopRunningTest() {
        testExecutor.stopRunningTest();
    }

}
