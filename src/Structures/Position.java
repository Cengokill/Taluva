package Structures;

import java.io.Serializable;

public class Position implements Serializable {
    final int ligne;
    final int colonne;

    public Position(int ligne, int colonne) {
        this.ligne = ligne;
        this.colonne = colonne;
    }

    public int ligne() {
        return ligne;
    }

    public int colonne() {
        return colonne;
    }

    public Position copy() {
        return new Position(this.ligne, this.colonne);
    }
}