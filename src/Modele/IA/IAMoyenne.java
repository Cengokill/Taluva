package Modele.IA;

public class IAMoyenne extends IAIntelligente {
    public static int poids_temple = 200;
    public static int poids_tour = 100;
    public static int poids_hutte = 6;

    public IAMoyenne(byte n, int profondeur) {
        super(n, profondeur);
    }
}