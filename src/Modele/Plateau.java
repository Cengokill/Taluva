package Modele;

import Structures.Position;
import Structures.TripletDePosition;

import java.io.Serializable;
import java.util.ArrayList;

public class Plateau implements Serializable {
    final int LIGNES = 40;
    final int COLONNES = 40;
    public int quantiteBatimentJoueur1, quantiteBatimentJoueur2;
    protected Hexagone[][] plateau ;
    protected byte[] quantitePionJoueur1;
    protected byte[] quantitePionJoueur2;
    private Historique historique;
    private ArrayList<Position> positions_libres;

    private ArrayList<TripletDePosition> tripletsPossible;
    private ArrayList<Position> positions_libres_batiments;

    public Plateau(){
        initPlateau();
        initHistorique();
        initQuantitePions();
        initQuantiteBatiment();
        initPlateau();
        initPositionsLibres();
        initTripletsPossibles();
    }

    private void initHistorique() {
        historique = new Historique();
    }

    private void initTripletsPossibles() {
        tripletsPossible = new ArrayList<>();
    }

    private void initPositionsLibres() {
        positions_libres = new ArrayList<>();
        positions_libres_batiments = new ArrayList<>();
    }

    private void initQuantiteBatiment() {
        quantiteBatimentJoueur1 = 0;
        quantiteBatimentJoueur2 = 0;
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
        plateau = new Hexagone[LIGNES][COLONNES];
        remplirPlateau();
    }

    private void remplirPlateau() {
        for (int i = 0; i < plateau.length; i++) {
            for (int j = 0; j < plateau[0].length; j++) {
                plateau[i][j] = new Hexagone((byte)0, Hexagone.VIDE, (byte)19, (byte)20);
            }
        }
    }

    public Hexagone[][] getPlateau() {
        return plateau;
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
        return plateau[x1][y1].getHauteur() == plateau[x2][y2].getHauteur() && plateau[x1][y1].getHauteur() == plateau[x3][y3].getHauteur();
    }

    public boolean estHexagoneLibre(int ligne, int colonne){
        return plateau[ligne][colonne].getBiomeTerrain() <= 1;
    }

    public boolean estDansTripletsPossibles(int ligneVolcan, int colonneVolcan, int ligneTile1, int colonneTile1, int ligneTile2, int colonneTile2) {
        for(TripletDePosition triplet : tripletsPossible){
            // Attention le Point X des triplets correspond toujours au volcan !!
            if (peutPlacerTuileFromTriplet(ligneVolcan, colonneVolcan, ligneTile1, colonneTile1, ligneTile2, colonneTile2, triplet))
                return true;
        }
        return false;
    }

    private boolean peutPlacerTuileFromTriplet(int ligneVolcan, int colonneVolcan, int ligneTile1, int colonneTile1, int ligneTile2, int colonneTile2, TripletDePosition triplet) {
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
        if(plateau[volcan_i][volcan_j].getBatiment()!=0 && plateau[volcan_i][volcan_j].getNumJoueur() == 0) nb_bat_1++;
        if(plateau[tile1_i][tile1_j].getBatiment()!=0 && plateau[volcan_i][volcan_j].getNumJoueur() == 0) nb_bat_1++;
        if(plateau[tile2_i][tile2_j].getBatiment()!=0  && plateau[volcan_i][volcan_j].getNumJoueur() == 0) nb_bat_1++;

        if(plateau[volcan_i][volcan_j].getBatiment()!=0  && plateau[volcan_i][volcan_j].getNumJoueur() == 1) nb_bat_2++;
        if(plateau[tile1_i][tile1_j].getBatiment()!=0  && plateau[volcan_i][volcan_j].getNumJoueur() == 1) nb_bat_2++;
        if(plateau[tile2_i][tile2_j].getBatiment()!=0  && plateau[volcan_i][volcan_j].getNumJoueur() == 1) nb_bat_2++;

        int[] tab =  new int[2];
        tab[0] = nb_bat_1;
        tab[1] = nb_bat_2;
        return tab;
    }

