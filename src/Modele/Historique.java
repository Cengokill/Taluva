package Modele;

import java.io.Serializable;
import java.util.LinkedList;

public class Historique implements Serializable {
    LinkedList<Coup> passe;
    LinkedList<Coup> futur;

    public Historique() {
        passe = new LinkedList<>();
        futur = new LinkedList<>();
    }

    public void ajoute(Coup c) {
        passe.addFirst(c);
        futur.clear();
    }

    public Coup annuler() {
        Coup tete = passe.removeFirst();
        futur.addFirst(tete);
        return tete;
    }

    public Coup refaire() {
        //System.out.println("On refait, taille de futur : " + futur.size());
        Coup tete = futur.removeFirst();
        passe.addFirst(tete);
        //System.out.println("tete vide ? " + tete.estVide());
        //System.out.println("taille de futur : " + futur.size());
        return tete;
    }

    public boolean peutAnnuler() {
        return !passe.isEmpty();
    }

    public boolean peutRefaire() {
        return !futur.isEmpty();
    }
}
