package Modele;

import java.util.Random;
import java.util.concurrent.TimeUnit;

class IAAleatoire extends IA {

    public IAAleatoire() {
    }
    // Coup(byte num_joueur, int volcan_x, int volcan_y, int tile1_x, int tile1_y, byte terrain1, int tile2_x, int tile2_y, byte terrain2)
    public Coup joue() {

        Random r = new Random();
        int i=0, j=0;
        int taille_x = jeu.getPlateau().plateau.length;
        int taille_y = jeu.getPlateau().plateau[0].length;
        byte numIA = jeu.getNumJoueurCourant();
        System.out.println("isEmpty: "+jeu.getPlateau().isEmpty());
        if(jeu.getPlateau().isEmpty()){ // l'IA est le premier à jouer donc on place au centres
            i = taille_x/2;
            j = taille_y/2;

            int j_modified;
            if (i % 2 == 1) {
                j_modified = j - 1;
            } else {
                j_modified = j + 1;
            }

            byte[] tuiles = jeu.getTuilesAPoser();
            byte[][] triplet = new byte[3][2];
            triplet[1][0] = tuiles[0]; // tile 1
            triplet[2][0] = tuiles[1]; // tile 2
            triplet[0][0] = tuiles[2];

            // controleur.placeEtage(i, j, i - 1, j_modified, triplet[1][0], i - 1, j_modified + 1, triplet[2][0]);
            return new Coup(numIA,i,j,i-1,j_modified,triplet[1][0],i-1,j_modified,triplet[2][0]);
        }else{

        }


        /*boolean estJouable = false;
        int i = 0, j = 0;
        while (!estJouable) {
            Random r = new Random();

            i = r.nextInt(this.jeu.gaufre().lignes());
            j = r.nextInt(this.jeu.gaufre().colonnes());

            if (!this.jeu.gaufre().estMangee(i, j) && !(i == 0 && j == 0)) {
                estJouable = true;
            }
        }

        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/

        //return new Coup(jeu.getNumJoueurCourant(),)
        return null;
    }

}
