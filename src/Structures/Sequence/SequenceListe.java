package Structures.Sequence;


import Structures.Iterateur.Iterateur;
import Structures.Iterateur.IterateurSequenceListe;
import Structures.Sequence.Maillon;

public class SequenceListe<E> implements Sequence<E> {
    public Maillon<E> tete;
    public Maillon<E> queue;
    private int taille = 0;

    @Override
    public void insereQueue(E element) {
        Maillon<E> m = new Maillon<>(element, null);
        if (tete == null) {
            tete = queue = m;
        } else {
            queue.suivant = m;
            queue = queue.suivant;
        }
        taille++;
    }

    public void incrementeTaille(){
        taille++;
    }

    @Override
    public void insereTete(E element) {
        Maillon<E> m = new Maillon<>(element, tete);
        if (tete == null) {
            tete = queue = m;
        } else {
            tete = m;
        }
        taille++;
    }

    @Override
    public E extraitTete() {
        E resultat;
        // Exception si tete == null (sequence vide)
        resultat = tete.element;
        tete = tete.suivant;
        if (tete == null) {
            queue = null;
        }
        taille--;
        return resultat;
    }

    public E extraitQueue(){
        if(queue == null){
            return null;
        }else if(tete == queue){// il y a un seul élément dans la liste
            E element = queue.element;
            tete = queue = null;
            taille--;
            return element;
        }else{
            // il y a plusieurs éléments dans la liste
            Maillon<E> precedent = tete;
            while(precedent.suivant != queue){
                precedent = precedent.suivant;
            }
            E element = queue.element;
            queue = precedent;
            queue.suivant = null;
            taille--;
            return element;
        }
    }

    public void melangeAleatoire(){//mélange la liste aléatoirement
        SequenceListe<E> listeMelangee = new SequenceListe<>();
        //on ajoute des éléments de manière aléatoire dans la nouvelle liste
        while(!this.estVide()){
            int alea = (int)(Math.random() * 2);
            if(alea == 0){
                listeMelangee.insereTete(this.extraitTete());
            }else{
                listeMelangee.insereQueue(this.extraitTete());
            }
        }
        //on remet les éléments dans la liste d'origine
        while(!listeMelangee.estVide()){
            this.insereQueue(listeMelangee.extraitTete());
        }
    }

    public E getTete(){
        return tete.element;
    }

    public E getQueue(){
        return queue.element;
    }

    @Override
    public boolean estVide() {
        return tete == null;
    }

    public int taille() {
        return taille;
    }

    @Override
    public String toString() {
        StringBuilder resultat = new StringBuilder("SequenceListe [ ");
        boolean premier = true;
        Maillon<E> m = tete;
        while (m != null) {
            if (!premier)
                resultat.append(", ");
            resultat.append(m.element);
            m = m.suivant;
            premier = false;
        }
        resultat.append(" ]");
        return resultat.toString();
    }

    @Override
    public Iterateur<E> iterateur() {
        return new IterateurSequenceListe<>(this);
    }

    public int compareTo() {
        return 0;//à redéfinir
    }
}
