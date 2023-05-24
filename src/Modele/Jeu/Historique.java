package Modele.Jeu;

import Modele.Jeu.Plateau.Hexagone;
import Modele.Jeu.Plateau.Plateau;
import Modele.Jeu.Plateau.Tuile;
import Structures.Position.Position;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

public class Historique implements Serializable {
    LinkedList<Coup> passe;
    LinkedList<Coup> futur;

    LinkedList<Tuile> pioche = new LinkedList<>();


    public Historique(Jeu jeu) {
        passe = new LinkedList<>();
        futur = new LinkedList<>();

        copiePioche(jeu);
    }

    public void affichePasse() {
        System.out.println("======================= HISTORIQUE PASSE =======================");
        for (Coup coup : passe) {
            System.out.println(coup.typePlacement);
        }
    }

    private void copiePioche(Jeu jeu) {
        for (int i = 0; i < jeu.pioche.size(); i++) {
            pioche.add(jeu.pioche.get(i));
        }
    }

    public LinkedList<Coup> getPasse() {
        return passe;
    }


}
