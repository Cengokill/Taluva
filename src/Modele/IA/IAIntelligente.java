package Modele.IA;

import Modele.Jeu.Coup;
import Modele.Jeu.CoupValeur;
import Modele.Jeu.Joueur;
import Modele.Jeu.Plateau.Plateau;
import Modele.Jeu.Plateau.Tuile;
import Structures.Position.Point2D;
import Structures.Position.Position;
import Structures.Position.TripletDePosition;

import java.util.*;

public class IAIntelligente extends AbstractIA {

    public byte num_joueur_ia;
    private int nbInstancesDifferentes =0;
    public static int poids_temple = 5000;
    public static int poids_tour = 1000;
    public static int poids_hutte = 1;
    public static int TEMPLE = 0;
    public static int HUTTE = 1;
    public static int TOUR = 2;
    private Random r;

    public IAIntelligente() {
        super();
    }

    public ArrayList<Tuile> ajoutTuilesPioche(LinkedList<Tuile> pioche_du_jeu){//15 tuiles différentes
        ArrayList<Tuile> pioche = new ArrayList<>();
        //calcule tous les coups avec chaque tuile de la pioche
        for(int tuileIndex = 0; tuileIndex<pioche_du_jeu.size(); tuileIndex++){
            if(!contientTuile(pioche,pioche_du_jeu.get(tuileIndex))){
                pioche.add(pioche_du_jeu.get(tuileIndex));
            }
        }
        return pioche;
    }

    public boolean contientTuile(ArrayList<Tuile> listeTuiles, Tuile t){
        for(int tuileIndex = 0; tuileIndex < listeTuiles.size(); tuileIndex++){
            if(listeTuiles.get(tuileIndex).biome0 == t.biome0 && listeTuiles.get(tuileIndex).biome1 == t.biome1){
                return true;
            }
        }
        return false;
    }

    @Override
    public CoupValeur joue(){
        System.out.println("jeu.getPioche().size : "+jeu.getPioche().size());
        ArrayList<Tuile> pioche = ajoutTuilesPioche(jeu.getPioche());
        System.out.println("pioche.size() : "+pioche.size());
        Plateau plateauIA = jeu.getPlateau();
        plateauIA.nbHutteDisponiblesJoueur = jeu.getJoueurCourantClasse().getNbHuttes();
        InstanceJeu instance = new InstanceJeu(pioche, plateauIA, jeu.getJoueurs(), jeu.getNumJoueurCourant());
        ArrayList<CoupValeur> listeMeilleursCoups = meilleursCoups(instance, 1);
        //on choisit le meilleur coup au hasard dans la liste des meilleurs coups
        r = new Random();
        int index = r.nextInt(listeMeilleursCoups.size());
        return listeMeilleursCoups.get(index);
    }

    public ArrayList<CoupValeur> meilleursCoups(InstanceJeu instance, int horizon){
        ArrayList<CoupValeur> coups_calcules = new ArrayList<>();
        ArrayList<ArrayList<Coup>> coups_possibles = coupsPossibles(instance);
        //for chaque coup possible
        for(int i = 0; i<coups_possibles.size(); i++){
            ArrayList<Coup> coupDuo = coups_possibles.get(i);//on rappelle qu'on coup est constitué d'un coup de tuile et d'un coup de bâtiment d'où le Duo
            InstanceJeu nouvelle_configuration = appliquerCoup(instance, coupDuo.get(0), coupDuo.get(1));
            int valeur = calculCoups_joueur_A(nouvelle_configuration, horizon);
            coups_calcules.add(new CoupValeur(coupDuo.get(0), coupDuo.get(1), valeur));
        }
        // Trouver le coup avec la valeur maximale
        ArrayList<CoupValeur> meilleurs_coups = coupsMax(coups_calcules);
        return meilleurs_coups;
    }

