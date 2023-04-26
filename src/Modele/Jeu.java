package Modele;
import javax.swing.*;

import Controleur.ControleurMediateur;
import Patterns.Observable;

public class Jeu extends Observable {
    Plateau plateau;
    Joueur joueur1, joueur2;
    IA IA1, IA2;
    byte jCourant;
    Object[] joueurs = new Object[2];
    Parametres p;
    int[]score =new int[2];

    byte[] tuile_a_poser = new byte[2];

    public Jeu(Parametres p){
        lancePartie();
    }

    public void lancePartie(){
        plateau = new Plateau();
        tuile_a_poser[0] = Hexagone.GRASS;
        tuile_a_poser[1] = Hexagone.WATER;
    }

    public byte[] getTuilesAPoser() {
        return tuile_a_poser;
    }

    public boolean joueJoueur(Coup cp) {
        return true;
    }

    public void annuler() {
        
    }

    public void refaire() {
    }

    public void sauvegarder() {
    }

    public void charger() {
    }

    public void reinitialise() {
    }

    public Plateau getPlateau() {
        return plateau;
    }
}
