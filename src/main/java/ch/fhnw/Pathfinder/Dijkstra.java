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
            if (cell.isStart()) cell.setDistance(0);
            cell.setCost(1);
            if (cell.isWall()) cell.setCost(Integer.MAX_VALUE);
            openList.add(cell);
        }));
    }

    public void step() {
        if (openList.isEmpty() || checkPoints.isEmpty()) {
            isRunning = false;
            return;
        }
        Cell currentCell = getLowestDistanceCell();
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
        double alternative = currentCell.getDistance() + neighbour.getCost();
        if (alternative < neighbour.getDistance()) {
            neighbour.setDistance(alternative);
            neighbour.setF_score(alternative);
            neighbour.setCameFrom(new Pair(currentCell.getIndex().i, currentCell.getIndex().j));
        }
    }
}
