import Controleur.ControleurMediateur;
import Modele.Client;
import Modele.Serveur;
import Modele.ImageLoader;
import Modele.Jeu;
import Vue.FenetreJeu;

public class TaluvaMain {
    public static void main(String[] args) {

        Jeu jeu = new Jeu(null);
        ControleurMediateur controler = new ControleurMediateur(jeu);
        Serveur serveur = new Serveur(6500);
        Client client = new Client("localhost", 6500);
        Thread t = new Thread(serveur);
        Thread t2 = new Thread(client);
        t.start();

        FenetreJeu fenetre = new FenetreJeu(jeu, controler);
        ImageLoader.loadImages();
        fenetre.affichagePlateau.repaint();
    }
}