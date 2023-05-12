package Modele.IA;

import Modele.Jeu.Coup;
import Modele.Jeu.CoupValeur;
import Modele.Jeu.Jeu;
import Modele.Jeu.Plateau.Tuile;

import java.util.LinkedList;

public class AbstractIA implements Runnable{//une AbstractIA est exécutée par un nouveau thread
    protected Jeu jeu;
    public static LinkedList<Tuile> pioche;
    private static int type_IA;
    private static int INTELLIGENTE = 0;
    private static int ALEATOIRE = 1;

    public static AbstractIA nouvelle(Jeu j) {
        AbstractIA resultat = null;
        pioche = new LinkedList<>();
        type_IA = INTELLIGENTE;
        if(type_IA == ALEATOIRE) {
            resultat = new IAAleatoire();
        }else if(type_IA == INTELLIGENTE) {
            resultat = new IAIntelligente();
        }else{
            System.err.println("AbstractIA non supportée.");
            System.exit(1);
        }
        if (resultat != null) {
            resultat.jeu = j;
        }
        return resultat;
    }

    @Override
    public void run() {
        if(type_IA == INTELLIGENTE){
            calcule_coup();
        }

    }

    public void calcule_coup(){
    }

    public CoupValeur joue(){
        return null;
    }

}
