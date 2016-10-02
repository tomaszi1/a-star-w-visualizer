package pl.edu.agh.idziak.asw.visualizer.gui.editpanel;

import pl.edu.agh.idziak.asw.visualizer.gui.root.CanvasMouseEventsDispatcher;
import pl.edu.agh.idziak.asw.visualizer.testing.TestController;

/**
 * Created by Tomasz on 01.10.2016.
 */
public class TestEditor {

    private TestController testController;
    private CanvasMouseEventsDispatcher canvasMouseEventsDispatcher;

    private Mode currentMode;

    private enum Mode {
        OFF, ADD_ENTITIES, REMOVE_ENTITIES, ADD_OBSTACLES
    }

    public TestEditor(CanvasMouseEventsDispatcher canvasMouseEventsDispatcher) {
        this.canvasMouseEventsDispatcher = canvasMouseEventsDispatcher;
        canvasMouseEventsDispatcher.subscribeCellClicked(this::handleCellClicked);
    }

    private void handleCellClicked(CanvasMouseEventsDispatcher.CellClickedEvent cellClickedEvent) {
        switch (currentMode) {
            case OFF:
                break;
            case ADD_ENTITIES:
                break;
            case REMOVE_ENTITIES:
                break;
            case ADD_OBSTACLES:
                break;
        }
    }

    public void enableAddEntitiesMode() {
        currentMode = Mode.ADD_ENTITIES;
    }

    public void disableEditing() {
        currentMode = Mode.OFF;
    }


}
