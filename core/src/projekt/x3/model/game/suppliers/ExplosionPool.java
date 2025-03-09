package x3.model.game.suppliers;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import x3.model.game.GameEngine;
import x3.model.game.objects.Explosion;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

/**
 * An ExplosionPool implements object pooling to efficiently create and distribute
 * {@link Explosion} objects and their containing {@link Body bodies} to the {@link GameEngine game engine}.
 * <br>
 * Body definitions and shapes are reused between body instantiations, and disposed of when this object's
 * lifecycle ends and {@link Disposable#dispose()} is called on it.
 */
public class ExplosionPool implements Disposable {
    private final World world;
    private final float PPM;

    private final BodyDef bodyDef;
    private final PolygonShape shape;

    private final Queue<Body> explosionQueue;
    private final List<SimpleEntry<FileHandle, FileHandle>> particles;

    /**
     * Only constructor.
     * <br>
     * This constructor stores the passed arguments in fields,
     * and initializes the common objects required for instantiating explosions.
     *
     * @param world     the {@link World} where the objects will be created by this pool.
     * @param PPM       Pixels Per Meter.
     * @param particles a list of particle file - particle image pairs.
     */
    public ExplosionPool(World world, float PPM, List<SimpleEntry<FileHandle, FileHandle>> particles) {
        this.world = world;
        this.PPM = PPM;
        this.particles = particles;

        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        shape = new PolygonShape();
        shape.setAsBox(PPM / 2, PPM / 2, new Vector2(PPM / 2, PPM / 2), 0);

        explosionQueue = new ArrayDeque<>(0);
    }

    /**
     * Returns an {@link Explosion} from the queue.
     * <br>
     * The body is placed at the target <code>position</code>,
     * and the internal {@link Explosion} object is created a delay multiplier of <code>offset</code>.
     * Returned bodies are always activated.
     * <br>
     * If the queue doesn't currently contain an object, a new one is {@link #buildExplosion() created}.
     *
     * @param position the new position of the {@link Body}.
     * @param offset   the delay multiplier of the {@link Explosion}.
     * @return the body retrieved from the queue.
     */
    public Body getExplosion(final Vector2 position, final int offset) {
        if (explosionQueue.isEmpty()) {
            buildExplosion();
        }

        Body target = explosionQueue.remove();
        target.setTransform(position, 0.0f);

        target.setUserData(new Explosion(particles, offset, world, position, PPM));
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
    public void returnExplosion(final Body body) {
        body.setUserData(null);
        body.setActive(false);
        explosionQueue.add(body);
    }

    /**
     * Creates a new body and puts it in the queue.
     * <br>
     * Bodies created by this method contain no user data. User data is added when the body is retrieved from
     * the queue.
     *
     * @see #getExplosion(Vector2, int)
     */
    private void buildExplosion() {
        Body body = world.createBody(bodyDef);

        Fixture f = body.createFixture(shape, 0.0f);
        f.setSensor(true);

        explosionQueue.add(body);
    }

    @Override
    public void dispose() {
        shape.dispose();
    }
}
