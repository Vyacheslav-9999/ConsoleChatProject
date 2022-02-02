package cschat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Objects;

class ConnectionsManager {
    private final LinkedList<ServerConnection> connections = new LinkedList<>();
    private final ServerSocket socket;

    ConnectionsManager(ServerSocket serverSocket) {
        socket = serverSocket;
    }

    public void start() {
        new Thread(
                () -> {
                    while (!socket.isClosed())try {
                        Socket socket = this.socket.accept();
                        ServerConnection connection = new ServerConnection(this, socket);
                        connections.add(connection);
                        connection.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        ).start();
    }

    public void process(String input, String sourceIp) {
        if(input.charAt(0) == '-'){
            if(input.equals("-disconnect")){
                stopConnection(sourceIp);
            }
        }else sendEveryone(sourceIp + ": " + input);
    }

    private void stopConnection(String ipOfClient){
        for(ServerConnection sc:connections){
            if(Objects.equals(sc.getClientIp(), ipOfClient)){
                sc.close();
                connections.remove(sc);
            }
        }
    }

    public void sendEveryone(String message){
        System.out.println(message);
        for (ServerConnection i : connections) {
            i.send(message);
        }
    }

    public void sendTo(String message, String destinationIp){
        for (ServerConnection connection : connections){
            if(connection.getClientIp().equals(destinationIp)){
                connection.send(message);
            }
        }
    }

    private void shutdown() {
        for (ServerConnection i : connections) {
            i.close();
        }
    }
}
