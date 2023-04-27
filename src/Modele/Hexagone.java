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

    private byte hauteur;
    private int IDvillage ;
    private byte terrain;
    private byte batiment;
    private byte numero ;
    private byte volcan_x;
    private byte volcan_y;
    private byte num_joueur;

    public Hexagone(byte hauteur, byte terrain, byte volcan_x, byte volcan_y) {
        this.num_joueur = -1;
        this.hauteur = hauteur;
        this.terrain = terrain;
        this.numero = (byte) ((int)(Math.random() * 2) + 1);
        this.volcan_x = volcan_x;
        this.volcan_y = volcan_y;
    }

    public Hexagone(byte numero_joueur, byte hauteur, byte terrain, byte batiment, byte volcan_x, byte volcan_y) {
        this.num_joueur = numero_joueur;
        this.hauteur = hauteur;
        this.terrain = terrain;
        this.batiment = batiment;
        this.numero = (byte) ((int)(Math.random() * 2) + 1);
        this.volcan_x = volcan_x;
        this.volcan_y = volcan_y;
    }

    public byte getNumJoueur(){return num_joueur;}

    public byte getHauteur() {
        return hauteur;
    }
    public byte getTerrain() {
        return terrain;
    }
    public byte getNum(){return numero;}

    public int getVolcanX(){
        return volcan_x;
    }
    public int getVolcanY(){
        return volcan_y;
    }

    public boolean HexagoneLibre(){
        if (terrain == 0) return true;
        else return false;
    }

    public int getBatiment() {return batiment;}

    public int getIDvillage (){return IDvillage;}
}
