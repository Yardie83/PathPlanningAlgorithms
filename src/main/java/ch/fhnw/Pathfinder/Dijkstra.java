package ch.fhnw.Pathfinder;

import ch.fhnw.Cell;
import ch.fhnw.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

class Dijkstra extends Pathfinder {

    public void init() {
        super.init();
        /*
         *  1. Init each cell with default values
         *  2. Start cell heuristic = 0
         *  3. Add cells to the openList
         */
        map.getGrid().forEach(cells -> cells.forEach(cell -> {
            cell.setVisited(false);
            cell.setCameFrom(null);
            cell.setDistance(Integer.MAX_VALUE);
            cell.setF_score(0);
            cell.setG_score(1);
            cell.setRobotPosition(false);
            if (cell.isStart()) {
                cell.setDistance(0);
                cell.setRobotPosition(true);
                robotCell = cell;
            }
            if (cell.isWall()) cell.setG_score(Integer.MAX_VALUE);
            openList.add(cell);
        }));
    }

    public void step() {
        if (openList.isEmpty() || checkPoints.isEmpty()) {
            isRunning = false;
            return;
        }
        robotCell.setRobotPosition(false);
        currentCell = getLowestDistanceCell();
        currentCell.setRobotPosition(true);
        robotCell = currentCell;
        currentCell.setVisited(true);
        openList.remove(currentCell);
        if (checkPointFound(currentCell)) return;
        ArrayList<Cell> neighbours = (ArrayList<Cell>) getNeighbours(currentCell).stream().filter(neighbour -> openList.contains(neighbour)).collect(Collectors.toList());
        neighbours.forEach(neighbourCell -> {
            updateDistance(currentCell, neighbourCell);
            if (!neighbourCell.isWall()) neighbourCell.setVisited(true);
        });
    }

    private Cell getLowestDistanceCell() {
        Optional<Cell> minDistanceCell = openList.stream().min(Comparator.comparing(Cell::getDistance));
        return minDistanceCell.orElse(null);
    }

    private void updateDistance(Cell currentCell, Cell neighbour) {
        double alternative = currentCell.getDistance() + heuristic(currentCell, neighbour);
        if (alternative < neighbour.getDistance()) {
            neighbour.setDistance(alternative);
            neighbour.setCameFrom(new Pair(currentCell.getIndex().i, currentCell.getIndex().j));

//         For visualization purposes we store the distance from start  to the current cell
            map.getGrid().forEach(cells -> {
                cells.forEach(cell -> {
                    if (cell.getIndex().i == neighbour.getIndex().i && cell.getIndex().j == neighbour.getIndex().j) {
                        cell.setF_score(alternative);
                    }
                });
            });
        }
    }
}