package x3.model.util;

import com.badlogic.gdx.physics.box2d.*;
import x3.model.effect.Effect;
import x3.model.effect.TimedEffect;
import x3.model.game.GameEngine;
import x3.model.game.objects.Bomb;
import x3.model.game.objects.Monster;
import x3.model.game.objects.Player;

/**
 * A CollisionListener is an object that handles collisions for the {@link GameEngine game engine}.
 * <br>
 * This object handles contact beginnings and endings for several interactions, including:
 * <ul>
 *     <li>{@link Player} - {@link Bomb}</li>
 *     <li>{@link Player} - {@link Effect}</li>
 *     <li>{@link Player} - {@link Monster}</li>
 *     <li>{@link Player} - <code>Boundary</code></li>
 * </ul>
 */
public class CollisionListener implements ContactListener {
    /**
     * Invoked when a {@link Player} and a {@link Bomb} collide.
     * <br>
     * This method checks if the bomb has {@link Bomb#hasSetPlayers set its list of players}
     * that it allows to pass through, and if it hasn't, it adds the detected player to the list.
     * @param playerBody {@link Body body} of the {@link Player}.
     * @param bombBody {@link Body body} of the {@link Bomb}.
     */
    private void beginPlayerOnBomb(Body playerBody, Body bombBody) {
        Bomb bomb = (Bomb) bombBody.getUserData();
        if (bomb.hasSetPlayers) {
            return;
        }
        Player player = (Player) playerBody.getUserData();
        bomb.playersAllowedThrough.add(player);
    }

    /**
     * Invoked when a {@link Player} and a {@link Bomb} stop colliding.
     * <br>
     * This method checks if the bomb has {@link Bomb#hasSetPlayers set its list of players}
     * that it allows to pass through, and if it has, it removes the detected player from the list,
     * to indicate that it is no longer allowed through.
     * @param playerBody {@link Body body} of the {@link Player}.
     * @param bombBody {@link Body body} of the {@link Bomb}.
     */
    private void endPlayerOnBomb(Body playerBody, Body bombBody) {
        Bomb bomb = (Bomb) bombBody.getUserData();
        if (!bomb.hasSetPlayers) {
            return;
        }

        Player player = (Player) playerBody.getUserData();
        bomb.playersAllowedThrough.remove(player);
    }

    private void beginPlayerOnEffect(Body playerBody, Body effectBody) {
        Player player = (Player) playerBody.getUserData();
        Effect effect = (Effect) effectBody.getUserData();

        if (effect instanceof TimedEffect) {
            player.apply((TimedEffect) effect);
        } else {
            player.apply(effect);
        }

        effect.bodyDead = true;
    }

    private void beginPlayerOnMonster(Body playerBody) {
        ((Player) playerBody.getUserData()).markDead();
    }

    private void beginPlayerOnBoundary(Body playerBody) {
        ((Player) playerBody.getUserData()).markDead();
    }

    private void beginMonsterOnBoundary(Body monsterBody){
        ((Monster) monsterBody.getUserData()).markDead();
    }

    private boolean is(Body body, Class<?> cls) {
        return cls.isInstance(body.getUserData());
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture f1 = contact.getFixtureA();
        Fixture f2 = contact.getFixtureB();

        Body b1 = f1.getBody();
        Body b2 = f2.getBody();

        if (is(b1, Bomb.class) && is(b2, Player.class)) {
            beginPlayerOnBomb(b2, b1);
        } else if (is(b2, Bomb.class) && is(b1, Player.class)) {
            beginPlayerOnBomb(b1, b2);
        } else if (is(b1, Effect.class) && is(b2, Player.class)) {
            beginPlayerOnEffect(b2, b1);
        } else if (is(b2, Effect.class) && is(b1, Player.class)) {
            beginPlayerOnEffect(b1, b2);
        } else if (is(b1, Monster.class) && is(b2, Player.class)) {
            beginPlayerOnMonster(b2);
        } else if (is(b2, Monster.class) && is(b1, Player.class)) {
            beginPlayerOnMonster(b1);
        } else if (is(b1, Pair.class) && is(b2, Player.class)) {
            beginPlayerOnBoundary(b2);
        } else if (is(b2, Pair.class) && is(b1, Player.class)) {
            beginPlayerOnBoundary(b1);
        } else if (is(b1, Pair.class) && is(b2, Monster.class)){
            beginMonsterOnBoundary(b2);
        } else if (is(b2, Pair.class) && is(b1, Monster.class)){
            beginMonsterOnBoundary(b1);
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture f1 = contact.getFixtureA();
        Fixture f2 = contact.getFixtureB();

        Body b1 = f1.getBody();
        Body b2 = f2.getBody();

        if (is(b1, Bomb.class) && is(b2, Player.class)) {
            endPlayerOnBomb(b2, b1);
        } else if (is(b2, Bomb.class) && is(b1, Player.class)) {
            endPlayerOnBomb(b1, b2);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }
}