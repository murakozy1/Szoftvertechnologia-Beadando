package x3.model.game.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import x3.model.effect.Effect;
import x3.model.game.GameEngine;

import java.util.Optional;

/**
 * A <code>Box</code> is a supplementary object that contains additional information about a box, that is utilized
 * by the {@link GameEngine game engine}.
 * <br>
 * <code>Boxes</code> may optionally hold an {@link Effect}.
 */
public class Box extends Sprite {
    private final Optional<Effect> effect;

    /**
     * Only constructor.
     * <br>
     * Optionally accepts an {@link Effect}.
     * @param effect the {@link Effect} that this <code>Box</code> holds.
     */
    public Box(Optional<Effect> effect){
        this.effect = effect;
    }

    /**
     * Returns the {@link Effect} held by this <code>Box</code>.
     * @return the {@link Effect}.
     */
    public Optional<Effect> getEffect() {
        return effect;
    }
}
