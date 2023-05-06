package Modele;

import java.io.Serializable;

public class Tuile implements Serializable {
    public final byte biome0;
    public final byte biome1;
    final byte biomeVolcan;
    final int numero0;
    final int numero1;
    final int numero2;

    public Tuile(byte b0, byte b1){
        //nombre fixe
        biome0 = b0;
        biome1 = b1;
        biomeVolcan = Hexagone.VOLCAN;
        //numéro aléatoire entre 0 et 2 pour l'affichage visuel uniquement
        numero0 = ((int)(Math.random() * 3));
        numero1 = ((int)(Math.random() * 3));
        numero2 = ((int)(Math.random() * 3));
    }
}
