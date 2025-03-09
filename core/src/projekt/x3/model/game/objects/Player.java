package x3.model.game.objects;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import x3.model.effect.Effect;
import x3.model.effect.TimedEffect;
import x3.model.effect.debuff.ForcedBombsEffect;
import x3.model.effect.debuff.NoBombsEffect;
import x3.model.effect.debuff.SlowEffect;
import x3.model.effect.debuff.SmallBombEffect;
import x3.model.game.GameEngine;
import x3.model.util.KeybindManager;
import x3.model.util.exception.NoBombsDownException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A <code>Player</code> is a supplementary object that contains additional information about a player, that is utilized
 * by the {@link GameEngine game engine}.
 * <br>
 * Players keep track of the number of bombs they placed and whether they can place more,
 * they also store the {@link Keys keycodes} associated with their movement input keys,
 * and they manage the {@link Effect Effects} currently affecting them.
 * <code>Player</code> objects are bound to Bodies by the {@link GameEngine game engine}.
 */
public class Player extends Sprite {
    /**
     * The default movement speed of this <code>Player</code>.
     */
    public final int defaultMoveSpeed = 15;
    /**
     * A mapping containing the names and instances of each {@link TimedEffect} (wrapped in an {@link Optional})
     * that are applied to this <code>Player</code>.
     * <br>
     * The {@link Optional} values in the mapping are empty if the given effect is currently not applied to this <code>Player</code>.
     */
    public final Map<Class<? extends TimedEffect>, Optional<TimedEffect>> effectMap;
    /**
     * The global index of this <code>Player</code>, with respect to the other Players in the game. Assigned by constructors.
     * <br>
     * <code>Player</code> indexes start from 0.
     */
    public final int ind;
    /**
     * The default maximum of the number of bombs this <code>Player</code> has.
     */
    public int defaultBombsMax = 1;
    /**
     * The current maximum of the number of bombs this <code>Player</code> has.
     */
    public int bombsMax = 1;
    /**
     * The current number of bombs this <code>Player</code> has placed down.
     */
    public int bombsPlaced = 0;
    /**
     * The default radius of bombs placed by this <code>Player</code>.
     */
    public int defaultBombRadius = 2;
    /**
     * The current radius of bombs placed by this <code>Player</code>.
     */
    public int bombRadius = 2;
    /**
     * The current movement speed of this <code>Player</code>.
     * <br>
     * Force impulses are applied to the <code>Player</code>'s {@link Body body}
     * only if its velocity is not above its current movement speed.
     */
    public int moveSpeed = 15;
    /**
     * Keycode of upwards movement for this <code>Player</code>.
     * <br>
     * Keycodes are defined according to {@link Keys Input.Keys}.
     */
    public int upKey;
    /**
     * Keycode of right movement for this <code>Player</code>.
     * <br>
     * Keycodes are defined according to {@link Keys Input.Keys}.
     */
    public int rightKey;
    /**
     * Keycode of right movement for this <code>Player</code>.
     * <br>
     * Keycodes are defined according to {@link Keys Input.Keys}.
     */
    public int downKey;
    /**
     * Keycode of downwards movement for this <code>Player</code>.
     * <br>
     * Keycodes are defined according to {@link Keys Input.Keys}.
     */
    public int leftKey;
    /**
     * Keycode of bomb placement for this <code>Player</code>.
     * <br>
     * Keycodes are defined according to {@link Keys Input.Keys}.
     */
    public int bombKey;
    private boolean dead = false;

    {
        effectMap = new HashMap<>();
        effectMap.put(NoBombsEffect.class, Optional.empty());
        effectMap.put(SmallBombEffect.class, Optional.empty());
        effectMap.put(SlowEffect.class, Optional.empty());
        effectMap.put(ForcedBombsEffect.class, Optional.empty());
    }

    /**
     * Secondary constructor for the <code>Player</code>.
     * <br>
     * Only for testing purposes.
     * This constructor sets the index and keybindings of this <code>Player</code> to -1 to indicate that objects created by
     * this constructor are not suitable for in-game use.
     */
    public Player() {
        this.ind = -1;
        upKey = -1;
        rightKey = -1;
        downKey = -1;
        leftKey = -1;
        bombKey = -1;
    }

    /**
     * Primary constructor.
     * <br>
     * Initializes the index of the <code>Player</code>, and its keybindings based on the index.
     *
     * @param ind the global {@link Player#ind index} of this <code>Player</code>
     * @see Player#upKey
     * @see Player#downKey
     * @see Player#rightKey
     * @see Player#leftKey
     * @see Player#bombKey
     */
    public Player(int ind) {
        this.ind = ind;
        Map<String, Integer> keys = KeybindManager.getKeybinds(ind);
        upKey = keys.get("UP");
        rightKey = keys.get("RIGHT");
        downKey = keys.get("DOWN");
        leftKey = keys.get("LEFT");
        bombKey = keys.get("BOMB");
    }

