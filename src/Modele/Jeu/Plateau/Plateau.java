package Modele.Jeu.Plateau;

import Modele.Jeu.Stock;
import Modele.Jeu.Coup;
import Structures.Position.Point2D;
import Structures.Position.Position;
import Structures.Position.TripletDePosition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import static Modele.Jeu.Plateau.Hexagone.*;

public class Plateau implements Serializable, Cloneable {
    final int LIGNES = 60;
    final int COLONNES = 60;
    protected Hexagone[][] carte;
    protected byte[] quantitePionJoueur1;
    protected byte[] quantitePionJoueur2;

    public int nbHutteDisponiblesJoueur =0; // Pour eviter d'aller dans le negatif lors de la propagation
    private Historique historique;
    private ArrayList<Position> positions_libres;

    private ArrayList<TripletDePosition> tripletsPossible;
    private ArrayList<Position> positions_libres_batiments;

    public Plateau(){
        initPlateau();
        initHistorique();
        initQuantitePions();
        initPlateau();
        initPositionsLibres();
        initTripletsPossibles();
        TripletDePosition tripletDeBase = new TripletDePosition(new Position(31,29),new Position(31,30),new Position(32,29));
        tripletsPossible.add(tripletDeBase);
        //placeEtage((byte) 0,31,29,31,30,(byte) 1,32,29,(byte) 2);
    }

    public Plateau copie(){
        Plateau p = new Plateau();
        p.historique = this.historique.copie();
        p.quantitePionJoueur1 = this.quantitePionJoueur1.clone();
        p.quantitePionJoueur2 = this.quantitePionJoueur2.clone();
        p.nbHutteDisponiblesJoueur = this.nbHutteDisponiblesJoueur;
        p.positions_libres = (ArrayList<Position>) this.positions_libres.clone();
        p.positions_libres_batiments = (ArrayList<Position>) this.positions_libres_batiments.clone();
        p.tripletsPossible = (ArrayList<TripletDePosition>) this.tripletsPossible.clone();
        p.carte = new Hexagone[LIGNES][COLONNES];
        for(int i=0;i<LIGNES;i++){
            for(int j=0;j<COLONNES;j++){
                p.carte[i][j] = this.carte[i][j];
            }
        }
        return p;
    }

    public void affiche(){
        int tronquer = 21;
        System.out.println("Plateau :");
        for(int i=tronquer;i<LIGNES-tronquer;i++){
            for(int j=tronquer;j<COLONNES-tronquer;j++){
                System.out.print("|");
                carte[i][j].affiche();
            }
            System.out.println();
        }
        System.out.println("------------------------------------------------------------------");
    }

