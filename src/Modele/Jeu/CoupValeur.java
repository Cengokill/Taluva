package Modele.Jeu;

public class CoupValeur {
    public Coup coupT, coupB;
    public int valeur;

    public CoupValeur(){
    }

    public CoupValeur(Coup coupT, Coup coupB, int valeur){
        this.coupT = coupT;
        this.coupB = coupB;
        this.valeur = valeur;
    }

    public Coup getCoupT(){
        return coupT;
    }

    public Coup getCoupB(){
        return coupB;
    }

    public int getValeur(){
        return valeur;
    }

    public void setCoupT(Coup coupT){
        this.coupT = coupT;
    }

    public void setCoupB(Coup coupB){
        this.coupB = coupB;
    }
}
