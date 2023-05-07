package Modele.Jeu;

import java.io.Serializable;

public class Coup implements Serializable {

    public static final byte TUILE = 0;
    public static final byte BATIMENT = 1;

    public final byte typePlacement;
    final byte num_joueur;

    public int volcanLigne;
    public int volcanColonne;
    public int tile1Ligne;
    public int tile1Colonne;
    public byte biome1;
    public int tile2Ligne;
    public int tile2Colonne;
    public byte biome2;
    int batiment_x;
    int batiment_y;
    int IDdeJoueur;
    public int batimentLigne;
    public int batimentColonne;

    public Coup(byte num_joueur, int volcanLigne, int volcanColonne, int tile1Ligne, int tile1Colonne, byte biome1, int tile2Ligne, int tile2Colonne, byte biome2) {// placement TUILE
        this.num_joueur = num_joueur;
        this.typePlacement = TUILE;
        this.volcanLigne = volcanLigne;
        this.volcanColonne = volcanColonne;
        this.tile1Ligne = tile1Ligne;
        this.tile1Colonne = tile1Colonne;
        this.biome1 = biome1;
        this.tile2Ligne = tile2Ligne;
        this.tile2Colonne = tile2Colonne;
        this.biome2 = biome2;
        this.IDdeJoueur=-1;

    }
    public Coup(byte num_joueur, int batimentLigne, int batimentColonne, byte typeBatiment) {// placement BATIMENT
        this.num_joueur = num_joueur;
        this.typePlacement = typeBatiment;
        this.batimentLigne = batimentLigne;
        this.batimentColonne = batimentColonne;
    }

    public byte getNumJoueur() {
        return num_joueur;
    }
}