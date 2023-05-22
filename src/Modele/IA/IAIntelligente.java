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
import java.lang.reflect.Array;
import java.util.*;

public class IAIntelligente extends AbstractIA implements Serializable {
    public int taille_max_tuiles_a_tester = 20;
    public static int poids_temple = 1000;
    public static int poids_tour = 100;
    public static int poids_hutte = 1;
    public static int TEMPLE = 0;
    public static int HUTTE = 1;
    public static int TOUR = 2;
    private Random r;
    double temps_copie_plateau = 0;
    double debut, fin, temps_total;
    double debut_test, fin_test, temps_test;
    double debut_test2, fin_test2, temps_test2;
    private int profondeur;

    public IAIntelligente(byte n, int profondeur) {
        super(IA, n, "IA"+n);
        this.profondeur = profondeur;
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
        debut = System.currentTimeMillis();
        long seed = 12345L;
        r = new Random(seed);
        ArrayList<Tuile> pioche = copiePioche(jeu.getPioche());
        Plateau plateauIA = jeu.getPlateau().copie();
        System.out.println(jeu.getJoueurCourantClasse().getPrenom());
        //plateauIA.affiche();
        //System.exit(0);
        plateauIA.nbHuttesDisponiblesJoueur = jeu.getJoueurCourantClasse().getNbHuttes();
        this.instance = new InstanceJeu(pioche, plateauIA, jeu.getJoueurs(),jeu.getNbJoueurs(), jeu.getNumJoueurCourant(),jeu.getJoueurCourant().getCouleur());
        instance.setTuilePiochee(jeu.getTuileCourante());//définit la tuile piochée de l'instance sans la retirer de la pioche, puisque à ce moment le jeu a déjà retiré la tuile de la pioche
        //aussi, si la pioche est vide, la tuile déjà piochée est stockée ici
        if(plateauIA.getTripletsPossibles().size()==1){ // Si c'est le premier coup sur le plateau
            ArrayList<CoupValeur> meilleursCoupsTab = meilleursCoupsTuile(instance,jeu.getTuileCourante());
            return meilleursCoupsTab.get(0);
        }
        ArrayList<CoupValeur> meilleursCoupsTab = meilleursCoupsInstance(instance,1);
        return meilleursCoupsTab.get(r.nextInt(meilleursCoupsTab.size()));
    }

    public ArrayList<CoupValeur> meilleursCoupsInstance(InstanceJeu instance, int horizon){
        //renvoie les meilleurs coups possibles de l'instance donnée en paramètre
        int valeurMax = Integer.MIN_VALUE;
        ArrayList<CoupValeur> coups_calcules = new ArrayList<>();
        ArrayList<CoupValeur> coups_meilleurs = meilleursCoupsTuile(instance,instance.getTuilePiochee());
        //pour chaque coup meilleur
        for(int i = 0; i<coups_meilleurs.size(); i++){
            CoupValeur coupCourant = coups_meilleurs.get(i);//un coup est constitué d'un coup de tuile et d'un coup de bâtiment d'où le Duo
            InstanceJeu nouvelle_configuration = new InstanceJeu(copiePioche(instance.getPioche()),instance.getPlateau().copie(),copyJoueurs(instance.getJoueurs()),instance.getNbJoueurs(), instance.getJoueurCourant(), instance.getCouleurJoueur());
            Plateau plateauCourant = nouvelle_configuration.getPlateau();
            plateauCourant.joueCoup(coupCourant.getCoupT());
            plateauCourant.joueCoup(coupCourant.getCoupB());
            int valeur = miniMaxJoueurA(nouvelle_configuration, horizon-1, Integer.MIN_VALUE, Integer.MIN_VALUE);
            //on ajoute un coup même s'il est perdant pour que l'IA ait quand même au moins un coup à jouer
            if(valeur==valeurMax){
                coups_calcules.add(new CoupValeur(coupCourant.getCoupT(), coupCourant.getCoupB(), valeur));
                if(estFinTemps()){
                    return coups_calcules;
                }
            }
            //sinon on ajoute des coups non perdants
            if(valeur > valeurMax){
                coups_calcules = new ArrayList<>();
                coups_calcules.add(new CoupValeur(coupCourant.getCoupT(), coupCourant.getCoupB(), valeur));
                valeurMax = valeur;
                if(estFinTemps()){
                    return coups_calcules;
                }
            }
        }
        return coups_calcules;
    }

