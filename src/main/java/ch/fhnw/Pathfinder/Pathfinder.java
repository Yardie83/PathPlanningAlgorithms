package ch.fhnw.Pathfinder;

import ch.fhnw.Cell;
import ch.fhnw.Map;
import ch.fhnw.util.Moves;
import ch.fhnw.util.Pair;

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

    ArrayList<Cell> getNeighbours(Cell currentCell) {
        ArrayList<Cell> neighbours = new ArrayList<>();

        //Add Left/Up/Right/Down Moves
        for (int i = 0; i < 4; i++) {
            Cell neighbour = this.getNeighbourNode(currentCell.getIndex().i + Moves.LURDMoves.get(i).i, currentCell.getIndex().j + Moves.LURDMoves.get(i).j);
            if (neighbour != null) {
                neighbours.add(neighbour);
            }
        }
        if (allowDiagonals) {
            for (int i = 0; i < 4; i++) {
                Cell neighbour = this.getNeighbourNode(currentCell.getIndex().i + Moves.diagonalMoves.get(i).i, currentCell.getIndex().j + Moves.diagonalMoves.get(i).j);
                if (neighbour != null) {
                    neighbours.add(neighbour);

                }
            }
        }
        return neighbours;
    }

    private Cell getNeighbourNode(int i, int j) {
        if (i < 0 || i >= map.getGrid().size() ||
                j < 0 || j >= map.getGrid().size()) {
            return null;
        }
        return map.getGrid().get(i).get(j);
    }

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
