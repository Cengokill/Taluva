package Modele.IA;

import Modele.Jeu.Coup;
import Structures.Position.Position;
import Structures.Position.TripletDePosition;

import java.util.ArrayList;
import java.util.Random;

import static Modele.Jeu.Plateau.EtatPlateau.scrollValue;

class IAAleatoire extends AbstractIA {

    public IAAleatoire() {
        super();
    }

    private int ajusterColonne(int ligne, int colonne){
        int j_modified;
        if (ligne % 2 == 1) {
            j_modified = colonne - 1;
        } else {
            j_modified = colonne;
        }
        return j_modified;
    }
    public Coup joue() {

        Random r = new Random();
        int ligne=0, colonne=0;
        int taille_x = jeu.getPlateau().getCarte().length;
        int taille_y = jeu.getPlateau().getCarte()[0].length;
        byte numIA = jeu.getNumJoueurCourant();

        if(jeu.doit_placer_tuile()){

            byte[] tuiles = jeu.getTuilesAPoser();
            byte[][] triplet = new byte[3][2];
            triplet[1][0] = tuiles[0]; // tile 1
            triplet[2][0] = tuiles[1]; // tile 2
            triplet[0][0] = tuiles[2];
            if(jeu.getPlateau().estVide()){ // l'AbstractIA est le premier � jouer donc on place au centres
                ligne = taille_x/2;
                colonne = taille_y/2;

                int colonne_ajustee = ajusterColonne(ligne,colonne);

                // controleur.placeEtage(ligne, colonne, ligne - 1, colonne_ajustee, triplet[1][0], ligne - 1, colonne_ajustee + 1, triplet[2][0]);
                return new Coup(numIA,ligne,colonne,ligne-1,colonne_ajustee,triplet[1][0],ligne-1,colonne_ajustee+1,triplet[2][0]);
            }else{
                // Trouver un emplacement pour les hexagones
                ArrayList<TripletDePosition> positionsPossible = jeu.getPlateau().getTripletsPossibles();
                TripletDePosition positionsrandom = positionsPossible.get(r.nextInt(positionsPossible.size()));
                boolean bon = jeu.getPlateau().estHexagoneVide(positionsrandom.getVolcan().ligne(),positionsrandom.getVolcan().colonne())&&jeu.getPlateau().estHexagoneVide(positionsrandom.getTile1().ligne(),positionsrandom.getTile1().colonne())&&jeu.getPlateau().estHexagoneVide(positionsrandom.getTile2().ligne(),positionsrandom.getTile2().colonne());
                while(!bon){
                    jeu.getPlateau().supprimeTriplets(positionsrandom);
                    positionsrandom = positionsPossible.get(r.nextInt(positionsPossible.size()));
                    bon = jeu.getPlateau().estHexagoneVide(positionsrandom.getVolcan().ligne(),positionsrandom.getVolcan().colonne())&&jeu.getPlateau().estHexagoneVide(positionsrandom.getTile1().ligne(),positionsrandom.getTile1().colonne())&&jeu.getPlateau().estHexagoneVide(positionsrandom.getTile2().ligne(),positionsrandom.getTile2().colonne());
                }

                int ligne1 = positionsrandom.getVolcan().ligne();
                int ligne2 = positionsrandom.getTile1().ligne();
                int ligne3 = positionsrandom.getTile2().ligne();

                int colonne1 = positionsrandom.getVolcan().colonne();
                int colonne2 = positionsrandom.getTile1().colonne();
                int colonne3 = positionsrandom.getTile2().colonne();

                return new Coup(numIA,ligne1,colonne1,ligne2,colonne2,triplet[1][0],ligne3,colonne3,triplet[2][0]);
            }
        }
        else if(jeu.doit_placer_batiment()){
            // Trouver un empla�ement pour le batiment
            ArrayList<Position> positionPossibles = jeu.getPlateau().getPositions_libres_batiments();
            //System.out.println("ICI DEFOIS MANGE LA MAISON DE L'ADVERSSAIRE A DEBUGGER");
            Position positionrandom = positionPossibles.get(r.nextInt(positionPossibles.size()));
            while(jeu.getPlateau().getBatiment(positionrandom.ligne(),positionrandom.colonne())!=0){
                jeu.getPlateau().supprimePosBatiment(positionrandom);
                positionrandom = positionPossibles.get(r.nextInt(positionPossibles.size()));
            }

            jeu.getPlateau().supprimeLibreBatiments(positionrandom);


            // Choisir un batiment � placer
            int batiment = -1;
            int[] batimensPlacable = coupJouableIA(positionrandom.ligne(),positionrandom.colonne());
            int randomInt = r.nextInt(3);
            //System.out.println("batimentPlacable hutte: "+);

            if (batimensPlacable[0] == 0 && batimensPlacable[2] == 0) batiment = 1; // si on ne peut pas placer de temple ni de tour
            else if(batimensPlacable[1]==0){       // On ne peut pas placer de hutte
                if(batimensPlacable[0]==0) batiment=2;
                else if(batimensPlacable[2]==0) batiment=0;
                else{
                    batiment = randomInt % 2;
                    if(batiment==1) batiment=2;
                }
            }
            else if (batimensPlacable[0] == 0) {   // on ne peut pas placer de temple
                batiment = randomInt % 2;
                if (batiment == 0) batiment = 2;
            } else if (batimensPlacable[2] == 0) { // On ne peut pas placer de tour
                batiment = randomInt % 2;
                if (batiment == 1) batiment = 0;
                else if (batiment == 0) batiment = 1;
            }

            return new Coup(numIA,positionrandom.ligne(),positionrandom.colonne(),(byte) batiment);
        }


        //return new Coup(jeu.getNumJoueurCourant(),)
        return null;
    }

    public int[] coupJouableIA(int i,int j){
        int[] coups = jeu.getPlateau().getBatimentPlacable(i,j, jeu.getNumJoueurCourant());

        int hauteurTuile = jeu.getPlateau().getHauteurTuile(i,j);

        if(jeu.getJoueurCourantClasse().getNbTemples()<=0) coups[0] = 0;
        if(jeu.getJoueurCourantClasse().getNbTours()<=0) coups[2] = 0;
        if(jeu.getJoueurCourantClasse().getNbHuttes()<hauteurTuile) coups[1] = 0;

        return coups;
    }

}
