package x3.model.game.suppliers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import x3.model.game.GameEngine;
import x3.model.game.objects.Bomb;
import x3.model.game.objects.Player;

import java.util.ArrayDeque;
import java.util.Queue;

import static x3.model.util.CollisionFilters.*;

/**
 * A BombPool implements object pooling to efficiently create and distribute
 * {@link Bomb} objects and their containing {@link Body bodies} to the {@link GameEngine game engine}.
 * <br>
 * Body definitions and shapes are reused between body instantiations, and disposed of when this object's
 * lifecycle ends and {@link Disposable#dispose()} is called on it.
 */
public class BombPool implements Disposable {
    private final World world;
    private final float PPM;

    private final BodyDef bodyDef;
    private final PolygonShape shape;

    private final Filter solidFilter;
    private final Filter sensorFilter;

    private final Texture bomb1Texture;
    private final Texture bomb2Texture;
    private final Texture bomb3Texture;

    private final float bombSize;

    private final Queue<Body> bombQueue;

    /**
     * Only constructor.
     * <br>
     * This constructor stores the passed arguments in fields,
     * and initializes the common objects, textures and filters required for instantiating explosions.
     *
     * @param world    the {@link World} where the objects will be created by this pool.
     * @param PPM      Pixels Per Meter.
     * @param bombSize the size of the bomb texture and body.
     * @param state1 the texture of the bombs in their first state.
     * @param state2 the texture of the bombs in their second state.
     * @param state3 the texture of the bombs in their third state.
     */
    public BombPool(World world, float PPM, float bombSize, Texture state1, Texture state2, Texture state3) {
        this.world = world;
        this.PPM = PPM;
        this.bombSize = bombSize;

        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.bullet = true;

        shape = new PolygonShape();
        shape.setAsBox(bombSize / 2, bombSize / 2, new Vector2(PPM / 2, PPM / 2), 0);

        solidFilter = new Filter();
        solidFilter.categoryBits = 0;
        solidFilter.maskBits = 0;

        sensorFilter = new Filter();
        sensorFilter.categoryBits = ALL_BOMBS;
        sensorFilter.maskBits = ALL_PLAYERS | MONSTER;

        bomb1Texture = state1;
        bomb2Texture = state2;
        bomb3Texture = state3;

        bombQueue = new ArrayDeque<>(0);
    }

    /**
     * Returns a {@link Bomb} from the queue.
     * <br>
     * The body is placed at the target <code>position</code>,
     * and the internal {@link Bomb} object is created with its default texture.
     * <br>
     * If the queue doesn't currently contain an object, a new one is {@link #buildBomb() created}.
     *
     * @param position the new position of the {@link Body}.
     * @param owner    the {@link Bomb#owner owner} of this bomb.
     * @return the body retrieved from the queue.
     */
    public Body getBomb(final Vector2 position, final Body owner) {
        if (bombQueue.isEmpty()) {
            buildBomb();
        }

        Body target = bombQueue.remove();
        initFilters(target);
        target.setTransform(position, 0.0f);

        Bomb bomb = new Bomb((Player) owner.getUserData(), bomb2Texture, bomb3Texture);
        bomb.setTexture(bomb1Texture);
        bomb.setSize(bombSize, bombSize);
        bomb.setOriginCenter();
        bomb.setPosition(target.getPosition().x + (PPM - bombSize) / 2, target.getPosition().y + (PPM - bombSize) / 2);

        target.setUserData(bomb);
        target.setActive(true);

        return target;
    }

    /**
     * Puts the given {@link Body} back into the queue for reuse.
     * <br>
     * This method deactivates the given body,
     * and removes its user data before returning it to the queue.
     *
     * @param body the body to be returned.
     */
    public void returnBomb(final Body body) {
        body.setUserData(null);
        body.setActive(false);
        bombQueue.add(body);
    }

    /**
     * Initializes the filters of the body's fixtures.
     * <br>
     * As required by the {@link GameEngine}, the bomb's first fixture is solid and
     * its second fixture is used for collision detection with {@link Player Players} to
     * dynamically change the solid fixture's filters.
     * <br>
     * The body passed to this method must contain at least two fixtures.
     *
     * @param body the body whose fixtures will be updated with the filters.
     */
    private void initFilters(Body body) {
        body.getFixtureList().get(0).setFilterData(solidFilter);
        body.getFixtureList().get(1).setFilterData(sensorFilter);
    }

    /**
     * Creates a new body and puts it in the queue.
     * <br>
     * Bodies created by this method contain no user data. User data is added when the body is retrieved from
     * the queue.
     *
     * @see #getBomb(Vector2, Body)
     */
    private void buildBomb() {
        Body body = world.createBody(bodyDef);

        Fixture solidFixture = body.createFixture(shape, 0.0f);
        Fixture sensorFixture = body.createFixture(shape, 0.0f);
        initFilters(body);
        solidFixture.setSensor(false);
        sensorFixture.setSensor(true);

        bombQueue.add(body);
    }

    @Override
    public void dispose() {
        shape.dispose();
    }
}
