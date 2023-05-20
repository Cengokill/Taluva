package Modele.Jeu;

import Modele.IA.AbstractIA;
import Modele.Jeu.Plateau.Plateau;
import Modele.Jeu.Plateau.Tuile;
import Patterns.Observable;
import Structures.Position.Position;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.io.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import static Modele.Jeu.Plateau.Hexagone.*;
import static Vue.ImageLoader.select_fin_partie;

public class Jeu extends Observable implements Serializable{

    public int indexSon = 0, indexMusique = 0;
    public final static byte CONSOLE = 0;
    public final static byte GRAPHIQUE = 1;
    public static boolean AFFICHAGE;
    public byte type_jeu;
    public int delai;
    public boolean debug;
    Plateau plateau;
    public MusicPlayer musicPlayer = new MusicPlayer("Musiques\\Back_On_The_Path.wav");
    private Tuile tuile_courante;
    private int delai_avant_pioche = 50;
    AbstractIA IA0 =null, IA1 = null, IA2 = null, IA3 = null;
    public byte jCourant;
    public byte jVainqueur;
    private Joueur[] joueurs;
    private int nb_joueurs;
    private double temps_tour;
    private boolean timerActif;
    Parametres p;
    final int[]score = new int[2];
    byte[] tuileAPoser = new byte[5];
    private boolean estPiochee = false;

    boolean doit_placer_tuile,doit_placer_batiment,estPartieFinie;
    boolean estFinPartie;
    public boolean unefoisIA=false;
    public LinkedList<Tuile> pioche;
    private static int taille_pioche;

    public Jeu(byte type_jeu){
        this.type_jeu = type_jeu;
        if(type_jeu == CONSOLE) {
            delai = 0;
        }else{
            delai = 50;
        }
        debug = false;
    }

    public void initPartie(String nomJoueur0, String nomJoueur1, String nomJoueur2, String nomJoueur3, int nbJoueurs) throws CloneNotSupportedException {
        //jCourant = (byte) new Random().nextInt(nb_joueurs-1);
        if(nomJoueur0.isBlank()) nomJoueur0 = "Joueur 1";
        if(nomJoueur1.isBlank()) nomJoueur1 = "Joueur 2";
        if(nomJoueur2.isBlank()) nomJoueur2 = "Joueur 3";
        if(nomJoueur3.isBlank()) nomJoueur3 = "Joueur 4";
        jCourant = 0;
        nb_joueurs = nbJoueurs;
        taille_pioche = 12 * nb_joueurs;
        int nbIA = 0;
        temps_tour = 40.0;//secondes avant la limite de fin de tour du joueur
        timerActif = true;
        joueurs = new Joueur[nb_joueurs];
        IA0 = AbstractIA.nouvelle(this, (byte)0, AbstractIA.INTELLIGENTE);
        IA1 = AbstractIA.nouvelle(this, (byte)1, AbstractIA.INTELLIGENTE);
        IA2 = AbstractIA.nouvelle(this, (byte)2, AbstractIA.ALEATOIRE);
        IA3 = AbstractIA.nouvelle(this, (byte)3, AbstractIA.ALEATOIRE);
        if (nomJoueur0.compareTo("IA") == 0) {
            IA0.setPrenom("IA" + (nbIA + 1));
            nbIA++;
            joueurs[0] = IA0;
        }else{
            joueurs[0] = new Joueur(Joueur.HUMAIN, (byte)1, nomJoueur0);
        }
        joueurs[0].setCouleur(Color.RED);
        if (nomJoueur1.compareTo("IA") == 0) {
            IA1.setPrenom("IA" + (nbIA + 1));
            nbIA++;
            joueurs[1] = IA1;
        }else{
            joueurs[1] = new Joueur(Joueur.HUMAIN, (byte)2, nomJoueur1);
        }
        joueurs[1].setCouleur(Color.BLUE);

        if (nbJoueurs >= 3) {
            joueurs[2] = new Joueur(Joueur.HUMAIN, (byte)3, nomJoueur2);
            if (nomJoueur2.compareTo("IA") == 0) {
                IA2 = AbstractIA.nouvelle(this, (byte)2, AbstractIA.ALEATOIRE);
                IA2.setPrenom("IA" + (nbIA + 1));
                nbIA++;
                joueurs[2] = IA2;
            }
            joueurs[2].setCouleur(Color.MAGENTA);
        }
        if (nbJoueurs == 4) {
            joueurs[3] = new Joueur(Joueur.HUMAIN, (byte)4, nomJoueur3);
            if (nomJoueur3.compareTo("IA") == 0) {
                IA3 = AbstractIA.nouvelle(this, (byte)3, AbstractIA.ALEATOIRE);
                IA3.setPrenom("IA" + (nbIA + 1));
                joueurs[3] = IA3;
            }
            joueurs[3].setCouleur(Color.GREEN);
        }

        pioche = new LinkedList<>();
        lancePartie();
    }

