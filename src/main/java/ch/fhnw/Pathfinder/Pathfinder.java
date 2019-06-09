package ch.fhnw.Pathfinder;

import ch.fhnw.Cell;
import ch.fhnw.Map;
import ch.fhnw.util.Distance;
import ch.fhnw.util.Moves;
import ch.fhnw.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Pathfinder {

    Map map;
    private int heuristic;
    private boolean allowCrossingCorners;
    private boolean allowDiagonals;
    public boolean isRunning = false;
    ArrayList<Cell> openList;
    ArrayList<Cell> closedList;
    ArrayList<Cell> checkPoints;

    public void init(){
        isRunning =true;
        openList = new ArrayList<>();
        closedList = new ArrayList<>();
        checkPoints = new ArrayList<>();
        checkPoints.addAll(map.getCheckPoints());
    };

    public abstract void step();

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


    double heuristic(Cell startCell, Cell endCell) {
        double distance;
        if (heuristic == 0) {
            distance = Distance.calculateEuclideanDistance(startCell, endCell);
        } else {
            distance = Distance.calculateManhattanDistance(startCell, endCell);
        }
        return distance;
    }

    private Cell getNeighbourNode(int i, int j) {
        if (i < 0 || i >= map.getGrid().size() ||
                j < 0 || j >= map.getGrid().size()) {
            return null;
        }
        return map.getGrid().get(i).get(j);
    }

    public ArrayList<Cell> getShortestPath() {
        ArrayList<Cell> path = null;
        Cell target = map.getGrid().stream().flatMap(List::stream).collect(Collectors.toList()).stream().filter(cell -> cell.isCheckPoint() || cell.isRobotPosition()).findFirst().orElse(null);
        if (target != null) {
            if (target.getCameFrom() != null) {
                path = new ArrayList<>();
                path.add(target);
            }

            while (target.getCameFrom() != null) {
                target = map.getGrid().get(target.getCameFrom().i).get(target.getCameFrom().j);
                path.add(0, target);
            }
        }
        return path;
    }

    boolean checkPointFound(Cell currentCell) {
        //      If we found a checkpoint
        if (checkPoints.get(0).equals(currentCell)) {
            checkPoints.remove(currentCell);
            if (checkPoints.isEmpty()) {
                isRunning = false;
                return true;
            }
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

    public void setAllowCrossingCorners(boolean allowCrossingCorners) {
        this.allowCrossingCorners = allowCrossingCorners;
    }

    public void setHeuristic(int heuristic) {
        this.heuristic = heuristic;
    }

}
