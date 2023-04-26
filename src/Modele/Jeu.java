package Modele;
import javax.swing.*;

import Controleur.ControleurMediateur;
import Patterns.Observable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Jeu extends Observable {
    Plateau plateau;
    Joueur joueur1, joueur2;
    IA IA1, IA2;
    byte jCourant;
    Object[] joueurs = new Object[2];
    Parametres p;
    int[]score =new int[2];

    byte[] tuile_a_poser = new byte[2];

    LinkedList<Tuile> pioche;
    private static int TAILLE_PIOCHE = 24;

    public Jeu(Parametres p){
        pioche = new LinkedList<Tuile>();
        lancePartie();
    }

    public void lancePartie(){
        initPioche();
        plateau = new Plateau();

        pioche();
    }

    public void initPioche(){//24 tuiles
        for(int i=0;i<TAILLE_PIOCHE;i++){
            pioche.add(new Tuile());
        }
        Collections.shuffle(pioche);
    }

    public byte[] getTuilesAPoser() {
        return tuile_a_poser;
    }

    public void joueCoup(Coup coup) {
        plateau.joueCoup(coup);
    }

    public void pioche() {
        Tuile tile_courante = pioche.removeFirst();
        tuile_a_poser[0] = tile_courante.terrain1;
        tuile_a_poser[1] = tile_courante.terrain2;
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
