package Modele.Jeu;

public class Stock {
    int nbBatiment ;
    int typeBatiment;
    boolean changementDeJoueur;
    int terrain1;
    int terrain2;



    public Stock(int nbBatiment ,int typeBatiment,boolean changementDeJoueur){
        this.nbBatiment=nbBatiment;
        this.typeBatiment=typeBatiment;
        this.changementDeJoueur=changementDeJoueur;
    }
    public void setTerrain1(int terrain1){
        this.terrain1=terrain1;
    }
    public void setTerrain2(int terrain2){
        this.terrain2=terrain2;
    }
    public int getTerrain1(){
        return terrain1;
    }
    public int getTerrain2(){
        return terrain2;
    }
}
