package Modele.IA;

import Modele.Jeu.Coup;
import Modele.Jeu.Joueur;
import Modele.Jeu.Plateau.Plateau;
import Modele.Jeu.Plateau.Tuile;
import Modele.Jeu.Stock;
import Structures.Position.Point2D;
import Structures.Position.Position;

import java.awt.*;
import java.sql.SQLOutput;
import java.util.ArrayList;

import static Vue.FenetreJeu.jeu;
import static Vue.ImageLoader.select_fin_partie;

public class InstanceJeu {
    private Plateau plateau;
    private ArrayList<Tuile> pioche;
    private Tuile tuilePiochee;
    private Joueur[] joueurs;
    private byte jCourant;
    private Color couleur_joueur;
    private int nb_joueurs;

    public static int TEMPLE = 2;
    public static int HUTTE = 1;
    public static int TOUR = 3;

    public InstanceJeu(ArrayList<Tuile> pioche, Plateau plateau, Joueur[] joueurs,int nbjoueurs, byte jCourant, Color color_joueur){
        this.pioche = pioche;
        this.plateau = plateau;
        this.joueurs = joueurs;
        this.jCourant = jCourant;
        this.nb_joueurs = nbjoueurs;
        this.couleur_joueur = color_joueur;
    }

    public int getNbJoueurs(){
        return nb_joueurs;
    }

    public byte getJoueurCourant(){
        return jCourant;
    }

    public Color getCouleurJoueur(){
        return couleur_joueur;
    }

    public Joueur getJoueurCourantClasse(){
        return joueurs[jCourant];
    }

    public Plateau getPlateau() {
        Plateau p = plateau;
        return p;
    }
    public ArrayList<Tuile> getPioche() {
        return pioche;
    }

    public Tuile pioche(){
        tuilePiochee = pioche.get(0);
        pioche.remove(0);
        return tuilePiochee;
    }

    public Tuile setTuilePiochee(){//ne supprime pas la tuile piochée de la pioche
        tuilePiochee = pioche.get(0);
        return tuilePiochee;
    }

    public Tuile setTuilePiochee(Tuile tuile){//ne supprime pas la tuile piochée de la pioche
        tuilePiochee = tuile;
        return tuilePiochee;
    }

    public Tuile getTuilePiochee(){
        return tuilePiochee;
    }

    public Joueur[] getJoueurs(){
        return joueurs;
    }
    public Joueur getJoueur(int n){
        return joueurs[n];
    }

    public boolean estFinJeu(){
        if(!pioche.isEmpty()){
            int nb_temples_j = joueurs[jCourant].getNbTemples();
            int nb_tours_j = joueurs[jCourant].getNbTours();
            int nb_huttes_j = joueurs[jCourant].getNbHuttes();
            if ((nb_temples_j == 0 && nb_tours_j == 0) || (nb_temples_j == 0 && nb_huttes_j == 0) || (nb_tours_j == 0 && nb_huttes_j == 0)) {
                return true;
            }
            return false;
        }else{
            return true;
        }
    }

    public void changeJoueur() {
        jCourant = (byte) ((jCourant + 1) % nb_joueurs);
        getPlateau().nbHuttesDisponiblesJoueur = joueurs[jCourant].getNbHuttes(); // Pour eviter d'aller dans le negatif lors de la propagation
    }

    private void joueTuile(Coup coupTuile,InstanceJeu instanceCourante){
        instanceCourante.plateau.placeEtage(getJoueurCourant(), coupTuile.volcanLigne, coupTuile.volcanColonne, coupTuile.tile1Ligne, coupTuile.tile1Colonne, coupTuile.biome1, coupTuile.tile2Ligne, coupTuile.tile2Colonne, coupTuile.biome2);
    }
    private void joueBatiment(Coup coupBatiment,InstanceJeu instanceCourante){
        byte joueur_courant = getJoueurCourant();
        Color couleur_joueur = getCouleurJoueur();
        int x = coupBatiment.batimentLigne;
        int y = coupBatiment.batimentColonne;

        if (coupBatiment.typePlacement == HUTTE){
        //On créer un tableau contenant toutes les coordonées où l'on doit propager
        ArrayList<Point2D> aPropager = instanceCourante.plateau.previsualisePropagation(x, y, couleur_joueur);
        //On place la hutte classique sans propagation
        Coup coupB = new Coup(joueur_courant, couleur_joueur, x, y, (byte) HUTTE);
        instanceCourante.plateau.joueCoup(coupB);
        //La position actuelle n'est plus libre
        Position posASupprimer = new Position(x, y);
        instanceCourante.plateau.supprimeElementNew(posASupprimer);
        //On met a jour le nombre de hutte restantes
        int hauteurCourante = plateau.getHauteurTuile(x, y);
        // On récupère le nombre de huttes disponibles pour le joueur courant
        int nbHuttesDispo = instanceCourante.plateau.nbHuttesDisponiblesJoueur - (plateau.getHauteurTuile(x,y));
        while (aPropager.size() != 0) {
            Point2D posCourantePropagation = aPropager.remove(0);
            hauteurCourante = instanceCourante.plateau.getHauteurTuile(posCourantePropagation.getPointX(), posCourantePropagation.getPointY());
            if (nbHuttesDispo >= hauteurCourante) {
                instanceCourante.plateau.placeBatiment(joueur_courant, couleur_joueur, posCourantePropagation.getPointX(), posCourantePropagation.getPointY(), (byte) HUTTE);
                // On place une hutte dessus, donc plus disponible
                posASupprimer = new Position(posCourantePropagation.getPointX(), posCourantePropagation.getPointY());
                instanceCourante.plateau.supprimeElementNew(posASupprimer);
                nbHuttesDispo -= hauteurCourante;
            }
        }
        } else { // Si nous ne posons pas de hutte, il n'y a pas de propagation
            Coup coupB = new Coup(joueur_courant, couleur_joueur, x, y, (byte) coupBatiment.typePlacement);
            instanceCourante.plateau.placeBatiment(joueur_courant, couleur_joueur, x, y, coupBatiment.typePlacement);
            //on supprime la position du bâtiment qui n'est plus libre
            Position posASupprimer = new Position(x, y);
            instanceCourante.plateau.supprimeElementNew(posASupprimer);
        }
    }
}

