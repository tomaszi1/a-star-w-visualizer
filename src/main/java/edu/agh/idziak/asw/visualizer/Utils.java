package edu.agh.idziak.asw.visualizer;

import java.util.function.Supplier;

/**
 * Created by Tomasz on 04.09.2016.
 */
public class Utils {
    public static void checkNotNullOrThrow(Supplier<RuntimeException> supplier, Object... objects) {
        for (Object object : objects) {
            if (object == null)
                throw supplier.get();
        }
    }
}
