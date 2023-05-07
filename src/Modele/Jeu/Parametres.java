package Modele.Jeu;

import java.io.*;
import java.util.Scanner;

public class Parametres {
    private String prenom1, prenom2;
    private String type_jeu, type_IA = "";
    private String nomFichier = "ressources/parametres.txt";
    public Parametres() throws IOException {
        lireFichierParametres();
    }

    public void lireFichierParametres() throws IOException {
        File f = new File(nomFichier);
        if (!f.exists()) {
            System.err.println("Le fichier '" + nomFichier + "' est introuvable.");
            return;
        }
        InputStream in = new FileInputStream(f);
        Scanner s = new Scanner(in);
        //lis une ligne
        if (s.hasNextLine()) {
            prenom1 = s.nextLine();
            prenom2 = s.nextLine();
            type_jeu = s.nextLine();
            type_IA = s.nextLine();
        }
    }

    public void sauvegarderParametres() throws IOException {
        File f = new File(nomFichier);
        f.createNewFile();
        OutputStream out = new FileOutputStream(f);
        out.write((prenom1 + System.lineSeparator()).getBytes());
        out.write((prenom2 + System.lineSeparator()).getBytes());
        out.write((type_jeu + System.lineSeparator()).getBytes());
        out.write((type_IA + System.lineSeparator()).getBytes());
        out.close();
    }

    public String getPrenom1() {
        return prenom1;
    }

    public String getPrenom2() {
        return prenom2;
    }

    public String getTypeJeu() { return type_jeu; }

    public String getType_IA() { return type_IA; }

    public void setPrenom1(String prenom1) {
        this.prenom1 = prenom1;
    }

    public void setPrenom2(String prenom2) {
        this.prenom2 = prenom2;
    }

    public void setType_jeu(String type_jeu) {
        this.type_jeu = type_jeu;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public void setType_IA(String type_IA) {
        this.type_IA = type_IA;
    }
}
