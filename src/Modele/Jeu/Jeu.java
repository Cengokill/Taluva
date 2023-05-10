package Modele.Jeu;

import Modele.IA.AbstractIA;
import Modele.Jeu.Plateau.Historique;
import Modele.Jeu.Plateau.Plateau;
import Modele.Jeu.Plateau.Tuile;
import Patterns.Observable;

import javax.swing.*;
import java.util.LinkedList;
import java.util.Random;

import static Modele.Jeu.Plateau.Hexagone.*;

public class Jeu extends Observable {


    Plateau plateau;
    Joueur joueur1, joueur2;
    AbstractIA IA1=null;
    AbstractIA IA2;
    public byte jCourant;
    byte jVainqueur;
    public final Joueur[] joueurs = new Joueur[2];

    final Object[] joueursObjet = new Object[2];
    Parametres p;
    final int[]score = new int[2];
    final byte[] tuileAPoser = new byte[5];

    boolean doit_placer_tuile;
    boolean doit_placer_batiment;
    boolean estFinPartie;

    public LinkedList<Tuile> pioche;
    private static final int TAILLE_PIOCHE = 24;

    public Jeu(){

    }

    public void initPartie(){
        jCourant = (byte) new Random().nextInt(1);

        pioche = new LinkedList<>();
        lancePartie();
    }

    public void lancePartie(){
        initPioche();
        initJoueurs();
        plateau = new Plateau();
        estFinPartie = false;
        doit_placer_tuile = true;
        doit_placer_batiment = false;
        pioche();

        if (estJoueurCourantUneIA()) {
            // Pour pas que l'AbstractIA joue directement
            // Attendez un certain temps avant d'ex�cuter l'action finale
            int delai = 1000; // delai en millisecondes (1000 ms = 1 s)
            Timer timer = new Timer(delai, e -> joueIA());
            timer.setRepeats(false); // Ne répétez pas l'action finale, exécutez-là une seule fois
            timer.start(); // Démarrez le timer
        }

    }

    public Joueur[] getJoueurs() {
        return joueurs;
    }

    public LinkedList<Tuile> getPioche(){
        return pioche;
    }

    public boolean estJoueurCourantUneIA() {
        return joueursObjet[jCourant] instanceof AbstractIA;
    }

    public void joueIA() {
        if (!estJoueurCourantUneIA()) {
            return;
        }

        Coup coup = ((AbstractIA)joueursObjet[jCourant]).joue(); // tuiles
        if (!getPlateau().estHexagoneLibre(coup.volcanLigne,coup.volcanColonne)) {
            System.out.println("pas libre A DEBUGGER");
            return;
        }

        getPlateau().joueCoup(coup);   // place la plateforme
        doit_placer_batiment = true;
        doit_placer_tuile = false;

        coup = ((AbstractIA)joueursObjet[jCourant]).joue(); // batiment
        //getPlateau().joueCoup(coup);
        joueurPlaceBatiment(coup.batimentLigne,coup.batimentColonne,coup.typePlacement);
        doit_placer_batiment = false;
        doit_placer_tuile = true;
        //changeJoueur();
        pioche();
    }

    public void calculScore(){
        for(int joueurIndex = 0; joueurIndex<joueurs.length; joueurIndex++){
            score[joueurIndex] = joueurs[joueurIndex].getNbTemplesPlaces()*1000 + joueurs[joueurIndex].getNbToursPlacees()*100 + joueurs[joueurIndex].getNbHuttesPlacees();
        }
    }

    public void initJoueurs(){
        joueurs[0] = new Joueur((byte)0,"Jean-Sebastien");
        joueurs[1] = new Joueur((byte)1,"Sacha");
    }
    public boolean estFinPartie() {
        if(estFinPartie) return true; // Pour pouvoir detecter quand un joueur ne peut plus passer aucun batiment
        int nb_temples_j = joueurs[jCourant].getNbTemples();
        int nb_tours_j = joueurs[jCourant].getNbTours();
        int nb_huttes_j = joueurs[jCourant].getNbHuttes();
        if ((nb_temples_j == 0 && nb_tours_j == 0) || (nb_temples_j == 0 && nb_huttes_j == 0) || (nb_tours_j == 0 && nb_huttes_j == 0)) {
            jVainqueur = jCourant;
            return true;
        }
        return pioche.isEmpty();
    }

    public Joueur getJoueurCourantClasse(){
        return joueurs[jCourant];
    }
    public void setFinPartie(){
        estFinPartie = true;
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

        doit_placer_batiment = true;
        doit_placer_tuile = false;
        return true;
    }

    public void joueurPlaceBatiment(int ligne, int colonne, byte type_bat){
        if (doit_placer_tuile) {
            return;
        }
        plateau.placeBatiment(jCourant, ligne,colonne, type_bat);
        if(type_bat!=4){
            if(type_bat == 2) {
                joueurs[jCourant].incrementeTemple();
            }
            else if(type_bat == 3) {
                joueurs[jCourant].incrementeTour();
            }
            doit_placer_batiment = false;
            doit_placer_tuile = true;
            changeJoueur();
        }
    }

    public void incrementePropagation(int ligne, int colonne){
        if(plateau.getHauteurTuile(ligne,colonne)==2) joueurs[jCourant].incrementeHutte();
        if(plateau.getHauteurTuile(ligne,colonne)==3) joueurs[jCourant].incrementeHutte();
        joueurs[jCourant].incrementeHutte();
    }

    public void changeJoueur() {
        if(estFinPartie()) System.out.println("FIN DE LA PARTIE");
        else{
            if (jCourant == (byte) 0) {
                jCourant = (byte) 1;
            } else {
                jCourant = (byte) 0;
            }
            getPlateau().nbHutteDisponibleJoueurCourant=joueurs[jCourant].getNbHuttes(); // Pour eviter d'aller dans le negatif lors de la propagation
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
    }

    public byte[] getTuilesAPoser() {
        return tuileAPoser;
    }

    public void joueurPlaceEtage(Coup coup) {
        plateau.joueCoup(coup);
    }

    public void pioche() {
        Random r = new Random();
        int index = r.nextInt(pioche.size()-1);
        Tuile tuile_courante = pioche.get(index);
        pioche.remove(index);
        tuileAPoser[0] = tuile_courante.biome0;
        tuileAPoser[1] = tuile_courante.biome1;
        tuileAPoser[2] = (byte) tuile_courante.numero0;
        tuileAPoser[3] = (byte) tuile_courante.numero1;
        tuileAPoser[4] = (byte) tuile_courante.numero2;
    }

    public void annuler() {
        plateau.annuler();
        changePhase();
    }

    public void refaire() {
        plateau.refaire();
        changePhase();
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

    public void changePhase(){
        if(doit_placer_batiment){
            doit_placer_batiment=false;
            doit_placer_tuile=true;
        }else {
            doit_placer_tuile=false;
            doit_placer_batiment=true;
        }
    }
}
