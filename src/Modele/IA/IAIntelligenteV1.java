package Modele.IA;

import Modele.Jeu.Coup;
import Modele.Jeu.CoupValeur;
import Structures.Position.Position;
import Structures.Position.TripletDePosition;

import java.util.ArrayList;
import java.util.Random;

class IAIntelligenteV1 extends AbstractIA {

    public IAIntelligenteV1() {
    }

    private int ajusterColonne(int ligne, int colonne){
        int colonneAjustee;
        if (ligne % 2 == 1) {
            colonneAjustee = colonne - 1;
        } else {
            colonneAjustee = colonne;
        }
        return colonneAjustee;
    }
    public CoupValeur joue() {

        Random r = new Random();
        int ligne, colonne;
        int taille_ligne = jeu.getPlateau().getCarte().length;
        int taille_colonne = jeu.getPlateau().getCarte()[0].length;

        byte numIA = jeu.getNumJoueurCourant();

        if(jeu.doit_placer_tuile()){

            byte[] tuiles = jeu.getTuilesAPoser();
            byte[][] triplet = new byte[3][2];
            triplet[1][0] = tuiles[0]; // tile 1
            triplet[2][0] = tuiles[1]; // tile 2
            triplet[0][0] = tuiles[2];
            if(jeu.getPlateau().estVide()){ // l'AbstractIA est le premier à jouer donc on place au centres
                ligne = taille_ligne/2;
                colonne = taille_colonne/2;

                int colonneAjustee = ajusterColonne(ligne,colonne);

                // controleur.placeEtage(ligne, colonne, ligne - 1, colonneAjustee, triplet[1][0], ligne - 1, colonneAjustee + 1, triplet[2][0]);
                //return new Coup(numIA,ligne,colonne,ligne-1,colonneAjustee,triplet[1][0],ligne-1,colonneAjustee+1,triplet[2][0]);
                return null;
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

                int x1 = positionsrandom.getVolcan().ligne();
                int x2 = positionsrandom.getTile1().ligne();
                int x3 = positionsrandom.getTile2().ligne();

                int y1 = positionsrandom.getVolcan().colonne();
                int y2 = positionsrandom.getTile1().colonne();
                int y3 = positionsrandom.getTile2().colonne();

                //return new Coup(numIA,x1,y1,x2,y2,triplet[1][0],x3,y3,triplet[2][0]);
                return null;
            }
        }
        else if(jeu.doit_placer_batiment()){
            // Trouver un emplaçement pour le batiment
            ArrayList<Position> positionPossibles = jeu.getPlateau().getPositions_libres_batiments();
            //System.out.println("ICI DEFOIS MANGE LA MAISON DE L'ADVERSSAIRE A DEBUGGER");
            Position positionrandom = positionPossibles.get(r.nextInt(positionPossibles.size()));
            while(jeu.getPlateau().getBatiment(positionrandom.ligne(),positionrandom.colonne())!=0){
                jeu.getPlateau().supprimePosBatiment(positionrandom);
                positionrandom = positionPossibles.get(r.nextInt(positionPossibles.size()));
            }

            jeu.getPlateau().supprimeLibreBatiments(positionrandom);


            // Choisir un batiment à placer
            byte batiment;
            int[] batimensPlacable = jeu.getPlateau().getBatimentPlacable(positionrandom.ligne(),positionrandom.colonne(),numIA);
            if(batimensPlacable[1]==0&&batimensPlacable[2]==0) batiment=1;
            else if(batimensPlacable[1]==0){
                int value = r.nextInt(2);
                if(value==0) batiment=1;
                else batiment = 3;
            }else{
                int value = r.nextInt(2);
                if(value==0) batiment=1;
                else batiment = 2;
            }

            //return new Coup(numIA,positionrandom.ligne(),positionrandom.colonne(),batiment);
        }


        //return new Coup(jeu.getNumJoueurCourant(),)
        return null;
    }

}
