package cschat.server;
import java.io.IOException;
import java.net.ServerSocket;

public class Server extends Thread{
    public final static int PORT = 4005;
    private ServerSocket serverSocket;

    public void run(){
        try {
            serverSocket = new ServerSocket(Server.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ServerConnectionsManager manager = new ServerConnectionsManager(serverSocket);
        manager.start();
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
