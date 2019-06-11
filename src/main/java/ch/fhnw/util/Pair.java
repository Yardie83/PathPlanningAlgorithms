package ch.fhnw.util;

public class Pair<F, S> {

    public F i;
    public S j;

    public Pair(F i, S j) {
        this.i = i;
        this.j = j;
    }

    public boolean lessThan(Pair other) {
        if ((double) this.i < (double) other.i) return true;
        else if ((double) this.i > (double) other.i) return true;
        return (double) this.j < (double) other.j;
    }
}
