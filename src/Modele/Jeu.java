package Modele;

import Patterns.Observable;

import javax.swing.*;
import java.util.Collections;
import java.util.LinkedList;

public class Jeu extends Observable {
    Plateau plateau;
    Joueur joueur1, joueur2;
    IA IA1, IA2;
    byte jCourant;
    byte jVainqueur;
    Joueur[] joueurs = new Joueur[2];

    Object[] joueursObjet = new Object[2];
    Parametres p;
    int[]score = new int[2];
    byte[] tuile_a_poser = new byte[5];

    boolean doit_placer_tuile;
    boolean doit_placer_batiment;

    LinkedList<Tuile> pioche;
    private static final int TAILLE_PIOCHE = 24;

    public Jeu(Parametres p){
        //jCourant = (byte) ((int)(Math.random() * 2));
        jCourant = 1;
        IA1 = IA.nouvelle(this,this.p);
        joueursObjet[0] = joueur1;
        joueursObjet[1] = IA1;

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
            // Pour pas que l'IA joue directement
            // Attendez un certain temps avant d'ex�cuter l'action finale
            int delai = 1000; // delai en millisecondes (1000 ms = 1 s)
            Timer timer = new Timer(delai, e -> joueIA());
            timer.setRepeats(false); // Ne r�p�tez pas l'action finale, ex�cutez-l� une seule fois
            timer.start(); // D�marrez le timer
        }

    }

    public boolean estJoueurCourantUneIA() {
        return joueursObjet[jCourant] instanceof IA;
    }

    public void joueIA() {
        if (!estJoueurCourantUneIA()) {
            return;
        }
        Coup c = ((IA)joueursObjet[jCourant]).joue(); // tuiles
        if (!getPlateau().estPlaceLibre(c.volcan_x,c.volcan_y)) {
            System.out.println("pas libre A DEBUGGER");
            return;
        }
        getPlateau().joueCoup(c);   // place la plateforme
        doit_placer_batiment = true;
        doit_placer_tuile = false;

        c = ((IA)joueursObjet[jCourant]).joue(); // batiment
        getPlateau().joueCoup(c);
        doit_placer_batiment = false;
        doit_placer_tuile = true;
        changeJoueur();
        pioche();
        /*if (p.getTypeJeu().compareTo("AIcAI") == 0) {
            // Attendez un certain temps avant d'ex�cuter l'action finale
            int delai = 500; // delai en millisecondes (500 ms = 0.5 s)
            Timer timer = new Timer(delai, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    joueIA();
                }
            });

            timer.setRepeats(false); // Ne r�p�tez pas l'action finale, ex�cutez-la une seule fois
            timer.start(); // D�marrez le timer
        }*/
    }

    public void calculScore(){
        for(int i = 0; i<joueurs.length; i++){
            score[i] = joueurs[i].getNbTemplesPlaces()*9 + joueurs[i].getNbToursPlacees()*3 + joueurs[i].getNbHuttesPlacees();
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
            if(type_bat == 0) {
                if(plateau.getHauteurTuile(i,j)==2) joueurs[jCourant].incrementeHutte();
                if(plateau.getHauteurTuile(i,j)==3) joueurs[jCourant].incrementeHutte();
                joueurs[jCourant].incrementeHutte();
            }
            else if(type_bat == 1) {
                joueurs[jCourant].incrementeTemple();
            }
            else if(type_bat == 2) {
                joueurs[jCourant].incrementeTour();
            }
            doit_placer_batiment = false;
            doit_placer_tuile = true;
            changeJoueur();
        }
    }

    public void changeJoueur() {
        if (jCourant == (byte) 0) {
            jCourant = (byte) 1;
        } else {
            jCourant = (byte) 0;
        }
    }

    public String getJoueurCourant(){
        return joueurs[jCourant].getPrenom();
    }

    public void initPioche(){//24 tuiles
        for(int i=0;i<TAILLE_PIOCHE;i++){
            pioche.add(new Tuile());
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
        tuile_a_poser[0] = tuile_courante.terrain0;
        tuile_a_poser[1] = tuile_courante.terrain1;
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
