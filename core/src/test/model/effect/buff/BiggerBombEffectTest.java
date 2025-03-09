package model.effect.buff;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import x3.model.effect.buff.BiggerBombEffect;
import x3.model.game.objects.Player;

public class BiggerBombEffectTest {
    @Test
    public void testApply() {
        Player p = new Player();
        BiggerBombEffect effect = new BiggerBombEffect(null);
        effect.apply(p);

        Assertions.assertAll(
                () -> Assertions.assertEquals(3, p.bombRadius),
                () -> Assertions.assertEquals(3, p.defaultBombRadius)
        );
    }
}
