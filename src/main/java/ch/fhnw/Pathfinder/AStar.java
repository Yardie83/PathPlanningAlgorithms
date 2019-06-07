package ch.fhnw.Pathfinder;

import ch.fhnw.Cell;
import ch.fhnw.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AStar extends Pathfinder {

//    boolean allowCrossingCorners;
//    Map map;
//    int start_i;
//    int start_j;
//    int target_i;
//    int target_j;
//    boolean allowDiagonals;
//    int heuristic;
//    public boolean isRunning = true;

    private ArrayList<Cell> openlist;
    private ArrayList<Cell> closedList;
    private boolean targetFound;

    @Override
    public void init() {
        openlist = new ArrayList<>();
        closedList = new ArrayList<>();
        targetFound = false;

        map.getGrid().forEach(cells -> cells.forEach(cell -> {
            cell.setG_score(1);
            if (cell.isStart()) {
                cell.setF_score(0);
            }
        }));

        map.getGrid().stream().flatMap(List::stream).collect(Collectors.toList()).stream().filter(Cell::isStart).findFirst().ifPresent(startCell -> openlist.add(startCell));

    }

    //    1. get lowest f-value cell
//    2. check if i am neighbour of this cell
//    3. if not drive back until i am neighbour
//    4. check if is wall
//    5. if wall then add time and step and remove cell from openlist
//    6 repeat
    @Override
    public Cell step() {
        if (!openlist.isEmpty() && !map.getCheckPoints().isEmpty()) {

            Optional<Cell> minCellOptional = openlist.stream().min(Comparator.comparing(Cell::getF_score));
            if (minCellOptional.isPresent()) {
                Cell currentCell = minCellOptional.get();
                currentCell.setVisited(true);
                openlist.remove(currentCell);
                closedList.add(currentCell);

//                If we found a checkpoint
                if (map.getCheckPoints().get(0).equals(currentCell)) {
                    map.getCheckPoints().remove(0);
                    if (map.getCheckPoints().isEmpty()) {
                        isRunning = false;
                    }
                    return new Cell(new Pair(0, 0));
                }

                ArrayList<Cell> neighboursCells = getNeighbours(currentCell);
                List<Cell> neighbours = neighboursCells.stream().filter(cell -> (!closedList.contains(cell))).collect(Collectors.toList());

                neighbours.forEach(neighbourCell -> {
                    double tempG_Score = currentCell.getG_score() + heuristic(currentCell, neighbourCell);
                    if (tempG_Score < neighbourCell.getG_score()) {
                        neighbourCell.setG_score(tempG_Score);
                        neighbourCell.setH_score((heuristic(currentCell, neighbourCell)));
                        neighbourCell.setF_score(neighbourCell.getG_score() + neighbourCell.getH_score());
                        neighbourCell.setCameFrom(new Pair(currentCell.getIndex().i, currentCell.getIndex().j));
                    }
                    if (!openlist.contains(neighbourCell)) {
                        openlist.add(neighbourCell);
                    }
                });
            }
        }
        return null;
    }

    @Override
    public ArrayList<Cell> getShortestPath() {
        ArrayList<Cell> path = null;
        Cell target = map.getGrid().stream().flatMap(List::stream).collect(Collectors.toList()).stream().filter(cell -> cell.isCheckPoint() || cell.isRobotPosition()).findFirst().orElse(null);

        if (target != null) {
            if (target.getCameFrom() != null) {
                path = new ArrayList<>();
                path.add(target);
            }

            while (target.getCameFrom() != null) {

                path.add(0, target);
            }
        }
        return path;
    }


    private double heuristic(Cell currentCell, Cell neighbour) {
        double distance;
        if (heuristic == 0) {
            // Euclidean distance
            distance = calculateEuclideanDistance(currentCell, neighbour);
        } else {
            //Manhattan distance
            distance = calculateManhattanDistance(currentCell, neighbour);
        }
        return distance;
    }

    private double calculateEuclideanDistance(Cell a, Cell b) {
        double ac = Math.abs(a.getIndex().i - b.getIndex().i);
        double cb = Math.abs(b.getIndex().i - b.getIndex().j);
        return Math.hypot(ac, cb);
    }

    private int calculateManhattanDistance(Cell a, Cell b) {
        return Math.abs(a.getIndex().i - b.getIndex().i) + Math.abs(a.getIndex().j - b.getIndex().j);
    }


}
