package pl.edu.agh.idziak.asw.visualizer.testing.grid2d.model;

/**
 * Created by Tomasz on 27.08.2016.
 */
public class Entity {
    private Integer id;

    public Entity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "E" + id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public static Entity of(Integer id) {
        return new Entity(id);
    }


}