    public void initialiseMusique(){
        if(type_jeu == GRAPHIQUE) {
            int musicVolume;
            if(indexMusique==0) musicVolume=-100000;
            else musicVolume = (-30)+indexMusique*13;

            int sonVolume;
            if(indexSon==0) sonVolume=-100000;
            else sonVolume = (-30)+indexSon*13;

            musicPlayer.setVolume(musicVolume);
            musicPlayer.loop();
        }
    }

    public void lancePartie() throws CloneNotSupportedException {
        initPioche();
        plateau = new Plateau();
        estPartieFinie = false;
        doit_placer_tuile = true;
        doit_placer_batiment = false;

        if (estJoueurCourantUneIA()) {
            if (type_jeu == GRAPHIQUE) {//l'IA joue avec un délai
                Timer timer = new Timer(delai, e -> {
                    try {
                        pioche();
                        joueIA();
                    } catch (CloneNotSupportedException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                timer.setRepeats(false); // Ne répétez pas l'action finale, exécutez-là une seule fois
                timer.start(); // Démarrez le timer
            }
        }else{
            if (type_jeu == GRAPHIQUE) {
                Timer timer = new Timer(delai_avant_pioche, e -> {
                    pioche();
                });
                timer.setRepeats(false); // Ne répétez pas l'action finale, exécutez-là une seule fois
                timer.start(); // Démarrez le timer
            }
        }
    }

    public void switchIAJoueur(int n){
        byte numero = joueurs[n].getNumero();
        int nbHuttes = joueurs[n].getNbHuttes();
        int nbTemples = joueurs[n].getNbTemples();
        int nbTours = joueurs[n].getNbTours();
        int nbHuttesPlacees = joueurs[n].getNbHuttesPlacees();
        int nbTemplesPlaces = joueurs[n].getNbTemplesPlaces();
        int nbToursPlacees = joueurs[n].getNbToursPlacees();
        double tempsTotal = joueurs[n].getTempsTotal();
        if(joueurs[n].type_joueur == Joueur.IA){
            joueurs[n] = new Joueur(Joueur.HUMAIN, numero, "Joueur "+numero);
        }else{
            joueurs[n] = AbstractIA.nouvelle(this, numero, AbstractIA.INTELLIGENTE);
        }
        joueurs[n].setNbHuttes(nbHuttes);
        joueurs[n].setNbTemples(nbTemples);
        joueurs[n].setNbTours(nbTours);
        joueurs[n].setNbHuttesPlacees(nbHuttesPlacees);
        joueurs[n].setNbTemplesPlaces(nbTemplesPlaces);
        joueurs[n].setNbToursPlacees(nbToursPlacees);
        joueurs[n].setTempsTotal(tempsTotal);
    }

    public Joueur[] getJoueurs() {
        return joueurs;
    }

    public double getTempsTour() {
        return temps_tour;
    }

    public LinkedList<Tuile> getPioche(){
        return pioche;
    }

    public boolean estJoueurCourantUneIA() {
        return joueurs[jCourant].type_joueur == Joueur.IA;
    }

    private void joueSansThread() throws CloneNotSupportedException {
        CoupValeur coupValeur = null;
        coupValeur = joueurs[jCourant].joue();
        Coup coupTuile = coupValeur.getCoupT();
        Coup coupBatiment = coupValeur.getCoupB();
        if (coupBatiment == null) {//l'IA ne peut pas placer de bâtiment
            estPartieFinie = true;
            jVainqueur = (byte) ((jCourant + 1) % nb_joueurs);
            return;
        }
        getPlateau().joueCoup(coupTuile);
        doit_placer_batiment = true;
        doit_placer_tuile = false;
        joueurPlaceBatiment(coupBatiment.batimentLigne, coupBatiment.batimentColonne, coupBatiment.typePlacement);
        doit_placer_batiment = false;
        doit_placer_tuile = true;
        changeJoueur();
    }

    private void joueMultiThread(){
        Thread iaThread = new Thread(()-> {
            CoupValeur coupValeur = null;
            try {
                coupValeur = joueurs[jCourant].joue();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
            Coup coupTuile = coupValeur.getCoupT();
            Coup coupBatiment = coupValeur.getCoupB();
            if (coupBatiment == null) {//l'IA ne peut pas placer de bâtiment
                joueurs[jCourant].stopChrono();
                estPartieFinie = true;
                jVainqueur = (byte) ((jCourant + 1) % nb_joueurs);
                return;
            }
            getPlateau().joueCoup(coupTuile);
            doit_placer_batiment = true;
            doit_placer_tuile = false;
            Timer timer = new Timer(delai, e -> {
                joueurPlaceBatiment(coupBatiment.batimentLigne, coupBatiment.batimentColonne, coupBatiment.typePlacement);
                doit_placer_batiment = false;
                doit_placer_tuile = true;
                joueurs[jCourant].stopChrono();
                changeJoueur();
            });
                timer.setRepeats(false); // Ne répétez pas l'action finale, exécutez-là une seule fois
                timer.start(); // Démarrez le timer
            });
        iaThread.start();
    }


    public void joueIA() throws CloneNotSupportedException {
        if(type_jeu==CONSOLE) joueSansThread();
        else{
            joueMultiThread();
        }
    }

    public void afficheScore(){
        for(int joueurIndex = 0; joueurIndex<joueurs.length; joueurIndex++){
            System.out.print(joueurs[joueurIndex].getPrenom()+" : "+joueurs[joueurIndex].calculScore()+" points :\t");
            System.out.println("Huttes : "+joueurs[joueurIndex].getNbHuttesPlacees()+"\tTours : "+joueurs[joueurIndex].getNbToursPlacees()+"\tTemples : "+joueurs[joueurIndex].getNbTemplesPlaces());
        }
    }

    public int[] coupJouable(int i,int j){
        int[] coups = getPlateau().getBatimentPlacable(i,j, getJoueurCourant().getCouleur());

        int hauteurTuile = getPlateau().getHauteurTuile(i,j);
        if(getJoueurCourantClasse().getNbTemples()<=0) coups[0] = 0;
        if(getJoueurCourantClasse().getNbTours()<=0) coups[2] = 0;
        if(getJoueurCourantClasse().getNbHuttes()<hauteurTuile) coups[1] = 0;

        return coups;
    }

    public void isJoueurCourantPerdu(){
        ArrayList<Position> posPlacable = getPlateau().getPositions_libres_batiments();
        for (Position posCourante: posPlacable) {
            int[] coupsPossibleCourant = coupJouable(posCourante.ligne(),posCourante.colonne());
            if(coupsPossibleCourant[0]!=0 || coupsPossibleCourant[1]!=0 || coupsPossibleCourant[2]!=0) return;
        }
        setFinPartie();
    }

    public Joueur getJoueurCourantClasse(){
        return joueurs[jCourant];
    }
    public void setFinPartie(){
        estPartieFinie = true;
    }

    public boolean doit_placer_tuile() {
        return doit_placer_tuile;
    }

    public boolean doit_placer_batiment() {
        return doit_placer_batiment;
    }

    public boolean joueurPlaceEtage(int volcan_x, int volcan_y, int tile1_x, int tile1_y, byte terrain1, int tile2_x, int tile2_y, byte terrain2){
        if (doit_placer_batiment) {
            return false;
        }
        plateau.placeEtage(jCourant, volcan_x, volcan_y, tile1_x, tile1_y, terrain1, tile2_x, tile2_y, terrain2);
        isJoueurCourantPerdu();
        doit_placer_batiment = true;
        doit_placer_tuile = false;
        return true;
    }

    public boolean joueurPlaceBatiment(int ligne, int colonne, byte type_bat){
        if (doit_placer_tuile) {
            System.err.println("Erreur : le joueur doit placer une tuile");
            joueurs[jCourant].stopChrono();
            return false;
        }
        plateau.placeBatiment(jCourant, getJoueurCourant().getCouleur(), ligne,colonne, type_bat);
        if(type_bat!=Coup.SELECTEUR_BATIMENT){
            if(type_bat == Coup.HUTTE){
                for (int hauteur = 0 ; hauteur<plateau.getHauteurTuile(ligne,colonne);hauteur++) {
                    joueurs[jCourant].incrementeHutte();
                    }
                }
            if(type_bat == Coup.TEMPLE) {
                joueurs[jCourant].incrementeTemple();
            }
            else if(type_bat == Coup.TOUR) {
                joueurs[jCourant].incrementeTour();
            }
            doit_placer_batiment = false;
            doit_placer_tuile = true;
        }
        return false;
    }

    public boolean estFinPartie() {
        if(estPartieFinie){// Si le joueur courant n'a pas pu jouer
            select_fin_partie = true;
            return true;
        }
        int nb_temples_j = joueurs[jCourant].getNbTemples();
        int nb_tours_j = joueurs[jCourant].getNbTours();
        int nb_huttes_j = joueurs[jCourant].getNbHuttes();
        if ((nb_temples_j == 0 && nb_tours_j == 0) || (nb_temples_j == 0 && nb_huttes_j == 0) || (nb_tours_j == 0 && nb_huttes_j == 0)) {
            jVainqueur = jCourant;
            if(AFFICHAGE){
                System.out.println(joueurs[jVainqueur].getPrenom() + " a gagne !");
            }
            select_fin_partie = true;
            return true;
        }
        return false;
    }

    public void incrementePropagation(int ligne, int colonne){
        //TODO : A modifier pour corriger le bu de propagation
        if(ligne==0||colonne==0) return;
        for (int hauteur = 0 ; hauteur<plateau.getHauteurTuile(ligne,colonne);hauteur++){
            joueurs[jCourant].incrementeHutte();
        }
    }

    public void changeJoueur() {
        getPlateau().nbHuttesDisponiblesJoueur = joueurs[jCourant].getNbHuttes(); // Pour éviter d'aller dans le négatif lors de la propagation
        if(estFinPartie()){
            return;
        }
        jCourant = (byte) ((jCourant + 1) % nb_joueurs);
        if(type_jeu==GRAPHIQUE){
            Timer timer = new Timer(delai_avant_pioche, e -> {
                if(getJoueurCourant().type_joueur==Joueur.IA) {
                    pioche();
                    try {
                        joueIA();
                    } catch (CloneNotSupportedException ex) {
                        throw new RuntimeException(ex);
                    }
                }else{
                    pioche();
                }
            });
            timer.setRepeats(false); // Ne répétez pas l'action finale, exécutez-là une seule fois
            timer.start();
        }
    }

    public Joueur getJoueurCourant(){
        return joueurs[jCourant];
    }

    public String getPrenomJoueurCourant(){
        return joueurs[jCourant].getPrenom();
    }

    public void initPioche(){//24 tuiles fixes définies
        byte desert = DESERT;
        byte foret = FORET;
        byte prairie = GRASS;
        byte montagne = MONTAGNE;
        byte lac = LAC;
        pioche.add(new Tuile(desert, desert));
        pioche.add(new Tuile(prairie, prairie));
        pioche.add(new Tuile(foret, foret));
        pioche.add(new Tuile(montagne, montagne));
        pioche.add(new Tuile(lac, lac));
        if(nb_joueurs == 4) {//48 en tout
            for (int i = 0; i < 2; i++) {
                pioche.add(new Tuile(lac, prairie));
                pioche.add(new Tuile(lac, desert));
                pioche.add(new Tuile(lac, montagne));
            }
            for (int i = 0; i < 3; i++) {
                pioche.add(new Tuile(lac, foret));
                pioche.add(new Tuile(montagne, desert));
            }
            for (int i = 0; i < 4; i++) {
                pioche.add(new Tuile(montagne, prairie));
                pioche.add(new Tuile(montagne, foret));
                pioche.add(new Tuile(prairie, desert));
            }
            for (int i = 0; i < 8; i++) {
                pioche.add(new Tuile(desert, foret));
            }
            for (int i = 0; i < 11; i++) {
                pioche.add(new Tuile(prairie, foret));
            }
        }else if(nb_joueurs == 3){//36 en tout
            pioche.add(new Tuile(lac, prairie));
            pioche.add(new Tuile(lac, desert));
            pioche.add(new Tuile(lac, montagne));//8
            for (int i = 0; i < 2; i++) {
                pioche.add(new Tuile(lac, foret));
                pioche.add(new Tuile(montagne, desert));
            }//12
            for (int i = 0; i < 3; i++) {
                pioche.add(new Tuile(montagne, prairie));
                pioche.add(new Tuile(montagne, foret));
                pioche.add(new Tuile(prairie, desert));
            }//21
            for (int i = 0; i < 6; i++) {
                pioche.add(new Tuile(desert, foret));
            }//27
            for (int i = 0; i < 9; i++) {
                pioche.add(new Tuile(prairie, foret));
            }//36
        }
        else if(nb_joueurs == 2){//24 en tout
            pioche.add(new Tuile(lac, prairie));
            pioche.add(new Tuile(lac, desert));
            pioche.add(new Tuile(lac, montagne));//8
            for (int i = 0; i < 2; i++) {
                pioche.add(new Tuile(lac, foret));
                pioche.add(new Tuile(montagne, desert));
            }//12
            pioche.add(new Tuile(montagne, prairie));
            pioche.add(new Tuile(montagne, foret));
            pioche.add(new Tuile(prairie, desert));//15
            for (int i = 0; i < 4; i++) {
                pioche.add(new Tuile(desert, foret));
            }//19
            for (int i = 0; i < 5; i++) {
                pioche.add(new Tuile(prairie, foret));
            }//24
        }
        //mélange la pioche
        long seed = 12345L;
        Collections.shuffle(pioche, new Random(seed));
    }

    public byte[] getTuilesAPoser() {
        return tuileAPoser;
    }

    public void joueurPlaceEtage(Coup coup) {
        plateau.joueCoup(coup);
    }

    public boolean pioche() {
        if(pioche.isEmpty()){
            estPartieFinie = true;
            if(AFFICHAGE) System.out.println("Partie terminee, pioche vide !");
            ArrayList<Joueur> joueurs_copie = new ArrayList<>();
            ArrayList<Joueur> joueurs_tries = new ArrayList<>();
            for (int i = 0; i < getJoueurs().length; i++) {
                joueurs_copie.add(getJoueurs()[i]);
            }
            int score_meilleur = Integer.MIN_VALUE;
            int indice_meilleur_joueur = -1;
            int score_courant;
            while (joueurs_copie.size() > 1) {
                for (int i = 0; i < joueurs_copie.size(); i++) {
                    score_courant = joueurs_copie.get(i).calculScore();
                    if (score_courant > score_meilleur) {
                        score_meilleur = score_courant;
                        indice_meilleur_joueur = i;
                        jVainqueur = (byte) indice_meilleur_joueur;
                    }
                }
                joueurs_tries.add(joueurs_copie.get(indice_meilleur_joueur));
                joueurs_copie.remove(indice_meilleur_joueur);
                indice_meilleur_joueur = -1;
                score_meilleur = Integer.MIN_VALUE;
            }
            if(AFFICHAGE){
                afficheScore();
            }
            return true;
        }
        if(debug) plateau.affiche();
        tuile_courante = pioche.get(0);
        pioche.remove(0);
        if(AFFICHAGE) System.out.println("Tuiles dans la pioche : " + pioche.size());
        if(type_jeu==GRAPHIQUE) {
            estPiochee = true;
            Timer timer = new Timer(600, e -> {
                estPiochee = false;
            });
            timer.setRepeats(false); // Sert à ne pas répéter l'action
            timer.start();
        }
        tuileAPoser[0] = tuile_courante.biome0;
        tuileAPoser[1] = tuile_courante.biome1;
        tuileAPoser[2] = (byte) tuile_courante.numero0;
        tuileAPoser[3] = (byte) tuile_courante.numero1;
        tuileAPoser[4] = (byte) tuile_courante.numero2;
        if(type_jeu==GRAPHIQUE){//chrono uniquement en mode GRAPHIQUE
            joueurs[jCourant].startChrono();
        }
        return false;
    }

    public Tuile getTuileCourante() {
        return tuile_courante;
    }

    public int getTaillePioche(){
        return taille_pioche;
    }

    public void annuler() {
        Stock stock = plateau.annuler();
            if(stock!=null) {
                if (stock.changementDeJoueur == false) {
                    changeJoueur();
                }
                if(stock.typeBatiment==Coup.TUILE){
                    pioche.addFirst(new Tuile((byte)stock.getTerrain1(),(byte)stock.getTerrain2()));
                } else if(stock.typeBatiment == Coup.TEMPLE) {
                    joueurs[jCourant].decrementeTemple();
                } else if (stock.typeBatiment == Coup.TOUR) {
                    joueurs[jCourant].decrementeTour();
                } else {
                    for (int i = 0; i < stock.nbBatiment; i++) {
                        joueurs[jCourant].decrementeHutte();
                    }
                }
                changePhase();
            }
    }

    public void refaire() {
        Stock stock =plateau.refaire();
        if(stock!=null) {
            if (stock.typeBatiment == Coup.TUILE) {
                pioche.removeFirst();
            } else if (stock.typeBatiment == Coup.TEMPLE) {
                joueurs[jCourant].incrementeTemple();
            } else if (stock.typeBatiment == Coup.TOUR) {
                joueurs[jCourant].incrementeTour();
            } else {
                for (int i = 0; i <= stock.nbBatiment; i++) {
                    joueurs[jCourant].incrementeHutte();
                }
            }
            if (stock.changementDeJoueur == true) {
                changeJoueur();
            }
            changePhase();
        }
    }

    public void sauvegarder() {
        System.out.println("Sauvegarder non implémenté");
    }

    public void charger() {
        System.out.println("Charger non implémenté");
    }

    public byte getNumJoueurCourant(){
        return jCourant;
    }

    public Plateau getPlateau() {
        return plateau;
    }

    public int getNbJoueurs(){
        return nb_joueurs;
    }

    public boolean getEstPiochee(){
        return estPiochee;
    }

    public boolean getTimerActif(){
        return timerActif;
    }

    public void changePhase(){
        if(doit_placer_batiment){
            doit_placer_batiment=false;
            doit_placer_tuile=true;
        }else {
            doit_placer_tuile=false;
            doit_placer_batiment=true;
        }
    }

    public void setJeu(Jeu jeu){
        this.AFFICHAGE=jeu.AFFICHAGE;
        this.type_jeu=jeu.type_jeu;
        this.delai=jeu.delai;
        this.debug=jeu.debug;
        this.plateau=jeu.plateau;
        this.tuile_courante=jeu.tuile_courante;
        this.jCourant=jeu.jCourant;
        this.jVainqueur=jeu.jVainqueur;
        this.joueurs[0]=jeu.joueurs[0];
        this.joueurs[1]=jeu.joueurs[1];
        this.p=jeu.p;
        this.tuileAPoser=jeu.tuileAPoser;
        this.doit_placer_tuile=jeu.doit_placer_tuile;
        this.doit_placer_batiment=jeu.doit_placer_batiment;
        this.estFinPartie= jeu.estFinPartie();
        this.estFinPartie= jeu.estFinPartie();
        this.unefoisIA=jeu.unefoisIA;
        this.pioche=jeu.pioche;
    }
}
