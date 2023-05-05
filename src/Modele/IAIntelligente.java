package Modele;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import static Modele.Hexagone.*;

public class IAIntelligente extends AbstractIA {

    public ArrayList<Tuile> tuilesPioche;
    public HashMap<Byte, Integer> instances;

    public IAIntelligente() {
        super();
        ajoutTuilesPioche();
    }



    public Coup joue() {
        return null;
    }

    public void ajouterInstance(Plateau plateau, ArrayList<Tuile> tuilesPiocheCourante) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream ();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(plateau);
            out.close();
            byte[] bytes = byteOut.toByteArray();
            System.out.println("Objet PlateauDeJeu sérialisé en " + bytes.length + " octets");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }



}
