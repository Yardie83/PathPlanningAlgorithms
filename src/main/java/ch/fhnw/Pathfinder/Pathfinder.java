package ch.fhnw.Pathfinder;

import ch.fhnw.Cell;
import ch.fhnw.Map;

import java.util.ArrayList;

public abstract class Pathfinder {

    boolean allowCrossingCorners;
    Map map;
    int start_i;
    int start_j;
    int target_i;
    int target_j;
    boolean allowDiagonals;
    int heuristic;
    public boolean isRunning = true;

    Pathfinder() {
    }

    public abstract void init();

    public abstract Cell step();

    public abstract ArrayList<Cell> getShortestPath();

    public void setMap(Map map) {
        this.map = map;
    }

    public void setStart(int start_i, int start_j) {
        this.start_i = start_i;
        this.start_j = start_j;
    }

    public void setTarget(int target_i, int target_j) {
        this.target_i = target_i;
        this.target_j = target_j;
    }

    public void setAllowDiagonals(boolean allowDiagonals) {
        this.allowDiagonals = allowDiagonals;
    }

    public void setAllowCrossingCorners(boolean allowCrossingCorners) {
        this.allowCrossingCorners = allowCrossingCorners;
    }

    public void setHeuristic(int heuristic) {
        this.heuristic = heuristic;
    }
}
