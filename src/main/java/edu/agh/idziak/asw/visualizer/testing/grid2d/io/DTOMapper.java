package edu.agh.idziak.asw.visualizer.testing.grid2d.io;

import edu.agh.idziak.asw.grid2d.G2DCollectiveState;
import edu.agh.idziak.asw.grid2d.G2DEntityState;
import edu.agh.idziak.asw.grid2d.G2DInputPlan;
import edu.agh.idziak.asw.grid2d.G2DStateSpace;
import edu.agh.idziak.asw.visualizer.testing.grid2d.model.Entity;
import edu.agh.idziak.asw.visualizer.testing.grid2d.model.TestCase;

import java.util.*;

/**
 * Created by Tomasz on 04.09.2016.
 */
public class DTOMapper {
    public static TestCase dtoToInternal(TestCaseDTO testCaseDTO) {
        int numEntities = testCaseDTO.getEntities().size();
        G2DStateSpace stateSpace = mapStateSpace(testCaseDTO);
        Set<Object> entities = new HashSet<>(numEntities);
        Map<Object, G2DEntityState> initialStates = new HashMap<>(numEntities);
        Map<Object, G2DEntityState> targetStates = new HashMap<>(numEntities);

        for (EntityDTO entityDTO : testCaseDTO.getEntities()) {
            Entity e = Entity.newBuilder().id(entityDTO.getId()).build();
            entities.add(e);
            initialStates.put(e, G2DEntityState.of(entityDTO.getRow(), entityDTO.getCol()));
            targetStates.put(e, G2DEntityState.of(entityDTO.getTargetRow(), entityDTO.getTargetCol()));
        }

        G2DCollectiveState initialState = G2DCollectiveState.fromEntityStates(initialStates);
        G2DCollectiveState targetState = G2DCollectiveState.fromEntityStates(targetStates);

        G2DInputPlan g2DInputPlan = new G2DInputPlan(entities, stateSpace, initialState, targetState);

        return new TestCase(testCaseDTO.getName(), g2DInputPlan, DTOMapper.isLightlyDefined(testCaseDTO));
    }

    private static G2DStateSpace mapStateSpace(TestCaseDTO testCaseDTO) {
        G2DStateSpace stateSpace;
        if (testCaseDTO.getStateSpace() != null) {
            stateSpace = new G2DStateSpace(testCaseDTO.getStateSpace());
        } else {
            if (!isLightlyDefined(testCaseDTO)) {
                throw new RuntimeException("Missing state space definition in " + testCaseDTO);
            }
            Integer rows = testCaseDTO.getStateSpaceRows();
            Integer cols = testCaseDTO.getStateSpaceCols();
            stateSpace = new G2DStateSpace(new int[rows][cols]);
        }
        return stateSpace;
    }

    private static boolean isLightlyDefined(TestCaseDTO testCaseDTO) {
        return testCaseDTO.getStateSpaceCols() != null && testCaseDTO.getStateSpaceRows() != null;
    }

    public static TestCaseDTO internalToDto(TestCase testCase) {
        TestCaseDTO.Builder builder = TestCaseDTO.newBuilder().name(testCase.getName());
        G2DStateSpace stateSpace = testCase.getInputPlan().getStateSpace();
        if (testCase.isLightDefinition()) {
            builder.stateSpaceCols(stateSpace.getCols());
            builder.stateSpaceRows(stateSpace.getRows());
        } else {
            builder.stateSpace(stateSpace.getData());
        }
        return builder
                .entities(inputPlanToEntityDTOs(testCase.getInputPlan()))
                .build();
    }

    private static List<EntityDTO> inputPlanToEntityDTOs(G2DInputPlan inputPlan) {
        Set<?> entities = inputPlan.getEntities();
        G2DCollectiveState initialCollectiveState = inputPlan.getInitialCollectiveState();
        G2DCollectiveState targetCollectiveState = inputPlan.getTargetCollectiveState();

        List<EntityDTO> dtoList = new ArrayList<>(entities.size());

        for (Object entity : entities) {
            G2DEntityState initialState = initialCollectiveState.getStateForEntity(entity);
            G2DEntityState targetState = targetCollectiveState.getStateForEntity(entity);

            EntityDTO.Builder builder = EntityDTO.newBuilder()
                    .row(initialState.getRow())
                    .col(initialState.getCol())
                    .targetRow(targetState.getRow())
                    .targetCol(targetState.getCol());

            if (entity instanceof Entity) {
                Entity entity1 = (Entity) entity;
                builder.id(entity1.getId());
            }

            dtoList.add(builder.build());
        }
        return dtoList;
    }
}
