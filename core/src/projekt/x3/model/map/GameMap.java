package x3.model.map;

import x3.model.effect.Effect;
import x3.model.game.GameEngine;
import x3.model.game.objects.Box;
import x3.model.game.objects.Monster;
import x3.model.game.objects.Player;
import x3.model.game.objects.Wall;
import x3.model.util.BattleRoyaleManager;
import x3.model.util.Pair;
import x3.model.util.exception.MapConstructionException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/*
IMPLEMENTÁCIÓS LÉPÉSEK:
    1) privát konstruktor
        1.a) falak megadása
        1.b) dobozok megadása
        1.c) játékos kiinduló pontok megadása
        1.d) szörny kiinduló pontok megadása
        1.e) check() hívás (metódusa a szülőnek)
        1.f) shuffle() hívás (metódusa a szülőnek)
    2) absztrakt metódusok implementálása
 */

/**
 * A GameMap object describes how the playable map should be constructed by the {@link GameEngine game engine}.
 * <br>
 * A map contains information about the
 * <ul>
 *     <li>Dimensions of the map,</li>
 *     <li>
 *         The position of {@link Wall walls}, {@link Box boxes},
 *         {@link Player players} and {@link Monster monsters},
 *     </li>
 *     <li>The number of {@link Effect buffs, debuffs} and monsters,</li>
 *     <li>The maximum number of {@link BattleRoyaleManager shrinks} the game is allowed to perform.</li>
 * </ul>
 * This object also has utility functions for filling areas with specific objects, clearing areas,
 * surrounding the play area with walls, shuffling spawn positions, and verifying the correctness
 * of the map description and properties.
 */
public abstract class GameMap {
    /**
     * List of {@link Wall} positions.
     */
    protected final List<Pair> walls = new ArrayList<>();
    /**
     * List of {@link Box} positions.
     */
    protected final List<Pair> boxes = new ArrayList<>();

    /**
     * List of {@link Player} spawn positions.
     */
    protected final List<Pair> playerSpawns = new ArrayList<>();
    /**
     * List of {@link Monster} spawn positions.
     */
    protected final List<Pair> monsterSpawns = new ArrayList<>();

    protected GameMap() {
    }

    public Stream<Pair> getWalls() {
        return walls.stream();
    }

    public Stream<Pair> getBoxes() {
        return boxes.stream();
    }

    /**
     * Returns a <code>playerCount</code> number of {@link Player} spawn positions.
     *
     * @param playerCount the number of spawn positions to return.
     * @return a stream of {@link Player} spawn positions.
     */
    public Stream<Pair> getPlayerSpawns(int playerCount) {
        return playerSpawns.stream().limit(playerCount);
    }

    /**
     * Returns as many {@link Monster} spawn positions as there are monsters on this map.
     *
     * @return a stream of {@link Monster} spawn positions.
     * @see #getMonsterCount()
     */
    public Stream<Pair> getMonsterSpawns() {
        return monsterSpawns.stream().limit(getMonsterCount());
    }

    /**
     * Adds all position to the target list that fit within the bounding
     * rectangle between <code>bottomLeft</code> and <code>topRight</code>
     *
     * @param target     the list where the area will be filled.
     * @param bottomLeft bottom left corner of the bounding rectangle.
     * @param topRight   bottom left corner of the bounding rectangle.
     */
    protected void areaFill(List<Pair> target, Pair bottomLeft, Pair topRight) {
        for (int i = bottomLeft.col; i <= topRight.col; ++i) {
            for (int j = bottomLeft.row; j <= topRight.row; ++j) {
                target.add(Pair.of(j, i));
            }
        }
    }

    /**
     * Removes all positions from the target list that fit within the bounding
     * rectangle between <code>bottomLeft</code> and <code>topRight</code>.
     *
     * @param target     the list where the area will be cleared.
     * @param bottomLeft bottom left corner of the bounding rectangle.
     * @param topRight   bottom left corner of the bounding rectangle.
     */
    protected void areaClear(List<Pair> target, Pair bottomLeft, Pair topRight) {
        for (int i = bottomLeft.col; i <= topRight.col; ++i) {
            for (int j = bottomLeft.row; j <= topRight.row; ++j) {
                target.remove(Pair.of(j, i));
            }
        }
    }

    /**
     * Surrounds the play area with walls.
     */
    protected void surroundWithWalls() {
        // south walls
        areaFill(walls, Pair.of(0, 0), Pair.of(0, getMapWidth() - 1));

        // north walls
        areaFill(walls, Pair.of(getMapHeight() - 1, 0), Pair.of(getMapHeight() - 1, getMapWidth() - 1));

        // west walls
        areaFill(walls, Pair.of(1, 0), Pair.of(getMapHeight() - 2, 0));

        // east walls
        areaFill(walls, Pair.of(1, getMapWidth() - 1), Pair.of(getMapHeight() - 2, getMapWidth() - 1));
    }

    /**
     * Shuffles the order of {@link Box} positions, {@link Player} spawn positions and {@link Monster} spawn positions.
     *
     * @see Collections#shuffle(List)
     */
    public void shuffle() {
        Collections.shuffle(boxes);
        Collections.shuffle(playerSpawns);
        Collections.shuffle(monsterSpawns);
    }

