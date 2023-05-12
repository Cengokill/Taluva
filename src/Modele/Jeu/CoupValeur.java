package Modele.Jeu;

public class CoupValeur {
    public Coup coup;
    public int valeur;

    public CoupValeur(Coup coup, int valeur){
        this.coup = coup;
        this.valeur = valeur;
    }

    public Coup getCoup(){
        return coup;
    }

    public int getValeur(){
        return valeur;
    }
}
