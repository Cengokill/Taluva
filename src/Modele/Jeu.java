package Modele;

import Patterns.Observable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class Jeu extends Observable {
    Plateau plateau;
    Joueur joueur1, joueur2;
    IA IA1, IA2;
    byte jCourant = 0;
    Object[] joueurs = new Object[2];
    Parametres p;
    int[]score =new int[2];
    byte[] tuile_a_poser = new byte[5];

    boolean doit_placer_tuile = true;
    boolean doit_placer_batiment = false;

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
        plateau.jCourant = jCourant;
        pioche();
    }


    public boolean doit_placer_tuile() {
        return doit_placer_tuile;
    }

    public boolean doit_placer_batiment() {
        return doit_placer_batiment;
    }

    public void joueurPlaceMaison(int i, int j){
        //System.out.println(doit_placer_tuile);
        //System.out.println(doit_placer_batiment);
        if (doit_placer_tuile) {
            return;
        }
        plateau.placeMaison(jCourant, i,j, (byte) plateau.getHauteurTuile(i,j));
        changeJoueur();
        doit_placer_tuile = true;
        doit_placer_batiment = false;
    }

    public boolean joueCoup(int volcan_x, int volcan_y, int tile1_x, int tile1_y, byte terrain1, int tile2_x, int tile2_y, byte terrain2){
        if (doit_placer_batiment) {
            return false;
        }
        plateau.placeEtage(jCourant, volcan_x, volcan_y, tile1_x, tile1_y, terrain1, tile2_x, tile2_y, terrain2);
        doit_placer_batiment = true;
        doit_placer_tuile = false;

        return true;
    }

    public void changeJoueur() {
        if (jCourant == (byte) 0) {
            jCourant = (byte) 1;
        } else {
            jCourant = (byte) 0;
        }
        plateau.jCourant = jCourant;
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

    public void joueJoueur(Coup cp) {
        return;
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
