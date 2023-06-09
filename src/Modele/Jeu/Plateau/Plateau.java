package Modele.Jeu.Plateau;

import Modele.Jeu.Historique;
import Modele.Jeu.Jeu;
import Modele.Jeu.Stock;
import Modele.Jeu.Coup;
import Structures.Position.Point2D;
import Structures.Position.Position;
import Structures.Position.TripletDePosition;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

import static Modele.Jeu.Jeu.annule;
import static Modele.Jeu.Plateau.Hexagone.*;
import static Vue.FenetreJeu.jeu;

public class Plateau implements Serializable, Cloneable {
    final int LIGNES = 60;
    final int COLONNES = 60;
    protected Hexagone[][] carte;

    public int nbHuttesDisponiblesJoueur = 0; // Pour eviter d'aller dans le negatif lors de la propagation
    private ArrayList<Position> positions_libres;

    private ArrayList<TripletDePosition> tripletsPossible;
    private ArrayList<TripletDePosition> joueurTripletsPossible;
    private ArrayList<Position> positions_libres_batiments;

    public int nbTuilePlacee = 0;

    public Plateau(){
        initPlateau();
        //initQuantitePions();
        initPlateau();
        initPositionsLibres();
        initTripletsPossibles();
    }

    public Plateau copie() {
        Plateau p = new Plateau();
        p.nbHuttesDisponiblesJoueur = this.nbHuttesDisponiblesJoueur;

        p.positions_libres = new ArrayList<>(this.positions_libres);
        p.positions_libres_batiments = new ArrayList<>(this.positions_libres_batiments);
        p.tripletsPossible = new ArrayList<>(this.tripletsPossible);

        p.carte = new Hexagone[LIGNES][COLONNES];
        for (int i = 0; i < LIGNES; i++) {
            System.arraycopy(this.carte[i], 0, p.carte[i], 0, COLONNES);
        }

        return p;
    }

