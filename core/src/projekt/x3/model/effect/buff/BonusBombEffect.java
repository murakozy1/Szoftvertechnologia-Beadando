package x3.model.effect.buff;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import x3.model.effect.Effect;
import x3.model.effect.debuff.NoBombsEffect;
import x3.model.game.GameEngine;
import x3.model.game.objects.Player;

/**
 * A supplementary object for the {@link GameEngine game engine} that describes an {@link Effect}
 * which increases the maximum number of bombs the given {@link Player} can have placed.
 */
public final class BonusBombEffect extends Effect {
    /**
     * Only constructor.
     * <br>
     * Initializes this {@link Sprite}'s {@link Texture texture},
     * that will be used by the {@link GameEngine game engine}.
     * @param texture the texture of this {@link Sprite}.
     * @see Sprite
     */
    public BonusBombEffect(Texture texture) {
        super(texture);
    }

    /**
     * Applies this <code>Effect</code> to the given {@link Player}.
     * <br>
     * This method increments the Players {@link Player#defaultBombsMax default maximum of bombs} it's allowed to place,
     * and also its current maximum of bombs if the Player doesn't have a {@link NoBombsEffect} applied to it.
     * @param p the {@link Player} that receives this effect.
     */
    @Override
    public void apply(Player p) {
        p.defaultBombsMax++;
        if (!p.hasEffect(NoBombsEffect.class)) {
            p.bombsMax++;
        }
    }
}
