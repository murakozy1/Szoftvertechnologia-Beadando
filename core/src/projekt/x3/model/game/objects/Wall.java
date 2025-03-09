package x3.model.game.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * This class is used exclusively for collision detection in the GameEngine.
 * <br>
 * Box2D bodies are assigned Wall objects (through their setUserData() method),
 * and the read during collision detection.
 */
public class Wall extends Sprite { }
