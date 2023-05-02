package Modele;

import java.io.Serializable;

public class Coup implements Serializable {

    public static final byte TUILE = 0;
    public static final byte BATIMENT = 1;

    final byte typePlacement;
    final byte num_joueur;

    int volcanX;
    int volcanY;
    int tile1X;
    int tile1Y;
    byte terrain1;
    int tile2X;
    int tile2Y;
    byte terrain2;
    int batiment_x;
    int batiment_y;
    int IDdeJoueur;
    int batimentX;
    int batimentY;

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
