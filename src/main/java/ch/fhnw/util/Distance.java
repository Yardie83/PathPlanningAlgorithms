package ch.fhnw.util;

import ch.fhnw.Cell;

public class Distance {

    public static double calculateEuclideanDistance(Cell a, Cell b) {
        double ac = Math.abs((int)a.getIndex().i - (int)b.getIndex().i);
        double cb = Math.abs((int)a.getIndex().j - (int)b.getIndex().j);

        return Math.hypot(ac, cb);
    }

    public static int calculateManhattanDistance(Cell a, Cell b) {
        return Math.abs((int)a.getIndex().i - (int)b.getIndex().i) + Math.abs((int)a.getIndex().j - (int)b.getIndex().j);
    }
}
