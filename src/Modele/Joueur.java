package Modele;

public class Joueur {
    private byte type;
    private String prenom;
    private String couleur;
    private int nbTours;
    private int nbHuttes;
    private int nbTemples;

    public Joueur(byte type, String prenom){
        this.type = type;//0 pour humain, 1 pour IA
        this.prenom = prenom;
        this.nbTours = 0;
        this.nbHuttes = 0;
        this.nbTemples = 0;
    }

    public byte getType(){
        return type;
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
}
