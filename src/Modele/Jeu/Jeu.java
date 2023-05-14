package Modele.Jeu;

import Modele.IA.AbstractIA;
import Modele.Jeu.Plateau.Plateau;
import Modele.Jeu.Plateau.Tuile;
import Patterns.Observable;

import javax.swing.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import static Modele.Jeu.Plateau.Hexagone.*;

public class Jeu extends Observable {
    public final static byte CONSOLE = 0;
    public final static byte GRAPHIQUE = 1;
    private static final int TAILLE_PIOCHE = 24;
    public byte type_jeu;
    public int delai;
    public boolean debug;
    Plateau plateau;
    Joueur joueur1, joueur2;
    AbstractIA IA1=null;
    AbstractIA IA2=null;
    public byte jCourant;
    byte jVainqueur;
    private Joueur[] joueurs = new Joueur[2];
    Parametres p;
    final int[]score = new int[2];
    final byte[] tuileAPoser = new byte[5];

    boolean doit_placer_tuile;
    boolean doit_placer_batiment;
    boolean estFinPartie;
    public boolean unefoisIA=false;
    public LinkedList<Tuile> pioche;

    public Jeu(byte type_jeu){
        this.type_jeu = type_jeu;
        if(type_jeu == CONSOLE) {
            delai = 0;
        }else{
            delai = 1000;
        }
        debug = false;
    }

    public void initPartie(){
        //jCourant = (byte) new Random().nextInt(1);
        jCourant = 1;
        IA1 = AbstractIA.nouvelle(this);
        IA2 = AbstractIA.nouvelle(this);
        //Thread ia1Thread = new Thread(IA1);
        //Thread ia2Thread = new Thread(IA2);
        //ia1Thread.start();
        //ia2Thread.start();
        joueurs[0] = IA2;//new Joueur(Joueur.HUMAIN, "Joueur 1");
        joueurs[1] = IA1;

        pioche = new LinkedList<>();
        lancePartie();
    }

    public void lancePartie(){
        initPioche();
        plateau = new Plateau();
        estFinPartie = false;
        doit_placer_tuile = true;
        doit_placer_batiment = false;
        pioche();

        if (estJoueurCourantUneIA()) {
            System.out.println("IA joue");
            // Pour pas que l'AbstractIA joue directement
            // Attendez un certain temps avant d'exécuter l'action finale
            Timer timer = new Timer(delai, e -> {
                joueIA();
            });
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
        return joueurs[jCourant].type_joueur == Joueur.IA;
    }

    public void joueIA(){
        if (!estJoueurCourantUneIA()) {
            return;
        }
        CoupValeur coupValeur = joueurs[jCourant].joue();
        Coup coupTuile = coupValeur.getCoupT();
        Coup coupBatiment = coupValeur.getCoupB();
        if (!getPlateau().estHexagoneLibre(coupTuile.volcanLigne,coupTuile.volcanColonne)) {
            System.out.println("pas libre A DEBUGGER");
            return;
        }
        getPlateau().joueCoup(coupTuile);   // place la plateforme
        doit_placer_batiment = true;
        doit_placer_tuile = false;
        Timer timer = new Timer(delai, e -> {
            joueurPlaceBatiment(coupBatiment.batimentLigne,coupBatiment.batimentColonne,coupBatiment.typePlacement);
            doit_placer_batiment = false;
            doit_placer_tuile = true;
        });
        timer.setRepeats(false); // Ne répétez pas l'action finale, exécutez-là une seule fois
        timer.start(); // Démarrez le timer
        /*
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
        //changeJoueur();*/
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
        if(estFinPartie) return true; // Pour pouvoir détecter quand un joueur ne peut plus poser de bâtiment
        int nb_temples_j = joueurs[jCourant].getNbTemples();
        int nb_tours_j = joueurs[jCourant].getNbTours();
        int nb_huttes_j = joueurs[jCourant].getNbHuttes();
        if ((nb_temples_j == 0 && nb_tours_j == 0) || (nb_temples_j == 0 && nb_huttes_j == 0) || (nb_tours_j == 0 && nb_huttes_j == 0)) {
            jVainqueur = jCourant;
            if(type_jeu==CONSOLE){
                System.out.println("Le joueur " + joueurs[jVainqueur].getPrenom() + " a gagné !");
            }
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
            if(type_bat == 1){
                if(plateau.getHauteurTuile(ligne,colonne)==2) joueurs[jCourant].incrementeHutte();
                if(plateau.getHauteurTuile(ligne,colonne)==3) joueurs[jCourant].incrementeHutte();
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
            changeJoueur();
        }
    }

    public void incrementePropagation(int ligne, int colonne){
        if(ligne==0||colonne==0) return;
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
            getPlateau().nbHutteDisponiblesJoueur=joueurs[jCourant].getNbHuttes(); // Pour eviter d'aller dans le negatif lors de la propagation
            System.out.println("nbHutte: "+getPlateau().nbHutteDisponiblesJoueur);
            Timer timer = new Timer(delai, e -> {
                joueIA();
            });
            timer.setRepeats(false); // Ne répétez pas l'action finale, exécutez-là une seule fois
            timer.start(); // Démarrez le timer
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
        plateau.affiche();
        int index = pioche.size()-1;
        Tuile tuile_courante = pioche.get(index);
        pioche.remove(index);
        tuileAPoser[0] = tuile_courante.biome0;
        tuileAPoser[1] = tuile_courante.biome1;
        tuileAPoser[2] = (byte) tuile_courante.numero0;
        tuileAPoser[3] = (byte) tuile_courante.numero1;
        tuileAPoser[4] = (byte) tuile_courante.numero2;
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
