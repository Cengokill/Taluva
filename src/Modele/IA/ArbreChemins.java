package Modele.IA;

import Modele.Jeu.Coup;
import Structures.Sequence.SequenceListe;

public class ArbreChemins implements Comparable<ArbreChemins>{
    private final InstancePlateau courant;
    private final ArbreChemins pere;
    private final SequenceListe<Coup> chemin;
    private final int poids;

    public ArbreChemins(InstancePlateau courant, SequenceListe<Coup> chemin, ArbreChemins pere, int poid){
        this.courant = courant;
        this.chemin = chemin;
        this.pere = pere;
        this.poids = poid;
    }

    public int getPoids(){
        return poids;
    }

    public InstancePlateau getCourant(){
        return courant;
    }

    public SequenceListe<Coup> getChemin(){
        return chemin;
    }

    public ArbreChemins getPere(){
        return pere;
    }

    @Override
    public int compareTo(ArbreChemins o) {
        return Integer.compare(o.getPoids(), this.getPoids());
    }
}
