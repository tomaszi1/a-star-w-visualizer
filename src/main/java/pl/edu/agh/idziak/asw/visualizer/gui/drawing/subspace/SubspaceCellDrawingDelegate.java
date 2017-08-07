package pl.edu.agh.idziak.asw.visualizer.gui.drawing.subspace;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.idziak.asw.visualizer.gui.drawing.DrawConstants;

import java.awt.*;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Tomasz on 01.10.2016.
 */
public class SubspaceCellDrawingDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(SubspaceCellDrawingDelegate.class);

    private static final Iterable<AbstractSubspaceCellDrawer> DRAWERS = ImmutableList.of(
            new SubspaceCellSlashStripeDrawer(),
            new SubspaceCellBackslashStripeDrawer(),
            new SubspaceCellVerticalStripeDrawer(),
            new SubspaceCellHorizontalStripeDrawer()
    );

    private Iterator<AbstractSubspaceCellDrawer> drawerIterator;
    private AbstractSubspaceCellDrawer currentDrawer;

    private Iterator<Map.Entry<String, Color>> colorIterator;
    private Color currentColor;

    private int cellTop;
    private int cellBottom;
    private int cellLeft;
    private int cellRight;

    public SubspaceCellDrawingDelegate() {
        resetState();
    }

    public void resetState() {
        colorIterator = Iterables.cycle(DrawConstants.SUBSPACE_STRIPE_COLORS.entrySet()).iterator();
        drawerIterator = Iterables.cycle(DRAWERS).iterator();
        switchPattern();
    }

    public void drawSubspaceCell(GraphicsContext gc) {
        AbstractSubspaceCellDrawer drawer = currentDrawer;
        drawer.setBounds(cellTop, cellLeft, cellBottom, cellRight);
        drawer.setColor(currentColor);
        drawer.drawCell(gc);
    }

    public void drawSubspaceCell(Graphics2D gc) {
        AbstractSubspaceCellDrawer drawer = currentDrawer;
        drawer.setBounds(cellTop, cellLeft, cellBottom, cellRight);
        drawer.setColor(currentColor);
        drawer.drawCell(gc);
    }

    public void switchPattern() {
        Map.Entry<String, Color> colorEntry = colorIterator.next();
        currentColor = colorEntry.getValue();
        currentDrawer = drawerIterator.next();
        LOG.debug("Switched pattern to {}, {}", colorEntry.getKey(), currentDrawer.getClass().getSimpleName());
    }

    public void setCellBounds(int top, int left, int bottom, int right) {
        cellTop = top;
        cellLeft = left;
        cellRight = right;
        cellBottom = bottom;
    }
}
