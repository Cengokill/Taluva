package Modele;

public class Tuile {
    public static final int VOLCAN = 32;
    public static final int GRASS = 1;
    public static final int MONTAGNE = 2;
    public static final int DESERT = 3;
    public static final int WATER = 4;
    public static final int FORET = 5;

    int terrain1, terrain2, terrain3;

    public Tuile(){
        //nombre aléatoire entre 1 et 6
        terrain1 = (int)(Math.random() * 5) + 1;
        terrain2 = (int)(Math.random() * 5) + 1;
        terrain3 = VOLCAN;
    }
}
