package ch.fhnw.Pathfinder;

import ch.fhnw.Cell;
import ch.fhnw.Map;
import ch.fhnw.util.Distance;
import ch.fhnw.util.Moves;

import java.util.ArrayList;

public abstract class Pathfinder {

    Map map;
    private int heuristic;
    private boolean allowCrossingCorners;
    private boolean allowDiagonals;
    public boolean isRunning = false;
    ArrayList<Cell> openList;
    ArrayList<Cell> closedList;
    ArrayList<Cell> checkPoints;
    Cell lastCell;
    Cell currentCell;
    ArrayList<Cell> path;

    boolean noveltyActive;
    boolean happyActive;
    boolean explorationActive;
    boolean confidenceActive;

    public void init() {
        isRunning = true;
        openList = new ArrayList<>();
        closedList = new ArrayList<>();
        checkPoints = new ArrayList<>();
        checkPoints.addAll(map.getCheckPoints());
        path = new ArrayList<>();
        lastCell = currentCell;
    }


    public abstract void step();

    ArrayList<Cell> getNeighbours(Cell currentCell) {
        ArrayList<Cell> neighbours = new ArrayList<>();

        //Add Left/Up/Right/Down Moves
        for (int i = 0; i < 4; i++) {
            int neighbour_i = (int) currentCell.getIndex().i;
            int neighbour_j = (int) currentCell.getIndex().j;
            Cell neighbour = this.getNeighbourNode(neighbour_i + (int) Moves.LURDMoves.get(i).i, neighbour_j + (int) Moves.LURDMoves.get(i).j);
            if (neighbour != null) {
                neighbours.add(neighbour);
            }
        }
        if (allowDiagonals) {
            for (int i = 0; i < 4; i++) {
                int neighbour_i = (int) currentCell.getIndex().i;
                int neighbour_j = (int) currentCell.getIndex().j;
                Cell neighbour = this.getNeighbourNode(neighbour_i + (int) Moves.diagonalMoves.get(i).i, neighbour_j + (int) Moves.diagonalMoves.get(i).j);
                if (neighbour != null) {
                    neighbours.add(neighbour);

                }
            }
        }
        return neighbours;
    }

    double heuristic(Cell startCell, Cell endCell) {
        double distance;
        if (heuristic == 0) {
            distance = Distance.calculateEuclideanDistance(startCell, endCell);
        } else {
            distance = Distance.calculateManhattanDistance(startCell, endCell);
        }
        if (endCell.isWall()) distance = Integer.MAX_VALUE;
        return distance;
    }

    private Cell getNeighbourNode(int i, int j) {
        if (i < 0 || i >= map.getGrid().size() ||
                j < 0 || j >= map.getGrid().size()) {
            return null;
        }
        return map.getGrid().get(i).get(j);
    }

    boolean checkPointFound(Cell currentCell) {
        //      If we found a checkpoint
        if (checkPoints.get(0).equals(currentCell)) {
            checkPoints.remove(currentCell);
            System.out.println("[Checkpoint] : reached");
            return true;
        }
        return false;
    }

    boolean lastCheckPointFound() {
        if (checkPoints.isEmpty()) {
            System.out.println("[Checkpoints]: No more checkpoints");
            isRunning = false;
            return true;
        }
        return false;
    }


    public void setMap(Map map) {
        this.map = map;
    }

    public Map getMap() {
        return map;
    }

    public void setAllowDiagonals(boolean allowDiagonals) {
        this.allowDiagonals = allowDiagonals;
    }

    public void setHappyActive(boolean happyActive) {
        this.happyActive = happyActive;
    }

    public void setNoveltyActive(boolean novelyActive) {
        this.noveltyActive = novelyActive;
    }

    public void setExplorationActive(boolean explorationActive) {
        this.explorationActive = explorationActive;
    }

    public void setConfidenceActive(boolean confidenceActive) {
        this.confidenceActive = confidenceActive;
    }

    public void setAllowCrossingCorners(boolean allowCrossingCorners) {
        this.allowCrossingCorners = allowCrossingCorners;
    }

    public void setHeuristic(int heuristic) {
        this.heuristic = heuristic;
    }

    public abstract ArrayList<Cell> getLocalPath();

    public abstract double getTotalTime();

    public abstract int getBumps();

    public abstract int getSteps();

}
