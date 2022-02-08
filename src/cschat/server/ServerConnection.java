package cschat.server;

import java.io.*;
import java.net.Socket;

public class ServerConnection extends Thread {
    private final Socket socket;
    private final ServerConnectionsManager connectionsManager;
    private String ip = null;
    private BufferedReader in;
    private PrintWriter out;

    ServerConnection(ServerConnectionsManager manager, Socket socket) {
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
        try {
            while (!socket.isClosed()) {
                String incoming = in.readLine();
                if (incoming != null) {
                    giveMessageToManager(incoming);
                }
            }
        } catch (IOException e) {
            if (!socket.isClosed()) e.printStackTrace();
        }finally {
            close();
        }
    }

    public void giveMessageToManager(String incoming) {
        connectionsManager.processIncomingFromClient(incoming, ip);
    }

    public String getClientIp() {
        while (ip == null) {
            System.out.println("ip isn't set yet");
        }
        return ip;
    }

    public void send(String message) {
        out.println(message);
    }
}
