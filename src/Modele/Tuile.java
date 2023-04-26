package Modele;

public class Tuile {
    byte terrain1, terrain2, terrain_volcan;
    int numero;

    public Tuile(){
        //nombre aléatoire entre 1 et 6
        //terrain1 = (byte) ((int)(Math.random() * 5) + 1);
        //terrain2 = (byte) ((int)(Math.random() * 5) + 1);
        terrain1 = (byte) ((int)(Math.random() * 4) + 1);
        terrain2 = (byte) ((int)(Math.random() * 4) + 1);
        terrain_volcan = Hexagone.VOLCAN;
        //numero aléatoire entre 0 et 2
        numero = ((int)(Math.random() * 3) + 1);
    }
}
