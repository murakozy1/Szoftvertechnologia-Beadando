package model.game.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import x3.model.game.objects.Explosion;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static x3.model.game.objects.Explosion.DELAY;

public class ExplosionTest {
    private final long forgiveness = 100;
    private final World world = new World(Vector2.Zero, true);

    @Execution(ExecutionMode.CONCURRENT)
    @ParameterizedTest
    @CsvSource(textBlock = """
        -1,
        0,
        1,
        2
        """)
    public void testReady(int offset) {
        long duration = Math.max(0, offset * DELAY);
        assertTimeoutPreemptively(Duration.ofMillis(duration + forgiveness), () -> {
            Explosion e = new Explosion(List.of(), offset, world, Vector2.Zero, 0);
            Thread.sleep(duration);
            assertTrue(e.ready());
        });
    }

    @Test
    public void testGetParticleEffectsThrows() {
        Explosion e = new Explosion(List.of(), 0, world, Vector2.Zero, 0);
        assertThrows(UnsupportedOperationException.class, () -> e.getParticleEffects().clear());
    }
}
