package Modele.IA;

import Modele.Jeu.Coup;
import Modele.Jeu.Joueur;
import Modele.Jeu.Plateau.Plateau;
import Modele.Jeu.Plateau.Tuile;
import Structures.Position.Position;
import Structures.Position.TripletDePosition;

import java.util.*;

public class IAIntelligente extends AbstractIA {

    public byte num_joueur_ia;
    public static int poids_temple = 5000;
    public static int poids_tour = 1000;
    public static int poids_hutte = 1;
    public static int TEMPLE = 0;
    public static int HUTTE = 1;
    public static int TOUR = 2;

    public IAIntelligente() {
        super();
    }

    public ArrayList<Tuile> ajoutTuilesPioche(LinkedList<Tuile> pioche_du_jeu){//15 tuiles différentes
        ArrayList<Tuile> pioche = new ArrayList<>();
        //calcule tous les coups avec chaque tuile de la pioche
        for(int tuileIndex = 0; tuileIndex<pioche_du_jeu.size(); tuileIndex++){
            if(!contientTuile(pioche,pioche_du_jeu.get(tuileIndex))){
                pioche.add(pioche_du_jeu.get(tuileIndex));
            }
        }
        return pioche;
    }

    public boolean contientTuile(ArrayList<Tuile> listeTuiles, Tuile t){
        for(int tuileIndex = 0; tuileIndex < listeTuiles.size(); tuileIndex++){
            if(listeTuiles.get(tuileIndex).biome0 == t.biome0 && listeTuiles.get(tuileIndex).biome1 == t.biome1){
                return true;
            }
        }
        return false;
    }

    @Override
    public Coup joue() {
        System.out.println("jeu.getPioche().size : "+jeu.getPioche().size());
        ArrayList<Tuile> pioche = ajoutTuilesPioche(jeu.getPioche());
        System.out.println("pioche.size() : "+pioche.size());
        InstanceJeu instance = new InstanceJeu(pioche, jeu.getPlateau(), jeu.getJoueurs(), jeu.getNumJoueurCourant());
        System.out.println(calculCoups_joueur_A(instance, 1));
        return null;
    }

    public int calculCoups_joueur_A(InstanceJeu instance, int horizon) {
        byte joueur_courant = instance.getJoueurCourant();
        if(horizon==0){
            return evaluation_joueur(instance, joueur_courant);
        }else {
            int valeur = Integer.MIN_VALUE;
            //le joueur A doit jouer
            //Toutes les positions possibles pour poser une tuile
            ArrayList<TripletDePosition> posPossibles = jeu.getPlateau().getTripletsPossibles();
            ArrayList<Tuile> tuilesPioche = instance.getPioche();
            //On parcourt l'ensemble des coups jouables par A
            System.out.println("tuilesPioche.size() : "+tuilesPioche.size());
            for (int i = 0; i < tuilesPioche.size(); i++) {
                System.out.println("A");
                Tuile tuile = tuilesPioche.get(i);
                //pour chaque carte unique de la pioche
                for (int j = 0; j < posPossibles.size(); j++) {
                    System.out.println("0");
                    TripletDePosition posCourante = posPossibles.get(j);
                    Position[] points = new Position[3];
                    points[0] = posCourante.getVolcan();
                    points[1] = posCourante.getTile1();
                    points[2] = posCourante.getTile2();
                    //pour chaque orientation possible de la tuile piochée
                    for (int k = 0; k < 3; k++) {
                        System.out.println("1");
                        Plateau plateauCopie = instance.getPlateau();
                        //on place la tuile avec une orientation précise
                        plateauCopie.placeEtage(joueur_courant, points[k].ligne(), points[k].colonne(), (points[k].ligne()+1)%3, (points[k].colonne()+1)%3, tuile.biome0, (points[k].ligne()+2)%3, (points[k].colonne()+2)%3, tuile.biome1);
                        //là il faut retirer la tuile de la pioche
                        ArrayList<Tuile> nouvellePioche;
                        nouvellePioche = tuilesPioche;
                        nouvellePioche.remove(i);
                        //On doit placer un bâtiment
                        ArrayList<Position> positionBatsPossibles = plateauCopie.getPositions_libres_batiments();
                        for (int posBat=0;posBat<positionBatsPossibles.size();posBat++){
                            System.out.println("2");
                            Position posCouranteBat = positionBatsPossibles.get(posBat);
                            int[] batimentsPlacable = plateauCopie.getBatimentPlacable(posCouranteBat.ligne(),posCouranteBat.colonne(),joueur_courant);
                            // On parcourt tous les choix de bâtiments possibles
                            for (int batChoisit=0;batChoisit<batimentsPlacable.length;batChoisit++){
                                System.out.println("3");
                                if(batimentsPlacable[batChoisit]==1){
                                    Joueur jCourantCopie = instance.getJoueur(joueur_courant);
                                    Joueur[] joueurs = instance.getJoueurs();

                                    plateauCopie.placeBatiment(joueur_courant,posCouranteBat.ligne(),posCouranteBat.colonne(),(byte) (batimentsPlacable[batChoisit]+1));
                                    if(batChoisit==TEMPLE) jCourantCopie.incrementeTemple();
                                    else if(batChoisit==HUTTE) jCourantCopie.incrementeHutte();
                                    else if(batChoisit==TOUR) jCourantCopie.incrementeTour();
                                    //on supprime la position du bâtiment qui n'est plus libre
                                    plateauCopie.supprimeLibreBatiments(posCouranteBat);

                                    joueurs[joueur_courant] = jCourantCopie;
                                    //on créer une copie de l'instance et on change le joueur courant
                                    InstanceJeu instanceCopie = new InstanceJeu(nouvellePioche, plateauCopie,joueurs, (byte) ((joueur_courant+1)%2));
                                    valeur = Math.max(valeur, calculCoups_joueur_B(instanceCopie, horizon - 1));
                                    System.out.println("valeur calculCoups_joueur_B : "+valeur);
                                }
                            }
                        }
                    }
                }
            }
            return valeur;
        }
    }

