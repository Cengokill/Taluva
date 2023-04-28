import Controleur.ControleurMediateur;
import Modele.EchoClient;
import Modele.EchoServeur;
import Modele.Jeu;
import Vue.TEngine;

public class TaluvaMain {
    public static void main(String[] args) {

        Jeu jeu = new Jeu(null);
        ControleurMediateur controler = new ControleurMediateur(jeu);
        EchoServeur serveur = new EchoServeur(6500);
        EchoClient client = new EchoClient("localhost", 6500);
        Thread t = new Thread(serveur);
        Thread t2 = new Thread(client);
        t.start();

        TEngine fenetre = new TEngine(jeu, controler);
        fenetre.setVisible(true);
    }
}