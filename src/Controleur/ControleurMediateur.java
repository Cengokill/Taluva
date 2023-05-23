package Controleur;

import Modele.Jeu.Coup;
import Modele.Jeu.Plateau.Hexagone;
import Modele.Jeu.Jeu;
import Patterns.CollecteurEvenements;
import Vue.FenetreJeu;

import static Vue.ImageLoader.select_fin_partie;

public class ControleurMediateur implements CollecteurEvenements {
    final Jeu jeu;
    FenetreJeu vue;

    public ControleurMediateur(Jeu j) {
        jeu = j;
    }

    public void setEngine(FenetreJeu vue) {
        this.vue = vue;
    }

    public int peutPlacerTuile(int volcan_i, int volcan_j, int tile1_i, int tile1_j, int tile2_i, int tile2_j) {
        return jeu.getPlateau().peutPlacerTuile(volcan_i, volcan_j, tile1_i, tile1_j, tile2_i, tile2_j);
    }
    public void placeEtage(int volcan_x, int volcan_y, int tile1_x, int tile1_y, byte terrain1, int tile2_x, int tile2_y, byte terrain2) {
        jeu.historiqueDeCoup.add(new Coup(jeu.jCourant,volcan_x,volcan_y,tile1_x, tile1_y, terrain1, tile2_x, tile2_y, terrain2));
        jeu.joueurPlaceEtage(volcan_x, volcan_y, tile1_x, tile1_y, terrain1, tile2_x, tile2_y, terrain2);
    }
    public boolean peutPlacerBatiment(int ligne, int colonne){
        return jeu.getPlateau().peutPlacerMaison(ligne,colonne);
    }

    public void placeBatiment(int ligne, int colonne, byte type_bat) {
        if(type_bat!=Coup.SELECTEUR_BATIMENT) jeu.historiqueDeCoup.add(new Coup(jeu.jCourant,jeu.getJoueurCourantClasse().getCouleur(),ligne,colonne,type_bat));
        jeu.joueurPlaceBatiment(ligne, colonne, type_bat);
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

    public void annuler() throws CloneNotSupportedException {
        jeu.annuler();
    }

    @Override
    public void refaire() {
        jeu.refaire();
    }


    public void sauvegarder() {
        FenetreJeu.sauvegarder();
    }


    public void charger() {FenetreJeu.charger();}


}

