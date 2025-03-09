package model.effect.buff;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import x3.model.effect.buff.BonusBombEffect;
import x3.model.game.objects.Player;

public class BonusBombEffectTest {
    @Test
    public void testApply() {
        Player p = new Player();
        BonusBombEffect effect = new BonusBombEffect(null);
        effect.apply(p);

        Assertions.assertAll(
                () -> Assertions.assertEquals(2, p.defaultBombsMax),
                () -> Assertions.assertEquals(2, p.bombsMax)
        );
    }
}
