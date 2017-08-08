package pl.edu.agh.idziak.asw.visualizer.gui.drawing;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import javafx.scene.paint.Color;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Tomasz on 23.02.2017.
 */
public class DrawConstants {
    public static final Collection<Color> COLORS = ImmutableSet.of(
            Color.DODGERBLUE,
            Color.LIGHTGREEN,
            Color.SALMON,
            Color.BROWN,
            Color.YELLOW,
            Color.VIOLET,
            Color.PURPLE
    );

    public static final Map<String, Color> SUBSPACE_STRIPE_COLORS = ImmutableMap.of(
            "gray", Color.GRAY,
            "blue", Color.BLUE,
            "orange", Color.ORANGE,
            "red", Color.RED,
            "violet", Color.VIOLET
    );

    public static final double SUBSPACE_LINES_WIDTH = 1d;
    public static final int NORMAL_CELL_WIDTH = 80;
    public static final double OBSTACLE_TO_CELL_WIDTH_RATIO = 0.98;
    public static final Color OBSTACLE_COLOR = Color.GREY;
    public static final int ENTITY_ID_FONT_SIZE = 40;
    public static final int PATH_LINE_WIDTH = 3;
    public static final int PATH_ARROW_SIZE = 4;
    public static final int ENTITY_STOP_DOT_SIZE = 15;
    public static final double PATH_POINT_CIRCLE_EDGE_WIDTH = 1.0;
    public static final double PATH_POINT_OFFSET_FACTOR = 0.05;

    public static PathPointPositionIndex getPositionIndexForPathPoint(int index, int overlaps) {
        if (overlaps <= 1) {
            return new PathPointPositionIndex(0, 0, 1, 1);
        }
        if (overlaps <= 2) {
            return index == 0 ?
                    new PathPointPositionIndex(0, 0, 2, 2, 1, 1)
                    : new PathPointPositionIndex(1, 1, 2, 2, -1, -1);
        }
        if (overlaps <= 4) {
            return new PathPointPositionIndex(index / 2, index % 2, 2, 2);
        }
        if (overlaps <= 6) {
            return new PathPointPositionIndex(index / 3, index % 3, 2, 3);
        }
        if (overlaps <= 9) {
            return new PathPointPositionIndex(index / 3, index % 3, 3, 3);
        }
        if(overlaps <= 12){
            return new PathPointPositionIndex(index / 3, index % 3, 4, 3);
        }
        throw new IllegalStateException();
    }

    public static class PathPointPositionIndex {
        public final int row;
        public final int col;
        public final int rowsCount;
        public final int colsCount;
        public final int offsetDirectionRow;
        public final int offsetDirectionCol;

        private PathPointPositionIndex(int row, int col, int rowsCount, int colsCount) {
            this(row, col, rowsCount, colsCount, 0, 0);
        }

        private PathPointPositionIndex(int row, int col, int rowsCount, int colsCount, int offsetDirectionRow, int offsetDirectionCol) {
            this.row = row;
            this.col = col;
            this.rowsCount = rowsCount;
            this.colsCount = colsCount;
            this.offsetDirectionRow = offsetDirectionRow;
            this.offsetDirectionCol = offsetDirectionCol;
        }
    }
}
