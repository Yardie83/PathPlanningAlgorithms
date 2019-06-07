package ch.fhnw.Pathfinder;

import ch.fhnw.Cell;
import ch.fhnw.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

class Dijkstra extends Pathfinder {

    private ArrayList<Pair> openList;
    private boolean targetFound = false;

    public void init() {

//        openList = new ArrayList<>();
//        targetFound = false;
//
//        for (int i = 0; i < this.map.getGrid().size(); i++) {
//            for (int j = 0; j < map.getGrid().get(i).size(); j++) {
//                Cell cell = map.getGrid().get(i).get(j);
//                cell.setVisited(false);
//                cell.setDistance(Integer.MAX_VALUE);
//                cell.setCameFrom(null);
//                ArrayList<Pair> neighbours = getNeighbours(cell);
//                cell.setNeighbours(neighbours);
//                if (cell.isStart()) {
//                    cell.setDistance(0);
//                }
//                openList.add(new Pair(cell.getIndex().i, cell.getIndex().j));
//            }
//        }
    }

//    private ArrayList<Pair> getNeighbours(Cell currentCell) {
//        ArrayList<Pair> neighbours = new ArrayList<>();
//
//        //Add Left/Up/Right/Down Moves
//        for (int i = 0; i < 4; i++) {
//            Cell neighbour = this.getNeighbourNode(currentCell.getIndex().i + Moves.LURDMoves.get(i).i, currentCell.getIndex().j + Moves.LURDMoves.get(i).j);
//            if (neighbour != null) {
//                if (!neighbour.isWall()) {
//                    neighbours.add(new Pair(neighbour.getIndex().i, neighbour.getIndex().j));
//                }
//            }
//        }
//        if (allowDiagonals) {
//            for (int i = 0; i < 4; i++) {
//                Cell neighbour = this.getNeighbourNode(currentCell.getIndex().i + Moves.diagonalMoves.get(i).i, currentCell.getIndex().j + Moves.diagonalMoves.get(i).j);
//                if (neighbour != null) {
//                    if (!neighbour.isWall()) {
//                        neighbours.add(new Pair(neighbour.getIndex().i, neighbour.getIndex().j));
//                    }
//                }
//            }
//        }
//        return neighbours;
//    }
//
//    private Cell getNeighbourNode(int i, int j) {
//        if (i < 0 || i >= map.getGrid().size() ||
//                j < 0 || j >= map.getGrid().size()) {
//            return null;
//        }
//        return map.getGrid().get(i).get(j);
//    }

    public Cell step() {
        if (!openList.isEmpty() && !targetFound) {
            int robotPosition = getNextRobotPosition();
            Pair openListCell = openList.get(robotPosition);
            Cell cell = map.getGrid().get(openListCell.i).get(openListCell.j);
            openList.remove(robotPosition);

//            The robot position becomes the index of one the neighbours visited

            cell.getNeighbours().forEach(neighbour -> {
                Cell neighbourCell = map.getGrid().get(neighbour.i).get(neighbour.j);
                if (!neighbourCell.isVisited()) {
                    update_Distance(cell, neighbourCell);
                    neighbourCell.setVisited(true);
                    if (neighbourCell.isCheckPoint()) {
                        targetFound = true;
                    }
                }
            });
        } else {
            isRunning = false;
        }
        return new Cell(new Pair(0, 0));
    }

    private int getNextRobotPosition() {

        int minIndex;

        if (openList.isEmpty()) {
            minIndex = -1;
        } else {
            final ListIterator<Cell> itr = map.getGrid().stream().flatMap(List::stream).collect(Collectors.toList()).listIterator();
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
//            neighbour.setCameFrom(currentCell);
        }
    }

    private double calculateEuclideanDistance(Cell a, Cell b) {
        double ac = Math.abs(a.getIndex().i - b.getIndex().i);
        double cb = Math.abs(b.getIndex().i - b.getIndex().j);

        return Math.hypot(ac, cb);
    }

    private int calculateManhattanDistance(Cell a, Cell b) {
        return Math.abs(a.getIndex().i - b.getIndex().i) + Math.abs(a.getIndex().j - b.getIndex().j);
    }

    public ArrayList<Cell> getShortestPath() {

        ArrayList<Cell> path = null;

        Cell target = map.getGrid().stream().flatMap(List::stream).collect(Collectors.toList()).stream().filter(cell -> cell.isCheckPoint() || cell.isRobotPosition()).findFirst().orElse(null);

        if (target != null) {
            Cell currentCell = target;
            if (currentCell.getCameFrom() != null) {
                path = new ArrayList<>();
                path.add(target);
            }

            while (currentCell.getCameFrom() != null) {
//                currentCell = currentCell.getCameFrom();
                path.add(0, currentCell);
            }
        }
        return path;
    }

}
