package Controleur;

import Modele.Coup;
import Modele.Jeu;
import Vue.CollecteurEvenements;
import Vue.TEngine;

public class ControleurMediateur implements CollecteurEvenements {
    Jeu jeu;
    TEngine vue;

    public ControleurMediateur(Jeu j) {
        jeu = j;
    }


    @Override
    public boolean clicSouris(int l, int c) {
        Coup cp = new Coup();
        return jeu.joueJoueur(cp);
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

