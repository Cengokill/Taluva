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

public class AbstractIA extends Joueur implements Runnable{
    protected Jeu jeu;
    private static int type_IA;
    public static final byte ALEATOIRE = 0;
    public static final byte MOYENNE = 1;
    public static final byte INTELLIGENTE = 2;
    public static final byte TEST = 3;
    public int poids_temple;
    public int poids_tour;
    public int poids_hutte;
    public int numero;

    public AbstractIA(byte type, byte n, String prenom) {
        super(type, n, prenom);
        numero = n;
    }

    public static AbstractIA nouvelle(Jeu j, byte n, byte difficulte) {
        AbstractIA resultat = null;
        type_IA = difficulte;
        if(type_IA == ALEATOIRE) {
            resultat = new IAAleatoire(n);
        }else if(type_IA == INTELLIGENTE) {
            resultat = new IAIntelligente(n, 2);
        }else if(type_IA == MOYENNE) {
            resultat = new IAMoyenne(n, 0);
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
