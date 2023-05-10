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

    public IAIntelligente() {
        super();
        ArrayList<Tuile> pioche = ajoutTuilesPioche(jeu.getPioche());
        InstancePlateau instance = new InstancePlateau(pioche, jeu.getPlateau());
    }

    public ArrayList<Tuile> ajoutTuilesPioche(LinkedList<Tuile> pioche_du_jeu){//15 tuiles différentes
        ArrayList<Tuile> pioche = new ArrayList<>();
        //calcule tous les coups avec chaque tuile de la pioche
        for(int tuileIndex = 0; tuileIndex<pioche.size(); tuileIndex++){
            if(!contientTuile(pioche,pioche_du_jeu.get(tuileIndex))) pioche.add(pioche_du_jeu.get(tuileIndex));
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
        return null;
    }

    public int calculCoups_joueur_A(InstancePlateau instance, int horizon) {
        Joueur jCourant = jeu.getJoueurCourant();
        if(horizon==0){
            return evaluation(instance, jCourant);
        }else {
            int valeur = Integer.MIN_VALUE;
            //le joueur A doit jouer
            //Toutes les positions possibles pour poser une tuile
            ArrayList<TripletDePosition> posPossibles = jeu.getPlateau().getTripletsPossibles();
            ArrayList<Tuile> tuilesPioche = instance.getPioche();

            for (int i = 0; i < tuilesPioche.size(); i++) {
                Tuile tuile = tuilesPioche.get(i);
                //pour chaque carte unique de la pioche
                for (int j = 0; j < posPossibles.size(); j++) {
                    TripletDePosition posCourante = posPossibles.get(j);
                    Position[] points = new Position[3];
                    points[0] = posCourante.getVolcan();
                    points[1] = posCourante.getTile1();
                    points[2] = posCourante.getTile2();
                    for (int k = 0; k < 3; k++) {
                        Plateau plateauCopie = instance.getPlateau();
                        //on place la tuile avec une orientation précise
                        plateauCopie.placeEtage(num_joueur_ia, points[k].ligne(), points[k].colonne(), (points[k].ligne()+1)%3, (points[k].colonne()+1)%3, tuile.biome0, (points[k].ligne()+2)%3, (points[k].colonne()+2)%3, tuile.biome1);
                        //là il faut retirer la tuile de la pioche
                        ArrayList<Tuile> nouvellePioche;
                        nouvellePioche = tuilesPioche;
                        nouvellePioche.remove(i);
                        InstancePlateau instanceCopie = new InstancePlateau(nouvellePioche, plateauCopie);
                        valeur = Math.max(valeur, calculCoups_joueur_B(instanceCopie, horizon - 1));
                    }
                }
            }
            return valeur;
        }
    }

    public int calculCoups_joueur_B(InstancePlateau instance, int horizon) {
        Joueur jCourant = jeu.getJoueurCourant();
        if(horizon==0){
            return evaluation(instance, jCourant);
        }else {
            int valeur = Integer.MAX_VALUE;
            //le joueur B doit jouer
            //Toutes les positions possibles pour poser une tuile
            ArrayList<TripletDePosition> posPossibles = jeu.getPlateau().getTripletsPossibles();
            ArrayList<Tuile> tuilesPioche = instance.getPioche();

            for (int i = 0; i < tuilesPioche.size(); i++) {
                Tuile tuile = tuilesPioche.get(i);
                //pour chaque carte unique de la pioche
                for (int j = 0; j < posPossibles.size(); j++) {
                    TripletDePosition posCourante = posPossibles.get(j);
                    Position[] points = new Position[3];
                    points[0] = posCourante.getVolcan();
                    points[1] = posCourante.getTile1();
                    points[2] = posCourante.getTile2();
                    for (int k = 0; k < 3; k++) {
                        Plateau plateauCopie = instance.getPlateau();
                        //on place la tuile avec une orientation précise
                        plateauCopie.placeEtage(num_joueur_ia, points[k].ligne(), points[k].colonne(), (points[k].ligne()+1)%3, (points[k].colonne()+1)%3, tuile.biome0, (points[k].ligne()+2)%3, (points[k].colonne()+2)%3, tuile.biome1);
                        //là il faut retirer la tuile de la pioche
                        ArrayList<Tuile> nouvellePioche;
                        nouvellePioche = tuilesPioche;
                        nouvellePioche.remove(i);
                        InstancePlateau instanceCopie = new InstancePlateau(nouvellePioche, plateauCopie);
                        valeur = Math.min(valeur, calculCoups_joueur_A(instanceCopie, horizon - 1));
                    }
                }
            }
            return valeur;
        }
    }

    public int evaluation_joueur(InstancePlateau instance, int num_joueur){
        Joueur j = instance.getJoueur(num_joueur);
        //si le joueur a posé tous ses bâtiments de 2 types, il a gagné
        if((j.getNbHuttes() == 0 && j.getNbTemples() == 0)||(j.getNbTemples() ==0 && j.getNbTours() ==0)||(j.getNbHuttes()==0 && j.getNbTours()==0)){
            return Integer.MAX_VALUE;
        }
        //si le joueur ne peut plus construire de huttes, il a perdu
        if(j.getNbHuttes() == 0){
            return Integer.MIN_VALUE;
        }
        //sinon on calcule le score du joueur
        int score_joueur = j.getNbHuttes() * poids_hutte;
        score_joueur += j.getNbTours() * poids_tour;
        score_joueur += j.getNbTemples() * poids_temple;

        return score_joueur;
    }

}
