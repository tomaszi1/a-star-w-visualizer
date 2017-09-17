package pl.edu.agh.idziak.asw.visualizer;

import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveState;
import pl.edu.agh.idziak.asw.impl.grid2d.GridEntityState;
import pl.edu.agh.idziak.asw.impl.grid2d.GridInputPlan;

/**
 * Created by Tomasz on 04.09.2016.
 */
public class Utils {

    public static String printReadableState(GridCollectiveState currentState, GridInputPlan inputPlan) {
        int rows = inputPlan.getCollectiveStateSpace().getRows();
        int cols = inputPlan.getCollectiveStateSpace().getCols();
        byte[][] array = new byte[rows][cols];
        byte index = 1;
        int size = inputPlan.getTargetCollectiveState().getEntityStates().size();
        for (int i = 0; i < size; i++) {
            GridEntityState current = currentState.getEntityStates().get(i);
            GridEntityState target = inputPlan.getTargetCollectiveState().getEntityStates().get(i);
            byte b = array[target.getRow()][target.getCol()];
            if (b == 0) {
                array[target.getRow()][target.getCol()] = (byte) -index;
            }
            array[current.getRow()][current.getCol()] = index;
            index++;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                byte value = array[i][j];
                if (value == 0) {
                    sb.append(".");
                } else if (value > 0) {
                    sb.append(Character.valueOf((char) (value + 64)));
                } else {
                    sb.append(Character.valueOf((char) (-value + 96)));
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
