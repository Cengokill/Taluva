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

    public void setEngine(TEngine vue) {
        this.vue = vue;
    }

    public boolean peutPlacerEtage(int volcan_x, int volcan_y, int tile1_x, int tile1_y, int tile2_x, int tile2_y) {
        return jeu.getPlateau().peutPlacerEtage(volcan_x, volcan_y, tile1_x, tile1_y, tile2_x, tile2_y);
    }

    public void placeEtage(int volcan_x, int volcan_y, int tile1_x, int tile1_y, byte terrain1, int tile2_x, int tile2_y, byte terrain2) {
        jeu.getPlateau().placeEtage(volcan_x, volcan_y, tile1_x, tile1_y, terrain1, tile2_x, tile2_y, terrain2);
        jeu.pioche();
    }

    public Hexagone[][] getPlateau() {
        return jeu.getPlateau().getPlateau();
    }

    public byte[] getTuileAPoser() {
        return jeu.getTuilesAPoser();
    }

    @Override
    public boolean clicSouris(int l, int c) {
        return true;
    }

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

