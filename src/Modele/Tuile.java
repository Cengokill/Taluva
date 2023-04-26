package Modele;

public class Tuile {
    byte terrain1, terrain2, terrain_volcan;
    int numero0, numero1, numero2;

    public Tuile(){
        //nombre aléatoire entre 1 et 6
        //terrain1 = (byte) ((int)(Math.random() * 5) + 1);
        //terrain2 = (byte) ((int)(Math.random() * 5) + 1);
        terrain1 = (byte) ((int)(Math.random() * 4) + 1);
        terrain2 = (byte) ((int)(Math.random() * 4) + 1);
        terrain_volcan = Hexagone.VOLCAN;
        //numero aléatoire entre 0 et 2
        numero0 = ((int)(Math.random() * 3) + 1);
        numero1 = ((int)(Math.random() * 3) + 1);
        numero2 = ((int)(Math.random() * 3) + 1);
    }
}
