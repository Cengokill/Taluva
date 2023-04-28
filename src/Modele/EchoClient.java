package Modele;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoClient implements Runnable{
    Socket socket_client;
    InputStream in;
    String ip_serveur;
    int port_serveur;

    public EchoClient(String ip, int p) {
        ip_serveur = ip;
        port_serveur = p;
    }

    public void recoitCoup() {
        try{
            ObjectInputStream ois = new ObjectInputStream(in);
            Coup c = (Coup) ois.readObject();
            System.out.println("Coup reçu : " + c);
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
