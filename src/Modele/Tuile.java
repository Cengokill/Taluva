package Modele;

import Modele.Hexagone;

public class Tuile {
    final byte terrain0;
    final byte terrain1;
    final byte terrain_volcan;
    final int numero0;
    final int numero1;
    final int numero2;

    public Tuile(){
        //nombre aléatoire entre 1 et 6
        terrain0 = (byte) ((int)(Math.random() * 4) + 1);
        terrain1 = (byte) ((int)(Math.random() * 4) + 1);
        terrain_volcan = Hexagone.VOLCAN;
        //numero aléatoire entre 0 et 2
        numero0 = ((int)(Math.random() * 3));
        numero1 = ((int)(Math.random() * 3));
        numero2 = ((int)(Math.random() * 3));
    }
}
