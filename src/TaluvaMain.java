import Controleur.ControleurMediateur;
import Modele.Jeu;
import Vue.TEngine;

public class TaluvaMain {
    public static void main(String[] args) {

        Jeu jeu = new Jeu(null);
        ControleurMediateur controler = new ControleurMediateur(jeu);


        TEngine fenetre = new TEngine(jeu, controler);
        fenetre.setVisible(true);
    }
}