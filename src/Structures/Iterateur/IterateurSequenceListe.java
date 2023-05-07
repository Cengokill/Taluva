package Structures.Iterateur;

import Structures.Sequence.Maillon;
import Structures.Sequence.SequenceListe;

import java.util.NoSuchElementException;

public class IterateurSequenceListe<T> implements Iterateur<T> {

    final SequenceListe<T> e;
    Maillon<T> pprec, prec, courant;
    boolean last;

    public IterateurSequenceListe(SequenceListe<T> e) {
        this.e = e;
        pprec = prec = null;
        courant = (Maillon<T>) e.getTete();
        last = false;
    }

    @Override
    public boolean aProchain() {
        return courant != null;
    }

    @Override
    public T prochain() {
        if (aProchain()) {
            pprec = prec;
            prec = courant;
            courant = ((Maillon<T>) courant).suivant;
            last = true;
            return ((Maillon<T>) prec).element;
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void supprime() {
        if (last) {
            if (pprec == null) {
                e.tete = courant;
            } else {
                pprec.suivant = courant;
            }
            if (prec == e.getQueue()) {
                e.queue = pprec;
            }
            prec = pprec;
            last = false;
        } else {
            throw new IllegalStateException();
        }
    }
}
