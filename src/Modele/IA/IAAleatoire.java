package Modele.IA;

import Modele.Jeu.Coup;
import Modele.Jeu.CoupValeur;
import Modele.Jeu.Joueur;
import Modele.Jeu.Plateau.Plateau;
import Modele.Jeu.Plateau.Tuile;
import Structures.Position.Point2D;
import Structures.Position.Position;
import Structures.Position.TripletDePosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

class IAAleatoire extends AbstractIA {
    private Random r;
    public static int TEMPLE = 0;
    public static int HUTTE = 1;
    public static int TOUR = 2;
    private long startTime=0,endTime=0;
    private long startTimeTotal=0,endTimeTotal=0;
    private long startTimeTemp=0,endTimeTemp=0;
    private long duree_temp = 0;
    private long duree_totale = 0;
    private long duree_getTripletsPossibles = 0;
    private long duree_getPlateau_copie = 0;
    private long duree_placeEtage = 0;
    private long duree_getBatimentPlacable = 0;

    public IAAleatoire() {
        super(IA, "IA aleatoire");
    }

    @Override
    public CoupValeur joue() {
        Tuile tuile_pioche = jeu.getPioche().get(0);
        ArrayList<Tuile> pioche = new ArrayList<>();
        pioche.add(tuile_pioche);
        Plateau plateauIA = jeu.getPlateau();
        plateauIA.nbHutteDisponiblesJoueur = jeu.getJoueurCourantClasse().getNbHuttes();
        InstanceJeu instance = new InstanceJeu(pioche, plateauIA, jeu.getJoueurs(), jeu.getNumJoueurCourant());
        //on choisit un coup au hasard dans la liste des coups
        CoupValeur coupValeur = choisitCoup(instance);
        /*System.out.println("duree_getTripletsPossibles : " + duree_getTripletsPossibles);
        System.out.println("duree_getPlateau_copie : " + duree_getPlateau_copie);
        System.out.println("duree_placeEtage : " + duree_placeEtage);
        System.out.println("duree_getBatimentPlacable : " + duree_getBatimentPlacable);
        System.out.println("duree_temp : " + duree_temp);
        System.out.println("duree_totale : " + duree_totale);*/
        return coupValeur;
    }

    public CoupValeur choisitCoup(InstanceJeu instance){
        duree_temp = 0;
        duree_totale = 0;
        startTimeTotal = System.currentTimeMillis();
        duree_getTripletsPossibles = 0;
        duree_getPlateau_copie = 0;
        duree_placeEtage = 0;
        duree_getBatimentPlacable = 0;
        int i = 0;
        Coup coupT = null, coupB = null;
        ArrayList<Coup> coupsT = coupsTuilesPossibles(instance);
        Collections.shuffle(coupsT);
        while(true){
            if(i >= coupsT.size()){
                return null;
            }
            coupT = coupsT.get(i);
            ArrayList<Coup> coupsB = coupsBatimentsPossibles(instance, coupT);
            if(!coupsB.isEmpty()){
                // Pour placer les tours en priorité
                for(Coup coupCourant:coupsB){
                    if(coupCourant.typePlacement == 3){
                        CoupValeur coupValeur = new CoupValeur(coupT, coupCourant, 0);
                        return coupValeur;
                    }
                }
                // Pour placer les temples en priorité
                for(Coup coupCourant:coupsB){
                    if(coupCourant.typePlacement == 2){
                        CoupValeur coupValeur = new CoupValeur(coupT, coupCourant, 0);
                        return coupValeur;
                    }
                }
                Collections.shuffle(coupsB);
                coupB = coupsB.get(0);
                CoupValeur coupValeur = new CoupValeur(coupT, coupB, 0);
                endTimeTotal = System.currentTimeMillis();
                duree_totale = endTimeTotal - startTimeTotal;
                return coupValeur;
            }
            i++;
        }
    }

    public ArrayList<Coup> coupsTuilesPossibles(InstanceJeu instance) {
        ArrayList<Coup> coups_possibles = new ArrayList<>();
        byte joueur_courant = instance.getJoueurCourant();
        ArrayList<TripletDePosition> tripletsPossibles = instance.getPlateau().getTripletsPossibles();
        Tuile tuile = instance.getPioche().get(0);
        //pour chaque position possible de la tuile sur le plateau
        for (int tripletsIndex = 0; tripletsIndex < tripletsPossibles.size(); tripletsIndex++) {
            TripletDePosition tripletCourant = tripletsPossibles.get(tripletsIndex);
            Position[] points = new Position[3];
            points[0] = tripletCourant.getVolcan();
            points[1] = tripletCourant.getTile1();
            points[2] = tripletCourant.getTile2();
            //pour chaque orientation possible de la tuile
            for (int orientationTuile = 0; orientationTuile < 3; orientationTuile++) {
                Coup coupT = new Coup(joueur_courant, points[orientationTuile].ligne(), points[orientationTuile].colonne(), (points[(orientationTuile + 1) % 3].ligne()), (points[(orientationTuile + 1) % 3].colonne()), tuile.biome0, (points[(orientationTuile + 2) % 3].ligne()), (points[(orientationTuile + 2) % 3].colonne()), tuile.biome1);
                coups_possibles.add(coupT);
            }
        }
        return coups_possibles;
    }

    public ArrayList<Coup> coupsBatimentsPossibles(InstanceJeu instance, Coup coupT) {
        ArrayList<Coup> coups_possibles = new ArrayList<>();
        Plateau plateauCopie = instance.getPlateau().copie();
        byte joueur_courant = instance.getJoueurCourant();
        plateauCopie.joueCoup(coupT);
        ArrayList<Position> positionsLibresBatiments = plateauCopie.getPositions_libres_batiments();
        //On parcourt toutes les positions libres des bâtiments
        for (int position = 0; position < positionsLibresBatiments.size(); position++) {
            Coup coupB = null;
            Position positionCourante = positionsLibresBatiments.get(position);
            int[] batimentsPlacable = plateauCopie.getBatimentPlacable(positionCourante.ligne(), positionCourante.colonne(), joueur_courant);
            //On parcourt tous les choix de bâtiments possibles
            for (int batimentChoisit = 0; batimentChoisit < batimentsPlacable.length; batimentChoisit++) {
                //si le bâtiment est plaçable
                if (batimentsPlacable[batimentChoisit] == 1) {
                    if (batimentChoisit == HUTTE) {
                        //On place la hutte classique sans propagation
                        coupB = new Coup(joueur_courant, positionCourante.ligne(), positionCourante.colonne(), (byte) HUTTE);
                    } else { // Si nous ne posons pas de hutte, il n'y a pas de propagation
                        coupB = new Coup(joueur_courant, positionCourante.ligne(), positionCourante.colonne(), (byte) (batimentsPlacable[batimentChoisit]+1));
                    }
                    coups_possibles.add(coupB);
                }
            }
        }
        return coups_possibles;
    }
}
