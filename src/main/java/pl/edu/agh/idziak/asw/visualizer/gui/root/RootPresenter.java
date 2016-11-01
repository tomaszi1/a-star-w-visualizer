package pl.edu.agh.idziak.asw.visualizer.gui.root;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.idziak.asw.visualizer.gui.editpanel.EditPanelController;
import pl.edu.agh.idziak.asw.visualizer.testing.TestController;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.TestCase;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Tomasz on 27.08.2016.
 */
public class RootPresenter implements Initializable {
    private static final Logger LOG = LoggerFactory.getLogger(RootPresenter.class);
    private static final String ENV_VAR_PRELOAD_TEST = "preload.test";

    @FXML private Label testFileLabel;
    @FXML private BorderPane rootBorderPane;
    @FXML private Canvas canvas;
    @FXML private Button buttonReloadTests;
    @FXML private Button buttonOpenTests;
    @FXML private Button buttonExecuteTests;
    @FXML private Button buttonScaleUp;
    @FXML private Button buttonScaleDown;
    @FXML private ListView<TestCase> testCaseListView;
    @FXML private Window window;
    @FXML private EditPanelController editPanelController;

    @Inject
    private TestController testController;

    private TestCaseListController testCaseListController;

    private GridCanvasController gridCanvasController;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CanvasMouseEventsDispatcher canvasMouseEventsDispatcher = new CanvasMouseEventsDispatcher();
        gridCanvasController = new GridCanvasController(canvas,
                testController.activeTestCaseProperty(),
                canvasMouseEventsDispatcher);
        initializeTestCaseList();
        initializeButtons();
        preloadTest();
    }

    private void initializeTestCaseList() {
        testCaseListController = new TestCaseListController(testCaseListView, testController);
        testFileLabel.textProperty().bind(Bindings
                .concat("Test file: ")
                .concat(testController.getActiveTestFileNameProperty()));
    }

    private void initializeButtons() {
        buttonOpenTests.setOnAction(event -> buttonOpenTestsClicked());
        buttonExecuteTests.setOnAction(event -> testController.executeActiveTest());
        buttonReloadTests.setOnAction(event -> testController.reloadTests());
        buttonScaleDown.setOnAction(event -> gridCanvasController.scaleDown());
        buttonScaleUp.setOnAction(event -> gridCanvasController.scaleUp());
    }

    private void buttonSaveTestsAsClicked() {
        File file = chooseFile();
        if (file == null) return;
        testController.saveTestsAs(file);
    }

    private void buttonOpenTestsClicked() {
        File file = chooseFile();
        if (file == null) return;
        testController.loadTests(file);
    }

    private void preloadTest() {
        String testFilePath = System.getProperty(ENV_VAR_PRELOAD_TEST, System.getenv(ENV_VAR_PRELOAD_TEST));
        if (testFilePath == null) {
            LOG.info("No tests to preload, for preloading use system/env var: " + ENV_VAR_PRELOAD_TEST);
            return;
        }
        LOG.info("Preloading test file: " + testFilePath);
        testController.loadTests(new File(testFilePath));
    }

    private File chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose JSON tests file");
        fileChooser.setInitialDirectory(new File("."));
        return fileChooser.showOpenDialog(window);
    }

    @FXML
    public void listViewClicked(MouseEvent mouseEvent) {
        testCaseListController.listViewClicked(mouseEvent);
    }

    public void initScene(Scene scene) {
        rootBorderPane.prefHeightProperty().bind(scene.heightProperty());
        rootBorderPane.prefWidthProperty().bind(scene.widthProperty());
    }
}