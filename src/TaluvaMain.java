import Controleur.ControleurMediateur;
import Modele.Reseau.Client;
import Modele.Reseau.Serveur;
import Modele.Jeu.Jeu;
import Vue.FenetreJeu;

import java.io.IOException;

public class TaluvaMain {
    public static void main(String[] args) throws IOException {

        Jeu jeu = new Jeu();
        ControleurMediateur controler = new ControleurMediateur(jeu);
        Serveur serveur = new Serveur(6500);
        Client client = new Client("localhost", 6500);
        Thread t = new Thread(serveur);
        Thread t2 = new Thread(client);
        t.start();


        FenetreJeu fenetre = new FenetreJeu(jeu, controler);
        fenetre.panelMenu.repaint();
        fenetre.panelMenu.setFenetre(fenetre);

    }
}