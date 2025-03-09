package x3.model.effect.debuff;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import x3.model.effect.TimedEffect;
import x3.model.game.GameEngine;
import x3.model.game.objects.Player;

/**
 * A supplementary object for the {@link GameEngine game engine} that describes a {@link TimedEffect}
 * which temporarily reduces the given {@link Player Player's} movement speed.
 */
public final class SlowEffect extends TimedEffect {
    /**
     * The temporary value that the {@link Player Player's} movement speed will be set to.
     * @see Player#moveSpeed
     */
    public final int newMoveSpeed = 5;

    /**
     * Only constructor.
     * <br>
     * Initializes this {@link Sprite}'s {@link Texture texture},
     * that will be used by the {@link GameEngine game engine},
     * and sets the duration of this effect to 5000ms.
     * @param texture the texture of this {@link Sprite}.
     */
    public SlowEffect(Texture texture) {
        super(texture, 5000);
    }

    /**
     * Records the {@link System#currentTimeMillis() time} into {@link TimedEffect#startTime},
     * and sets the movement speed of the given {@link Player Player}
     * to the {@link SlowEffect#newMoveSpeed temporary value}.
     * @param p the {@link Player} that receives this effect.
     */
    @Override
    public void apply(Player p) {
        startTime = System.currentTimeMillis();
        p.moveSpeed = newMoveSpeed;
    }

    /**
     * Removes this <code>TimedEffect</code> from the given {@link Player}.
     * <br>
     * This method resets the movement speed of the given {@link Player}
     * to its unique default value.
     * @param p the {@link Player} that needs this effect removed.
     */
    @Override
    public void remove(Player p) {
        p.moveSpeed = p.defaultMoveSpeed;
    }
}
