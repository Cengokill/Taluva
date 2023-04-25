package Modele;

public class Plateau {
    protected Hexagone [][]plateau ;
    protected int[]nbPionJ1;
    protected int[]nbPionJ2;

    public Plateau(){
        plateau = new Hexagone [40][40];
        nbPionJ1=new int [3];
        nbPionJ2=new int [3];
        nbPionJ1[0]=10;nbPionJ2[0]=10;
        nbPionJ1[1]=10;nbPionJ2[1]=10;
        nbPionJ1[3]=10;nbPionJ2[3]=10;
    }
    // check si la condition de victoire du nb de piece est bonne
    public boolean fini1(int joueur){
        int nb_pion_vite_J1 = 0;
        for (int j = 0; j<3;j++){
            if(nbPionJ1[j]==0 && joueur==1)nb_pion_vite_J1++;
            if(nbPionJ2[j]==0 && joueur==2)nb_pion_vite_J1++;
        }
        if(nb_pion_vite_J1>=2){return true;}
        return false;
    }
    public boolean tousLesMeme(int x1 , int y1 , int x2 ,int y2 ,int x3 ,int y3){
        if(plateau[x1][y1].getHauteur()==plateau[x2][y2].getHauteur() &&plateau[x1][y1].getHauteur()==plateau[x3][y3].getHauteur())
            return true;
        return false;
    }

    public boolean estPlaceLibre(int x1, int y1){
        if (plateau[x1][y1].getTypeTion()<=1)
            return true;
        return false;
    }

    public void joueHexagone(int x, int y){}
}
