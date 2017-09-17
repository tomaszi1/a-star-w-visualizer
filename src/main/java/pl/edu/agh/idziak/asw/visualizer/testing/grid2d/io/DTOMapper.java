package pl.edu.agh.idziak.asw.visualizer.testing.grid2d.io;

import pl.edu.agh.idziak.asw.common.Utils;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveState;
import pl.edu.agh.idziak.asw.impl.grid2d.GridInputPlan;
import pl.edu.agh.idziak.asw.impl.grid2d.GridCollectiveStateSpace;
import pl.edu.agh.idziak.asw.impl.grid2d.NeighborhoodType;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.Entity;
import pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model.TestCase;

import java.util.LinkedList;
import java.util.List;

import static java.util.Comparator.comparing;

/**
 * Created by Tomasz on 04.09.2016.
 */
public class DTOMapper {

    public static TestCase dtoToInternal(TestCaseDTO testCaseDTO) {
        List<EntityDTO> entityDtos = testCaseDTO.getEntities();
        GridCollectiveStateSpace stateSpace = mapStateSpace(testCaseDTO);

        entityDtos.sort(comparing(EntityDTO::getId));

        List<Integer> initialEntityStates = new LinkedList<>();
        List<Integer> targetEntityStates = new LinkedList<>();
        List<Entity> entities = new LinkedList<>();

        for (int i = 0; i < entityDtos.size(); i++) {
            EntityDTO entityDTO = entityDtos.get(i);
            Entity entity = Entity.of(entityDTO.getId());

            initialEntityStates.add(entityDTO.getRow());
            initialEntityStates.add(entityDTO.getCol());

            targetEntityStates.add(entityDTO.getTargetRow());
            targetEntityStates.add(entityDTO.getTargetCol());

            entities.add(entity);
        }

        GridCollectiveState initialState = new GridCollectiveState(initialEntityStates);
        GridCollectiveState targetState = new GridCollectiveState(targetEntityStates);

        NeighborhoodType neighborhood;
        if (testCaseDTO.getNeighborhood() == null) {
            neighborhood = NeighborhoodType.VON_NEUMANN;
        } else {
            neighborhood = testCaseDTO.getNeighborhood().equals(0) ? NeighborhoodType.VON_NEUMANN : NeighborhoodType.MOORE;
        }
        GridInputPlan gridInputPlan = new GridInputPlan(entities, stateSpace, initialState, targetState, neighborhood);

        return new TestCase(testCaseDTO.getName(), gridInputPlan, testCaseDTO.getId());
    }

    private static GridCollectiveStateSpace mapStateSpace(TestCaseDTO testCaseDTO) {
        GridCollectiveStateSpace stateSpace;
        if (testCaseDTO.getStateSpace() != null) {
            stateSpace = new GridCollectiveStateSpace(Utils.toByteArray(testCaseDTO.getStateSpace()));
        } else {
            if (!isLightlyDefined(testCaseDTO)) {
                throw new RuntimeException("Missing state space definition in " + testCaseDTO);
            }
            Integer rows = testCaseDTO.getStateSpaceRows();
            Integer cols = testCaseDTO.getStateSpaceCols();
            stateSpace = new GridCollectiveStateSpace(new byte[rows][cols]);
        }
        return stateSpace;
    }

    private static boolean isLightlyDefined(TestCaseDTO testCaseDTO) {
        return testCaseDTO.getStateSpaceCols() != null && testCaseDTO.getStateSpaceRows() != null;
    }

}
