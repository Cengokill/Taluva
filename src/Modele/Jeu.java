package Modele;
import javax.swing.*;
import Patterns.Observable;

public class Jeu extends Observable {
    Plateau plateau;
    Joueur joueur1, joueur2;
    IA IA1, IA2;
    byte jCourant;
    Object[] joueurs = new Object[2];
    Parametres p;
    int[]score =new int[2];

    public Jeu(Parametres p){
        this.p = p;
    }
}
