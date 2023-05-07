package Structures.Sequence;

public class Maillon<E> {
    public final E element;
    public Maillon<E> suivant;

    Maillon(E element, Maillon<E> suivant) {
        this.element = element;
        this.suivant = suivant;
    }
}
