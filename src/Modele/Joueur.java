package Modele;

public class Joueur {
    private byte type_bat;
    private String prenom;
    private String couleur;
    //définit le nombre de bâtiments initial
    private int nbHuttes;
    private int nbTours;
    private int nbTemples;
    //définit le nombre de bâtiments placés en jeu
    private int nbToursPlacees;
    private int nbHuttesPlacees;
    private int nbTemplesPlaces;
    private int nbVillages;//nombre de villages construits

    public Joueur(byte type, String prenom){
        this.type_bat = type;//0 pour humain, 1 pour IA
        this.prenom = prenom;
        nbHuttesPlacees = 0;
        nbToursPlacees = 0;
        nbTemplesPlaces = 0;
        nbVillages = 0;
        nbHuttes = 20;
        nbTours = 3;
        nbTemples = 2;
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

    public int getNbToursPlacees(){
        return nbToursPlacees;
    }

    public int getNbHuttesPlacees(){
        return nbHuttesPlacees;
    }

    public int getNbTemplesPlaces(){
        return nbTemplesPlaces;
    }

    public void setCouleur(String couleur){
        this.couleur = couleur;
    }
    public int getNbVillages(){return nbVillages;}

    public void incrementeHutte(){
        nbHuttes--;
        nbHuttesPlacees++;}

    public void incrementeTour(){
        nbTours--;
        nbToursPlacees++;}

    public void incrementeTemple() {
        nbTemples--;
        nbTemplesPlaces++;
    }

    public void incrementeVillage() {
        nbVillages++;
    }
}


