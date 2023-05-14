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
import java.util.Random;

class IAAleatoire extends AbstractIA {
    private Random r;
    private CoupValeur coupValeur = new CoupValeur();
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
        ArrayList<ArrayList<Coup>> listeCoups = coupsPossibles(instance);
        System.out.println("listeCoups size : " + listeCoups.size());
        r = new Random();
        int index = r.nextInt(listeCoups.size());
        coupValeur.coupT = listeCoups.get(index).get(0);
        coupValeur.coupB = listeCoups.get(index).get(1);
        System.out.println("duree_getTripletsPossibles : " + duree_getTripletsPossibles);
        System.out.println("duree_getPlateau_copie : " + duree_getPlateau_copie);
        System.out.println("duree_placeEtage : " + duree_placeEtage);
        System.out.println("duree_getBatimentPlacable : " + duree_getBatimentPlacable);
        System.out.println("duree_temp : " + duree_temp);
        System.out.println("duree_totale : " + duree_totale);
        return coupValeur;
    }

    @Override
    public ArrayList<ArrayList<Coup>> coupsPossibles(InstanceJeu instance){
        duree_temp = 0;
        duree_totale = 0;
        startTimeTotal = System.currentTimeMillis();
        duree_getTripletsPossibles = 0;
        duree_getPlateau_copie = 0;
        duree_placeEtage = 0;
        duree_getBatimentPlacable = 0;
        ArrayList<ArrayList<Coup>> coups_possibles = new ArrayList<>();
        byte joueur_courant = instance.getJoueurCourant();
        startTime = System.currentTimeMillis();
        ArrayList<TripletDePosition> tripletsPossibles = instance.getPlateau().getTripletsPossibles();
        endTime = System.currentTimeMillis();
        duree_getTripletsPossibles += endTime - startTime;
        ArrayList<Coup> coupDuo = new ArrayList<>();
            Tuile tuile = instance.getPioche().get(0);
            //pour chaque position possible de la tuile sur le plateau
            for (int tripletsIndex = 0; tripletsIndex < tripletsPossibles.size(); tripletsIndex++) {
                TripletDePosition tripletCourant = tripletsPossibles.get(tripletsIndex);
                Position[] points = new Position[3];
                points[0] = tripletCourant.getVolcan();
                points[1] = tripletCourant.getTile1();
                points[2] = tripletCourant.getTile2();
                for (int orientationTuile = 0; orientationTuile < 3; orientationTuile++) {
                    Coup coupT = new Coup(joueur_courant, points[orientationTuile].ligne(), points[orientationTuile].colonne(), (points[(orientationTuile + 1) % 3].ligne()), (points[(orientationTuile + 1) % 3].colonne()), tuile.biome0, (points[(orientationTuile + 2) % 3].ligne()), (points[(orientationTuile + 2) % 3].colonne()), tuile.biome1);
                    startTime = System.currentTimeMillis();
                    Plateau plateauCopie = instance.getPlateau().copie();
                    endTime = System.currentTimeMillis();
                    duree_getPlateau_copie += endTime - startTime;
                    startTime = System.currentTimeMillis();
                    plateauCopie.placeEtage(joueur_courant, points[orientationTuile].ligne(), points[orientationTuile].colonne(), (points[(orientationTuile + 1) % 3].ligne()), (points[(orientationTuile + 1) % 3].colonne()), tuile.biome0, (points[(orientationTuile + 2) % 3].ligne()), (points[(orientationTuile + 2) % 3].colonne()), tuile.biome1);
                    endTime = System.currentTimeMillis();
                    duree_placeEtage += endTime - startTime;
                    ArrayList<Position> positionsLibresBatiments = plateauCopie.getPositions_libres_batiments();
                    //On parcourt toutes les positions libres des bâtiments
                    for (int position = 0; position < positionsLibresBatiments.size(); position++) {
                        Coup coupB = null;
                        Position positionCourante = positionsLibresBatiments.get(position);
                        startTime = System.currentTimeMillis();
                        int[] batimentsPlacable = plateauCopie.getBatimentPlacable(positionCourante.ligne(), positionCourante.colonne(), joueur_courant);
                        endTime = System.currentTimeMillis();
                        duree_getBatimentPlacable += endTime - startTime;
                        //On parcourt tous les choix de bâtiments possibles
                        for (int batimentChoisit = 0; batimentChoisit < batimentsPlacable.length; batimentChoisit++) {
                            //si le bâtiment est plaçable
                            if (batimentsPlacable[batimentChoisit] == 1) {
                                Joueur jCourantCopie = instance.getJoueur(joueur_courant);
                                Joueur[] joueurs = instance.getJoueurs();
                                startTimeTemp = System.currentTimeMillis();
                                Plateau plateauCopie2 = plateauCopie.copie();
                                endTimeTemp = System.currentTimeMillis();
                                duree_temp += endTimeTemp - startTimeTemp;
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
                                            nbHuttesDispo -= hauteurCourante;
                                        }
                                    }
                                } else { // Si nous ne posons pas de hutte, il n'y a pas de propagation
                                    coupB = new Coup(joueur_courant, positionCourante.ligne(), positionCourante.colonne(), (byte) (batimentsPlacable[batimentChoisit]));
                                    plateauCopie2.placeBatiment(joueur_courant, positionCourante.ligne(), positionCourante.colonne(), (byte) (batimentsPlacable[batimentChoisit]));
                                    //on supprime la position du bâtiment qui n'est plus libre
                                    Position posASupprimer = new Position(positionCourante.ligne(), positionCourante.colonne());
                                    plateauCopie2.supprimeElementNew(posASupprimer);
                                }
                                joueurs[joueur_courant] = jCourantCopie;
                            }
                            if(coupB!=null){
                                coupDuo.add(coupT);
                                coupDuo.add(coupB);
                            }
                        }
                    }
                }
                coups_possibles.add(coupDuo);
        }
        endTimeTotal = System.currentTimeMillis();
        duree_totale = endTimeTotal - startTimeTotal;
        return coups_possibles;
    }

}
