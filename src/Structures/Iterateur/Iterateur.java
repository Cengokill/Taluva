package Structures.Iterateur;

public interface Iterateur<T> {
    boolean aProchain();

    T prochain();

    void supprime();
}
