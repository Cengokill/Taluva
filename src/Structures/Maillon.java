package Structures;

class Maillon<E> {
    E element;
    Maillon<E> suivant;

    Maillon(E element, Maillon<E> suivant) {
        this.element = element;
        this.suivant = suivant;
    }
}
