import Controleur.ControleurMediateur;
import Modele.Client;
import Modele.Serveur;
import Modele.ImageLoader;
import Modele.Jeu;
import Vue.FenetreJeu;
import Vue.MenuGraphique;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class TaluvaMain {
    public static void main(String[] args) throws IOException {

        Jeu jeu = new Jeu(null);
        ControleurMediateur controler = new ControleurMediateur(jeu);
        Serveur serveur = new Serveur(6500);
        Client client = new Client("localhost", 6500);
        Thread t = new Thread(serveur);
        Thread t2 = new Thread(client);
        t.start();

        JFrame frame = new JFrame("Taluva deluxe edition ULTIMATE 2027");
        ImageIcon icon = new ImageIcon("ressources/volcan_0_Tile.png");
        frame.setIconImage(icon.getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1380, 720);
        MenuGraphique menu = new MenuGraphique(frame);
        frame.add(menu);
        frame.setVisible(true);

        //FenetreJeu fenetre = new FenetreJeu(jeu, controler,1);
        //ImageLoader.loadImages();
        //fenetre.menuGraphique.repaint();
        //fenetre.affichagePlateau.repaint();
    }
}