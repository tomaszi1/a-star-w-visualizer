package pl.edu.agh.idziak.asw.visualizer.gui.root;

import com.google.common.base.MoreObjects;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by Tomasz on 01.10.2016.
 */
public class CanvasMouseEventsDispatcher {
    private static final Logger LOG = LoggerFactory.getLogger(CanvasMouseEventsDispatcher.class);

    public Set<Consumer<CellClickedEvent>> cellClickedConsumers = new HashSet<>();

    public CanvasMouseEventsDispatcher() {
    }

    public void cellClicked(int row, int col) {
        CellClickedEvent cellClickedEvent = new CellClickedEvent(row, col);
        LOG.debug("Dispatching " + cellClickedEvent);
        cellClickedConsumers.forEach(consumer -> Platform.runLater(() -> consumer.accept(cellClickedEvent)));
    }

    public void subscribeCellClicked(Consumer<CellClickedEvent> consumer) {
        cellClickedConsumers.add(consumer);
    }

    public static class CellClickedEvent {
        private int row;
        private int col;

        public CellClickedEvent(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }


        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("row", row).add("col", col).toString();
        }
    }
}
