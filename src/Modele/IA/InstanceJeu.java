package Modele.IA;

import Modele.Jeu.Coup;
import Modele.Jeu.CoupValeur;
import Modele.Jeu.Joueur;
import Modele.Jeu.Plateau.Plateau;
import Modele.Jeu.Plateau.Tuile;
import Structures.Position.Point2D;
import Structures.Position.Position;

import java.util.ArrayList;

public class InstanceJeu {
    /*
    public Integer pioche;
    public Integer plateau;
     */

    public Plateau plateau;
    public ArrayList<Tuile> pioche;
    public Joueur[] joueurs;
    public byte jCourant;
    public boolean estFinJeu;

    public static int TEMPLE = 2;
    public static int HUTTE = 1;
    public static int TOUR = 3;

    public InstanceJeu(ArrayList<Tuile> pioche, Plateau plateau, Joueur[] joueurs, byte jCourant, boolean estFinJeu){
        this.pioche = pioche;
        this.plateau = plateau;
        this.joueurs = joueurs;
        this.jCourant = jCourant;
        this.estFinJeu = estFinJeu;
    }

    public byte getJoueurCourant(){
        return jCourant;
    }

    public Plateau getPlateau() {
        Plateau p = (Plateau) plateau;
        return p;
    }
    public ArrayList<Tuile> getPioche() {
        return pioche;
    }

    public Joueur[] getJoueurs(){
        return joueurs;
    }
    public Joueur getJoueur(int n){
        return joueurs[n];
    }

    public InstanceJeu simulerCoup(CoupValeur coupValeur){
        InstanceJeu instanceNew = new InstanceJeu(pioche,plateau.copie(),joueurs,jCourant,estFinJeu);
        joueTuile(coupValeur.getCoupT(),instanceNew);
        joueBatiment(coupValeur.getCoupB(),instanceNew);
        return instanceNew;
    }

    public InstanceJeu simulerTuile(Coup coupT){
        InstanceJeu instanceNew = new InstanceJeu(pioche,plateau.copie(),joueurs,jCourant,estFinJeu);
        joueTuile(coupT,instanceNew);
        return instanceNew;
    }

    public InstanceJeu simulerBatiment(Coup coupB){
        InstanceJeu instanceNew = new InstanceJeu(pioche,plateau.copie(),joueurs,jCourant,estFinJeu);
        joueBatiment(coupB,instanceNew);
        return instanceNew;
    }

    private void joueTuile(Coup coupTuile,InstanceJeu instanceCourante){
        instanceCourante.plateau.placeEtage(getJoueurCourant(), coupTuile.volcanLigne, coupTuile.volcanColonne, coupTuile.tile1Ligne, coupTuile.tile1Colonne, coupTuile.biome1, coupTuile.tile2Ligne, coupTuile.tile2Colonne, coupTuile.biome2);
    }
    private void joueBatiment(Coup coupBatiment,InstanceJeu instanceCourante){
        byte joueur_courant = getJoueurCourant();
        int x = coupBatiment.batimentLigne;
        int y = coupBatiment.batimentColonne;

        if (coupBatiment.typePlacement == HUTTE){
        //On créer un tableau contenant toutes les coordonées où l'on doit propager
        ArrayList<Point2D> aPropager = instanceCourante.plateau.previsualisePropagation(x, y, joueur_courant);
        //On place la hutte classique sans propagation
        Coup coupB = new Coup(joueur_courant, x, y, (byte) HUTTE);
        instanceCourante.plateau.joueCoup(coupB);
        //La position actuelle n'est plus libre
        Position posASupprimer = new Position(x, y);
        instanceCourante.plateau.supprimeElementNew(posASupprimer);
        //On met a jour le nombre de hutte restantes
        int hauteurCourante = plateau.getHauteurTuile(x, y);
        // On récupère le nombre de huttes disponibles pour le joueur courant
        int nbHuttesDispo = instanceCourante.plateau.nbHutteDisponiblesJoueur - (plateau.getHauteurTuile(x,y));
        while (aPropager.size() != 0) {
            Point2D posCourantePropagation = aPropager.remove(0);
            hauteurCourante = instanceCourante.plateau.getHauteurTuile(posCourantePropagation.getPointX(), posCourantePropagation.getPointY());
            if (nbHuttesDispo >= hauteurCourante) {
                instanceCourante.plateau.placeBatiment(joueur_courant, posCourantePropagation.getPointX(), posCourantePropagation.getPointY(), (byte) HUTTE);
                // On place une hutte dessus, donc plus disponible
                posASupprimer = new Position(posCourantePropagation.getPointX(), posCourantePropagation.getPointY());
                instanceCourante.plateau.supprimeElementNew(posASupprimer);
                nbHuttesDispo -= hauteurCourante;
            }
        }
        } else { // Si nous ne posons pas de hutte, il n'y a pas de propagation
            Coup coupB = new Coup(joueur_courant, x, y, (byte) coupBatiment.typePlacement);
            instanceCourante.plateau.placeBatiment(joueur_courant, x,y, (byte) coupBatiment.typePlacement);
            //on supprime la position du bâtiment qui n'est plus libre
            Position posASupprimer = new Position(x, y);
            instanceCourante.plateau.supprimeElementNew(posASupprimer);
        }
    }

    /*

    public InstanceJeu(ArrayList<Tuile> pioche, Plateau plateau){
        // R�cup�re la pioche et le plateau sous forme Serializable

        ByteArrayOutputStream piocheByte = new ByteArrayOutputStream();
        ByteArrayOutputStream plateauByte = new ByteArrayOutputStream();

        creerObjetPioche(piocheByte);
        creerObjetPlateau(plateau, plateauByte);

        byte[] piocheByteData = convertirEnByte(piocheByte);
        byte[] plateauByteData = convertirEnByte(plateauByte);

        this.pioche = readFromByte(piocheByteData);
        this.plateau = readFromByte(plateauByteData);
    }
    private int readFromByte(byte[] objectData) {
        return ByteBuffer.wrap(objectData).getInt();
    }

    private byte[] convertirEnByte(ByteArrayOutputStream piocheByte) {
        byte[] piocheByteData = piocheByte.toByteArray();
        return piocheByteData;
    }

    private void creerObjetPlateau(Plateau plateau, ByteArrayOutputStream plateauByte) {
        try {
            ObjectOutputStream plateauStream = new ObjectOutputStream(plateauByte);


            plateauStream.writeObject(plateau);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void creerObjetPioche(ByteArrayOutputStream piocheByte) {
        try {
            ObjectOutputStream piocheStream = new ObjectOutputStream(piocheByte);


            piocheStream.writeObject(piocheStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
     */
}

