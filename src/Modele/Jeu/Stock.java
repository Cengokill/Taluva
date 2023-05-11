package Modele.Jeu;

public class Stock {
    int nbBatiment ;
    int typeBatiment;
    boolean changementDeJoueur;



    public Stock(int nbBatiment ,int typeBatiment,boolean changementDeJoueur){
        this.nbBatiment=nbBatiment;
        this.typeBatiment=typeBatiment;
        this.changementDeJoueur=changementDeJoueur;
    }
}
