package cschat.server;
import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    public final static int PORT = 4005;
    private ServerSocket serverSocket;

    public void start(){
        try {
            serverSocket = new ServerSocket(Server.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ConnectionsManager connector = new ConnectionsManager(serverSocket);
        connector.start();
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
