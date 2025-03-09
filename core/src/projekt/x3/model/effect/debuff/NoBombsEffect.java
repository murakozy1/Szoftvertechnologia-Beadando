package x3.model.effect.debuff;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import x3.model.effect.TimedEffect;
import x3.model.game.GameEngine;
import x3.model.game.objects.Player;

/**
 * A supplementary object for the {@link GameEngine game engine} that describes a {@link TimedEffect}
 * which temporarily removes the given {@link Player Player's} ability to place bombs.
 */
public final class NoBombsEffect extends TimedEffect {
    /**
     * Only constructor.
     * <br>
     * Initializes this {@link Sprite}'s {@link Texture texture},
     * that will be used by the {@link GameEngine game engine},
     * and sets the duration of this effect to 5000ms.
     * @param texture the texture of this {@link Sprite}.
     */
    public NoBombsEffect(Texture texture) {
        super(texture, 5000);
    }

    /**
     * Records the {@link System#currentTimeMillis() time} into {@link TimedEffect#startTime},
     * and changes the maximum number of bombs the given {@link Player Player} can have placed to 0.
     * @param p the {@link Player} that receives this effect.
     */
    @Override
    public void apply(Player p) {
        startTime = System.currentTimeMillis();
        p.bombsMax = 0;
    }

    /**
     * Removes this <code>TimedEffect</code> from the given {@link Player}.
     * <br>
     * This method resets the maximum number of bombs the {@link Player Player} can have placed
     * to its unique default value.
     * @param p the {@link Player} that needs this effect removed.
     */
    @Override
    public void remove(Player p) {
        p.bombsMax = p.defaultBombsMax;
    }
}
