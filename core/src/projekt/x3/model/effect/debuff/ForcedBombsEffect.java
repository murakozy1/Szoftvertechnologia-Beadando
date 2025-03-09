package x3.model.effect.debuff;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import x3.model.effect.TimedEffect;
import x3.model.game.GameEngine;
import x3.model.game.objects.Player;

/**
 * A supplementary object for the {@link GameEngine game engine} that describes a {@link TimedEffect}
 * which forces the given {@link Player} to place its bombs.
 * <br>
 * Logic connected to this effect is managed by the {@link GameEngine game engine}. This object is used
 * only for the {@link Player Players} to track whether this kind of effect is applied to them.
 */
public final class ForcedBombsEffect extends TimedEffect {
    /**
     * Only constructor.
     * <br>
     * Initializes this {@link Sprite}'s {@link Texture texture},
     * that will be used by the {@link GameEngine game engine},
     * and sets the duration of this effect to 5000ms.
     * @param texture the texture of this {@link Sprite}.
     */
    public ForcedBombsEffect(Texture texture) {
        super(texture, 5000);
    }

    /**
     * Records the {@link System#currentTimeMillis() time} into {@link TimedEffect#startTime}.
     * @param p the {@link Player} that receives this effect.
     */
    @Override
    public void apply(Player p) {
        startTime = System.currentTimeMillis();
    }

    /**
     * Removes this <code>TimedEffect</code> from the given {@link Player}.
     * <br>
     * This object has no actual effect on the {@link Player}, so this method has no effect on it either.
     * @param p the {@link Player} that needs this effect removed.
     */
    @Override
    public void remove(Player p) { }
}
