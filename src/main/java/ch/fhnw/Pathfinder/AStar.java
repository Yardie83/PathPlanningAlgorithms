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
                localPath.add(currentCell);
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
    private boolean isNeighbour = true;

    @Override
    public void step() {
        if (openList.isEmpty() || checkPoints.isEmpty()) {
            isRunning = false;
            return;
        }
        System.out.println("============= START ==============================");
        System.out.println("CurrentCell: " + currentCell.getIndex().i + ":" + currentCell.getIndex().j);
        if (isNeighbour) {
            currentCell = getLowestDistanceCell();
            System.out.println(currentCell);
            System.out.println("New lowest: " + currentCell.getIndex().i + ":" + currentCell.getIndex().j);
        }

        if (currentCell != null) {
            currentCell.setRobotPosition(true);
            lastCell.setRobotPosition(false);

            System.out.println("============= CURRENT / LAST ======================");
            if (!currentCell.sameIndex(lastCell)) {
                isNeighbour = false;
                System.out.println("CurrentCell: " + currentCell.getIndex().i + ":" + currentCell.getIndex().j);
                System.out.println("LastCell: " + lastCell.getIndex().i + ":" + lastCell.getIndex().j);
                System.out.println("============= NEIGHBOURS ======================");
                getNeighbours(currentCell).forEach(cell -> {
                    System.out.println("NeighbourCell: " + cell.getIndex().i + ":" + cell.getIndex().j);
                    if (cell.sameIndex(lastCell)) {
                        System.out.println("============= NEIGHBOUR TRUE ======================");
                        isNeighbour = true;
                    }
                });

                if (!isNeighbour) {
                    System.out.println("============= LOCAL PATH ======================");
                    Optional<Cell> first = localPath.stream().filter(localCell -> localCell.sameIndex(lastCell)).findFirst();
                    if (first.isPresent()) {
                        Cell cell = first.get();
                        System.out.println("Cell found: " + cell.getIndex().i + ":" + cell.getIndex().j);
                        if (localPath.indexOf(cell) > 0) {
                            System.out.println(localPath.get(localPath.indexOf(cell) - 1).getIndex().i + ":" + localPath.get(localPath.indexOf(cell) - 1).getIndex().j);
                            currentCell = localPath.get(localPath.indexOf(cell) - 1);
                            return;
                        }
                    }
                    return;
                }
            }
            System.out.println("============= NOT Returned PATH ======================");
            isNeighbour = true;

            currentCell.setRobotPosition(false);
            currentCell.setVisited(true);
            localPath.add(currentCell);
            lastCell = (Cell) currentCell.clone();
            map.getGrid().forEach(cells ->{cells.forEach(cell -> {
                cell.setRobotPosition(false);
                if (cell.sameIndex(lastCell)){
                    cell.setRobotPosition(true);
                }
            });} );

            openList.remove(currentCell);
            closedList.add(currentCell);

            checkForCheckPoint();
            if (checkForLastCheckPoint()) return;

            updateNeighbours();
        }
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
//        List<Cell> neighbours = neighboursCells.stream().filter(cell -> (!closedList.contains(cell))).collect(Collectors.toList());

        neighboursCells.forEach(neighbourCell ->
        {
            neighbourCell.setF_score(heuristic(neighbourCell, checkPoints.get(0)));
            if (!openList.contains(neighbourCell) && !neighbourCell.isWall() && !neighbourCell.isVisited()) {
                openList.add(neighbourCell);
            }
        });
    }


    private Cell getLowestDistanceCell() {
        Optional<Cell> minDistanceCell = openList.stream().min(Comparator.comparing(Cell::getF_score));
        Cell cell = minDistanceCell.orElse(null);
        if (cell != null && cell.isWall()) {
//            TODO: Add penalty time and return null
            cell.setVisited(true);
            openList.remove(cell);
            return null;
        }
        return cell;
    }

    public ArrayList<Cell> getLocalPath() {
        return localPath;
    }
}
