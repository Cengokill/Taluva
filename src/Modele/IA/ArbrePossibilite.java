package Modele.IA;

import Modele.Jeu.Coup;
import Structures.Sequence.SequenceListe;

public class ArbrePossibilite implements Comparable<ArbrePossibilite>{
    private final InstancePlateau courant;
    private final ArbrePossibilite pere;
    private final SequenceListe<Coup> chemin;
    private final int poids;

    public ArbrePossibilite(InstancePlateau courant, SequenceListe<Coup> chemin, ArbrePossibilite pere, int poid){
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

    public ArbrePossibilite getPere(){
        return pere;
    }

    @Override
    public int compareTo(ArbrePossibilite o) {
        return Integer.compare(o.getPoids(), this.getPoids());
    }
}
