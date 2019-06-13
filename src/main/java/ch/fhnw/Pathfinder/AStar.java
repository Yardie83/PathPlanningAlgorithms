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


    ArrayList<Cell> localPath;

    @Override
    public void init() {
        super.init();

        localPath = new ArrayList<>();

        map.getGrid().forEach(cells -> cells.forEach(cell -> {
            cell.setVisited(false);
            cell.setG_score(1);
            if (cell.isStart()) {
                cell.setF_score(0);
                cell.setRobotPosition(true);
                lastCell = currentCell = cell;
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

        if (currentCell != null) {
            currentCell.setRobotPosition(true);
            lastCell.setRobotPosition(false);

            currentCell.setRobotPosition(false);
            currentCell.setVisited(true);
            if (noveltyActive) incrementNoveltyCellValue(currentCell);
            localPath.add(currentCell);
            lastCell = (Cell) currentCell.clone();

            map.getGrid().forEach(cells -> {
                cells.forEach(cell -> {
                    cell.setRobotPosition(false);
                    if (cell.sameIndex(lastCell)) {
                        cell.setRobotPosition(true);
                    }
                    if (cell.sameIndex(currentCell)) {
                        cell.setG_score(currentCell.getG_score());
                    }
                });
            });

            openList.remove(currentCell);
            closedList.add(currentCell);

            checkForCheckPoint();
            if (checkForLastCheckPoint()) return;

            updateNeighbours();
        }

    }

    private void incrementNoveltyCellValue(Cell currentCell) {
        currentCell.setG_score(currentCell.getG_score() + 2);
    }

    private boolean checkForLastCheckPoint() {
        if (lastCheckPointFound()) {
            return true;
        }
        return false;
    }

    private void checkForCheckPoint() {
        if (checkPointFound(currentCell)) {
            map.getGrid().forEach(cells -> cells.forEach(cell -> {
                if (cell.isStart()) {
                    cell.setStart(false);
                }
            }));

            openList.clear();
            closedList.clear();
            openList.add(currentCell);

            map.getGrid().forEach(cells -> cells.forEach(cell -> cell.setVisited(false)));
        }
    }

    private void updateNeighbours() {
        ArrayList<Cell> neighboursCells = getNeighbours(currentCell);

        neighboursCells.forEach(neighbourCell ->
        {
            neighbourCell.setF_score(heuristic(neighbourCell, checkPoints.get(0)));
            if (!openList.contains(neighbourCell) && !neighbourCell.isWall() && !neighbourCell.isVisited()) {
                openList.add(neighbourCell);
            }
        });
    }


    private Cell getLowestDistanceCell() {
        double f_min = Double.POSITIVE_INFINITY;
        Cell minDistCell = null;
        for (ArrayList<Cell> cells : map.getGrid()) {
            for (Cell cell : cells) {
                for (Cell openListCell :
                        openList) {
                    if (cell.sameIndex(openListCell)) {
                        if (cell.getF_score() < f_min) {
                            minDistCell = cell;
                            f_min = cell.getF_score();

                        }
                    }
                }
            }
        }

        if (minDistCell != null && minDistCell.isWall()) {
//            TODO: Add penalty time and return null
            minDistCell.setVisited(true);
            openList.remove(minDistCell);

            return null;
        }
        return minDistCell;
    }

    public ArrayList<Cell> getLocalPath() {
        return localPath;
    }
}
