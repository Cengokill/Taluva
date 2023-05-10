package Modele.IA;

import Modele.Jeu.Coup;
import Modele.Jeu.Joueur;
import Modele.Jeu.Plateau.Tuile;
import Structures.Position.TripletDePosition;

import java.util.*;

public class IAIntelligente extends AbstractIA {

    public ArrayList<Tuile> tuilesPioche;
    HashMap<String, byte[]> instances = new HashMap<>();
    public int num_joueur;

    public IAIntelligente() {
        super();
        ajoutTuilesPioche();
    }

    public void ajoutTuilesPioche(){//15 tuiles différentes
        tuilesPioche = new ArrayList<>();
        //calcule tous les coups avec chaque tuile de la pioche
        for(int tuileIndex = 0; tuileIndex<pioche.size(); tuileIndex++){
            if(!contientTuile(tuilesPioche,pioche.get(tuileIndex))) tuilesPioche.add(pioche.get(tuileIndex));
        }
    }

    public boolean contientTuile(ArrayList<Tuile> listeTuiles, Tuile t){
        for(int tuileIndex = 0; tuileIndex < listeTuiles.size(); tuileIndex++){
            if(listeTuiles.get(tuileIndex).biome0 == t.biome0 && listeTuiles.get(tuileIndex).biome1 == t.biome1){
                return true;
            }
        }
        return false;
    }

    public int calculCoups_joueur_A(InstancePlateau instance, int horizon) {
        Joueur jCourant = jeu.getJoueurCourant();
        if(horizon==0){
            return evaluation(instance, jCourant);
        }else{
            int valeur = Integer.MIN_VALUE;
            //le joueur A doit jouer
            // Toutes les positions possibles pour poser une tuile
            ArrayList<TripletDePosition> posPossibles = jeu.getPlateau().getTripletsPossibles();
            for (int i = 0; i < tuilesPioche.size(); i++) {
                Tuile tuile = tuilesPioche.get(i);
                ajoutTuilesPioche();
                for (int j = 0; j < posPossibles.size(); j++) {
                    TripletDePosition posCourante = posPossibles.get(j);
                }
            }
        }
        return 0;

    }


    @Override
    public Coup joue() {
        return null;
    }


    public int evaluation(InstancePlateau instance, Joueur j){
        return 0;
    }

}
