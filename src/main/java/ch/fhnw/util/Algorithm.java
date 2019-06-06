package ch.fhnw.util;

public enum Algorithm {
    DIJKSTRA("Dijkstra"),
    A_STAR("A*"),
    D_STAR_LITE("D*Lite"),
    MPAA ("MPAA");

    private final String name;

    Algorithm(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
