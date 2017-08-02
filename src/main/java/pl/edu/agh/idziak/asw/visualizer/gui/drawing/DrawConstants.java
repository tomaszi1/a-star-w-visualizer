package pl.edu.agh.idziak.asw.visualizer.gui.drawing;

import com.google.common.collect.ImmutableSet;
import javafx.scene.paint.Color;

import java.util.Collection;

/**
 * Created by Tomasz on 23.02.2017.
 */
public class DrawConstants {
    public static final Collection<Color> COLORS = ImmutableSet.of(
            Color.DODGERBLUE,
            Color.GREEN,
            Color.SALMON,
            Color.BROWN,
            Color.YELLOWGREEN,
            Color.VIOLET,
            Color.PURPLE
    );


    public static final int NORMAL_CELL_WIDTH = 40;
    public static final double OBSTACLE_TO_CELL_WIDTH_RATIO = 0.98;
    public static final Color OBSTACLE_COLOR = Color.GREY;
}
