package ch.fhnw.Pathfinder;

import ch.fhnw.Cell;

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

    @Override
    public void init() {
        super.init();

        map.getGrid().forEach(cells -> cells.forEach(cell -> {
            cell.setG_score(1);
            cell.setF_score(heuristic(cell, map.getCheckPoints().get(0)));
            if (cell.isStart()) {
                cell.setF_score(0);
                cell.setRobotPosition(true);
                robotCell = cell;
            }
            if (cell.isWall()) cell.setF_score(Integer.MAX_VALUE);
        }));

        map.getGrid().stream().flatMap(List::stream).collect(Collectors.toList()).stream().filter(Cell::isStart).findFirst().ifPresent(startCell -> openList.add(startCell));

    }

    //    1. get lowest f-value cell
//    2. check if i am neighbour of this cell
//    3. if not drive back until i am neighbour
//    4. check if is wall
//    5. if wall then add time and step and remove cell from openList
//    6 repeat
    @Override
    public void step() {
        if (openList.isEmpty() || checkPoints.isEmpty()) {
            isRunning = false;
            return;
        }
        currentCell = getLowestDistanceCell();
        if (currentCell == null) return;
        currentCell.setRobotPosition(true);
        robotCell.setRobotPosition(false);
        robotCell = currentCell;
        currentCell.setVisited(true);
        openList.remove(currentCell);
        closedList.add(currentCell);
        if (checkPointFound(currentCell)) return;

        ArrayList<Cell> neighboursCells = getNeighbours(currentCell);
        List<Cell> neighbours = neighboursCells.stream().filter(cell -> (!closedList.contains(cell))).collect(Collectors.toList());

        neighbours.forEach(neighbourCell -> {

//                    neighbourCell.setG_score(tempG_Score);
//                        neighbourCell.setCameFrom(new Pair(currentCell.getIndex().i, currentCell.getIndex().j));
//                    }
//
//                    neighbourCell.setH_score((heuristic(neighbourCell, checkPoints.get(0))));
//                    neighbourCell.setF_score(neighbourCell.getG_score() + neighbourCell.getH_score());

            if (!openList.contains(neighbourCell)) {
                openList.add(neighbourCell);
            }
        });
    }

    private Cell getLowestDistanceCell() {
        Optional<Cell> minDistanceCell = openList.stream().min(Comparator.comparing(Cell::getF_score));
        Cell cell = minDistanceCell.orElse(null);
        if (cell != null && cell.isWall()){
//            TODO: Add penalty time and return null
            openList.remove(cell);
            return null;
        }
        return cell;
    }
}
