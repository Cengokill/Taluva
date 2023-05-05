package Modele;

import java.util.LinkedList;

import static Modele.Hexagone.*;

public class AbstractIA implements Runnable{//une AbstractIA est ex�cut�e par un nouveau thread
    protected Jeu jeu;
    public static LinkedList pioche;

    public static AbstractIA nouvelle(Jeu j, Parametres p) {
        AbstractIA resultat = null;
        pioche = new LinkedList<>();

        //String type = p.getType_IA();
        String type = "Aléatoire";
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

    public void getPioche(){//24 tuiles fixes définies
        pioche = jeu.getPioche();
    }

    public Coup joue() {
        return null;
    }

    @Override
    public void run() {

    }
}
