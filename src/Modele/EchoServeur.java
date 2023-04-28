package Modele;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServeur implements Runnable{
    ServerSocket socket_serveur;
    Socket socket_client;
    int port_serveur;

    public EchoServeur(int p) {
        port_serveur = p;
    }

    public void envoieCoup(Coup c) {
        try{
            OutputStream out = socket_client.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(c);
            oos.flush();
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
