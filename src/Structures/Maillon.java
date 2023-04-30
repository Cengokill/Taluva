package Structures;

class Maillon<E> {
    final E element;
    Maillon<E> suivant;

    Maillon(E element, Maillon<E> suivant) {
        this.element = element;
        this.suivant = suivant;
    }
}
