package x3.model.util.exception;

import x3.model.game.GameEngine;
import x3.model.game.objects.Player;

/**
 * This class describes an exception that occurs when the {@link GameEngine game engine}
 * attempts to signal to a {@link Player} that one its bombs has blown up, but the Player has no bombs placed down.
 */
public class NoBombsDownException extends IllegalStateException {
    /**
     * Default constructor.
     */
    public NoBombsDownException() {
        super("No bombs placed currently by this player [default].");
    }
}
