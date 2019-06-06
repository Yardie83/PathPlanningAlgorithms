package ch.fhnw;

import ch.fhnw.util.Pair;

import java.util.ArrayList;

public class Map {

    private ArrayList<ArrayList<Cell>> grid;
    private ArrayList<Cell> path;

    Map(int mapSize) {
        grid = new ArrayList<>();
        for (int i = 0; i < mapSize; i++) {
            grid.add(new ArrayList<>());
            for (int j = 0; j < mapSize; j++) {
                grid.get(i).add(new Cell(new Pair(i, j)));
            }
        }
    }

    public ArrayList<ArrayList<Cell>> getGrid() {
        return grid;
    }

    void addStairsSimulationFeatures() {
        for (int i = 2; i < 4; i++) {
            grid.get(i).get(14).setWall(true);
        }
        for (int i = 13; i < 15; i++) {
            grid.get(3).get(i).setWall(true);
        }
        for (int i = 3; i < 10; i++) {
            grid.get(i).get(12).setWall(true);
        }
        for (int i = 12; i < 18; i++) {
            grid.get(10).get(i).setWall(true);
        }
        for (int i = 10; i < 14; i++) {
            grid.get(i).get(18).setWall(true);
        }
        for (int i = 18; i < 21; i++) {
            grid.get(14).get(i).setWall(true);
        }
        for (int i = 14; i < 17; i++) {
            grid.get(i).get(21).setWall(true);
        }
        for (int i = 21; i < 23; i++) {
            grid.get(17).get(i).setWall(true);
        }
        for (int i = 17; i < 20; i++) {
            grid.get(i).get(23).setWall(true);
        }
        for (int i = 23; i < 25; i++) {
            grid.get(20).get(i).setWall(true);
        }
        for (int i = 20; i < 24; i++) {
            grid.get(i).get(25).setWall(true);
        }

        grid.get(5).get(11).setCheckPoint(true);
        grid.get(21).get(26).setCheckPoint(true);

        grid.get(24).get(28).setStart(true);

    }

    void addTheWallMapFeatures() {
        for (int i = 1; i < 10; i++) {
            for (int j = 1; j < 4; j++) {
                grid.get(i).get(j).setWall(true);
            }
        }
        for (int i = 4; i < 11; i++) {
            grid.get(9).get(i).setWall(true);
        }

        for (int i = 9; i < 13; i++) {
            grid.get(i).get(14).setWall(true);
        }
        for (int i = 14; i < 61; i++) {
            grid.get(13).get(i).setWall(true);
        }
        grid.get(10).get(30).setCheckPoint(true);
        grid.get(50).get(9).setCheckPoint(true);
        grid.get(1).get(63).setCheckPoint(true);

        grid.get(0).get(0).setStart(true);
    }

    void setPath(ArrayList<Cell> path) {
        this.path = path;
    }

    ArrayList<Cell> getPath() {
        return path;
    }
}
