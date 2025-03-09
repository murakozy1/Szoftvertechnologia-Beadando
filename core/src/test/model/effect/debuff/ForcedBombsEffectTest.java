package model.effect.debuff;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import x3.model.effect.debuff.ForcedBombsEffect;
import x3.model.game.objects.Player;

import static model.TestingConstants.START_TIME_DELTA;

public class ForcedBombsEffectTest {
    static Player p;
    static ForcedBombsEffect effect;

    @BeforeEach
    public void beforeAll() {
        p = new Player();
        effect = new ForcedBombsEffect(null);
    }

    @Test
    public void testApply() {
        effect.apply(p);
        long startTime = System.currentTimeMillis();

        Assertions.assertAll(
                // () -> Assertions.assertTrue(p.hasEffect(ForcedBombsEffect.class)),
                () -> Assertions.assertTrue(Math.abs(effect.startTime - startTime) < START_TIME_DELTA)
        );
    }

    @Test
    public void testRemove() {
        effect.remove(p);

        Assertions.assertFalse(p.hasEffect(ForcedBombsEffect.class));
    }
}
