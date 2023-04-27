package Modele;

import org.w3c.dom.ls.LSOutput;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Plateau {
    protected Hexagone[][] plateau ;
    protected int[] nbPionsJ1;
    protected int[] nbPionsJ2;
    private Historique historique;

    byte jCourant;

    public Plateau(){

        plateau = new Hexagone [40][40];
        historique = new Historique();
        nbPionsJ1 = new int [3];
        nbPionsJ2 = new int [3];
        nbPionsJ1[0]=10 ; nbPionsJ2[0]=10;
        nbPionsJ1[1]=10 ; nbPionsJ2[1]=10;
        nbPionsJ1[2]=10 ; nbPionsJ2[2]=10;
        initPlateau();
    }

    private void initPlateau() {
        for (int i = 0; i < plateau.length; i++) {
            for (int j = 0; j < plateau[0].length; j++) {
                plateau[i][j] = new Hexagone((byte)0, Hexagone.VIDE, (byte)19, (byte)20);
            }
        }
        //plateau[18][19] = new Hexagone((byte) 1, Hexagone.GRASS, (byte) 20, (byte)19);
        plateau[19][20] = new Hexagone((byte)1, Hexagone.VOLCAN, (byte)19, (byte)20);
        //plateau[18][20] = new Hexagone((byte)1, Hexagone.GRASS, (byte)19, (byte)20);

    }

    public Hexagone[][] getPlateau() {
        return plateau;
    }

    // check si la condition de victoire du nb de pi�ces est bonne
    public boolean fini1(int joueur){
        int nb_pion_vite_J1 = 0;
        for (int j = 0; j<3;j++){
            if(nbPionsJ1[j]==0 && joueur==1)nb_pion_vite_J1++;
            if(nbPionsJ2[j]==0 && joueur==2)nb_pion_vite_J1++;
        }
        if(nb_pion_vite_J1>=2){return true;}
        return false;
    }
    public boolean tousLesMeme(int x1 , int y1 , int x2 ,int y2 ,int x3 ,int y3){
        if(plateau[x1][y1].getHauteur()==plateau[x2][y2].getHauteur() &&plateau[x1][y1].getHauteur()==plateau[x3][y3].getHauteur())
            return true;
        return false;
    }

    public boolean estPlaceLibre(int x1, int y1){
        if (plateau[x1][y1].getTerrain()<=1)
            return true;
        return false;
    }

    public boolean peutPlacerTuile(int volcan_i, int volcan_j, int tile1_i, int tile1_j, int tile2_i, int tile2_j) {

        /*
        System.out.println("================================");
        System.out.println("volcan_y: " + volcan_y);
        System.out.println("tile1_y: " + tile1_y);
        System.out.println("tile2_y: " + tile2_y);
        System.out.println();
        System.out.println("volcan_x: " + volcan_x);
        System.out.println("tile1_x: " + tile1_x);
        System.out.println("tile2_x: " + tile2_x);
         */

        int hauteur = plateau[volcan_i][volcan_j].getHauteur();
        if (plateau[tile1_i][tile1_j].getVolcanJ() == volcan_j && plateau[tile2_i][tile2_j].getVolcanI() == volcan_i) {
            return false;
        }


        // Hauteur max
        if (hauteur == 3) {
            return false;
        }
        // V�rifie si on place un volcan sur un volcan
        if (plateau[volcan_i][volcan_j].getTerrain() != Hexagone.VOLCAN && plateau[volcan_i][volcan_j].getTerrain() != Hexagone.VIDE) {
            return false;
        }


        // V�rifie la hauteur de toutes les cases
        if (plateau[volcan_i][volcan_j].getHauteur() != hauteur) {
            return false;
        }
        if (plateau[tile1_i][tile1_j].getHauteur() != hauteur) {
            return false;
        }
        if (plateau[tile2_i][tile2_j].getHauteur() != hauteur) {
            return false;
        }


        if (plateau[volcan_i][volcan_j].getTerrain() == Hexagone.VIDE && plateau[tile1_i][tile1_j].getTerrain() == Hexagone.VIDE && plateau[tile2_i][tile2_j].getTerrain() == Hexagone.VIDE) {


            if (!(
                    // Gauche droite
                    plateau[volcan_i][volcan_j - 1].getTerrain() != Hexagone.VIDE ||
                    plateau[volcan_i][volcan_j + 1].getTerrain() != Hexagone.VIDE ||
                    plateau[tile1_i][tile1_j - 1].getTerrain() != Hexagone.VIDE ||
                    plateau[tile1_i][tile1_j + 1].getTerrain() != Hexagone.VIDE ||
                    plateau[tile2_i][tile2_j - 1].getTerrain() != Hexagone.VIDE ||
                    plateau[tile2_i][tile2_j + 1].getTerrain() != Hexagone.VIDE)) {

                if (volcan_i % 2 == 1) {
                    volcan_j -= 1;
                }
                if (tile1_i % 2 == 1) {
                    tile1_j -= 1;
                }
                if (tile2_i % 2 == 1) {
                    tile2_j -= 1;
                }

                if (!(
                    plateau[volcan_i - 1][volcan_j + 1].getTerrain() != Hexagone.VIDE ||
                    plateau[volcan_i - 1][volcan_j].getTerrain() != Hexagone.VIDE ||
                    plateau[volcan_i + 1][volcan_j + 1].getTerrain() != Hexagone.VIDE ||
                    plateau[volcan_i + 1][volcan_j].getTerrain() != Hexagone.VIDE ||

                    plateau[tile1_i - 1][tile1_j + 1].getTerrain() != Hexagone.VIDE ||
                    plateau[tile1_i - 1][tile1_j].getTerrain() != Hexagone.VIDE ||
                    plateau[tile1_i + 1][tile1_j + 1].getTerrain() != Hexagone.VIDE ||
                    plateau[tile1_i + 1][tile1_j].getTerrain() != Hexagone.VIDE ||

                    plateau[tile2_i - 1][tile2_j + 1].getTerrain() != Hexagone.VIDE ||
                    plateau[tile2_i - 1][tile2_j].getTerrain() != Hexagone.VIDE ||
                    plateau[tile2_i + 1][tile2_j + 1].getTerrain() != Hexagone.VIDE ||
                    plateau[tile2_i + 1][tile2_j].getTerrain() != Hexagone.VIDE
                )) {
                    return false;
                }
            }
        }

        return true;
    }
    public boolean peutPlacerVillage(int x ,int y){
        if(plateau[x][y].getTerrain()!=Hexagone.VOLCAN ||plateau[x][y].getBatiment()==Hexagone.VIDE){
            return true;
        }
        return false;

    }
    public boolean VillageQuestionMarK(int x , int y){
        if (plateau[x][y].getBatiment()==Hexagone.MAISON){
            return true;
        }else{
            return false;
        }

    }
    public ArrayList<Integer> propagation (int x, int y ){
        ArrayList<Integer> listeDecases = new ArrayList<>();
        int IDvillage = plateau[x][y].getIDvillage();
        byte TypeTerrain = plateau[x][y].getTerrain();
        for (int i = 0; i<plateau.length;i++){
            for (int j = 0; j<plateau[0].length;j++){
                if(plateau[i][j].getIDvillage()==IDvillage || (i!=x && y!=j)){// pense a quand place village regarder si tour ou temple
                    if(plateau[i][j-1].getTerrain()== TypeTerrain) {
                        listeDecases.add(i);
                        listeDecases.add(j-1);
                    }
                    if(plateau[i-1][j].getTerrain()== TypeTerrain) {
                        listeDecases.add(i-1);
                        listeDecases.add(j);
                    }
                    if(plateau[i][j+1].getTerrain()== TypeTerrain) {
                        listeDecases.add(i);
                        listeDecases.add(j+1);
                    }
                    if(plateau[i+1][j].getTerrain()== TypeTerrain) {
                        listeDecases.add(i+1);
                        listeDecases.add(j);
                    }
                    if(i%2==1){
                        if(plateau[i-1][j+1].getTerrain()== TypeTerrain) {
                            listeDecases.add(i-1);
                            listeDecases.add(j+1);
                        }
                        if(plateau[i+1][j+1].getTerrain()== TypeTerrain) {
                            listeDecases.add(i+1);
                            listeDecases.add(j+1);
                        }
                    }else{
                        if(plateau[i-1][j-1].getTerrain()== TypeTerrain) {
                            listeDecases.add(i-1);
                            listeDecases.add(j-1);
                        }
                        if(plateau[i+1][j-1].getTerrain()== TypeTerrain) {
                            listeDecases.add(i+1);
                            listeDecases.add(j-1);
                        }
                    }
                }
            }
        }
        return listeDecases;
    }
    public boolean estDansTableau(int x , int y){return x>-1 || x<31 || y>-1 || y<31  ;}
    public void joueCoup(Coup coup) {
        byte num_joueur = coup.getNumJoueur();
        int hauteur = plateau[coup.volcan_x][coup.volcan_y].getHauteur();
        if (coup.type == Coup.TUILE) {
            plateau[coup.volcan_x][coup.volcan_y] = new Hexagone((byte) (hauteur + 1), Hexagone.VOLCAN, (byte)coup.volcan_x, (byte)coup.volcan_y);
            plateau[coup.tile1_x][coup.tile1_y] = new Hexagone((byte) (hauteur + 1), coup.terrain1, (byte)coup.volcan_x, (byte)coup.volcan_y);
            plateau[coup.tile2_x][coup.tile2_y] = new Hexagone((byte) (hauteur + 1), coup.terrain2, (byte)coup.volcan_x, (byte)coup.volcan_y);

        } else if (coup.type == Coup.BATIMENT || coup.type == 2 || coup.type == 3){
            hauteur = plateau[coup.batiment_x][coup.batiment_y].getHauteur();
            byte batiment = 0;
            if (hauteur == 1) {
                batiment = Hexagone.MAISON;
            } else if (hauteur == 2) {
                if(plateau[coup.batiment_x][coup.batiment_y].getTerrain() == Hexagone.FORET) batiment = Hexagone.TEMPLE_FORET;
                if(plateau[coup.batiment_x][coup.batiment_y].getTerrain() == Hexagone.GRASS) batiment = Hexagone.TEMPLE_PRAIRIE;
                if(plateau[coup.batiment_x][coup.batiment_y].getTerrain() == Hexagone.MONTAGNE) batiment = Hexagone.TEMPLE_PIERRE;
                if(plateau[coup.batiment_x][coup.batiment_y].getTerrain() == Hexagone.DESERT) batiment = Hexagone.TEMPLE_SABLE;
            } else if (hauteur == 3) {
                batiment = Hexagone.TOUR;
            }
            plateau[coup.batiment_x][coup.batiment_y] = new Hexagone(num_joueur, (byte) hauteur, plateau[coup.batiment_x][coup.batiment_y].getTerrain(), batiment, (byte)plateau[coup.batiment_x][coup.batiment_y].getVolcanI(), (byte)plateau[coup.batiment_x][coup.batiment_y].getVolcanJ());
        }
    }


    // N�cessite un appel � peutPlacerEtage
    public void placeEtage(byte joueurCourant, int volcan_x, int volcan_y, int tile1_x, int tile1_y, byte terrain1, int tile2_x, int tile2_y, byte terrain2) {
        Coup coup = new Coup(joueurCourant, volcan_x, volcan_y, tile1_x, tile1_y, terrain1, tile2_x, tile2_y, terrain2);
        historique.ajoute(coup);
        joueCoup(coup);
    }

    public boolean peutPlacerMaison(int i,int j){
        return plateau[i][j].getTerrain()!=Hexagone.VOLCAN && plateau[i][j].getBatiment()==Hexagone.VIDE;
    }
    public void placeMaison(byte joueurCourant, int i,int j, byte type){
        Coup coup = new Coup(joueurCourant, i,j,type);
        historique.ajoute(coup);
        joueCoup(coup);
    }

    public int getHauteurTuile(int i,int j){
        return plateau[i][j].getHauteur();
    }

    public Hexagone getTuile(int i, int j){
        return plateau[i][j];
    }
    public void joueHexagone(int x, int y){}

    public void resetHistorique(){
        historique = new Historique();
    }

    public boolean peutAnnuler() {
        return historique.peutAnnuler();
    }

    public boolean peutRefaire() {
        return historique.peutRefaire();
    }

    public boolean annuler() {
        if (peutAnnuler()) {
            return true;
        }
        return false;
    }

    public boolean refaire() {
        if (peutRefaire()) {
            return true;
        }
        return false;
    }
}
