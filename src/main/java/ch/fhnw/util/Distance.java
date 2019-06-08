package ch.fhnw.util;

import ch.fhnw.Cell;

public class Distance {

    public static double calculateEuclideanDistance(Cell a, Cell b) {
        double ac = Math.abs(a.getIndex().i - b.getIndex().i);
        double cb = Math.abs(a.getIndex().j - b.getIndex().j);

        return Math.hypot(ac, cb);
    }

    public static int calculateManhattanDistance(Cell a, Cell b) {
        return Math.abs(a.getIndex().i - b.getIndex().i) + Math.abs(a.getIndex().j - b.getIndex().j);
    }
}
