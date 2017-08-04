package pl.edu.agh.idziak.asw.visualizer.gui.drawing.simulation;

import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveState;
import pl.edu.agh.idziak.asw.impl.grid2d.GridEntityState;
import pl.edu.agh.idziak.asw.impl.grid2d.GridInputPlan;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveStateSpace;
import pl.edu.agh.idziak.asw.model.ASWOutputPlan;
import pl.edu.agh.idziak.asw.visualizer.GlobalEventBus;

import java.util.*;

/**
 * Created by Tomasz on 14.03.2017.
 */
public class Simulation {

    private static final Logger LOG = LoggerFactory.getLogger(Simulation.class);

    private final GridInputPlan inputPlan;
    private final ASWOutputPlan<GridCollectiveStateSpace, GridCollectiveState> outputPlan;
    private GridCollectiveState currentState;
    private GridCollectiveState nextPlannedState;
    private Map<Object, Deque<GridEntityState>> deviationsForNextStep = new HashMap<>();
    private Stack<GridCollectiveState> historicalStates = new Stack<>();
    private EventBus eventBus = GlobalEventBus.INSTANCE.get();

    public Simulation(GridInputPlan inputPlan, ASWOutputPlan<GridCollectiveStateSpace, GridCollectiveState> outputPlan) {
        this.inputPlan = inputPlan;
        this.outputPlan = outputPlan;
        currentState = inputPlan.getInitialCollectiveState();
        nextPlannedState = findNextPlannedState();
    }

    public boolean nextStep() {
        GridCollectiveState nextState = getEffectiveNextState();
        if (nextState == null || !inputPlan.getStateSpace().isValidState(nextState)) {
            return false;
        }
        historicalStates.add(currentState);
        currentState = nextState;
        deviationsForNextStep.clear();
        nextPlannedState = findNextPlannedState();
        publishSimulationStateChange();
        return true;
    }

    public boolean previousStep() {
        if (historicalStates.isEmpty())
            return false;
        currentState = historicalStates.pop();
        deviationsForNextStep.clear();
        nextPlannedState = findNextPlannedState();
        publishSimulationStateChange();
        return true;
    }

    public void reset() {
        currentState = inputPlan.getInitialCollectiveState();
        deviationsForNextStep.clear();
        nextPlannedState = findNextPlannedState();
        historicalStates.clear();
        publishSimulationStateChange();
    }

    public GridCollectiveState findNextPlannedState() {
        // if (inputPlan.getTargetCollectiveState().equals(currentState)) {
        //     return null;
        // }
        //
        // List<GridCollectiveState> collectivePath = outputPlan.getCollectivePath().get();
        // int size = collectivePath.size();
        // long mostMatches = 0;
        // GridCollectiveState bestMatch = null;
        // for (int i = 0; i < size; i++) {
        //     GridCollectiveState state = collectivePath.get(i);
        //     if (state.equals(currentState)) {
        //         return collectivePath.get(i + 1);
        //     }
        //     long countMatchingEntityStates = getEntitiesWithStatesMatchingCurrentState(state).size();
        //     if (countMatchingEntityStates >= mostMatches) {
        //         bestMatch = state;
        //         mostMatches = countMatchingEntityStates;
        //     }
        // }
        //
        // if (bestMatch == null) {
        //     throw new IllegalStateException("Could not find any matching states");
        // }
        //
        // Map<Object, GridEntityState> unmatchedStates = getUnmatchedEntities(bestMatch);
        //
        // Map<Object, GridEntityState> movesFromDeviationZones = findPathsInDeviationZones(unmatchedStates);
        // if (!unmatchedStates.isEmpty())
        //     return null;
        //
        // Map<Object, GridEntityState> finalCollectiveState = new HashMap<>(bestMatch.getEntityStates());
        // finalCollectiveState.putAll(movesFromDeviationZones);
        // return GridCollectiveState.from(finalCollectiveState);
        return null;
    }

