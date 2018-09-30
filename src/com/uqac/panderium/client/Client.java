package com.uqac.panderium.client;

import com.uqac.panderium.server.Connexion;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client extends Connexion {


    public Client(InetAddress ia, int port) throws IOException {
        super(new Socket(ia, port));
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Adresse IP serveur : ");
        String ip = sc.nextLine();
        System.out.print("Port serveur : ");
        int port = sc.nextInt();
        try {
            Client client = new Client(InetAddress.getByName(ip), port);
            new Thread(client).start();
        } catch (IOException e) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void sendFile(String pathname, boolean binary) {
        try {
            File file = new File(pathname);
            in = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            int count;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }
            if (binary)
                out.write("\r\n".getBytes());
            out.flush();
        } catch (Exception e) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, e);
        }
    }


    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        String choice = " ";
        String serverResponse;
        while (!choice.equals("Q") && !choice.equals("q")) {
            do {
                StringBuffer sb = new StringBuffer("Choisir une option \n");
                sb.append("1 - SOURCEColl \n");
                sb.append("2 - BYTEColl \n");
                sb.append("3 - OBJECTColl \n");
                sb.append("Q - Quitter \n");
                System.out.println(sb.toString());
                choice = sc.nextLine();
            }
            while (!choice.equals("1") && !choice.equals("2") && !choice.equals("3") && !choice.equals("Q") && !choice.equals("q"));

            switch (choice) {
                case "1":
                    sendMessage("java");
                    serverResponse = readCommand();
                    System.out.println("Réponse du server : " + serverResponse);
                    sendFile("files/Calc.java", false);
                    System.out.println("Envoi fichier source\n");
                    serverResponse = readCommand();
                    System.out.println("Réponse du server : " + serverResponse);
                    serverResponse = readCommand();
                    System.out.println("Réponse du server : " + serverResponse);
                    break;
                case "2":
                    sendMessage("class");
                    serverResponse = readCommand();
                    System.out.println("Réponse du server : " + serverResponse);
                    sendFile("files/Calc.class", true);
                    System.out.println("Envoi fichier binaire\n");
                    serverResponse = readCommand();
                    System.out.println("Réponse du server : " + serverResponse);
                    serverResponse = readCommand();
                    System.out.println("Réponse du server : " + serverResponse);
                    break;
                case "3":
                    sendMessage("object");
                    Calc calc = new Calc();
                    serverResponse = readCommand();
                    System.out.println("Réponse du server : " + serverResponse);
                    try {
                        //sérialisation dans un fichier
                        File file = new File("files/calc.ser");
                        OutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
                        ((ObjectOutputStream) objectOutputStream).writeObject(calc);
                        objectOutputStream.close();
                        sendFile("files/calc.ser", true);
                    } catch (IOException e) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, e);
                    }
                    System.out.println("Envoi de l'objet\n");
                    serverResponse = readCommand();
                    System.out.println("Réponse du server : " + serverResponse);
                    serverResponse = readCommand();
                    System.out.println("Réponse du server : " + serverResponse);
                    break;
                case "q":
                    sendMessage("quit");
                    break;
                case "Q":
                    sendMessage("quit");
                    break;
                default:
                    break;
            }
        }
    }
}
