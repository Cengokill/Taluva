package Vue;

public interface CollecteurEvenements {
    boolean clicSouris(int l, int c);
    void toucheClavier(String t);
    void annuler();
    void refaire();
    void sauvegarder();
    void charger();
}