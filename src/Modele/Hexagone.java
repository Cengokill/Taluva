package Modele;

public class Hexagone {

    public static final int VIDE = 0;
    public static final int GRASS = 1;
    public static final int MONTAGNE = 0;
    public static final int VOLCAN = 3;
    public static final int DESERT = 0;
    public static final int WATER = 5;
    private int Hauteur;

    private int terrain;
    int numero ;

    public Hexagone(int hauteur, int terrain, int n) {
        this.Hauteur = hauteur;
        this.terrain = terrain;
        this.numero = n;
    }
    public int getHauteur() {
        return Hauteur;
    }
    public int getTerrain() {
        return terrain;
    }
    public int getNum(){return numero;}
    public boolean HexagoneLibre(){
        if (terrain == 0) return true;
        else return false;
    }
}
