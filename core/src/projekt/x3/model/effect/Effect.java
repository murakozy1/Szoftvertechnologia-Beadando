package x3.model.effect;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import x3.model.game.GameEngine;
import x3.model.game.objects.Box;
import x3.model.game.objects.Player;

/**
 * An <code>Effect</code> is a supplementary object that contains additional information about an effect
 * that is stored in a {@link Box} or can be picked up by a {@link Player},
 * and is utilized by the {@link GameEngine game engine}.
 * <br>
 * An <code>Effect</code> keeps track of whether it's still allowed on the playing field, and it
 * contains methods for applying its unique effects to a given {@link Player}.
 */
public abstract class Effect extends Sprite {
    /**
     * Whether the body holding this effect is dead.
     */
    public boolean bodyDead = false;

    /**
     * Applies this <code>Effect</code> to the given {@link Player}.
     * @param p the {@link Player} that receives this effect.
     */
    public abstract void apply(Player p);

    /**
     * Only constructor.
     * <br>
     * Initializes the {@link Texture} of this <code>Effect</code>.
     * @param texture the texture of this {@link Sprite}.
     */
    protected Effect(Texture texture) {
        setTexture(texture);
    }
}
