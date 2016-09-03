package edu.agh.idziak.asw.visualizer.gui.root;

import com.google.common.base.Preconditions;
import edu.agh.idziak.asw.grid2d.G2DCollectiveState;
import edu.agh.idziak.asw.grid2d.G2DEntityState;
import edu.agh.idziak.asw.grid2d.G2DInputPlan;
import edu.agh.idziak.asw.grid2d.G2DStateSpace;
import edu.agh.idziak.asw.visualizer.testing.TestLoader;
import edu.agh.idziak.asw.visualizer.testing.grid2d.io.EntityDTO;
import edu.agh.idziak.asw.visualizer.testing.grid2d.io.TestCaseDTO;
import edu.agh.idziak.asw.visualizer.testing.grid2d.model.TestCase;
import edu.agh.idziak.common.SimpleEntity;
import edu.agh.idziak.common.SimpleEntityFactory;
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
import java.util.*;

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
        } catch (IOException e) {
            dialogDisplay.displayError("Could not load test cases: " + e.getMessage());
            LOG.info("Error while loading file", e);
        }
    }

    private void unsafeLoadTests(File newTestsFile) throws IOException {
        List<TestCaseDTO> testCaseDTOs = testLoader.openTestsFile(newTestsFile);

        List<TestCase> newTestCases = testCaseDTOs.stream().map(dto -> {
            TestCase testCase = new TestCase(dtoToInputPlan(dto));
            testCase.setName(dto.getName());
            return testCase;
        }).collect(toList());

        this.testCases.setAll(newTestCases);

        currentTestsFile = newTestsFile;
        activeTestFileNameProperty.set(currentTestsFile.getName());
    }


    private static G2DInputPlan dtoToInputPlan(TestCaseDTO testCaseDTO) {
        int numEntities = testCaseDTO.getEntities().size();
        G2DStateSpace stateSpace = new G2DStateSpace(testCaseDTO.getStateSpace());
        Set<SimpleEntity> entities = new HashSet<>(numEntities);
        Map<SimpleEntity, G2DEntityState> initialStates = new HashMap<>(numEntities);
        Map<SimpleEntity, G2DEntityState> targetStates = new HashMap<>(numEntities);

        for (EntityDTO entityDTO : testCaseDTO.getEntities()) {
            SimpleEntity e = SimpleEntityFactory.create();
            entities.add(e);
            initialStates.put(e, G2DEntityState.of(entityDTO.getRow(), entityDTO.getCol()));
            targetStates.put(e, G2DEntityState.of(entityDTO.getTargetRow(), entityDTO.getTargetCol()));
        }

        G2DCollectiveState initialState = G2DCollectiveState.fromEntityStates(initialStates);
        G2DCollectiveState targetState = G2DCollectiveState.fromEntityStates(targetStates);

        return new G2DInputPlan(entities, stateSpace, initialState, targetState);
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
        List<TestCaseDTO> testCaseDTOs = testCases.stream().map(TestsController::testCaseToDto).collect(toList());
        testLoader.writeTestsFile(file, testCaseDTOs);
    }

    private static TestCaseDTO testCaseToDto(TestCase testCase) {
        return TestCaseDTO.newBuilder()
                .name(testCase.getName())
                .stateSpace(testCase.getInputPlan().getStateSpace().getData())
                .entities(inputPlanToEntityDTOs(testCase.getInputPlan()))
                .build();
    }

    private static List<EntityDTO> inputPlanToEntityDTOs(G2DInputPlan inputPlan) {
        Set<?> entities = inputPlan.getEntities();
        List<EntityDTO> dtoList = new ArrayList<>(entities.size());
        for (Object entity : entities) {
            G2DEntityState initialState = inputPlan.getInitialCollectiveState().getStateForEntity(entity);
            G2DEntityState targetState = inputPlan.getTargetCollectiveState().getStateForEntity(entity);

            EntityDTO entityDTO = EntityDTO.newBuilder()
                    .row(initialState.getRow())
                    .col(initialState.getCol())
                    .targetRow(targetState.getRow())
                    .targetCol(targetState.getCol())
                    .build();
            dtoList.add(entityDTO);
        }
        return dtoList;
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
