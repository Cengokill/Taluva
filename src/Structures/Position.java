package Structures;

public record Position(int ligne, int colonne) {

    public Position copy() {
        Position position = new Position(this.ligne, this.colonne);
        return position;
    }
}
