package Modele;

public class Hexagone {

    public static final byte VOLCAN = 32;
    public static final byte WATER = 16;

    public static final byte VIDE = 0;
    public static final byte GRASS = 1;
    public static final byte MONTAGNE = 2;
    public static final byte DESERT = 3;
    public static final byte FORET = 4;

    public static final byte MAISON = 10;
    public static final byte TEMPLE_FORET = 11;
    public static final byte TEMPLE_SABLE = 12;
    public static final byte TEMPLE_PIERRE = 13;
    public static final byte TEMPLE_PRAIRIE = 14;
    public static final byte TOUR = 15;
    private int Hauteur;

    private int IDvillage ;
    private byte terrain;
    private byte batiment;
    int numero ;

    public Hexagone(int hauteur, byte terrain) {
        this.Hauteur = hauteur;
        this.terrain = terrain;
        this.numero = ((int)(Math.random() * 2) + 1);
        this.IDvillage = 0 ;
    }
    public Hexagone(int hauteur, byte terrain, byte batiment) {
        this.Hauteur = hauteur;
        this.terrain = terrain;
        this.batiment = batiment;
        this.numero = ((int)(Math.random() * 2) + 1);
    }


    public int getHauteur() {
        return Hauteur;
    }
    public byte getTerrain() {
        return terrain;
    }
    public int getNum(){return numero;}
    public boolean HexagoneLibre(){
        if (terrain == 0) return true;
        else return false;
    }

    public int getBatiment() {return batiment;}

    public int getIDvillage (){return IDvillage;}
}