    /**
     * Performs all checks on the map.
     * <br>
     * Order of checks:
     * <ol>
     *     <li>{@link #checkMapSize}</li>
     *     <li>{@link #checkCounts}</li>
     *     <li>{@link #checkHangingObjects}</li>
     *     <li>{@link #checkOverlaps}</li>
     *     <li>{@link #checkBoxCount}</li>
     * </ol>
     */
    protected void check() {
        checkMapSize();
        checkCounts();
        checkHangingObjects();
        checkOverlaps();
        checkBoxCount();
    }

    /**
     * Verifies that the width and height of the map is at least 1 unit.
     *
     * @throws MapConstructionException when at least one dimension of the map is less than 1.
     * @see #getMapWidth()
     * @see #getMapHeight()
     */
    protected void checkMapSize() throws MapConstructionException {
        if (getMapWidth() < 1 || getMapHeight() < 1) {
            throw new MapConstructionException("Map area size cannot be less than 1.");
        }
    }

    /**
     * Verifies that the number of {@link Effect buffs, debuffs} and {@link Monster monsters} are non-negative.
     *
     * @throws MapConstructionException when at least one of these values is negative.
     * @see #getBuffCount()
     * @see #getDebuffCount()
     * @see #getMonsterCount()
     */
    protected void checkCounts() throws MapConstructionException {
        if (getBuffCount() < 0) {
            throw new MapConstructionException("Buff count cannot be negative.");
        }
        if (getDebuffCount() < 0) {
            throw new MapConstructionException("Debuff count cannot be negative.");
        }
        if (getMonsterCount() < 0) {
            throw new MapConstructionException("Monster count cannot be negative.");
        }
    }

    /**
     * Verifies that no two objects overlap.
     * <br>
     * Two objects overlap if their corresponding coordinates are identical. Since position values
     * are described as (column, row) integer pairs, no floating point error can occur.
     * <br>
     * This method checks all {@link Wall walls}, {@link Box boxes}, {@link Player players}
     * and {@link Monster monsters} for overlap.
     *
     * @throws MapConstructionException when there's at least one pair of positions overlapping.
     */
    protected void checkOverlaps() throws MapConstructionException {
        List<Pair> pairs = Stream.of(walls.stream(), boxes.stream(), playerSpawns.stream(), monsterSpawns.stream())
            .flatMap(pair -> pair)
            .toList();

        for (int i = 0; i < pairs.size(); ++i) {
            for (int j = i + 1; j < pairs.size(); ++j) {
                Pair p1 = pairs.get(i);
                Pair p2 = pairs.get(j);
                if (p1.equals(p2))
                    throw new MapConstructionException("Pair (" + p1.col + ", " + p1.row + ") occurs more than once, possible overlap.");
            }
        }
    }

    /**
     * Verifies that there are no objects hanging outside the bounds of the map.
     * <br>
     * An object is considered 'hanging' if its position values don't fit within the {@link #getMapWidth() width}
     * and {@link #getMapHeight() height} values of the map
     * (e.g. when a position value is {@code < 0} or {@code >} {@link #getMapWidth() width}).
     * <br>
     * This method goes over a stream of all {@link Wall wall}, {@link Box box}, {@link Player player}
     * and {@link Monster monster} positions and finds the first whose coordinates (at least one) is invalid.
     *
     * @throws MapConstructionException when an invalidly positioned object is found.
     */
    protected void checkHangingObjects() throws MapConstructionException {
        Optional<Pair> anyHanging = Stream.of(walls.stream(), boxes.stream(), playerSpawns.stream(), monsterSpawns.stream())
            .flatMap(pair -> pair)
            .parallel()
            .filter(pair ->
                pair.col < 0 || pair.col > getMapWidth() - 1
                    || pair.row < 0 || pair.row > getMapHeight() - 1)
            .findAny();
        if (anyHanging.isPresent()) {
            Pair p = anyHanging.get();
            throw new MapConstructionException("Pair with indeces (" + p.col + ", " + p.row + ") is overhanging.");
        }
    }

    /**
     * Verifies that the number of {@link Effect effects} (buffs and debuffs total) don't exceed
     * the number of {@link Box boxes} on the map.
     *
     * @throws MapConstructionException when there's fewer boxes than there are effects.
     */
    protected void checkBoxCount() throws MapConstructionException {
        int effectCount = getBuffCount() + getDebuffCount();
        if (boxes.size() < effectCount) {
            throw new MapConstructionException("Not enough boxes (=" + boxes.size() + ") to store all effects (=" + effectCount + ").");
        }
    }

    /**
     * Returns the number of {@link Effect buffs} that this map's boxes are supposed to contain.
     *
     * @return the number of {@link Effect buffs}.
     */
    public abstract int getBuffCount();

    /**
     * Returns the number of {@link Effect debuffs} that this map's boxes are supposed to contain.
     *
     * @return the number of {@link Effect debuffs}.
     */
    public abstract int getDebuffCount();

    /**
     * Returns the number of {@link Monster monsters} that this map is supposed to have.
     *
     * @return the number of {@link Monster monsters}.
     */
    public abstract int getMonsterCount();

    /**
     * Returns the number of shrinks that the game's {@link BattleRoyaleManager battle royale} functionality
     * is allowed to perform.
     * <br>
     * This method is provided so that shrinking can be limited, leaving an intact zone
     * in the middle of the map.
     *
     * @return the number of shrinks.
     * @see BattleRoyaleManager
     */
    public abstract int getMaxShrinks();

    /**
     * Returns the width of the map as the number of grid cells.
     *
     * @return the width.
     */
    public abstract int getMapWidth();

    /**
     * Returns the height of the map as the number of grid cells.
     *
     * @return the height.
     */
    public abstract int getMapHeight();
}
