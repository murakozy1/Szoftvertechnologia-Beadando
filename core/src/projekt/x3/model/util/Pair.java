package x3.model.util;

import java.util.Objects;

/**
 * A <code>Pair</code> describes a column-row pair on the game field, for precise reference to cells.
 * <br>
 * <code>Pairs</code> are immutable.
 */
public final class Pair {
    /**
     * Column index.
     */
    public final int col;
    /**
     * Row index.
     */
    public final int row;

    private Pair(int col, int row) {
        this.col = col;
        this.row = row;
    }

    /**
     * Creates a new <code>Pair</code>.
     *
     * @param col column index.
     * @param row row index.
     * @return the newly created <code>Pair</code>.
     */
    public static Pair of(int col, int row) {
        return new Pair(col, row);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return col == pair.col && row == pair.row;
    }

    @Override
    public int hashCode() {
        return Objects.hash(col, row);
    }

    @Override
    public String toString() {
        return "Pair(" + col + ", " + row + ")";
    }
}
