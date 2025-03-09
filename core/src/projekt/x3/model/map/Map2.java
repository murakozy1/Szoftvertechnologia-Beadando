package x3.model.map;

import x3.model.util.Pair;

import java.util.List;

/**
 * A large map with a complex maze-like layout and several monsters.
 */
public class Map2 extends GameMap {
    /**
     * Only constructor.
     */
    public Map2() {
        surroundWithWalls();
        // kulso player
        areaFill(walls, Pair.of(2, 2), Pair.of(18, 18));
        areaClear(walls, Pair.of(3, 3), Pair.of(17, 17));
        // masodik layer
        areaFill(walls, Pair.of(4, 4), Pair.of(16, 16));
        areaClear(walls, Pair.of(5, 5), Pair.of(15, 15));
        // belso layer
        areaFill(walls, Pair.of(6, 6), Pair.of(14, 14));
        areaClear(walls, Pair.of(7, 7), Pair.of(13, 13));
        // kozepso 3x3
        areaFill(walls, Pair.of(9, 9), Pair.of(11, 11));

        walls.addAll(List.of(
            Pair.of(10, 8), Pair.of(8, 10), Pair.of(12, 10), Pair.of(10, 12),
            Pair.of(10, 1), Pair.of(1, 10), Pair.of(19, 10), Pair.of(10, 19)
        ));

        walls.removeAll(List.of(
            // kulso layer
            Pair.of(4, 2), Pair.of(7, 2), Pair.of(13, 2), Pair.of(16, 2),
            Pair.of(2, 4), Pair.of(18, 4),
            Pair.of(2, 7), Pair.of(18, 7),
            Pair.of(2, 13), Pair.of(18, 13),
            Pair.of(2, 16), Pair.of(18, 16),
            Pair.of(4, 18), Pair.of(7, 18), Pair.of(13, 18), Pair.of(16, 18),
            // masodik layer
            Pair.of(6, 4), Pair.of(14, 4), Pair.of(4, 6), Pair.of(16, 6),
            Pair.of(4, 14), Pair.of(16, 14), Pair.of(6, 16), Pair.of(14, 16),
            // belso layer
            Pair.of(8, 6), Pair.of(12, 6), Pair.of(6, 8), Pair.of(14, 8),
            Pair.of(6, 12), Pair.of(14, 12), Pair.of(8, 14), Pair.of(12, 14)
        ));

        boxes.addAll(List.of(
            // kulso layer
            Pair.of(4, 2), Pair.of(7, 2), Pair.of(13, 2), Pair.of(16, 2),
            Pair.of(2, 4), Pair.of(18, 4),
            Pair.of(2, 7), Pair.of(18, 7),
            Pair.of(2, 13), Pair.of(18, 13),
            Pair.of(2, 16), Pair.of(18, 16),
            Pair.of(4, 18), Pair.of(7, 18), Pair.of(13, 18), Pair.of(16, 18),
            // belso layer
            Pair.of(8, 6), Pair.of(12, 6), Pair.of(6, 8), Pair.of(14, 8),
            Pair.of(6, 12), Pair.of(14, 12), Pair.of(8, 14), Pair.of(12, 14)
        ));

        playerSpawns.addAll(List.of(
            Pair.of(1, 1), Pair.of(19, 19), Pair.of(19, 1), Pair.of(1, 19)
        ));

        monsterSpawns.addAll(List.of(
            Pair.of(10, 3), Pair.of(5, 5), Pair.of(15, 5), Pair.of(3, 10),
            Pair.of(17, 10), Pair.of(5, 15), Pair.of(15, 15), Pair.of(10, 17)
        ));

        shuffle();
        check();
    }

    @Override
    public int getBuffCount() {
        return 15;
    }

    @Override
    public int getDebuffCount() {
        return 5;
    }

    @Override
    public int getMonsterCount() {
        return 8;
    }

    @Override
    public int getMaxShrinks() {
        return 6;
    }

    @Override
    public int getMapWidth() {
        return 21;
    }

    @Override
    public int getMapHeight() {
        return 21;
    }
}
