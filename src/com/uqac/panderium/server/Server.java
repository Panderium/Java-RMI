package com.uqac.panderium.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Initie le serveur, ce serveur est concurent c'est à dire qu'il est capable d'instancier plusieurs connexions (6 dans notre cas)
 */
public class Server implements Runnable {

    private ServerSocket serverSocket;
    public static final int PORT = 2000;
    public static final int NB_MAX_CON = 6;


    /**
     * Constructeur
     * Initie le socket du serveur sur une IP et un Port donnés
     */
    public Server() {
        try {
            serverSocket = new ServerSocket(PORT, NB_MAX_CON);
        } catch (IOException e) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * instancie un serveur et le fait tourner dans un thread
     * @param args
     */
    public static void main(String[] args) {
        Server server = new Server();
        new Thread(server).start();
    }

    /**
     * Surcharge de la méthode run de Runnable
     * Gère les connexions entrantes des clients et lance un nouveau thread connexion qui servira à échanger avec le client
     */
    @Override
    public void run() {
        while (true) {

            try {
                Socket connexion;
                connexion = serverSocket.accept();
                System.out.println("New Connection");
                new Thread(new Connexion(connexion)).start();
            } catch (IOException e) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, e);
                break;
            }

        }
    }
}
