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
    public static final byte CHOISIR_BATIMENT = 16;

    private final byte hauteur;
    private int IDvillage ;
    private final byte biomeTerrain;
    private byte batiment = 0;
    private final byte numero ;
    private final byte volcan_j;
    private final byte volcan_i;
    private byte num_joueur;

    public Hexagone(byte hauteur, byte biomeTerrain, byte volcan_i, byte volcan_j) {
        this.num_joueur = -1;
        this.hauteur = hauteur;
        this.biomeTerrain = biomeTerrain;
        this.numero = (byte) ((int)(Math.random() * 2) + 1);
        this.volcan_i = volcan_i;
        this.volcan_j = volcan_j;
    }

    public Hexagone(byte numero_joueur, byte hauteur, byte biomeTerrain, byte batiment, byte volcan_i, byte volcan_j) {
        this.num_joueur = numero_joueur;
        this.hauteur = hauteur;
        this.biomeTerrain = biomeTerrain;
        this.batiment = batiment;
        this.numero = (byte) ((int)(Math.random() * 2) + 1);
        this.volcan_i = volcan_i;
        this.volcan_j = volcan_j;
    }

    public byte getNumJoueur(){return num_joueur;}

    public byte getHauteur() {
        return hauteur;
    }
    public byte getBiomeTerrain() {
        return biomeTerrain;
    }
    public byte getNum(){return numero;}

    public int getColonneVolcan(){
        return volcan_j;
    }
    public int getLigneVolcan(){
        return volcan_i;
    }

    public boolean HexagoneLibre(){
        return biomeTerrain == 0;
    }

    public int getBatiment() {return batiment;}

    public int getIDvillage (){return IDvillage;}

    public Hexagone copy() {
        return new Hexagone(num_joueur, hauteur, biomeTerrain, batiment, volcan_i, volcan_j);
    }
}
