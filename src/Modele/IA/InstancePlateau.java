package Modele.IA;

import Modele.Jeu.Plateau.Plateau;
import Modele.Jeu.Plateau.Tuile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class InstancePlateau {
    public Integer piocheJeu;
    public Integer plateau;

    public InstancePlateau(ArrayList<Tuile> pioche, Plateau pl){
        //r�cup�re la pioche sous forme Serializable
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            ObjectOutputStream oos2 = new ObjectOutputStream(bos2);
            oos.writeObject(pioche);
            oos2.writeObject(pl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] data1 = bos.toByteArray();
        byte[] data2 = bos2.toByteArray();
        piocheJeu = ByteBuffer.wrap(data1).getInt();
        plateau = ByteBuffer.wrap(data2).getInt();
    }
}

