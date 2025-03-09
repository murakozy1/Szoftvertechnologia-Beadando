package x3.model.util;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import x3.model.effect.Effect;
import x3.model.game.objects.Bomb;
import x3.model.game.objects.Box;
import x3.model.game.objects.Wall;

/**
 * This is a callback object for AABB querying.
 * <br>
 * It records all static objects in a certain bounding box.
 * These objects include: walls, boxes, bombs and effects.
 * Each object type's presence can be queried as a boolean value.
 */
public class StaticQuery implements QueryCallback {
    private boolean bombFound = false;
    private boolean wallFound = false;
    private boolean boxFound = false;
    private boolean effectFound = false;
    private Body result;

    @Override
    public boolean reportFixture(Fixture fixture) {
        Body body = fixture.getBody();
        if (body.getUserData() instanceof Wall) {
            wallFound = true;
            result = body;
            return false;
        } else if (body.getUserData() instanceof Box) {
            result = body;
            boxFound = true;
            return false;
        } else if (body.getUserData() instanceof Bomb) {
            result = body;
            bombFound = true;
            return false;
        } else if (body.getUserData() instanceof Effect) {
            result = body;
            effectFound = true;
            return false;
        }
        return true;
    }

    public boolean isBombFound() {
        return bombFound;
    }

    public boolean isWallFound() {
        return wallFound;
    }

    public boolean isBoxFound() {
        return boxFound;
    }

    public boolean isEffectFound() {
        return effectFound;
    }

    /**
     * Returns the body that was first encountered in the bounding box.
     * Order of object checking is:
     * <ol>
     *     <li>Walls</li>
     *     <li>Boxes</li>
     *     <li>Bombs</li>
     *     <li>Effects</li>
     * </ol>
     * @return the first body that was encountered.
     */
    public Body getResult() {
        return result;
    }
}