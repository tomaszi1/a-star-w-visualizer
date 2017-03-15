package pl.edu.agh.idziak.asw.visualizer.gui.drawing.simulation;

import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.idziak.asw.impl.grid2d.G2DCollectiveState;
import pl.edu.agh.idziak.asw.impl.grid2d.G2DEntityState;
import pl.edu.agh.idziak.asw.impl.grid2d.G2DInputPlan;
import pl.edu.agh.idziak.asw.impl.grid2d.G2DStateSpace;
import pl.edu.agh.idziak.asw.model.ASWOutputPlan;
import pl.edu.agh.idziak.asw.visualizer.GlobalEventBus;
import pl.edu.agh.idziak.asw.wavefront.SubspacePlan;

import java.util.*;

import static java.util.stream.Collectors.*;

/**
 * Created by Tomasz on 14.03.2017.
 */
public class Simulation {

    private static final Logger LOG = LoggerFactory.getLogger(Simulation.class);

    private final G2DInputPlan inputPlan;
    private final ASWOutputPlan<G2DStateSpace, G2DCollectiveState> outputPlan;
    private G2DCollectiveState currentState;
    private G2DCollectiveState nextPlannedState;
    private Map<Object, Deque<G2DEntityState>> deviationsForNextStep = new HashMap<>();
    private Stack<G2DCollectiveState> historicalStates = new Stack<>();
    private EventBus eventBus = GlobalEventBus.INSTANCE.get();

    public Simulation(G2DInputPlan inputPlan, ASWOutputPlan<G2DStateSpace, G2DCollectiveState> outputPlan) {
        this.inputPlan = inputPlan;
        this.outputPlan = outputPlan;
        currentState = inputPlan.getInitialCollectiveState();
        nextPlannedState = findNextPlannedState();
    }

    public boolean nextStep() {
        G2DCollectiveState nextState = getEffectiveNextState();
        if (nextState == null || !nextState.isValid()) {
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

    public G2DCollectiveState findNextPlannedState() {
        if (inputPlan.getTargetCollectiveState().equals(currentState)) {
            return null;
        }

        List<G2DCollectiveState> collectivePath = outputPlan.getCollectivePath().get();
        int size = collectivePath.size();
        long mostMatches = 0;
        G2DCollectiveState bestMatch = null;
        for (int i = 0; i < size; i++) {
            G2DCollectiveState state = collectivePath.get(i);
            if (state.equals(currentState)) {
                return collectivePath.get(i + 1);
            }
            long countMatchingEntityStates = getEntitiesWithStatesMatchingCurrentState(state).size();
            if (countMatchingEntityStates >= mostMatches) {
                bestMatch = state;
                mostMatches = countMatchingEntityStates;
            }
        }

        if (bestMatch == null) {
            throw new IllegalStateException("Could not find any matching states");
        }

        Map<Object, G2DEntityState> unmatchedStates = getUnmatchedEntities(bestMatch);

        Map<Object, G2DEntityState> movesFromDeviationZones = findPathsInDeviationZones(unmatchedStates);
        if (!unmatchedStates.isEmpty())
            return null;

        Map<Object, G2DEntityState> finalCollectiveState = new HashMap<>(bestMatch.getEntityStates());
        finalCollectiveState.putAll(movesFromDeviationZones);
        return G2DCollectiveState.from(finalCollectiveState);
    }

    public HashMap<Object, G2DEntityState> getUnmatchedEntities(G2DCollectiveState bestMatch) {
        return bestMatch.getEntityStates()
                        .entrySet()
                        .stream()
                        .filter(entry -> !currentState
                                .getStateForEntity(entry.getKey())
                                .equals(entry.getValue()))
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue,
                                (a, b) -> a, HashMap::new));
    }

    private Map<Object, G2DEntityState> findPathsInDeviationZones(Map<Object, G2DEntityState> unmatchedStates) {
        List<SubspacePlan<G2DCollectiveState>> plansSorted =
                outputPlan.getSubspacePlans().stream()
                          .sorted((o1, o2) -> Integer.compare(o1.getEntities().size(), o2.getEntities().size()))
                          .collect(toList());

        Map<Object, G2DEntityState> devZoneBasedMoves = new HashMap<>();

        plansSorted.forEach(plan -> {
            Set<?> matchedWithThisPlan = plan.getEntities()
                                             .stream()
                                             .filter(unmatchedStates::containsKey)
                                             .collect(toSet());
            if (matchedWithThisPlan.size() == plan.getEntities().size()) {
                Map<Object, G2DEntityState> matchingCollectiveState =
                        unmatchedStates.entrySet()
                                       .stream()
                                       .filter(entry -> matchedWithThisPlan.contains(entry.getKey()))
                                       .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
                G2DCollectiveState nextMove = plan.getNextMove(G2DCollectiveState.from(matchingCollectiveState));
                if (nextMove != null) {
                    devZoneBasedMoves.putAll(nextMove.getEntityStates());
                    unmatchedStates.keySet().removeAll(matchedWithThisPlan);
                }
            }
        });
        return devZoneBasedMoves;
    }

    public Set<?> getEntitiesWithStatesMatchingCurrentState(G2DCollectiveState state) {
        return state.getEntityStates()
                    .entrySet()
                    .stream()
                    .filter(entry -> currentState.getStateForEntity(entry.getKey())
                                                 .equals(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .collect(toSet());
    }

    public G2DInputPlan getInputPlan() {
        return inputPlan;
    }

    public ASWOutputPlan<G2DStateSpace, G2DCollectiveState> getOutputPlan() {
        return outputPlan;
    }

    public G2DCollectiveState getEffectiveNextState() {
        if (nextPlannedState == null)
            return null;
        Map<Object, G2DEntityState> nextEntityStates = new HashMap<>(nextPlannedState.getEntityStates());
        deviationsForNextStep.forEach((entity, statesDeque) -> nextEntityStates.put(entity, statesDeque.peekFirst()));
        return G2DCollectiveState.from(nextEntityStates);
    }

    private void publishSimulationStateChange() {
        eventBus.post(new SimulationStateChangedEvent(this));
    }

    public G2DCollectiveState getCurrentState() {
        return currentState;
    }

    public boolean deviateNextStepOfEntity(Object entity) {
        Deque<G2DEntityState> deviationsQueue = deviationsForNextStep.get(entity);
        if (deviationsQueue != null) {
            deviationsQueue.addLast(deviationsQueue.removeFirst());
            publishSimulationStateChange();
            return true;
        }

        G2DEntityState currentStateForEntity = currentState.getStateForEntity(entity);
        if (currentStateForEntity == null) {
            throw new IllegalStateException("Given entity is not a part of given simulation");
        }
        Set<G2DEntityState> possibleDeviations = inputPlan.getStateSpace().getNeighborStatesOf(currentStateForEntity);
        possibleDeviations.add(currentStateForEntity);
        deviationsQueue = new ArrayDeque<>(possibleDeviations);
        deviationsForNextStep.put(entity, deviationsQueue);
        publishSimulationStateChange();
        return true;
    }

    public boolean isReset() {
        return deviationsForNextStep.isEmpty() && inputPlan.getInitialCollectiveState().equals(currentState);
    }
}
