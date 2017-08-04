package pl.edu.agh.idziak.asw.visualizer.gui.drawing.entity;

import com.google.common.base.Preconditions;
import pl.edu.agh.idziak.asw.common.Pair;
import pl.edu.agh.idziak.asw.common.SingleTypePair;

import java.util.HashMap;
import java.util.Map;

public class OverlapTracker {

    private Map<SingleTypePair<Integer>, Integer> pathPointsAtPosition = new HashMap<>();
    private Map<SingleTypePair<Integer>, Integer> pathPointsIndexCountdown = new HashMap<>();

    public void addPathPointInPosition(int row, int col) {
        pathPointsAtPosition.compute(Pair.ofSameType(row, col), (key, val) -> val != null ? val + 1 : 1);
    }

    public void initCountdown() {
        pathPointsIndexCountdown = new HashMap<>(pathPointsAtPosition);
    }

    public int getOverlapsAtPosition(int row, int col) {
        return pathPointsAtPosition.getOrDefault(Pair.ofSameType(row, col), 0);
    }

    public int nextIndexAtPosition(int row, int col) {
        return pathPointsIndexCountdown.compute(Pair.ofSameType(row, col), (key, val) -> {
            Preconditions.checkArgument(val != null && val > 0);
            return val - 1;
        });
    }

}
