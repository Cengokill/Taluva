package Modele.Jeu.Plateau;

import java.awt.*;
import java.io.Serializable;

public class Hexagone implements Serializable {

    public static final byte VOLCAN = 32;
    public static final byte WATER = 16;

    public static final byte VIDE = 0;
    public static final byte GRASS = 1;
    public static final byte MONTAGNE = 2;
    public static final byte DESERT = 3;
    public static final byte FORET = 4;
    public static final byte LAC = 5;

    public static final byte HUTTE = 10;
    public static final byte TEMPLE = 11;
    public static final byte TOUR = 15;
    public static final byte CHOISIR_BATIMENT = 16;

    private final byte hauteur;
    private int IDvillage ;
    private final byte biomeTerrain;
    private byte batiment = 0;
    private final byte numeroTexture;
    private final byte volcan_j;
    private final byte volcan_i;
    private Color couleur_joueur;

    public boolean placable = false;

    public Hexagone(byte hauteur, byte biomeTerrain, byte volcan_i, byte volcan_j) {
        this.couleur_joueur = null;
        this.hauteur = hauteur;
        this.biomeTerrain = biomeTerrain;
        this.numeroTexture = 0;
        this.volcan_i = volcan_i;
        this.volcan_j = volcan_j;
    }

    public Hexagone(Color color, byte hauteur, byte biomeTerrain, byte batiment, byte volcan_i, byte volcan_j) {
        this.couleur_joueur = color;
        this.hauteur = hauteur;
        this.biomeTerrain = biomeTerrain;
        this.batiment = batiment;
        this.numeroTexture = 0;//(byte) ((int)(Math.random() * 2) + 1);
        this.volcan_i = volcan_i;
        this.volcan_j = volcan_j;
    }

    public Hexagone(byte hauteur, byte biomeTerrain, byte volcan_i, byte volcan_j, byte numeroTexture) {
        this.hauteur = hauteur;
        this.biomeTerrain = biomeTerrain;
        this.numeroTexture = numeroTexture;
        this.volcan_i = volcan_i;
        this.volcan_j = volcan_j;
    }

    public Color getColorJoueur(){return couleur_joueur;}

    public byte getHauteur() {
        return hauteur;
    }
    public byte getBiomeTerrain() {
        return biomeTerrain;
    }
    public byte getNum(){return numeroTexture;}

    public int getColonneVolcan(){
        return volcan_j;
    }
    public int getLigneVolcan(){
        return volcan_i;
    }

    public void resetBatHexagone(){
        this.batiment = 0;
    }


    public int getBatiment() {return batiment;}

    public void affiche(){
        if(getHauteur()==1) System.out.print("1");
        if(getHauteur()==2) System.out.print("2");
        if(getHauteur()==3) System.out.print("3");
        switch(biomeTerrain){
            case VOLCAN:
                System.out.print("V");
                break;
            case GRASS:
                System.out.print("G");
                break;
            case MONTAGNE:
                System.out.print("M");
                break;
            case DESERT:
                System.out.print("D");
                break;
            case FORET:
                System.out.print("F");
                break;
            case LAC:
                System.out.print("L");
                break;
            case WATER:
            case VIDE:
            default:
                System.out.print(" ");
                break;
        }
        switch(batiment){
            case HUTTE:
                if(hauteur==0) System.out.print("0");
                else if(hauteur==1) System.out.print("1");
                else if(hauteur==2) System.out.print("2");
                else if(hauteur==3) System.out.print("3");
                else System.out.print("?");
                break;
            case TEMPLE:
                System.out.print("$");
                break;
            case TOUR:
                System.out.print("!");
                break;
            default:
                System.out.print(" ");
                break;
        }
    }


    public Hexagone copy() {
        return new Hexagone(couleur_joueur, hauteur, biomeTerrain, batiment, volcan_i, volcan_j);
    }
}
