package Modele;

import Structures.TripletDePosition;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
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

    public void ajoutTuilesPioche(){//15 tuiles différentes
        tuilesPioche = new ArrayList<>();
        //calcule tous les coups avec chaque tuile de la pioche
        for(int i = 0; i<pioche.size(); i++){
            if(!contientTuile(tuilesPioche,pioche.get(i))) tuilesPioche.add(pioche.get(i));
        }
    }

    public boolean contientTuile(ArrayList<Tuile> listeTuiles, Tuile t){
        for(int i = 0; i<listeTuiles.size(); i++){
            if(listeTuiles.get(i).biome0 == t.biome0 && listeTuiles.get(i).biome1 == t.biome1){
                return true;
            }
        }
        return false;
    }

    public Coup joue() {
        // Toutes les positions possibles pour poser une tuile
        ArrayList<TripletDePosition> posPossibles = jeu.getPlateau().getTripletsPossibles();
        for(int i = 0; i<tuilesPioche.size(); i++){
            Tuile tuile = tuilesPioche.get(i);
            ajoutTuilesPioche();
            for(int j = 0; j<posPossibles.size(); j++){
                TripletDePosition posCourante = posPossibles.get(j);
            }
        }
        //test
        System.out.println("ON RENTRE DANS COUP");
        ajouterInstance(jeu.getPlateau(), tuilesPioche);
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

    public void estInstance(Plateau plateau, ArrayList<Tuile> tuilesPiocheCourante){

    }

}
