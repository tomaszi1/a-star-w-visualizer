package pl.edu.agh.idziak.asw.visualizer.gui.root;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    @FXML
    private BorderPane testListBorderPane;
    @FXML
    private Label testFileLabel;
    @FXML
    private SpacePane spacePane;
    @FXML
    private BorderPane rootBorderPane;
    @FXML
    private Canvas canvas;
    @FXML
    private SplitPane splitPane;
    @FXML
    private Button buttonOpenTests;
    @FXML
    private Button buttonSaveTests;
    @FXML
    private Button buttonSaveTestsAs;
    @FXML
    private Button buttonExecuteTest;
    @FXML
    private ListView<TestCase> testCaseListView;

    private Scene scene;

    @Inject
    private TestsController testsController;

    private TestCaseListController testCaseListController;

    private GridCanvasController gridCanvasController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // splitPane.setDividerPositions(0.15f, 0.85f);
        gridCanvasController = new GridCanvasController(canvas, testsController.activeTestCaseProperty());

        testCaseListController = new TestCaseListController(testCaseListView, testsController);
        testFileLabel.textProperty().bind(Bindings
                .concat("Test file: ")
                .concat(testsController.getActiveTestFileNameProperty()));

        buttonOpenTests.setOnAction(event -> {
            File file = chooseFile();
            if (file == null) return;
            testsController.loadTests(file);
        });

        buttonSaveTests.setOnAction(event -> {
            testsController.saveTests();
        });

        buttonSaveTestsAs.setOnAction(event -> {
            File file = chooseFile();
            if (file == null) return;
            testsController.saveTestsAs(file);
        });

        preloadTest();
    }

    private void preloadTest() {
        String testFilePath = System.getenv("preload.test");
        if (testFilePath != null) {
            LOG.info("Preloading test file: " + testFilePath);
            testsController.loadTests(new File(testFilePath));
        }
    }

    private File chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose JSON tests file");
        fileChooser.setInitialDirectory(new File("."));
        return fileChooser.showOpenDialog(scene.getWindow());
    }

    @FXML
    public void listViewClicked(MouseEvent mouseEvent) {
        testCaseListController.listViewClicked(mouseEvent);
    }

    public void initScene(Scene scene) {
        this.scene = scene;
        rootBorderPane.prefHeightProperty().bind(scene.heightProperty());
        rootBorderPane.prefWidthProperty().bind(scene.widthProperty());
    }
}
