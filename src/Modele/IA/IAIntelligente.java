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

    public IAIntelligente() {
        super(IA, "IA");
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
        ArrayList<TripletDePosition> coupsTuilePossibles = instance.getPlateau().getTripletsPossibles();
        while(i < coupsTuilePossibles.size()){
            TripletDePosition tripletCourant = coupsTuilePossibles.get(i);
            InstanceJeu instanceCourante = new InstanceJeu(copyPioche(instance.pioche),instance.getPlateau().copie(),instance.getJoueurs(), instance.jCourant,instance.estFinJeu);
            Plateau plateauCopie = instanceCourante.getPlateau(); // TODO A RETIRER QUAND YAURA ANNULER
            Coup coupT = new Coup(instanceCourante.getJoueurCourant(),tripletCourant.getVolcan().ligne(),tripletCourant.getVolcan().colonne(),tripletCourant.getTile1().ligne(),tripletCourant.getTile1().colonne(),tuile.biome0,tripletCourant.getTile2().ligne(),tripletCourant.getTile2().colonne(),tuile.biome1);
            //instance.getPlateau().joueCoup(coupT);
            plateauCopie.joueCoup(coupT); // TODO A RETIRER QUAND YAURA ANNULER
            coupAFaire = choisirCoupBatiment(coupT,instanceCourante); // TODO A RETIRER QUAND YAURA ANNULER l'arguement PlateauCopie
            if(coupARenvoyer!=null){
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
            //instance.annuler();
            i++;
        }
        // On renvoie un coup des coups optimaux calculés
        if(coupARenvoyer.size()==0){
            System.out.println("L'IA ne peut pas jouer");
            return null;
        }
        return coupARenvoyer.get(r.nextInt(coupARenvoyer.size()));
    }


    public ArrayList<Tuile> copyPioche(ArrayList<Tuile> pioche){
        ArrayList<Tuile> piocheCopie = new ArrayList<>();
        for (Tuile tuileCourante:pioche) {
            piocheCopie.add(tuileCourante);
        }
        return piocheCopie;
    }

    private CoupValeur choisirCoupBatiment(Coup coupT, InstanceJeu instance) { // TODO A RETIRER QUAND YAURA ANNULER l'arguement plateau
        int i=0, score_max = Integer.MIN_VALUE;
        int score_courant;
        ArrayList<Coup> coupsBatimentARenvoyer = new ArrayList<>();
        InstanceJeu instanceCourante = new InstanceJeu(copyPioche(instance.pioche),instance.getPlateau().copie(),instance.getJoueurs(), instance.jCourant,instance.estFinJeu);
        ArrayList<Coup> coupsBatimentPossible = getTousLesCoupsPossiblesDesBatiments(instanceCourante);

        while(i < coupsBatimentPossible.size()){

            Plateau plateauCopie2 = instanceCourante.getPlateau().copie(); // TODO A RETIRER QUAND YAURA ANNULER
            Coup coupCourant = coupsBatimentPossible.get(i);
            //instance.getPlateau().joueCoup(coupCourant);
            plateauCopie2.joueCoup(coupCourant); // TODO A RETIRER QUAND YAURA ANNULER
            score_courant = Evaluation(instanceCourante);
            if(score_courant == score_max){
                coupsBatimentARenvoyer.add(coupCourant);
            }else if(score_courant > score_max){
                coupsBatimentARenvoyer = new ArrayList<>();
                coupsBatimentARenvoyer.add(coupCourant);
                score_max = score_courant;
            }
            // TODO annuler le coup sur instance
            //instance.plateau.affiche();
            //instance.annuler();
            i++;
        }
        if(coupsBatimentARenvoyer.size()==0){
            return null;
        }
        return new CoupValeur(coupT,coupsBatimentARenvoyer.get(r.nextInt(coupsBatimentARenvoyer.size())),score_max);
    }

    public int Evaluation(InstanceJeu instanceCourante){
        Joueur j = instanceCourante.getJoueur(instanceCourante.getJoueurCourant());
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


    private ArrayList<Coup> getTousLesCoupsPossiblesDesBatiments(InstanceJeu instanceCourante){
        Plateau plateauCopie = instanceCourante.getPlateau().copie();
        ArrayList<Coup> coupsPossiblesARenvoyer = new ArrayList<>();
        ArrayList<Position> positionsPossiblesBatiment = plateauCopie.getPositions_libres_batiments(); // TODO ICI ANNULER TMTC
        //ArrayList<Position> positionsPossiblesBatiment = instance.getPlateau().getPositions_libres_batiments();  // TODO ICI ANNULER TMTC
        byte joueur_courant = instanceCourante.getJoueurCourant();
        // On parcours toutes les positions possibles
        for (int position = 0; position < positionsPossiblesBatiment.size(); position++) {
            Coup coupB = null;
            Position positionCourante = positionsPossiblesBatiment.get(position);
            //int[] batimentsPlacable = instance.getPlateau().getBatimentPlacable(positionCourante.ligne(), positionCourante.colonne(), joueur_courant);  // TODO ICI ANNULER TMTC
            int[] batimentsPlacable = plateauCopie.getBatimentPlacable(positionCourante.ligne(), positionCourante.colonne(), joueur_courant); // TODO ICI ANNULER TMTC
            //On parcourt tous les choix de bâtiments possibles
            for (int batimentChoisit = 0; batimentChoisit < batimentsPlacable.length; batimentChoisit++) {
                //si le bâtiment est plaçable
                if (batimentsPlacable[batimentChoisit] == 1) {
                    Joueur jCourantCopie = instanceCourante.getJoueur(joueur_courant);
                    Joueur[] joueurs = instanceCourante.getJoueurs();
                    Plateau plateauCopie2 = plateauCopie.copie(); // TODO ICI ANNULER TMTC
                    //si HUTTE (propagation potentielle)
                    if (batimentChoisit == HUTTE){
                        //On créer un tableau contenant toutes les coordonées où l'on doit propager
                        //ArrayList<Point2D> aPropager = instance.getPlateau().previsualisePropagation(positionCourante.ligne(), positionCourante.colonne(), joueur_courant); // TODO ICI ANNULER TMTC
                        ArrayList<Point2D> aPropager = plateauCopie2.previsualisePropagation(positionCourante.ligne(), positionCourante.colonne(), joueur_courant); // TODO ICI ANNULER TMTC
                        //On place la hutte classique sans propagation
                        coupB = new Coup(joueur_courant, positionCourante.ligne(), positionCourante.colonne(), (byte) HUTTE);
                        //instance.getPlateau().placeBatiment(joueur_courant, positionCourante.ligne(), positionCourante.colonne(), (byte) HUTTE); // TODO ICI ANNULER TMTC
                        plateauCopie2.placeBatiment(joueur_courant, positionCourante.ligne(), positionCourante.colonne(), (byte) HUTTE); // TODO ICI ANNULER TMTC
                        //La position actuelle n'est plus libre
                        Position posASupprimer = new Position(positionCourante.ligne(), positionCourante.colonne()); // TODO ICI ANNULER TMTC
                        plateauCopie2.supprimeElementNew(posASupprimer); // TODO ICI ANNULER TMTC
                        //On met a jour le nombre de hutte restantes
                        int hauteurCourante = instance.getPlateau().getHauteurTuile(positionCourante.ligne(), positionCourante.colonne());
                        // On récupère le nombre de huttes disponibles pour le joueur courant
                        //int nbHuttesDispo = instance.getPlateau().nbHutteDisponiblesJoueur - (instance.getPlateau().getHauteurTuile(positionCourante.ligne(), positionCourante.colonne())); // TODO ICI ANNULER TMTC
                        int nbHuttesDispo = plateauCopie2.nbHutteDisponiblesJoueur - (plateauCopie2.getHauteurTuile(positionCourante.ligne(), positionCourante.colonne())); // TODO ICI ANNULER TMTC
                        while (aPropager.size() != 0) {
                            Point2D posCourantePropagation = aPropager.remove(0);
                            //hauteurCourante = instance.getPlateau().getHauteurTuile(posCourantePropagation.getPointX(), posCourantePropagation.getPointY()); // TODO ICI ANNULER TMTC
                            hauteurCourante = plateauCopie2.getHauteurTuile(posCourantePropagation.getPointX(), posCourantePropagation.getPointY()); // TODO ICI ANNULER TMTC
                            if (nbHuttesDispo >= hauteurCourante) {
                                //instance.getPlateau().placeBatiment(joueur_courant, posCourantePropagation.getPointX(), posCourantePropagation.getPointY(), (byte) HUTTE); // TODO ICI ANNULER TMTC
                                plateauCopie2.placeBatiment(joueur_courant, posCourantePropagation.getPointX(), posCourantePropagation.getPointY(), (byte) HUTTE); // TODO ICI ANNULER TMTC
                                // On place une hutte dessus, donc plus disponible
                                posASupprimer = new Position(posCourantePropagation.getPointX(), posCourantePropagation.getPointY()); // TODO ICI ANNULER TMTC
                                plateauCopie2.supprimeElementNew(posASupprimer); // TODO ICI ANNULER TMTC
                                nbHuttesDispo -= hauteurCourante;
                            }
                        }
                    } else { // Si nous ne posons pas de hutte, il n'y a pas de propagation
                        coupB = new Coup(joueur_courant, positionCourante.ligne(), positionCourante.colonne(), (byte) (batimentsPlacable[batimentChoisit]+1));
                        //instance.getPlateau().placeBatiment(joueur_courant, positionCourante.ligne(), positionCourante.colonne(), (byte) (batimentsPlacable[batimentChoisit])); // TODO ICI ANNULER TMTC
                        plateauCopie2.placeBatiment(joueur_courant, positionCourante.ligne(), positionCourante.colonne(), (byte) (batimentsPlacable[batimentChoisit])); // TODO ICI ANNULER TMTC
                        //on supprime la position du bâtiment qui n'est plus libre
                        Position posASupprimer = new Position(positionCourante.ligne(), positionCourante.colonne()); // TODO ICI ANNULER TMTC
                        plateauCopie2.supprimeElementNew(posASupprimer); // TODO ICI ANNULER TMTC
                    }
                    joueurs[joueur_courant] = jCourantCopie;
                    coupsPossiblesARenvoyer.add(coupB);
                    // TODO annuler l'action précédente
                    //instance.annuler();
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
