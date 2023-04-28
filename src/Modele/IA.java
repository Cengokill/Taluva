package Modele;

public class IA implements Runnable{//une IA est ex�cut�e par un nouveau thread
    protected Jeu jeu;

    public static IA nouvelle(Jeu j, Parametres p) {
        IA resultat = null;

        String type = p.getType_IA();
        switch (type) {
            case "Al�atoire":
                resultat = new IAAleatoire();
                break;
            case "tropSmart":
                resultat = new IAIntelligente();//new IAResolveur();
                break;
            default:
                System.err.println("IA non support�e.");
                System.exit(1);
        }
        if (resultat != null) {
            resultat.jeu = j;
        }
        return resultat;
    }

    public Coup joue() {
        return null;
    }

    @Override
    public void run() {

    }
}
