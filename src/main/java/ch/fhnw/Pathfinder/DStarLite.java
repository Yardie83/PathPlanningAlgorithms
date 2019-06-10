package ch.fhnw.Pathfinder;

import ch.fhnw.Cell;
import ch.fhnw.util.Pair;


import java.util.*;

public class DStarLite extends Pathfinder {

    private Cell start;
    private LinkedHashMap<Cell, Pair> priorityQueue;

    @Override
    public void init() {
        super.init();
        priorityQueue = new LinkedHashMap<>();
        int km = 0;
        for (int i = 0; i < map.getGrid().size(); i++) {
            for (int j = 0; j < map.getGrid().get(i).size(); j++) {
                Cell cell = map.getGrid().get(i).get(j);
                cell.setRHS(Double.POSITIVE_INFINITY);
                cell.setG_score(Double.POSITIVE_INFINITY);
                cell.setCost(1);
                if (cell.isStart()) {
                    start = cell;
                }
                if (cell.isCheckPoint()) {
                    cell.setRHS(0);
                    priorityQueue.put(cell, new Pair<>(heuristic(start, start), 0d));
                }
            }
        }
    }

    @Override
    public void step() {
        Cell last = start;
        computeShortestPath();
        while (start != checkPoints.get(0)) {
            if (start.getRHS() == Double.POSITIVE_INFINITY) return;
            ArrayList<Cell> neighbours = getNeighbours(start);
            Optional<Cell> min = neighbours.stream().min(Comparator.comparing(Cell::getG_score));
            min.ifPresent(cell -> {
                start = cell;
                System.out.println(start.getIndex().i + ": " + start.getIndex().j);
            });
        }
    }

    private void computeShortestPath() {
        Map.Entry<Cell, Pair> next = priorityQueue.entrySet().iterator().next();
        Cell u = next.getKey();
        Pair key = next.getValue();
        while (key.lessThan(calculateKey(start)) || start.getRHS() > start.getG_score()) {
            Pair k_new = calculateKey(u);
            ArrayList<Cell> neighbours = getNeighbours(u);
            if (key.lessThan(k_new)) {
                priorityQueue.put(u, k_new);
            } else if (u.getG_score() > u.getRHS()) {
                u.setG_score(u.getRHS());
                priorityQueue.remove(u);
                neighbours.forEach(s -> {
                    if (s != checkPoints.get(0)) {
                        s.setRHS(Math.min(s.getRHS(), u.getG_score()));
                        updateVertex(s);
                    }
                });
            } else {
                double g_score_old = u.getG_score();
                u.setG_score(Double.MAX_VALUE);

                double tempG = Double.POSITIVE_INFINITY;
                double lowestG;
                for (Cell s : neighbours) {
                    lowestG = s.getG_score();
                    if (s.getG_score() < tempG) {
                        lowestG = tempG;
                    }
                    if (s.getRHS() == g_score_old) {
                        if (s != checkPoints.get(0)) {
                            s.setRHS(lowestG);
                        }
                    }
                    updateVertex(s);
                }
            }
        }
    }

    private void updateVertex(Cell u) {
        if (u.getG_score() != u.getRHS() && priorityQueue.containsKey(u)) {
            priorityQueue.put(u, calculateKey(u));
        } else if (u.getG_score() == u.getRHS()) {
            priorityQueue.remove(u);
        }
    }

    private Pair calculateKey(Cell s) {
        return new Pair<>(Math.min(s.getG_score(), s.getRHS() + heuristic(start, s)), Math.min(s.getG_score(), s.getRHS()));
    }

    @Override
    public ArrayList<Cell> getShortestPath() {
        return null;
    }
}
