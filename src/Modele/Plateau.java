package Modele;

public class Plateau {
    protected Hexagone[][] plateau ;
    protected int[] nbPionsJ1;
    protected int[] nbPionsJ2;
    private Historique historique;

    public Plateau(){
        plateau = new Hexagone [40][40];
        historique = new Historique();
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
                plateau[i][j] = new Hexagone(0, Hexagone.VIDE, 0);
            }
        }

        plateau[18][19] = new Hexagone(1, Hexagone.VOLCAN, 0);
        plateau[19][20] = new Hexagone(1, Hexagone.VOLCAN, 0);
        plateau[18][20] = new Hexagone(1, Hexagone.VOLCAN, 0);
        plateau[18][21] = new Hexagone(1, Hexagone.VOLCAN, 0);
        plateau[19][17] = new Hexagone(1, Hexagone.VOLCAN, 0);
        plateau[19][18] = new Hexagone(1, Hexagone.VOLCAN, 0);
        plateau[20][18] = new Hexagone(1, Hexagone.VOLCAN, 0);
        plateau[19][19] = new Hexagone(1, Hexagone.VOLCAN, 0);
        plateau[17][18] = new Hexagone(1, Hexagone.GRASS, 0);
        plateau[17][18] = new Hexagone(1, Hexagone.GRASS, 0, Hexagone.MAISON);
        plateau[17][19] = new Hexagone(3, Hexagone.GRASS, 0);
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
        if (plateau[x1][y1].getTerrain()<=1)
            return true;
        return false;
    }

    public boolean peutPlacerEtage(int volcan_x, int volcan_y, int tile1_x, int tile1_y, int tile2_x, int tile2_y) {

        int hauteur = plateau[volcan_x][volcan_y].getHauteur();


        System.out.println("====================");
        System.out.println(hauteur);
        System.out.println("====================");
        System.out.println(volcan_x);
        System.out.println(volcan_y);
        System.out.println("====================");
        System.out.println(tile1_x);
        System.out.println(tile1_y);
        System.out.println("====================");
        System.out.println(tile2_x);
        System.out.println(tile2_y);
        System.out.println("====================");
        System.out.println(plateau[volcan_x][volcan_y].getTerrain());

        // Hauteur max
        if (hauteur == 3) {
            return false;
        }
        // Vérifie si on place un volcan sur un volcan
        if (plateau[volcan_x][volcan_y].getTerrain() != Hexagone.VOLCAN) {
            return false;
        }

        // Vérifie la hauteur de toutes les cases
        if (plateau[volcan_x][volcan_y].getHauteur() != hauteur) {
            System.out.println("Volcan :" + plateau[volcan_x][volcan_y].getHauteur());
            return false;
        }
        if (plateau[tile1_x][tile1_y].getHauteur() != hauteur) {
            System.out.println("1 :" + plateau[tile1_x][tile1_y].getHauteur());
            return false;
        }
        if (plateau[tile2_x][tile2_y].getHauteur() != hauteur) {
            System.out.println("2 :" + plateau[tile2_x][tile2_y].getHauteur());
            return false;
        }
        System.out.println("C bon");
        return true;
    }

    // Nécessite un appel à peutPlacerEtage
    public void placeEtage(int volcan_x, int volcan_y, int tile1_x, int tile1_y, int terrain1, int tile2_x, int tile2_y, int terrain2) {
        System.out.println("???");
        int hauteur = plateau[volcan_x][volcan_y].getHauteur();

        System.out.println("====================");
        System.out.println(hauteur);
        System.out.println("====================");
        System.out.println(volcan_x);
        System.out.println(volcan_y);
        System.out.println("====================");
        System.out.println(tile1_x);
        System.out.println(tile1_y);
        System.out.println("====================");
        System.out.println(tile2_x);
        System.out.println(tile2_y);
        System.out.println("====================");

        plateau[volcan_x][volcan_y] = new Hexagone(hauteur + 1, plateau[volcan_x][volcan_y].getTerrain(), 0);
        plateau[tile1_x][tile1_y] = new Hexagone(hauteur + 1, terrain1, 0);
        plateau[tile2_x][tile2_y] = new Hexagone(hauteur + 1, terrain2, 0);
    }

    public void joueHexagone(int x, int y){}

    public void resetHistorique(){
        historique = new Historique();
    }

    public boolean peutAnnuler() {
        return historique.peutAnnuler();
    }

    public boolean peutRefaire() {
        return historique.peutRefaire();
    }

    public boolean annuler() {
        if (peutAnnuler()) {
            return true;
        }
        return false;
    }

    public boolean refaire() {
        if (peutRefaire()) {
            return true;
        }
        return false;
    }
}
