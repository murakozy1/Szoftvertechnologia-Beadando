package x3.model.util;

import x3.model.effect.Effect;
import x3.model.game.GameEngine;
import x3.model.game.objects.Box;
import x3.model.game.objects.Monster;
import x3.model.game.objects.Player;
import x3.model.game.objects.Wall;

/**
 * This class describes class-level constants used by the {@link GameEngine game engine} for collision filtering.
 */
public class CollisionFilters {
    /**
     * Collision filter of the {@link Player} with index 1.
     */
    public static final short PLAYER_1      = 0b0000_0000_0001;
    /**
     * Collision filter of the {@link Player} with index 2.
     */
    public static final short PLAYER_2      = 0b0000_0000_0010;
    /**
     * Collision filter of the {@link Player} with index 3.
     */
    public static final short PLAYER_3      = 0b0000_0000_0100;
    /**
     * Collision filter for all {@link Player Players}.
     * <br>
     * Shorthand for {@link CollisionFilters#PLAYER_1 PLAYER_1} | {@link CollisionFilters#PLAYER_2 PLAYER_2}
     * | {@link CollisionFilters#PLAYER_3 PLAYER_3}.
     */
    public static final short ALL_PLAYERS   = 0b0000_0000_0111;
    /**
     * Collision filter for bombs that the {@link Player} with index 1 will collide with.
     */
    public static final short BOMB_TO_P1    = 0b0000_0000_1000;
    /**
     * Collision filter for bombs that the {@link Player} with index 2 will collide with.
     */
    public static final short BOMB_TO_P2    = 0b0000_0001_0000;
    /**
     * Collision filter for bombs that the {@link Player} with index 3 will collide with.
     */
    public static final short BOMB_TO_P3    = 0b0000_0010_0000;
    /**
     * Collision filter for bombs that all {@link Player Players} will collide with.
     */
    public static final short ALL_BOMBS     = 0b0000_0011_1000;
    /**
     * Collision filter for {@link Wall Walls}.
     */
    public static final short WALL          = 0b0000_0100_0000;
    /**
     * Collision filter for {@link Box Boxes}.
     */
    public static final short BOX           = 0b0000_1000_0000;
    /**
     * Collision filter for {@link Effect Effects}.
     */
    public static final short EFFECT        = 0b0001_0000_0000;
    /**
     * Collision filter for {@link Monster Monsters}.
     */
    public static final short MONSTER       = 0b0010_0000_0000;
    /**
     * Collision filter for boundaries.
     * @see BattleRoyaleManager
     */
    public static final short BOUNDARY      = 0b0100_0000_0000;

    private CollisionFilters() {
    }
}
