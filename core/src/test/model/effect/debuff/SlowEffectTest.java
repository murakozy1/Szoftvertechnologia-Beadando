package model.effect.debuff;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import x3.model.effect.debuff.SlowEffect;
import x3.model.game.objects.Player;

import static model.TestingConstants.START_TIME_DELTA;

public class SlowEffectTest {
    static Player p;
    static SlowEffect effect;

    @BeforeAll
    public static void beforeAll() {
        p = new Player();
        effect = new SlowEffect(null);
    }

    @Test
    public void testApply() {
        effect.apply(p);
        long startTime = System.currentTimeMillis();

        Assertions.assertAll(
                () -> Assertions.assertEquals(effect.newMoveSpeed, p.moveSpeed),
                // () -> Assertions.assertTrue(p.hasEffect(SlowEffect.class)),
                () -> Assertions.assertTrue(Math.abs(effect.startTime - startTime) < START_TIME_DELTA)
        );
    }

    @Test
    public void testRemove() {
        effect.remove(p);

        Assertions.assertAll(
                () -> Assertions.assertEquals(p.defaultMoveSpeed, p.moveSpeed),
                () -> Assertions.assertFalse(p.hasEffect(SlowEffect.class))
        );
    }
}
