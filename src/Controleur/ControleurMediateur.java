package Controleur;

import Modele.*;
import Vue.CollecteurEvenements;
import Vue.FenetreJeu;

public class ControleurMediateur implements CollecteurEvenements {
    final Jeu jeu;
    FenetreJeu vue;

    public ControleurMediateur(Jeu j) {
        jeu = j;
    }

    public void setEngine(FenetreJeu vue) {
        this.vue = vue;
    }

    public boolean peutPlacerTuile(int volcan_i, int volcan_j, int tile1_i, int tile1_j, int tile2_i, int tile2_j) {
        //return jeu.getPlateau().estDansTripletsPossibles(volcan_i, volcan_j, tile1_i, tile1_j, tile2_i, tile2_j);
        return jeu.getPlateau().peutPlacerTuile(volcan_i, volcan_j, tile1_i, tile1_j, tile2_i, tile2_j);
    }
    public void placeEtage(int volcan_x, int volcan_y, int tile1_x, int tile1_y, byte terrain1, int tile2_x, int tile2_y, byte terrain2) {
        if (jeu.joueurPlaceEtage(volcan_x, volcan_y, tile1_x, tile1_y, terrain1, tile2_x, tile2_y, terrain2)) {
            jeu.pioche();
        }
    }
    public boolean peutPlacerBatiment(int i, int j){
        return jeu.getPlateau().peutPlacerMaison(i,j);
    }

    public void placeBatiment(int i, int j, byte type_bat) {
        jeu.joueurPlaceBatiment(i, j, type_bat);
    }

    public Hexagone[][] getPlateau() {
        return jeu.getPlateau().getCarte();
    }

    public byte[] getTuileAPoser() {
        return jeu.getTuilesAPoser();
    }

    @Override
    public boolean clicSouris() {
        return true;
    }

    @Override
    public void toucheClavier() {
    }

    public void annuler(){
        jeu.annuler();
    }

    @Override
    public void refaire() {
        jeu.refaire();
    }

    @Override
    public void sauvegarder() {
        jeu.sauvegarder();
    }

    @Override
    public void charger() {
        jeu.charger();
    }
}

