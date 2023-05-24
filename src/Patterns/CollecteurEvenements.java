package Patterns;

public interface CollecteurEvenements {
    boolean clicSouris();
    void toucheClavier();
    void annuler() throws CloneNotSupportedException;
    void refaire();
}