    public boolean peutPlacerTuile(int ligneVolcan, int colonneVolcan, int ligneTile1, int colonneTile1, int ligneTile2, int colonneTile2) {
        // TODO V?rifier que ?a tue pas un village et faire en sort que 2 villages s?par?s aient pas le meme ID

        if(estVide()) return true;


        if (plateau[ligneTile1][colonneTile1].getColonneVolcan() == colonneVolcan && plateau[ligneTile2][colonneTile2].getLigneVolcan() == ligneVolcan) {
            return false;
        }


        // Hauteur max
        int hauteur = plateau[ligneVolcan][colonneVolcan].getHauteur();
        if (hauteur == 4) {
            return false;
        }
        // V?rifie si on place un volcan sur un volcan
        if (plateau[ligneVolcan][colonneVolcan].getBiomeTerrain() != Hexagone.VOLCAN && plateau[ligneVolcan][colonneVolcan].getBiomeTerrain() != Hexagone.VIDE) {
            return false;
        }

        // V?rifie qu'on detruit pas tous les batiments des joueurs
        if (plateau[ligneVolcan][colonneVolcan].getNumJoueur()==0 && quantiteBatimentJoueur1 !=0 && getNbBatEcrase(ligneVolcan,colonneVolcan,ligneTile1,colonneTile1,ligneTile2,colonneTile2)[0] >= quantiteBatimentJoueur1) return false; // joueur 0
        if (plateau[ligneVolcan][colonneVolcan].getNumJoueur()==0 && quantiteBatimentJoueur2 !=0 && getNbBatEcrase(ligneVolcan,colonneVolcan,ligneTile1,colonneTile1,ligneTile2,colonneTile2)[1] >= quantiteBatimentJoueur2) return false; // joueur 1

        // V?rifie la hauteur de toutes les cases
        if (plateau[ligneVolcan][colonneVolcan].getHauteur() != hauteur) {
            return false;
        }
        if (plateau[ligneTile1][colonneTile1].getHauteur() != hauteur) {
            return false;
        }
        if (plateau[ligneTile2][colonneTile2].getHauteur() != hauteur) {
            return false;
        }


        if (plateau[ligneVolcan][colonneVolcan].getBiomeTerrain() == Hexagone.VIDE && plateau[ligneTile1][colonneTile1].getBiomeTerrain() == Hexagone.VIDE && plateau[ligneTile2][colonneTile2].getBiomeTerrain() == Hexagone.VIDE) {


            if (!(
                    // Gauche droite
                    plateau[ligneVolcan][colonneVolcan - 1].getBiomeTerrain() != Hexagone.VIDE ||
                            plateau[ligneVolcan][colonneVolcan + 1].getBiomeTerrain() != Hexagone.VIDE ||
                            plateau[ligneTile1][colonneTile1 - 1].getBiomeTerrain() != Hexagone.VIDE ||
                            plateau[ligneTile1][colonneTile1 + 1].getBiomeTerrain() != Hexagone.VIDE ||
                            plateau[ligneTile2][colonneTile2 - 1].getBiomeTerrain() != Hexagone.VIDE ||
                            plateau[ligneTile2][colonneTile2 + 1].getBiomeTerrain() != Hexagone.VIDE)) {

                if (ligneVolcan % 2 == 1) {
                    colonneVolcan -= 1;
                }
                if (ligneTile1 % 2 == 1) {
                    colonneTile1 -= 1;
                }
                if (ligneTile2 % 2 == 1) {
                    colonneTile2 -= 1;
                }

                return plateau[ligneVolcan - 1][colonneVolcan + 1].getBiomeTerrain() != Hexagone.VIDE ||
                        plateau[ligneVolcan - 1][colonneVolcan].getBiomeTerrain() != Hexagone.VIDE ||
                        plateau[ligneVolcan + 1][colonneVolcan + 1].getBiomeTerrain() != Hexagone.VIDE ||
                        plateau[ligneVolcan + 1][colonneVolcan].getBiomeTerrain() != Hexagone.VIDE ||

                        plateau[ligneTile1 - 1][colonneTile1 + 1].getBiomeTerrain() != Hexagone.VIDE ||
                        plateau[ligneTile1 - 1][colonneTile1].getBiomeTerrain() != Hexagone.VIDE ||
                        plateau[ligneTile1 + 1][colonneTile1 + 1].getBiomeTerrain() != Hexagone.VIDE ||
                        plateau[ligneTile1 + 1][colonneTile1].getBiomeTerrain() != Hexagone.VIDE ||

                        plateau[ligneTile2 - 1][colonneTile2 + 1].getBiomeTerrain() != Hexagone.VIDE ||
                        plateau[ligneTile2 - 1][colonneTile2].getBiomeTerrain() != Hexagone.VIDE ||
                        plateau[ligneTile2 + 1][colonneTile2 + 1].getBiomeTerrain() != Hexagone.VIDE ||
                        plateau[ligneTile2 + 1][colonneTile2].getBiomeTerrain() != Hexagone.VIDE;
            }
        }

        return true;
    }
    public boolean peutPlacerVillage(int x ,int y){
        return plateau[x][y].getBiomeTerrain() != Hexagone.VOLCAN || plateau[x][y].getBatiment() == Hexagone.VIDE;

    }
    public boolean VillageQuestionMarK(int x , int y){
        return plateau[x][y].getBatiment() == Hexagone.HUTTE;

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

            if (peutPlacerTuile(courant.ligne(), courant.colonne(), enHautGauche.ligne(), enHautGauche.colonne(), enHautDroite.ligne(), enHautDroite.colonne())) {
                triplets.add(new TripletDePosition(courant, enHautGauche, enHautDroite));
            }
            if (peutPlacerTuile(courant.ligne(), courant.colonne(), enHautGauche.ligne(), enHautGauche.colonne(), gauche.ligne(), gauche.colonne())) {
                triplets.add(new TripletDePosition(courant, enHautGauche, gauche));
            }
            if (peutPlacerTuile(courant.ligne(), courant.colonne(), droite.ligne(), droite.colonne(), enHautDroite.ligne(), enHautDroite.colonne())) {
                triplets.add(new TripletDePosition(courant, droite, enHautDroite));
            }
            if (peutPlacerTuile(courant.ligne(), courant.colonne(), gauche.ligne(), gauche.colonne(), enBasGauche.ligne(), enBasGauche.colonne())) {
                triplets.add(new TripletDePosition(courant, gauche, enBasGauche));
            }
            if (peutPlacerTuile(courant.ligne(), courant.colonne(), enBasDroite.ligne(), enBasGauche.colonne(), enBasDroite.ligne(), enBasDroite.colonne())) {
                triplets.add(new TripletDePosition(courant, enBasGauche, enBasDroite));
            }
            if (peutPlacerTuile(courant.ligne(), courant.colonne(), enBasDroite.ligne(), enBasDroite.colonne(), droite.ligne(), droite.colonne())) {
                triplets.add(new TripletDePosition(courant, enBasDroite, droite));
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
        if(plateau[l][c].getBiomeTerrain()==Hexagone.VIDE){
            return true;
        }
        return plateau[l][c].getBiomeTerrain() == Hexagone.WATER;
    }
    public boolean estVolcan(int l, int c){
        return plateau[l][c].getBiomeTerrain() == Hexagone.VOLCAN;
    }

    public boolean aPourVolcan(int hexagone_c, int hexagon_l, int volcan_c, int volcan_l) {
        return plateau[hexagone_c][hexagon_l].getColonneVolcan() == volcan_c &&  plateau[hexagone_c][hexagon_l].getLigneVolcan() == volcan_l;
    }

    public void joueCoup(Coup coup) {
        byte num_joueur = coup.getNumJoueur();
        int hauteur = plateau[coup.volcanX][coup.volcanY].getHauteur();
        if (coup.typePlacement == Coup.TUILE) {
            plateau[coup.volcanX][coup.volcanY] = new Hexagone((byte) (hauteur + 1), Hexagone.VOLCAN, (byte)coup.volcanX, (byte)coup.volcanY);
            plateau[coup.tile1X][coup.tile1Y] = new Hexagone((byte) (hauteur + 1), coup.terrain1, (byte)coup.volcanX, (byte)coup.volcanY);
            plateau[coup.tile2X][coup.tile2Y] = new Hexagone((byte) (hauteur + 1), coup.terrain2, (byte)coup.volcanX, (byte)coup.volcanY);
            // On ajoute les emplacements libres des batiments
            positions_libres_batiments.add(new Position(coup.tile1X,coup.tile1Y));
            positions_libres_batiments.add(new Position(coup.tile2X,coup.tile2Y));
            // On ajoute les emplacements libres des tuiles
            ArrayList<Position> listeVoisins = voisins(coup.volcanX,coup.volcanY);
            metAjourPositionsLibres(listeVoisins);
            listeVoisins = voisins(coup.tile1X,coup.tile1Y);
            metAjourPositionsLibres(listeVoisins);
            listeVoisins = voisins(coup.tile2X,coup.tile2Y);
            metAjourPositionsLibres(listeVoisins);
            creerTriplets(positions_libres);
            historique.ajoute(coup);

        } else if (coup.typePlacement == Coup.BATIMENT || coup.typePlacement == 2 || coup.typePlacement == 3 || coup.typePlacement == 4){
            hauteur = plateau[coup.batimentX][coup.batimentY].getHauteur();
            byte batiment = 0;
            if (coup.typePlacement == 1) {
                batiment = Hexagone.HUTTE;
            } else if (coup.typePlacement == 2) {
                if(plateau[coup.batimentX][coup.batimentY].getBiomeTerrain() == Hexagone.FORET) batiment = Hexagone.TEMPLE_FORET;
                if(plateau[coup.batimentX][coup.batimentY].getBiomeTerrain() == Hexagone.GRASS) batiment = Hexagone.TEMPLE_PRAIRIE;
                if(plateau[coup.batimentX][coup.batimentY].getBiomeTerrain() == Hexagone.MONTAGNE) batiment = Hexagone.TEMPLE_PIERRE;
                if(plateau[coup.batimentX][coup.batimentY].getBiomeTerrain() == Hexagone.DESERT) batiment = Hexagone.TEMPLE_SABLE;
            } else if (coup.typePlacement == 3) {
                batiment = Hexagone.TOUR;
            } else if (coup.typePlacement == 4){
                batiment = Hexagone.CHOISIR_BATIMENT;
            }
            if(batiment!=Hexagone.CHOISIR_BATIMENT){
                Position aSupprimer = new Position(coup.batimentX,coup.batimentY);
                positions_libres_batiments.remove(aSupprimer);
            }

            plateau[coup.batimentX][coup.batimentY] = new Hexagone(num_joueur, (byte) hauteur, plateau[coup.batimentX][coup.batimentY].getBiomeTerrain(), batiment, (byte)plateau[coup.batimentX][coup.batimentY].getLigneVolcan(), (byte)plateau[coup.batimentX][coup.batimentY].getColonneVolcan());
            historique.ajoute(coup);
        }
    }


    // N?cessite un appel ? peutPlacerEtage
    public void placeEtage(byte joueurCourant, int volcan_x, int volcan_y, int tile1_x, int tile1_y, byte terrain1, int tile2_x, int tile2_y, byte terrain2) {
        Coup coup = new Coup(joueurCourant, volcan_x, volcan_y, tile1_x, tile1_y, terrain1, tile2_x, tile2_y, terrain2);
        historique.ajoute(coup);
        joueCoup(coup);
    }

    public boolean peutPlacerMaison(int i,int j){
        return (plateau[i][j].getBiomeTerrain()!=Hexagone.VOLCAN && plateau[i][j].getBatiment()==Hexagone.VIDE && plateau[i][j].getBiomeTerrain()!=Hexagone.VIDE );
    }
    public void placeBatiment(byte joueurCourant, int i, int j, byte type_bat){
        Coup coup = new Coup(joueurCourant, i,j,type_bat);
        historique.ajoute(coup);
        joueCoup(coup);
        if (type_bat == (byte)1){
            ArrayList<Point2> nlh ;
            nlh = propagation(i,j,joueurCourant);
            while(nlh.size()!=0) {
                Point2 a = nlh.remove(0);
                Coup ah ;
                if (joueurCourant==(byte)1){
                    ah=  new Coup(joueurCourant,a.x,a.y,(byte)0);
                }else {
                    ah= new Coup(joueurCourant,a.x,a.y,(byte)1);
                }
                System.out.println(a.x+" "+a.y);
                //historique.ajoute(ah);
                //joueCoup(ah);
            }
        }


    }

    public boolean notIn (ArrayList<Point2> lp, Point2 p){
        for (int i =0;i<lp.size();i++){
            if(lp.get(i).PionsEquals(p)){
                return false;
            }
        }
        return true;

    }
    public boolean check (int x, int y,int IDjoueurs) {
        return estDansPlateau(x, y)&&getTuile(x,y).getBatiment()==(byte)10&&getTuile(x,y).getNumJoueur()==IDjoueurs;
    }
    public boolean check2 (int x, int y,byte TypeTerrain) {
        return estDansPlateau(x, y) && getTuile(x,y).getBatiment()==(byte)0&&getTuile(x,y).getBiomeTerrain()==TypeTerrain;
    }
    public boolean estDansPlateau (int x , int y ){
        return (x<COLONNES)&&(x>-1)&&(y>-1)&&(y<LIGNES);
    }
    public ArrayList<Point2> propagation (int hutteX, int hutteY,byte joueurCourant ){
        ArrayList<Point2> listeDecases = new ArrayList<>();
        byte idJoueurs =joueurCourant;
        byte biome = getTuile(hutteX,hutteY).getBiomeTerrain();
        ArrayList<Point2> listeDesHutesVoisines = new ArrayList<>();
        Point2 positionHutte = new Point2(hutteX,hutteY);
        listeDesHutesVoisines.add(positionHutte);
        int i = 0;
        while (listeDesHutesVoisines.size()!=i){
            Point2 HuteCourant = listeDesHutesVoisines.get(i);
            if(check (HuteCourant.x-1 ,HuteCourant.y,idJoueurs)){
                Point2 p1 = new Point2(HuteCourant.x-1 ,HuteCourant.y);
                if(notIn(listeDesHutesVoisines,p1))
                    listeDesHutesVoisines.add(p1);
            }
            if(check (HuteCourant.x+1 ,HuteCourant.y,idJoueurs)){
                Point2 p1 = new Point2(HuteCourant.x+1,HuteCourant.y);
                if(notIn(listeDesHutesVoisines,p1))
                    listeDesHutesVoisines.add(p1);
            }
            if(check (HuteCourant.x ,HuteCourant.y-1,idJoueurs)){
                Point2 p1 = new Point2(HuteCourant.x ,HuteCourant.y-1);
                if(notIn(listeDesHutesVoisines,p1))
                    listeDesHutesVoisines.add(p1);

            }
            if(check(HuteCourant.x ,HuteCourant.y+1,idJoueurs)){
                Point2 p1 = new Point2(HuteCourant.x ,HuteCourant.y+1);
                if(notIn(listeDesHutesVoisines,p1))
                    listeDesHutesVoisines.add(p1);
            }
            if(HuteCourant.x%2==1){
                if(check (HuteCourant.x-1 ,HuteCourant.y-1,idJoueurs)){
                    Point2 p1 = new Point2(HuteCourant.x-1,HuteCourant.y-1);
                    if(notIn(listeDesHutesVoisines,p1))
                        listeDesHutesVoisines.add(p1);
                }
                if(check (HuteCourant.x+1 ,HuteCourant.y-1,idJoueurs)){
                    Point2 p1 = new Point2(HuteCourant.x+1 ,HuteCourant.y-1);
                    if(notIn(listeDesHutesVoisines,p1))
                        listeDesHutesVoisines.add(p1);

                }
            }else{
                if(check (HuteCourant.x-1 ,HuteCourant.y+1,idJoueurs)){
                    Point2 p1 = new Point2(HuteCourant.x-1 ,HuteCourant.y+1);
                    if(notIn(listeDesHutesVoisines,p1))
                        listeDesHutesVoisines.add(p1);

                }
                if(check (HuteCourant.x+1 ,HuteCourant.y+1,idJoueurs)){
                    Point2 p1 = new Point2(HuteCourant.x+1,HuteCourant.y+1);
                    if(notIn(listeDesHutesVoisines,p1))
                        listeDesHutesVoisines.add(p1);
                }
            }
            i++;
        }
        listeDesHutesVoisines.remove(0);
        i=0;
        while (listeDesHutesVoisines.size()!=i){
            Point2 HuteCourant = listeDesHutesVoisines.get(i);
            if(check2 (HuteCourant.x-1 ,HuteCourant.y,biome)){
                Point2 p1 = new Point2(HuteCourant.x-1 ,HuteCourant.y);
                if(notIn(listeDecases,p1))
                    listeDecases.add(p1);
            }
            if(check2 (HuteCourant.x ,HuteCourant.y+1,biome)){
                Point2 p1 = new Point2(HuteCourant.x ,HuteCourant.y+1);
                if(notIn(listeDecases,p1))
                    listeDecases.add(p1);
            }
            if(check2 (HuteCourant.x+1 ,HuteCourant.y,biome)){
                Point2 p1 = new Point2(HuteCourant.x+1 ,HuteCourant.y);
                if(notIn(listeDecases,p1))
                    listeDecases.add(p1);

            }
            if(check2(HuteCourant.x ,HuteCourant.y-1,biome)){
                Point2 p1 = new Point2(HuteCourant.x ,HuteCourant.y-1);
                if(notIn(listeDecases,p1))
                    listeDecases.add(p1);
            }
            if(HuteCourant.x%2==1){
                if(check2 (HuteCourant.x-1 ,HuteCourant.y-1,biome)){
                    Point2 p1 = new Point2(HuteCourant.x-1,HuteCourant.y-1);
                    if(notIn(listeDecases,p1))
                        listeDecases.add(p1);
                }
                if(check2 (HuteCourant.x+1 ,HuteCourant.y-1,biome)){
                    Point2 p1 = new Point2(HuteCourant.x+1 ,HuteCourant.y-1);
                    if(notIn(listeDecases,p1))
                        listeDecases.add(p1);

                }
            }else{
                if(check2 (HuteCourant.x-1 ,HuteCourant.y+1,biome)){
                    Point2 p1 = new Point2(HuteCourant.x-1 ,HuteCourant.y+1);
                    if(notIn(listeDecases,p1))
                        listeDecases.add(p1);

                }
                if(check2 (HuteCourant.x+1 ,HuteCourant.y+1,biome)){
                    Point2 p1 = new Point2(HuteCourant.x+1,HuteCourant.y+1);
                    if(notIn(listeDecases,p1))
                        listeDecases.add(p1);
                }
            }
            i++;
        }
        System.out.println("taille de liste de casses :"+listeDecases.size());
        i=0;
        while(i<listeDecases.size()){
            System.out.println(listeDecases.get(i).x+ " "+listeDecases.get(i).y);
            i++;
        }
        return listeDecases;
    }

    public boolean peutEcraser(Point2 a , Point2 b){
        int i =0 ;
        byte IDjoueurs = getTuile(a.x,b.x).getNumJoueur();
        if(getTuile(a.x,a.y).getBatiment()==10) {
            if (check(a.x - 1, a.y, IDjoueurs)) {
                i++;
            }
            if (check(a.x, a.y + 1, IDjoueurs)) {
                i++;
            }
            if (check(a.x + 1, a.y, IDjoueurs)) {
                i++;
            }
            if (check(a.x, a.y - 1, IDjoueurs)) {
                i++;
            }
            if (i % 2 == 1) {
                if (check(a.x - 1, a.y - 1, IDjoueurs)) {
                    i++;
                }
            } else {
                if (check(a.x - 1, a.y + 1, IDjoueurs)) {
                    i++;
                }
            }
            if (check(a.x + 1, a.y - 1, IDjoueurs)) {
                i++;

            }
        }
        if(getTuile(b.x,b.y).getBatiment()==10) {
            if (check(b.x - 1, b.y, IDjoueurs)) {
                i++;
            }
            if (check(b.x, b.y + 1, IDjoueurs)) {
                i++;
            }
            if (check(b.x + 1, b.y, IDjoueurs)) {
                i++;
            }
            if (check(b.x, b.y - 1, IDjoueurs)) {
                i++;
            }
            if (i % 2 == 1) {
                if (check(b.x - 1, b.y - 1, IDjoueurs)) {
                    i++;
                }
            } else {
                if (check(b.x - 1, b.y + 1, IDjoueurs)) {
                    i++;
                }
            }
            if (check(b.x + 1, b.y - 1, IDjoueurs)) {
                i++;

            }
        }
        if(getTuile(a.x,a.y).getNumJoueur()==getTuile(b.x,b.y).getNumJoueur()&&getTuile(b.x,b.y).getBatiment()==10&&getTuile(a.x,a.y).getBatiment()==10)
            i--;
        return i > 0;
    }

    public int getBatiment(int i,int j){
        return plateau[i][j].getBatiment();
    }

    public int[] getBatimentPlacable(int i,int j, int numJoueur){
        int[] coups = new int[3];
        coups[0] = 1;
        if(getHauteurTuile(i,j)==3) coups[2] = 1;
        if(aCiteAutour(i,j,numJoueur)) coups[1] = 1;
        return coups;
    }

    private boolean possedeBatiment(int i,int j,int numJoueur){
        return (getBatiment(i,j) != 0 && getTuile(i,j).getNumJoueur()==numJoueur);
    }

    private boolean aCiteAutour(int i,int j,int numJoueur){
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
        return plateau[i][j].getHauteur();
    }

    public Hexagone getTuile(int i, int j){
        return plateau[i][j];
    }
    public void joueHexagone(int x, int y){}

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
        for (Hexagone[] hexagones : plateau) {
            for (int j = 0; j < plateau[0].length; j++) {
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

    public boolean annuler() {
        return peutAnnuler();
    }

    public boolean refaire() {
        return peutRefaire();
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
}