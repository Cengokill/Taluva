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

    public void setPioche(){//24 tuiles fixes définies
        byte desert = DESERT;
        byte foret = FORET;
        byte prairie = GRASS;
        byte montagne = MONTAGNE;
        byte lac = LAC;
        pioche.add(new Tuile(desert, desert));
        pioche.add(new Tuile(prairie, prairie));
        pioche.add(new Tuile(foret, foret));
        pioche.add(new Tuile(montagne, montagne));
        pioche.add(new Tuile(lac, lac));
        for(int i=0; i<2; i++){
            pioche.add(new Tuile(lac, prairie));
            pioche.add(new Tuile(lac, desert));
            pioche.add(new Tuile(lac, montagne));
        }
        for(int i=0; i<3; i++){
            pioche.add(new Tuile(lac, foret));
            pioche.add(new Tuile(montagne, desert));
        }
        for(int i=0; i<4; i++){
            pioche.add(new Tuile(montagne, prairie));
            pioche.add(new Tuile(montagne, foret));
            pioche.add(new Tuile(prairie, desert));
        }
        for(int i=0; i<8; i++){
            pioche.add(new Tuile(desert, foret));
        }
        for(int i=0; i<11; i++){
            pioche.add(new Tuile(prairie, foret));
        }
    }

    public Coup joue() {
        return null;
    }

    @Override
    public void run() {

    }
}
