package Controleur;

import Modele.*;
import Vue.CollecteurEvenements;
import Vue.TEngine;

public class ControleurMediateur implements CollecteurEvenements {
    Jeu jeu;
    TEngine vue;

    public ControleurMediateur(Jeu j) {
        jeu = j;
    }

    public boolean peutPlacerEtage(int volcan_x, int volcan_y, int tile1_x, int tile1_y, int tile2_x, int tile2_y) {
        return jeu.getPlateau().peutPlacerEtage(volcan_x, volcan_y, tile1_x, tile1_y, tile2_x, tile2_y);
    }

    public void placeEtage(int volcan_x, int volcan_y, int tile1_x, int tile1_y, int terrain1, int tile2_x, int tile2_y, int terrain2) {
        jeu.getPlateau().placeEtage(volcan_x, volcan_y, tile1_x, tile1_y, terrain1, tile2_x, tile2_y, terrain2);
    }

    public Hexagone[][] getPlateau() {
        return jeu.getPlateau().getPlateau();
    }


    /*
    @Override
    public boolean clicSouris(int l, int c) {
        Coup cp = new Coup((byte) 0);
        return jeu.joueJoueur(cp);
    }
    */

    @Override
    public void toucheClavier(String t) {
    }

    public void clicAnnuler(){
        jeu.annuler();
    }

    @Override
    public void clicRefaire() {
        jeu.refaire();
    }

    @Override
    public void clicSauvegarder() {
        jeu.sauvegarder();
    }

    @Override
    public void clicCharger() {
        jeu.charger();
    }

    @Override
    public void clicRecommencer() {
        jeu.reinitialise();
    }

}

