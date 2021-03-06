package cschat.client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    public final static int PORT = 4005;
    public final static String EXIT = "-exit";
    public final static String CONNECT = "-connect ";
    public final static String DISCONNECT = "-disconnect";
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public void connect(String serverName){
        if (socket != null && !socket.isClosed()) disconnect();
        try {
            socket = new Socket(serverName, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            startReadingMessagesFromServer();
        } catch (UnknownHostException e) {
            System.err.println("Wrong address." + e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void startReadingMessagesFromServer() {
        new Thread(() -> {
            while (!socket.isClosed()) {
                try {
                    String message = in.readLine();
                    System.out.println(message);
                } catch (IOException e) {
                    System.out.print("");
                }
            }
        }).start();
    }

    public void disconnect() {
        try {
            send(DISCONNECT);
            socket.close();
            in.close();
            out.close();
            System.out.println("Disconnected from " + socket.getInetAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String message) {
        out.println(message);
    }

    public boolean isClosed(){
        return socket.isClosed();
    }

    public static void main(String[] args) {
        Client client = new Client();
        ClientConsoleReader reader = new ClientConsoleReader(client);
        reader.start();
    }
}
