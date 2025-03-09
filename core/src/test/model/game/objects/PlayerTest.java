package model.game.objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import x3.model.effect.debuff.ForcedBombsEffect;
import x3.model.effect.debuff.NoBombsEffect;
import x3.model.effect.debuff.SlowEffect;
import x3.model.effect.debuff.SmallBombEffect;
import x3.model.game.objects.Player;
import x3.model.util.exception.NoBombsDownException;

import java.util.Optional;

public class PlayerTest {
    Player p;

    @BeforeEach
    public void beforeEach() {
        p = new Player();
    }

    @Test
    public void testCanPlace() {
        Assertions.assertAll(
                () -> Assertions.assertTrue(p.canPlace()),
                () -> {
                    p.bombsPlaced++;
                    Assertions.assertFalse(p.canPlace());
                }
        );
    }

    @Test
    public void testBombPlace() {
        p.bombPlace();
        Assertions.assertAll(
                () -> Assertions.assertFalse(p.canPlace()),
                () -> Assertions.assertEquals(1, p.bombsPlaced)
        );
    }

    @Test
    public void testBombBlewError() {
        Assertions.assertThrows(NoBombsDownException.class, () -> p.bombBlew());
    }

    @Test
    public void testBombBlew() {
        p.bombsPlaced++;
        p.bombBlew();

        Assertions.assertAll(
                () -> Assertions.assertTrue(p.canPlace()),
                () -> Assertions.assertEquals(0, p.bombsPlaced)
        );
    }

    @Test
    public void testHasEffect() {
        p.effectMap.replace(SlowEffect.class, Optional.of(new SlowEffect(null)));

        Assertions.assertAll(
                () -> Assertions.assertTrue(p.hasEffect(SlowEffect.class)),
                () -> Assertions.assertFalse(p.hasEffect(NoBombsEffect.class)),
                () -> Assertions.assertFalse(p.hasEffect(ForcedBombsEffect.class)),
                () -> Assertions.assertFalse(p.hasEffect(SmallBombEffect.class))
        );
    }

    @Test
    public void testApplySlow() {
        p.apply(new SlowEffect(null));

        Assertions.assertTrue(p.hasEffect(SlowEffect.class));
    }

    @Test
    public void testApplyNoBombs() {
        p.apply(new NoBombsEffect(null));

        Assertions.assertTrue(p.hasEffect(NoBombsEffect.class));
    }

    @Test
    public void testApplyForcedBombs() {
        p.apply(new ForcedBombsEffect(null));

        Assertions.assertTrue(p.hasEffect(ForcedBombsEffect.class));
    }

    @Test
    public void testApplySmallBomb() {
        p.apply(new SmallBombEffect(null));

        Assertions.assertTrue(p.hasEffect(SmallBombEffect.class));
    }
}