    public int calculCoups_joueur_B(InstanceJeu instance, int horizon) {
        byte joueur_courant = instance.getJoueurCourant();
        if(horizon==0){
            return evaluation_joueur(instance, joueur_courant);
        }else {
            int valeur = Integer.MAX_VALUE;
            //le joueur B doit jouer
            //Toutes les positions possibles pour poser une tuile
            ArrayList<TripletDePosition> posPossibles = jeu.getPlateau().getTripletsPossibles();
            ArrayList<Tuile> tuilesPioche = instance.getPioche();
            //On parcourt l'ensemble des coups jouables par B
            for (int i = 0; i < tuilesPioche.size(); i++) {
                Tuile tuile = tuilesPioche.get(i);
                //pour chaque carte unique de la pioche
                for (int j = 0; j < posPossibles.size(); j++) {
                    TripletDePosition posCourante = posPossibles.get(j);
                    Position[] points = new Position[3];
                    points[0] = posCourante.getVolcan();
                    points[1] = posCourante.getTile1();
                    points[2] = posCourante.getTile2();
                    //pour chaque orientation possible de la tuile piochée
                    for (int k = 0; k < 3; k++) {
                        Plateau plateauCopie = instance.getPlateau();
                        //on place la tuile avec une orientation précise
                        plateauCopie.placeEtage(joueur_courant, points[k].ligne(), points[k].colonne(), (points[k].ligne()+1)%3, (points[k].colonne()+1)%3, tuile.biome0, (points[k].ligne()+2)%3, (points[k].colonne()+2)%3, tuile.biome1);
                        //là il faut retirer la tuile de la pioche
                        ArrayList<Tuile> nouvellePioche;
                        nouvellePioche = tuilesPioche;
                        nouvellePioche.remove(i);
                        //On doit placer un bâtiment
                        ArrayList<Position> positionBatsPossibles = plateauCopie.getPositions_libres_batiments();
                        for (int posBat=0;posBat<positionBatsPossibles.size();posBat++){
                            Position posCouranteBat = positionBatsPossibles.get(posBat);
                            int[] batimentsPlacable = plateauCopie.getBatimentPlacable(posCouranteBat.ligne(),posCouranteBat.colonne(),joueur_courant);
                            // On parcourt tous les choix de bâtiments possibles
                            for (int batChoisit=0;batChoisit<batimentsPlacable.length;batChoisit++){
                                if(batimentsPlacable[batChoisit]==1){
                                    Joueur jCourantCopie = instance.getJoueur(joueur_courant);
                                    Joueur[] joueurs = instance.getJoueurs();

                                    plateauCopie.placeBatiment(joueur_courant,posCouranteBat.ligne(),posCouranteBat.colonne(),(byte) (batimentsPlacable[batChoisit]+1));
                                    if(batChoisit==TEMPLE) jCourantCopie.incrementeTemple();
                                    else if(batChoisit==HUTTE) jCourantCopie.incrementeHutte();
                                    else if(batChoisit==TOUR) jCourantCopie.incrementeTour();
                                    //on supprime la position du bâtiment qui n'est plus libre
                                    plateauCopie.supprimeLibreBatiments(posCouranteBat);

                                    joueurs[joueur_courant] = jCourantCopie;
                                    //on créer une copie de l'instance et on change le joueur courant
                                    InstanceJeu instanceCopie = new InstanceJeu(nouvellePioche, plateauCopie,joueurs, (byte) ((joueur_courant+1)%2));
                                    valeur = Math.min(valeur, calculCoups_joueur_A(instanceCopie, horizon - 1));
                                    System.out.println("valeur calculCoups_joueur_A : "+valeur);
                                }
                            }
                        }
                    }
                }
            }
            return valeur;
        }
    }

    public int evaluation_joueur(InstanceJeu instance, int num_joueur){
        Joueur j = instance.getJoueur(num_joueur);
        //si le joueur a posé tous ses bâtiments de 2 types, il a gagné
        if((j.getNbHuttes() == 0 && j.getNbTemples() == 0)||(j.getNbTemples() ==0 && j.getNbTours() ==0)||(j.getNbHuttes()==0 && j.getNbTours()==0)){
            return Integer.MAX_VALUE;
        }
        //si le joueur ne peut plus construire de huttes, il doit placer un temple ou une tour
        if(j.getNbHuttes() == 0){
            return 0;
        }
        //sinon on calcule le score du joueur
        int score_joueur = j.getNbHuttesPlacees() * poids_hutte;
        score_joueur += j.getNbToursPlacees() * poids_tour;
        score_joueur += j.getNbTemplesPlaces() * poids_temple;

        return score_joueur;
    }

}
