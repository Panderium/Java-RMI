package com.uqac.panderium.server;


import java.io.*;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connexion implements Runnable {
    public final static int CR = 13;
    public final static int LF = 10;
    protected Socket socket;
    protected InputStream in;
    protected OutputStream out;
    protected BufferedInputStream bufIn;
    private Boolean loop;

    public Connexion(Socket connexion) {
        loop = true;
        socket = connexion;
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
            bufIn = new BufferedInputStream(in);
        } catch (IOException e) {
            Logger.getLogger(Connexion.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @Override
    public void run() {
        Runtime rt = Runtime.getRuntime();
        Process process;
        while (loop) {
            String str = readCommand();
            String[] cmd = str.split("\\s+");
            switch (cmd[0]) {
                case "java":
                    sendMessage("Ready to receive java file");
                    readFile("src/Calc.java");
                    sendMessage("File received");
                    try {
                        process = rt.exec("javac src/Calc.java");
                        process.waitFor();
                    } catch (Exception e) {
                        Logger.getLogger(Connexion.class.getName()).log(Level.SEVERE, null, e);
                    }
                    sendMessage(Double.toString(createInstanceFromFileAndExecute()));
                    break;
                case "class":
                    sendMessage("Ready to receive class file");
                    readFile("src/Calc.class");
                    sendMessage("File received");
                    sendMessage(Double.toString(createInstanceFromFileAndExecute()));
                    break;
                case "object":
                    sendMessage("Ready to receive a Java Object");
                    readFile("src/class.ser");
                    sendMessage("File received");
                    File file = new File("src/class.ser");
                    try {
                        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
                        Object calc = objectInputStream.readObject();
                        Method[] methods = calc.getClass().getMethods();
                        sendMessage(Double.toString((Double) methods[2].invoke(calc, 10, 32)));
                    } catch (Exception e) {
                        Logger.getLogger(Connexion.class.getName()).log(Level.SEVERE, null, e);
                    }
                    break;
                case "quit":
                    loop = false;
                    System.out.println("Connection closed");
                    break;
                default:
                    break;
            }
        }
    }

    private double createInstanceFromFileAndExecute() {
        File file = new File("src/");
        try {
            URL url = file.toURL();
            URL[] urls = new URL[]{url};
            ClassLoader classLoader = new URLClassLoader(urls);
            Class aClass = classLoader.loadClass("Calc");
            Object o = aClass.getConstructor().newInstance();
            o = aClass.cast(o);
            Method[] methods = aClass.getMethods();
            return (double) methods[2].invoke(o, 10, 32);
        } catch (Exception e) {
            Logger.getLogger(Connexion.class.getName()).log(Level.SEVERE, null, e);
        }
        return 0;
    }

    private void readFile(String pathname) {
        int character = -1;
        boolean end = false, crReceived = false;
        String request = "";
        try {
            byte[] bytes = new byte[4096];
            OutputStream classFile = new FileOutputStream(new File(pathname));
            int bytesRead;
            do {
                character = bufIn.read();
                classFile.write(character);
                end = crReceived && character == LF;
                crReceived = (character == CR);
            } while (character != -1 && !end);
            classFile.close();
        } catch (Exception e) {
            Logger.getLogger(Connexion.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    protected void sendMessage(String cmd) {
        try {
            out.write((cmd + "\r\n").getBytes());
        } catch (IOException e) {
            Logger.getLogger(Connexion.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    protected String readCommand() {
        int character = -1;
        boolean end = false, crReceived = false;
        String request = "";
        do {
            try {
                character = bufIn.read();

                request += (char) character;

                end = crReceived && character == LF;

                crReceived = (character == CR);

            } catch (IOException e) {
                Logger.getLogger(Connexion.class.getName()).log(Level.SEVERE, null, e);
            }
        } while (character != -1 && !end);
        return request;
    }
}
