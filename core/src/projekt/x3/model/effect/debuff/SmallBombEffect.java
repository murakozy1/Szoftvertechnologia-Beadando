package x3.model.effect.debuff;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import x3.model.effect.TimedEffect;
import x3.model.game.GameEngine;
import x3.model.game.objects.Player;

/**
 * A supplementary object for the {@link GameEngine game engine} that describes a {@link TimedEffect}
 * which temporarily reduces the radius of the given {@link Player Player's} bombs to one game unit.
 */
public final class SmallBombEffect extends TimedEffect {
    /**
     * Only constructor.
     * <br>
     * Initializes this {@link Sprite}'s {@link Texture texture},
     * that will be used by the {@link GameEngine game engine},
     * and sets the duration of this effect to 5000ms.
     * @param texture the texture of this {@link Sprite}.
     */
    public SmallBombEffect(Texture texture) {
        super(texture, 5000);
    }

    /**
     * Records the {@link System#currentTimeMillis() time} into {@link TimedEffect#startTime},
     * and sets radius of the given {@link Player Player's} bombs to 1.
     * @param p the {@link Player} that receives this effect.
     */
    @Override
    public void apply(Player p) {
        startTime = System.currentTimeMillis();
        p.bombRadius = 1;
    }

    /**
     * Removes this <code>TimedEffect</code> from the given {@link Player}.
     * <br>
     * This method resets the radius of the given {@link Player Player's} bombs
     * to its unique default value.
     * @param p the {@link Player} that needs this effect removed.
     */
    @Override
    public void remove(Player p) {
        p.bombRadius = p.defaultBombRadius;
    }
}
