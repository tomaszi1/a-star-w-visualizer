package pl.edu.agh.idziak.asw.visualizer.gui.root;

import com.google.common.base.Preconditions;
import pl.edu.agh.idziak.asw.visualizer.testing.TestLoader;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.io.DTOMapper;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.io.TestCaseDTO;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.TestCase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by Tomasz on 27.08.2016.
 */
public class TestsController {
    private static final Logger LOG = LoggerFactory.getLogger(TestsController.class);

    @Inject
    private TestLoader testLoader;

    @Inject
    private DialogDisplay dialogDisplay;

    private File currentTestsFile;
    private final ObservableList<TestCase> testCases;
    private final SimpleStringProperty activeTestFileNameProperty;
    private final SimpleObjectProperty<TestCase> activeTestCase;

    public TestsController() {
        testCases = FXCollections.observableArrayList();
        activeTestFileNameProperty = new SimpleStringProperty();
        activeTestCase = new SimpleObjectProperty<>();
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

    public void saveTestsAs(File file) {
        try {
            unsafeSaveTests(file);
        } catch (IOException e) {
            dialogDisplay.displayError("Could not load test cases: " + e.getMessage());
            LOG.info("Error while loading file", e);
        }
    }

    public void saveTests() {
        if (currentTestsFile == null) {
            return;
        }
        saveTestsAs(currentTestsFile);
    }

    private void unsafeSaveTests(File file) throws IOException {
        List<TestCaseDTO> testCaseDTOs = testCases.stream().map(DTOMapper::internalToDto).collect(toList());
        testLoader.writeTestsFile(file, testCaseDTOs);
    }

    ObservableStringValue getActiveTestFileNameProperty() {
        return activeTestFileNameProperty;
    }

    ObservableObjectValue<TestCase> activeTestCaseProperty() {
        return activeTestCase;
    }

    void setActiveTestCase(TestCase testCase) {
        Preconditions.checkArgument(testCases.contains(testCase), "Given test case is not in current test set");
        activeTestCase.set(testCase);
    }
}