    public int evaluation(InstanceJeu instance){
        int score = 0;
        int nouveauPoidsHutte = 1;
        int nouveauPoidsTour = 1;
        // batimentsPlacablesNombre[0] -> nombre de temples constructibles par les joueurs adverses
        // batimentsPlacablesNombre[1] -> nombre de huttes constructibles par les joueurs adverses
        // batimentsPlacablesNombre[2] -> nombre de tours constructibles par les joueurs adverses
        Joueur[] joueurs = instance.getJoueurs();
        int nbjoueurs = instance.getNbJoueurs();
        int jCourant = instance.getJoueurCourant();
        for(int i=0; i<nbjoueurs; i++){
            Joueur joueurCourant = joueurs[i];
            int[] batimentsPlacablesNombre = getNombreBatimentsConstructibles(instance, i);
            //si le joueur a perdu
            if(batimentsPlacablesNombre[0] == 0 && batimentsPlacablesNombre[1] == 0 && batimentsPlacablesNombre[2] == 0){
                if(i == jCourant){
                    return Integer.MIN_VALUE;
                }
                else{
                    return Integer.MAX_VALUE;
                }
            }
            //si le joueur a gagné avec une victoire anticipée
            if((joueurCourant.getNbHuttes() == 0 && joueurCourant.getNbTemples() == 0)||(joueurCourant.getNbTemples() ==0 && joueurCourant.getNbTours() ==0)||(joueurCourant.getNbHuttes()==0 && joueurCourant.getNbTours()==0)){
                if(i == jCourant) {
                    return Integer.MAX_VALUE;
                }
                else{
                    return Integer.MIN_VALUE;
                }
            }
            //si le joueur a placé 2 bâtiments de chaque type, on augmente le poids des bâtimetns restants
            if(joueurCourant.getNbTemples()==0 && joueurCourant.getNbHuttes()==0){
                nouveauPoidsTour= 500;
            }else if(joueurCourant.getNbTemples()==0 && joueurCourant.getNbTours()==0) {
                nouveauPoidsHutte = 20;
            }
            //si le joueur ne peut plus construire de huttes
            else if(joueurCourant.getNbHuttes() == 0){
                if(i == jCourant) {
                    return 0;
                }
                else{
                    return poids_temple;//poids à définir
                }
            }
            //sinon son score se calcule en fonction des bâtiments qu'il a placés
            score += (batimentsPlacablesNombre[0]*joueurCourant.getNbTemples())*(poids_temple);
            score += batimentsPlacablesNombre[1]*joueurCourant.getNbHuttes()*(poids_hutte*nouveauPoidsHutte);
            score += (batimentsPlacablesNombre[2]*joueurCourant.getNbTours())*(poids_tour*nouveauPoidsTour);
        }
        return score/joueurs.length;
    }

    public int miniMaxJoueurA(InstanceJeu instance, int horizon, int alpha, int beta){
        if(horizon <= 0 || instance.estFinJeu()){
            //return evaluation(instance);
            return evaluationScoreTuile(instance);
        }
        int valeur = Integer.MIN_VALUE;
        ArrayList<CoupValeur> meilleursCoups = meilleursCoupsTuile(instance,instance.pioche());//on retire la première tuile de la pioche
        for(int i = 0; i<meilleursCoups.size(); i++){
            CoupValeur coupDuo = meilleursCoups.get(i);
            InstanceJeu instanceCourante = new InstanceJeu(copiePioche(instance.getPioche()),instance.getPlateau().copie(),copyJoueurs(instance.getJoueurs()),instance.getNbJoueurs(), instance.getJoueurCourant(), instance.getCouleurJoueur());
            Plateau plateauCopie = instanceCourante.getPlateau();
            //joue le coup Tuile
            plateauCopie.joueCoup(coupDuo.getCoupT());
            //joue le coup Bâtiment
            plateauCopie.joueCoup(coupDuo.getCoupB());
            instanceCourante.changeJoueur();
            valeur = Math.max(valeur, miniMaxJoueurB(instanceCourante, horizon-1, alpha, beta));
            if (valeur >= beta) {
                return valeur; // Coupure beta
            }
            alpha = Math.max(alpha, valeur);
            if(estFinTemps()){
                return valeur;
            }
        }
        return valeur;
    }

