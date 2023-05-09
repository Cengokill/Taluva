package Modele.IA;

import Modele.Jeu.Coup;
import Modele.Jeu.Jeu;
import Modele.Jeu.Plateau.Tuile;

import java.util.LinkedList;

public class AbstractIA implements Runnable{//une AbstractIA est ex�cut�e par un nouveau thread
    protected Jeu jeu;
    public static LinkedList<Tuile> pioche;

    public static AbstractIA nouvelle(Jeu j) {
        AbstractIA resultat = null;
        pioche = new LinkedList<>();

        //String type = p.getType_IA();
        String type = "tropSmart";
        switch (type) {
            case "Aléatoire":
                resultat = new IAAleatoire();
                break;
            case "tropSmart":
                resultat = new IAIntelligente();//new IAResolveur();
                break;
            default:
                System.err.println("AbstractIA non supportée.");
                System.exit(1);
        }
        if (resultat != null) {
            resultat.jeu = j;
        }
        return resultat;
    }

    public Coup joue(){
        return null;
    }

    @Override
    public void run() {

    }
}
