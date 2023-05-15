package Modele.Jeu.Plateau;

import Modele.Jeu.Stock;
import Modele.Jeu.Coup;
import Modele.Jeu.Joueur;
import Structures.Position.Position;
import Structures.Position.TripletDePosition;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

import static Modele.Jeu.Plateau.Hexagone.*;

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

    public static Stock annuler(Hexagone[][] carte, ArrayList<ArrayList<TripletDePosition>>  ListetripletDePosition,ArrayList<Position>positions_libres_batiments,ArrayList<ArrayList<Position>>ListePositions_libres) {
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
                carte[tete.tile1Ligne][tete.tile1Colonne] = new Hexagone((byte) (hauteur-1), tete.getOldTerrain1(), (byte) tete.volcanColonne, (byte) tete.volcanColonne,carte[tete.tile1Ligne][tete.tile1Colonne].getNum());
                carte[tete.tile2Ligne][tete.tile2Colonne] = new Hexagone((byte) (hauteur-1), tete.getOldTerrain2(), (byte) tete.volcanLigne, (byte) tete.volcanColonne,carte[tete.tile2Ligne][tete.tile2Colonne].getNum());
                futur.addFirst(tete);
                Position aSupprimer1 = new Position(tete.tile1Ligne,tete.tile1Colonne);
                Position aSupprimer2 = new Position(tete.tile2Ligne, tete.tile2Colonne);
                supprimeElementNew(aSupprimer1,positions_libres_batiments);
                supprimeElementNew(aSupprimer2,positions_libres_batiments);
                Stock stock=new Stock(-1,Coup.TUILE,true);
                stock.setTerrain1(tete.biome1);
                stock.setTerrain2(tete.biome2);
                //System.out.println("taille du futur :" + futur.size());
                //System.out.println("taille du passé : " + passe.size());
                ListetripletDePosition.remove(ListetripletDePosition.size()-1);
                ListePositions_libres.remove(ListePositions_libres.size()-1);

                return stock;
            } else {
                int rendbatiment = 0;
                byte typeDeBatiment = tete.typePlacement;
                while (passe.size() != 0 && tete.typePlacement != Coup.TUILE) {
                    carte[tete.batimentLigne][tete.batimentColonne] = new Hexagone((byte) -1, (byte) (hauteur+1),
                            carte[tete.batimentLigne][tete.batimentColonne].getBiomeTerrain(), (byte) 0,
                            (byte) carte[tete.batimentColonne][tete.batimentColonne].getLigneVolcan(),
                            (byte) carte[tete.batimentLigne][tete.batimentColonne].getColonneVolcan());
                    rendbatiment+= hauteur+1;

                    Position ajouter = new Position(tete.batimentLigne,tete.batimentColonne);
                    positions_libres_batiments.add(ajouter);


                    futur.addFirst(tete);
                    if (passe.size() != 0) {
                        tete = passe.removeFirst();
                    }

                }
                passe.addFirst(tete);
                //System.out.println("nb de position libre de batiment : "+positions_libres_batiments.size());
                Stock stock =new Stock(rendbatiment,typeDeBatiment, false);
                //System.out.println("taille du futur :" + futur.size());
                //System.out.println("taille du passé : " + passe.size());
                return stock;

            }

        }

        return null;



    }


    public Stock refaire(Hexagone[][]carte ) {
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
                byte typebatiment = tete.typePlacement;
                int reprendbatiment = 0;
                do{

                    byte batiment = 0;
                    if (tete.typePlacement == 1) {
                        batiment = Hexagone.HUTTE;
                    } else if (tete.typePlacement == 2) {
                        batiment = Hexagone.TEMPLE;
                    } else if (tete.typePlacement == 3) {
                        batiment = Hexagone.TOUR;
                    }
                    carte[tete.batimentLigne][tete.batimentColonne] = new Hexagone(tete.getNumJoueur(), (byte) (hauteur + 1),
                            carte[tete.batimentLigne][tete.batimentColonne].getBiomeTerrain(), batiment,
                            (byte) carte[tete.batimentLigne][tete.batimentColonne].getLigneVolcan(),
                            (byte) carte[tete.batimentLigne][tete.batimentColonne].getColonneVolcan());
                    reprendbatiment+= hauteur+1;
                    if ( futur.size()!= 0) {
                        passe.addFirst(tete);
                        tete = futur.removeFirst();
                    }
                } while(futur.size()!=0&&tete.typePlacement!=Coup.TUILE);
                futur.addFirst(tete);
                Stock stock =new Stock(reprendbatiment, typebatiment, true);
                return stock;
            }
        }
        //System.out.println("taille du futur  : "+futur.size()+" pour refaire");
        //System.out.println("taille du passe  : " + passe.size()+" pour refaire");
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
    public static void supprimeElementNew(Position posASupprimer, ArrayList<Position> positions_libres_batiments){
        ArrayList<Position> positionsASupprimer = new ArrayList<>();
        for(Position posCourante: positions_libres_batiments){
            if(posCourante.ligne()==posASupprimer.ligne() && posCourante.colonne()==posASupprimer.colonne()) positionsASupprimer.add(posCourante);
        }
        for(Position posCourante: positionsASupprimer){
            positions_libres_batiments.remove(posCourante);
        }
    }

}
