package model.game.objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import x3.model.game.objects.Box;

import java.util.Optional;

public class BoxTest {
    @Test
    public void effectSetTest(){
        Box box = new Box(Optional.empty());
        Assertions.assertFalse(box.getEffect().isPresent());
    }
}