    public InstanceJeu appliquerCoup(InstanceJeu instance, Coup coupT, Coup coupB){
        ArrayList<Tuile> pioche = copyPioche(instance.getPioche());
        Plateau plateauCopie = instance.getPlateau().copie();
        Joueur[] joueurs = instance.getJoueurs();
        byte joueur_courant = instance.getJoueurCourant();
        plateauCopie.placeEtage(joueur_courant, coupT.volcanLigne, coupT.volcanColonne, coupT.tile1Ligne, coupT.tile1Colonne, coupT.biome1, coupT.tile2Ligne, coupT.tile2Colonne, coupT.biome2);
        byte type_batiment = coupB.typePlacement;
        Position positionCourante = new Position(coupB.getBatLigne(), coupB.getBatColonne());
        Joueur jCourantCopie = instance.getJoueur(joueur_courant);

        // La position actuelle n'est plus libre
        Position posASupprimer = new Position(positionCourante.ligne(), positionCourante.colonne());
        plateauCopie.supprimeElementNew(posASupprimer);
        if(type_batiment == HUTTE) {
            // On créer un tableau contenant toutes les coordonées où l'on doit propager
            ArrayList<Point2D> aPropager = plateauCopie.previsualisePropagation(positionCourante.ligne(), positionCourante.colonne(), (byte) ((joueur_courant + 1) % 2)); // A MODIFIER POUR PLUS TARD TEST remettre joueur_courant
            // On place la hutte classique sans propagation
            plateauCopie.placeBatiment(joueur_courant, positionCourante.ligne(), positionCourante.colonne(), (byte) HUTTE);
            // On met a jour le nombre de huttes restantes
            int hauteurCourante = plateauCopie.getHauteurTuile(positionCourante.ligne(), positionCourante.colonne());
            updateBatimentsJoueur((byte) HUTTE, jCourantCopie, hauteurCourante);
            // On récupère le nombre de hutte disponibles pour le joueur courant
            int nbHuttesDispo = plateauCopie.nbHutteDisponiblesJoueur - (plateauCopie.getHauteurTuile(positionCourante.ligne(), positionCourante.colonne()));
            while (aPropager.size() != 0) {
                Point2D PosCourantePropagation = aPropager.remove(0);
                hauteurCourante = plateauCopie.getHauteurTuile(PosCourantePropagation.getPointX(), PosCourantePropagation.getPointY());
                if(nbHuttesDispo>=hauteurCourante){
                    plateauCopie.placeBatiment(joueur_courant,PosCourantePropagation.getPointX(),PosCourantePropagation.getPointY(),(byte) HUTTE);
                    // On place une hutte dessus, donc plus disponible
                    posASupprimer = new Position(PosCourantePropagation.getPointX(),PosCourantePropagation.getPointY());
                    plateauCopie.supprimeElementNew(posASupprimer);
                    // On met a jour le compteur des huttes du joueur courant
                    updateBatimentsJoueur((byte) HUTTE, jCourantCopie,hauteurCourante);
                    nbHuttesDispo-=hauteurCourante;
                }
            }
        }else { // Si nous ne posons pas de hutte, il n'y a pas de propagation
            plateauCopie.placeBatiment(joueur_courant, positionCourante.ligne(), positionCourante.colonne(), coupB.typePlacement);
            updateBatimentsJoueur(coupB.typePlacement, jCourantCopie, 0);
        }
        joueurs[joueur_courant] = jCourantCopie;
        InstanceJeu instanceCopie = new InstanceJeu(pioche, plateauCopie,joueurs, (byte) ((joueur_courant+1)%2));
        return instanceCopie;
    }

    public static ArrayList<CoupValeur> coupsMax(ArrayList<CoupValeur> coups) {
        ArrayList<CoupValeur> coupsMax = new ArrayList<>();
        int valeur_max = Integer.MIN_VALUE;
        for (CoupValeur coup_valeur : coups) {
            if (coup_valeur.getValeur() > valeur_max) {
                coupsMax = new ArrayList<>();
                coupsMax.add(coup_valeur);
                valeur_max = coup_valeur.getValeur();
            } else if (coup_valeur.getValeur() == valeur_max) {
                coupsMax.add(coup_valeur);
            }
        }
        return coupsMax;
    }

