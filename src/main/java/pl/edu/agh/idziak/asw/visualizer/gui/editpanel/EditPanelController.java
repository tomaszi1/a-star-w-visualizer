package pl.edu.agh.idziak.asw.visualizer.gui.editpanel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.idziak.asw.visualizer.testing.TestController;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Tomasz on 11.09.2016.
 */
public class EditPanelController implements Initializable {
    private static final Logger LOG = LoggerFactory.getLogger(EditPanelController.class);

    @FXML private BorderPane editPanel;

    @FXML private Button buttonRemoveEntitiesMode;
    @FXML private Button buttonAddEntitiesMode;
    @FXML private Button buttonAddObstaclesMode;

    @Inject private TestController testController;

    private TestEditor testEditor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buttonAddEntitiesMode.setOnAction(this::buttonAddEntitiesModeClicked);
    }

    private void buttonAddEntitiesModeClicked(ActionEvent event) {
        testEditor.enableAddEntitiesMode();
    }
}
