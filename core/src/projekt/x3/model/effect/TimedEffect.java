package x3.model.effect;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import x3.model.game.GameEngine;
import x3.model.game.objects.Box;
import x3.model.game.objects.Player;

/**
 * A <code>TimedEffect</code> is a supplementary object that contains additional information about a temporary effect
 * that is stored in a {@link Box} or can be picked up by a {@link Player},
 * and is utilized by the {@link GameEngine game engine}.
 * <br>
 * A <code>TimedEffect</code> keeps track of when its effects were applied to a {@link Player}, how long its effects
 * are allowed to last, and it defines methods for removing its effects from the Player that previously received them.
 */
public abstract class TimedEffect extends Effect {
    /**
     * The timestamp recorded when the effect was applied.
     * @see System#currentTimeMillis()
     */
    public long startTime;
    /**
     * The duration of this <code>TimedEffect</code> measured in milliseconds.
     */
    public final long duration;

    /**
     * Only constructor.
     * <br>
     * This constructor is used to set the {@link Texture} of this {@link Sprite},
     * and the {@link TimedEffect#duration duration} of its effect.
     * @param texture the texture of this {@link Sprite}.
     * @param duration the duration after which this <code>TimedEffect</code> becomes invalid.
     */
    protected TimedEffect(Texture texture, long duration) {
        super(texture);
        this.duration = duration;
    }

    /**
     * Removes this <code>TimedEffect</code> from the given {@link Player}.
     * @param p the {@link Player} that needs this effect removed.
     */
    public abstract void remove(Player p);

    /**
     * Returns whether the {@link TimedEffect#duration duration} of this effect has expired, relative to the time recorded in {@link #apply}.
     * @return true if the duration has passed.
     */
    public boolean hasExpired() {
        return startTime + duration <= System.currentTimeMillis();
    }
}
