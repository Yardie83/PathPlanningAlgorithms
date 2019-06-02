package ch.fhnw;

import ch.fhnw.util.Pair;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class Dijkstra {

    private SimulationMap map;
    private int start_i;
    private int start_j;
    private boolean allowDiagonals;
    private int heuristic;
    private final ArrayList<Pair> LURDMoves;
    private final ArrayList<Pair> diagonalMoves;
    private ArrayList<Cell> openList;
    public boolean isRunning;

    Dijkstra(SimulationMap map, int start_i, int start_j, boolean allowDiagonals, int heuristic) {
        this.map = map;
        this.start_i = start_i;
        this.start_j = start_j;
        this.allowDiagonals = allowDiagonals;
        this.heuristic = heuristic;

        LURDMoves = new ArrayList<>();
        LURDMoves.add(new Pair(-1, 0));
        LURDMoves.add(new Pair(0, -1));
        LURDMoves.add(new Pair(1, 0));
        LURDMoves.add(new Pair(0, 1));

        diagonalMoves = new ArrayList<>();

        diagonalMoves.add(new Pair(-1, -1));
        diagonalMoves.add(new Pair(1, -1));
        diagonalMoves.add(new Pair(1, 1));
        diagonalMoves.add(new Pair(-1, 1));

        isRunning = true;

        init();
    }

    private void init() {

        openList = new ArrayList<>();

        for (int i = 0; i < map.getGrid().size(); i++) {
            for (int j = 0; j < map.getGrid().get(i).size(); j++) {
                Cell cell = map.getGrid().get(i).get(j);
                cell.setDistance(Integer.MAX_VALUE);
                ArrayList<Cell> neighbours = populateNeighbours(cell);
                cell.setNeighbours(neighbours);
                cell.setCameFrom(null);
                if (cell.getCell_Index().i == start_i && cell.getCell_Index().j == start_j) {
                    cell.setDistance(0);
                }
                openList.add(cell);
            }
        }
    }

    ArrayList<Cell> getShortestPath()   {

        ArrayList<Cell> path = null;

        Cell target = map.getGrid().get(map.getGrid().size() - 1).get(map.getGrid().size() - 1);

        Cell currentCell = target;
        if (currentCell.getCameFrom() != null) {
            path = new ArrayList<>();
            path.add(target);
        }

        while (currentCell.getCameFrom() != null) {
            currentCell = currentCell.getCameFrom();
            path.add(0, currentCell);
        }
        return path;
    }

    void step() {
        if (!openList.isEmpty()) {
            int smallestDistanceIndex = getNextSmallestDistanceIndex();
            Cell cell = openList.get(smallestDistanceIndex);
            openList.remove(smallestDistanceIndex);

            cell.getNeighbours().forEach(neighbour -> {
                if (openList.contains(neighbour)) {
                    update_Distance(cell, neighbour);
                }
            });
        } else {
            isRunning = false;
        }
    }

    private int getNextSmallestDistanceIndex() {

        int minIndex;

        if (openList.isEmpty()) {
            minIndex = -1;
        } else {
            final ListIterator<Cell> itr = openList.listIterator();
            Cell current = itr.next(); // first element as the current minimum
            minIndex = itr.previousIndex();
            while (itr.hasNext()) {
                final Cell next = itr.next();
                if (next.getDistance() < current.getDistance()) {
                    current = next;
                    minIndex = itr.previousIndex();
                }
            }
        }
        return minIndex;
    }

    private void update_Distance(Cell currentCell, Cell neighbour) {

        double distance;
        if (heuristic == 0) {
            // Euclidean distance
            distance = calculateEuclideanDistance(currentCell, neighbour);
        } else {
            //Manhattan distance
            distance = calculateManhattanDistance(currentCell, neighbour);
        }

        double alternative = currentCell.getDistance() + distance;
        if (alternative < neighbour.getDistance()) {
            neighbour.setDistance(alternative);
            neighbour.setCameFrom(currentCell);
            map.getGrid().get(neighbour.getCell_Index().i).get(neighbour.getCell_Index().j).setCameFrom(currentCell);
        }
    }

    private double calculateEuclideanDistance(Cell a, Cell b) {
        double ac = Math.abs(a.getCell_Index().i - b.getCell_Index().i);
        double cb = Math.abs(b.getCell_Index().i - b.getCell_Index().j);

        return Math.hypot(ac, cb);
    }

    private int calculateManhattanDistance(Cell a, Cell b) {
        return Math.abs(a.getCell_Index().i - b.getCell_Index().i) + Math.abs(a.getCell_Index().j - b.getCell_Index().j);
    }

    private ArrayList<Cell> populateNeighbours(Cell currentCell) {

        ArrayList<Cell> neighbours = new ArrayList<>();

        //Add Left/Up/Right/Down Moves
        for (int i = 0; i < 4; i++) {
            Cell neighbour = this.getNeighbourNode(currentCell.getCell_Index().i + LURDMoves.get(i).i, currentCell.getCell_Index().j + LURDMoves.get(i).j);
            if (neighbour != null) {
                if (!neighbour.isWall())
                    neighbours.add(neighbour);
            }
        }

        if (allowDiagonals) {
            for (int i = 0; i < 4; i++) {
                Cell neighbour = this.getNeighbourNode(currentCell.getCell_Index().i + diagonalMoves.get(i).i, currentCell.getCell_Index().j + diagonalMoves.get(i).j);
                if (neighbour != null) {
                    if (!neighbour.isWall())
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
}
