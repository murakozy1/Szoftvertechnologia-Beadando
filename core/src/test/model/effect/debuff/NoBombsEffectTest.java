package model.effect.debuff;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import x3.model.effect.debuff.NoBombsEffect;
import x3.model.game.objects.Player;

import static model.TestingConstants.START_TIME_DELTA;

public class NoBombsEffectTest {
    static Player p;
    static NoBombsEffect effect;

    @BeforeAll
    public static void beforeAll() {
        p = new Player();
        effect = new NoBombsEffect(null);
    }

    @Test
    public void testApply() {
        effect.apply(p);
        long startTime = System.currentTimeMillis();

        Assertions.assertAll(
                () -> Assertions.assertEquals(0, p.bombsMax),
                // () -> Assertions.assertTrue(p.hasEffect(NoBombsEffect.class)),
                () -> Assertions.assertTrue(Math.abs(effect.startTime - startTime) < START_TIME_DELTA)
        );
    }

    @Test
    public void testRemove() {
        effect.remove(p);

        Assertions.assertEquals(p.defaultBombsMax, p.bombsMax);
    }
}
