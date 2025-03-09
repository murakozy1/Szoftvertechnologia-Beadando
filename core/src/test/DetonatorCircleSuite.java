import model.effect.buff.BiggerBombEffectTest;
import model.effect.buff.BonusBombEffectTest;
import model.effect.debuff.ForcedBombsEffectTest;
import model.effect.debuff.NoBombsEffectTest;
import model.effect.debuff.SlowEffectTest;
import model.effect.debuff.SmallBombEffectTest;
import model.game.objects.*;
import model.game.suppliers.BombPoolTest;
import model.game.suppliers.ExplosionPoolTest;
import model.map.GameMapTest;
import model.util.BattleRoyaleManagerTest;
import model.util.KeybindManagerTest;
import model.util.PairTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BombTest.class,
        BoxTest.class,
        MonsterTest.class,
        PlayerTest.class,
        ExplosionTest.class,
        BiggerBombEffectTest.class,
        BonusBombEffectTest.class,
        ForcedBombsEffectTest.class,
        NoBombsEffectTest.class,
        SlowEffectTest.class,
        SmallBombEffectTest.class,
        ExplosionPoolTest.class,
        BombPoolTest.class,
        GameMapTest.class,
        BattleRoyaleManagerTest.class,
        KeybindManagerTest.class,
        PairTest.class
})
public class DetonatorCircleSuite {
}
