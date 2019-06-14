package ch.fhnw.Pathfinder;

import ch.fhnw.Cell;
import ch.fhnw.util.Pair;

import java.awt.geom.Point2D;
import java.util.*;
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

    private int bumps;
    private int steps;
    private int confidentSteps;
    private Cell bumpCell;
    private HashMap<Cell, Integer> bumpRemember = new HashMap<>();

    private double totalTime;

    @Override
    public void init() {
        super.init();

        localPath = new ArrayList<>();
        bumps = 0;
        steps = -1;
        confidentSteps = 0;

        map.getGrid().forEach(cells -> cells.forEach(cell -> {
            cell.setVisited(false);
            cell.setG_score(1);
            if (cell.isStart()) {
                cell.setF_score(0);
                cell.setRobotPosition(true);
                lastCell = currentCell = cell;
            }
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

        if (explorationActive && currentCell != null) {
            activateExplorativeMode();
        }

        if (currentCell != null && !currentCell.isWall()) {

            if (confidenceActive && currentCell.isVisited()) {
                confidentSteps++;
            }

            steps++;
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
        System.out.println("[Steps] : " + steps);
    }


    private void activateExplorativeMode() {

        // remove bumps that are too old (10 steps)
        // increase for others
        if (!bumpRemember.isEmpty() && bumpCell != null) {
            for (Iterator<HashMap.Entry<Cell, Integer>> it = bumpRemember.entrySet().iterator(); it.hasNext(); ) {
                HashMap.Entry<Cell, Integer> entry = it.next();
                if (entry.getValue() > 10) {
                    it.remove();
                } else {
                    entry.setValue(entry.getValue() + 1);
                }
            }
        }

        if (bumpCell == null) {
            bumpCell = currentCell;
        }

        // if > 3 bumps, calculate jump-point
        if (bumpRemember.size() > 3) {
            int avgX = 0;
            int avgY = 0;
            for (Cell bumpCell : bumpRemember.keySet()) {
                avgX += (int) bumpCell.getIndex().i;
                avgY += (int) bumpCell.getIndex().j;
            }
            // bump center
            avgX = Math.round((float) avgX / bumpRemember.size());
            avgY = Math.round((float) avgY / bumpRemember.size());

            // mirror actual position to bump-center
            int goX = (int) currentCell.getIndex().i + ((int) currentCell.getIndex().i - avgX);
            int goY = (int) currentCell.getIndex().j + ((int) currentCell.getIndex().j - avgY);

            goX = Math.min(map.getGrid().size() - 1, goX);
            goX = Math.max(goX, 0);
            goY = Math.max(goY, 0);
            goY = Math.min(map.getGrid().size() - 1, goY);

            int finalGoX = goX;
            int finalGoY = goY;

            map.getGrid().forEach(cells -> cells.forEach(cell -> {
                if ((int) cell.getIndex().i == finalGoX && (int) cell.getIndex().j == finalGoY) currentCell = cell;
            }));

            bumpRemember.clear();
        }
    }

    private void incrementNoveltyCellValue(Cell currentCell) {
        currentCell.setG_score(currentCell.getG_score() + 2);
    }

    private boolean checkForLastCheckPoint() {
        if (lastCheckPointFound()) {
            calculateFinalValues();
            steps = steps - 1;
            return true;
        }
        return false;
    }

    private void calculateFinalValues() {

        totalTime = steps * 4;
        totalTime += bumps * 4;

        if (happyActive) {
            totalTime = steps / 2;
            double penaltyTime = bumps * 1 * 2 * 4;
            totalTime += penaltyTime;
        }

        if (confidenceActive) {
            //Normal steps
            totalTime = (steps - confidentSteps) / 2;

            //Confident steps
            totalTime += confidentSteps / 4;
        }

        System.out.println("Total Time: " + totalTime);
        System.out.println("Bumps " + bumps);
        System.out.println("Steps " + steps);
    }

    private void checkForCheckPoint() {
        if (checkPointFound(currentCell)) {
            openList.clear();
            closedList.clear();
            openList.add(currentCell);
            steps = steps - 1;

            map.getGrid().forEach(cells -> cells.forEach(cell -> cell.setVisited(false)));
        }
    }

    private void updateNeighbours() {
        ArrayList<Cell> neighboursCells = getNeighbours(currentCell);

        neighboursCells.forEach(neighbourCell ->
        {
            neighbourCell.setF_score(heuristic(neighbourCell, checkPoints.get(0)));
            if (!openList.contains(neighbourCell) && !neighbourCell.isVisited()) {
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
            System.out.println("is this cell visited? " + minDistCell.isVisited());

            bumps++;
            bumpRemember.put(minDistCell, 0);


            minDistCell.setVisited(true);
            minDistCell.setF_score(Integer.MAX_VALUE);

            openList.remove(minDistCell);

            return null;

        }
        return minDistCell;
    }

    public ArrayList<Cell> getLocalPath() {
        return localPath;
    }

    public Cell getBumpCell() {
        return bumpCell;
    }

    public int getBumps() {
        return bumps;
    }

    public int getSteps() {
        return steps;
    }

    public double getTotalTime() {
        return totalTime;
    }
}