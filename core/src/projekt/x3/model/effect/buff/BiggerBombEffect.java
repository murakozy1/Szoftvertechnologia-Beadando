package x3.model.effect.buff;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import x3.model.effect.Effect;
import x3.model.effect.debuff.SmallBombEffect;
import x3.model.game.GameEngine;
import x3.model.game.objects.Player;

/**
 * A supplementary object for the {@link GameEngine game engine} that describes an {@link Effect}
 * which increases the given {@link Player}'s bombs' radius by one game unit.
 */
public final class BiggerBombEffect extends Effect {
    /**
     * Only constructor.
     * <br>
     * Initializes this {@link Sprite}'s {@link Texture texture},
     * that will be used by the {@link GameEngine game engine}.
     * @param texture the texture of this {@link Sprite}.
     * @see Sprite
     */
    public BiggerBombEffect(Texture texture) {
        super(texture);
    }

    /**
     * Applies this <code>Effect</code> to the given {@link Player}.
     * <br>
     * This method increments the Players {@link Player#defaultBombRadius default bomb radius},
     * and also its current bombs radius if the Player doesn't have a {@link SmallBombEffect} applied to it.
     * @param p the {@link Player} that receives this effect.
     */
    @Override
    public void apply(Player p) {
        p.defaultBombRadius++;
        if (!p.hasEffect(SmallBombEffect.class)) {
            p.bombRadius++;
        }
    }
}
