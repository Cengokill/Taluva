package Modele.Reseau;

import Modele.Jeu.Coup;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class Client implements Runnable{
    Socket socket_client;
    InputStream in;
    final String ip_serveur;
    final int port_serveur;

    public Client(String ip, int p) {
        ip_serveur = ip;
        port_serveur = p;
    }

    public void recoitCoup() {
        try{
            ObjectInputStream ois = new ObjectInputStream(in);
            Coup c = (Coup) ois.readObject();
            System.out.println("Coup re�u : " + c);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            socket_client = new Socket(ip_serveur, port_serveur);
            in = socket_client.getInputStream();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