    public int miniMaxJoueurB(InstanceJeu instance, int horizon, int alpha, int beta){
        if(horizon == 0 || instance.estFinJeu()){
            //return evaluation(instance);
            return evaluationScoreTuile(instance);
        }
        int valeur = Integer.MAX_VALUE;
        ArrayList<CoupValeur> meilleursCoups = meilleursCoupsTuile(instance,instance.pioche());//on retire la première tuile de la pioche
        for(int i = 0; i<meilleursCoups.size(); i++){
            CoupValeur coupDuo = meilleursCoups.get(i);
            InstanceJeu instanceCourante = new InstanceJeu(copiePioche(instance.getPioche()),instance.getPlateau().copie(),copyJoueurs(instance.getJoueurs()),instance.getNbJoueurs(), instance.getJoueurCourant(), instance.getCouleurJoueur());
            Plateau plateauCopie = instanceCourante.getPlateau();
            //joue le coup Tuile
            plateauCopie.joueCoup(coupDuo.getCoupT());
            //joue le coup Batiment
            plateauCopie.joueCoup(coupDuo.getCoupB());
            instanceCourante.changeJoueur();
            valeur = Math.min(valeur, miniMaxJoueurA(instanceCourante, horizon-1, alpha, beta));
            if (alpha >= valeur) {
                return valeur; // Coupure alpha
            }
            beta = Math.min(beta, valeur);
            if(estFinTemps()){
                return valeur;
            }
        }
        return valeur;
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
            joueurs.add(joueur);
        }
        int score = evaluerInstanceTuile(instanceCourante,joueurs);

