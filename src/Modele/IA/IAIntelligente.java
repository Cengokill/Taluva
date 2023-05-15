package Modele.IA;

import Modele.Jeu.Coup;
import Modele.Jeu.CoupValeur;
import Modele.Jeu.Joueur;
import Modele.Jeu.Plateau.Plateau;
import Modele.Jeu.Plateau.Tuile;
import Structures.Position.Point2D;
import Structures.Position.Position;
import Structures.Position.TripletDePosition;

import java.lang.reflect.Array;
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
    private CoupValeur coupValeur;

    public IAIntelligente(byte n) {
        super(IA, n, "IA"+n);
    }

    InstanceJeu instance;

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

    private CoupValeur choisirCoupTuile(Tuile tuile){
        int i=0, score_max = Integer.MAX_VALUE;
        int score_courant;
        ArrayList<CoupValeur> coupARenvoyer = new ArrayList<>();
        CoupValeur coupAFaire;
        ArrayList<ArrayList<TripletDePosition>> listeTripletsPossible = instance.getPlateau().getListeTripletsPossible();
        ArrayList<TripletDePosition> coupsTuilePossibles = listeTripletsPossible.get(listeTripletsPossible.size()-1);

        while(i < coupsTuilePossibles.size()){
            TripletDePosition tripletCourant = coupsTuilePossibles.get(i);
            Coup coupT = new Coup(instance.getJoueurCourant(),tripletCourant.getVolcan().ligne(),tripletCourant.getVolcan().colonne(),tripletCourant.getTile1().ligne(),tripletCourant.getTile1().colonne(),tuile.biome0,tripletCourant.getTile2().ligne(),tripletCourant.getTile2().colonne(),tuile.biome1);
            instance.getPlateau().joueCoup(coupT);
            coupAFaire = choisirCoupBatiment(coupT);
            if(coupAFaire!=null){
                score_courant = coupAFaire.getValeur();
                // si le coup est aussi bien que notre meilleur on le rajoute
                if(score_courant==score_max){
                    coupARenvoyer.add(coupAFaire);
                }
                // si le coup est mieux on efface la liste, et on met la nouvelle valeur
                else{
                    score_max = score_courant;
                    coupARenvoyer = new ArrayList<>();
                    coupARenvoyer.add(coupAFaire);
                }
            }
            // TODO annuler le placement dans instance
            instance.annuler();
            i++;
        }
        // On renvoie un coup des coups optimaux calculés
        if(coupARenvoyer.size()==0){
            System.out.println("L'IA ne peut pas jouer");
            return null;
        }
        return coupARenvoyer.get(r.nextInt(coupARenvoyer.size()));
    }


    private static void augmenteBatimentsJoueur(int batimentChoisit, Joueur jCourantCopie, int hauteur) {
        if(batimentChoisit == TEMPLE) jCourantCopie.incrementeTemple();
        else if(batimentChoisit == TOUR) jCourantCopie.incrementeTour();
        else{
            if(hauteur>=3) jCourantCopie.incrementeHutte();
            if(hauteur==2) jCourantCopie.incrementeHutte();
            jCourantCopie.incrementeHutte();
        }
    }

    private static Joueur diminueBatimentsJoueur(int batimentChoisit, Joueur jCourantCopie, int hauteur) {
        if(batimentChoisit == TEMPLE) jCourantCopie.decrementeTemple();
        else if(batimentChoisit == TOUR) jCourantCopie.decrementeTour();
        else{
            if(hauteur>=3) jCourantCopie.decrementeHutte();
            if(hauteur==2) jCourantCopie.decrementeHutte();
            jCourantCopie.decrementeHutte();
        }
        return jCourantCopie;
    }



    private CoupValeur choisirCoupBatiment(Coup coupT) {
        int i=0, score_max = Integer.MIN_VALUE;
        int score_courant;
        ArrayList<Coup> coupsBatimentARenvoyer = new ArrayList<>();
        ArrayList<Coup> coupsBatimentPossible = getTousLesCoupsPossiblesDesBatiments();

        while(i < coupsBatimentPossible.size()){
            Coup coupCourant = coupsBatimentPossible.get(i);
            ArrayList<Coup> coupPropagation = new ArrayList<>();
            /*if (coupCourant.typePlacement == HUTTE){
                //On créer un tableau contenant toutes les coordonées où l'on doit propager
                ArrayList<Point2D> aPropager = instance.getPlateau().previsualisePropagation(coupCourant.batimentLigne, coupCourant.batimentColonne, instance.getJoueurCourant());
                //On place la hutte classique sans propagation
                coupPropagation.add(new Coup(instance.getJoueurCourant(), coupCourant.batimentLigne, coupCourant.batimentColonne, (byte) HUTTE));
                // On récupère le nombre de huttes disponibles pour le joueur courant
                int nbHuttesDispo = instance.getPlateau().nbHutteDisponiblesJoueur - (instance.getPlateau().getHauteurTuile(coupCourant.batimentLigne, coupCourant.batimentColonne));

                while (aPropager.size() != 0) {
                    Point2D posCourantePropagation = aPropager.remove(0);
                    int hauteurCourante = instance.getPlateau().getHauteurTuile(posCourantePropagation.getPointX(), posCourantePropagation.getPointY());
                    if (nbHuttesDispo >= hauteurCourante) {
                        coupPropagation.add(new Coup(instance.getJoueurCourant(), posCourantePropagation.getPointX(),posCourantePropagation.getPointY(), (byte) HUTTE));
                        nbHuttesDispo -= hauteurCourante;
                    }
                }
            }*/
            Joueur joueurCourant = instance.getJoueur(instance.getJoueurCourant());
            int batiment = 1;
            /*if(coupPropagation.size()>0){
                for(Coup coupPropager: coupPropagation){
                    instance.getPlateau().joueCoup(coupPropager);
                    augmenteBatimentsJoueur(HUTTE,joueurCourant,instance.getPlateau().getHauteurTuile(coupPropager.batimentLigne,coupPropager.batimentColonne));
                }
            }else{*/
                instance.getPlateau().joueCoup(coupCourant);
                if(coupCourant.typePlacement==2){
                    System.out.println("en x: "+coupCourant.batimentLigne+" y: "+coupCourant.batimentColonne);
                    batiment = TEMPLE;
                }
                else if (coupCourant.typePlacement==3) batiment = TOUR;
                augmenteBatimentsJoueur(batiment,joueurCourant,0);
            //}

            score_courant = Evaluation(joueurCourant);
            if(score_courant == score_max){
                coupsBatimentARenvoyer.add(coupCourant);
            }else if(score_courant > score_max){
                coupsBatimentARenvoyer = new ArrayList<>();
                coupsBatimentARenvoyer.add(coupCourant);
                score_max = score_courant;
            }
            // TODO annuler le coup sur instance
            /*if(coupPropagation.size()>0){
                for(Coup coupPropager: coupPropagation){
                    diminueBatimentsJoueur(HUTTE,joueurCourant,instance.getPlateau().getHauteurTuile(coupPropager.batimentLigne,coupPropager.batimentColonne));
                    instance.annuler();
                }
            }else{*/
                diminueBatimentsJoueur(batiment,joueurCourant,0);
                instance.annuler();
            //}
            i++;
        }
        if(coupsBatimentARenvoyer.size()==0){
            return null;
        }
        return new CoupValeur(coupT,coupsBatimentARenvoyer.get(r.nextInt(coupsBatimentARenvoyer.size())),score_max);
    }

    public int Evaluation(Joueur joueur){
        //if(joueur.getNbTemplesPlaces()!=0) System.out.println("NB TEMPLE PLACES "+joueur.getNbTemplesPlaces());
        //si le joueur a posé tous ses bâtiments de 2 types, il a gagné
        if((joueur.getNbHuttes() == 0 && joueur.getNbTemples() == 0)||(joueur.getNbTemples() ==0 && joueur.getNbTours() ==0)||(joueur.getNbHuttes()==0 && joueur.getNbTours()==0)){
            System.out.println("infini");
            return Integer.MAX_VALUE;
        }
        //si le joueur ne peut plus construire de huttes, il doit placer un temple ou une tour
        if(joueur.getNbHuttes() == 0){
            System.out.println("c'est 0");
            return 0;
        }
        //sinon on calcule le score du joueur
        int score_joueur = joueur.getNbHuttesPlacees() * poids_hutte;
        score_joueur += joueur.getNbToursPlacees() * poids_tour;
        score_joueur += joueur.getNbTemplesPlaces() * poids_temple;

        return score_joueur;
    }


    private ArrayList<Coup> getTousLesCoupsPossiblesDesBatiments(){
        ArrayList<Coup> coupsPossiblesARenvoyer = new ArrayList<>();
        ArrayList<Position> positionsPossiblesBatiment = instance.getPlateau().getPositions_libres_batiments();
        byte joueur_courant = instance.getJoueurCourant();

        // On parcours toutes les positions possibles
        for (int position = 0; position < positionsPossiblesBatiment.size(); position++) {
            Coup coupB = null;
            Position positionCourante = positionsPossiblesBatiment.get(position);
            int[] batimentsPlacable = instance.getPlateau().getBatimentPlacable(positionCourante.ligne(), positionCourante.colonne(), joueur_courant);

            //On parcourt tous les choix de bâtiments possibles
            for (int batimentChoisit = 0; batimentChoisit < batimentsPlacable.length; batimentChoisit++) {

                //si le bâtiment est plaçable
                if (batimentsPlacable[batimentChoisit] == 1) {
                    Joueur jCourantCopie = instance.getJoueur(joueur_courant);
                    Joueur[] joueurs = instance.getJoueurs();
                    //si HUTTE (propagation potentielle)
                    if (batimentChoisit == HUTTE){
                        //On créer un tableau contenant toutes les coordonées où l'on doit propager
                        ArrayList<Point2D> aPropager = instance.getPlateau().previsualisePropagation(positionCourante.ligne(), positionCourante.colonne(), joueur_courant);
                        //On place la hutte classique sans propagation
                        coupB = new Coup(joueur_courant, positionCourante.ligne(), positionCourante.colonne(), (byte) HUTTE);
                        instance.getPlateau().placeBatiment(joueur_courant, positionCourante.ligne(), positionCourante.colonne(), (byte) HUTTE);
                        //La position actuelle n'est plus libre
                        Position posASupprimer = new Position(positionCourante.ligne(), positionCourante.colonne());
                        //instance.getPlateau().supprimeElementNew(posASupprimer);
                        //On met a jour le nombre de hutte restantes
                        int hauteurCourante = instance.getPlateau().getHauteurTuile(positionCourante.ligne(), positionCourante.colonne());
                        // On récupère le nombre de huttes disponibles pour le joueur courant
                        int nbHuttesDispo = instance.getPlateau().nbHutteDisponiblesJoueur - (instance.getPlateau().getHauteurTuile(positionCourante.ligne(), positionCourante.colonne()));

                        while (aPropager.size() != 0) {
                            Point2D posCourantePropagation = aPropager.remove(0);
                            hauteurCourante = instance.getPlateau().getHauteurTuile(posCourantePropagation.getPointX(), posCourantePropagation.getPointY());
                            if (nbHuttesDispo >= hauteurCourante) {
                                instance.getPlateau().placeBatiment(joueur_courant, posCourantePropagation.getPointX(), posCourantePropagation.getPointY(), (byte) HUTTE);
                                // On place une hutte dessus, donc plus disponible
                                posASupprimer = new Position(posCourantePropagation.getPointX(), posCourantePropagation.getPointY());
                                //instance.getPlateau().supprimeElementNew(posASupprimer);
                                nbHuttesDispo -= hauteurCourante;
                            }
                        }
                    } else { // Si nous ne posons pas de hutte, il n'y a pas de propagation
                        coupB = new Coup(joueur_courant, positionCourante.ligne(), positionCourante.colonne(), (byte) (batimentsPlacable[batimentChoisit]+1));
                        instance.getPlateau().placeBatiment(joueur_courant, positionCourante.ligne(), positionCourante.colonne(), (byte) (batimentsPlacable[batimentChoisit]+1));
                        //on supprime la position du bâtiment qui n'est plus libre
                        Position posASupprimer = new Position(positionCourante.ligne(), positionCourante.colonne());
                        //instance.getPlateau().supprimeElementNew(posASupprimer);
                    }
                    coupsPossiblesARenvoyer.add(coupB);
                    // TODO annuler l'action précédente
                    instance.annuler();
                }
            }
        }
        return coupsPossiblesARenvoyer;
    }


    @Override
    public CoupValeur joue() {
        r = new Random();
        ArrayList<Tuile> pioche = ajoutTuilesPioche(jeu.getPioche());
        Plateau plateauIA = jeu.getPlateau().copie();
        plateauIA.nbHutteDisponiblesJoueur = jeu.getJoueurCourantClasse().getNbHuttes();
        this.instance = new InstanceJeu(pioche, plateauIA, jeu.getJoueurs(), jeu.getNumJoueurCourant(),false);
        return choisirCoupTuile(jeu.getTuileCourante());
    }
}
