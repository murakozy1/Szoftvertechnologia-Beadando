package x3.model.game.objects;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.graphics.ParticleEmitterBox2D;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

/**
 * An <code>Explosion</code> manages its own particle effects and state so the engine only needs to access
 * these properties.
 */
public class Explosion {
    /**
     * The delay between explosions of different offsets.
     */
    public static final long DELAY = 100;

    private final long created;
    private final long delay;
    private final List<ParticleEffect> effects;

    /**
     * Only constructor.
     * <br>
     * Initializes the particle effects that will be rendered by the game.
     *
     * @param particleEffects a list of {@link FileHandle file handle} {@link SimpleEntry pairs}, that contain
     *                        the effect files and the locations where the particle images are of each particle effect.
     * @param offset          a delay multiplier.
     * @param world           the {@link World} where the particle's physics are simulated.
     * @param position        the origin of the effects.
     * @param PPM             Pixels Per Meter.
     */
    public Explosion(List<SimpleEntry<FileHandle, FileHandle>> particleEffects, int offset, World world, Vector2 position, float PPM) {
        created = System.currentTimeMillis();
        delay = offset * DELAY;
        effects = new ArrayList<>();

        particleEffects.forEach(file -> {
            ParticleEffect effect = new ParticleEffect();
            effect.load(file.getKey(), file.getValue());
            ParticleEmitterBox2D emitter = new ParticleEmitterBox2D(world, effect.getEmitters().first());
            effect.getEmitters().add(emitter);
            effect.getEmitters().removeIndex(0);
            effect.setPosition(position.x + PPM / 2, position.y + PPM / 2);
            effect.scaleEffect(0.13f);
            effect.start();
            effects.add(effect);
        });
    }

    /**
     * Returns whether the built-in delay has passed and the particle effects are ready to be shown.
     *
     * @return true if <code>delay</code> has elapsed.
     */
    public boolean ready() {
        return System.currentTimeMillis() >= created + delay;
    }

    /**
     * Returns the particle effects bound to this object.
     *
     * @return a list of particle effects.
     */
    public List<ParticleEffect> getParticleEffects() {
        return effects.stream().toList();
    }
}
