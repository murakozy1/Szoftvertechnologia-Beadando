package model.game.suppliers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import x3.model.game.objects.Bomb;
import x3.model.game.objects.Player;
import x3.model.game.suppliers.BombPool;

import static org.junit.jupiter.api.Assertions.*;

public class BombPoolTest {
    private World world;
    private BombPool bombPool;
    private Player player;
    private Body owner;

    @BeforeEach
    public void beforeEach() {
        world = new World(Vector2.Zero, true);
        bombPool = new BombPool(world, 0, 0, null, null, null);
        player = new Player(0);
        owner = world.createBody(new BodyDef());
        owner.setUserData(player);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
        0, 0,
        -1, -1
        1, 1,
        2, 3,
        -3, -2
        """)
    public void testGetBomb(int x, int y) {
        Body b = bombPool.getBomb(new Vector2(x, y), owner);
        assertAll(
            () -> assertTrue(b.isActive()),
            () -> assertInstanceOf(Bomb.class, b.getUserData()),
            () -> assertEquals(new Vector2(x, y), b.getPosition())
        );
    }

    @Test
    public void testReturnBomb() {
        Body b = world.createBody(new BodyDef());
        bombPool.returnBomb(b);
        assertAll(
            () -> assertNull(b.getUserData()),
            () -> assertFalse(b.isActive())
        );
    }
}
