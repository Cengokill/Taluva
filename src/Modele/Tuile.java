package Modele;

public class Tuile {
    byte terrain1, terrain2, terrain_volcan;

    public Tuile(){
        //nombre aléatoire entre 1 et 6
        //terrain1 = (byte) ((int)(Math.random() * 5) + 1);
        //terrain2 = (byte) ((int)(Math.random() * 5) + 1);
        terrain1 = (byte) ((int)(Math.random() * 2) + 1);
        terrain2 = (byte) ((int)(Math.random() * 2) + 1);
        terrain_volcan = Hexagone.VOLCAN;
    }
}