    /**
     * Returns whether this <code>Player</code> can currently place a bomb.
     * <br>
     * A <code>Player</code> can place a bomb if the number of {@link #bombsPlaced bombs placed}
     * is less than the {@link #bombsMax current maximum} of bombs it can place.
     *
     * @return false if the <code>Player</code> has already placed all bombs available to it.
     */
    public boolean canPlace() {
        return bombsPlaced < bombsMax;
    }

    /**
     * Indicates to the <code>Player</code> that a bomb has been placed by it.
     * <br>
     * This method increments the number of {@link #bombsPlaced bombs placed} by this <code>Player</code>.
     * Actual {@link Bomb Bomb} placement and management
     * is handled by the {@link GameEngine game engine}.
     */
    public void bombPlace() {
        bombsPlaced++;
    }

    /**
     * Returns whether a given {@link TimedEffect} is currently applied to this <code>Player</code>.
     * <br>
     * Currently applied effects are stored in an {@link Player#effectMap internal mapping}.
     *
     * @param effectClass the class of the {@link TimedEffect} that needs checking.
     * @return false if the {@link Player#effectMap internal mapping} contains an {@link Optional#isPresent() empty}
     * {@link Optional} at the given key.
     */
    public boolean hasEffect(Class<? extends TimedEffect> effectClass) {
        return effectMap.get(effectClass).isPresent();
    }

    /**
     * Indicates to the <code>Player</code> that a bomb it has previously placed has blown up.
     * <br>
     * This method decrements the number of bombs placed by this <code>Player</code>. Actual {@link Bomb Bomb} placement
     * and management is handled by the {@link GameEngine game engine}. Number of bombs placed
     * down currently cannot fall below <code>0</code>.
     *
     * @throws NoBombsDownException when the number of bombs placed is already <code>0</code>.
     */
    public void bombBlew() throws NoBombsDownException {
        if (bombsPlaced == 0) {
            throw new NoBombsDownException();
        }
        bombsPlaced--;
    }

    /**
     * Applies the given {@link Effect} to this <code>Player</code>.
     * <br>
     * Calls {@link Effect#apply Effect.apply(this)}.
     *
     * @param effect the {@link Effect} to apply to this <code>Player</code>.
     */
    public void apply(Effect effect) {
        effect.apply(this);
    }

    /**
     * Applies the given {@link TimedEffect} to this <code>Player</code>.
     * <br>
     * If the {@link TimedEffect} is already active, the previous instance's effects are {@link #removeEffect removed},
     * and it is replaced with the new one (the argument) in the {@link Player#effectMap internal mapping}.
     * Finally, {@link Effect#apply Effect.apply(this)} is called.
     *
     * @param effect the {@link TimedEffect} to apply to this <code>Player</code>.
     */
    public void apply(TimedEffect effect) {
        removeEffect(effect.getClass());
        effectMap.replace(effect.getClass(), Optional.of(effect));
        effect.apply(this);
    }

    private void removeEffect(Class<? extends TimedEffect> effectClass) {
        Optional<TimedEffect> effect = effectMap.get(effectClass);
        if (effect.isPresent()) {
            effect.get().remove(this);
            effectMap.replace(effectClass, Optional.empty());
        }
    }

    /**
     * Manages the currently active {@link Effect effects} on this <code>Player</code>.
     * <br>
     * This method loops over the effects stored in the <code>Player</code>'s {@link Player#effectMap internal mapping}
     * and if it finds an expired {@link TimedEffect}, it removes its effects from the <code>Player</code>,
     * then replaces the value in the mapping with an empty {@link Optional}.
     */
    public void manageEffects() {
        for (Map.Entry<Class<? extends TimedEffect>, Optional<TimedEffect>> entry : effectMap.entrySet()) {
            Optional<TimedEffect> effect = entry.getValue();
            if (effect.isPresent() && effect.get().hasExpired()) {
                effect.get().remove(this);
                entry.setValue(Optional.empty());
            }
        }
    }

    /**
     * Marks this <code>Player</code> as dead.
     * <br>
     * This status variable is used by the {@link GameEngine game engine}
     * to determine which <code>Player</code> is still playing.
     */
    public void markDead() {
        dead = true;
    }

    /**
     * Returns whether this <code>Player</code> is still playing the game.
     * <br>
     * This status variable is used by the {@link GameEngine game engine}
     * to determine which <code>Player</code> is still playing.
     *
     * @return false if the <code>Player</code> is still playing.
     */
    public boolean isDead() {
        return dead;
    }

    @Override
    public String toString() {
        return "Player#" + ind;
    }
}
