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
        byte biomeCourant = this.biome0;
        boolean fin = false;
        while(!fin){
            if(biomeCourant == Hexagone.DESERT){
                if(biomeCourant == this.biome0) biome0 = "Desert";
                else biome1 = "Desert";
            }else if(biomeCourant == Hexagone.FORET){
                if(biomeCourant == this.biome0) biome0 = "Foret";
                else biome1 = "Foret";
            }else if(biomeCourant == Hexagone.GRASS){
                if(biomeCourant == this.biome0) biome0 = "Prairie";
                else biome1 = "Prairie";
            }else if(biomeCourant == Hexagone.MONTAGNE){
                if(biomeCourant == this.biome0) biome0 = "Montagne";
                else biome1 = "Montagne";
            }else if(biomeCourant == Hexagone.LAC){
                if(biomeCourant == this.biome0) biome0 = "Lac";
                else biome1 = "Lac";
            }
            if(biomeCourant == this.biome1) fin = true;
            biomeCourant = this.biome1;
        }
        return biome0 + " | " + biome1;
    }


}
