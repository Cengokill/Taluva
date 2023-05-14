import Controleur.ControleurMediateur;
import Modele.Jeu.Jeu;
import Modele.Reseau.Client;
import Modele.Reseau.Serveur;

import java.io.IOException;

public class TaluvaMainConsole {
    public final static byte CONSOLE = 0;
    public final static byte GRAPHIQUE = 1;
    public static void main(String[] args) throws IOException {

        byte type_jeu = CONSOLE;

        Jeu jeu = new Jeu(type_jeu);
        jeu.initPartie();

    }
}
