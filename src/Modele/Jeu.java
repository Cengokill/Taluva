package Modele;
import javax.swing.*;
import Patterns.Observable;

public class Jeu extends Observable {
    Plateau plateau;
    Joueur joueur1, joueur2;
    IA IA1, IA2;
    byte jCourant;
    Object[] joueurs = new Object[2];
    Parametres p;
    int[]score =new int[2];

    public Jeu(Parametres p){
        this.p = p;
        score[0] = 0;
        score[1] = 0;
        String type_jeu = p.getTypeJeu();
        switch (type_jeu) {
            case "JcJ":
                joueur1 = new Joueur((byte) 0, p.getPrenom1());
                joueur2 = new Joueur((byte) 0, p.getPrenom2());
                joueurs[0] = joueur1;
                joueurs[1] = joueur2;
                break;
            case "JcAI":
                joueur1 = new Joueur((byte) 0, p.getPrenom1());
                joueur2 = new Joueur((byte) 1, p.getType_IA());
                IA1 = IA.nouvelle(this, this.p);
                joueurs[0] = joueur1;
                joueurs[1] = IA1;
                break;
            case "AIcAI":
                joueur1 = new Joueur((byte) 1, p.getType_IA() + " 1");
                joueur2 = new Joueur((byte) 1, p.getType_IA() + " 2");
                IA1 = IA.nouvelle(this, this.p);
                IA2 = IA.nouvelle(this, this.p);
                joueurs[0] = IA1;
                joueurs[1] = IA2;
                break;
        }
        plateau = new Plateau();
        lancePartie();
    }

    public void lancePartie(){
    }
}
