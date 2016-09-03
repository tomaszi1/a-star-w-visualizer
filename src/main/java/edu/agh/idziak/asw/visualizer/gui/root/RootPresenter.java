package edu.agh.idziak.asw.visualizer.gui.root;

import edu.agh.idziak.asw.visualizer.testing.grid2d.model.TestCase;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Tomasz on 27.08.2016.
 */
public class RootPresenter implements Initializable {

    @FXML
    public Label testFileLabel;
    @FXML
    public SpacePane spacePane;
    @FXML
    public BorderPane rootBorderPane;
    @FXML
    public Canvas canvas;
    @FXML
    public AnchorPane canvasContainer;
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

    private CanvasController canvasController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        canvasController = new CanvasController(canvas, canvasContainer,testsController.activeTestCaseProperty());

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
