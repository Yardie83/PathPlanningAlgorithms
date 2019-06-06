package ch.fhnw.util;

import java.util.ArrayList;
import java.util.List;

public class Moves {

    public static final ArrayList<Pair> LURDMoves = new ArrayList<>(List.of(
            new Pair(-1, 0),
            new Pair(0, -1),
            new Pair(1, 0),
            new Pair(0, 1)));
    public static final ArrayList<Pair> diagonalMoves = new ArrayList<>(List.of(
            new Pair(-1, -1),
            new Pair(1, -1),
            new Pair(1, 1),
            new Pair(-1, 1)));
}
