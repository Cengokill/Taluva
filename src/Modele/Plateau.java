package Modele;

import java.awt.*;
import java.util.ArrayList;

public class Plateau {
    protected Hexagone[][] plateau ;
    protected int[] nbPionsJ1;
    protected int[] nbPionsJ2;
    private Historique historique;

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
                plateau[i][j] = new Hexagone((byte) 0, Hexagone.VIDE, (byte)0, (byte)0);
            }
        }

        plateau[18][19] = new Hexagone((byte) 1, Hexagone.GRASS, (byte) 20, (byte)19);
        plateau[19][20] = new Hexagone((byte)1, Hexagone.VOLCAN, (byte)20, (byte)19);
        plateau[18][20] = new Hexagone((byte)1, Hexagone.GRASS, (byte)20, (byte)19);

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

    public boolean peutPlacerTuile(int volcan_x, int volcan_y, int tile1_x, int tile1_y, int tile2_x, int tile2_y) {

        int hauteur = plateau[volcan_x][volcan_y].getHauteur();

        // Hauteur max
        if (hauteur == 3) {
            System.out.println("a");
            return false;
        }
        // V�rifie si on place un volcan sur un volcan
        if (plateau[volcan_x][volcan_y].getTerrain() != Hexagone.VOLCAN && plateau[volcan_x][volcan_y].getTerrain() != Hexagone.VIDE) {
            System.out.println("b");

            return false;
        }

        // V�rifie la hauteur de toutes les cases
        if (plateau[volcan_x][volcan_y].getHauteur() != hauteur) {
            System.out.println("c");

            return false;
        }
        if (plateau[tile1_x][tile1_y].getHauteur() != hauteur) {
            System.out.println("d");

            return false;
        }
        if (plateau[tile2_x][tile2_y].getHauteur() != hauteur) {
            System.out.println("e");

            return false;
        }

        if (plateau[volcan_x][volcan_y].getTerrain() == Hexagone.VIDE && plateau[tile1_x][tile1_y].getTerrain() == Hexagone.VIDE && plateau[tile2_x][tile2_y].getTerrain() == Hexagone.VIDE) {
            if (volcan_y % 2 == 1) {
                volcan_x = volcan_x + 1;
            } else {
                volcan_x = volcan_x;
            }
            if (tile1_y % 2 == 1) {
                tile1_x = tile1_x + 1;
            } else {
                tile1_x = tile1_x;
            }
            if (tile2_y % 2 == 1) {
                tile2_x = tile2_x + 1;
            } else {
                tile2_x = tile2_x;
            }
            if (!(
                    // Volcan proche d'une ile
                    plateau[volcan_x + 1][volcan_y].getTerrain() != Hexagone.VIDE ||
                    plateau[volcan_x - 1][volcan_y].getTerrain() != Hexagone.VIDE ||

                    plateau[volcan_x][volcan_y + 1].getTerrain() != Hexagone.VIDE ||
                    plateau[volcan_x][volcan_y - 1].getTerrain() != Hexagone.VIDE ||

                    plateau[volcan_x - 1][volcan_y + 1].getTerrain() != Hexagone.VIDE ||
                    plateau[volcan_x - 1][volcan_y - 1].getTerrain() != Hexagone.VIDE ||

                    // Tile 1 proche d'une ile
                    plateau[tile1_x + 1][tile1_y].getTerrain() != Hexagone.VIDE ||
                    plateau[tile1_x - 1][tile1_y].getTerrain() != Hexagone.VIDE ||

                    plateau[tile1_x][tile1_y + 1].getTerrain() != Hexagone.VIDE ||
                    plateau[tile1_x][tile1_y - 1].getTerrain() != Hexagone.VIDE ||

                    plateau[tile1_x - 1][tile1_y + 1].getTerrain() != Hexagone.VIDE ||
                    plateau[tile1_x - 1][tile1_y - 1].getTerrain() != Hexagone.VIDE ||

                    // Tile 2 proche d'une ile
                    plateau[tile2_x + 1][tile2_y].getTerrain() != Hexagone.VIDE ||
                    plateau[tile2_x - 1][tile2_y].getTerrain() != Hexagone.VIDE ||

                    plateau[tile2_x][tile2_y + 1].getTerrain() != Hexagone.VIDE ||
                    plateau[tile2_x][tile2_y - 1].getTerrain() != Hexagone.VIDE ||

                    plateau[tile2_x - 1][tile2_y + 1].getTerrain() != Hexagone.VIDE ||
                    plateau[tile2_x - 1][tile2_y - 1].getTerrain() != Hexagone.VIDE
            )) {
                System.out.println("f");
                return false;
            }
        }

        return true;
    }

    public void joueCoup(Coup coup) {
        int hauteur = plateau[coup.volcan_x][coup.volcan_y].getHauteur();
        if (coup.type == Coup.TUILE) {
            plateau[coup.volcan_x][coup.volcan_y] = new Hexagone((byte) (hauteur + 1), Hexagone.VOLCAN, (byte)coup.volcan_x, (byte)coup.volcan_y);
            plateau[coup.tile1_x][coup.tile1_y] = new Hexagone((byte) (hauteur + 1), coup.terrain1, (byte)coup.volcan_x, (byte)coup.volcan_y);
            plateau[coup.tile2_x][coup.tile2_y] = new Hexagone((byte) (hauteur + 1), coup.terrain2, (byte)coup.volcan_x, (byte)coup.volcan_y);

        } else if (coup.type == Coup.BATIMENT){
            hauteur = plateau[coup.batiment_x][coup.batiment_y].getHauteur();
            byte batiment = 1;
            if (hauteur == 1) {
                batiment = Hexagone.MAISON;
            } else if (hauteur == 2) {
                batiment = Hexagone.MAISON;
            } else if (hauteur == 3) {
                batiment = Hexagone.MAISON;
            }

            plateau[coup.batiment_x][coup.batiment_y] = new Hexagone((byte) hauteur, plateau[coup.batiment_x][coup.batiment_y].getTerrain(), batiment, (byte)coup.volcan_x, (byte)coup.volcan_y);
        }
    }


    // N�cessite un appel � peutPlacerEtage
    public void placeEtage(int volcan_x, int volcan_y, int tile1_x, int tile1_y, byte terrain1, int tile2_x, int tile2_y, byte terrain2) {
        Coup coup = new Coup(volcan_x, volcan_y, tile1_x, tile1_y, terrain1, tile2_x, tile2_y, terrain2);
        historique.ajoute(coup);
        joueCoup(coup);
    }

    public boolean peutPlacerMaison(int i,int j){
        return plateau[i][j].getTerrain()!=Hexagone.VOLCAN && plateau[i][j].getBatiment()==Hexagone.VIDE;
    }
    public void placeMaison(int i,int j){
        Coup coup = new Coup(i,j);
        historique.ajoute(coup);
        joueCoup(coup);
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