    public HashMap<Object, GridEntityState> getUnmatchedEntities(GridCollectiveState bestMatch) {
        return null;
        // return bestMatch.getEntityStates()
        //         .entrySet()
        //         .stream()
        //         .filter(entry -> !currentState
        //                 .getStateForEntity(entry.getKey())
        //                 .equals(entry.getValue()))
        //         .collect(toMap(Map.Entry::getKey, Map.Entry::getValue,
        //                 (a, b) -> a, HashMap::new));
    }

    private Map<Object, GridEntityState> findPathsInDeviationZones(Map<Object, GridEntityState> unmatchedStates) {
        // List<DeviationSubspacePlan<GridCollectiveState>> plansSorted =
        //         outputPlan.getDeviationSubspacePlans().stream()
        //                 .sorted((o1, o2) -> Integer.compare(o1.getEntities().size(), o2.getEntities().size()))
        //                 .collect(toList());
        //
        // Map<Object, GridEntityState> devZoneBasedMoves = new HashMap<>();
        //
        // plansSorted.forEach(plan -> {
        //     Set<?> matchedWithThisPlan = plan.getEntities()
        //             .stream()
        //             .filter(unmatchedStates::containsKey)
        //             .collect(toSet());
        //     if (matchedWithThisPlan.size() == plan.getEntities().size()) {
        //         Map<Object, GridEntityState> matchingCollectiveState =
        //                 unmatchedStates.entrySet()
        //                         .stream()
        //                         .filter(entry -> matchedWithThisPlan.contains(entry.getKey()))
        //                         .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        //         GridCollectiveState nextMove = plan.getNextMove(GridCollectiveState.from(matchingCollectiveState));
        //         if (nextMove != null) {
        //             devZoneBasedMoves.putAll(nextMove.getEntityStates());
        //             unmatchedStates.keySet().removeAll(matchedWithThisPlan);
        //         }
        //     }
        // });
        // return devZoneBasedMoves;
        return null;
    }

    public Set<?> getEntitiesWithStatesMatchingCurrentState(GridCollectiveState state) {
        // return state.getEntityStates()
        //         .entrySet()
        //         .stream()
        //         .filter(entry -> currentState.getStateForEntity(entry.getKey())
        //                 .equals(entry.getValue()))
        //         .map(Map.Entry::getKey)
        //         .collect(toSet());
        return null;
    }

    public GridInputPlan getInputPlan() {
        return inputPlan;
    }

    public ASWOutputPlan<GridCollectiveStateSpace, GridCollectiveState> getOutputPlan() {
        return outputPlan;
    }

    public GridCollectiveState getEffectiveNextState() {
        // if (nextPlannedState == null)
        //     return null;
        // Map<Object, GridEntityState> nextEntityStates = new HashMap<>(nextPlannedState.getEntityStates());
        // deviationsForNextStep.forEach((entity, statesDeque) -> nextEntityStates.put(entity, statesDeque.peekFirst()));
        // return GridCollectiveState.from(nextEntityStates);
        return null;
    }

    private void publishSimulationStateChange() {
        eventBus.post(new SimulationStateChangedEvent(this));
    }

    public GridCollectiveState getCurrentState() {
        return currentState;
    }

    public boolean deviateNextStepOfEntity(Object entity) {
        Deque<GridEntityState> deviationsQueue = deviationsForNextStep.get(entity);
        if (deviationsQueue != null) {
            deviationsQueue.addLast(deviationsQueue.removeFirst());
            publishSimulationStateChange();
            return true;
        }

        GridEntityState currentStateForEntity = inputPlan.getStateForEntity(currentState, entity);
        if (currentStateForEntity == null) {
            throw new IllegalStateException("Given entity is not a part of given simulation");
        }
        Set<GridEntityState> possibleDeviations = ImmutableSet.copyOf(inputPlan.getStateSpace().getNeighborStatesOf(currentStateForEntity));
        possibleDeviations.add(currentStateForEntity);
        deviationsQueue = new ArrayDeque<>(possibleDeviations);
        deviationsForNextStep.put(entity, deviationsQueue);
        publishSimulationStateChange();
        return true;
    }

    public boolean isReset() {
        return true;//deviationsForNextStep.isEmpty() && inputPlan.getInitialCollectiveState().equals(currentState);
    }
}
