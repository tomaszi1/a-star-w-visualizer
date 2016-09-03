package edu.agh.idziak.asw.visualizer.gui.root;

import edu.agh.idziak.asw.visualizer.testing.grid2d.model.TestCase;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

/**
 * Created by Tomasz on 27.08.2016.
 */
public class TestCaseListController {

    private ListView<TestCase> listView;

    private TestsController testsController;

    public TestCaseListController(ListView<TestCase> listView, TestsController testsController) {
        this.listView = listView;
        this.testsController = testsController;
        listView.setItems(testsController.getTestCases());
        listView.setCellFactory(buildCellFactory());
    }

    private Callback<ListView<TestCase>, ListCell<TestCase>> buildCellFactory() {
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
        testsController.setActiveTestCase(selectedTestCase);
    }
}
