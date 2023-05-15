package Modele.Jeu.Plateau;

import Modele.Jeu.Stock;
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

    public static Stock annuler(Hexagone[][] carte) {
        //System.out.println("passé : "+passe.size());
        //System.out.println("futur : "+futur.size());
        if (peutAnnuler()) {
            Coup tete = passe.removeFirst();
            int hauteur = carte[tete.volcanLigne][tete.volcanColonne].getHauteur();
            if (tete.typePlacement == Coup.TUILE) {
                if (hauteur == 1) {
                    carte[tete.volcanLigne][tete.volcanColonne] = new Hexagone((byte) (hauteur-1), (byte) 0, (byte) tete.batimentLigne, (byte) tete.batimentColonne);
                } else {
                    carte[tete.volcanLigne][tete.volcanColonne] = new Hexagone((byte) (hauteur-1), Hexagone.VOLCAN, (byte) tete.batimentLigne, (byte) tete.batimentColonne);
                }
                System.out.println(tete.oldTerrain1+" "+tete.oldTerrain2);
                carte[tete.tile1Ligne][tete.tile1Colonne] = new Hexagone((byte) (hauteur-1), tete.getOldTerrain1(), (byte) tete.volcanLigne, (byte) tete.volcanColonne,carte[tete.tile1Ligne][tete.tile1Colonne].getNum());
                carte[tete.tile2Ligne][tete.tile2Colonne] = new Hexagone((byte) (hauteur-1 ), tete.getOldTerrain2(), (byte) tete.volcanLigne, (byte) tete.volcanColonne,carte[tete.tile2Ligne][tete.tile2Colonne].getNum());
                futur.addFirst(tete);
                Stock stock=new Stock(-1,Coup.TUILE,true);
                stock.setTerrain1(tete.biome1);
                stock.setTerrain2(tete.biome2);
                return stock;
            } else {
                int rendbatiment = 0;
                System.out.println(tete.typePlacement);
                System.out.println(Coup.BATIMENT);
                while (passe.size() != 0 && tete.typePlacement >= Coup.BATIMENT) {
                    System.out.println("icissssssssssss");
                    carte[tete.batimentLigne][tete.batimentColonne] = new Hexagone((byte) -1, (byte) (hauteur+1),
                            carte[tete.batimentLigne][tete.batimentColonne].getBiomeTerrain(), (byte) 0,
                            (byte) carte[tete.batimentColonne][tete.batimentColonne].getLigneVolcan(),
                            (byte) carte[tete.batimentLigne][tete.batimentColonne].getColonneVolcan());
                    rendbatiment+= hauteur;
                    if (passe.size() != 0) {
                        tete = passe.removeFirst();
                    }
                }
                if(rendbatiment>1){
                    rendbatiment=rendbatiment/2;
                }
                passe.addFirst(tete);
                Stock stock =new Stock(rendbatiment, tete.typePlacement, false);
                return stock;

            }

        }
        return null;

        //System.out.println("taille du futur :" + futur.size());
        //System.out.println("taille du passé : " + passe.size());

    }


    public Stock refaire(Hexagone[][]carte) {
        if (peutRefaire()) {
            Coup tete = futur.removeFirst();
            int hauteur = carte[tete.volcanLigne][tete.volcanColonne].getHauteur();
            if (tete.typePlacement == Coup.TUILE) {
                carte[tete.volcanLigne][tete.volcanColonne] = new Hexagone((byte) (hauteur+1), Hexagone.VOLCAN, (byte) tete.volcanLigne, (byte) tete.volcanColonne);
                carte[tete.tile1Ligne][tete.tile1Colonne] = new Hexagone((byte) (hauteur+1), tete.biome1, (byte) tete.tile1Ligne, (byte) tete.tile1Colonne);
                carte[tete.tile2Ligne][tete.tile2Colonne] = new Hexagone((byte) (hauteur+1), tete.biome2, (byte) tete.tile2Ligne, (byte) tete.tile2Colonne);
                passe.addFirst(tete);
                Stock stock=new Stock(-1,Coup.TUILE,false);
                return stock;
            } else {
                boolean skip=true;
                int reprendbatiment = 0;
                System.out.println(tete.typePlacement);
                while(skip&&tete.typePlacement>=Coup.BATIMENT) {
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

                    reprendbatiment=reprendbatiment+hauteur;

                    if (futur.size() != 0) {
                        passe.addFirst(tete);
                        tete = futur.removeFirst();
                    }else{
                        skip=false;
                    }
                }
                if(reprendbatiment>1){
                    //TODO probleme ici
                    reprendbatiment=reprendbatiment/2;
                }

                futur.addFirst(tete);
                Stock stock =new Stock(reprendbatiment, tete.typePlacement, true);
                return stock;
            }
        }
        //System.out.println("taille du futur 2 : "+futur.size());
        //System.out.println("taille du passe 2 : " + passe.size());
        return null;
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
