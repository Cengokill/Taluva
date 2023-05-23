package Modele.IA;

public class IAMoyenne extends IAIntelligente {
    public IAMoyenne(byte n, int profondeur) {
        super(n, profondeur);
        poids_temple = 200;
        poids_tour = 100;
        poids_hutte = 6;
    }
}