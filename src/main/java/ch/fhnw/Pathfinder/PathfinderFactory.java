package ch.fhnw.Pathfinder;


import ch.fhnw.util.Algorithm;

public class PathfinderFactory {

    public static Pathfinder getPathfinder(String algorithm) {
        if (Algorithm.DIJKSTRA.equalsName(algorithm)) return new Dijkstra();
        if (Algorithm.A_STAR.equalsName(algorithm)) return new AStar();
        if (Algorithm.D_STAR_LITE.equalsName(algorithm)) return new DStarLites();
        if (Algorithm.MPAA.equalsName(algorithm)) return new MPAA();
        return null;
    }
}
