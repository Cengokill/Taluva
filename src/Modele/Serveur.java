package Modele;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur implements Runnable{
    ServerSocket socket_serveur;
    Socket socket_client;
    final int port_serveur;

    public Serveur(int p) {
        port_serveur = p;
    }

    public void envoieCoup(Coup c) {
        try{
            OutputStream out = socket_client.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out);
            System.out.println("Coup envoy� : " + c);
            oos.writeObject(c);
            oos.flush();//on vide le buffer
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void run() {
        try {
            socket_serveur = new ServerSocket(port_serveur);
            socket_client = socket_serveur.accept();//on attend et accepte la connexion du client
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
