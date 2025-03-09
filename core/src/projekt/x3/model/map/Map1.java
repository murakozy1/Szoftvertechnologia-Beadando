package x3.model.map;

import x3.model.util.Pair;

import java.util.List;

/**
 * A medium map with empty horizontal rows.
 */
public class Map1 extends GameMap {
    /**
     * Only constructor.
     */
    public Map1() {
        surroundWithWalls();

        walls.addAll(List.of(
            Pair.of(2, 2), Pair.of(4, 2), Pair.of(6, 2), Pair.of(8, 2), Pair.of(10, 2), Pair.of(12, 2),
            Pair.of(2, 4), Pair.of(4, 4), Pair.of(6, 4), Pair.of(8, 4), Pair.of(10, 4), Pair.of(12, 4),
            Pair.of(2, 6), Pair.of(4, 6), Pair.of(6, 6), Pair.of(8, 6), Pair.of(10, 6), Pair.of(12, 6),
            Pair.of(2, 8), Pair.of(4, 8), Pair.of(6, 8), Pair.of(8, 8), Pair.of(10, 8), Pair.of(12, 8),
            Pair.of(2, 10), Pair.of(4, 10), Pair.of(6, 10), Pair.of(8, 10), Pair.of(10, 10), Pair.of(12, 10),
            Pair.of(2, 12), Pair.of(4, 12), Pair.of(6, 12), Pair.of(8, 12), Pair.of(10, 12), Pair.of(12, 12)
        ));

        boxes.addAll(List.of(
            Pair.of(3, 2), Pair.of(5, 2), Pair.of(7, 2), Pair.of(9, 2), Pair.of(11, 2),
            Pair.of(3, 4), Pair.of(5, 4), Pair.of(7, 4), Pair.of(9, 4), Pair.of(11, 4),
            Pair.of(3, 6), Pair.of(5, 6), Pair.of(7, 6), Pair.of(9, 6), Pair.of(11, 6),
            Pair.of(3, 8), Pair.of(5, 8), Pair.of(7, 8), Pair.of(9, 8), Pair.of(11, 8),
            Pair.of(3, 10), Pair.of(5, 10), Pair.of(7, 10), Pair.of(9, 10), Pair.of(11, 10),
            Pair.of(3, 12), Pair.of(5, 12), Pair.of(7, 12), Pair.of(9, 12), Pair.of(11, 12)
        ));

        playerSpawns.addAll(List.of(
            Pair.of(1, 1), Pair.of(13, 13), Pair.of(13, 1), Pair.of(1, 13)
        ));

        monsterSpawns.addAll(List.of(
            Pair.of(7, 3), Pair.of(7, 5), Pair.of(7, 7), Pair.of(7, 9), Pair.of(7, 11)
        ));

        shuffle();
        check();
    }


    @Override
    public int getBuffCount() {
        return 14;
    }

    @Override
    public int getDebuffCount() {
        return 6;
    }

    @Override
    public int getMonsterCount() {
        return 5;
    }

    @Override
    public int getMaxShrinks() {
        return 4;
    }

    @Override
    public int getMapWidth() {
        return 15;
    }

    @Override
    public int getMapHeight() {
        return 15;
    }
}
