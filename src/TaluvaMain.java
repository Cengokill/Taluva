import Controleur.ControleurMediateur;
import Modele.Reseau.Client;
import Modele.Reseau.Serveur;
import Modele.Jeu.Jeu;
import Vue.FenetreJeu;

import java.io.IOException;

public class TaluvaMain {
    public final static byte CONSOLE = 0;
    public final static byte GRAPHIQUE = 1;
    public static void main(String[] args) throws IOException {

        byte type_jeu = CONSOLE;

        Jeu jeu = new Jeu(CONSOLE);
        ControleurMediateur controler = new ControleurMediateur(jeu);
        Serveur serveur = new Serveur(42113);
        Client client = new Client("localhost", 42113);
        Thread t = new Thread(serveur);
        Thread t2 = new Thread(client);
        t.start();


        FenetreJeu fenetre = new FenetreJeu(jeu, controler);
        fenetre.panelMenu.repaint();
        fenetre.panelMenu.setFenetre(fenetre);

    }
}