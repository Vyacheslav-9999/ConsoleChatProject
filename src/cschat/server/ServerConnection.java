package cschat.server;
import java.io.*;
import java.net.Socket;

public class ServerConnection extends Thread {
    private final Socket socket;
    private final ConnectionsManager connectionsManager;
    private String ip;
    private BufferedReader in;
    private PrintWriter out;

    ServerConnection(ConnectionsManager manager, Socket socket) {
        this.connectionsManager = manager;
        this.socket = socket;
    }

    public void close() {
        try {
            in.close();
            out.close();
            socket.close();
            this.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            ip = socket.getInetAddress().toString();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            giveMessageToManager(ip + " connected.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!socket.isClosed()) {
            try {
                String incoming = in.readLine();
                if(incoming != null) {
                    giveMessageToManager(incoming);
                }
            } catch (IOException e) {
                if(!socket.isClosed())e.printStackTrace();
                close();
            }
        }
    }
    public void giveMessageToManager(String incoming){
        connectionsManager.process(incoming,ip);
    }

    public String getClientIp() {
        return ip;
    }

    public void send(String message) {
        out.println(message);
    }
}