        return score;
    }
    private int evaluerInstanceTuile(InstanceJeu instanceCourante, ArrayList<Joueur> joueurs){
        // On regarde ce que cette nouvelle tuile nous permet de construire
        int[] batimentsPlacablesNombre = getNombreBatimentsConstructible(instanceCourante,joueurs);
        int score = 0;
        int nouveauPoidsHutte = 1;
        // batimentsPlacablesNombre[0] -> nombre de temples constructibles par les joueurs adverses
        // batimentsPlacablesNombre[1] -> nombre de huttes constructibles par les joueurs adverses
        // batimentsPlacablesNombre[2] -> nombre de tours constructibles par les joueurs adverses
        for(Joueur joueurCourant: joueurs){
            if(batimentsPlacablesNombre[0] == 0 && batimentsPlacablesNombre[1] == 0 && batimentsPlacablesNombre[2] == 0) return Integer.MIN_VALUE;
            if(seraJoueurVictorieux(instanceCourante,joueurs)) return Integer.MAX_VALUE;
            else if(joueurCourant.getNbTemples() == 0 || joueurCourant.getNbTours() == 0){
                nouveauPoidsHutte = 5;
            }else if(joueurCourant.getNbHuttes() == 0){
                return 0;
            }
            score += (batimentsPlacablesNombre[0]*joueurCourant.getNbTemples())*(poids_temple);
            score += batimentsPlacablesNombre[1]*joueurCourant.getNbHuttes()*(poids_hutte*nouveauPoidsHutte);
            score += (batimentsPlacablesNombre[2]*joueurCourant.getNbTours())*(poids_tour);
        }
        // TODO à completer (par exemple isoler un temple ou couper un gros village en 2...)
        return score/joueurs.size();
    }

    private int[] getNombreBatimentsConstructible(InstanceJeu instanceCourante, ArrayList<Joueur> joueursAEvaluer){
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
                    }
                }
            }
        }
        return batPlacables;
    }

    private int[] getNombreBatimentsConstructibles(InstanceJeu instanceCourante, int joueurAEvaluer){
        Joueur[] joueurs = instanceCourante.getJoueurs();
        Joueur joueurCourant = joueurs[joueurAEvaluer];
        int[] batPlacables = new int[3];
        // On regarde tous les batiments placables par le joueurAEvaluer sur l'instance courante
        for(int i=0;i<instanceCourante.getPlateau().getLIGNES();i++) {
            for (int j = 0; j < instanceCourante.getPlateau().getCOLONNES(); j++) {
                if (instanceCourante.getPlateau().getHexagone(i, j).getBiomeTerrain() != Hexagone.VIDE) {
                    int hauteurCourante = instanceCourante.getPlateau().getHauteurTuile(i, j);
                    int[] batimentsPlacables = instanceCourante.getPlateau().getBatimentPlacable(i, j, joueurCourant.getCouleur());
                    if (batimentsPlacables[0] == 1) batPlacables[0]++;
                    if (batimentsPlacables[1] == 1) batPlacables[1] = batPlacables[1] + hauteurCourante;
                    if (batimentsPlacables[2] == 1) batPlacables[2]++;
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


    private ArrayList<CoupValeur> meilleursCoupsTuile(InstanceJeu instanceJeu, Tuile tuile){
        int i=0, scoreBatiment_max = Integer.MIN_VALUE, scoreTuile_max = Integer.MIN_VALUE;
        int score_courant;
        ArrayList<CoupValeur> coupARenvoyer = new ArrayList<>();
        CoupValeur coupAFaire;
        ArrayList<TripletDePosition> coupsTuilePossibles = instanceJeu.getPlateau().getTripletsPossibles();
        ArrayList<Coup> coupTuileBatimentsAEvaluer = new ArrayList<>();

        // On parcourt toutes les tuiles pour trouver les meilleures.
        debut_test = System.currentTimeMillis();
        while(i < coupsTuilePossibles.size()){
            TripletDePosition tripletCourant = coupsTuilePossibles.get(i);
            InstanceJeu instanceCourante = new InstanceJeu(copiePioche(instanceJeu.getPioche()),instanceJeu.getPlateau().copie(),copyJoueurs(instanceJeu.getJoueurs()),instanceJeu.getNbJoueurs(), instanceJeu.getJoueurCourant(), instanceJeu.getCouleurJoueur());
            Plateau plateauCopie = instanceCourante.getPlateau();
            Coup coupT = new Coup(instanceCourante.getJoueurCourant(),tripletCourant.getVolcan().ligne(),tripletCourant.getVolcan().colonne(),tripletCourant.getTile1().ligne(),tripletCourant.getTile1().colonne(),tuile.biome0,tripletCourant.getTile2().ligne(),tripletCourant.getTile2().colonne(),tuile.biome1);
            plateauCopie.joueCoup(coupT);

            int scoreTuile_courante = evaluationScoreTuile(instanceCourante);
            // si le coup est aussi bien que notre meilleur on le rajoute
            if(scoreTuile_courante==scoreTuile_max){
                coupTuileBatimentsAEvaluer.add(coupT);
            }else if(scoreTuile_courante>scoreTuile_max){ // si le coup est mieux on efface la liste, et on met la nouvelle valeur
                scoreTuile_max = scoreTuile_courante;
                coupTuileBatimentsAEvaluer = new ArrayList<>();
                coupTuileBatimentsAEvaluer.add(coupT);
            }
            i++;
            if(estFinTemps()){
                i = coupsTuilePossibles.size();
            }
        }
        if(coupTuileBatimentsAEvaluer.size()>taille_max_tuiles_a_tester){
            coupTuileBatimentsAEvaluer = new ArrayList<>(coupTuileBatimentsAEvaluer.subList(0,taille_max_tuiles_a_tester));
        }
        fin_test = System.currentTimeMillis();
        // Pour toutes les meilleures tuiles trouvées :
        debut_test2 = System.currentTimeMillis();
        for(Coup coupCourant: coupTuileBatimentsAEvaluer){
            InstanceJeu instanceCourante = new InstanceJeu(copiePioche(instanceJeu.getPioche()),instanceJeu.getPlateau().copie(),copyJoueurs(instanceJeu.getJoueurs()),instanceJeu.getNbJoueurs(), instanceJeu.getJoueurCourant(), instanceJeu.getCouleurJoueur());
            Plateau plateauCopie = instanceCourante.getPlateau();
            plateauCopie.joueCoup(coupCourant);
            coupAFaire = choisirCoupBatiment(coupCourant,instanceCourante);

            if(coupAFaire!=null) {
                score_courant = coupAFaire.getValeur();
                // si le coup est aussi bien que notre meilleur on le rajoute
                if (score_courant == scoreBatiment_max) {
                    coupARenvoyer.add(coupAFaire);
                    if (estFinTemps()) {
                        return coupARenvoyer;
                    }
                }
                // si le coup est mieux on efface la liste, et on met là jour la valeur du meilleur coup
                else if (score_courant > scoreBatiment_max) {
                    scoreBatiment_max = score_courant;
                    coupARenvoyer = new ArrayList<>();
                    coupARenvoyer.add(coupAFaire);
                    if (estFinTemps()) {
                        return coupARenvoyer;
                    }
                }
            }
        }
        fin_test2 = System.currentTimeMillis();
        fin = System.currentTimeMillis();
        // On renvoie un coup des coups optimaux calculés
        temps_total += fin-debut;
        temps_test += fin_test-debut_test;
        temps_test2 += fin_test2-debut_test2;
        //System.out.println("temps copie plateau : "+temps_copie_plateau);
        //System.out.println("---------------------------------------");
        //System.out.println("temps eval. tuiles : "+temps_test);
        //System.out.println("temps eval. batiments : "+temps_test2);
        //System.out.println("taille coupTuileBatimentsAEvaluer : "+coupTuileBatimentsAEvaluer.size());
        //System.out.println("temps total : "+temps_total);
        temps_copie_plateau = 0;
        temps_total = 0;
        temps_test = 0;
        temps_test2 = 0;
        if(coupARenvoyer.size()==0){
            System.out.println("L'IA ne peut pas jouer");
            return null;
        }
        return coupARenvoyer;
    }

    public ArrayList<Tuile> copiePioche(ArrayList<Tuile> pioche){
        ArrayList<Tuile> piocheCopie = new ArrayList<>();
        for (Tuile tuileCourante:pioche) {
            piocheCopie.add(tuileCourante);
        }
        return piocheCopie;
    }

    public ArrayList<Tuile> copiePioche(LinkedList<Tuile> pioche){
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
            for (int index = 0; index<hauteur; index++) {
                jCourantCopie.incrementeHutte();
            }
        }
    }

    private static Joueur diminueBatimentsJoueur(int batimentChoisit, Joueur jCourantCopie, int hauteur) {
        if(batimentChoisit == TEMPLE) jCourantCopie.decrementeTemple();
        else if(batimentChoisit == TOUR) jCourantCopie.decrementeTour();
        else{
            for (int index = 0; index<hauteur; index++) {
                jCourantCopie.decrementeHutte();
            }
        }
        return jCourantCopie;
    }

    private boolean possedePosition(ArrayList<Position> posAutour, Position pos){
        for(Position posCourant : posAutour){
            if(posCourant.ligne() == pos.ligne() && posCourant.colonne() == pos.colonne()) return true;
        }
        return false;
    }

    private ArrayList<Position> posVillage(ArrayList<Position> posAutour,InstanceJeu instanceAEvaluer){
        ArrayList<Position> aAjouter = new ArrayList<>();
        for(Position posCourante : posAutour){
            ArrayList<Position> posAutourCourant = instanceAEvaluer.getPlateau().getCiteAutour(posCourante.ligne(),posCourante.colonne(),instance.getJoueurCourantClasse().getCouleur());
            for(Position posCourante2 : posAutourCourant){
                ArrayList<Position> posAutourCourant2 = instanceAEvaluer.getPlateau().getCiteAutour(posCourante2.ligne(),posCourante2.colonne(),instance.getJoueurCourantClasse().getCouleur());
                for(Position posCourante3 : posAutourCourant2){
                    if(!possedePosition(posAutour,posCourante3) && !possedePosition(aAjouter,posCourante3)){
                        aAjouter.add(posCourante3);
                    }
                }
            }
        }
        aAjouter.addAll(posAutour);
        return aAjouter;
    }

    private CoupValeur choisirCoupBatiment(Coup coupT, InstanceJeu instance) {
        int i=0, score_max = Integer.MIN_VALUE;
        int score_courant;
        ArrayList<Coup> coupsBatimentARenvoyer = new ArrayList<>();
        InstanceJeu instanceCourante = new InstanceJeu(instance.getPioche(),instance.getPlateau().copie(),instance.getJoueurs(),instance.getNbJoueurs(), instance.getJoueurCourant(), instance.getCouleurJoueur());
        ArrayList<Coup> coupsBatimentPossible = getTousLesCoupsPossiblesDesBatiments(instanceCourante);
        if(coupsBatimentPossible.size()==0){
            instanceCourante.getPlateau().affiche();
        }

        while(i < coupsBatimentPossible.size()){
            score_courant = 0;
            Plateau plateauCopie2 = instanceCourante.getPlateau().copie();
            Coup coupCourant = coupsBatimentPossible.get(i);
            ArrayList<Coup> coupPropagation = new ArrayList<>();
            if (coupCourant.typePlacement == Coup.HUTTE){
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
            double debut2 = System.currentTimeMillis();
            InstanceJeu instanceAEvaluer = new InstanceJeu(instance.getPioche(),plateauCopie2,instance.getJoueurs(),instance.getNbJoueurs(), instance.getJoueurCourant(), instance.getCouleurJoueur());
            double fin2 = System.currentTimeMillis();
            temps_copie_plateau += fin2-debut2;
            Joueur joueurAEvaluer = instanceAEvaluer.getJoueur(instanceAEvaluer.getJoueurCourant());
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

            // TEST //
            if(coupCourant.typePlacement==HUTTE){
                System.out.println("////////////////////////////////////////");
                ArrayList<ArrayList<Point2D>> posVillage = instanceAEvaluer.getPlateau().getTousLesVillagesVoisins(coupCourant.batimentLigne,coupCourant.batimentColonne,joueurAEvaluer.getCouleur());
                for(ArrayList<Point2D> village : posVillage){
                    System.out.println("---Village--- : "+village.size());
                    for(Point2D posBat : village){
                        if(posBat!=null){
                            System.out.println("posBat");
                            if(instanceAEvaluer.getPlateau().getBatiment(posBat.getPointX(),posBat.getPointY())==Coup.TEMPLE) score_courant = -50000;
                            if(village.size()-1>3) score_courant = -50000;
                            //System.out.println("posBat i: "+posBat.getPointX() +" j: "+posBat.getPointY());
                        }
                    }

                }
                System.out.println("////////////////////////////////////////");
            }
            // On évalue la nouvelle instance
            score_courant += evaluationScoreInstance(instanceAEvaluer);
            /*if(!instanceCourante.getPlateau().aCiteAutour(coupCourant.batimentLigne,coupCourant.batimentColonne,instanceCourante.getJoueurCourantClasse().getCouleur())){ // TEST
                score_courant = score_courant*1000;
            }*/
            System.out.println("Scoremax: "+score_max +" scoreCourant: "+score_courant);
            //score_courant = evaluation(instanceAEvaluer);
            if(score_courant == score_max){
                coupsBatimentARenvoyer.add(coupCourant);
            }else if(score_courant > score_max){
                coupsBatimentARenvoyer = new ArrayList<>();
                coupsBatimentARenvoyer.add(coupCourant);
                score_max = score_courant;
            }
            if(coupsBatimentARenvoyer.size()==0){
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
        //plateauCopie.affiche();
        byte joueur_courant = instanceCourante.getJoueurCourant();
        Color color_joueur_courant = instanceCourante.getJoueurCourantClasse().getCouleur();
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
        int score = 0;
        for(Joueur joueurCourant: joueurs){
            //si le joueur a posé tous ses bâtiments de 2 types, il a gagné
            if(estJoueurVictorieux(joueur)){
                return Integer.MAX_VALUE;
            }
            // On veut éviter le cas où le joueur n'a plus de huttes
            if(joueur.getNbHuttes() == 0){
                return 0;
            }
            score += joueurCourant.getNbHuttesPlacees() * poids_hutte;
            score += joueurCourant.getNbToursPlacees() * poids_tour;
            score += joueurCourant.getNbTemplesPlaces() * poids_temple;
        }

        // On regarde si le joueur pourra gagner au prochain tour en posant le batiment
        //if(seraJoueurVictorieux(instanceCourante,joueurs)) return Integer.MAX_VALUE; // (!) potentiellement lourd

        // on evalue pas dans la boucle pour eviter de parcour nbJoueurs fois la carte
        //score += evaluerVillages(instanceCourante,joueurs);
        return score;
    }

    private int[] getBatimentsVillage(InstanceJeu instanceJeu, int i,int j){
        ArrayList<Point2D> dejavisite = new ArrayList<>();
        int[] batiments = new int[3];
        Plateau plateau = instanceJeu.getPlateau();
        // Les positions de chaque hutte du village
        if(plateau.getBatiment(i,j)!=0){
            Color couleurJoueur = instanceJeu.getPlateau().getHexagone(i,j).getColorJoueur();
            ArrayList<Point2D> pointsVillage = instanceJeu.getPlateau().positionsBatsVillage(i,j,couleurJoueur);
            for(Point2D posCourante : pointsVillage){
                if(plateau.getHexagone(posCourante.getPointX(), posCourante.getPointY()).getColorJoueur()==couleurJoueur && !dejavisite.contains(posCourante)){
                    if(plateau.getBatiment(posCourante.getPointX(),posCourante.getPointY()) == Coup.HUTTE) batiments[1]++;
                    if(plateau.getBatiment(posCourante.getPointX(),posCourante.getPointY()) == Coup.TEMPLE) batiments[0]++;
                    if(plateau.getBatiment(posCourante.getPointX(),posCourante.getPointY()) == Coup.TOUR) batiments[2]++;
                    dejavisite.add(posCourante);
                }
            }
        }
        return batiments;
    }

    private int evaluerVillages(InstanceJeu instanceJeu,ArrayList<Joueur> joueursAEvaluer){
        // TODO savoir quels villages il faut agrandir ou pas
        // TODO à partir de quel moment un village n'est plus intéressant à agrandir
        int score=0;

        // On regarde les batiments placables maintenant que nous avons posé le batiment
        int[] batimentsPlacables = getNombreBatimentsConstructible(instanceJeu,joueursAEvaluer);

        for(Joueur joueurCourant: joueursAEvaluer){
            score += joueurCourant.getNbVillages();
            // Score de previsualisation (en prévision du futur)
            score += (batimentsPlacables[0]*joueurCourant.getNbTemples())*(poids_temple/5);
            score += batimentsPlacables[1]*joueurCourant.getNbHuttes()*(poids_hutte/5);
            score += (batimentsPlacables[2]*joueurCourant.getNbTours())*(poids_tour/5);
            // Score de placement
            score += joueurCourant.getNbHuttesPlacees() * poids_hutte;
            score += joueurCourant.getNbToursPlacees() * poids_tour;
            score += joueurCourant.getNbTemplesPlaces() * poids_temple;

        }
        return score/joueursAEvaluer.size();
    }

    private boolean estJoueurVictorieux(Joueur joueur){
        //fin anticipée si le joueur a posé tous ses bâtiments de 2 types
        return (joueur.getNbHuttes() == 0 && joueur.getNbTemples() == 0)||(joueur.getNbTemples() ==0 && joueur.getNbTours() ==0)||(joueur.getNbHuttes()==0 && joueur.getNbTours()==0);
    }

    private boolean seraJoueurVictorieux(InstanceJeu instanceCourante,ArrayList<Joueur> joueurs){
        int[] batimentsPlacables = getNombreBatimentsConstructible(instanceCourante,joueurs);
        for(Joueur joueurCourant: joueurs) {
            // Si on peut placer un temple et que ca nous donne la victoire
            if(batimentsPlacables[0]==1 && (joueurCourant.getNbTemples()==1 && (joueurCourant.getNbHuttes()==0 || joueurCourant.getNbTours()==0))) return true;
            // Si on peut placer une hutte et que ca nous donne la victoire
            if(batimentsPlacables[1]==1 && (joueurCourant.getNbHuttes()==1 && (joueurCourant.getNbTemples()==0 || joueurCourant.getNbTours()==0))) return true;
            // Si on peut placer une tour et que ca nous donne la victoire
            if(batimentsPlacables[2]==1 && (joueurCourant.getNbTours()==1 && (joueurCourant.getNbHuttes()==0 || joueurCourant.getNbTemples()==0))) return true;
        }
        return false;
    }

    public boolean estFinTemps(){
        if (jeu.getTimerActif()) {
            System.out.println("Appel à estFinTemps()");
            double tempsEcoule = System.currentTimeMillis() - jeu.getJoueurs()[instance.getJoueurCourant()].getTempsTemp();
            if (tempsEcoule >= 20000000) {
                tempsEcoule = 0.0;
            }
            double tempsArrondi = tempsEcoule / 1000;// Convertir en secondes
            tempsArrondi = Math.round(tempsArrondi * 10) / 10.0;// Arrondir au dixième
            return tempsArrondi >= jeu.getTempsTour()*0.92;
        }
        return false;
    }


}
