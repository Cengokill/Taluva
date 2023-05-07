package Modele.IA;

import Modele.Jeu.Coup;
import Modele.Jeu.Plateau.Tuile;
import Structures.Sequence.SequenceListe;
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
        InstancePlateau instancePlateauDepart = new InstancePlateau(tuilesPioche, jeu.getPlateau());
        ArbreChemins arbreCheminsTete = new ArbreChemins(instancePlateauDepart, null, null, 10000);
        queue.add(arbreCheminsTete);

        while (!queue.isEmpty()) {
            chemin = new ArrayList<>();
            ArbreChemins arbreCheminsAvant = arbreCheminsTete;
            arbreCheminsTete = queue.poll();
            //récupère l'instance courante qui contient le plateau et les tuiles
            InstancePlateau instancePlateauCourante = arbreCheminsTete.getCourant();
            //si c'est à l'IA de jouer
                //calcule tous les placements possibles de la tuile de la pioche sur le plateau du jeu
            //sinon
                //même chose mais on calcule les placements possibles du joueur adverse
            SequenceListe<Coup> coupsPossibles = null;
            //TODO: calculer les placements

            //pour chaque coup possible
            while (!coupsPossibles.estVide()) {
                //on joue le coup
                //on enregistre le plateau et le pioche après avoir joué le coup dans une nouvelle InstancePlateau
                //si l'InstancePlateau n'est pas dans la Hashmap
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

    public ArrayList<Coup> joueSequence() {
        return calculCoups();
    }

    @Override
    public Coup joue() {
        return null;
    }

}
