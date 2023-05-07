package Modele.Jeu;

import java.io.Serializable;

public class Coup implements Serializable {

    public static final byte TUILE = 0;
    public static final byte BATIMENT = 1;

    public final byte typePlacement;
    final byte num_joueur;

    public int volcanX;
    public int volcanY;
    public int tile1X;
    public int tile1Y;
    public byte terrain1;
    public int tile2X;
    public int tile2Y;
    public byte terrain2;
    int batiment_x;
    int batiment_y;
    int IDdeJoueur;
    public int batimentX;
    public int batimentY;

    public Coup(byte num_joueur, int volcanX, int volcanY, int tile1X, int tile1Y, byte terrain1, int tile2X, int tile2Y, byte terrain2) {// placement TUILE
        this.num_joueur = num_joueur;
        this.typePlacement = TUILE;
        this.volcanX = volcanX;
        this.volcanY = volcanY;
        this.tile1X = tile1X;
        this.tile1Y = tile1Y;
        this.terrain1 = terrain1;
        this.tile2X = tile2X;
        this.tile2Y = tile2Y;
        this.terrain2 = terrain2;
        this.IDdeJoueur=-1;

    }
    public Coup(byte num_joueur, int batimentX, int batimentY, byte typeBatiment) {// placement BATIMENT
        this.num_joueur = num_joueur;
        this.typePlacement = typeBatiment;
        this.batimentX = batimentX;
        this.batimentY = batimentY;
    }

    public byte getNumJoueur() {
        return num_joueur;
    }
}
