package x3.model.map;

import x3.model.util.Pair;

import java.util.List;

/**
 * A small fast-paced map with isolated spawn points for the players.
 */
public class Map3 extends GameMap {
    /**
     * Only constructor.
     */
    public Map3() {
        surroundWithWalls();

        areaFill(walls, Pair.of(2, 2), Pair.of(3, 3));
        areaFill(walls, Pair.of(7, 2), Pair.of(8, 3));
        areaFill(walls, Pair.of(2, 7), Pair.of(3, 8));
        areaFill(walls, Pair.of(7, 7), Pair.of(8, 8));

        walls.remove(Pair.of(2, 2));
        walls.remove(Pair.of(8, 2));
        walls.remove(Pair.of(2, 8));
        walls.remove(Pair.of(8, 8));

        boxes.addAll(List.of(
            Pair.of(3, 1), Pair.of(7, 1), Pair.of(1, 3), Pair.of(9, 3),
            Pair.of(1, 7), Pair.of(9, 7), Pair.of(3, 9), Pair.of(7, 9),
            Pair.of(5, 4), Pair.of(4, 5), Pair.of(5, 5), Pair.of(6, 5), Pair.of(5, 6)
        ));

        playerSpawns.addAll(List.of(
            Pair.of(1, 1), Pair.of(9, 1), Pair.of(1, 9), Pair.of(9, 9)
        ));

        monsterSpawns.addAll(List.of(
            Pair.of(5, 1), Pair.of(1, 5), Pair.of(9, 5), Pair.of(5, 9)
        ));

        shuffle();
        check();
    }


    @Override
    public int getBuffCount() {
        return 7;
    }

    @Override
    public int getDebuffCount() {
        return 3;
    }

    @Override
    public int getMonsterCount() {
        return 4;
    }

    @Override
    public int getMaxShrinks() {
        return 4;
    }

    @Override
    public int getMapWidth() {
        return 11;
    }

    @Override
    public int getMapHeight() {
        return 11;
    }
}
