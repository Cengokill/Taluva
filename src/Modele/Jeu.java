package Modele;

import Patterns.Observable;

import javax.swing.*;
import java.util.Collections;
import java.util.LinkedList;

import static Modele.Hexagone.*;

public class Jeu extends Observable {
    Plateau plateau;
    Joueur joueur1, joueur2;
    final AbstractIA IA1=null;
    AbstractIA IA2;
    byte jCourant;
    byte jVainqueur;
    final Joueur[] joueurs = new Joueur[2];

    final Object[] joueursObjet = new Object[2];
    Parametres p;
    final int[]score = new int[2];
    final byte[] tuile_a_poser = new byte[5];

    boolean doit_placer_tuile;
    boolean doit_placer_batiment;

    public LinkedList<Tuile> pioche;
    private static final int TAILLE_PIOCHE = 24;

    public Jeu(Parametres p){
        jCourant = (byte) ((int)(Math.random() * 2));
        /*jCourant = 1;
        IA1 = AbstractIA.nouvelle(this,this.p);
        joueursObjet[0] = joueur1;
        joueursObjet[1] = IA1;*/

        pioche = new LinkedList<>();
        lancePartie();
    }

    public void lancePartie(){
        initPioche();
        initJoueurs();
        plateau = new Plateau();
        doit_placer_tuile = true;
        doit_placer_batiment = false;
        pioche();

        if (estJoueurCourantUneIA()) {
            // Pour pas que l'AbstractIA joue directement
            // Attendez un certain temps avant d'ex�cuter l'action finale
            int delai = 1000; // delai en millisecondes (1000 ms = 1 s)
            Timer timer = new Timer(delai, e -> joueIA());
            timer.setRepeats(false); // Ne r�p�tez pas l'action finale, ex�cutez-l� une seule fois
            timer.start(); // D�marrez le timer
        }

    }

    public boolean estJoueurCourantUneIA() {
        return joueursObjet[jCourant] instanceof AbstractIA;
    }

    public void joueIA() {
        if (!estJoueurCourantUneIA()) {
            return;
        }
        Coup c = ((AbstractIA)joueursObjet[jCourant]).joue(); // tuiles
        if (!getPlateau().estHexagoneLibre(c.volcanX,c.volcanY)) {
            System.out.println("pas libre A DEBUGGER");
            return;
        }
        getPlateau().joueCoup(c);   // place la plateforme
        doit_placer_batiment = true;
        doit_placer_tuile = false;

        c = ((AbstractIA)joueursObjet[jCourant]).joue(); // batiment
        //getPlateau().joueCoup(c);
        joueurPlaceBatiment(c.batimentX,c.batimentY,c.typePlacement);
        doit_placer_batiment = false;
        doit_placer_tuile = true;
        //changeJoueur();
        pioche();
    }

    public void calculScore(){
        for(int i = 0; i<joueurs.length; i++){
            score[i] = joueurs[i].getNbTemplesPlaces()*1000 + joueurs[i].getNbToursPlacees()*100 + joueurs[i].getNbHuttesPlacees();
        }
    }

    public void initJoueurs(){
        joueurs[0] = new Joueur((byte)0,"Killian");
        joueurs[1] = new Joueur((byte)1,"Sacha");
    }
    public boolean estFinPartie() {
        int nb_temples_j = joueurs[jCourant].getNbTemples();
        int nb_tours_j = joueurs[jCourant].getNbTours();
        int nb_huttes_j = joueurs[jCourant].getNbHuttes();
        if ((nb_temples_j == 0 && nb_tours_j == 0) || (nb_temples_j == 0 && nb_huttes_j == 0) || (nb_tours_j == 0 && nb_huttes_j == 0)) {
            jVainqueur = jCourant;
            return true;
        }
        return pioche.isEmpty();
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

    public void joueurPlaceBatiment(int i, int j, byte type_bat){
        if (doit_placer_tuile) {
            return;
        }
        plateau.placeBatiment(jCourant, i,j, type_bat);
        if(type_bat!=4){
            if(getNumJoueurCourant()==0) plateau.quantiteBatimentJoueur1++;
            if(getNumJoueurCourant()==1) plateau.quantiteBatimentJoueur2++;

            if(type_bat == 1) {
                if(plateau.getHauteurTuile(i,j)==2) joueurs[jCourant].incrementeHutte();
                if(plateau.getHauteurTuile(i,j)==3) joueurs[jCourant].incrementeHutte();
                joueurs[jCourant].incrementeHutte();
            }
            else if(type_bat == 2) {
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

    public void changeJoueur() {
        System.out.println(estFinPartie());
        if (jCourant == (byte) 0) {
            jCourant = (byte) 1;
        } else {
            jCourant = (byte) 0;
        }
    }

    public String getJoueurCourant(){
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
        Collections.shuffle(pioche);
    }

    public byte[] getTuilesAPoser() {
        return tuile_a_poser;
    }

    public void joueurPlaceEtage(Coup coup) {
        plateau.joueCoup(coup);
    }

    public void pioche() {
        Tuile tuile_courante = pioche.removeFirst();
        tuile_a_poser[0] = tuile_courante.biome0;
        tuile_a_poser[1] = tuile_courante.biome1;
        tuile_a_poser[2] = (byte) tuile_courante.numero0;
        tuile_a_poser[3] = (byte) tuile_courante.numero1;
        tuile_a_poser[4] = (byte) tuile_courante.numero2;
    }

    public void annuler() {
    }

    public void refaire() {
    }

    public void sauvegarder() {
    }

    public void charger() {
    }

    public void reinitialise() {
    }

    public byte getNumJoueurCourant(){
        return jCourant;
    }

    public Plateau getPlateau() {
        return plateau;
    }
}
