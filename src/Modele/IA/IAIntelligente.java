package Modele.IA;

import Modele.Coup;
import Modele.Tuile;
import Structures.SequenceListe;
import Structures.TripletDePosition;

import java.util.*;

public class IAIntelligente extends AbstractIA {

    public ArrayList<Tuile> tuilesPioche;
    Map<String, byte[]> instances = new HashMap<>();
    public int num_joueur;

    public IAIntelligente() {
        super();
        ajoutTuilesPioche();
    }

    public void ajoutTuilesPioche(){//15 tuiles différentes
        tuilesPioche = new ArrayList<>();
        //calcule tous les coups avec chaque tuile de la pioche
        for(int i = 0; i<pioche.size(); i++){
            if(!contientTuile(tuilesPioche,pioche.get(i))) tuilesPioche.add(pioche.get(i));
        }
    }

    public boolean contientTuile(ArrayList<Tuile> listeTuiles, Tuile t){
        for(int i = 0; i<listeTuiles.size(); i++){
            if(listeTuiles.get(i).biome0 == t.biome0 && listeTuiles.get(i).biome1 == t.biome1){
                return true;
            }
        }
        return false;
    }

    public ArrayList<Coup> calculCoups() {
        ArrayList<Coup> chemin = new ArrayList<>();
        LinkedList<ArbreChemins> queue = new LinkedList<>();
        // Toutes les positions possibles pour poser une tuile
        ArrayList<TripletDePosition> posPossibles = jeu.getPlateau().getTripletsPossibles();
        for (int i = 0; i < tuilesPioche.size(); i++) {
            Tuile tuile = tuilesPioche.get(i);
            ajoutTuilesPioche();
            for (int j = 0; j < posPossibles.size(); j++) {
                TripletDePosition posCourante = posPossibles.get(j);
            }
        }
        int heurist_huttes = jeu.joueurs[num_joueur].getNbHuttes() * 10;
        int heuri_tours = jeu.joueurs[num_joueur].getNbTours() * 100;
        int heuri_temples = jeu.joueurs[num_joueur].getNbTemples() * 400;
        int valeur = heurist_huttes + heuri_tours + heuri_temples;
        Instance instanceDepart = new Instance(tuilesPioche, jeu.getPlateau());
        ArbreChemins arbreCheminsTete = new ArbreChemins(instanceDepart, null, null, 10000);
        queue.add(arbreCheminsTete);

        while (!queue.isEmpty()) {
            chemin = new ArrayList<>();
            ArbreChemins arbreCheminsAvant = arbreCheminsTete;
            arbreCheminsTete = queue.poll();
            //récupère l'instance courante qui contient le plateau et les tuiles
            Instance instanceCourante = arbreCheminsTete.getCourant();
            //calcule tous les placements possibles de la tuile de la pioche sur le plateau du jeu
            SequenceListe<Coup> coupsPossibles = null;
            //TODO: calculer les placements

            //pour chaque coup possible
            while (!coupsPossibles.estVide()) {
                //on joue le coup
                //on enregistre le plateau et le pioche après avoir joué le coup dans une nouvelle Instance
                //si l'Instance n'est pas dans la Hashmap
                //on calcule la valeur de l'instance
                //si l'instance est une victoire de l'IA
                //on crée un nouvel arbre avec l'instance et la valeur
                //tant que l'instance courante n'est pas l'instance de départ
                //on fait comme dans le Sokoban
                //on renvoie le chemin de Coups inversé

                //si ce n'est pas une victoire de l'IA
                // on ajoute l'instance à la Hashmap avec son poids calculé (heuristique)

                //sinon on ne fait rien
            }
        }
        return chemin;
    }

    public ArrayList<Coup> joue() {
        return calculCoups();
    }

}
