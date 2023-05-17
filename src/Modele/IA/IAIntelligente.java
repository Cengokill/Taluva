package Modele.IA;

import Modele.Jeu.Coup;
import Modele.Jeu.CoupValeur;
import Modele.Jeu.Joueur;
import Modele.Jeu.Plateau.Hexagone;
import Modele.Jeu.Plateau.Plateau;
import Modele.Jeu.Plateau.Tuile;
import Structures.Position.Point2D;
import Structures.Position.Position;
import Structures.Position.TripletDePosition;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;

public class IAIntelligente extends AbstractIA implements Serializable {

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
    @Override
    public CoupValeur joue() {
        r = new Random();
        ArrayList<Tuile> pioche = ajoutTuilesPioche(jeu.getPioche());
        Plateau plateauIA = jeu.getPlateau().copie();
        plateauIA.nbHutteDisponiblesJoueur = jeu.getJoueurCourantClasse().getNbHuttes();
        this.instance = new InstanceJeu(pioche, plateauIA, jeu.getJoueurs(), jeu.getNumJoueurCourant(),false);
        return choisirCoupTuile(jeu.getTuileCourante());
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

    private CoupValeur choisirCoupTuile(Tuile tuile){
        int i=0, score_max = Integer.MAX_VALUE;
        int score_courant;
        ArrayList<CoupValeur> coupARenvoyer = new ArrayList<>();
        CoupValeur coupAFaire;
        ArrayList<TripletDePosition> coupsTuilePossibles = instance.getPlateau().getTripletsPossibles();
        while(i < coupsTuilePossibles.size()){
            TripletDePosition tripletCourant = coupsTuilePossibles.get(i);
            InstanceJeu instanceCourante = new InstanceJeu(copyPioche(instance.pioche),instance.getPlateau().copie(),instance.getJoueurs(), instance.jCourant,instance.estFinJeu);
            Plateau plateauCopie = instanceCourante.getPlateau();
            Coup coupT = new Coup(instanceCourante.getJoueurCourant(),tripletCourant.getVolcan().ligne(),tripletCourant.getVolcan().colonne(),tripletCourant.getTile1().ligne(),tripletCourant.getTile1().colonne(),tuile.biome0,tripletCourant.getTile2().ligne(),tripletCourant.getTile2().colonne(),tuile.biome1);
            plateauCopie.joueCoup(coupT);
            coupAFaire = choisirCoupBatiment(coupT,instanceCourante);
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

    private CoupValeur choisirCoupBatiment(Coup coupT, InstanceJeu instance) {
        int i=0, score_max = Integer.MIN_VALUE;
        int score_courant;
        ArrayList<Coup> coupsBatimentARenvoyer = new ArrayList<>();
        InstanceJeu instanceCourante = new InstanceJeu(copyPioche(instance.pioche),instance.getPlateau().copie(),instance.getJoueurs(), instance.jCourant,instance.estFinJeu);
        ArrayList<Coup> coupsBatimentPossible = getTousLesCoupsPossiblesDesBatiments(instanceCourante);

        while(i < coupsBatimentPossible.size()){
            Plateau plateauCopie2 = instanceCourante.getPlateau().copie();
            Joueur joueurCourant = instance.getJoueur(instance.getJoueurCourant());
            Coup coupCourant = coupsBatimentPossible.get(i);
            ArrayList<Coup> coupPropagation = new ArrayList<>();
            if (coupCourant.typePlacement == HUTTE){
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
            }
            InstanceJeu instanceAEvaluer = new InstanceJeu(null,plateauCopie2,instance.getJoueurs(), instance.jCourant,instance.estFinJeu);
            Joueur joueurAEvaluer = instanceAEvaluer.getJoueur(instanceAEvaluer.jCourant);
            int batiment = 1;

            if(coupPropagation.size()>0){ // Huttes
                for(Coup coupPropager: coupPropagation){
                    instanceAEvaluer.getPlateau().joueCoup(coupPropager);
                    augmenteBatimentsJoueur(HUTTE,joueurAEvaluer,instanceAEvaluer.getPlateau().getHauteurTuile(coupPropager.batimentLigne,coupPropager.batimentColonne));
                }
            }else{ // Tour ou Temple
                //System.out.println("TEMPLE");
                instanceAEvaluer.getPlateau().joueCoup(coupCourant);
                if(coupCourant.typePlacement==2) batiment = TEMPLE;
                else if (coupCourant.typePlacement==3) batiment = TOUR;
                augmenteBatimentsJoueur(batiment,joueurAEvaluer,0);
                //System.out.println("nbTemplesAvant: "+joueurAEvaluer.getNbTemplesPlaces());
            }

            // On evalue la nouvelle instance
            score_courant = evaluationScoreInstance(instanceAEvaluer);
            if(score_courant == score_max){
                coupsBatimentARenvoyer.add(coupCourant);
            }else if(score_courant > score_max){
                coupsBatimentARenvoyer = new ArrayList<>();
                coupsBatimentARenvoyer.add(coupCourant);
                score_max = score_courant;
            }

            if(coupPropagation.size()>0){
                for(Coup coupPropager: coupPropagation){
                    diminueBatimentsJoueur(HUTTE,joueurAEvaluer,instanceAEvaluer.getPlateau().getHauteurTuile(coupPropager.batimentLigne,coupPropager.batimentColonne));
                }
            }else{
                diminueBatimentsJoueur(batiment,joueurAEvaluer,0);
                //System.out.println("nbTemplesApres: "+joueurAEvaluer.getNbTemplesPlaces());
            }
            i++;
        }
        if(coupsBatimentARenvoyer.size()==0){
            return null;
        }
        return new CoupValeur(coupT,coupsBatimentARenvoyer.get(r.nextInt(coupsBatimentARenvoyer.size())),score_max);
    }

    private ArrayList<Coup> getTousLesCoupsPossiblesDesBatiments(InstanceJeu instanceCourante){
        Plateau plateauCopie = instanceCourante.getPlateau().copie();
        ArrayList<Coup> coupsPossiblesARenvoyer = new ArrayList<>();
        ArrayList<Position> positionsPossiblesBatiment = plateauCopie.getPositions_libres_batiments();
        byte joueur_courant = instanceCourante.getJoueurCourant();
        // On parcours toutes les positions possibles
        for (int position = 0; position < positionsPossiblesBatiment.size(); position++) {
            Coup coupB = null;
            Position positionCourante = positionsPossiblesBatiment.get(position);
            int[] batimentsPlacable = plateauCopie.getBatimentPlacable(positionCourante.ligne(), positionCourante.colonne(), joueur_courant);
            //On parcourt tous les choix de bâtiments possibles
            for (int batimentChoisit = 0; batimentChoisit < batimentsPlacable.length; batimentChoisit++) {
                //si le bâtiment est plaçable
                if (batimentsPlacable[batimentChoisit] == 1) {
                    Joueur jCourantCopie = instanceCourante.getJoueur(joueur_courant);
                    Joueur[] joueurs = instanceCourante.getJoueurs();
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
                        int hauteurCourante = instance.getPlateau().getHauteurTuile(positionCourante.ligne(), positionCourante.colonne());
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
                        coupB = new Coup(joueur_courant, positionCourante.ligne(), positionCourante.colonne(), (byte) (batimentsPlacable[batimentChoisit]+1));
                        plateauCopie2.placeBatiment(joueur_courant, positionCourante.ligne(), positionCourante.colonne(), (byte) (batimentsPlacable[batimentChoisit]));
                        //on supprime la position du bâtiment qui n'est plus libre
                        Position posASupprimer = new Position(positionCourante.ligne(), positionCourante.colonne());
                        plateauCopie2.supprimeElementNew(posASupprimer);
                    }
                    joueurs[joueur_courant] = jCourantCopie;
                    coupsPossiblesARenvoyer.add(coupB);
                }
            }
        }
        return coupsPossiblesARenvoyer;
    }

    private int evaluationScoreInstance(InstanceJeu instanceCourante){
        // idée potentielle faire un calcul sur un coeur et l'autre sur un autre coeur multithreadé ca
        int scoreJoueur = evaluationInstance(instanceCourante,true);
        //int scoreAdverse = evaluationInstance(instanceCourante,false);

        return scoreJoueur;//-scoreAdverse;
    }

    private int evaluationInstance(InstanceJeu instanceCourante, boolean joueurCourant){
        Joueur joueur;
        if(!joueurCourant) instanceCourante.changeJoueur();
        joueur = instanceCourante.getJoueur(instanceCourante.getJoueurCourant());

        //si le joueur a posé tous ses bâtiments de 2 types, il a gagné
        if((joueur.getNbHuttes() == 0 && joueur.getNbTemples() == 0)||(joueur.getNbTemples() ==0 && joueur.getNbTours() ==0)||(joueur.getNbHuttes()==0 && joueur.getNbTours()==0)){
            return Integer.MAX_VALUE;
        }
        // On veut eviter le cas où le joueur n'a plus de hutte
        if(joueur.getNbHuttes() == 0){
            return 0;
        }

        int score = evaluerVillages(instanceCourante,joueur);

        if(!joueurCourant) instanceCourante.changeJoueur();
        return score;
    }

    private int tailleVillage(InstanceJeu instanceJeu, byte numJoueur, int i, int j){
        int taille = 0;
        Plateau plateau = instanceJeu.getPlateau();
        // Les positions de chaque hutte du village
        ArrayList<Point2D> pointsVillage = instanceJeu.getPlateau().positionsBatsVillage(i,j,numJoueur);
        for(Point2D posCourante : pointsVillage){
            if(plateau.getTuile(posCourante.getPointX(), posCourante.getPointY()).getNumJoueur()==numJoueur){
                System.out.println("numJoueur: "+numJoueur);
                System.out.println("posVillage x: "+posCourante.getPointX()+" y: "+posCourante.getPointY());
                taille++;
            }
        }
        return taille;
    }

    private int evaluerVillages(InstanceJeu instanceJeu,Joueur joueurAEvaluer){
        //System.out.println("Numero joueur a evaluer : "+joueurAEvaluer.getNumero());
        int nombreVillages = joueurAEvaluer.getNbVillages();
        int score = nombreVillages;
        int nbHuttesPlacables = 0;
        int nbTemplesPlacables = 0;
        int nbToursPlacables = 0;

        // On regarde tous les batiments placables par le joueurAEvaluer sur l'instance courante
        for(int i=0;i<instanceJeu.getPlateau().getLIGNES();i++){
            for(int j=0;j<instanceJeu.getPlateau().getCOLONNES();j++){
                if(instanceJeu.getPlateau().getTuile(i,j).getBiomeTerrain()!= Hexagone.VIDE){
                    // On calcul les batiments possables
                    int hauteurCourante = instanceJeu.getPlateau().getHauteurTuile(i,j);
                    int[] batimentsPlacables = instanceJeu.getPlateau().getBatimentPlacable(i,j,joueurAEvaluer.getNumero());
                    if(batimentsPlacables[0]==1) nbTemplesPlacables++;
                    if(batimentsPlacables[1]==1) nbHuttesPlacables = nbHuttesPlacables+hauteurCourante;
                    if(batimentsPlacables[2]==1) nbToursPlacables++;


                    // On calcul la taille du village
                    // !! Attention ici faut faire une verification pour pas verifier plusieurs fois le meme village
                    /*if(instanceJeu.getPlateau().getBatiment(i,j)!=0){
                       if((tailleVillage(instanceJeu,joueurAEvaluer.getNumero(),i,j))>1){
                           score = 90000;
                           //instanceJeu.getPlateau().affiche();
                       }
                    }*/
                }
            }
        }

        // Score de previsualisation (en prévision du futur)
        score += (nbTemplesPlacables*joueurAEvaluer.getNbTemples())*(poids_temple/5);
        score += nbHuttesPlacables*joueurAEvaluer.getNbHuttes()*(poids_hutte/5);
        score += (nbToursPlacables*joueurAEvaluer.getNbTours())*(poids_tour/5);

        // Score de placement
        score += joueurAEvaluer.getNbHuttesPlacees() * poids_hutte;
        score += joueurAEvaluer.getNbToursPlacees() * poids_tour;
        score += joueurAEvaluer.getNbTemplesPlaces() * 100000000; // j'avais mis 1000 ca poser plus de temple

        if(joueurAEvaluer.getNbToursPlacees()!=0){
            //System.out.println("Temple placé");
        }

        return score;
    }

}
