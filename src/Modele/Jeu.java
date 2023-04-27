package Modele;

import Patterns.Observable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class Jeu extends Observable {
    Plateau plateau;
    Joueur joueur1, joueur2;
    IA IA1, IA2;
    byte jCourant;
    Object[] joueurs = new Object[2];
    Parametres p;
    int[]score =new int[2];

    byte[] tuile_a_poser = new byte[5];

    LinkedList<Tuile> pioche;
    private static int TAILLE_PIOCHE = 24;

    public Jeu(Parametres p){
        jCourant = (byte) ((int)(Math.random() * 2));
        pioche = new LinkedList<Tuile>();
        lancePartie();
    }

    public void lancePartie(){
        initPioche();
        plateau = new Plateau();
        pioche();
    }

    public void joueurPlaceMaison(int i, int j){
        plateau.placeMaison(jCourant, i,j, (byte) plateau.getHauteurTuile(i,j));
    }

    public void joueurPlaceEtage(int volcan_x, int volcan_y, int tile1_x, int tile1_y, byte terrain1, int tile2_x, int tile2_y, byte terrain2){
        plateau.placeEtage(jCourant, volcan_x, volcan_y, tile1_x, tile1_y, terrain1, tile2_x, tile2_y, terrain2);
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
        Tuile tuile_courante = pioche.removeFirst();
        tuile_a_poser[0] = tuile_courante.terrain0;
        tuile_a_poser[1] = tuile_courante.terrain1;
        tuile_a_poser[2] = (byte) tuile_courante.numero0;
        tuile_a_poser[3] = (byte) tuile_courante.numero1;
        tuile_a_poser[4] = (byte) tuile_courante.numero2;
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
