package Modele;

public class Plateau {
    protected Hexagone[][] plateau ;
    protected int[] nbPionsJ1;
    protected int[] nbPionsJ2;

    public Plateau(){
        plateau = new Hexagone [40][40];
        nbPionsJ1 = new int [3];
        nbPionsJ2 = new int [3];
        nbPionsJ1[0]=10 ; nbPionsJ2[0]=10;
        nbPionsJ1[1]=10 ; nbPionsJ2[1]=10;
        nbPionsJ1[2]=10 ; nbPionsJ2[2]=10;
        initPlateau();
    }

    private void initPlateau() {
        for (int i = 0; i < plateau.length; i++) {
            for (int j = 0; j < plateau[0].length; j++) {
                plateau[i][j] = new Hexagone(-1, 0, 0, Hexagone.VIDE);
            }
        }

        plateau[18][18] = new Hexagone(1, 0, 0, Hexagone.GRASS);
        plateau[17][18] = new Hexagone(2, 0, 0, Hexagone.GRASS);
        plateau[17][19] = new Hexagone(3, 0, 0, Hexagone.GRASS);
    }

    public Hexagone[][] getPlateau() {
        return plateau;
    }

    // check si la condition de victoire du nb de pièces est bonne
    public boolean fini1(int joueur){
        int nb_pion_vite_J1 = 0;
        for (int j = 0; j<3;j++){
            if(nbPionsJ1[j]==0 && joueur==1)nb_pion_vite_J1++;
            if(nbPionsJ2[j]==0 && joueur==2)nb_pion_vite_J1++;
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
