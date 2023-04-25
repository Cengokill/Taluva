package Modele;

public class Hexagone {
    private int Hauteur;
    private int terrain;
    private int typeTion;
    int numero ;

    public Hexagone(int hauteur, int terrain, int n) {
        this.Hauteur = hauteur;
        this.terrain = terrain;
        this.typeTion = 0;
        this.numero = n;
    }
    public int getHauteur() {
        return Hauteur;
    }
    public int getTerrain() {
        return terrain;
    }
    public int getTypeTion(){return typeTion;}
    public int getNum(){return numero;}
    public boolean HexagoneLibre(){
        if (typeTion == 0) return true;
        else return false;
    }
}
