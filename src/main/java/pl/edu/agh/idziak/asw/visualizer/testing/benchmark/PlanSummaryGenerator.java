package pl.edu.agh.idziak.asw.visualizer.testing.benchmark;

import com.google.common.base.MoreObjects;
import pl.edu.agh.idziak.asw.astar.SortingPreference;
import pl.edu.agh.idziak.asw.impl.BaseWavefrontPlanner;
import pl.edu.agh.idziak.asw.impl.grid2d.GridASWOutputPlan;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveDeviationSubspace;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveState;
import pl.edu.agh.idziak.asw.impl.grid2d.GridInputPlan;
import pl.edu.agh.idziak.asw.visualizer.testing.AStarStateStore;
import pl.edu.agh.idziak.asw.wavefront.DeviationSubspacePlan;
import pl.edu.agh.idziak.asw.wavefront.impl.GradientDeviationSubspacePlan;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlanSummaryGenerator {
    public static String getPlanSummary(GridInputPlan inputPlan, GridASWOutputPlan plan, AStarStateStore monitor, SortingPreference preference) {
        return MoreObjects.toStringHelper("ASWOutputPlan")
                .add("algorithm", plan.getAlgorithmType())
                .add("sortPref", preference)
                .add("entities", inputPlan.getEntities().size())
                .add("pathLength", plan.getCollectivePath().get().size())
                .add("deviationSubspacePlans", getDeviationSubspacePlansSummary(plan))
                .add("iterations", monitor.getClosedSetSize())
                .add("maxOpenSetSize", monitor.getMaxOpenSetSize())
                .toString();
    }

    private static String getDeviationSubspacePlansSummary(GridASWOutputPlan plan) {
        int numOfPlans = plan.getDeviationSubspacePlans().size();
        Map<Integer, Long> groupedByNumOfEntities = plan.getDeviationSubspacePlans().stream()
                .collect(Collectors.groupingBy(PlanSummaryGenerator::countSubspaceEntities, Collectors.counting()));
        List<Integer> collectiveStatesPerSubspace = plan.getDeviationSubspacePlans().stream()
                .map(PlanSummaryGenerator::countSubspaceStates)
                .sorted()
                .collect(Collectors.toList());
        int allStatesCount = collectiveStatesPerSubspace.stream()
                .mapToInt(value -> value)
                .sum();
        return MoreObjects.toStringHelper("SubspacePlanSummary")
                .add("numberOfSubspaces", numOfPlans)
                .add("groupedByEntitiesCount", groupedByNumOfEntities)
                .add("allStatesCount", allStatesCount)
                .add("statesCount", collectiveStatesPerSubspace)
                .toString();
    }

    private static Integer countSubspaceStates(DeviationSubspacePlan<GridCollectiveState> plan) {
        if (plan instanceof GradientDeviationSubspacePlan) {
            return ((GradientDeviationSubspacePlan) plan).size();
        }
        throw new IllegalStateException();
    }

    private static int countSubspaceEntities(DeviationSubspacePlan<GridCollectiveState> o) {
        if (o.getDeviationSubspace() instanceof GridCollectiveDeviationSubspace) {
            return ((GridCollectiveDeviationSubspace) o.getDeviationSubspace()).countEntities();
        } else if (o.getDeviationSubspace() instanceof BaseWavefrontPlanner.StateSpaceAsDeviationSubspace) {
            return 0;
        }
        throw new IllegalStateException();
    }
}
