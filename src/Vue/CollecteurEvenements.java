package Vue;

public interface CollecteurEvenements {
    boolean clicSouris(int l, int c);
    void toucheClavier(String t);
    void clicAnnuler();
    void clicRefaire();
    void clicSauvegarder();
    void clicCharger();
    void clicRecommencer();
}