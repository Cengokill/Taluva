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

import static Modele.Jeu.Plateau.Hexagone.*;

public class Jeu extends Observable implements Serializable{
    public final static byte CONSOLE = 0;
    public final static byte GRAPHIQUE = 1;
    private static final int TAILLE_PIOCHE = 24;
    public static boolean AFFICHAGE;
    public byte type_jeu;
    public int delai;
    public boolean debug;
    Plateau plateau;
    private Tuile tuile_courante;
    private int delai_avant_pioche = 500;
    AbstractIA IA0 =null;
    AbstractIA IA1 =null;
    public byte jCourant;
    public byte jVainqueur;
    private Joueur[] joueurs;
    private int nb_joueurs;
    Parametres p;
    final int[]score = new int[2];
    byte[] tuileAPoser = new byte[5];

    boolean doit_placer_tuile,doit_placer_batiment,estPartieFinie;
    boolean estFinPartie;
    public boolean unefoisIA=false;
    public LinkedList<Tuile> pioche;

    public MusicPlayer musicPlayer;


    public Jeu(byte type_jeu){
        this.type_jeu = type_jeu;
        if(type_jeu == CONSOLE) {
            delai = 0;
        }else{
            delai = 10;
        }
        debug = false;
    }

    public void initPartie() throws CloneNotSupportedException {
        //jCourant = (byte) new Random().nextInt(1);
        nb_joueurs = 2;
        joueurs = new Joueur[nb_joueurs];
        jCourant = 0;
        IA0 = AbstractIA.nouvelle(this, (byte)1, AbstractIA.ALEATOIRE);
        IA1 = AbstractIA.nouvelle(this, (byte)0, AbstractIA.ALEATOIRE);
        IA0.setPrenom("IA0");
        IA1.setPrenom("IA1");
        //Thread ia1Thread = new Thread(IA1);
        //Thread ia2Thread = new Thread(IA2);
        //ia1Thread.start();
        //ia2Thread.start();
        //joueurs[0] = new Joueur(Joueur.HUMAIN, (byte)1, "Joueur 1");
        //joueurs[1] = new Joueur(Joueur.HUMAIN, (byte)2, "Joueur 2");
        joueurs[0] = IA1;
        joueurs[1] = IA1;
        joueurs[1].setCouleur(Color.RED);
        joueurs[0].setCouleur(Color.BLUE);
        pioche = new LinkedList<>();
        lancePartie();
        if(type_jeu == GRAPHIQUE) {
            musicPlayer = new MusicPlayer("Musiques\\Back_On_The_Path.wav");
            musicPlayer.setVolume(-50.0f);
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
            jVainqueur = (byte) ((jCourant + 1) % 2);
            return;
        }
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
                jVainqueur = (byte) ((jCourant + 1) % 2);
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

    public void calculScore(){
    }

    public void afficheScore(){
        System.out.println("Score :");
        for(int joueurIndex = 0; joueurIndex<joueurs.length; joueurIndex++){
            System.out.println(joueurs[joueurIndex].getPrenom() + " : " + score[joueurIndex]);
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
        if(type_bat!=4){
            if(type_bat == 1){
                if(plateau.getHauteurTuile(ligne,colonne)==2) joueurs[jCourant].incrementeHutte();
                if(plateau.getHauteurTuile(ligne,colonne)>=3) joueurs[jCourant].incrementeHutte();
                joueurs[jCourant].incrementeHutte();
            }
            if(type_bat == 2) {
                joueurs[jCourant].incrementeTemple();
            }
            else if(type_bat == 3) {
                joueurs[jCourant].incrementeTour();
            }
            doit_placer_batiment = false;
            doit_placer_tuile = true;
        }
        return false;
    }

    public boolean estFinPartie() {
        if(estPartieFinie){// Si le joueur courant n'a pas pu jouer
            if(AFFICHAGE){
                System.out.println(joueurs[((jVainqueur+1)%2)].getPrenom() + " ne peut plus placer de batiment !");
                System.out.println(joueurs[jVainqueur].getPrenom() + " a gagne !");
            }
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
            return true;
        }
        if(pioche.isEmpty()){
            calculScore();
            if(AFFICHAGE){
                System.out.println("Partie terminee, pioche vide !");
                afficheScore();
            }
            return true;
        }
        return false;
    }

    public void incrementePropagation(int ligne, int colonne){
        if(ligne==0||colonne==0) return;
        if(plateau.getHauteurTuile(ligne,colonne)==2) joueurs[jCourant].incrementeHutte();
        if(plateau.getHauteurTuile(ligne,colonne)==3) joueurs[jCourant].incrementeHutte();
        joueurs[jCourant].incrementeHutte();
    }

    public void changeJoueur() {
        //System.out.println("Changement de joueur");
        if (jCourant == (byte) 0) {
            jCourant = (byte) 1;
        } else {
            jCourant = (byte) 0;
        }
        getPlateau().nbHuttesDisponiblesJoueur = joueurs[jCourant].getNbHuttes(); // Pour eviter d'aller dans le negatif lors de la propagation
        if(type_jeu==GRAPHIQUE){
            if(estFinPartie()){
                return;
            }
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
        for(int i=0; i<2; i++){
            pioche.add(new Tuile(lac, prairie));
            pioche.add(new Tuile(lac, desert));
            pioche.add(new Tuile(lac, montagne));
        }
        for(int i=0; i<3; i++){
            pioche.add(new Tuile(lac, foret));
            pioche.add(new Tuile(montagne, desert));
        }
        for(int i=0; i<4; i++){
            pioche.add(new Tuile(montagne, prairie));
            pioche.add(new Tuile(montagne, foret));
            pioche.add(new Tuile(prairie, desert));
        }
        for(int i=0; i<8; i++){
            pioche.add(new Tuile(desert, foret));
        }
        for(int i=0; i<11; i++){
            pioche.add(new Tuile(prairie, foret));
        }
        //mélange la pioche
        Collections.shuffle(pioche);
    }

    public byte[] getTuilesAPoser() {
        return tuileAPoser;
    }

    public void joueurPlaceEtage(Coup coup) {
        plateau.joueCoup(coup);
    }

    public void pioche() {
        if(type_jeu==CONSOLE && AFFICHAGE && debug) {
            System.out.println("Tuiles dans la pioche : " + pioche.size());
            plateau.affiche();
        }else if(type_jeu==GRAPHIQUE){//chono uniquement en mode GRAPHIQUE
            System.out.println("pioche : Joueur courant : " + joueurs[jCourant].getPrenom());
            joueurs[jCourant].startChrono();
        }
        tuile_courante = pioche.get(0);
        pioche.remove(0);
        tuileAPoser[0] = tuile_courante.biome0;
        tuileAPoser[1] = tuile_courante.biome1;
        tuileAPoser[2] = (byte) tuile_courante.numero0;
        tuileAPoser[3] = (byte) tuile_courante.numero1;
        tuileAPoser[4] = (byte) tuile_courante.numero2;
    }

    public Tuile getTuileCourante() {
        return tuile_courante;
    }

    public void annuler() {
        Stock stock = plateau.annuler();
            if(stock!=null) {
                if (stock.changementDeJoueur == false) {
                    changeJoueur();
                }
                if(stock.typeBatiment==Coup.TUILE){
                    pioche.addFirst(new Tuile((byte)stock.getTerrain1(),(byte)stock.getTerrain2()));
                } else if(stock.typeBatiment == TEMPLE) {
                    joueurs[jCourant].decrementeTemple();
                } else if (stock.typeBatiment == TOUR) {
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
            } else if (stock.typeBatiment == TEMPLE) {
                joueurs[jCourant].incrementeTemple();
            } else if (stock.typeBatiment == TOUR) {
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