    public void resetPlacable(){
        for(int i=0; i<LIGNES;i++){
            for(int j=0; j<COLONNES;j++){
                carte[i][j].placable = false;
            }
        }
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void initTripletsPossibles() {
        tripletsPossible = new ArrayList<>();
        TripletDePosition tripletDeBase1 = new TripletDePosition(new Position(30,28),new Position(30,29),new Position(31,29));
        TripletDePosition tripletDeBase2 = new TripletDePosition(new Position(30,29),new Position(31,30),new Position(31,29));
        TripletDePosition tripletDeBase3 = new TripletDePosition(new Position(31,29),new Position(31,30),new Position(32,29));
        TripletDePosition tripletDeBase4 = new TripletDePosition(new Position(31,29),new Position(32,29),new Position(32,28));
        TripletDePosition tripletDeBase5 = new TripletDePosition(new Position(31,28),new Position(31,29),new Position(32,28));
        TripletDePosition tripletDeBase6 = new TripletDePosition(new Position(30,28),new Position(31,29),new Position(31,28));
        tripletsPossible.add(tripletDeBase1);
        tripletsPossible.add(tripletDeBase2);
        tripletsPossible.add(tripletDeBase3);
        tripletsPossible.add(tripletDeBase4);
        tripletsPossible.add(tripletDeBase5);
        tripletsPossible.add(tripletDeBase6);
    }

    private void initPositionsLibres() {
        positions_libres = new ArrayList<>();
        positions_libres_batiments = new ArrayList<>();
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

    public boolean estDansTripletsPossibles(int ligneVolcan, int colonneVolcan, int ligneTile1, int colonneTile1, int ligneTile2, int colonneTile2) {
        for(TripletDePosition triplet : tripletsPossible){
            // Attention le Point X des triplets correspond toujours au volcan !!
            if (peutPlacerTuileFromTriplet(ligneVolcan, colonneVolcan, ligneTile1, colonneTile1, ligneTile2, colonneTile2, triplet))
                return true;
        }
        return false;
    }

    public boolean estDansJoueurTripletsPossibles(int ligneVolcan, int colonneVolcan, int ligneTile1, int colonneTile1, int ligneTile2, int colonneTile2) {
        for(TripletDePosition triplet : joueurTripletsPossible){
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
    public ArrayList<Point2D> positionsBatsVillage(int x, int y, Color color_joueur){
        ArrayList<Point2D> listeDesHutesVoisines = new ArrayList<>();
        Point2D positionHutte = new Point2D(x,y);
        listeDesHutesVoisines.add(positionHutte);

        int i = 0;
        while (listeDesHutesVoisines.size()!=i){
            Point2D HuteCourant = listeDesHutesVoisines.get(i);
            ajouterHuttesVoisines(color_joueur, listeDesHutesVoisines, HuteCourant);
            i++;
        }
        return listeDesHutesVoisines;
    }

    public ArrayList<Point2D> positionsBatsVillage2(int x, int y, Color color_joueur){
        ArrayList<Point2D> listeDesHutesVoisines = new ArrayList<>();
        Point2D positionHutte = new Point2D(x,y);
        //listeDesHutesVoisines.add(positionHutte);

        int i = 0;
        while (listeDesHutesVoisines.size()!=i){
            Point2D HuteCourant = listeDesHutesVoisines.get(i);
            ajouterHuttesVoisines(color_joueur, listeDesHutesVoisines, HuteCourant);
            i++;
        }
        return listeDesHutesVoisines;
    }

    private boolean estBatiment(int i,int j){
        return carte[i][j].getBatiment()==HUTTE||carte[i][j].getBatiment()==TEMPLE||carte[i][j].getBatiment()==TOUR;
    }
    public ArrayList<ArrayList<Point2D>> getTousLesVillagesVoisins(int x, int y, Color color_joueur){
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
        if(estBatiment(x-1,y) && carte[x-1][y].getColorJoueur()==color_joueur) positionHutte_1 = new Point2D(x-1,y);
        if(estBatiment(x+1,y) && carte[x+1][y].getColorJoueur()==color_joueur) positionHutte_2 = new Point2D(x+1,y);
        if(estBatiment(x,y-1) && carte[x][y-1].getColorJoueur()==color_joueur) positionHutte_3 = new Point2D(x,y-1);
        if(estBatiment(x,y+1) && carte[x][y+1].getColorJoueur()==color_joueur) positionHutte_4 = new Point2D(x,y+1);

        if(x%2==1){
            if(estBatiment(x-1,y-1) && carte[x-1][y-1].getColorJoueur()==color_joueur) positionHutte_5 = new Point2D(x-1,y-1);
            if(estBatiment(x+1,y-1) && carte[x+1][y-1].getColorJoueur()==color_joueur) positionHutte_6 = new Point2D(x+1,y-1);
        }else{
            if(estBatiment(x-1,y+1) && carte[x-1][y+1].getColorJoueur()==color_joueur) positionHutte_5 = new Point2D(x-1,y+1);
            if(estBatiment(x+1,y+1) && carte[x+1][y+1].getColorJoueur()==color_joueur) positionHutte_6 = new Point2D(x+1,y+1);
        }

        listeDesHutesVoisines_1.add(positionHutte_1);
        listeDesHutesVoisines_2.add(positionHutte_2);
        listeDesHutesVoisines_3.add(positionHutte_3);
        listeDesHutesVoisines_4.add(positionHutte_4);
        listeDesHutesVoisines_5.add(positionHutte_5);
        listeDesHutesVoisines_6.add(positionHutte_6);

        listeDesHutesVoisines_1 = positionBatVillageCourant(listeDesHutesVoisines_1,color_joueur);
        listeDesHutesVoisines_2 = positionBatVillageCourant(listeDesHutesVoisines_2,color_joueur);
        listeDesHutesVoisines_3 = positionBatVillageCourant(listeDesHutesVoisines_3,color_joueur);
        listeDesHutesVoisines_4 = positionBatVillageCourant(listeDesHutesVoisines_4,color_joueur);
        listeDesHutesVoisines_5 = positionBatVillageCourant(listeDesHutesVoisines_5,color_joueur);
        listeDesHutesVoisines_6 = positionBatVillageCourant(listeDesHutesVoisines_6,color_joueur);

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
    public ArrayList<Point2D> positionBatVillageCourant(ArrayList<Point2D> listeDesHutesVoisines, Color color_joueur){
        int i = 0;
        while (listeDesHutesVoisines.size()!=i){
            Point2D HuteCourant = listeDesHutesVoisines.get(i);
            if(HuteCourant!=null) ajouterHuttesVoisines(color_joueur, listeDesHutesVoisines, HuteCourant);
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


    private void ajouterHuttesVoisines(Color color_joueur, ArrayList<Point2D> listeDesHutesVoisines, Point2D HuteCourant) {
        //System.out.println("IDJOUEUR: "+idjoueur);
        if(check(HuteCourant.getPointX()-1 , HuteCourant.getPointY(), color_joueur)){
            Point2D p1 = new Point2D(HuteCourant.getPointX()-1 , HuteCourant.getPointY());
            if(notIn(listeDesHutesVoisines,p1))
                listeDesHutesVoisines.add(p1);
        }
        if(check(HuteCourant.getPointX()+1 , HuteCourant.getPointY(), color_joueur)){
            Point2D p1 = new Point2D(HuteCourant.getPointX()+1 , HuteCourant.getPointY());
            if(notIn(listeDesHutesVoisines,p1))
                listeDesHutesVoisines.add(p1);
        }
        if(check(HuteCourant.getPointX() , HuteCourant.getPointY()-1, color_joueur)){
            Point2D p1 = new Point2D(HuteCourant.getPointX() , HuteCourant.getPointY()-1);
            if(notIn(listeDesHutesVoisines,p1))
                listeDesHutesVoisines.add(p1);
        }
        if(check(HuteCourant.getPointX() , HuteCourant.getPointY()+1, color_joueur)){
            Point2D p1 = new Point2D(HuteCourant.getPointX() , HuteCourant.getPointY()+1);
            if(notIn(listeDesHutesVoisines,p1))
                listeDesHutesVoisines.add(p1);
        }
        if(HuteCourant.getPointX()%2==1){
            if(check(HuteCourant.getPointX()-1 , HuteCourant.getPointY()-1, color_joueur)){
                Point2D p1 = new Point2D(HuteCourant.getPointX()-1 , HuteCourant.getPointY()-1);
                if(notIn(listeDesHutesVoisines,p1))
                    listeDesHutesVoisines.add(p1);
            }
            if(check(HuteCourant.getPointX()+1 , HuteCourant.getPointY()-1, color_joueur)){
                Point2D p1 = new Point2D(HuteCourant.getPointX()+1 , HuteCourant.getPointY()-1);
                if(notIn(listeDesHutesVoisines,p1))
                    listeDesHutesVoisines.add(p1);
            }
        }else{
            if(check(HuteCourant.getPointX()-1 , HuteCourant.getPointY()+1, color_joueur)){
                Point2D p1 = new Point2D(HuteCourant.getPointX()-1 , HuteCourant.getPointY()+1);
                if(notIn(listeDesHutesVoisines,p1))
                    listeDesHutesVoisines.add(p1);
            }
            if(check(HuteCourant.getPointX()+1 , HuteCourant.getPointY()+1, color_joueur)){
                Point2D p1 = new Point2D(HuteCourant.getPointX()+1 , HuteCourant.getPointY()+1);
                if(notIn(listeDesHutesVoisines,p1))
                    listeDesHutesVoisines.add(p1);
            }
        }
    }

    private boolean effaceUnVillageEntier(int ligneTile1, int colonneTile1, int ligneTile2, int colonneTile2){
        if(getBatiment(ligneTile1,colonneTile1)==HUTTE){                            // une hutte sur la premiere tuile
            Color colorBat_1 = getCarte()[ligneTile1][colonneTile1].getColorJoueur();
            if(getBatiment(ligneTile2,colonneTile2)==HUTTE){                        // sur la deuxieme aussi
                Color colorBat_2 = getCarte()[ligneTile2][colonneTile2].getColorJoueur();
                if(colorBat_1==colorBat_2){                                               // les 2 appartiennent au m�me joueur
                    return positionsBatsVillage(ligneTile1, colonneTile1, colorBat_1).size() <= 2 && positionsBatsVillage(ligneTile1, colonneTile1, colorBat_1).size() > 0; // on efface tout le village qui contient 2 huttes
                }else{                                                              // les 2 appartiennet � des joueurs differents
                    if(positionsBatsVillage(ligneTile1,colonneTile1,colorBat_1).size()<=1 && positionsBatsVillage(ligneTile1,colonneTile1,colorBat_1).size()>0) return true; // on efface tout le village de idBat_1 qui contient 1 hutte
                    return positionsBatsVillage(ligneTile2, colonneTile2, colorBat_2).size() <= 1 && positionsBatsVillage(ligneTile1, colonneTile1, colorBat_2).size() > 0; // on efface tout le village de idBat_2 qui contient 1 hutte
                }
            }else{ // il n'y a pas de batiment sur la deuxieme hutte
                return positionsBatsVillage(ligneTile1, colonneTile1, colorBat_1).size() <= 1 && positionsBatsVillage(ligneTile1, colonneTile1, colorBat_1).size() > 0; // on efface tout le village qui contient 1 hutte
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
        /*
        int nb_bat_1 = 0,nb_bat_2 = 0;
        if(carte[volcan_i][volcan_j].getBatiment()!=0 && carte[volcan_i][volcan_j].getColorJoueur() == 0) nb_bat_1++;
        if(carte[tile1_i][tile1_j].getBatiment()!=0 && carte[volcan_i][volcan_j].getColorJoueur() == 0) nb_bat_1++;
        if(carte[tile2_i][tile2_j].getBatiment()!=0  && carte[volcan_i][volcan_j].getColorJoueur() == 0) nb_bat_1++;

        if(carte[volcan_i][volcan_j].getBatiment()!=0  && carte[volcan_i][volcan_j].getColorJoueur() == 1) nb_bat_2++;
        if(carte[tile1_i][tile1_j].getBatiment()!=0  && carte[volcan_i][volcan_j].getColorJoueur() == 1) nb_bat_2++;
        if(carte[tile2_i][tile2_j].getBatiment()!=0  && carte[volcan_i][volcan_j].getColorJoueur() == 1) nb_bat_2++;

        int[] tab =  new int[2];
        tab[0] = nb_bat_1;
        tab[1] = nb_bat_2;
        return tab;
         */
        return null;
    }

    public int peutPlacerTuile(int ligneVolcan, int colonneVolcan, int ligneTile1, int colonneTile1, int ligneTile2, int colonneTile2) {    // renvoie 0 si OK
        // V�rifie qu'on ne pose pas au bord du plateau
        if (ligneVolcan < 2 || colonneVolcan < 2 || ligneVolcan >= carte.length-2 || colonneVolcan >= carte.length-2) {
            return 1;
        }
        if (ligneTile1 < 2 || colonneTile1 < 2 || ligneTile1 >= carte.length-2 || colonneTile1 >= carte.length-2) {
            return 1;
        }
        if (ligneTile2 < 2 || colonneTile2 < 2 || ligneTile2 >= carte.length-2 || colonneTile2 >= carte.length-2) {
            return 1;
        }

        // Premiere tuile pos�e
        if(estVide() && estDansTripletsPossibles(ligneVolcan,colonneVolcan,ligneTile1,colonneTile1,ligneTile2,colonneTile2)){
            return 0;
        }

        // Hauteur max
        int hauteur = carte[ligneVolcan][colonneVolcan].getHauteur();
        if (hauteur == 4) {
            return 2;
        }
        // V�rifie si on place un volcan sur un volcan
        if (carte[ligneVolcan][colonneVolcan].getBiomeTerrain() != Hexagone.VOLCAN && carte[ligneVolcan][colonneVolcan].getBiomeTerrain() != Hexagone.VIDE) {
            return 3;
        }
        // V�rifie qu'on ne place pas pile poil par dessus une autre tuile
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

        // V�rifie la hauteur de toutes les cases
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


    public void metAjourPositionsLibres(ArrayList<Position> listeVoisins) {
        for (int i = listeVoisins.size() - 1; i >= 0; i--) {
            Position p = listeVoisins.get(i);
            if (!estHexagoneVide(p.ligne(), p.colonne())) {
                listeVoisins.remove(i);
            }
        }

        for (int i = tripletsPossible.size() - 1; i >= 0; i--) {
            TripletDePosition t = tripletsPossible.get(i);
            Position volcano = t.getVolcan();
            Position tile1 = t.getTile1();
            Position tile2 = t.getTile2();

            if (!estHexagoneVide(volcano.ligne(), volcano.colonne())
                    || !estHexagoneVide(tile1.ligne(), tile1.colonne())
                    || !estHexagoneVide(tile2.ligne(), tile2.colonne())) {
                tripletsPossible.remove(i);
            }
        }

        positions_libres.addAll(listeVoisins);
    }


    public boolean estCaseHorsPlateau(int l, int c){
        return l < 0 || l >= LIGNES || c < 0 || c >= COLONNES;
    }

    public boolean estHexagoneVide(int l, int c) {
        if (estCaseHorsPlateau(l, c)) {
            return false;
        }

        byte terrain = carte[l][c].getBiomeTerrain();
        return terrain == Hexagone.VIDE || terrain == Hexagone.WATER;
    }
    public boolean estVolcan(int l, int c){
        return carte[l][c].getBiomeTerrain() == Hexagone.VOLCAN;
    }

    public boolean aPourVolcan(int hexagone_c, int hexagon_l, int volcan_c, int volcan_l) {
        return carte[hexagone_c][hexagon_l].getColonneVolcan() == volcan_c &&  carte[hexagone_c][hexagon_l].getLigneVolcan() == volcan_l;
    }

    public void joueCoup(Coup coup) {
        Color color_joueur = coup.getCouleurJoueur();
        int hauteur = carte[coup.volcanLigne][coup.volcanColonne].getHauteur();
        if (coup.typePlacement == Coup.TUILE) {
            nbTuilePlacee++;
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


        } else if (coup.typePlacement == Coup.SELECTEUR_BATIMENT || coup.typePlacement == Coup.HUTTE || coup.typePlacement == Coup.TOUR || coup.typePlacement == Coup.TEMPLE){
            hauteur = carte[coup.batimentLigne][coup.batimentColonne].getHauteur();
            byte batiment = 0;
            if (coup.typePlacement == Coup.HUTTE) {
                batiment = Hexagone.HUTTE;
            } else if (coup.typePlacement == Coup.TEMPLE) {
                batiment = TEMPLE;
            } else if (coup.typePlacement == Coup.TOUR) {
                batiment = Hexagone.TOUR;
            } else if (coup.typePlacement == Coup.SELECTEUR_BATIMENT){
                batiment = Hexagone.CHOISIR_BATIMENT;
            }
            if(batiment!=Hexagone.CHOISIR_BATIMENT){
                Position aSupprimer = new Position(coup.batimentLigne,coup.batimentColonne);
                positions_libres_batiments.remove(aSupprimer);
                supPoseVillageTripletsPossibles(new Position(coup.batimentLigne, coup.batimentColonne));
            }

            carte[coup.batimentLigne][coup.batimentColonne] = new Hexagone(color_joueur, (byte) hauteur, carte[coup.batimentLigne][coup.batimentColonne].getBiomeTerrain(), batiment, (byte) carte[coup.batimentLigne][coup.batimentColonne].getLigneVolcan(), (byte) carte[coup.batimentLigne][coup.batimentColonne].getColonneVolcan());
        }
        if (coup.typePlacement != Coup.SELECTEUR_BATIMENT) {
            jeu.historique.getPasse().add(coup);
        }
        if (!annule) {
            jeu.historique.futur.clear();
        }
    }

    private void supPoseVillageTripletsPossibles(Position posVillage){
        for(int i= 0;i< tripletsPossible.size();i++){
            if(tripletsPossible.get(i).contientPosition(posVillage)){
                tripletsPossible.remove(i);
                i--;
            }
        }
    }

    public ArrayList<Point2D> previsualisePropagation(int hutteX, int hutteY, Color color_joueur){
        ArrayList<Point2D> nlh ;
        return propagation(hutteX,hutteY,color_joueur);
    }


    // n�cessite un appel � peutPlacerEtage
    public void placeEtage(byte joueurCourant, int volcanLigne, int volcanColonne, int tile1Ligne, int tile1Colonne, byte biome1, int tile2Ligne, int tile2Colonne, byte biome2) {
        Coup coup = new Coup(joueurCourant, volcanLigne, volcanColonne, tile1Ligne, tile1Colonne, biome1, tile2Ligne, tile2Colonne, biome2);
        //historique.ajoute(coup);
        joueCoup(coup);
    }

    public boolean peutPlacerMaison(int ligne,int colonne){
        return (carte[ligne][colonne].getBiomeTerrain()!=Hexagone.VOLCAN && carte[ligne][colonne].getBatiment()==VIDE && carte[ligne][colonne].getBiomeTerrain()!=Hexagone.VIDE);
    }
    public void placeBatiment(byte joueurCourant, Color color_joueur, int ligne, int colonne, byte type_bat){
        Coup coup = new Coup(joueurCourant, color_joueur, ligne,colonne,type_bat);
        //todo historique.ajoute(coup);
        joueCoup(coup);

        if (type_bat == (byte)1){
            nbHuttesDisponiblesJoueur -=getHauteurTuile(ligne,colonne);
            ArrayList<Point2D> nlh ;
            nlh = propagation(ligne,colonne,color_joueur);
            while(nlh.size()!=0) {
                Point2D a = nlh.remove(0);
                Coup Coup_propagation = new Coup(joueurCourant, color_joueur, a.x, a.y, (byte)1);
                if(nbHuttesDisponiblesJoueur >=getHauteurTuile(a.x,a.y) && nbHuttesDisponiblesJoueur !=0){
                    if(coup.typePlacement!=4) {
                        joueCoup(Coup_propagation);
                        nbHuttesDisponiblesJoueur -=(getHauteurTuile(a.x,a.y));
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
    public boolean check (int x, int y, Color color_joueur) {
        return getHexagone(x,y).getColorJoueur()==color_joueur && (getHexagone(x,y).getBatiment()==HUTTE||estTemple(x,y)||estTour(x,y));
    }
    public boolean check2 (int x, int y,byte TypeTerrain) {
        return estDansPlateau(x, y) && getHexagone(x,y).getBatiment()==(byte)0&& getHexagone(x,y).getBiomeTerrain()==TypeTerrain;
    }
    public boolean estDansPlateau (int x , int y ){
        return (x<COLONNES)&&(x>-1)&&(y>-1)&&(y<LIGNES);
    }
    public ArrayList<Point2D> propagation (int hutteX, int hutteY, Color color_joueur ){
        ArrayList<Point2D> listeDecases = new ArrayList<>();
        byte biome = getHexagone(hutteX,hutteY).getBiomeTerrain();
        ArrayList<Point2D> listeDesHutesVoisines = new ArrayList<>();
        Point2D positionHutte = new Point2D(hutteX,hutteY);
        listeDesHutesVoisines.add(positionHutte);
        int i = 0;
        while (listeDesHutesVoisines.size()!=i){
            Point2D HuteCourant = listeDesHutesVoisines.get(i);
            if(check (HuteCourant.x-1 ,HuteCourant.y, color_joueur)){
                Point2D p1 = new Point2D(HuteCourant.x-1 ,HuteCourant.y);
                if(notIn(listeDesHutesVoisines,p1))
                    listeDesHutesVoisines.add(p1);
            }
            if(check (HuteCourant.x+1 ,HuteCourant.y, color_joueur)){
                Point2D p1 = new Point2D(HuteCourant.x+1,HuteCourant.y);
                if(notIn(listeDesHutesVoisines,p1))
                    listeDesHutesVoisines.add(p1);
            }
            if(check (HuteCourant.x ,HuteCourant.y-1, color_joueur)){
                Point2D p1 = new Point2D(HuteCourant.x ,HuteCourant.y-1);
                if(notIn(listeDesHutesVoisines,p1))
                    listeDesHutesVoisines.add(p1);

            }
            if(check(HuteCourant.x ,HuteCourant.y+1, color_joueur)){
                Point2D p1 = new Point2D(HuteCourant.x ,HuteCourant.y+1);
                if(notIn(listeDesHutesVoisines,p1))
                    listeDesHutesVoisines.add(p1);
            }
            if(HuteCourant.x%2==1){
                if(check (HuteCourant.x-1 ,HuteCourant.y-1, color_joueur)){
                    Point2D p1 = new Point2D(HuteCourant.x-1,HuteCourant.y-1);
                    if(notIn(listeDesHutesVoisines,p1))
                        listeDesHutesVoisines.add(p1);
                }
                if(check (HuteCourant.x+1 ,HuteCourant.y-1, color_joueur)){
                    Point2D p1 = new Point2D(HuteCourant.x+1 ,HuteCourant.y-1);
                    if(notIn(listeDesHutesVoisines,p1))
                        listeDesHutesVoisines.add(p1);

                }
            }else{
                if(check (HuteCourant.x-1 ,HuteCourant.y+1, color_joueur)){
                    Point2D p1 = new Point2D(HuteCourant.x-1 ,HuteCourant.y+1);
                    if(notIn(listeDesHutesVoisines,p1))
                        listeDesHutesVoisines.add(p1);

                }
                if(check (HuteCourant.x+1 ,HuteCourant.y+1, color_joueur)){
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

    public boolean peutPoserTemple(int i,int j,Color color_joueur){
        // CAS CLASSIQUE (ON RENVOIE VRAI SI AUCUN TEMPLE ET VILLAGE ASSEZ GRAND)
        boolean PeutClassique = true;
        ArrayList<Point2D> pointsVillage = positionsBatsVillage2(i,j,color_joueur);
        if(pointsVillage.size()<=3) PeutClassique =  false;                             // On verifie que la hauteur est d'au moins 3
        for(Point2D p : pointsVillage){                                                 // On verifie que la cit� ne poss�de pas d�j� une tour
            if(estTemple(p.getPointX(),p.getPointY())) PeutClassique =  false;
        }
        if(PeutClassique && aCiteAutour(i,j,color_joueur)) return true;

        // CAS POUR GERER ISOLATION DE TEMPLE
        ArrayList<ArrayList<Point2D>> Villages = getTousLesVillagesVoisins(i,j,color_joueur);
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
            if(peut[k] && aCiteAutour(i,j,color_joueur)) return true;
        }
        return false;
    }

    public boolean peutPoserTour(int i,int j,Color color_joueur){
        // CAS CLASSIQUE (ON RENVOIE VRAI SI AUCUNE TOUR ET HAUTEUR>=3)
        boolean PeutClassique = true;
        ArrayList<Point2D> pointsVillage = positionsBatsVillage2(i,j,color_joueur);
        if(getHauteurTuile(i,j)<3) PeutClassique = false;                            // On verifie que la hauteur est d'au moins 3
        for(Point2D p : pointsVillage){                                              // On verifie que la cit� ne poss�de pas d�j� une tour
            if(estTour(p.getPointX(),p.getPointY())) PeutClassique = false;
        }
        if(PeutClassique && aCiteAutour(i,j,color_joueur)) return true;

        // CAS POUR GERER ISOLATION DE TOUR
        ArrayList<ArrayList<Point2D>> Villages = getTousLesVillagesVoisins(i,j,color_joueur);
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
            if(peut[k] && aCiteAutour(i,j,color_joueur)) return true;
        }
        return false;
    }

    // TOUJOURS verifier qu'il reste le b�timent dans l'inventaire du joueur avant de la poser
    public int[] getBatimentPlacable(int i,int j, Color color_joueur){
        //coups[0] = 1 si on peut poser un temple, 0 sinon
        //coups[1] = 1 si on peut poser une hutte, 0 sinon
        //coups[2] = 1 si on peut poser une tour, 0 sinon
        int[] coups = new int[3];
        if(getHexagone(i,j).getBiomeTerrain()==VOLCAN) return coups;
        if(getBatiment(i,j)!=0 && getBatiment(i,j)!=CHOISIR_BATIMENT) return coups; // S'il y a deja un b�timent, ce n'est pas constructible
        if(getHauteurTuile(i,j)>0) coups[1] = 1;
        if((getHauteurTuile(i,j)>1 && !aCiteAutour(i,j,color_joueur))) coups[1] = 0;  // Peut pas placer hutte a une hauteur > 1 s'il n'y pas de hutte � c�t� OU plus de hutte dans l'inventaire
        if(peutPoserTour(i,j,color_joueur)) coups[2] = 1;
        if(peutPoserTemple(i,j,color_joueur)) coups[0] = 1;
        return coups;
    }

    private boolean possedeBatiment(int i,int j, Color color_joueur){
        return (getBatiment(i,j) != 0 && getHexagone(i,j).getColorJoueur()==color_joueur);
    }

    public boolean aCiteAutour(int i, int j, Color color_joueur){
        boolean bool = possedeBatiment(i-1,j,color_joueur)||possedeBatiment(i+1,j,color_joueur)||possedeBatiment(i,j-1,color_joueur)||possedeBatiment(i,j+1,color_joueur);
        if(i%2==1){
            if(possedeBatiment(i-1,j-1,color_joueur)) {
                bool = true;
            }
            if(possedeBatiment(i+1,j-1,color_joueur)) {
                bool = true;
            }
        }else{
            if(possedeBatiment(i-1,j+1,color_joueur)) {
                bool = true;
            }
            if(possedeBatiment(i+1,j+1,color_joueur)) {
                bool = true;
            }
        }
        return bool;
    }

    public ArrayList<Position> getCiteAutour(int i, int j, Color color_joueur){
        ArrayList<Position> citeAutour = new ArrayList<>();
        if(possedeBatiment(i-1,j,color_joueur)) citeAutour.add(new Position(i-1,j));
        if(possedeBatiment(i+1,j,color_joueur)) citeAutour.add(new Position(i+1,j));
        if(possedeBatiment(i,j-1,color_joueur)) citeAutour.add(new Position(i,j-1));
        if(possedeBatiment(i,j+1,color_joueur)) citeAutour.add(new Position(i,j+1));
        if(i%2==1){
            if(possedeBatiment(i-1,j-1,color_joueur)) {
                citeAutour.add(new Position(i-1,j-1));
            }
            if(possedeBatiment(i+1,j-1,color_joueur)) {
                citeAutour.add(new Position(i+1,j-1));
            }
        }else{
            if(possedeBatiment(i-1,j+1,color_joueur)) {
                citeAutour.add(new Position(i-1,j+1));
            }
            if(possedeBatiment(i+1,j+1,color_joueur)) {
                citeAutour.add(new Position(i+1,j+1));;
            }
        }
        return citeAutour;
    }


    public int getHauteurTuile(int i,int j){
        return carte[i][j].getHauteur();
    }

    public Hexagone getHexagone(int i, int j){
        return carte[i][j];
    }
    public void joueHexagone(){}







    public boolean estVide(){
        /*for (Hexagone[] hexagones : carte) {
            for (int j = 0; j < carte[0].length; j++) {
                if (hexagones[j].getBiomeTerrain() != Hexagone.VIDE && hexagones[j].getBiomeTerrain() != Hexagone.WATER)
                    return false;
            }
        }
        return true;*/
        return nbTuilePlacee==0;
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

    public void copiePlateau(Hexagone[][]carte){
        for(int i = 0 ; i<getCOLONNES();i++){
            for(int j =0 ;j<getLIGNES();j++){
                this.carte[i][j]=carte[i][j];
            }
        }
    }
}