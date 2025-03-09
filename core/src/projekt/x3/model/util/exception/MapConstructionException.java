package x3.model.util.exception;

import x3.model.map.GameMap;

/**
 * This class describes an exception that occurs during the initialization of a {@link GameMap},
 * if the map fails some check.
 * <br>
 * Checks are performed on a map during instantiation.
 */
public class MapConstructionException extends RuntimeException {
    /**
     * Default constructor.
     */
    public MapConstructionException() {
        super("Map cannot be constructed.");
    }

    /**
     * Primary constructor.
     * @param msg the message that gets printed with the stacktrace.
     */
    public MapConstructionException(String msg) {
        super(msg);
    }
}