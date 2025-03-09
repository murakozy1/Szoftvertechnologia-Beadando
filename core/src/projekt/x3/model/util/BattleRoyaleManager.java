package x3.model.util;

/**
 * This object stores information about the battle royale feature of the game.
 * <br>
 * Map shrinks and state are recorded and stored here for later querying.
 * A <code>BattleRoyaleManager</code> knows how shrunk the map is supposed to be.
 */
public class BattleRoyaleManager {
    private final long shrinkStart;
    private final long shrinkInterval;
    private final int maxShrinks;
    private int shrinksShrunk;
    private boolean startedShrinking;

    /**
     * Only constructor.
     * <br>
     * Sets the maximum number of shrinks, records the time and sets the shrink interval to 20 seconds.
     *
     * @param max the maximum number of shrinks.
     */
    public BattleRoyaleManager(int max) {
        maxShrinks = max;
        shrinkStart = System.currentTimeMillis() + 10_000;
        shrinkInterval = 20_000;
    }

    /**
     * Returns whether the game can start the shrinking.
     * <br>
     * The game can start the shrinking if it {@link #startShrinking() hasn't already},
     * and 10 seconds have passed since instantiation.
     *
     * @return true if the game is allowed to start the shrinking.
     */
    public boolean canStartShrinking() {
        return !startedShrinking && System.currentTimeMillis() - shrinkStart >= 0;
    }

    /**
     * Signals to this object that the game has started the shrinking already and to not allow state change anymore.
     */
    public void startShrinking() {
        startedShrinking = true;
    }

    /**
     * Returns whether the game has started the shrinking already.
     *
     * @return true if the game has started the shrinking.
     * @see #startShrinking()
     */
    public boolean getStartedShrinking() {
        return startedShrinking;
    }

    /**
     * Records a shrinking done by the game.
     * <br>
     * Internally, the number of completed shrinks is incremented.
     */
    public void shrink() {
        shrinksShrunk++;
    }

    /**
     * Returns the number of shrinks that are supposed to have happened since instantiation,
     * or the maximum if the number would exceed it.
     *
     * @return the number of expected shrinks.
     */
    private int getExpectedShrinks() {
        return Math.min((int) ((System.currentTimeMillis() - shrinkStart) / shrinkInterval), maxShrinks);
    }

    /**
     * Returns the remaining time until the next shrink is supposed to occur, in milliseconds.
     *
     * @return the remaining time in milliseconds.
     */
    public long timeUntilShrink() {
        return shrinkInterval - ((System.currentTimeMillis() - shrinkStart) % shrinkInterval) - (!startedShrinking ? shrinkInterval : 0);
    }

    /**
     * Returns whether the game is currently allowed to perform a shrink.
     *
     * @return true if the number of shrinks performed is less than the expected.
     */
    public boolean canShrink() {
        return getShrunk() < getExpectedShrinks();
    }

    /**
     * Returns how many shrinks have actually been performed by the game.
     *
     * @return the number of shrinks performed.
     */
    public int getShrunk() {
        return shrinksShrunk;
    }

    /**
     * Returns whether the shrinks are still allowed to occur.
     * <br>
     * Shrinking is not allowed if the expected number of shrinks has reached the maximum.
     *
     * @return true if the expected number of shrinks is less than the maximum.
     */
    public boolean isAtMax() {
        return getExpectedShrinks() >= maxShrinks;
    }
}
