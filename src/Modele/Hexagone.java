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
    public static final byte TEMPLE = 10;
    public static final byte TOUR = 10;
    private byte Hauteur;

    private byte terrain;
    private byte batiment;
    byte numero ;

    public byte volcan_x;
    public byte volcan_y;

    public Hexagone(byte hauteur, byte terrain, byte volcan_x, byte volcan_y) {
        this.Hauteur = hauteur;
        this.terrain = terrain;
        this.numero = (byte) ((int)(Math.random() * 2) + 1);
        this.volcan_x = volcan_x;
        this.volcan_y = volcan_y;
    }
    public Hexagone(byte hauteur, byte terrain, byte batiment, byte volcan_x, byte volcan_y) {
        this.Hauteur = hauteur;
        this.terrain = terrain;
        this.batiment = batiment;
        this.numero = (byte) ((int)(Math.random() * 2) + 1);
        this.volcan_x = volcan_x;
        this.volcan_y = volcan_y;
    }


    public byte getHauteur() {
        return Hauteur;
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

    public int getBatiment() {
        return batiment;
    }
}
