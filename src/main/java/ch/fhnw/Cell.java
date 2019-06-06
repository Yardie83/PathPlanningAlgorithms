package ch.fhnw;

import ch.fhnw.util.Pair;

import java.util.ArrayList;

public class Cell {

    private boolean isWall = false;
    private boolean isCheckPoint = false;
    private double distance;
    private ArrayList<Pair> neighbours;
    private Cell cameFrom;
    private Pair index;
    private boolean isStart;
    private boolean isTarget;
    private boolean visited;
    private boolean robotPosition;


    public boolean isStart() {
        return isStart;
    }

    void setStart(boolean start) {
        isStart = start;
    }

    public Cell(Pair index) {
        this.index = index;
    }

    public boolean isWall() {
        return isWall;
    }

    void setWall(boolean wall) {
        isWall = wall;
    }

    boolean isCheckPoint() {
        return isCheckPoint;
    }

    void setCheckPoint(boolean checkPoint) {
        isCheckPoint = checkPoint;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public void setNeighbours(ArrayList<Pair> neighbours) {
        this.neighbours = neighbours;
    }

    public ArrayList<Pair> getNeighbours() {
        return neighbours;
    }

    public void setCameFrom(Cell cameFrom) {
        this.cameFrom = cameFrom;
    }

    public Cell getCameFrom() {
        return cameFrom;
    }

    public Pair getIndex() {
        return index;
    }

    public boolean isTarget() {
        return isTarget;
    }

    void setTarget(boolean target) {
        isTarget = target;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isRobotPosition() {
        return robotPosition;
    }

    public void setRobotPosition(boolean robotPosition) {
        this.robotPosition = robotPosition;
    }
}
