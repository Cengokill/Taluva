package Modele;

public class Joueur {
    private byte type_bat;
    private String prenom;
    private String couleur;
    private int nbTours;
    private int nbHuttes;
    private int nbTemples;
    private int nbVillages;

    public Joueur(byte type, String prenom){
        this.type_bat = type;//0 pour humain, 1 pour IA
        this.prenom = prenom;
        this.nbHuttes = 0;
        this.nbTours = 0;
        this.nbTemples = 0;
        this.nbVillages = 0;
    }

    public byte getTypeBat(){
        return type_bat;
    }

    public String getPrenom(){
        return prenom;
    }

    public String getCouleur(){
        return couleur;
    }

    public int getNbTours(){
        return nbTours;
    }

    public int getNbHuttes(){
        return nbHuttes;
    }

    public int getNbTemples(){
        return nbTemples;
    }

    public void setCouleur(String couleur){
        this.couleur = couleur;
    }
    public int getNbVillages(){return nbVillages;}

    public void incrementTour(){
        nbTemples++;}

    public void incrementeHutte(){
        nbHuttes++;}

    public void incrementeTour(){
        nbTours++;}
    public void incrementeVillage(){
        nbVillages++;}

    public void incrementeTemple() {
        nbTemples++;
    }
}
