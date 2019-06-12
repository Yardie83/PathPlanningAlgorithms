package ch.fhnw.Pathfinder;

import ch.fhnw.Cell;

import java.util.ArrayList;

public class MPAA extends Pathfinder {

    private int counter;

    @Override
    public void init() {
        super.init();
        counter = 0;
        map.getGrid().forEach(cells -> cells.forEach(cell -> {
            if(cell.isStart()){
                observe(cell);
            }
        }));
    }

    private void observe(Cell cell) {

    }

    private void initState(Cell cell){
        if (cell.getSearch() != counter){
            cell.setG_score(Double.MAX_VALUE);
        }
        cell.setSearch(counter);
    }

    @Override
    public void step() {

    }

    @Override
    public ArrayList<Cell> getShortestPath() {
        return null;
    }

    @Override
    public ArrayList<Cell> getLocalPath() {
        return null;
    }
}
