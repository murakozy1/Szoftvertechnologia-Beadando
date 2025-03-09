package x3.model.game.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class Monster extends Sprite {
    private static final Random RANDOM = new Random();

    /**
     * The movement speed of this <code>Monster</code>.
     */
    public final int moveSpeed = 10;
    /**
     * The current direction of this <code>Monster</code>.
     */
    public final Vector2 direction = new Vector2(0,0);
    private boolean dead = false;

    /**
     * Sets the monster's direction to a random value.
     */
    public void setRandomDir(){
        switch (RANDOM.nextInt(0, 4)) {
            case 0 -> this.direction.set(moveSpeed, 0);
            case 1 -> this.direction.set(-moveSpeed, 0);
            case 2 -> this.direction.set(0, moveSpeed);
            default -> this.direction.set(0, -moveSpeed);
        }
    }

    /**
     * Marks this <code>Monster</code> as dead, signalling to the engine that it can be removed.
     */
    public void markDead() {
        dead = true;
    }

    /**
     * Returns whether this <code>Monster</code> is dead.
     * @return true if this <code>Monster</code> is dead.
     */
    public boolean isDead() {
        return dead;
    }
}
