package model.game.objects;

import org.junit.jupiter.api.BeforeEach;
import x3.model.game.objects.Bomb;
import x3.model.game.objects.Player;

public class BombTest {

    static Bomb b;
    static Player p;

    @BeforeEach
    public void beforeAll(){
        p = new Player();
        b = new Bomb(p, null, null);
    }
}
