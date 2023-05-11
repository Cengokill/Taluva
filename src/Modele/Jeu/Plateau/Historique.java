package Modele.Jeu.Plateau;

import Modele.Jeu.Coup;
import Modele.Jeu.Joueur;


import java.io.Serializable;
import java.util.LinkedList;

import static Modele.Jeu.Plateau.Hexagone.HUTTE;
import static Modele.Jeu.Plateau.Hexagone.TEMPLE;

public class Historique implements Serializable {
    static LinkedList<Coup> passe;
    static LinkedList<Coup> futur;

    public Historique() {
        passe = new LinkedList<>();
        futur = new LinkedList<>();
    }

    public void ajoute(Coup c) {
        passe.addFirst(c);
        futur.clear();
    }

    public Historique copie() {
        Historique h = new Historique();
        h.passe = (LinkedList<Coup>) passe.clone();
        h.futur = (LinkedList<Coup>) futur.clone();
        return h;
    }

    public static void annuler(Hexagone[][] carte) {
        //System.out.println("passé : "+passe.size() );
        //System.out.println("futur : "+futur.size());
        if (peutAnnuler()) {
            Coup tete = passe.removeFirst();
            int hauteur = carte[tete.volcanLigne][tete.volcanColonne].getHauteur();
            //System.out.println(hauteur+": hauteur");
            if (tete.typePlacement == Coup.TUILE) {
                if (hauteur == 1) {
                    carte[tete.volcanLigne][tete.volcanColonne] = new Hexagone((byte) (hauteur-1), (byte) 0, (byte) tete.batimentLigne, (byte) tete.batimentColonne);
                } else {
                    carte[tete.volcanLigne][tete.volcanColonne] = new Hexagone((byte) (hauteur-1), Hexagone.VOLCAN, (byte) tete.batimentLigne, (byte) tete.batimentColonne);
                }
                System.out.println(tete.oldTerrain1+" "+tete.oldTerrain2);
                carte[tete.tile1Ligne][tete.tile1Colonne] = new Hexagone((byte) (hauteur-1), tete.getOldTerrain1(), (byte) tete.volcanLigne, (byte) tete.volcanColonne,carte[tete.tile1Ligne][tete.tile1Colonne].getNum());
                carte[tete.tile2Ligne][tete.tile2Colonne] = new Hexagone((byte) (hauteur-1 ), tete.getOldTerrain2(), (byte) tete.volcanLigne, (byte) tete.volcanColonne,carte[tete.tile2Ligne][tete.tile2Colonne].getNum());
                //TODO changé de joueur
                futur.addFirst(tete);
            } else {
                int rendHutte = 0;
                while (passe.size() != 0 && tete.typePlacement == Coup.BATIMENT) {
                    carte[tete.batimentLigne][tete.batimentColonne] = new Hexagone((byte) -1, (byte) (hauteur+1),
                            carte[tete.batimentLigne][tete.batimentColonne].getBiomeTerrain(), (byte) 0,
                            (byte) carte[tete.batimentColonne][tete.batimentColonne].getLigneVolcan(),
                            (byte) carte[tete.batimentLigne][tete.batimentColonne].getColonneVolcan());
                    //TODO comprendre pk il y a deux fois le meme coups ?
                    if(tete.typePlacement==HUTTE){
                        rendHutte++;
                    }
                    if (passe.size() != 0) {
                        futur.addFirst(tete);
                        tete = passe.removeFirst();
                    }
                }
                passe.addFirst(tete);

                //TODO rendre les hutes au joueur ici demande au gens
            }

        }
        //System.out.println("taille du futur :" + futur.size());
        //System.out.println("taille du passé : " + passe.size());
    }


    public void refaire(Hexagone[][]carte) {
        if (peutRefaire()) {
            Coup tete = futur.removeFirst();
            int hauteur = carte[tete.volcanLigne][tete.volcanColonne].getHauteur();
            if (tete.typePlacement == Coup.TUILE) {
                carte[tete.volcanLigne][tete.volcanColonne] = new Hexagone((byte) (hauteur+1), Hexagone.VOLCAN, (byte) tete.volcanLigne, (byte) tete.volcanColonne);
                carte[tete.tile1Ligne][tete.tile1Colonne] = new Hexagone((byte) (hauteur+1), tete.biome1, (byte) tete.tile1Ligne, (byte) tete.tile1Colonne);
                carte[tete.tile2Ligne][tete.tile2Colonne] = new Hexagone((byte) (hauteur+1), tete.biome2, (byte) tete.tile2Ligne, (byte) tete.tile2Colonne);
                //TODO changé de joueur !!!???
                passe.addFirst(tete);
            } else {
                boolean skip=true;
                int reprendHUtte = 0;
                System.out.println(tete.typePlacement);
                while(skip&&tete.typePlacement==Coup.BATIMENT) {
                    byte batiment = 0;
                    if (tete.typePlacement == 1) {
                        batiment = Hexagone.HUTTE;
                    } else if (tete.typePlacement == 2) {
                        batiment = TEMPLE;
                    } else if (tete.typePlacement == 3) {
                        batiment = Hexagone.TOUR;
                    } else if (tete.typePlacement == 4) {
                        batiment = Hexagone.CHOISIR_BATIMENT;
                    }
                    carte[tete.batimentLigne][tete.batimentColonne] = new Hexagone(tete.getNumJoueur(), (byte) (hauteur + 1),
                            carte[tete.batimentLigne][tete.batimentColonne].getBiomeTerrain(), batiment,
                            (byte) carte[tete.batimentLigne][tete.batimentColonne].getLigneVolcan(),
                            (byte) carte[tete.batimentLigne][tete.batimentColonne].getColonneVolcan());
                    if(tete.typePlacement==HUTTE){
                        reprendHUtte++;
                    }
                    if (futur.size() != 0) {
                        passe.addFirst(tete);
                        tete = futur.removeFirst();
                    }else{
                        skip=false;
                    }
                }
                futur.addFirst(tete);


                //TODO renprendre les huttes au joueurs
            }
        }
        //System.out.println("taille du futur 2 : "+futur.size());
        //System.out.println("taille du passe 2 : " + passe.size());
    }
    public static boolean peutAnnuler() {
        return !passe.isEmpty();
    }

    public static boolean peutRefaire() {
        return !futur.isEmpty();
    }

    public Historique copy() {
        Historique historique = new Historique();
        historique.passe = this.passe;
        historique.futur = this.futur;
        return historique;
    }

    public static LinkedList<Coup> getFutur() {
        return futur;
    }

    public static LinkedList<Coup> getPasse() {
        return passe;
    }
    public static void setFutur(LinkedList<Coup>futu){
        futur=futu;
    }
    public static void setPasse(LinkedList<Coup>pass){
        passe=pass;
    }
}
