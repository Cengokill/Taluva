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

import java.awt.*;
import java.io.Serializable;
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

    // TODO IMPLEMENTATIONS //
    // TODO -> QU'ELLE PREFERE AGRANDIR UN VILLAGE AU LIEU DE S'EPARPILLER JUSQU'A UNE CERTAINE CONDITION // (!) faut qu'elle arête au bout d'un moment
    // TODO -> QU'ELLE ARETE DE FAIRE DES VILLAGES IMMENSES (?)
    // TODO -> TROUVER UNE VALEUR DE COUP POUR LAQUELLE ON SE DIT QU'ON LA RETURN (coup de tuile et coup de bat)
    // TODO -> METTRE UN MINUTEUR DYNAMIQUE par exemple au debut 1 secondes puis 2 puis... jusqu'a 5-10 à voir
    // TODO -> POUR L'INSTANT 3 JOUEURS C'EST UNE SEULE ENTITE (si c'est pas lourd on peut faire pour chaque joueur et valoriser la pénalisation du plus en avance)

    InstanceJeu instance;
    @Override
    public CoupValeur joue() {
        r = new Random();
        ArrayList<Tuile> pioche = ajoutTuilesPioche(jeu.getPioche());
        Plateau plateauIA = jeu.getPlateau().copie();
        plateauIA.nbHuttesDisponiblesJoueur = jeu.getJoueurCourantClasse().getNbHuttes();
        this.instance = new InstanceJeu(pioche, plateauIA, jeu.getJoueurs(),jeu.getNbJoueurs(), jeu.getNumJoueurCourant(),jeu.getJoueurCourant().getCouleur(), false);
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

    private int evaluationScoreTuile(InstanceJeu instanceCourante){
        // TODO si c'est pas trop lourd, on peut rajouter de la profondeur
        final int[] scoreJoueur = new int[1];
        final int[] scoreAdverse = new int[1];

        // On regarde les points pour l'IA sur un thread
        Thread iaThread = new Thread(() -> {
            scoreJoueur[0] = evaluationTuile(instanceCourante, true);
        });

        // On regarde les points pour le joueur adverse sur un autre thread
        Thread iaThread2 = new Thread(() -> {
            scoreAdverse[0] = evaluationTuile(instanceCourante, false);
        });

        iaThread.start();
        iaThread2.start();

        try { // On attend la fin des calculs des threads
            iaThread.join();
            iaThread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return scoreJoueur[0] - scoreAdverse[0];
    }

    private int evaluationTuile(InstanceJeu instanceCourante, boolean estJoueurCourant){
        ArrayList<Joueur> joueurs = new ArrayList<>();
        Joueur joueur = instanceCourante.getJoueurCourantClasse();
        if(!estJoueurCourant){
            // On évalue les adversaires de l'IA
            for(int i = 0; i<instanceCourante.getNbJoueurs(); i++){
                Joueur joueurCourantAdverse = instanceCourante.getJoueur(i);
                if(joueurCourantAdverse!=joueur) joueurs.add(joueurCourantAdverse);
            }
        }else{
            // On évalue l'IA
            joueurs.add(joueur);
        }

        for(Joueur joueurCourant: joueurs){
            // On regarde si le joueur pourra gagner a ce tour en posant la tuile ici
            if(seraJoueurVictorieux(instanceCourante,joueurs)) return Integer.MAX_VALUE; // (!) potentiellement lourd

            // On veut éviter le cas où le joueur n'a plus de huttes
            if(joueur.getNbHuttes() == 0){
                return 0;
            }
        }
        //sinon on évalue le score de l'instance avec cette tuile
        int score = evaluerInstanceTuile(instanceCourante,joueurs);

        return score;
    }
    private int evaluerInstanceTuile(InstanceJeu instanceCourante, ArrayList<Joueur> joueurs){
        // On regarde ce que cette nouvelle tuile nous permet de construire
        int[] batimentsPlacablesNombre = getNombreBatimentsPlacable(instanceCourante,joueurs);
        int score = 0;
        // batimentsPlacablesNombre[0] -> nombre de temples constructibles par les joueurs adverses
        // batimentsPlacablesNombre[1] -> nombre de huttes constructibles par les joueurs adverses
        // batimentsPlacablesNombre[2] -> nombre de tours constructibles par les joueurs adverses
        for(Joueur joueurCourant: joueurs){
            score += (batimentsPlacablesNombre[0]*joueurCourant.getNbTemples())*(poids_temple);
            score += batimentsPlacablesNombre[1]*joueurCourant.getNbHuttes()*(poids_hutte);
            score += (batimentsPlacablesNombre[2]*joueurCourant.getNbTours())*(poids_tour);
        }

        // TODO à completer (par exemple isoler un temple ou couper un gros village en 2...)

        return score/joueurs.size();
    }

    private int[] getNombreBatimentsPlacable(InstanceJeu instanceCourante, ArrayList<Joueur> joueursAEvaluer){
        int[] batPlacables = new int[3];

        // On regarde tous les batiments placables par le joueurAEvaluer sur l'instance courante
        for(int i=0;i<instanceCourante.getPlateau().getLIGNES();i++) {
            for (int j = 0; j < instanceCourante.getPlateau().getCOLONNES(); j++) {
                if (instanceCourante.getPlateau().getHexagone(i, j).getBiomeTerrain() != Hexagone.VIDE) {
                    for(Joueur joueurCourant : joueursAEvaluer){
                        // On calcul les batiments possables
                        int hauteurCourante = instanceCourante.getPlateau().getHauteurTuile(i, j);
                        int[] batimentsPlacables = instanceCourante.getPlateau().getBatimentPlacable(i, j, joueurCourant.getCouleur());
                        if (batimentsPlacables[0] == 1) batPlacables[0]++;
                        if (batimentsPlacables[1] == 1) batPlacables[1] = batPlacables[1] + hauteurCourante;
                        if (batimentsPlacables[2] == 1) batPlacables[2]++;

                        // On calcul la taille du village
                        // !! Attention ici faut faire une verification pour pas verifier plusieurs fois le meme village
                        /*if(instanceCourante.getPlateau().getBatiment(i,j)!=0){
                           if((tailleVillage(instanceCourante,joueurCourant.getCouleur(),i,j))>1){
                               System.out.println("i: "+i+" j: "+j+" taille : " + tailleVillage(instanceCourante,joueurCourant.getCouleur(),i,j));
                           }
                        }*/
                    }
                }
            }
        }
        return batPlacables;
    }

    public void affichetripletpossible(){
        System.out.println("on affiche");
        for(TripletDePosition t : jeu.getPlateau().getTripletsPossibles()){
            System.out.println("("+ t.getVolcan().ligne()+", "+t.getVolcan().colonne()+") "+"("+ t.getTile1().ligne()+", "+t.getTile1().colonne()+") "+"("+ t.getTile2().ligne()+", "+t.getTile2().colonne()+") ");
        }
    }


    private CoupValeur choisirCoupTuile(Tuile tuile){
        int i=0, scoreBatiment_max = Integer.MIN_VALUE, scoreTuile_max = Integer.MIN_VALUE;
        int score_courant;
        ArrayList<CoupValeur> coupARenvoyer = new ArrayList<>();
        CoupValeur coupAFaire;
        ArrayList<TripletDePosition> coupsTuilePossibles = instance.getPlateau().getTripletsPossibles();
        //affichetripletpossible();
        ArrayList<Coup> coupTuileBatimentsAEvaluer = new ArrayList<>();

        // On parcourt toutes les tuiles pour trouver les meilleures.
        while(i < coupsTuilePossibles.size()){
            TripletDePosition tripletCourant = coupsTuilePossibles.get(i);
            InstanceJeu instanceCourante = new InstanceJeu(copyPioche(instance.pioche),instance.getPlateau().copie(),copyJoueurs(instance.getJoueurs()),instance.getNbJoueurs(), instance.jCourant, instance.getCouleurJoueur(), instance.estFinJeu);
            Plateau plateauCopie = instanceCourante.getPlateau();
            Coup coupT = new Coup(instanceCourante.getJoueurCourant(),tripletCourant.getVolcan().ligne(),tripletCourant.getVolcan().colonne(),tripletCourant.getTile1().ligne(),tripletCourant.getTile1().colonne(),tuile.biome0,tripletCourant.getTile2().ligne(),tripletCourant.getTile2().colonne(),tuile.biome1);
            plateauCopie.joueCoup(coupT);

            int scoreTuile_courante = evaluationScoreTuile(instanceCourante);
            // si le coup est aussi bien que notre meilleur on le rajoute
            if(scoreTuile_courante==scoreTuile_max){
                coupTuileBatimentsAEvaluer.add(coupT);
            }
            // si le coup est mieux on efface la liste, et on met la nouvelle valeur
            else if(scoreTuile_courante>scoreTuile_max){
                scoreTuile_max = scoreTuile_courante;
                coupTuileBatimentsAEvaluer = new ArrayList<>();
                coupTuileBatimentsAEvaluer.add(coupT);
            }
            i++;
        }
        // Pour toutes les meilleures tuiles trouvées :
        for(Coup coupCourant: coupTuileBatimentsAEvaluer){
            InstanceJeu instanceCourante = new InstanceJeu(copyPioche(instance.pioche),instance.getPlateau().copie(),copyJoueurs(instance.getJoueurs()),instance.getNbJoueurs(), instance.jCourant, instance.getCouleurJoueur(), instance.estFinJeu);
            Plateau plateauCopie = instanceCourante.getPlateau();
            plateauCopie.joueCoup(coupCourant);
            coupAFaire = choisirCoupBatiment(coupCourant,instanceCourante);

            AffichertailleVillage(instanceCourante,instanceCourante.getJoueurCourantClasse().getCouleur());

            if(coupAFaire!=null){
                score_courant = coupAFaire.getValeur();
                // si le coup est aussi bien que notre meilleur on le rajoute
                if(score_courant==scoreBatiment_max){
                    coupARenvoyer.add(coupAFaire);
                }
                // si le coup est mieux on efface la liste, et on met là jour la valeur du meilleur coup
                else if(score_courant>scoreBatiment_max){
                    scoreBatiment_max = score_courant;
                    coupARenvoyer = new ArrayList<>();
                    coupARenvoyer.add(coupAFaire);
                }
            }else{ // TODO on essaie toutes les tuiles qu'on a pas essayé, si on ne peut tout de meme pas jouer l'IA a perdu.
                System.out.println("coupAFaire null A DEBUGGUER -> IAintelligente 233");
            }
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

    public Joueur[] copyJoueurs(Joueur[] joueurs){
        Joueur[] joueursCopie = new Joueur[joueurs.length];
        for (int i = 0; i < joueurs.length; i++) {
            joueursCopie[i] = joueurs[i].copie();
        }
        return joueursCopie;
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
        InstanceJeu instanceCourante = new InstanceJeu(instance.pioche,instance.getPlateau().copie(),instance.getJoueurs(),instance.getNbJoueurs(), instance.jCourant, instance.getCouleurJoueur(), instance.estFinJeu);
        ArrayList<Coup> coupsBatimentPossible = getTousLesCoupsPossiblesDesBatiments(instanceCourante);

        while(i < coupsBatimentPossible.size()){
            Plateau plateauCopie2 = instanceCourante.getPlateau().copie();
            Coup coupCourant = coupsBatimentPossible.get(i);
            ArrayList<Coup> coupPropagation = new ArrayList<>();
            if (coupCourant.typePlacement == HUTTE){
                //On créer un tableau contenant toutes les coordonées où l'on doit propager
                ArrayList<Point2D> aPropager = instance.getPlateau().previsualisePropagation(coupCourant.batimentLigne, coupCourant.batimentColonne, instance.getCouleurJoueur());
                //On place la hutte classique sans propagation
                coupPropagation.add(new Coup(instance.getJoueurCourant(), instance.getCouleurJoueur(), coupCourant.batimentLigne, coupCourant.batimentColonne, Coup.HUTTE));
                // On récupère le nombre de huttes disponibles pour le joueur courant
                int nbHuttesDispo = instance.getPlateau().nbHuttesDisponiblesJoueur - (instance.getPlateau().getHauteurTuile(coupCourant.batimentLigne, coupCourant.batimentColonne));

                while (aPropager.size() != 0) {
                    Point2D posCourantePropagation = aPropager.remove(0);
                    int hauteurCourante = instance.getPlateau().getHauteurTuile(posCourantePropagation.getPointX(), posCourantePropagation.getPointY());
                    if (nbHuttesDispo >= hauteurCourante) {
                        coupPropagation.add(new Coup(instance.getJoueurCourant(), instance.getCouleurJoueur(), posCourantePropagation.getPointX(),posCourantePropagation.getPointY(), Coup.HUTTE));
                        nbHuttesDispo -= hauteurCourante;
                    }
                }
            }
            InstanceJeu instanceAEvaluer = new InstanceJeu(null,plateauCopie2,instance.getJoueurs(),instance.getNbJoueurs(), instance.jCourant, instance.couleur_joueur, instance.estFinJeu);
            Joueur joueurAEvaluer = instanceAEvaluer.getJoueur(instanceAEvaluer.jCourant);
            int batiment = 1;

            if(coupPropagation.size()>0){ // Huttes
                for(Coup coupPropager: coupPropagation){
                    instanceAEvaluer.getPlateau().joueCoup(coupPropager);
                    augmenteBatimentsJoueur(HUTTE,joueurAEvaluer,instanceAEvaluer.getPlateau().getHauteurTuile(coupPropager.batimentLigne,coupPropager.batimentColonne));
                }
            }else{ // Tour ou Temple
                instanceAEvaluer.getPlateau().joueCoup(coupCourant);
                batiment = coupCourant.typePlacement;
                augmenteBatimentsJoueur(batiment,joueurAEvaluer,0);
            }
            // On évalue la nouvelle instance
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
            }
            i++;
        }
        if(coupsBatimentARenvoyer.size()==0){
            return null;
        }
        CoupValeur coupARenvoyer = new CoupValeur(coupT,coupsBatimentARenvoyer.get(r.nextInt(coupsBatimentARenvoyer.size())),score_max);
        return coupARenvoyer;
    }

    private ArrayList<Coup> getTousLesCoupsPossiblesDesBatiments(InstanceJeu instanceCourante){
        Plateau plateauCopie = instanceCourante.getPlateau().copie();
        ArrayList<Coup> coupsPossiblesARenvoyer = new ArrayList<>();
        ArrayList<Position> positionsPossiblesBatiment = plateauCopie.getPositions_libres_batiments();
        byte joueur_courant = instanceCourante.getJoueurCourant();
        Color color_joueur_courant = instanceCourante.getJoueur(joueur_courant).getCouleur();
        // On parcours toutes les positions possibles
        for (int position = 0; position < positionsPossiblesBatiment.size(); position++) {
            Coup coupB = null;
            Position positionCourante = positionsPossiblesBatiment.get(position);
            int[] batimentsPlacable = plateauCopie.getBatimentPlacable(positionCourante.ligne(), positionCourante.colonne(), color_joueur_courant);
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
                        ArrayList<Point2D> aPropager = plateauCopie2.previsualisePropagation(positionCourante.ligne(), positionCourante.colonne(), color_joueur_courant);
                        //On place la hutte classique sans propagation
                        coupB = new Coup(joueur_courant, color_joueur_courant, positionCourante.ligne(), positionCourante.colonne(), Coup.HUTTE);
                        plateauCopie2.placeBatiment(joueur_courant, color_joueur_courant, positionCourante.ligne(), positionCourante.colonne(), Coup.HUTTE);
                        //La position actuelle n'est plus libre
                        Position posASupprimer = new Position(positionCourante.ligne(), positionCourante.colonne());
                        plateauCopie2.supprimeElementNew(posASupprimer);
                        //On met a jour le nombre de hutte restantes
                        int hauteurCourante = instance.getPlateau().getHauteurTuile(positionCourante.ligne(), positionCourante.colonne());
                        // On récupère le nombre de huttes disponibles pour le joueur courant
                        int nbHuttesDispo = plateauCopie2.nbHuttesDisponiblesJoueur - (plateauCopie2.getHauteurTuile(positionCourante.ligne(), positionCourante.colonne()));
                        while (aPropager.size() != 0) {
                            Point2D posCourantePropagation = aPropager.remove(0);
                            hauteurCourante = plateauCopie2.getHauteurTuile(posCourantePropagation.getPointX(), posCourantePropagation.getPointY());
                            if (nbHuttesDispo >= hauteurCourante) {
                                plateauCopie2.placeBatiment(joueur_courant, color_joueur_courant, posCourantePropagation.getPointX(), posCourantePropagation.getPointY(), Coup.HUTTE);
                                // On place une hutte dessus, donc plus disponible
                                posASupprimer = new Position(posCourantePropagation.getPointX(), posCourantePropagation.getPointY());
                                plateauCopie2.supprimeElementNew(posASupprimer);
                                nbHuttesDispo -= hauteurCourante;
                            }
                        }
                    } else { // Si nous ne posons pas de hutte, il n'y a pas de propagation
                        if(batimentChoisit==0){ // Temple
                            coupB = new Coup(joueur_courant, color_joueur_courant, positionCourante.ligne(), positionCourante.colonne(),Coup.TEMPLE);
                            plateauCopie2.placeBatiment(joueur_courant, color_joueur_courant, positionCourante.ligne(), positionCourante.colonne(), Coup.TEMPLE);
                        }else{ // TOUR
                            coupB = new Coup(joueur_courant, color_joueur_courant, positionCourante.ligne(), positionCourante.colonne(),Coup.TOUR);
                            plateauCopie2.placeBatiment(joueur_courant, color_joueur_courant, positionCourante.ligne(), positionCourante.colonne(),Coup.TOUR);
                        }
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
        final int[] scoreJoueur = new int[1];
        final int[] scoreAdverse = new int[1];

        // On regarde les points pour l'IA sur un thread
        Thread iaThread = new Thread(() -> {
            scoreJoueur[0] = evaluationInstance(instanceCourante,true);
        });

        // On regarde les points pour le joueur adverse sur un autre thread
        Thread iaThread2 = new Thread(() -> {
            scoreAdverse[0] = evaluationInstance(instanceCourante,false);
        });

        iaThread.start();
        iaThread2.start();

        try { // On attends la fin des calculs des threads
            iaThread.join();
            iaThread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return scoreJoueur[0] - scoreAdverse[0];
    }

    private int evaluationInstance(InstanceJeu instanceCourante, boolean estJoueurCourant){
        ArrayList<Joueur> joueurs = new ArrayList<>();
        Joueur joueur = instanceCourante.getJoueurCourantClasse();
        if(!estJoueurCourant){
            // On évalue les adversaires de l'IA
            for(int i = 0; i<instanceCourante.getNbJoueurs(); i++){
                Joueur joueurCourantBoucle = instanceCourante.getJoueur(i);
                if(joueurCourantBoucle!=joueur) joueurs.add(joueurCourantBoucle);
            }
        }else{
            // On evalue l'IA
            joueurs.add(joueur);
        }

        for(Joueur joueurCourant: joueurs){
            //si le joueur a posé tous ses bâtiments de 2 types, il a gagné
            if(estJoueurVictorieux(joueur)){
                return Integer.MAX_VALUE;
            }
            // On veut éviter le cas où le joueur n'a plus de huttes
            if(joueur.getNbHuttes() == 0){
                return 0;
            }
        }

        // On regarde si le joueur pourra gagner au prochain tour en posant le batiment
        if(seraJoueurVictorieux(instanceCourante,joueurs)) return Integer.MAX_VALUE; // (!) potentiellement lourd

        // on evalue pas dans la boucle pour eviter de parcour nbJoueurs fois la carte
        int score = evaluerVillages(instanceCourante,joueurs);

        return score;
    }

    private void AffichertailleVillage(InstanceJeu instanceJeu, Color color_joueur){
        int taille = 0;
        ArrayList<Point2D> dejavisite = new ArrayList<>();
        Plateau plateau = instanceJeu.getPlateau();
        for(int i=0; i<plateau.getLIGNES(); i++){
            for(int j=0; j<plateau.getCOLONNES(); j++){
                // Les positions de chaque hutte du village
                if(plateau.getBatiment(i,j)!=0){
                    taille = 0;
                    ArrayList<Point2D> pointsVillage = instanceJeu.getPlateau().positionsBatsVillage(i,j,color_joueur);
                    for(Point2D posCourante : pointsVillage){
                        if(plateau.getHexagone(posCourante.getPointX(), posCourante.getPointY()).getColorJoueur()==color_joueur && !dejavisite.contains(posCourante)){
                            dejavisite.add(posCourante);
                            //System.out.println("numJoueur: "+color_joueur);
                            //System.out.println("posVillage x: "+posCourante.getPointX()+" y: "+posCourante.getPointY());
                            taille++;
                        }
                    }
                    System.out.println("taille : " + taille + " couleur : "+color_joueur);
                }
            }
        }
    }

    private int evaluerVillages(InstanceJeu instanceJeu,ArrayList<Joueur> joueursAEvaluer){
        // TODO savoir quels villages il faut agrandir ou pas
        // TODO à partir de quel moment un village n'est plus intéressant à agrandir
        int score=0;

        // On regarde les batiments placables maintenant que nous avons posé le batiment
        int[] batimentsPlacables = getNombreBatimentsPlacable(instanceJeu,joueursAEvaluer);

        for(Joueur joueurCourant: joueursAEvaluer){
            score += joueurCourant.getNbVillages();

            // Score de previsualisation (en prévision du futur)
            score += (batimentsPlacables[0]*joueurCourant.getNbTemples())*(poids_temple/5);
            score += batimentsPlacables[1]*joueurCourant.getNbHuttes()*(poids_hutte/5);
            score += (batimentsPlacables[2]*joueurCourant.getNbTours())*(poids_tour/5);

            // Score de placement
            score += joueurCourant.getNbHuttesPlacees() * poids_hutte;
            score += joueurCourant.getNbToursPlacees() * poids_tour;
            score += joueurCourant.getNbTemplesPlaces() * 100000000;

        }
        return score/joueursAEvaluer.size();
    }

    private boolean estJoueurVictorieux(Joueur joueur){
        //fin anticipée si le joueur a posé tous ses bâtiments de 2 types
        return (joueur.getNbHuttes() == 0 && joueur.getNbTemples() == 0)||(joueur.getNbTemples() ==0 && joueur.getNbTours() ==0)||(joueur.getNbHuttes()==0 && joueur.getNbTours()==0);
    }

    private boolean seraJoueurVictorieux(InstanceJeu instanceCourante,ArrayList<Joueur> joueurs){
        boolean gagnant = false;
        int[] batimentsPlacables = getNombreBatimentsPlacable(instanceCourante,joueurs);
        for(Joueur joueurCourant: joueurs) {
            // Si on peut placer un temple et que ca nous donne la victoire
            if(batimentsPlacables[0]==1 && (joueurCourant.getNbTemples()==1 && (joueurCourant.getNbHuttes()==0 || joueurCourant.getNbTours()==0))) gagnant = true;
            // Si on peut placer une hutte et que ca nous donne la victoire
            if(batimentsPlacables[1]==1 && (joueurCourant.getNbHuttes()==1 && (joueurCourant.getNbTemples()==0 || joueurCourant.getNbTours()==0))) gagnant = true;
            // Si on peut placer une tour et que ca nous donne la victoire
            if(batimentsPlacables[2]==1 && (joueurCourant.getNbTours()==1 && (joueurCourant.getNbHuttes()==0 || joueurCourant.getNbTemples()==0))) gagnant = true;
        }
        return gagnant;
    }


}
