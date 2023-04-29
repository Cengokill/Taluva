package Modele;

public class Position {
    int l;
    int c;

    public Position(int l, int c) {
        this.l = l;
        this.c = c;
    }

    public int getL() {
        return l;
    }

    public int getC() {
        return c;
    }

    public Position copy() {
        Position position = new Position(this.l, this.c);
        return position;
    }
}
