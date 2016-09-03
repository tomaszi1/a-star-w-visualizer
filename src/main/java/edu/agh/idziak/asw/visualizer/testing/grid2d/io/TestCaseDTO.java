package edu.agh.idziak.asw.visualizer.testing.grid2d.io;

import java.util.List;

/**
 * Created by Tomasz on 27.08.2016.
 */
public class TestCaseDTO {
    private int[][] stateSpace;
    private List<EntityDTO> entities;
    private String name;

    public TestCaseDTO() {
    }

    private TestCaseDTO(Builder builder) {
        stateSpace = builder.stateSpace;
        entities = builder.entities;
        name = builder.name;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public int[][] getStateSpace() {
        return stateSpace;
    }

    public List<EntityDTO> getEntities() {
        return entities;
    }

    public String getName() {
        return name;
    }


    public static final class Builder {
        private int[][] stateSpace;
        private List<EntityDTO> entities;
        private String name;

        private Builder() {
        }

        public Builder stateSpace(int[][] val) {
            stateSpace = val;
            return this;
        }

        public Builder entities(List<EntityDTO> val) {
            entities = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public TestCaseDTO build() {
            return new TestCaseDTO(this);
        }
    }
}
