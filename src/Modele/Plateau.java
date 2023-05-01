package Modele;

import Structures.Position;
import Structures.TripletDePosition;

import java.io.Serializable;
import java.util.ArrayList;

public class Plateau implements Serializable, Cloneable {
    final int LIGNES = 40;
    final int COLONNES = 40;
    protected Hexagone[][] plateau ;
    protected byte[] nbPionsJ1;
    protected byte[] nbPionsJ2;

    public int nb_bat_j1, nb_bat_j2;

    private Historique historique;
    private ArrayList<Position> positions_libres;

    private ArrayList<TripletDePosition> tripletsPossible;
    private ArrayList<Position> positions_libres_batiments;

    public Plateau(){
        plateau = new Hexagone [LIGNES][COLONNES];
        historique = new Historique();
        nbPionsJ1 = new byte[3];
        nbPionsJ2 = new byte[3];
        nbPionsJ1[0]=10 ; nbPionsJ2[0]=10;
        nbPionsJ1[1]=10 ; nbPionsJ2[1]=10;
        nbPionsJ1[2]=10 ; nbPionsJ2[2]=10;
        nb_bat_j1 = 0;
        nb_bat_j2 = 0;
        initPlateau();
        positions_libres = new ArrayList<>();
        positions_libres_batiments = new ArrayList<>();
        tripletsPossible = new ArrayList<>();
    }


    private void initPlateau() {
        for (int i = 0; i < plateau.length; i++) {
            for (int j = 0; j < plateau[0].length; j++) {
                plateau[i][j] = new Hexagone((byte)0, Hexagone.VIDE, (byte)19, (byte)20);
            }
        }

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
        return nb_pion_vite_J1 >= 2;
    }
    public boolean tousLesMeme(int x1 , int y1 , int x2 ,int y2 ,int x3 ,int y3){
        return plateau[x1][y1].getHauteur() == plateau[x2][y2].getHauteur() && plateau[x1][y1].getHauteur() == plateau[x3][y3].getHauteur();
    }

    public boolean estPlaceLibre(int x1, int y1){
        return plateau[x1][y1].getTerrain() <= 1;
    }

