package com.uqac.panderium.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {

    private ServerSocket serverSocket;
    public static final int PORT = 2000;
    public static final int NB_MAX_CON = 6;


    public Server() {
        try {
            serverSocket = new ServerSocket(PORT, NB_MAX_CON);
        } catch (IOException e) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        new Thread(server).start();
    }

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
