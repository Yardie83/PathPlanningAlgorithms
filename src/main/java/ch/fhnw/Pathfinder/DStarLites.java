package ch.fhnw.Pathfinder;

import ch.fhnw.Cell;
import ch.fhnw.util.Pair;


import java.util.*;

public class DStarLites extends Pathfinder {

    private Cell start;
    private Cell goal;
    private PriorityQueue<Cell> U;
    private int k_m;

    @Override
    public void init() {
        super.init();
        path.clear();
        U = new PriorityQueue<>();
        k_m = 0;

        map.getGrid().forEach(cells -> {
            cells.forEach(cell -> {
                cell.setVisited(false);
                cell.setRHS(Double.POSITIVE_INFINITY);
                cell.setG_score(Double.POSITIVE_INFINITY);
                if (cell.isCheckPoint()) {
                    cell.setRHS(0);
                    cell.keys.i = heuristic(start, cell);
                    cell.keys.j = 0.0;
                    goal = cell;

                }
                if (cell.isStart()) {
                    start = cell;
                    currentCell = start;
                }
            });
        });
        U.add(goal);
        computeShortestPath();
    }

    @Override
    public void step() {
        ArrayList<Cell> neighbours;
        if (currentCell != goal) {
            currentCell.setVisited(true);
            path.add(currentCell);
            neighbours = getNeighbours(currentCell);

            double minCost = Double.POSITIVE_INFINITY;
            double minDist = 0;

            Cell minCell  =null;

            for (Cell neighbour : neighbours) {
                if (!neighbour.isVisited()) {
                    double cost = neighbour.getG_score();
                    double distance = heuristic(neighbour, goal) + heuristic(start, neighbour);

                    if (cost == minCost) {
                        if (minDist > distance) {
                            minDist = distance;
                            minCost = cost;
                            minCell = neighbour;
                        }
                    } else if (cost < minCost) {
                        minDist = distance;
                        minCost = cost;
                        minCell = neighbour;
                    }
                }
                if (minCell != null) {
                    currentCell = minCell;
                }
            }
            return;
        }
        isRunning = false;
        System.out.println(goal.getIndex().i + " : " + goal.getIndex().j);
        path.add(goal);
    }

    private void computeShortestPath() {

        int k = 0;
        while ((!U.isEmpty()) &&
                (U.peek().keys.lessThan(calculateKey(start).keys) ||
                        (start.getRHS() != start.getG_score()))) {

            if (k++ > 80000) {
                System.out.println("At maxSteps");
                return;
            }

            Cell u = U.peek();
            Pair key_old = null;
            if (U.peek() != null) {
                key_old = U.peek().keys;
            }
            Pair<Double, Double> key_new = calculateKey(u).keys;
            if (key_old.lessThan(key_new)) {
                u.keys = key_new;
            } else if (u.getG_score() > u.getRHS()) {
                u.setG_score(u.getRHS());
                U.remove(u);
                getNeighbours(u).forEach(cell -> {
                    if (cell != goal) {
                        cell.setRHS(Math.min(cell.getRHS(), heuristic(cell, u)));
                    }
                    updateVertex(cell);
                });
            } else {
                u.setG_score(Double.POSITIVE_INFINITY);
                getNeighbours(u).forEach(this::updateVertex);
            }
            updateVertex(u);
        }
    }

    private void updateVertex(Cell u) {
        if (u.getG_score() != u.getRHS() && U.contains(u)) u.keys = calculateKey(u).keys;
        else if ((u.getG_score() != u.getRHS() && !U.contains(u))) {
            u.keys = calculateKey(u).keys;
            U.add(u);
        } else if ((u.getG_score() == u.getRHS())) U.remove(u);
    }

    private Cell calculateKey(Cell u) {
        double val = Math.min(u.getRHS(), u.getG_score());

        u.keys.i = val + heuristic(u, start) + k_m;
        u.keys.j = val;

        return u;
    }

    @Override
    public ArrayList<Cell> getShortestPath() {
        return path;
    }
}
