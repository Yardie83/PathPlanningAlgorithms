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
                robotCell = cell;
                currentCell = cell;
                localPath.add(robotCell);
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

        if (isNeighbour) currentCell = getLowestDistanceCell();
        if (currentCell == null) return;

        currentCell.setRobotPosition(true);
        robotCell.setRobotPosition(false);

        ArrayList<Cell> currentCellNeighbours = getNeighbours(currentCell);

        if (currentCell != robotCell) {
            isNeighbour = false;
            for (Cell currentCellNeighbour : currentCellNeighbours) {
                if (currentCellNeighbour.getIndex().i == robotCell.getIndex().i && currentCellNeighbour.getIndex().j == robotCell.getIndex().j) {
                    isNeighbour = true;
                }
            }

            if (!isNeighbour) {
                if (localPath.size() > 1 && !currentCell.isStart()) {
                    for (int i = 0; i < localPath.size(); i++) {
                        Cell localPathCell = localPath.get(i);
                        if (localPathCell.getIndex().i == robotCell.getIndex().i && localPathCell.getIndex().j == robotCell.getIndex().j) {
                            currentCell.setRobotPosition(false);
                            currentCell = localPath.get(localPath.indexOf(localPathCell) - 1);
                            System.out.println("[Changed CurrentCell]" + currentCell.getIndex().i + ":" + currentCell.getIndex().j + ", [F-Score]" + currentCell.getF_score());
                            currentCell.setRobotPosition(true);
                            return;
                        }
                    }
                }
                return;
            }
            isNeighbour = true;
        }
        System.out.println("[UnChanged CurrentCell]" + currentCell.getIndex().i + ":" + currentCell.getIndex().j + ", [F-Score]" + currentCell.getF_score());
        currentCell.setCameFrom(new Pair<>(robotCell.getIndex().i, robotCell.getIndex().j));
        currentCell.setRobotPosition(false);
        robotCell = currentCell;
        robotCell.setRobotPosition(true);

        if (!(localPath.get(localPath.size() - 1).getIndex().i == robotCell.getIndex().i && localPath.get(localPath.size() - 1).getIndex().j == robotCell.getIndex().j)) {
            localPath.add((Cell) robotCell.clone());
        }

        currentCell.setVisited(true);
        openList.remove(currentCell);
        closedList.add(currentCell);

        if (checkPointFound(currentCell)) {

            map.getGrid().forEach(cells -> cells.forEach(cell -> {
                if (cell.isStart()) {
                    cell.setStart(false);
                }
            }));

            currentCell.setStart(true);

            openList.clear();
            closedList.clear();

            openList.add(currentCell);

            map.getGrid().forEach(cells -> cells.forEach(cell -> cell.setVisited(false)));
        }

        if (lastCheckPointFound()) return;


        ArrayList<Cell> neighboursCells = getNeighbours(currentCell);
        List<Cell> neighbours = neighboursCells.stream().filter(cell -> (!closedList.contains(cell))).collect(Collectors.toList());

        neighbours.forEach(neighbourCell ->
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
