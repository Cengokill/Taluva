package Modele;

public class Tuile {
    final byte biome0;
    final byte biome1;
    final byte biomeVolcan;
    final int numero0;
    final int numero1;
    final int numero2;

    public Tuile(){
        //nombre aléatoire entre 1 et 6
        biome0 = (byte) ((int)(Math.random() * 4) + 1);
        biome1 = (byte) ((int)(Math.random() * 4) + 1);
        biomeVolcan = Hexagone.VOLCAN;
        //numero aléatoire entre 0 et 2
        numero0 = ((int)(Math.random() * 3));
        numero1 = ((int)(Math.random() * 3));
        numero2 = ((int)(Math.random() * 3));
    }
}
