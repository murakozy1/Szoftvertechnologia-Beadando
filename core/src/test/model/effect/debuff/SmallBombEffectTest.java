package model.effect.debuff;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import x3.model.effect.debuff.SmallBombEffect;
import x3.model.game.objects.Player;

import static model.TestingConstants.START_TIME_DELTA;

public class SmallBombEffectTest {
    static Player p;
    static SmallBombEffect effect;

    @BeforeAll
    public static void beforeAll() {
        p = new Player();
        effect = new SmallBombEffect(null);
    }

    @Test
    public void testApply() {
        effect.apply(p);
        long startTime = System.currentTimeMillis();

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, p.bombRadius),
                // () -> Assertions.assertTrue(p.hasEffect(SmallBombEffect.class)),
                () -> Assertions.assertTrue(Math.abs(effect.startTime - startTime) < START_TIME_DELTA)
        );
    }

    @Test
    public void testRemove() {
        effect.remove(p);

        Assertions.assertAll(
                () -> Assertions.assertEquals(p.defaultBombRadius, p.bombRadius),
                () -> Assertions.assertFalse(p.hasEffect(SmallBombEffect.class))
        );
    }
}
