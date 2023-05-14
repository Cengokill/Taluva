package Modele.IA;

import Modele.Jeu.Coup;
import Modele.Jeu.CoupValeur;
import Modele.Jeu.Jeu;
import Modele.Jeu.Joueur;
import Modele.Jeu.Plateau.Plateau;
import Modele.Jeu.Plateau.Tuile;
import Structures.Position.Point2D;
import Structures.Position.Position;
import Structures.Position.TripletDePosition;

import java.util.ArrayList;
import java.util.LinkedList;

public class AbstractIA extends Joueur implements Runnable{//une AbstractIA est exécutée par un nouveau thread
    protected Jeu jeu;
    public static LinkedList<Tuile> pioche;
    private static int type_IA;
    private static int INTELLIGENTE = 0;
    private static int ALEATOIRE = 1;

    public AbstractIA(byte type, String prenom) {
        super(type, prenom);
    }

    public static AbstractIA nouvelle(Jeu j) {
        AbstractIA resultat = null;
        pioche = new LinkedList<>();
        type_IA = ALEATOIRE;
        if(type_IA == ALEATOIRE) {
            resultat = new IAAleatoire();
        }else if(type_IA == INTELLIGENTE) {
            resultat = new IAIntelligente();
        }else{
            System.err.println("AbstractIA non supportée.");
            System.exit(1);
        }
        if (resultat != null) {
            resultat.jeu = j;
        }
        return resultat;
    }

    @Override
    public void run() {
    }

    public void calcule_coup(){
    }

    public CoupValeur joue() throws CloneNotSupportedException {
        return null;
    }

    public ArrayList<ArrayList<Coup>> coupsPossibles(InstanceJeu instance) throws CloneNotSupportedException {
        return null;
    }

}
