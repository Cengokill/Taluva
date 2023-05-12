package Modele.IA;

import Modele.Jeu.Coup;
import Modele.Jeu.Joueur;
import Modele.Jeu.Plateau.Plateau;
import Modele.Jeu.Plateau.Tuile;
import Structures.Position.Position;
import Structures.Position.TripletDePosition;

import java.util.*;

public class IAIntelligente extends AbstractIA {

    public byte num_joueur_ia;
    private int NbInstanceDifferentes=0;
    public static int poids_temple = 5000;
    public static int poids_tour = 1000;
    public static int poids_hutte = 1;
    public static int TEMPLE = 0;
    public static int HUTTE = 1;
    public static int TOUR = 2;

    public IAIntelligente() {
        super();
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

    @Override
    public Coup joue(){
        System.out.println("jeu.getPioche().size : "+jeu.getPioche().size());
        ArrayList<Tuile> pioche = ajoutTuilesPioche(jeu.getPioche());
        System.out.println("pioche.size() : "+pioche.size());
        InstanceJeu instance = new InstanceJeu(pioche, jeu.getPlateau(), jeu.getJoueurs(), jeu.getNumJoueurCourant());
        System.out.println(calculCoups_joueur_A(instance, 2));
        System.out.println("nbInstance: "+NbInstanceDifferentes);
        return null;
    }

    public int calculCoups_joueur_A(InstanceJeu instance, int horizon) {
        byte joueur_courant = instance.getJoueurCourant();
        if(horizon==0){
            return evaluation_joueur(instance, joueur_courant);
        }
        
        int valeur = Integer.MIN_VALUE;

        ArrayList<TripletDePosition> tripletsPossibles = instance.getPlateau().getTripletsPossibles();
        ArrayList<Tuile> pioche = copyPioche(instance.getPioche());
        
        System.out.println("pioche.size() : "+pioche.size());
        for (int piocheIndex = 0; piocheIndex < pioche.size(); piocheIndex++) {

            Tuile tuile = pioche.get(piocheIndex);
            //pour chaque carte unique de la pioche
            for (int tripletsIndex = 0; tripletsIndex < tripletsPossibles.size(); tripletsIndex++) {
                System.out.println("0");

                TripletDePosition tripletCourant = tripletsPossibles.get(tripletsIndex);
                Position[] points = new Position[3];
                points[0] = tripletCourant.getVolcan();
                points[1] = tripletCourant.getTile1();
                points[2] = tripletCourant.getTile2();

                for (int orientationTuile = 0; orientationTuile < 3; orientationTuile++) {
                    System.out.println("1");
                    Plateau plateauCopie = instance.getPlateau().copie(); // FAUDRA FAIRE UNE COPIE
                    plateauCopie.placeEtage(joueur_courant, points[orientationTuile].ligne(), points[orientationTuile].colonne(), (points[(orientationTuile+1)%3].ligne()), (points[(orientationTuile+1)%3].colonne()), tuile.biome0, (points[(orientationTuile+2)%3].ligne()), (points[(orientationTuile+2)%3].colonne()), tuile.biome1);

                    ArrayList<Tuile> nouvellePioche;
                    nouvellePioche = copyPioche(pioche);
                    nouvellePioche.remove(piocheIndex);
                    ArrayList<Position> positionsLibresBatiments = plateauCopie.getPositions_libres_batiments();

                    for (int position = 0; position < positionsLibresBatiments.size(); position++){
                        System.out.println("2");
                        Position positionCourante = positionsLibresBatiments.get(position);
                        int[] batimentsPlacable = plateauCopie.getBatimentPlacable(positionCourante.ligne(),positionCourante.colonne(),joueur_courant);
                        // On parcourt tous les choix de bâtiments possibles
                        for (int batimentChoisit = 0; batimentChoisit < batimentsPlacable.length; batimentChoisit++){
                            if(batimentsPlacable[batimentChoisit]==1){ // si le batiment est possable il est egal à 1
                                System.out.println("3");
                                Joueur jCourantCopie = instance.getJoueur(joueur_courant);
                                Joueur[] joueurs = instance.getJoueurs();
                                Plateau plateauCopie2 = plateauCopie.copie();

                                plateauCopie2.placeBatiment(joueur_courant,positionCourante.ligne(),positionCourante.colonne(),(byte) (batimentsPlacable[batimentChoisit]));
                                plateauCopie2.affiche();

                                updateBatimentsJoueur(batimentChoisit, jCourantCopie);

                                //on supprime la position du bâtiment qui n'est plus libre
                                plateauCopie2.supprimeLibreBatiments(positionCourante);
                                joueurs[joueur_courant] = jCourantCopie;
                                //on créer une copie de l'instance et on change le joueur courant
                                NbInstanceDifferentes++;
                                InstanceJeu instanceCopie = new InstanceJeu(nouvellePioche, plateauCopie2,joueurs, (byte) ((joueur_courant+1)%2));
                                valeur = Math.max(valeur, calculCoups_joueur_B(instanceCopie, horizon - 1));
                                //System.out.println("valeur calculCoups_joueur_B : "+valeur);
                            }
                        }
                    }
                }
            }
        }
        return valeur;
    }

    private static void updateBatimentsJoueur(int batimentChoisit, Joueur jCourantCopie) {
        if(batimentChoisit == TEMPLE) jCourantCopie.incrementeTemple();
        else if(batimentChoisit == HUTTE) jCourantCopie.incrementeHutte();
        else if(batimentChoisit == TOUR) jCourantCopie.incrementeTour();
    }

    public int calculCoups_joueur_B(InstanceJeu instance, int horizon){
        byte joueur_courant = instance.getJoueurCourant();
        if(horizon==0){
            return evaluation_joueur(instance, joueur_courant);
        }

        int valeur = Integer.MAX_VALUE;
        ArrayList<TripletDePosition> tripletsPossibles = instance.getPlateau().getTripletsPossibles();
        ArrayList<Tuile> pioche = copyPioche(instance.getPioche());

        for (int piocheIndex = 0; piocheIndex < pioche.size(); piocheIndex++) {

            Tuile tuile = pioche.get(piocheIndex);
            //pour chaque carte unique de la pioche
            for (int tripletsIndex = 0; tripletsIndex < tripletsPossibles.size(); tripletsIndex++) {
                TripletDePosition tripletCourant = tripletsPossibles.get(tripletsIndex);
                Position[] points = new Position[3];
                points[0] = tripletCourant.getVolcan();
                points[1] = tripletCourant.getTile1();
                points[2] = tripletCourant.getTile2();

                for (int orientationTuile = 0; orientationTuile < 3; orientationTuile++) {
                    Plateau plateauCopie = instance.getPlateau().copie();
                    plateauCopie.placeEtage(joueur_courant, points[orientationTuile].ligne(), points[orientationTuile].colonne(), (points[(orientationTuile+1)%3].ligne()), (points[(orientationTuile+1)%3].colonne()), tuile.biome0, (points[(orientationTuile+2)%3].ligne()), (points[(orientationTuile+2)%3].colonne()), tuile.biome1);

                    ArrayList<Tuile> nouvellePioche;
                    nouvellePioche = copyPioche(pioche);
                    nouvellePioche.remove(piocheIndex);
                    ArrayList<Position> positionsLibresBatiments = plateauCopie.getPositions_libres_batiments();

                    for (int position = 0; position < positionsLibresBatiments.size(); position++) {
                        Position positionCourante = positionsLibresBatiments.get(position);
                        int[] batimentsPlacable = plateauCopie.getBatimentPlacable(positionCourante.ligne(), positionCourante.colonne(), joueur_courant);
                        // On parcourt tous les choix de bâtiments possibles
                        for (int batimentChoisit = 0; batimentChoisit < batimentsPlacable.length; batimentChoisit++) {
                            if (batimentsPlacable[batimentChoisit] == 1) { // si le batiment est possable il est egal à 1
                                Joueur jCourantCopie = instance.getJoueur(joueur_courant);
                                Joueur[] joueurs = instance.getJoueurs();
                                Plateau plateauCopie2 = plateauCopie.copie();

                                plateauCopie2.placeBatiment(joueur_courant, positionCourante.ligne(), positionCourante.colonne(), (byte) (batimentsPlacable[batimentChoisit]));
                                plateauCopie2.affiche();

                                updateBatimentsJoueur(batimentChoisit, jCourantCopie);

                                //on supprime la position du bâtiment qui n'est plus libre
                                plateauCopie2.supprimeLibreBatiments(positionCourante);
                                joueurs[joueur_courant] = jCourantCopie;
                                //on créer une copie de l'instance et on change le joueur courant
                                NbInstanceDifferentes++;
                                InstanceJeu instanceCopie = new InstanceJeu(nouvellePioche, plateauCopie2, joueurs, (byte) ((joueur_courant + 1) % 2));
                                valeur = Math.max(valeur, calculCoups_joueur_A(instanceCopie, horizon - 1));
                                //System.out.println("valeur calculCoups_joueur_B : "+valeur);
                            }
                        }
                    }
                }
            }
        }
        return valeur;
    }

    public ArrayList<Tuile> copyPioche(ArrayList<Tuile> pioche){
        ArrayList<Tuile> piocheCopie = new ArrayList<>();
        for (Tuile tuileCourante:pioche) {
            piocheCopie.add(tuileCourante);
        }
        return piocheCopie;
    }

    public int evaluation_joueur(InstanceJeu instance, int num_joueur){
        Joueur j = instance.getJoueur(num_joueur);
        //si le joueur a posé tous ses bâtiments de 2 types, il a gagné
        if((j.getNbHuttes() == 0 && j.getNbTemples() == 0)||(j.getNbTemples() ==0 && j.getNbTours() ==0)||(j.getNbHuttes()==0 && j.getNbTours()==0)){
            return Integer.MAX_VALUE;
        }
        //si le joueur ne peut plus construire de huttes, il doit placer un temple ou une tour
        if(j.getNbHuttes() == 0){
            return 0;
        }
        //sinon on calcule le score du joueur
        int score_joueur = j.getNbHuttesPlacees() * poids_hutte;
        score_joueur += j.getNbToursPlacees() * poids_tour;
        score_joueur += j.getNbTemplesPlaces() * poids_temple;

        return score_joueur;
    }

}
