import Controleur.ControleurMediateur;
import Modele.IA.AbstractIA;
import Modele.Jeu.Joueur;
import Modele.Reseau.Client;
import Modele.Reseau.Serveur;
import Modele.Jeu.Jeu;
import Vue.FenetreJeu;
import com.sun.tools.javac.Main;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class TaluvaMain {
    public final static byte CONSOLE = 0;
    public final static byte GRAPHIQUE = 1;
    public static void main(String[] args) throws IOException, CloneNotSupportedException, FontFormatException {
        byte type_jeu = GRAPHIQUE;
        int nb_parties = 200;
        Jeu jeu = new Jeu(type_jeu);
        int[] victoires_defaites = new int[2];
        jeu.AFFICHAGE = true;

        if(type_jeu == GRAPHIQUE) {
            ControleurMediateur controler = new ControleurMediateur(jeu);
            FenetreJeu fenetre = new FenetreJeu(jeu, controler);
            fenetre.panelMenu.metAJour();
            fenetre.panelMenu.setFenetre(fenetre);
        }else{// CONSOLE
            //démarre le calcul du temps
            long startTime = System.currentTimeMillis();
            for(int i = 0; i < nb_parties; i++) {
                long startTimePartie = System.currentTimeMillis();
                System.out.println("Partie " + (i+1) + "/" + nb_parties);
                ArrayList<String> difficultes = new ArrayList<>();
                difficultes.add("Difficile");
                difficultes.add("Intermediaire");
                difficultes.add("Difficile");
                difficultes.add("Difficile");
                jeu.initPartie("IA", "IA", "IA", "IA", 2, "Infini", difficultes);
                for(Joueur joueur : jeu.getJoueurs()){
                    if(joueur.getTypeJoueur()!=Joueur.IA) {
                        System.err.println("Le joueur " + joueur.getPrenom() + " n'est pas une IA. Le mode CONSOLE doit etre active uniquement si tous les joueurs sont des IA.");
                        System.exit(1);
                    }
                }
                while (!jeu.estFinPartie()) {
                    jeu.pioche();
                    jeu.joueIA();
                }
                if(jeu.jVainqueur == 0 && jeu.getJoueurs()[0].calculScore() != jeu.getJoueurs()[1].calculScore()){
                    victoires_defaites[0]++;
                }else{
                    if(jeu.getJoueurs()[0].calculScore() != jeu.getJoueurs()[1].calculScore())
                    victoires_defaites[1]++;
                }//sinon égalité
                System.out.println("Victoires IA 1 : " + victoires_defaites[0]);
                System.out.println("Victoires IA 2 : " + victoires_defaites[1]);
                long endTimePartie = System.currentTimeMillis();
                long tempsPartie =  endTimePartie - startTimePartie;
                System.out.println("Temps de la partie : " + tempsPartie/1000);
            }
            long endTime = System.currentTimeMillis();
            long temps =  endTime - startTime;
            System.out.println("Temps total : " + temps/1000);
        }
    }
}