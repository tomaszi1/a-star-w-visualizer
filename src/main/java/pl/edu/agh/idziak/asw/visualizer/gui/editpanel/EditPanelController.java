package pl.edu.agh.idziak.asw.visualizer.gui.editpanel;

import com.google.common.eventbus.Subscribe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.idziak.asw.visualizer.GlobalEventBus;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.simulation.Simulation;
import pl.edu.agh.idziak.asw.visualizer.testing.ActiveTestCaseChangeEvent;
import pl.edu.agh.idziak.asw.visualizer.testing.TestController;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.TestCase;

import javax.inject.Inject;
import java.net.URL;
import java.util.Comparator;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Tomasz on 11.09.2016.
 */
public class EditPanelController implements Initializable {

    private static final Logger LOG = LoggerFactory.getLogger(EditPanelController.class);

    @FXML private Button simulationReset;
    @FXML private Button simulationNextStep;
    @FXML private Button simulationPreviousStep;
    @FXML public ListView<Object> deviationTriggersListView;
    @FXML private BorderPane editPanel;
    @FXML private Label labelStatistics;
    private ObservableList<Object> entityList = FXCollections.observableArrayList();

    @Inject private TestController testController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        testController.statisticsProperty().addListener((observable, oldValue, newValue) -> {
            Map<String, Integer> statsMap = newValue.getAsMap();
            StringBuilder sb = new StringBuilder();
            statsMap.entrySet()
                    .stream()
                    .sorted(Comparator.comparing(Map.Entry::getKey))
                    .forEach(entry -> sb.append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n"));
            labelStatistics.setText(sb.toString());
        });

        simulationReset.setOnAction(event -> {
            TestCase testCase = testController.activeTestCaseProperty().get();
            if (testCase != null && testCase.getActiveSimulation() != null) {
                testCase.getActiveSimulation().reset();
            }
        });

        simulationNextStep.setOnAction(event -> {
            TestCase testCase = testController.activeTestCaseProperty().get();
            if (testCase != null && testCase.getActiveSimulation() != null) {
                testCase.getActiveSimulation().nextStep();
            }
        });

        simulationPreviousStep.setOnAction(event -> {
            TestCase testCase = testController.activeTestCaseProperty().get();
            if (testCase != null && testCase.getActiveSimulation() != null) {
                testCase.getActiveSimulation().previousStep();
            }
        });

        deviationTriggersListView.setItems(entityList);

        deviationTriggersListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Object selectedItem = deviationTriggersListView.getSelectionModel().getSelectedItem();
                if (selectedItem == null) {
                    return;
                }
                TestCase testCase = testController.activeTestCaseProperty().get();
                Simulation activeSimulation = testCase.getActiveSimulation();
                if (activeSimulation != null) {
                    boolean deviated = activeSimulation.deviateNextStepOfEntity(selectedItem);
                    LOG.info("deviation success: {}", deviated);
                }
            }
        });

        GlobalEventBus.INSTANCE.get().register(new TestCaseChangeListener());
    }

    private class TestCaseChangeListener {

        @Subscribe
        public void newTestCase(ActiveTestCaseChangeEvent event) {
            TestCase testCase = event.getTestCase();
            entityList.clear();
            entityList.addAll(testCase.getInputPlan().getEntities());
            entityList.sort(Comparator.comparing(Object::toString));
        }
    }
}