    public boolean peutPlacerTuileFromTriplets(int volcan_i, int volcan_j, int tile1_i, int tile1_j, int tile2_i, int tile2_j) {
        for(TripletDePosition p : tripletsPossible){
            // Attention le Point X des triplets correspond toujours au volcan !!
            if (p.getX().getL() == volcan_i && p.getX().getC() == volcan_j && p.getY().getL() == tile1_i && p.getY().getC() == tile1_j && p.getZ().getL() == tile2_i && p.getZ().getC() == tile2_j)
                return true;
            if (p.getX().getL() == volcan_i && p.getX().getC() == volcan_j && p.getY().getL() == tile2_i && p.getY().getC() == tile2_j && p.getZ().getL() == tile1_i && p.getZ().getC() == tile1_j)
                return true;
            if (estHexagoneVide(volcan_i, volcan_j) && estHexagoneVide(tile1_i, tile1_j) && estHexagoneVide(tile2_i, tile2_j)) {
                if (p.getX().getL() == tile1_i && p.getX().getC() == tile1_j && p.getY().getL() == volcan_i && p.getY().getC() == volcan_j && p.getZ().getL() == tile2_i && p.getZ().getC() == tile2_j)
                    return true;
                if (p.getX().getL() == tile1_i && p.getX().getC() == tile1_j && p.getY().getL() == tile2_i && p.getY().getC() == tile2_j && p.getZ().getL() == volcan_i && p.getZ().getC() == volcan_j)
                    return true;
                if (p.getX().getL() == tile2_i && p.getX().getC() == tile2_j && p.getY().getL() == volcan_i && p.getY().getC() == volcan_j && p.getZ().getL() == tile1_i && p.getZ().getC() == tile1_j)
                    return true;
                if (p.getX().getL() == tile2_i && p.getX().getC() == tile2_j && p.getY().getL() == tile1_i && p.getY().getC() == tile1_j && p.getZ().getL() == volcan_i && p.getZ().getC() == volcan_j)
                    return true;
            }
        }
        return false;
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

    public boolean peutPlacerTuile(int volcan_i, int volcan_j, int tile1_i, int tile1_j, int tile2_i, int tile2_j) {
        // TODO Vérifier que ça tue pas un village et faire en sort que 2 villages séparés aient pas le meme ID

        if(estVide()) return true;


        int hauteur = plateau[volcan_i][volcan_j].getHauteur();
        if (plateau[tile1_i][tile1_j].getVolcanJ() == volcan_j && plateau[tile2_i][tile2_j].getVolcanI() == volcan_i) {
            return false;
        }


        // Hauteur max
        if (hauteur == 3) {
            return false;
        }
        // Vérifie si on place un volcan sur un volcan
        if (plateau[volcan_i][volcan_j].getTerrain() != Hexagone.VOLCAN && plateau[volcan_i][volcan_j].getTerrain() != Hexagone.VIDE) {
            return false;
        }

        // Vérifie qu'on detruit pas tous les batiments des joueurs
        if (plateau[volcan_i][volcan_j].getNumJoueur()==0 && nb_bat_j1!=0 && getNbBatEcrase(volcan_i,volcan_j,tile1_i,tile1_j,tile2_i,tile2_j)[0] >= nb_bat_j1) return false; // joueur 0
        if (plateau[volcan_i][volcan_j].getNumJoueur()==0 && nb_bat_j2!=0 && getNbBatEcrase(volcan_i,volcan_j,tile1_i,tile1_j,tile2_i,tile2_j)[1] >= nb_bat_j2) return false; // joueur 1

        // Vérifie la hauteur de toutes les cases
        if (plateau[volcan_i][volcan_j].getHauteur() != hauteur) {
            return false;
        }
        if (plateau[tile1_i][tile1_j].getHauteur() != hauteur) {
            return false;
        }
        if (plateau[tile2_i][tile2_j].getHauteur() != hauteur) {
            return false;
        }


        if (plateau[volcan_i][volcan_j].getTerrain() == Hexagone.VIDE && plateau[tile1_i][tile1_j].getTerrain() == Hexagone.VIDE && plateau[tile2_i][tile2_j].getTerrain() == Hexagone.VIDE) {


            if (!(
                    // Gauche droite
                    plateau[volcan_i][volcan_j - 1].getTerrain() != Hexagone.VIDE ||
                    plateau[volcan_i][volcan_j + 1].getTerrain() != Hexagone.VIDE ||
                    plateau[tile1_i][tile1_j - 1].getTerrain() != Hexagone.VIDE ||
                    plateau[tile1_i][tile1_j + 1].getTerrain() != Hexagone.VIDE ||
                    plateau[tile2_i][tile2_j - 1].getTerrain() != Hexagone.VIDE ||
                    plateau[tile2_i][tile2_j + 1].getTerrain() != Hexagone.VIDE)) {

                if (volcan_i % 2 == 1) {
                    volcan_j -= 1;
                }
                if (tile1_i % 2 == 1) {
                    tile1_j -= 1;
                }
                if (tile2_i % 2 == 1) {
                    tile2_j -= 1;
                }

                return plateau[volcan_i - 1][volcan_j + 1].getTerrain() != Hexagone.VIDE ||
                        plateau[volcan_i - 1][volcan_j].getTerrain() != Hexagone.VIDE ||
                        plateau[volcan_i + 1][volcan_j + 1].getTerrain() != Hexagone.VIDE ||
                        plateau[volcan_i + 1][volcan_j].getTerrain() != Hexagone.VIDE ||

                        plateau[tile1_i - 1][tile1_j + 1].getTerrain() != Hexagone.VIDE ||
                        plateau[tile1_i - 1][tile1_j].getTerrain() != Hexagone.VIDE ||
                        plateau[tile1_i + 1][tile1_j + 1].getTerrain() != Hexagone.VIDE ||
                        plateau[tile1_i + 1][tile1_j].getTerrain() != Hexagone.VIDE ||

                        plateau[tile2_i - 1][tile2_j + 1].getTerrain() != Hexagone.VIDE ||
                        plateau[tile2_i - 1][tile2_j].getTerrain() != Hexagone.VIDE ||
                        plateau[tile2_i + 1][tile2_j + 1].getTerrain() != Hexagone.VIDE ||
                        plateau[tile2_i + 1][tile2_j].getTerrain() != Hexagone.VIDE;
            }
        }

        return true;
    }
    public boolean peutPlacerVillage(int x ,int y){
        return plateau[x][y].getTerrain() != Hexagone.VOLCAN || plateau[x][y].getBatiment() == Hexagone.VIDE;

    }
    public boolean VillageQuestionMarK(int x , int y){
        return plateau[x][y].getBatiment() == Hexagone.MAISON;

    }
    public ArrayList<Integer> propagation (int x, int y ){
        ArrayList<Integer> listeDecases = new ArrayList<>();
        int IDvillage = plateau[x][y].getIDvillage();
        byte TypeTerrain = plateau[x][y].getTerrain();
        for (int i = 0; i<plateau.length;i++){
            for (int j = 0; j<plateau[0].length;j++){
                if(plateau[i][j].getIDvillage()==IDvillage || (i!=x && y!=j)){// pense a quand place village regarder si tour ou temple
                    if(plateau[i][j-1].getTerrain()== TypeTerrain) {
                        listeDecases.add(i);
                        listeDecases.add(j-1);
                    }
                    if(plateau[i-1][j].getTerrain()== TypeTerrain) {
                        listeDecases.add(i-1);
                        listeDecases.add(j);
                    }
                    if(plateau[i][j+1].getTerrain()== TypeTerrain) {
                        listeDecases.add(i);
                        listeDecases.add(j+1);
                    }
                    if(plateau[i+1][j].getTerrain()== TypeTerrain) {
                        listeDecases.add(i+1);
                        listeDecases.add(j);
                    }
                    if(i%2==1){
                        if(plateau[i-1][j+1].getTerrain()== TypeTerrain) {
                            listeDecases.add(i-1);
                            listeDecases.add(j+1);
                        }
                        if(plateau[i+1][j+1].getTerrain()== TypeTerrain) {
                            listeDecases.add(i+1);
                            listeDecases.add(j+1);
                        }
                    }else{
                        if(plateau[i-1][j-1].getTerrain()== TypeTerrain) {
                            listeDecases.add(i-1);
                            listeDecases.add(j-1);
                        }
                        if(plateau[i+1][j-1].getTerrain()== TypeTerrain) {
                            listeDecases.add(i+1);
                            listeDecases.add(j-1);
                        }
                    }
                }
            }
        }
        return listeDecases;
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
        for(Position p : voisins){
            ArrayList<Position> voisinsDeVoisins = new ArrayList<>();
            voisinsDeVoisins = voisins(p.getL(),p.getC());
            Position courant = new Position(p.getL(),p.getC()) ;
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

            if (peutPlacerTuile(courant.getL(), courant.getC(), enHautGauche.getL(), enHautGauche.getC(), enHautDroite.getL(), enHautDroite.getC())) {
                triplets.add(new TripletDePosition(courant, enHautGauche, enHautDroite));
            }
            if (peutPlacerTuile(courant.getL(), courant.getC(), enHautGauche.getL(), enHautGauche.getC(), gauche.getL(), gauche.getC())) {
                triplets.add(new TripletDePosition(courant, enHautGauche, gauche));
            }
            if (peutPlacerTuile(courant.getL(), courant.getC(), droite.getL(), droite.getC(), enHautDroite.getL(), enHautDroite.getC())) {
                triplets.add(new TripletDePosition(courant, droite, enHautDroite));
            }
            if (peutPlacerTuile(courant.getL(), courant.getC(), gauche.getL(), gauche.getC(), enBasGauche.getL(), enBasGauche.getC())) {
                triplets.add(new TripletDePosition(courant, gauche, enBasGauche));
            }
            if (peutPlacerTuile(courant.getL(), courant.getC(), enBasDroite.getL(), enBasGauche.getC(), enBasDroite.getL(), enBasDroite.getC())) {
                triplets.add(new TripletDePosition(courant, enBasGauche, enBasDroite));
            }
            if (peutPlacerTuile(courant.getL(), courant.getC(), enBasDroite.getL(), enBasDroite.getC(), droite.getL(), droite.getC())) {
                triplets.add(new TripletDePosition(courant, enBasDroite, droite));
            }
        }
        tripletsPossible.addAll(triplets);
    }


    public void metAjourPositionsLibres(ArrayList<Position> listeVoisins){
        ArrayList<Position> aSupprimer = new ArrayList<>();
        ArrayList<TripletDePosition> tripletsaSupprimer = new ArrayList<>();
        for(Position p : listeVoisins){
            //si p est dans positions_libres et n'est pas de l'eau, on l'enlève
            if(!estHexagoneVide(p.getL(), p.getC())) {
                aSupprimer.add(p);
            }
        }
        //System.out.println("Pour être sur taille AVANT: "+listeVoisins.size());

        for(Position p : aSupprimer){
            listeVoisins.remove(p);
            for(TripletDePosition t : tripletsPossible){
                if(((t.getX().getL()==p.getL() && t.getX().getC()==p.getC())||(t.getY().getL()==p.getL() && t.getY().getC()==p.getC())||(t.getZ().getL()==p.getL() && t.getZ().getC()==p.getC()))
                        ||!estHexagoneVide(t.getX().getL(),t.getX().getC())||!estHexagoneVide(t.getY().getL(),t.getY().getC())||!estHexagoneVide(t.getZ().getL(),t.getZ().getC())){
                    tripletsaSupprimer.add(t);
                }
            }
        }
        //System.out.println("Pour être sur taille APRES: "+listeVoisins.size());
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
        if(plateau[l][c].getTerrain()==Hexagone.VIDE){
            return true;
        }
        return plateau[l][c].getTerrain() == Hexagone.WATER;
    }
    public boolean estVolcan(int l, int c){
        return plateau[l][c].getTerrain() == Hexagone.VOLCAN;
    }

    public boolean aPourVolcan(int hexagone_c, int hexagon_l, int volcan_c, int volcan_l) {
        return plateau[hexagone_c][hexagon_l].getVolcanJ() == volcan_c &&  plateau[hexagone_c][hexagon_l].getVolcanI() == volcan_l;
    }

    public void joueCoup(Coup coup) {
        byte num_joueur = coup.getNumJoueur();
        int hauteur = plateau[coup.volcan_x][coup.volcan_y].getHauteur();
        if (coup.type == Coup.TUILE) {
            plateau[coup.volcan_x][coup.volcan_y] = new Hexagone((byte) (hauteur + 1), Hexagone.VOLCAN, (byte)coup.volcan_x, (byte)coup.volcan_y);
            plateau[coup.tile1_x][coup.tile1_y] = new Hexagone((byte) (hauteur + 1), coup.terrain1, (byte)coup.volcan_x, (byte)coup.volcan_y);
            plateau[coup.tile2_x][coup.tile2_y] = new Hexagone((byte) (hauteur + 1), coup.terrain2, (byte)coup.volcan_x, (byte)coup.volcan_y);
            // On ajoute les emplacements libres des batiments
            positions_libres_batiments.add(new Position(coup.tile1_x,coup.tile1_y));
            positions_libres_batiments.add(new Position(coup.tile2_x,coup.tile2_y));
            // On ajoute les emplacements libres des tuiles
            ArrayList<Position> listeVoisins = voisins(coup.volcan_x,coup.volcan_y);
            metAjourPositionsLibres(listeVoisins);
            listeVoisins = voisins(coup.tile1_x,coup.tile1_y);
            metAjourPositionsLibres(listeVoisins);
            listeVoisins = voisins(coup.tile2_x,coup.tile2_y);
            metAjourPositionsLibres(listeVoisins);
            creerTriplets(positions_libres);

        } else if (coup.type == Coup.BATIMENT || coup.type == 2 || coup.type == 3 || coup.type == 4){
            hauteur = plateau[coup.batiment_x][coup.batiment_y].getHauteur();
            byte batiment = 0;
            if (coup.type == 1) {
                batiment = Hexagone.MAISON;
            } else if (coup.type == 2) {
                if(plateau[coup.batiment_x][coup.batiment_y].getTerrain() == Hexagone.FORET) batiment = Hexagone.TEMPLE_FORET;
                if(plateau[coup.batiment_x][coup.batiment_y].getTerrain() == Hexagone.GRASS) batiment = Hexagone.TEMPLE_PRAIRIE;
                if(plateau[coup.batiment_x][coup.batiment_y].getTerrain() == Hexagone.MONTAGNE) batiment = Hexagone.TEMPLE_PIERRE;
                if(plateau[coup.batiment_x][coup.batiment_y].getTerrain() == Hexagone.DESERT) batiment = Hexagone.TEMPLE_SABLE;
            } else if (coup.type == 3) {
                batiment = Hexagone.TOUR;
            } else if (coup.type == 4){
                batiment = Hexagone.CHOISIR_BATIMENT;
            }
            if(batiment!=Hexagone.CHOISIR_BATIMENT){
                Position aSupprimer = new Position(coup.batiment_x,coup.batiment_y);
                positions_libres_batiments.remove(aSupprimer);
            }

            plateau[coup.batiment_x][coup.batiment_y] = new Hexagone(num_joueur, (byte) hauteur, plateau[coup.batiment_x][coup.batiment_y].getTerrain(), batiment, (byte)plateau[coup.batiment_x][coup.batiment_y].getVolcanI(), (byte)plateau[coup.batiment_x][coup.batiment_y].getVolcanJ());
        }
    }


    // Nécessite un appel à peutPlacerEtage
    public void placeEtage(byte joueurCourant, int volcan_x, int volcan_y, int tile1_x, int tile1_y, byte terrain1, int tile2_x, int tile2_y, byte terrain2) {
        Coup coup = new Coup(joueurCourant, volcan_x, volcan_y, tile1_x, tile1_y, terrain1, tile2_x, tile2_y, terrain2);
        historique.ajoute(coup);
        joueCoup(coup);
    }

    public boolean peutPlacerMaison(int i,int j){
        return (plateau[i][j].getTerrain()!=Hexagone.VOLCAN && plateau[i][j].getBatiment()==Hexagone.VIDE && plateau[i][j].getTerrain()!=Hexagone.VIDE );
    }
    public void placeBatiment(byte joueurCourant, int i, int j, byte type_bat){
        Coup coup = new Coup(joueurCourant, i,j,type_bat);
        historique.ajoute(coup);
        joueCoup(coup);
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
        historique = new Historique();
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
                if (hexagones[j].getTerrain() != Hexagone.VIDE && hexagones[j].getTerrain() != Hexagone.WATER)
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

        System.arraycopy(this.nbPionsJ1, 0, nbPions[0], 0, this.nbPionsJ1.length);
        System.arraycopy(this.nbPionsJ2, 0, nbPions[1], 0, this.nbPionsJ2.length);
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
