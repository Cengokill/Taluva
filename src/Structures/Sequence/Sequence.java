package Structures.Sequence;

import Structures.Iterateur.Iterateur;

public interface Sequence<E> {
    void insereQueue(E element);

    void insereTete(E element);

    E extraitTete();

    E extraitQueue();

    boolean estVide();

    Iterateur<E> iterateur();
}

