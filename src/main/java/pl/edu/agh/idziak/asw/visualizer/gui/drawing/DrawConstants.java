package pl.edu.agh.idziak.asw.visualizer.gui.drawing;

import com.google.common.collect.ImmutableSet;
import javafx.scene.paint.Color;

import java.util.Collection;

/**
 * Created by Tomasz on 23.02.2017.
 */
public class DrawConstants {
    public static final Collection<Color> COLORS = ImmutableSet.of(
            Color.GRAY,
            Color.BLUE,
            Color.GREEN,
            Color.SALMON,
            Color.BROWN,
            Color.VIOLET
    );


    public static final int NORMAL_CELL_WIDTH = 40;
    public static final double OBSTACLE_TO_CELL_WIDTH_RATIO = 0.5;
}