    private void initHistorique() {
        historique = new Historique();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void initTripletsPossibles() {
        tripletsPossible = new ArrayList<>();
    }

    private void initPositionsLibres() {
        positions_libres = new ArrayList<>();
        positions_libres_batiments = new ArrayList<>();
    }

    private void initQuantitePions() {
        quantitePionJoueur1 = new byte[3];
        quantitePionJoueur2 = new byte[3];
        quantitePionJoueur1[0]=10 ;
        quantitePionJoueur2[0]=10;
        quantitePionJoueur1[1]=10 ;
        quantitePionJoueur2[1]=10;
        quantitePionJoueur1[2]=10 ;
        quantitePionJoueur2[2]=10;
    }


    private void initPlateau() {
        carte = new Hexagone[LIGNES][COLONNES];
        remplirPlateau();
    }

    private void remplirPlateau() {
        for (int ligne = 0; ligne < carte.length; ligne++) {
            for (int colonne = 0; colonne < carte[0].length; colonne++) {
                carte[ligne][colonne] = new Hexagone((byte)0, Hexagone.VIDE, (byte)19, (byte)20);
            }
        }
    }

    public Hexagone[][] getCarte() {
        return carte;
    }

    // check si la condition de victoire du nb de pi?ces est bonne
    public boolean aGagneJoueur1(int joueur){
        int nb_pion_vite_J1 = 0;
        for (int j = 0; j<3;j++){
            if(quantitePionJoueur1[j]==0 && joueur==1)nb_pion_vite_J1++;
            if(quantitePionJoueur2[j]==0 && joueur==2)nb_pion_vite_J1++;
        }
        return nb_pion_vite_J1 >= 2;
    }
    public boolean tousLesMeme(int x1 , int y1 , int x2 ,int y2 ,int x3 ,int y3){
        return carte[x1][y1].getHauteur() == carte[x2][y2].getHauteur() && carte[x1][y1].getHauteur() == carte[x3][y3].getHauteur();
    }

    public boolean estHexagoneLibre(int ligne, int colonne){
        return carte[ligne][colonne].getBiomeTerrain() <= 1;
    }

    public boolean estDansTripletsPossibles(int ligneVolcan, int colonneVolcan, int ligneTile1, int colonneTile1, int ligneTile2, int colonneTile2) {
        for(TripletDePosition triplet : tripletsPossible){
            // Attention le Point X des triplets correspond toujours au volcan !!
            if (peutPlacerTuileFromTriplet(ligneVolcan, colonneVolcan, ligneTile1, colonneTile1, ligneTile2, colonneTile2, triplet))
                return true;
        }
        return false;
    }

    private boolean estTemple(int ligne,int colonne){
        return getBatiment(ligne,colonne)==TEMPLE;
    }

    private boolean estTour(int ligne,int colonne){
        return getBatiment(ligne, colonne) == TOUR;
    }
    private ArrayList<Point2D> positionsBatsVillage(int x, int y, byte idjoueur){
        ArrayList<Point2D> listeDesHutesVoisines = new ArrayList<>();
        Point2D positionHutte = new Point2D(x,y);
        listeDesHutesVoisines.add(positionHutte);

        int i = 0;
        while (listeDesHutesVoisines.size()!=i){
            Point2D HuteCourant = listeDesHutesVoisines.get(i);
            ajouterHuttesVoisines(idjoueur, listeDesHutesVoisines, HuteCourant);
            i++;
        }
        return listeDesHutesVoisines;
    }

    private boolean estBatiment(int i,int j){
        return carte[i][j].getBatiment()==HUTTE||carte[i][j].getBatiment()==TEMPLE||carte[i][j].getBatiment()==TOUR;
    }
    private ArrayList<ArrayList<Point2D>> getTousLesVillagesVoisins(int x, int y, byte idjoueur){
        ArrayList<ArrayList<Point2D>> listeVillages = new ArrayList<>();
        ArrayList<Point2D> listeDesHutesVoisines_1 = new ArrayList<>();
        ArrayList<Point2D> listeDesHutesVoisines_2 = new ArrayList<>();
        ArrayList<Point2D> listeDesHutesVoisines_3 = new ArrayList<>();
        ArrayList<Point2D> listeDesHutesVoisines_4 = new ArrayList<>();
        ArrayList<Point2D> listeDesHutesVoisines_5 = new ArrayList<>();
        ArrayList<Point2D> listeDesHutesVoisines_6 = new ArrayList<>();
        Point2D positionHutte_1 = null;
        Point2D positionHutte_2 = null;
        Point2D positionHutte_3 = null;
        Point2D positionHutte_4 = null;
        Point2D positionHutte_5 = null;
        Point2D positionHutte_6 = null;
        if(estBatiment(x-1,y) && carte[x-1][y].getNumJoueur()==idjoueur) positionHutte_1 = new Point2D(x-1,y);
        if(estBatiment(x+1,y) && carte[x+1][y].getNumJoueur()==idjoueur) positionHutte_2 = new Point2D(x+1,y);
        if(estBatiment(x,y-1) && carte[x][y-1].getNumJoueur()==idjoueur) positionHutte_3 = new Point2D(x,y-1);
        if(estBatiment(x,y+1) && carte[x][y+1].getNumJoueur()==idjoueur) positionHutte_4 = new Point2D(x,y+1);

        if(x%2==1){
            if(estBatiment(x-1,y-1) && carte[x-1][y-1].getNumJoueur()==idjoueur ) positionHutte_5 = new Point2D(x-1,y-1);
            if(estBatiment(x+1,y-1) && carte[x+1][y-1].getNumJoueur()==idjoueur) positionHutte_6 = new Point2D(x+1,y-1);
        }else{
            if(estBatiment(x-1,y+1) && carte[x-1][y+1].getNumJoueur()==idjoueur) positionHutte_5 = new Point2D(x-1,y+1);
            if(estBatiment(x+1,y+1) && carte[x+1][y+1].getNumJoueur()==idjoueur) positionHutte_6 = new Point2D(x+1,y+1);
        }

        listeDesHutesVoisines_1.add(positionHutte_1);
        listeDesHutesVoisines_2.add(positionHutte_2);
        listeDesHutesVoisines_3.add(positionHutte_3);
        listeDesHutesVoisines_4.add(positionHutte_4);
        listeDesHutesVoisines_5.add(positionHutte_5);
        listeDesHutesVoisines_6.add(positionHutte_6);

        listeDesHutesVoisines_1 = positionBatVillageCourant(listeDesHutesVoisines_1,idjoueur);
        listeDesHutesVoisines_2 = positionBatVillageCourant(listeDesHutesVoisines_2,idjoueur);
        listeDesHutesVoisines_3 = positionBatVillageCourant(listeDesHutesVoisines_3,idjoueur);
        listeDesHutesVoisines_4 = positionBatVillageCourant(listeDesHutesVoisines_4,idjoueur);
        listeDesHutesVoisines_5 = positionBatVillageCourant(listeDesHutesVoisines_5,idjoueur);
        listeDesHutesVoisines_6 = positionBatVillageCourant(listeDesHutesVoisines_6,idjoueur);

        if(listeDesHutesVoisines_1!=null)
            listeDesHutesVoisines_1.add(positionHutte_1);
        listeVillages.add(listeDesHutesVoisines_1);
        if(!batEnCommun(listeDesHutesVoisines_1,listeDesHutesVoisines_2))
            if(listeDesHutesVoisines_2!=null)
                listeDesHutesVoisines_2.add(positionHutte_2);
        listeVillages.add(listeDesHutesVoisines_2);
        if(!batEnCommun(listeDesHutesVoisines_1,listeDesHutesVoisines_3) && !batEnCommun(listeDesHutesVoisines_2,listeDesHutesVoisines_3))
            if(listeDesHutesVoisines_3!=null)
                listeDesHutesVoisines_3.add(positionHutte_3);
        listeVillages.add(listeDesHutesVoisines_3);
        if(!batEnCommun(listeDesHutesVoisines_1,listeDesHutesVoisines_4) && !batEnCommun(listeDesHutesVoisines_2,listeDesHutesVoisines_4) && !batEnCommun(listeDesHutesVoisines_3,listeDesHutesVoisines_4))
            if(listeDesHutesVoisines_4!=null)
                listeDesHutesVoisines_4.add(positionHutte_4);
        listeVillages.add(listeDesHutesVoisines_4);
        if(!batEnCommun(listeDesHutesVoisines_1,listeDesHutesVoisines_5) && !batEnCommun(listeDesHutesVoisines_2,listeDesHutesVoisines_5) && !batEnCommun(listeDesHutesVoisines_3,listeDesHutesVoisines_5)  && !batEnCommun(listeDesHutesVoisines_4,listeDesHutesVoisines_5))
            if(listeDesHutesVoisines_5!=null)
                listeDesHutesVoisines_5.add(positionHutte_5);
        listeVillages.add(listeDesHutesVoisines_5);
        if(!batEnCommun(listeDesHutesVoisines_1,listeDesHutesVoisines_6) && !batEnCommun(listeDesHutesVoisines_2,listeDesHutesVoisines_6) && !batEnCommun(listeDesHutesVoisines_3,listeDesHutesVoisines_6)  && !batEnCommun(listeDesHutesVoisines_4,listeDesHutesVoisines_6) && !batEnCommun(listeDesHutesVoisines_5,listeDesHutesVoisines_6))
            if(listeDesHutesVoisines_6!=null)
                listeDesHutesVoisines_6.add(positionHutte_6);
        listeVillages.add(listeDesHutesVoisines_6);

        return listeVillages;
    }
    private ArrayList<Point2D> positionBatVillageCourant(ArrayList<Point2D> listeDesHutesVoisines, byte idjoueur){
        int i = 0;
        while (listeDesHutesVoisines.size()!=i){
            Point2D HuteCourant = listeDesHutesVoisines.get(i);
            if(HuteCourant!=null) ajouterHuttesVoisines(idjoueur, listeDesHutesVoisines, HuteCourant);
            i++;
        }
        return listeDesHutesVoisines;
    }

    private boolean batEnCommun(ArrayList<Point2D> village1, ArrayList<Point2D> village2){
        if(village1==null||village2==null) return false;
        for(Point2D pointCourant1: village1){
            for(Point2D pointCourant2: village2){
                if(pointCourant1!=null && pointCourant2!=null && pointCourant1.equals(pointCourant2)) return true;
            }
        }
        return false;
    }


    private void ajouterHuttesVoisines(byte idjoueur, ArrayList<Point2D> listeDesHutesVoisines, Point2D HuteCourant) {
        if(check(HuteCourant.getPointX()-1 , HuteCourant.getPointY(), idjoueur)){
            Point2D p1 = new Point2D(HuteCourant.getPointX()-1 , HuteCourant.getPointY());
            if(notIn(listeDesHutesVoisines,p1))
                listeDesHutesVoisines.add(p1);
        }
        if(check(HuteCourant.getPointX()+1 , HuteCourant.getPointY(), idjoueur)){
            Point2D p1 = new Point2D(HuteCourant.getPointX()+1 , HuteCourant.getPointY());
            if(notIn(listeDesHutesVoisines,p1))
                listeDesHutesVoisines.add(p1);
        }
        if(check(HuteCourant.getPointX() , HuteCourant.getPointY()-1, idjoueur)){
            Point2D p1 = new Point2D(HuteCourant.getPointX() , HuteCourant.getPointY()-1);
            if(notIn(listeDesHutesVoisines,p1))
                listeDesHutesVoisines.add(p1);
        }
        if(check(HuteCourant.getPointX() , HuteCourant.getPointY()+1, idjoueur)){
            Point2D p1 = new Point2D(HuteCourant.getPointX() , HuteCourant.getPointY()+1);
            if(notIn(listeDesHutesVoisines,p1))
                listeDesHutesVoisines.add(p1);
        }
        if(HuteCourant.getPointX()%2==1){
            if(check(HuteCourant.getPointX()-1 , HuteCourant.getPointY()-1, idjoueur)){
                Point2D p1 = new Point2D(HuteCourant.getPointX()-1 , HuteCourant.getPointY()-1);
                if(notIn(listeDesHutesVoisines,p1))
                    listeDesHutesVoisines.add(p1);
            }
            if(check(HuteCourant.getPointX()+1 , HuteCourant.getPointY()-1, idjoueur)){
                Point2D p1 = new Point2D(HuteCourant.getPointX()+1 , HuteCourant.getPointY()-1);
                if(notIn(listeDesHutesVoisines,p1))
                    listeDesHutesVoisines.add(p1);
            }
        }else{
            if(check(HuteCourant.getPointX()-1 , HuteCourant.getPointY()+1, idjoueur)){
                Point2D p1 = new Point2D(HuteCourant.getPointX()-1 , HuteCourant.getPointY()+1);
                if(notIn(listeDesHutesVoisines,p1))
                    listeDesHutesVoisines.add(p1);
            }
            if(check(HuteCourant.getPointX()+1 , HuteCourant.getPointY()+1, idjoueur)){
                Point2D p1 = new Point2D(HuteCourant.getPointX()+1 , HuteCourant.getPointY()+1);
                if(notIn(listeDesHutesVoisines,p1))
                    listeDesHutesVoisines.add(p1);
            }
        }
    }

    private boolean effaceUnVillageEntier(int ligneTile1, int colonneTile1, int ligneTile2, int colonneTile2){
        if(getBatiment(ligneTile1,colonneTile1)==HUTTE){                            // une hutte sur la premiere tuile
            byte idBat_1 = getCarte()[ligneTile1][colonneTile1].getNumJoueur();
            if(getBatiment(ligneTile2,colonneTile2)==HUTTE){                        // sur la deuxieme aussi
                byte idBat_2 = getCarte()[ligneTile2][colonneTile2].getNumJoueur();
                if(idBat_1==idBat_2){                                               // les 2 appartiennent au même joueur
                    return positionsBatsVillage(ligneTile1, colonneTile1, idBat_1).size() <= 2 && positionsBatsVillage(ligneTile1, colonneTile1, idBat_1).size() > 0; // on efface tout le village qui contient 2 huttes
                }else{                                                              // les 2 appartiennet à des joueurs differents
                    if(positionsBatsVillage(ligneTile1,colonneTile1,idBat_1).size()<=1 && positionsBatsVillage(ligneTile1,colonneTile1,idBat_1).size()>0) return true; // on efface tout le village de idBat_1 qui contient 1 hutte
                    return positionsBatsVillage(ligneTile2, colonneTile2, idBat_2).size() <= 1 && positionsBatsVillage(ligneTile1, colonneTile1, idBat_2).size() > 0; // on efface tout le village de idBat_2 qui contient 1 hutte
                }
            }else{ // il n'y a pas de batiment sur la deuxieme hutte
                return positionsBatsVillage(ligneTile1, colonneTile1, idBat_1).size() <= 1 && positionsBatsVillage(ligneTile1, colonneTile1, idBat_1).size() > 0; // on efface tout le village qui contient 1 hutte
            }
        }
        return false;
    }

    private boolean peutPlacerTuileFromTriplet(int ligneVolcan, int colonneVolcan, int ligneTile1, int colonneTile1, int ligneTile2, int colonneTile2, TripletDePosition triplet) {
        if(estTemple(ligneVolcan,colonneVolcan)||estTemple(ligneTile1,colonneTile1)||estTemple(ligneTile2,colonneTile2)) return false;                                      // On ne place pas sur un temple
        if(estTour(ligneVolcan,colonneVolcan)||estTour(ligneTile1,colonneTile1)||estTour(ligneTile2,colonneTile2)) return false;                                            // On ne place pas sur une tour
        if(effaceUnVillageEntier(ligneTile1,colonneTile1,ligneTile2,colonneTile2)||effaceUnVillageEntier(ligneTile2,colonneTile2,ligneTile1,colonneTile1)) return false;    // On efface un village entier

        if (estMemePositionTriplet(ligneVolcan, colonneVolcan, ligneTile1, colonneTile1, ligneTile2, colonneTile2, triplet)) {
            return true;
        }
        if (estEmplacementSurOcean(ligneVolcan, colonneVolcan, ligneTile1, colonneTile1, ligneTile2, colonneTile2)) {
            if (estMemePositionTriplet(ligneTile1, colonneTile1, ligneVolcan, colonneVolcan, ligneTile2, colonneTile2, triplet))
                return true;
            return estMemePositionTriplet(ligneTile2, colonneTile2, ligneVolcan, colonneVolcan, ligneTile1, colonneTile1, triplet);
        }
        return false;
    }

    private boolean estEmplacementSurOcean(int ligneVolcan, int colonneVolcan, int ligneTile1, int colonneTile1, int ligneTile2, int colonneTile2) {
        return estHexagoneVide(ligneVolcan, colonneVolcan) && estHexagoneVide(ligneTile1, colonneTile1) && estHexagoneVide(ligneTile2, colonneTile2);
    }

    private boolean estMemePositionTriplet(int ligneVolcan, int colonneVolcan, int ligneTile1, int colonneTile1, int ligneTile2, int colonneTile2, TripletDePosition triplet) {
        if (triplet.getVolcan().ligne() == ligneVolcan && triplet.getVolcan().colonne() == colonneVolcan && triplet.getTile1().ligne() == ligneTile1 && triplet.getTile1().colonne() == colonneTile1 && triplet.getTile2().ligne() == ligneTile2 && triplet.getTile2().colonne() == colonneTile2)
            return true;
        return triplet.getVolcan().ligne() == ligneVolcan && triplet.getVolcan().colonne() == colonneVolcan && triplet.getTile1().ligne() == ligneTile2 && triplet.getTile1().colonne() == colonneTile2 && triplet.getTile2().ligne() == ligneTile1 && triplet.getTile2().colonne() == colonneTile1;
    }

    public int[] getNbBatEcrase(int volcan_i, int volcan_j, int tile1_i, int tile1_j, int tile2_i, int tile2_j){
        int nb_bat_1 = 0,nb_bat_2 = 0;
        if(carte[volcan_i][volcan_j].getBatiment()!=0 && carte[volcan_i][volcan_j].getNumJoueur() == 0) nb_bat_1++;
        if(carte[tile1_i][tile1_j].getBatiment()!=0 && carte[volcan_i][volcan_j].getNumJoueur() == 0) nb_bat_1++;
        if(carte[tile2_i][tile2_j].getBatiment()!=0  && carte[volcan_i][volcan_j].getNumJoueur() == 0) nb_bat_1++;

        if(carte[volcan_i][volcan_j].getBatiment()!=0  && carte[volcan_i][volcan_j].getNumJoueur() == 1) nb_bat_2++;
        if(carte[tile1_i][tile1_j].getBatiment()!=0  && carte[volcan_i][volcan_j].getNumJoueur() == 1) nb_bat_2++;
        if(carte[tile2_i][tile2_j].getBatiment()!=0  && carte[volcan_i][volcan_j].getNumJoueur() == 1) nb_bat_2++;

        int[] tab =  new int[2];
        tab[0] = nb_bat_1;
        tab[1] = nb_bat_2;
        return tab;
    }

    public int peutPlacerTuile(int ligneVolcan, int colonneVolcan, int ligneTile1, int colonneTile1, int ligneTile2, int colonneTile2) {    // renvoie 0 si OK
        // Vérifie qu'on ne pose pas au bord du plateau
        if (ligneVolcan < 2 || colonneVolcan < 2 || ligneVolcan >= carte.length-2 || colonneVolcan >= carte.length-2) {
            return 1;
        }
        if (ligneTile1 < 2 || colonneTile1 < 2 || ligneTile1 >= carte.length-2 || colonneTile1 >= carte.length-2) {
            return 1;
        }
        if (ligneTile2 < 2 || colonneTile2 < 2 || ligneTile2 >= carte.length-2 || colonneTile2 >= carte.length-2) {
            return 1;
        }

        // Premiere tuile posée
        if(estVide() && (ligneVolcan>=carte.length/2-1) && (ligneVolcan<=carte.length/2+4) && (colonneVolcan>=carte.length/2-2) && (colonneVolcan<=carte.length/2)){
            return 0;
        }

        // Hauteur max
        int hauteur = carte[ligneVolcan][colonneVolcan].getHauteur();
        if (hauteur == 4) {
            return 2;
        }
        // Vérifie si on place un volcan sur un volcan
        if (carte[ligneVolcan][colonneVolcan].getBiomeTerrain() != Hexagone.VOLCAN && carte[ligneVolcan][colonneVolcan].getBiomeTerrain() != Hexagone.VIDE) {
            return 3;
        }
        // Vérifie qu'on ne place pas pile poil par dessus une autre tuile
        if(carte[ligneTile1][colonneTile1].getLigneVolcan()==ligneVolcan && carte[ligneTile1][colonneTile1].getColonneVolcan()==colonneVolcan
                && carte[ligneTile2][colonneTile2].getLigneVolcan()==ligneVolcan && carte[ligneTile2][colonneTile2].getColonneVolcan()==colonneVolcan){
            return 4;
        }

        // On ne place pas sur un temple
        if(estTemple(ligneVolcan,colonneVolcan)||estTemple(ligneTile1,colonneTile1)||estTemple(ligneTile2,colonneTile2)) return 5;
        // On ne place pas sur une tour
        if(estTour(ligneVolcan,colonneVolcan)||estTour(ligneTile1,colonneTile1)||estTour(ligneTile2,colonneTile2)) return 6;
        // On efface un village entier
        if(effaceUnVillageEntier(ligneTile1,colonneTile1,ligneTile2,colonneTile2)||effaceUnVillageEntier(ligneTile2,colonneTile2,ligneTile1,colonneTile1)) return 7;

        // Vérifie la hauteur de toutes les cases
        if (carte[ligneVolcan][colonneVolcan].getHauteur() != hauteur) {
            return 8;
        }
        if (carte[ligneTile1][colonneTile1].getHauteur() != hauteur) {
            return 8;
        }
        if (carte[ligneTile2][colonneTile2].getHauteur() != hauteur) {
            return 8;
        }


        if (carte[ligneVolcan][colonneVolcan].getBiomeTerrain() == Hexagone.VIDE && carte[ligneTile1][colonneTile1].getBiomeTerrain() == Hexagone.VIDE && carte[ligneTile2][colonneTile2].getBiomeTerrain() == Hexagone.VIDE) {


            if (!(
                    // Gauche droite
                    carte[ligneVolcan][colonneVolcan - 1].getBiomeTerrain() != Hexagone.VIDE ||
                            carte[ligneVolcan][colonneVolcan + 1].getBiomeTerrain() != Hexagone.VIDE ||
                            carte[ligneTile1][colonneTile1 - 1].getBiomeTerrain() != Hexagone.VIDE ||
                            carte[ligneTile1][colonneTile1 + 1].getBiomeTerrain() != Hexagone.VIDE ||
                            carte[ligneTile2][colonneTile2 - 1].getBiomeTerrain() != Hexagone.VIDE ||
                            carte[ligneTile2][colonneTile2 + 1].getBiomeTerrain() != Hexagone.VIDE)) {

                if (ligneVolcan % 2 == 1) {
                    colonneVolcan -= 1;
                }
                if (ligneTile1 % 2 == 1) {
                    colonneTile1 -= 1;
                }
                if (ligneTile2 % 2 == 1) {
                    colonneTile2 -= 1;
                }

                if (carte[ligneVolcan - 1][colonneVolcan + 1].getBiomeTerrain() != Hexagone.VIDE ||
                        carte[ligneVolcan - 1][colonneVolcan].getBiomeTerrain() != Hexagone.VIDE ||
                        carte[ligneVolcan + 1][colonneVolcan + 1].getBiomeTerrain() != Hexagone.VIDE ||
                        carte[ligneVolcan + 1][colonneVolcan].getBiomeTerrain() != Hexagone.VIDE ||

                        carte[ligneTile1 - 1][colonneTile1 + 1].getBiomeTerrain() != Hexagone.VIDE ||
                        carte[ligneTile1 - 1][colonneTile1].getBiomeTerrain() != Hexagone.VIDE ||
                        carte[ligneTile1 + 1][colonneTile1 + 1].getBiomeTerrain() != Hexagone.VIDE ||
                        carte[ligneTile1 + 1][colonneTile1].getBiomeTerrain() != Hexagone.VIDE ||

                        carte[ligneTile2 - 1][colonneTile2 + 1].getBiomeTerrain() != Hexagone.VIDE ||
                        carte[ligneTile2 - 1][colonneTile2].getBiomeTerrain() != Hexagone.VIDE ||
                        carte[ligneTile2 + 1][colonneTile2 + 1].getBiomeTerrain() != Hexagone.VIDE ||
                        carte[ligneTile2 + 1][colonneTile2].getBiomeTerrain() != Hexagone.VIDE) return 0;
                else return 9;
            }

        }

        return 0;
    }
    public boolean peutPlacerVillage(int x ,int y){
        return carte[x][y].getBiomeTerrain() != Hexagone.VOLCAN || carte[x][y].getBatiment() == Hexagone.VIDE;

    }
    public boolean VillageQuestionMarK(int x , int y){
        return carte[x][y].getBatiment() == Hexagone.HUTTE;

    }

    public ArrayList<Position> voisins(int l, int c){
        ArrayList<Position> listeVoisins = new ArrayList<>();

        // Gauche
        listeVoisins.add(new Position(l,c-1));


        // Droite
        listeVoisins.add(new Position(l,c+1));

        // Si ligne impaire
        if(l%2==1) {
            c = c - 1;
        }

        // En bas a gauche
        listeVoisins.add(new Position(l+1,c));

        // En bas a droite
        listeVoisins.add(new Position(l+1,c+1));

        // En haut a gauche
        listeVoisins.add(new Position(l-1,c));

        // En haut a droite
        listeVoisins.add(new Position(l-1,c+1));

        return listeVoisins;
    }

    public void supprimeTriplets(TripletDePosition pos){
        tripletsPossible.remove(pos);
    }

    public void creerTriplets(ArrayList<Position> voisins){
        ArrayList<TripletDePosition> triplets = new ArrayList<>();
        for(Position voisin : voisins){
            ArrayList<Position> voisinsDeVoisins = new ArrayList<>();
            voisinsDeVoisins = voisins(voisin.ligne(),voisin.colonne());
            Position courant = new Position(voisin.ligne(),voisin.colonne()) ;
            Position enHautDroite;
            Position enBasDroite;
            Position gauche;
            Position droite;
            Position enHautGauche;
            Position enBasGauche;
            gauche = voisinsDeVoisins.get(0);
            droite = voisinsDeVoisins.get(1);
            enBasGauche = voisinsDeVoisins.get(2);
            enBasDroite = voisinsDeVoisins.get(3);
            enHautGauche= voisinsDeVoisins.get(4);
            enHautDroite = voisinsDeVoisins.get(5);

            if ((peutPlacerTuile(courant.ligne(), courant.colonne(), enHautGauche.ligne(), enHautGauche.colonne(), enHautDroite.ligne(), enHautDroite.colonne()))==0) {
                triplets.add(new TripletDePosition(courant, enHautGauche, enHautDroite));
            }
            if ((peutPlacerTuile(courant.ligne(), courant.colonne(), gauche.ligne(), gauche.colonne(), enHautGauche.ligne(), enHautGauche.colonne()))==0) {
                triplets.add(new TripletDePosition(courant, gauche, enHautGauche));
            }
            if ((peutPlacerTuile(courant.ligne(), courant.colonne(), enBasGauche.ligne(), enBasGauche.colonne(), gauche.ligne(), gauche.colonne()))==0) {
                triplets.add(new TripletDePosition(courant, enBasGauche, gauche));
            }
            if ((peutPlacerTuile(courant.ligne(), courant.colonne(), enBasDroite.ligne(), enBasDroite.colonne(), enBasGauche.ligne(), enBasGauche.colonne()))==0) {
                triplets.add(new TripletDePosition(courant, enBasDroite, enBasGauche));
            }
            if ((peutPlacerTuile(courant.ligne(), courant.colonne(), droite.ligne(), droite.colonne(), enBasDroite.ligne(), enBasDroite.colonne()))==0) {
                triplets.add(new TripletDePosition(courant, droite, enBasDroite));
            }
            if ((peutPlacerTuile(courant.ligne(), courant.colonne(), droite.ligne(), droite.colonne(), enHautDroite.ligne(), enHautDroite.colonne()))==0) {
                triplets.add(new TripletDePosition(courant, droite, enHautDroite));
            }
        }
        tripletsPossible.addAll(triplets);
    }


    public void metAjourPositionsLibres(ArrayList<Position> listeVoisins){
        ArrayList<Position> aSupprimer = new ArrayList<>();
        ArrayList<TripletDePosition> tripletsaSupprimer = new ArrayList<>();
        for(Position p : listeVoisins){
            //si p est dans positions_libres et n'est pas de l'eau, on l'enl?ve
            if(!estHexagoneVide(p.ligne(), p.colonne())) {
                aSupprimer.add(p);
            }
        }
        //System.out.println("Pour ?tre sur taille AVANT: "+listeVoisins.size());

        for(Position p : aSupprimer){
            listeVoisins.remove(p);
            for(TripletDePosition t : tripletsPossible){
                if(((t.getVolcan().ligne()==p.ligne() && t.getVolcan().colonne()==p.colonne())||(t.getTile1().ligne()==p.ligne() && t.getTile1().colonne()==p.colonne())||(t.getTile2().ligne()==p.ligne() && t.getTile2().colonne()==p.colonne()))
                        ||!estHexagoneVide(t.getVolcan().ligne(),t.getVolcan().colonne())||!estHexagoneVide(t.getTile1().ligne(),t.getTile1().colonne())||!estHexagoneVide(t.getTile2().ligne(),t.getTile2().colonne())){
                    tripletsaSupprimer.add(t);
                }
            }
        }
        //System.out.println("Pour ?tre sur taille APRES: "+listeVoisins.size());
        for(TripletDePosition t : tripletsaSupprimer){
            tripletsPossible.remove(t);
        }
        positions_libres.addAll(listeVoisins);
    }


    public boolean estCaseHorsPlateau(int l, int c){
        return l < 0 || l >= LIGNES || c < 0 || c >= COLONNES;
    }

    public boolean estHexagoneVide(int l, int c){
        if(estCaseHorsPlateau(l,c)){
            return false;
        }
        if(carte[l][c].getBiomeTerrain()==Hexagone.VIDE){
            return true;
        }
        return carte[l][c].getBiomeTerrain() == Hexagone.WATER;
    }
    public boolean estVolcan(int l, int c){
        return carte[l][c].getBiomeTerrain() == Hexagone.VOLCAN;
    }

    public boolean aPourVolcan(int hexagone_c, int hexagon_l, int volcan_c, int volcan_l) {
        return carte[hexagone_c][hexagon_l].getColonneVolcan() == volcan_c &&  carte[hexagone_c][hexagon_l].getLigneVolcan() == volcan_l;
    }

    public void joueCoup(Coup coup) {
        byte num_joueur = coup.getNumJoueur();
        int hauteur = carte[coup.volcanLigne][coup.volcanColonne].getHauteur();
        if (coup.typePlacement == Coup.TUILE) {
            coup.oldTerrain1=carte[coup.tile1Ligne][coup.tile1Colonne].getBiomeTerrain();
            coup.oldTerrain2=carte[coup.tile2Ligne][coup.tile2Colonne].getBiomeTerrain();
            carte[coup.volcanLigne][coup.volcanColonne] = new Hexagone((byte) (hauteur + 1), Hexagone.VOLCAN, (byte)coup.volcanLigne, (byte)coup.volcanColonne, carte[coup.volcanLigne][coup.volcanColonne].getNum());
            carte[coup.tile1Ligne][coup.tile1Colonne] = new Hexagone((byte) (hauteur + 1), coup.biome1, (byte)coup.volcanLigne, (byte)coup.volcanColonne, carte[coup.tile1Ligne][coup.tile1Colonne].getNum());
            carte[coup.tile2Ligne][coup.tile2Colonne] = new Hexagone((byte) (hauteur + 1), coup.biome2, (byte)coup.volcanLigne, (byte)coup.volcanColonne, carte[coup.tile2Ligne][coup.tile2Colonne].getNum());
            carte[coup.volcanLigne][coup.volcanColonne].resetBatHexagone();
            carte[coup.tile1Ligne][coup.tile1Colonne].resetBatHexagone();
            carte[coup.tile2Ligne][coup.tile2Colonne].resetBatHexagone();

            // On ajoute les emplacements libres des batiments
            positions_libres_batiments.add(new Position(coup.tile1Ligne,coup.tile1Colonne));
            positions_libres_batiments.add(new Position(coup.tile2Ligne,coup.tile2Colonne));
            // On ajoute les emplacements libres des tuiles
            ArrayList<Position> listeVoisins = voisins(coup.volcanLigne,coup.volcanColonne);
            metAjourPositionsLibres(listeVoisins);
            listeVoisins = voisins(coup.tile1Ligne,coup.tile1Colonne);
            metAjourPositionsLibres(listeVoisins);
            listeVoisins = voisins(coup.tile2Ligne,coup.tile2Colonne);
            metAjourPositionsLibres(listeVoisins);
            creerTriplets(positions_libres);
            if(coup.typePlacement!=4) {
                historique.ajoute(coup);
            }

        } else if (coup.typePlacement == Coup.BATIMENT || coup.typePlacement == 2 || coup.typePlacement == 3 || coup.typePlacement == 4){
            hauteur = carte[coup.batimentLigne][coup.batimentColonne].getHauteur();
            byte batiment = 0;
            if (coup.typePlacement == 1) {
                batiment = Hexagone.HUTTE;
            } else if (coup.typePlacement == 2) {
                batiment = TEMPLE;
            } else if (coup.typePlacement == 3) {
                batiment = Hexagone.TOUR;
            } else if (coup.typePlacement == 4){
                batiment = Hexagone.CHOISIR_BATIMENT;
            }
            if(batiment!=Hexagone.CHOISIR_BATIMENT){
                Position aSupprimer = new Position(coup.batimentLigne,coup.batimentColonne);
                positions_libres_batiments.remove(aSupprimer);
            }

            carte[coup.batimentLigne][coup.batimentColonne] = new Hexagone(num_joueur, (byte) hauteur, carte[coup.batimentLigne][coup.batimentColonne].getBiomeTerrain(), batiment, (byte) carte[coup.batimentLigne][coup.batimentColonne].getLigneVolcan(), (byte) carte[coup.batimentLigne][coup.batimentColonne].getColonneVolcan());
            if(coup.typePlacement!=4){
                historique.ajoute(coup);
            }

        }
    }

    public ArrayList<Point2D> previsualisePropagation(int hutteX, int hutteY,byte joueurCourant){
        ArrayList<Point2D> nlh ;
        return propagation(hutteX,hutteY,joueurCourant);
    }


    // Nécessite un appel à peutPlacerEtage
    public void placeEtage(byte joueurCourant, int volcanLigne, int volcanColonne, int tile1Ligne, int tile1Colonne, byte biome1, int tile2Ligne, int tile2Colonne, byte biome2) {
        Coup coup = new Coup(joueurCourant, volcanLigne, volcanColonne, tile1Ligne, tile1Colonne, biome1, tile2Ligne, tile2Colonne, biome2);
        //historique.ajoute(coup);
        joueCoup(coup);
    }

    public boolean peutPlacerMaison(int ligne,int colonne){
        return (carte[ligne][colonne].getBiomeTerrain()!=Hexagone.VOLCAN && carte[ligne][colonne].getBatiment()==VIDE && carte[ligne][colonne].getBiomeTerrain()!=Hexagone.VIDE);
    }
    public void placeBatiment(byte joueurCourant, int ligne, int colonne, byte type_bat){
        Coup coup = new Coup(joueurCourant, ligne,colonne,type_bat);
        //todo historique.ajoute(coup);
        joueCoup(coup);

        if (type_bat == (byte)1){
            nbHutteDisponiblesJoueur -=getHauteurTuile(ligne,colonne);
            ArrayList<Point2D> nlh ;
            nlh = propagation(ligne,colonne,joueurCourant);
            while(nlh.size()!=0) {
                Point2D a = nlh.remove(0);
                Coup Coup_propagation = new Coup(joueurCourant,a.x,a.y,(byte)1);
                if(nbHutteDisponiblesJoueur >=getHauteurTuile(a.x,a.y) && nbHutteDisponiblesJoueur !=0){
                    if(coup.typePlacement!=4) {
                        historique.ajoute(Coup_propagation);
                        joueCoup(Coup_propagation);
                        nbHutteDisponiblesJoueur -=(getHauteurTuile(a.x,a.y));
                    }
                }
            }
        }
    }

    public boolean notIn (ArrayList<Point2D> lp, Point2D p){
        for (int i =0;i<lp.size();i++){
            if(lp.get(i).PionsEquals(p)){
                return false;
            }
        }
        return true;
    }
    public boolean check (int x, int y,int IDjoueurs) {
        if(!estDansPlateau(x,y)){
            System.out.println("pas dans le plateau : "+x+" "+y);
        }
        return getTuile(x,y).getNumJoueur()==IDjoueurs && (getTuile(x,y).getBatiment()==HUTTE||estTemple(x,y)||estTour(x,y));
    }
    public boolean check2 (int x, int y,byte TypeTerrain) {
        return estDansPlateau(x, y) && getTuile(x,y).getBatiment()==(byte)0&&getTuile(x,y).getBiomeTerrain()==TypeTerrain;
    }
    public boolean estDansPlateau (int x , int y ){
        return (x<COLONNES)&&(x>-1)&&(y>-1)&&(y<LIGNES);
    }
    public ArrayList<Point2D> propagation (int hutteX, int hutteY, byte joueurCourant ){
        ArrayList<Point2D> listeDecases = new ArrayList<>();
        byte biome = getTuile(hutteX,hutteY).getBiomeTerrain();
        ArrayList<Point2D> listeDesHutesVoisines = new ArrayList<>();
        Point2D positionHutte = new Point2D(hutteX,hutteY);
        listeDesHutesVoisines.add(positionHutte);
        int i = 0;
        while (listeDesHutesVoisines.size()!=i){
            Point2D HuteCourant = listeDesHutesVoisines.get(i);
            if(check (HuteCourant.x-1 ,HuteCourant.y, joueurCourant)){
                Point2D p1 = new Point2D(HuteCourant.x-1 ,HuteCourant.y);
                if(notIn(listeDesHutesVoisines,p1))
                    listeDesHutesVoisines.add(p1);
            }
            if(check (HuteCourant.x+1 ,HuteCourant.y, joueurCourant)){
                Point2D p1 = new Point2D(HuteCourant.x+1,HuteCourant.y);
                if(notIn(listeDesHutesVoisines,p1))
                    listeDesHutesVoisines.add(p1);
            }
            if(check (HuteCourant.x ,HuteCourant.y-1, joueurCourant)){
                Point2D p1 = new Point2D(HuteCourant.x ,HuteCourant.y-1);
                if(notIn(listeDesHutesVoisines,p1))
                    listeDesHutesVoisines.add(p1);

            }
            if(check(HuteCourant.x ,HuteCourant.y+1, joueurCourant)){
                Point2D p1 = new Point2D(HuteCourant.x ,HuteCourant.y+1);
                if(notIn(listeDesHutesVoisines,p1))
                    listeDesHutesVoisines.add(p1);
            }
            if(HuteCourant.x%2==1){
                if(check (HuteCourant.x-1 ,HuteCourant.y-1, joueurCourant)){
                    Point2D p1 = new Point2D(HuteCourant.x-1,HuteCourant.y-1);
                    if(notIn(listeDesHutesVoisines,p1))
                        listeDesHutesVoisines.add(p1);
                }
                if(check (HuteCourant.x+1 ,HuteCourant.y-1, joueurCourant)){
                    Point2D p1 = new Point2D(HuteCourant.x+1 ,HuteCourant.y-1);
                    if(notIn(listeDesHutesVoisines,p1))
                        listeDesHutesVoisines.add(p1);

                }
            }else{
                if(check (HuteCourant.x-1 ,HuteCourant.y+1, joueurCourant)){
                    Point2D p1 = new Point2D(HuteCourant.x-1 ,HuteCourant.y+1);
                    if(notIn(listeDesHutesVoisines,p1))
                        listeDesHutesVoisines.add(p1);

                }
                if(check (HuteCourant.x+1 ,HuteCourant.y+1, joueurCourant)){
                    Point2D p1 = new Point2D(HuteCourant.x+1,HuteCourant.y+1);
                    if(notIn(listeDesHutesVoisines,p1))
                        listeDesHutesVoisines.add(p1);
                }
            }
            i++;
        }
        listeDesHutesVoisines.remove(0);
        i=0;
        while (listeDesHutesVoisines.size()!=i){
            Point2D HuteCourant = listeDesHutesVoisines.get(i);
            if(check2 (HuteCourant.x-1 ,HuteCourant.y,biome)){
                Point2D p1 = new Point2D(HuteCourant.x-1 ,HuteCourant.y);
                if(notIn(listeDecases,p1))
                    listeDecases.add(p1);
            }
            if(check2 (HuteCourant.x ,HuteCourant.y+1,biome)){
                Point2D p1 = new Point2D(HuteCourant.x ,HuteCourant.y+1);
                if(notIn(listeDecases,p1))
                    listeDecases.add(p1);
            }
            if(check2 (HuteCourant.x+1 ,HuteCourant.y,biome)){
                Point2D p1 = new Point2D(HuteCourant.x+1 ,HuteCourant.y);
                if(notIn(listeDecases,p1))
                    listeDecases.add(p1);

            }
            if(check2(HuteCourant.x ,HuteCourant.y-1,biome)){
                Point2D p1 = new Point2D(HuteCourant.x ,HuteCourant.y-1);
                if(notIn(listeDecases,p1))
                    listeDecases.add(p1);
            }
            if(HuteCourant.x%2==1){
                if(check2 (HuteCourant.x-1 ,HuteCourant.y-1,biome)){
                    Point2D p1 = new Point2D(HuteCourant.x-1,HuteCourant.y-1);
                    if(notIn(listeDecases,p1))
                        listeDecases.add(p1);
                }
                if(check2 (HuteCourant.x+1 ,HuteCourant.y-1,biome)){
                    Point2D p1 = new Point2D(HuteCourant.x+1 ,HuteCourant.y-1);
                    if(notIn(listeDecases,p1))
                        listeDecases.add(p1);

                }
            }else{
                if(check2 (HuteCourant.x-1 ,HuteCourant.y+1,biome)){
                    Point2D p1 = new Point2D(HuteCourant.x-1 ,HuteCourant.y+1);
                    if(notIn(listeDecases,p1))
                        listeDecases.add(p1);

                }
                if(check2 (HuteCourant.x+1 ,HuteCourant.y+1,biome)){
                    Point2D p1 = new Point2D(HuteCourant.x+1,HuteCourant.y+1);
                    if(notIn(listeDecases,p1))
                        listeDecases.add(p1);
                }
            }
            i++;
        }
        i=0;
        while(i<listeDecases.size()){
            i++;
        }
        return listeDecases;
    }

    public int getBatiment(int i,int j){
        return carte[i][j].getBatiment();
    }

    public boolean peutPoserTemple(int i,int j,byte numJoueur){
        // CAS CLASSIQUE (ON RENVOIE VRAI SI AUCUN TEMPLE ET VILLAGE ASSEZ GRAND)
        boolean PeutClassique = true;
        ArrayList<Point2D> pointsVillage = positionsBatsVillage(i,j,numJoueur);
        if(pointsVillage.size()<=3) PeutClassique =  false;                             // On verifie que la hauteur est d'au moins 3
        for(Point2D p : pointsVillage){                                                 // On verifie que la cité ne possède pas déjà une tour
            if(estTemple(p.getPointX(),p.getPointY())) PeutClassique =  false;
        }
        if(PeutClassique && aCiteAutour(i,j,numJoueur)) return true;

        // CAS POUR GERER ISOLATION DE TEMPLE
        ArrayList<ArrayList<Point2D>> Villages = getTousLesVillagesVoisins(i,j,numJoueur);
        boolean[] peut = new boolean[Villages.size()];
        for(int k=0;k<Villages.size();k++){
            peut[k]=true;
            ArrayList<Point2D> pointsVillageCourant = Villages.get(k);
            if(pointsVillageCourant.size()<=3){
                peut[k]=false;
            }
            for(Point2D p : pointsVillageCourant){
                if(p==null) peut[k]=false;
                else if(estTemple(p.getPointX(),p.getPointY())) peut[k]=false;
            }
            if(peut[k] && aCiteAutour(i,j,numJoueur)) return true;
        }
        return false;
    }

    public boolean peutPoserTour(int i,int j,byte numJoueur){
        // CAS CLASSIQUE (ON RENVOIE VRAI SI AUCUNE TOUR ET HAUTEUR>=3)
        boolean PeutClassique = true;
        ArrayList<Point2D> pointsVillage = positionsBatsVillage(i,j,numJoueur);
        if(getHauteurTuile(i,j)<3) PeutClassique = false;                            // On verifie que la hauteur est d'au moins 3
        for(Point2D p : pointsVillage){                                              // On verifie que la cité ne possède pas déjà une tour
            if(estTour(p.getPointX(),p.getPointY())) PeutClassique = false;
        }
        if(PeutClassique && aCiteAutour(i,j,numJoueur)) return true;

        // CAS POUR GERER ISOLATION DE TOUR
        ArrayList<ArrayList<Point2D>> Villages = getTousLesVillagesVoisins(i,j,numJoueur);
        boolean[] peut = new boolean[Villages.size()];
        for(int k=0;k<Villages.size();k++){
            peut[k]=true;
            ArrayList<Point2D> pointsVillageCourant = Villages.get(k);
            if(getHauteurTuile(i,j)<3) peut[k]=false;
            else{
                for(Point2D p : pointsVillageCourant){
                    if(p==null) peut[k]=false;
                    else if(estTour(p.getPointX(),p.getPointY())) peut[k]=false;
                }
            }
            if(peut[k] && aCiteAutour(i,j,numJoueur)) return true;
        }
        return false;
    }

    // TOUJOURS verifier qu'il reste le batiment dans l'inventaire du joueur avant de la poser
    public int[] getBatimentPlacable(int i,int j, byte numJoueur){
        int[] coups = new int[3];
        if(getBatiment(i,j)!=0 && getBatiment(i,j)!=CHOISIR_BATIMENT) return coups; // S'il y a deja un batiment, ce n'est pas construisible
        if(getHauteurTuile(i,j)>0) coups[1] = 1;
        if((getHauteurTuile(i,j)>1 && !aCiteAutour(i,j,numJoueur))) coups[1] = 0;  // Peut pas placer hutte a une hauteur > 1 s'il n'y pas de hutte à côté OU plus de hutte dans l'inventaire
        if(peutPoserTour(i,j,numJoueur)) coups[2] = 1;
        if(peutPoserTemple(i,j,numJoueur)) coups[0] = 1;
        return coups;
    }

    private boolean possedeBatiment(int i,int j,int numJoueur){
        return (getBatiment(i,j) != 0 && getTuile(i,j).getNumJoueur()==numJoueur);
    }

    public boolean aCiteAutour(int i,int j,int numJoueur){
        boolean bool = possedeBatiment(i-1,j,numJoueur)||possedeBatiment(i+1,j,numJoueur)||possedeBatiment(i,j-1,numJoueur)||possedeBatiment(i,j+1,numJoueur);
        if(i%2==1){
            if(possedeBatiment(i-1,j-1,numJoueur)) {
                bool = true;
            }
            if(possedeBatiment(i+1,j-1,numJoueur)) {
                bool = true;
            }
        }else{
            if(possedeBatiment(i-1,j+1,numJoueur)) {
                bool = true;
            }
            if(possedeBatiment(i+1,j+1,numJoueur)) {
                bool = true;
            }
        }
        return bool;
    }


    public int getHauteurTuile(int i,int j){
        return carte[i][j].getHauteur();
    }

    public Hexagone getTuile(int i, int j){
        return carte[i][j];
    }
    public void joueHexagone(){}

    public void resetHistorique(){
        initHistorique();
    }

    public boolean peutAnnuler() {
        return historique.peutAnnuler();
    }

    public boolean peutRefaire() {
        return historique.peutRefaire();
    }

    public boolean estVide(){
        for (Hexagone[] hexagones : carte) {
            for (int j = 0; j < carte[0].length; j++) {
                if (hexagones[j].getBiomeTerrain() != Hexagone.VIDE && hexagones[j].getBiomeTerrain() != Hexagone.WATER)
                    return false;
            }
        }
        return true;
    }

    public void supprimePosBatiment(Position pos){
        positions_libres_batiments.remove(pos);
    }
    public ArrayList<Position> getPositions_libres_batiments(){
        return positions_libres_batiments;
    }

    public void supprimeLibreBatiments(Position pos){
        positions_libres_batiments.remove(pos);
    }

    public void supprimeElementNew(Position posASupprimer){
        ArrayList<Position> positionsASupprimer = new ArrayList<>();
        for(Position posCourante: positions_libres_batiments){
            if(posCourante.ligne()==posASupprimer.ligne() && posCourante.colonne()==posASupprimer.colonne()) positionsASupprimer.add(posCourante);
        }
        for(Position posCourante: positionsASupprimer){
            positions_libres_batiments.remove(posCourante);
        }
    }


    public Stock annuler() {
        Stock stock =historique.annuler(carte);
        return stock;
    }

    public Stock refaire() {
        Stock stock = historique.refaire(carte);
        return stock;
    }

    public ArrayList<TripletDePosition> getTripletsPossibles(){
        return tripletsPossible;
    }

    public Hexagone[][] copyPlateauMap(Hexagone[][] plateau) {
        Hexagone[][] nouveauPlateau = new Hexagone[plateau.length][plateau[0].length];
        for (int i = 0; i < plateau.length; i++) {
            for (int j = 0; j < plateau[0].length; j++) {
                nouveauPlateau[i][j] = plateau[i][j].copy();
            }
        }
        return nouveauPlateau;
    }

    private byte[][] copyPions() {
        byte[][] nbPions = new byte[2][3];

        System.arraycopy(this.quantitePionJoueur1, 0, nbPions[0], 0, this.quantitePionJoueur1.length);
        System.arraycopy(this.quantitePionJoueur2, 0, nbPions[1], 0, this.quantitePionJoueur2.length);
        return nbPions;
    }

    private ArrayList<Position> copyPositionsLibres() {
        ArrayList<Position> positions_libres = new ArrayList<>();
        for (Position positions_libre : this.positions_libres) {
            positions_libres.add(positions_libre.copy());
        }
        return positions_libres;
    }

    private ArrayList<Position> copyPositionsLibresBatiment() {
        ArrayList<Position> positions_libres_batiments = new ArrayList<>();
        for (Position positions_libres_batiment : this.positions_libres_batiments) {
            positions_libres_batiments.add(positions_libres_batiment.copy());
        }
        return positions_libres_batiments;
    }

    private ArrayList<TripletDePosition> copyTripletPossibles() {
        ArrayList<TripletDePosition> tripletsPossible = new ArrayList<>();
        for (TripletDePosition tripletDePosition : this.tripletsPossible) {
            tripletsPossible.add(tripletDePosition.copy());
        }
        return tripletsPossible;
    }

    public int getLIGNES() {
        return LIGNES;
    }
    public int getCOLONNES(){
        return COLONNES;
    }
}