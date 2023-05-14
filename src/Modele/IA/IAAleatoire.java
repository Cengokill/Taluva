package Modele.IA;

import Modele.Jeu.Coup;
import Modele.Jeu.CoupValeur;
import Modele.Jeu.Plateau.Plateau;
import Structures.Position.Position;
import Structures.Position.TripletDePosition;

import java.util.ArrayList;
import java.util.Random;

class IAAleatoire extends AbstractIA {

    public IAAleatoire() {
        super(IA, "IA aleatoire");
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

    @Override
    public CoupValeur joue() {
        Random r = new Random();
        int ligne=0, colonne=0;
        int taille_x = jeu.getPlateau().getCarte().length;
        int taille_y = jeu.getPlateau().getCarte()[0].length;
        byte numIA = jeu.getNumJoueurCourant();

        Coup coupT = null, coupB = null;

        byte[] tuiles = jeu.getTuilesAPoser();
        byte[][] triplet = new byte[3][2];
        triplet[1][0] = tuiles[0]; // tile 1
        triplet[2][0] = tuiles[1]; // tile 2
        triplet[0][0] = tuiles[2];
        // Trouver un emplacement pour les hexagones
        ArrayList<TripletDePosition> positionsPossible = jeu.getPlateau().getTripletsPossibles();
        //System.out.println("Taille des triplets: "+positionsPossible.size());

        TripletDePosition positionsrandom = positionsPossible.get(r.nextInt(positionsPossible.size()));
        boolean bon = jeu.getPlateau().estHexagoneVide(positionsrandom.getVolcan().ligne(),positionsrandom.getVolcan().colonne())&&jeu.getPlateau().estHexagoneVide(positionsrandom.getTile1().ligne(),positionsrandom.getTile1().colonne())&&jeu.getPlateau().estHexagoneVide(positionsrandom.getTile2().ligne(),positionsrandom.getTile2().colonne());

        int ligne1 = positionsrandom.getVolcan().ligne();
        int ligne2 = positionsrandom.getTile1().ligne();
        int ligne3 = positionsrandom.getTile2().ligne();

        int colonne1 = positionsrandom.getVolcan().colonne();
        int colonne2 = positionsrandom.getTile1().colonne();
        int colonne3 = positionsrandom.getTile2().colonne();

        coupT = new Coup(numIA,ligne1,colonne1,ligne2,colonne2,triplet[1][0],ligne3,colonne3,triplet[2][0]);

        // Trouver un emplacement pour le bâtiment
        Plateau plateauCopie = jeu.getPlateau().copie();
        plateauCopie.placeEtage(jeu.getNumJoueurCourant(), coupT.volcanLigne, coupT.volcanColonne, coupT.tile1Ligne, coupT.tile1Colonne, coupT.biome1, coupT.tile2Ligne, coupT.tile2Colonne, coupT.biome2);
        ArrayList<Position> positionPossibles = plateauCopie.getPositions_libres_batiments();
        Position positionrandom = positionPossibles.get(r.nextInt(positionPossibles.size()));

        while(plateauCopie.getBatiment(positionrandom.ligne(),positionrandom.colonne())!=0){
            plateauCopie.supprimePosBatiment(positionrandom);
            positionrandom = positionPossibles.get(r.nextInt(positionPossibles.size()));
        }
        plateauCopie.supprimeLibreBatiments(positionrandom);

        // Choisir un batiment à placer
        int batiment = -1;
        int[] batimensPlacable = coupJouableIA(positionrandom.ligne(),positionrandom.colonne(),plateauCopie);
        int randomInt = r.nextInt(3);
        //System.out.println("hutte : "+batimensPlacable[1]+" temple: "+batimensPlacable[0]+" tour: "+batimensPlacable[2]);
        if(batimensPlacable[0]==0 && batimensPlacable[1]==0 && batimensPlacable[2]==0){ // Si l'IA ne peut jouer aucun coup, on met fin a la partie
            jeu.setFinPartie();
            return null;
        }

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
        coupB = new Coup(numIA,positionrandom.ligne(),positionrandom.colonne(),(byte) batiment);
        return new CoupValeur(coupT,coupB,0);
    }

    public int[] coupJouableIA(int i,int j,Plateau plateau){
        int[] coups = plateau.getBatimentPlacable(i,j, jeu.getNumJoueurCourant());

        int hauteurTuile = plateau.getHauteurTuile(i,j);

        if(jeu.getJoueurCourantClasse().getNbTemples()<=0) coups[0] = 0;
        if(jeu.getJoueurCourantClasse().getNbTours()<=0) coups[2] = 0;
        if(jeu.getJoueurCourantClasse().getNbHuttes()<hauteurTuile) coups[1] = 0;

        return coups;
    }

}