    //calcule les coups possibles pour une configuration de jeu : un coup de placement de tuile, et un coup de placement de bâtiment
    public ArrayList<ArrayList<Coup>> coupsPossibles(InstanceJeu instance){
        ArrayList<ArrayList<Coup>> coups_possibles = new ArrayList<>();
        byte joueur_courant = instance.getJoueurCourant();
        ArrayList<TripletDePosition> tripletsPossibles = instance.getPlateau().getTripletsPossibles();
        ArrayList<Tuile> pioche = copyPioche(instance.getPioche());
        for (int piocheIndex = 0; piocheIndex < pioche.size(); piocheIndex++) {
            Tuile tuile = pioche.get(piocheIndex);
            //pour chaque tuile unique de la pioche
            for (int tripletsIndex = 0; tripletsIndex < tripletsPossibles.size(); tripletsIndex++) {
                TripletDePosition tripletCourant = tripletsPossibles.get(tripletsIndex);
                Position[] points = new Position[3];
                points[0] = tripletCourant.getVolcan();
                points[1] = tripletCourant.getTile1();
                points[2] = tripletCourant.getTile2();
                ArrayList<Coup> coupDuo = new ArrayList<>();
                for (int orientationTuile = 0; orientationTuile < 3; orientationTuile++) {
                    Coup coupT = new Coup(joueur_courant, points[orientationTuile].ligne(), points[orientationTuile].colonne(), (points[(orientationTuile + 1) % 3].ligne()), (points[(orientationTuile + 1) % 3].colonne()), tuile.biome0, (points[(orientationTuile + 2) % 3].ligne()), (points[(orientationTuile + 2) % 3].colonne()), tuile.biome1);
                    coupDuo.add(coupT);
                    Plateau plateauCopie = instance.getPlateau().copie();
                    plateauCopie.placeEtage(joueur_courant, points[orientationTuile].ligne(), points[orientationTuile].colonne(), (points[(orientationTuile + 1) % 3].ligne()), (points[(orientationTuile + 1) % 3].colonne()), tuile.biome0, (points[(orientationTuile + 2) % 3].ligne()), (points[(orientationTuile + 2) % 3].colonne()), tuile.biome1);
                    ArrayList<Tuile> nouvellePioche;
                    nouvellePioche = copyPioche(pioche);
                    nouvellePioche.remove(piocheIndex);
                    ArrayList<Position> positionsLibresBatiments = plateauCopie.getPositions_libres_batiments();
                    //On parcourt toutes les positions libres des bâtiments
                    for (int position = 0; position < positionsLibresBatiments.size(); position++) {
                        Coup coupB = null;
                        Position positionCourante = positionsLibresBatiments.get(position);
                        int[] batimentsPlacable = plateauCopie.getBatimentPlacable(positionCourante.ligne(), positionCourante.colonne(), joueur_courant);
                        //On parcourt tous les choix de bâtiments possibles
                        for (int batimentChoisit = 0; batimentChoisit < batimentsPlacable.length; batimentChoisit++) {
                            System.out.println("batimentChoisit : " + batimentChoisit);
                            //si le bâtiment est plaçable
                            if (batimentsPlacable[batimentChoisit] == 1) {
                                Joueur jCourantCopie = instance.getJoueur(joueur_courant);
                                Joueur[] joueurs = instance.getJoueurs();
                                Plateau plateauCopie2 = plateauCopie.copie();
                                //si HUTTE (propagation potentielle)
                                if (batimentChoisit == HUTTE){
                                    //On créer un tableau contenant toutes les coordonées où l'on doit propager
                                    ArrayList<Point2D> aPropager = plateauCopie2.previsualisePropagation(positionCourante.ligne(), positionCourante.colonne(), joueur_courant);
                                    //On place la hutte classique sans propagation
                                    coupB = new Coup(joueur_courant, positionCourante.ligne(), positionCourante.colonne(), (byte) HUTTE);
                                    plateauCopie2.placeBatiment(joueur_courant, positionCourante.ligne(), positionCourante.colonne(), (byte) HUTTE);
                                    //La position actuelle n'est plus libre
                                    Position posASupprimer = new Position(positionCourante.ligne(), positionCourante.colonne());
                                    plateauCopie2.supprimeElementNew(posASupprimer);
                                    //On met a jour le nombre de hutte restantes
                                    int hauteurCourante = plateauCopie2.getHauteurTuile(positionCourante.ligne(), positionCourante.colonne());
                                    updateBatimentsJoueur((byte) HUTTE, jCourantCopie, hauteurCourante);
                                    // On récupère le nombre de huttes disponibles pour le joueur courant
                                    int nbHuttesDispo = plateauCopie2.nbHutteDisponiblesJoueur - (plateauCopie2.getHauteurTuile(positionCourante.ligne(), positionCourante.colonne()));
                                    while (aPropager.size() != 0) {
                                        Point2D posCourantePropagation = aPropager.remove(0);
                                        hauteurCourante = plateauCopie2.getHauteurTuile(posCourantePropagation.getPointX(), posCourantePropagation.getPointY());
                                        if (nbHuttesDispo >= hauteurCourante) {
                                            plateauCopie2.placeBatiment(joueur_courant, posCourantePropagation.getPointX(), posCourantePropagation.getPointY(), (byte) HUTTE);
                                            // On place une hutte dessus, donc plus disponible
                                            posASupprimer = new Position(posCourantePropagation.getPointX(), posCourantePropagation.getPointY());
                                            plateauCopie2.supprimeElementNew(posASupprimer);
                                            updateBatimentsJoueur((byte) HUTTE, jCourantCopie, hauteurCourante);
                                            nbHuttesDispo -= hauteurCourante;
                                        }
                                    }
                                } else { // Si nous ne posons pas de hutte, il n'y a pas de propagation
                                    coupB = new Coup(joueur_courant, positionCourante.ligne(), positionCourante.colonne(), (byte) (batimentsPlacable[batimentChoisit]));
                                    plateauCopie2.placeBatiment(joueur_courant, positionCourante.ligne(), positionCourante.colonne(), (byte) (batimentsPlacable[batimentChoisit]));
                                    updateBatimentsJoueur(batimentChoisit, jCourantCopie, 0);
                                    //on supprime la position du bâtiment qui n'est plus libre
                                    Position posASupprimer = new Position(positionCourante.ligne(), positionCourante.colonne());
                                    plateauCopie2.supprimeElementNew(posASupprimer);
                                }
                                joueurs[joueur_courant] = jCourantCopie;
                            }
                            coupDuo.add(coupB);
                        }
                    }
                }
                //affiche coups_possibles
                for(int i=0; i<coups_possibles.size(); i++){
                    System.out.println("Coup de tuile :");
                    coups_possibles.get(i).get(0).affiche();
                    System.out.println("Coup de batiment :");
                    coups_possibles.get(i).get(1).affiche();
                    System.out.println("----------------------------------------");
                }
                coups_possibles.add(coupDuo);
            }
        }
        return coups_possibles;
    }

