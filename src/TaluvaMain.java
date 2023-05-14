import Controleur.ControleurMediateur;
import Modele.Reseau.Client;
import Modele.Reseau.Serveur;
import Modele.Jeu.Jeu;
import Vue.FenetreJeu;

import java.io.IOException;

public class TaluvaMain {
    public final static byte CONSOLE = 0;
    public final static byte GRAPHIQUE = 1;
    public static void main(String[] args) throws IOException, CloneNotSupportedException {
        byte type_jeu = GRAPHIQUE;
        /*
        Serveur serveur = new Serveur(42113);
        Client client = new Client("localhost", 42113);
        Thread t = new Thread(serveur);
        Thread t2 = new Thread(client);
        t.start();
         */
        Jeu jeu = new Jeu(type_jeu);

        if(type_jeu == GRAPHIQUE) {
            ControleurMediateur controler = new ControleurMediateur(jeu);
            FenetreJeu fenetre = new FenetreJeu(jeu, controler);
            fenetre.panelMenu.metAJour();
            fenetre.panelMenu.setFenetre(fenetre);
        }else{
            jeu.initPartie();
            while(!jeu.estFinPartie()){
                jeu.joueIA();
            }
        }
    }
}