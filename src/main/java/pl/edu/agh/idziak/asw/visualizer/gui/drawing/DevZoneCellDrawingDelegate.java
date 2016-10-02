package pl.edu.agh.idziak.asw.visualizer.gui.drawing;

import com.google.common.collect.Iterables;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Iterator;

/**
 * Created by Tomasz on 01.10.2016.
 */
public class DevZoneCellDrawingDelegate {
    private final Iterable<DevZoneCellDrawer> drawers = Iterables.cycle(
            new DevZoneCellSlashStripeDrawer(),
            new DevZoneCellBackslashStripeDrawer(),
            new DevZoneCellVerticalStripeDrawer(),
            new DevZoneCellHorizontalStripeDrawer()
    );
    private Iterator<DevZoneCellDrawer> drawerIterator;
    private DevZoneCellDrawer currentDrawer;

    private final Iterable<Color> colors = Iterables.cycle(
            Color.GRAY,
            Color.BLUE,
            Color.GREEN,
            Color.SALMON
    );
    private Iterator<Color> colorIterator;
    private Color currentColor;

    private int cellTop;
    private int cellBottom;
    private int cellLeft;
    private int cellRight;

    public DevZoneCellDrawingDelegate() {
        resetState();
        switchPattern();
    }

    public void resetState() {
        colorIterator = colors.iterator();
        drawerIterator = drawers.iterator();
    }

    public void drawDevZone(GraphicsContext gc) {
        DevZoneCellDrawer drawer = currentDrawer;
        drawer.setBounds(cellTop, cellLeft, cellBottom, cellRight);
        drawer.setColor(currentColor);
        drawer.drawCell(gc);
    }

    public void switchPattern() {
        currentColor = colorIterator.next();
        currentDrawer = drawerIterator.next();
    }

    public void setCellBounds(int top, int left, int bottom, int right) {
        cellTop = top;
        cellLeft = left;
        cellRight = right;
        cellBottom = bottom;
    }
}
