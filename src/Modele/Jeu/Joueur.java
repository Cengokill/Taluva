package Modele.Jeu;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Joueur {
    public final static byte HUMAIN = 0;
    public final static byte IA = 1;
    final byte type_joueur;
    private String prenom;
    private String couleur;
    private int nb_victoires;
    //définit le nombre de bâtiments initial
    private int nbHuttes;
    private int nbTours;
    private int nbTemples;
    //définit le nombre de bâtiments placés en jeu
    private int nbToursPlacees;
    private int nbHuttesPlacees;
    private int nbTemplesPlaces;
    private int nbVillages;//nombre de villages construits
    //Image de profil

    private boolean doitJouer;
    private BufferedImage image;
    private int score = 0;
    private byte numero;

    public Joueur(byte type, byte numero, String prenom){
        this.type_joueur = type;
        this.numero = numero;
        this.prenom = prenom;
        doitJouer = false;
        nbHuttesPlacees = 0;
        nbToursPlacees = 0;
        nbTemplesPlaces = 0;
        nbVillages = 0;
        nbHuttes = 20;
        nbTours = 2;
        nbTemples = 3;
    }

    public void setDoitJouer(boolean bool){
        doitJouer=bool;
    }

    public void setImage(String image_nom){
        String CHEMIN = "ressources/";
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(CHEMIN + image_nom + ".png"));
        } catch (IOException e) {
            System.err.println("Impossible de charger l'image " + CHEMIN + image_nom + ".png");
        }
        this.image = image;
    }

    public BufferedImage getImage(){
        return image;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setNbHuttes(int n){
        nbHuttes = n;
    }

    public void setNbTours(int n){
        nbTours = n;
    }

    public void setNbTemples(int n){
        nbTemples = n;
    }

    public void setNbHuttesPlacees(int n){
        nbHuttesPlacees = n;
    }

    public void setNbToursPlacees(int n){
        nbToursPlacees = n;
    }

    public void setNbTemplesPlaces(int n){
        nbTemplesPlaces = n;
    }

    public void setNumero(byte n){
        numero = numero;
    }

    public String getPrenom(){
        return prenom;
    }

    public String getCouleur(){
        return couleur;
    }

    public byte getNumero(){
        return numero;
    }

    public int getNbToursPlacees(){
        return nbToursPlacees;
    }

    public int getNbHuttesPlacees(){
        return nbHuttesPlacees;
    }

    public int getNbTemplesPlaces(){
        return nbTemplesPlaces;
    }

    public int getNbHuttes(){
        return nbHuttes;
    }

    public int getNbTours(){
        return nbTours;
    }

    public int getNbTemples(){
        return nbTemples;
    }

    public byte getTypeJoueur(){
        return type_joueur;
    }

    public int calculScore(){
        score = nbTemplesPlaces*1000 + nbToursPlacees*100 + nbHuttesPlacees;
        return score;
    }

    public void setCouleur(String couleur){
        this.couleur = couleur;
    }

    public void incrementeHutte(){
        if(nbHuttes > 0) {
            nbHuttes--;
        }
        nbHuttesPlacees++;}

    public void incrementeTour(){
        if(nbTours > 0) {
            nbTours--;
        }
        nbToursPlacees++;}

    public void incrementeTemple() {
        if(nbTemples > 0) {
            nbTemples--;
        }
        nbTemplesPlaces++;
    }
    public void decrementeHutte() {
        nbHuttes++;
        if(nbHuttesPlacees > 0) {
            nbHuttesPlacees--;
        }
    }

    public void decrementeTour(){
        nbTours++;
        if(nbToursPlacees>0) {
            nbToursPlacees--;
        }
    }
    public void decrementeTemple() {
        nbTemples++;
        if(nbTemplesPlaces > 0) {
            nbTemplesPlaces--;
        }
    }

    public CoupValeur joue() throws CloneNotSupportedException {
        return null;
    }
}


