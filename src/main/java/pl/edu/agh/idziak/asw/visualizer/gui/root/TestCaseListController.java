package pl.edu.agh.idziak.asw.visualizer.gui.root;

import pl.edu.agh.idziak.asw.visualizer.testing.TestController;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.TestCase;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

/**
 * Created by Tomasz on 27.08.2016.
 */
public class TestCaseListController {

    private ListView<TestCase> listView;

    private TestController testController;

    public TestCaseListController(ListView<TestCase> listView, TestController testController) {
        this.listView = listView;
        this.testController = testController;
        listView.setItems(testController.getTestCases());
        listView.setCellFactory(buildCellFactory());
    }

    private static Callback<ListView<TestCase>, ListCell<TestCase>> buildCellFactory() {
        return new Callback<ListView<TestCase>, ListCell<TestCase>>() {
            @Override
            public ListCell<TestCase> call(ListView<TestCase> param) {
                return new ListCell<TestCase>() {
                    @Override
                    protected void updateItem(TestCase item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null)
                            setText(item.getName());
                    }
                };
            }
        };
    }

    void listViewClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() != 2) {
            return;
        }
        TestCase selectedTestCase = listView.getSelectionModel().getSelectedItem();
        if (selectedTestCase == null) {
            return;
        }
        testController.setActiveTestCase(selectedTestCase);
    }
}
