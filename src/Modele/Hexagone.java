package Modele;

public class Hexagone {
    private int Hauteur;
    private int terrain;
    private int typeTion;
    int numero_tuile ;

    public Hexagone(int hauteur, int terrain,int numero_tuile) {
        this.Hauteur = hauteur;
        this.terrain = terrain;
        this.typeTion = 0;
        this.numero_tuile= numero_tuile;
    }
    public int getHauteur() {
        return Hauteur;
    }
    public int getTerrain() {
        return terrain;
    }
    public int getTypeTion(){return typeTion;}
    public int getNumero_tuile(){return numero_tuile;}
    public boolean HexagoneLibre(){
        if (typeTion == 0) return true;
        else return false;
    }
}
