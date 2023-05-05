package Modele;

import Structures.TripletDePosition;

import java.io.*;
import java.util.*;

import static Modele.Hexagone.*;

public class IAIntelligente extends AbstractIA {

    public ArrayList<Tuile> tuilesPioche;
    Map<String, byte[]> instances = new HashMap<>();

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
        ajouterInstance(jeu.getPlateau(), tuilesPioche);
        return null;
    }

    public void ajouterInstance(Plateau plateau, ArrayList<Tuile> tuilesPiocheCourante) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream ();

        ByteArrayOutputStream byteOut2 = new ByteArrayOutputStream ();
        try {
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            ObjectOutputStream out2 = new ObjectOutputStream(byteOut);

            out.writeObject(plateau);
            out2.writeObject(tuilesPiocheCourante);
            out.close();
            out2.close();
            byte[] bytes = byteOut.toByteArray();
            ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
            ObjectInputStream in = new ObjectInputStream(byteIn);
            byte[] bytes_test =  byteIn.readAllBytes();

            in.close();

            byte[] bytes2 = byteOut2.toByteArray();
            int cle = Arrays.hashCode(byteOut.toByteArray());
            int cle2 = Arrays.hashCode(byteOut2.toByteArray());
            String cleString = Integer.toString(cle)+Integer.toString(cle2);
            instances.put(cleString, bytes);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public boolean estInstance(Plateau plateau, ArrayList<Tuile> tuilesPiocheCourante){
        return false;
    }

}
