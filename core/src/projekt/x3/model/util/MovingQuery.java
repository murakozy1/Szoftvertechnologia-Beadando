package x3.model.util;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import x3.model.game.objects.Monster;
import x3.model.game.objects.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is a callback object for AABB querying.
 * <br>
 * It records all players and monsters (moving objects) in a certain bounding box and stores them in separate lists.
 */
public class MovingQuery implements QueryCallback {
    private final Set<Player> players = new HashSet<>();
    private final List<Monster> monsters = new ArrayList<>();

    @Override
    public boolean reportFixture(Fixture fixture) {
        Body body = fixture.getBody();
        if (body.getUserData() instanceof Player) {
            players.add((Player) body.getUserData());
        } else if (body.getUserData() instanceof Monster) {
            monsters.add((Monster) body.getUserData());
        }
        return true;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public List<Monster> getMonsters() {
        return monsters;
    }
}