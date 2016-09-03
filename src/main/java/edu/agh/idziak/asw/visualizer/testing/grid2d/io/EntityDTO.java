package edu.agh.idziak.asw.visualizer.testing.grid2d.io;

/**
 * Created by Tomasz on 27.08.2016.
 */
public class EntityDTO {
    private Integer row;
    private Integer col;
    private Integer targetRow;
    private Integer targetCol;

    public EntityDTO() {
    }

    private EntityDTO(Builder builder) {
        row = builder.row;
        col = builder.col;
        targetRow = builder.targetRow;
        targetCol = builder.targetCol;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Integer getRow() {
        return row;
    }

    public Integer getCol() {
        return col;
    }

    public Integer getTargetRow() {
        return targetRow;
    }

    public Integer getTargetCol() {
        return targetCol;
    }


    public static final class Builder {
        private Integer row;
        private Integer col;
        private Integer targetRow;
        private Integer targetCol;

        private Builder() {
        }

        public Builder row(Integer val) {
            row = val;
            return this;
        }

        public Builder col(Integer val) {
            col = val;
            return this;
        }

        public Builder targetRow(Integer val) {
            targetRow = val;
            return this;
        }

        public Builder targetCol(Integer val) {
            targetCol = val;
            return this;
        }

        public EntityDTO build() {
            return new EntityDTO(this);
        }
    }
}
