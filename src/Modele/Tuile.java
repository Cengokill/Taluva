package Modele;

import java.util.List;

public class Tuile {
    //représente 3 hexagones fusionnés
    private List<Hexagone> hexagones;
    private int numero;

    public Tuile(int n, Hexagone h1, Hexagone h2, Hexagone h3) {
        numero = n;
        hexagones.add(h1);
        hexagones.add(h2);
        hexagones.add(h3);
    }

    public int getNumero() {
        return numero;
    }

    public List<Hexagone> getHexagones() {
        return hexagones;
    }



}