    public int calculCoups_joueur_A(InstanceJeu instance, int horizon) {
        if(horizon==0){
            return evaluation_joueur(instance, instance.getJoueurCourant());
        }
        int valeur = Integer.MIN_VALUE;
        ArrayList<ArrayList<Coup>> coups_possibles = coupsPossibles(instance);
        for(int i = 0; i<coups_possibles.size(); i++){
            ArrayList<Coup> coupDuo = coups_possibles.get(i);
            //si impossible de placer un bâtiment
            if(coupDuo.get(1)==null) valeur = Integer.MIN_VALUE;
            else{
                InstanceJeu successeur = appliquerCoup(instance, coupDuo.get(0), coupDuo.get(1));
                valeur = Math.max(valeur, calculCoups_joueur_B(successeur, horizon-1));
            }
        }
        return valeur;
    }

    public int calculCoups_joueur_B(InstanceJeu instance, int horizon) {
        if(horizon==0){
            return evaluation_joueur(instance, instance.getJoueurCourant());
        }
        int valeur = Integer.MAX_VALUE;
        ArrayList<ArrayList<Coup>> coups_possibles = coupsPossibles(instance);
        for(int i = 0; i<coups_possibles.size(); i++){
            ArrayList<Coup> coupDuo = coups_possibles.get(i);
            //si impossible de placer un bâtiment
            if(coupDuo.get(1)==null) valeur = Integer.MAX_VALUE;
            else{
                InstanceJeu successeur = appliquerCoup(instance, coupDuo.get(0), coupDuo.get(1));
                valeur = Math.min(valeur, calculCoups_joueur_A(successeur, horizon-1));
            }
        }
        return valeur;
    }

    private static void updateBatimentsJoueur(int batimentChoisit, Joueur jCourantCopie, int hauteur) {
        if(batimentChoisit == TEMPLE) jCourantCopie.incrementeTemple();
        else if(batimentChoisit == TOUR) jCourantCopie.incrementeTour();
        else{
            if(hauteur>=3) jCourantCopie.incrementeHutte();
            if(hauteur==2) jCourantCopie.incrementeHutte();
            jCourantCopie.incrementeHutte();
        }
    }
    public ArrayList<Tuile> copyPioche(ArrayList<Tuile> pioche){
        ArrayList<Tuile> piocheCopie = new ArrayList<>();
        for (Tuile tuileCourante:pioche) {
            piocheCopie.add(tuileCourante);
        }
        return piocheCopie;
    }

    public int evaluation_joueur(InstanceJeu instance, int num_joueur){
        Joueur j = instance.getJoueur(num_joueur);
        //si le joueur a posé tous ses bâtiments de 2 types, il a gagné
        if((j.getNbHuttes() == 0 && j.getNbTemples() == 0)||(j.getNbTemples() ==0 && j.getNbTours() ==0)||(j.getNbHuttes()==0 && j.getNbTours()==0)){
            return Integer.MAX_VALUE;
        }
        //si le joueur ne peut plus construire de huttes, il doit placer un temple ou une tour
        if(j.getNbHuttes() == 0){
            return 0;
        }
        //sinon on calcule le score du joueur
        int score_joueur = j.getNbHuttesPlacees() * poids_hutte;
        score_joueur += j.getNbToursPlacees() * poids_tour;
        score_joueur += j.getNbTemplesPlaces() * poids_temple;

        return score_joueur;
    }

}
