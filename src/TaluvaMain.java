import Controleur.ControleurMediateur;
import Modele.Jeu.Joueur;
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
        int nb_parties = 1;
        Jeu jeu = new Jeu(type_jeu);
        int[] victoires_defaites = new int[2];
        jeu.AFFICHAGE = true;

        if(type_jeu == GRAPHIQUE) {
            ControleurMediateur controler = new ControleurMediateur(jeu);
            FenetreJeu fenetre = new FenetreJeu(jeu, controler);
            fenetre.panelMenu.metAJour();
            fenetre.panelMenu.setFenetre(fenetre);
        }else{// CONSOLE
            for(int i = 0; i < nb_parties; i++) {
                System.out.println("Partie " + (i+1) + "/" + nb_parties);
                jeu.initPartie("A", "B", "C", "D", 2);
                for(Joueur joueur : jeu.getJoueurs()){
                    if(joueur.getTypeJoueur()!=Joueur.IA) {
                        System.err.println("Le joueur " + joueur.getPrenom() + " n'est pas une IA. Le mode CONSOLE ne peut etre active uniquement si tous les joueurs sont des IA.");
                        System.exit(1);
                    }
                }
                while (!jeu.estFinPartie()) {
                    jeu.pioche();
                    jeu.joueIA();
                }
                if(jeu.jVainqueur == 0){
                    victoires_defaites[0]++;
                }else{
                    victoires_defaites[1]++;
                }
            }
            System.out.println("Victoires joueur 0 : " + victoires_defaites[0]);
            System.out.println("Victoires joueur 1 : " + victoires_defaites[1]);
        }
    }
}