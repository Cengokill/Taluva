package Modele.Jeu.Plateau;

import java.io.Serializable;

public class Tuile implements Serializable {
    public final byte biome0;
    public final byte biome1;
    final byte biomeVolcan;
    public final int numero0;
    public final int numero1;
    public final int numero2;

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

    @Override
    public String toString(){
        String biome0 = "";
        String biome1 = "";
        if(this.biome0 == Hexagone.DESERT){
            biome0 = "Desert";
        }
        if(this.biome1 == Hexagone.DESERT){
            biome1 = "Desert";
        }
        if(this.biome0 == Hexagone.FORET){
            biome0 = "Foret";
        }
        if(this.biome1 == Hexagone.FORET){
            biome1 = "Foret";
        }
        if(this.biome0 == Hexagone.GRASS){
            biome0 = "Prairie";
        }
        if(this.biome1 == Hexagone.GRASS){
            biome1 = "Prairie";
        }
        if(this.biome0 == Hexagone.MONTAGNE){
            biome0 = "Montagne";
        }
        if(this.biome1 == Hexagone.MONTAGNE){
            biome1 = "Montagne";
        }
        if(this.biome0 == Hexagone.LAC){
            biome0 = "Lac";
        }
        if(this.biome1 == Hexagone.LAC){
            biome1 = "Lac";
        }
        return biome0 + " | " + biome1;
    }


}
