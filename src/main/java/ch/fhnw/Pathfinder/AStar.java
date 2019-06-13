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
    private boolean isNeighbour = true;
    private int localPathBackIndex = -1;

    @Override
    public void step() {
        if (openList.isEmpty() || checkPoints.isEmpty()) {
            isRunning = false;
            return;
        }
        if (isNeighbour) {
            currentCell = getLowestDistanceCell();
        }
        if (currentCell != null) {
            System.out.println("CurrentCell : " + currentCell.getIndex().i + ":" + currentCell.getIndex().j + " | ");
            currentCell.setRobotPosition(true);
            lastCell.setRobotPosition(false);

            if (!currentCell.sameIndex(lastCell)) {
                isNeighbour = false;
                getNeighbours(currentCell).forEach(cell -> {
                    if (cell.sameIndex(lastCell)) {
                        isNeighbour = true;
                        localPathBackIndex = localPath.size() - 1;
                    }
                });

                if (!isNeighbour) {
                    localPathBackIndex = localPathBackIndex-1;
                    System.out.println(localPathBackIndex);
                    localPath.forEach(cell -> System.out.print(cell.getIndex().i + ":" + cell.getIndex().j + " | "));
                    System.out.println(" | ");
                    for (int i = localPath.size() - 1; i >= 0; i--) {
                        Cell cell = localPath.get(i);
                        if (cell.sameIndex(lastCell)) {

                            if (localPath.size() > 0 && localPath.indexOf(cell) > 1) {
                                if (!localPath.get(localPath.size() - 1).sameIndex(currentCell)) {
//                                    localPath.add(cell);
                                    currentCell = localPath.get(localPathBackIndex);
                                    return;
                                }
                                return;
                            }
                        }
                    }
                    System.out.println("\n");
//                    return;
                }
            }
            isNeighbour = true;

            currentCell.setRobotPosition(false);
            currentCell.setVisited(true);
            localPath.add(currentCell);
            lastCell = (Cell) currentCell.clone();
            map.getGrid().forEach(cells -> {
                cells.forEach(cell -> {
                    cell.setRobotPosition(false);
                    if (cell.sameIndex(lastCell)) {
                        cell.setRobotPosition(true);
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
