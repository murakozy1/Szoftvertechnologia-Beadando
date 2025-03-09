package x3.model.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import x3.model.effect.debuff.ForcedBombsEffect;
import x3.model.game.GameEngine;

import java.util.HashSet;
import java.util.Set;

/**
 * A <code>Bomb</code> is a supplementary object that contains additional information about a bomb, that is used
 * by the {@link GameEngine game engine}.
 * <br>
 * Bombs keep track of their own status, their owners and their texture settings.
 */
public class Bomb extends Sprite {
    /**
     * Whether the bomb has already blown up or not.
     */
    public boolean hasBlown;
    /**
     * Whether the bomb is being forced to blow up by the owner's {@link ForcedBombsEffect}.
     */
    public boolean forceBlow = false;
    /**
     * The owner of this bomb.
     * <br>
     * The owner defines the radius of the bomb.
     */
    public final Player owner;
    private final long placeTime;
    private final long waitTime = 2000;
    private final Texture texture2;
    private final Texture texture3;
    private int age;

    /**
     * A set of {@link Player Players} that are allowed to go through the bomb.
     */
    public final Set<Player> playersAllowedThrough = new HashSet<>();
    /**
     * Whether the bomb has already finished listing the players that were over it when it was created.
     */
    public boolean hasSetPlayers = false;

    /**
     * Only constructor.
     * <br>
     * Records the time and sets the owner so its data can be retrieved later.
     * @param owner the owner of this bomb.
     * @param texture2 the texture for the second state of the bomb.
     * @param texture3 the texture for the third state of the bomb.
     */
    public Bomb(Player owner, Texture texture2, Texture texture3){
        this.owner = owner;
        this.placeTime = System.currentTimeMillis();

        this.texture2 = texture2;
        this.texture3 = texture3;
    }

    /**
     * Increases the age of the bomb.
     * <br>
     * Age is tracked for collision filtering. The bomb's age represents the number of frames it has spent alive
     * in the game.
     */
    public void stepAge() {
        age++;
    }

    /**
     * Returns the bomb's age.
     * @return the bomb's age
     * @see #stepAge()
     */
    public int getAge() {
        return age;
    }

    /**
     * Returns whether this bomb is allowed to blow up.
     * <br>
     * A bomb is allowed to blow up either if it is being {@link #forceBlow forced},
     * or if the {@link #waitTime wait time} has elapsed.
     * @return true if the bomb is ready to blow up.
     */
    public boolean canBlow() {
        return forceBlow || System.currentTimeMillis() >= placeTime + waitTime;
    }

    /**
     * Sets the bombs texture based on how long it's been alive.
     * <br>
     * Second texture is applied 250ms after placement, third is applied 500ms after placement.
     */
    public void checkBombState(){
        if ((placeTime + waitTime) - System.currentTimeMillis() <= 250){
            this.setTexture(texture3);
        }
        else if ((placeTime + waitTime) - System.currentTimeMillis() <= 500){
            this.setTexture(texture2);
        }
    }
}
