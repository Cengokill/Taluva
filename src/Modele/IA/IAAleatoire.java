package Modele.IA;

import Modele.Coup;
import Modele.IA.AbstractIA;
import Structures.Position;
import Structures.TripletDePosition;

import java.util.ArrayList;
import java.util.Random;

class IAAleatoire extends AbstractIA {

    public IAAleatoire() {
    }

    private int modified(int i,int j){
        int j_modified;
        if (i % 2 == 1) {
            j_modified = j - 1;
        } else {
            j_modified = j;
        }
        return j_modified;
    }
    public Coup joue() {

        Random r = new Random();
        int i=0, j=0;
        int taille_x = jeu.getPlateau().plateau.length;
        int taille_y = jeu.getPlateau().plateau[0].length;
        byte numIA = jeu.getNumJoueurCourant();

        if(jeu.doit_placer_tuile()){

            byte[] tuiles = jeu.getTuilesAPoser();
            byte[][] triplet = new byte[3][2];
            triplet[1][0] = tuiles[0]; // tile 1
            triplet[2][0] = tuiles[1]; // tile 2
            triplet[0][0] = tuiles[2];
            if(jeu.getPlateau().estVide()){ // l'AbstractIA est le premier � jouer donc on place au centres
                i = taille_x/2;
                j = taille_y/2;

                int j_modified = modified(i,j);

                // controleur.placeEtage(i, j, i - 1, j_modified, triplet[1][0], i - 1, j_modified + 1, triplet[2][0]);
                return new Coup(numIA,i,j,i-1,j_modified,triplet[1][0],i-1,j_modified+1,triplet[2][0]);
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

                return new Coup(numIA,x1,y1,x2,y2,triplet[1][0],x3,y3,triplet[2][0]);
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

            return new Coup(numIA,positionrandom.ligne(),positionrandom.colonne(),batiment);
        }


        //return new Coup(jeu.getNumJoueurCourant(),)
        return null;
    }

}
