package Modele;

public class Hexagone {

    public static final int VIDE = 0;
    public static final int GRASS = 1;
    public static final int MONTAGNE = 0;
    public static final int VOLCAN = 0;
    public static final int DESERT = 0;
    private int Hauteur;

    private int terrain;
    private int typeTion;
    int numero ;

    public Hexagone(int hauteur, int terrain, int n, int type) {
        this.Hauteur = hauteur;
        this.terrain = terrain;
        this.typeTion = type;
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
