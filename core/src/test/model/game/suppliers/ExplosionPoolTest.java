package model.game.suppliers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import x3.model.game.objects.Explosion;
import x3.model.game.suppliers.ExplosionPool;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExplosionPoolTest {
    private World world;
    private ExplosionPool explosionPool;

    @BeforeEach
    public void beforeEach() {
        world = new World(Vector2.Zero, true);
        explosionPool = new ExplosionPool(world, 0, List.of());
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
        0, 0,
        -1, -1
        1, 1,
        2, 3,
        -3, -2
        """)
    public void testGetExplosion(int x, int y) {
        Body b = explosionPool.getExplosion(new Vector2(x, y), 0);
        assertAll(
            () -> assertTrue(b.isActive()),
            () -> assertInstanceOf(Explosion.class, b.getUserData()),
            () -> assertEquals(new Vector2(x, y), b.getPosition())
        );
    }

    @Test
    public void testReturnExplosion() {
        Body b = world.createBody(new BodyDef());
        explosionPool.returnExplosion(b);
        assertAll(
            () -> assertNull(b.getUserData()),
            () -> assertFalse(b.isActive())
        );
    }
}
