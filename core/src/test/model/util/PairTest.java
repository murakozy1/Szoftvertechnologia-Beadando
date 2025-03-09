package model.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import x3.model.util.Pair;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PairTest {
    @ParameterizedTest
    @CsvSource(textBlock = """
            0, 0, 0, 0,
            1, 1, 1, 1,
            -100, -100, -100, -100
            0, 1, 0, 1
            10, 0, 10, 10
            1, 1, 2, 2
            2, 2, 1, 1
            0, 0, -1, -1
            """)
    public void testEquals(int p1a, int p1b, int p2a, int p2b) {
        assertAll(
                () -> assertEquals(p1a == p2a && p1b == p2b, Pair.of(p1a, p1b).equals(Pair.of(p2a, p2b))),
                () -> assertEquals(p1a == p2a && p1b == p2b, Pair.of(p2a, p2b).equals(Pair.of(p1a, p1b)))
        );
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0, 0, 0, 0,
            1, 1, 1, 1,
            -100, -100, -100, -100
            """)
    public void testHashCode(int p1a, int p1b, int p2a, int p2b) {
        assertEquals(Pair.of(p1a, p1b).hashCode(), Pair.of(p2a, p2b).hashCode());
    }
}
