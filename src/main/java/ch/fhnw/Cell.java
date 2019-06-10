package ch.fhnw;

import ch.fhnw.util.Pair;

import java.util.ArrayList;

public class Cell {

    private boolean isWall = false;
    private boolean isCheckPoint = false;
    private double distance;
    private double cost;
    private ArrayList<Pair> neighbours;
    private Pair cameFrom;
    private Pair index;
    private boolean isStart;
    private boolean isVisited;
    private boolean robotPosition;
    private double g_score;
    private double h_score;
    private double f_score;

    public Cell(Pair index) {
        this.index = index;
        this.isVisited = false;
    }

    public boolean isStart() {
        return isStart;
    }

    void setStart(boolean start) {
        isStart = start;
    }

    public boolean isWall() {
        return isWall;
    }

    void setWall(boolean wall) {
        isWall = wall;
    }

    public boolean isCheckPoint() {
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

    public void setCameFrom(Pair cameFrom) {
        this.cameFrom = cameFrom;
    }

    public Pair getCameFrom() {
        return cameFrom;
    }

    public Pair getIndex() {
        return index;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visitied) {
        this.isVisited = visitied;
    }

    public boolean isRobotPosition() {
        return robotPosition;
    }

    public void setRobotPosition(boolean robotPosition) {
        this.robotPosition = robotPosition;
    }

    public double getG_score() {
        return g_score;
    }

    public void setG_score(double g_score) {
        this.g_score = g_score;
    }

    public double getH_score() {
        return h_score;
    }

    public void setH_score(double h_score) {
        this.h_score = h_score;
    }

    public double getF_score() {
        return f_score;
    }

    public void setF_score(double f_score) {
        this.f_score = f_score;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
