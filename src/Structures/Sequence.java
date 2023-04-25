package Structures;

import Structures.Iterateur;

public interface Sequence<E> {
    void insereQueue(E element);

    void insereTete(E element);

    E extraitTete();

    E extraitQueue();

    boolean estVide();

    Iterateur<E> iterateur();
}

