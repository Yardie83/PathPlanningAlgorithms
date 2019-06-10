package ch.fhnw.util;

public class Pair<F, S> {

    public final F i;
    public final S j;

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
