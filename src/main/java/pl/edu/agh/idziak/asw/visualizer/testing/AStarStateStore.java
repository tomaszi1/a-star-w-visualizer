package pl.edu.agh.idziak.asw.visualizer.testing;

import pl.edu.agh.idziak.asw.astar.AStarIterationData;
import pl.edu.agh.idziak.asw.astar.AStarStateMonitor;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveState;

public final class AStarStateStore extends AStarStateMonitor<GridCollectiveState> {
    private int closedSetSize;
    private int maxOpenSetSize;

    @Override
    public void onFinish(int closedSetSize, int ignore) {
        this.closedSetSize = closedSetSize;
    }

    @Override
    public void onIteration(AStarIterationData<GridCollectiveState> aStarIterationData) {
        this.maxOpenSetSize = Math.max(aStarIterationData.getOpenSetSize(), maxOpenSetSize);
    }

    public int getClosedSetSize() {
        return closedSetSize;
    }

    public int getMaxOpenSetSize() {
        return maxOpenSetSize;
    }
}
