package Modele.IA;

import Modele.Jeu.Joueur;
import Modele.Jeu.Plateau.Plateau;
import Modele.Jeu.Plateau.Tuile;

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

    public InstanceJeu(ArrayList<Tuile> pioche, Plateau plateau, Joueur[] joueurs, byte jCourant){
        this.pioche = pioche;
        this.plateau = plateau;
        this.joueurs = joueurs;
        this.jCourant = jCourant;
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

    /*

    public InstanceJeu(ArrayList<Tuile> pioche, Plateau plateau){
        // Récupère la pioche et le plateau sous forme Serializable

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

