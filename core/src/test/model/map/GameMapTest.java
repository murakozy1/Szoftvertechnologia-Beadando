package model.map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import x3.model.map.GameMap;
import x3.model.util.Pair;
import x3.model.util.exception.MapConstructionException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class GameMapTest {
    @ParameterizedTest
    @CsvSource(textBlock = """
            0, 0, 0, -1, -1
            1, 0, 0, 0, 0,
            2, 0, 0, 1, 0,
            2, 0, 0, 0, 1,
            9, 0, 0, 2, 2,
            """)
    public void testAreaFillList(int expected, int rowMin, int colMin, int rowMax, int colMax) {
        List<Pair> output = new ArrayList<>();

        new GameMap() {
            {
                areaFill(output, Pair.of(colMin, rowMin), Pair.of(colMax, rowMax));
            }

            @Override
            public int getBuffCount() { return 0; }

            @Override
            public int getDebuffCount() { return 0; }

            @Override
            public int getMonsterCount() { return 0; }

            @Override
            public int getMaxShrinks() { return 0; }

            @Override
            public int getMapWidth() { return 0; }

            @Override
            public int getMapHeight() { return 0; }
        };

        assertEquals(expected, output.size());
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0, 0, 0, -1, -1
            1, 0, 0, 0, 0,
            2, 0, 0, 1, 0,
            2, 0, 0, 0, 1,
            9, 0, 0, 2, 2,
            """)
    public void testAreaFillListDouble(int expected, int rowMin, int colMin, int rowMax, int colMax) {
        List<Pair> output = new ArrayList<>();

        new GameMap() {
            {
                areaFill(output, Pair.of(colMin, rowMin), Pair.of(colMax, rowMax));
                areaFill(output, Pair.of(colMin, rowMin), Pair.of(colMax, rowMax));
            }

            @Override
            public int getBuffCount() { return 0; }

            @Override
            public int getDebuffCount() { return 0; }

            @Override
            public int getMonsterCount() { return 0; }

            @Override
            public int getMaxShrinks() { return 0; }

            @Override
            public int getMapWidth() { return 0; }

            @Override
            public int getMapHeight() { return 0; }
        };

        assertEquals(2*expected, output.size());
    }

    @Test
    public void testAreaClearEmpty() {
        List<Pair> output = new ArrayList<>();

        new GameMap() {
            {
                areaClear(output, Pair.of(0, 0), Pair.of(100, 100));
            }

            @Override
            public int getBuffCount() { return 0; }

            @Override
            public int getDebuffCount() { return 0; }

            @Override
            public int getMonsterCount() { return 0; }

            @Override
            public int getMaxShrinks() { return 0; }

            @Override
            public int getMapWidth() { return 0; }

            @Override
            public int getMapHeight() { return 0; }
        };

        assertEquals(0, output.size());
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0, -1, -1, -1, -1,
            1, 0, 0, 0, 0,
            9, 0, 0, 2, 2
            9, -100, -100, 2, 2
            100, -1, -1, 0, 99,
            5000, 0, 0, 49, 99
            5000, -100, -100, 49, 99
            10000, 0, 0, 99, 99
            """)
    public void testAreaClear(int expected, int rowMin, int colMin, int rowMax, int colMax) {
        List<Pair> output = new ArrayList<>();
        int max = 10_000;

        new GameMap() {
            {
                areaFill(output, Pair.of(0, 0), Pair.of(99, 99));
                areaClear(output, Pair.of(colMin, rowMin), Pair.of(colMax, rowMax));
            }

            @Override
            public int getBuffCount() { return 0; }

            @Override
            public int getDebuffCount() { return 0; }

            @Override
            public int getMonsterCount() { return 0; }

            @Override
            public int getMaxShrinks() { return 0; }

            @Override
            public int getMapWidth() { return 0; }

            @Override
            public int getMapHeight() { return 0; }
        };

        assertEquals(max-expected, output.size());
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0, 0, 0,
            0, -1, 0,
            0, 0, -1,
            0, -100, -100,
            1, 1, 1,
            1, 10, 10,
            1, 100, 100
            """)
    public void testCheckMapSize(int shouldPass, int width, int height) {
        Executable exec = () -> new GameMap() {
            {
                checkMapSize();
            }

            @Override
            public int getBuffCount() { return 0; }

            @Override
            public int getDebuffCount() { return 0; }

            @Override
            public int getMonsterCount() { return 0; }

            @Override
            public int getMaxShrinks() { return 0; }

            @Override
            public int getMapWidth() { return width; }

            @Override
            public int getMapHeight() { return height; }
        };

        if (shouldPass == 1) {
            assertDoesNotThrow(exec);
        } else if (shouldPass == 0) {
            assertThrows(MapConstructionException.class, exec);
        } else {
            fail("Invalid parameter " + shouldPass + " for test config (should be 0 or 1).");
        }
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0, 0, 0,
            1, 1, 1,
            100, 100, 100
            """)
    public void testCheckCountsCorrect(int buffCount, int debuffCount, int monsterCount) {
        assertDoesNotThrow(() -> new GameMap() {
            {
                checkCounts();
            }

            @Override
            public int getBuffCount() { return buffCount; }

            @Override
            public int getDebuffCount() { return debuffCount; }

            @Override
            public int getMonsterCount() { return monsterCount; }

            @Override
            public int getMaxShrinks() { return 0; }

            @Override
            public int getMapWidth() { return 0; }

            @Override
            public int getMapHeight() { return 0; }
        });
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            -1, 0, 0,
            0, -1, 0,
            0, 0, -1
            """)
    public void testCheckCountsIncorrect(int buffCount, int debuffCount, int monsterCount) {
        assertThrows(MapConstructionException.class, () -> new GameMap() {
            {
                checkCounts();
            }

            @Override
            public int getBuffCount() { return buffCount; }

            @Override
            public int getDebuffCount() { return debuffCount; }

            @Override
            public int getMonsterCount() { return monsterCount; }

            @Override
            public int getMaxShrinks() { return 0; }

            @Override
            public int getMapWidth() { return 0; }

            @Override
            public int getMapHeight() { return 0; }
        });
    }

    @Test
    public void testCheckOverlapsEmpty() {
        assertDoesNotThrow(() -> new GameMap() {
            {
                checkOverlaps();
            }
            @Override
            public int getBuffCount() { return 0; }

            @Override
            public int getDebuffCount() { return 0; }

            @Override
            public int getMonsterCount() { return 0; }

            @Override
            public int getMaxShrinks() { return 0; }

            @Override
            public int getMapWidth() { return 0; }

            @Override
            public int getMapHeight() { return 0; }
        });
    }

    @Test
    public void testCheckOverlapsCorrect() {
        assertDoesNotThrow(() -> new GameMap() {
            {
                walls.addAll(List.of(Pair.of(0, 0), Pair.of(1, 1)));
                checkOverlaps();
            }

            @Override
            public int getBuffCount() { return 0; }

            @Override
            public int getDebuffCount() { return 0; }

            @Override
            public int getMonsterCount() { return 0; }

            @Override
            public int getMaxShrinks() { return 0; }

            @Override
            public int getMapWidth() { return 0; }

            @Override
            public int getMapHeight() { return 0; }
        });
    }

    @Test
    public void testCheckOverlapsIncorrect() {
        assertThrows(MapConstructionException.class, () -> new GameMap() {
            {
                walls.addAll(List.of(Pair.of(0, 0), Pair.of(0, 0)));
                checkOverlaps();
            }

            @Override
            public int getBuffCount() { return 0; }

            @Override
            public int getDebuffCount() { return 0; }

            @Override
            public int getMonsterCount() { return 0; }

            @Override
            public int getMaxShrinks() { return 0; }

            @Override
            public int getMapWidth() { return 0; }

            @Override
            public int getMapHeight() { return 0; }
        });
    }

    @Test
    public void testCheckHangingObjectsEmpty() {
        assertDoesNotThrow(() -> new GameMap() {
            {
                checkHangingObjects();
            }
            @Override
            public int getBuffCount() { return 0; }

            @Override
            public int getDebuffCount() { return 0; }

            @Override
            public int getMonsterCount() { return 0; }

            @Override
            public int getMaxShrinks() { return 0; }

            @Override
            public int getMapWidth() { return 0; }

            @Override
            public int getMapHeight() { return 0; }
        });
    }

    @Test
    public void testCheckHangingObjectsCorrect() {
        assertDoesNotThrow(() -> new GameMap() {
            {
                walls.addAll(List.of(
                        Pair.of(0, 0),
                        Pair.of(1, 1),
                        Pair.of(9, 9)
                ));
                checkHangingObjects();
            }
            @Override
            public int getBuffCount() { return 0; }

            @Override
            public int getDebuffCount() { return 0; }

            @Override
            public int getMonsterCount() { return 0; }

            @Override
            public int getMaxShrinks() { return 0; }

            @Override
            public int getMapWidth() { return 10; }

            @Override
            public int getMapHeight() { return 10; }
        });
    }

    @Test
    public void testCheckHangingObjectsIncorrect() {
        assertThrows(MapConstructionException.class, () -> new GameMap() {
            {
                walls.addAll(List.of(
                        Pair.of(0, 0),
                        Pair.of(-1, -1),
                        Pair.of(10, 10)
                ));
                checkHangingObjects();
            }
            @Override
            public int getBuffCount() { return 0; }

            @Override
            public int getDebuffCount() { return 0; }

            @Override
            public int getMonsterCount() { return 0; }

            @Override
            public int getMaxShrinks() { return 0; }

            @Override
            public int getMapWidth() { return 10; }

            @Override
            public int getMapHeight() { return 10; }
        });
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0, 0, 0,
            1, 0, 0,
            100, 0, 0,
            1, 1, 0,
            1, 0, 1
            """)
    public void testCheckBoxCountCorrect(int boxCount, int buffCount, int debuffCount) {
        assertDoesNotThrow(() -> new GameMap() {
            {
                IntStream.range(0, boxCount)
                        .mapToObj(ind -> Pair.of(ind, 0))
                        .forEach(boxes::add);
                checkBoxCount();
            }

            @Override
            public int getBuffCount() { return buffCount; }

            @Override
            public int getDebuffCount() { return debuffCount; }

            @Override
            public int getMonsterCount() { return 0; }

            @Override
            public int getMaxShrinks() { return 0; }

            @Override
            public int getMapWidth() { return 0; }

            @Override
            public int getMapHeight() { return 0; }
        });
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            1, 1, 1,
            1, 1, 2,
            1, 100, 100
            """)
    public void testCheckBoxCountIncorrect(int boxCount, int buffCount, int debuffCount) {
        assertThrows(MapConstructionException.class, () -> new GameMap() {
            {
                IntStream.range(0, boxCount)
                        .mapToObj(ind -> Pair.of(ind, 0))
                        .forEach(boxes::add);
                checkBoxCount();
            }

            @Override
            public int getBuffCount() { return buffCount; }

            @Override
            public int getDebuffCount() { return debuffCount; }

            @Override
            public int getMonsterCount() { return 0; }

            @Override
            public int getMaxShrinks() { return 0; }

            @Override
            public int getMapWidth() { return 0; }

            @Override
            public int getMapHeight() { return 0; }
        });
    }
}
