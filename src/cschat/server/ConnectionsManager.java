package cschat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

class ConnectionsManager {
    private final ConcurrentHashMap<String,ServerConnection> connections = new ConcurrentHashMap<>();
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
                        connection.start();
                        connections.put(connection.getClientIp(),connection);
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
        ServerConnection connection = connections.get(ipOfClient);
        connection.close();
        connections.remove(ipOfClient);
        if(connections.isEmpty())this.shutdown();
    }

    public void sendEveryone(String message){
        System.out.println(message);
        for (ServerConnection connection : connections.values()) {
            connection.send(message);
        }
    }

    public void sendTo(String message, String destinationIp){
        connections.get(destinationIp).send(message);
    }

    private void shutdown() {
        for (ServerConnection connection : connections.values()) {
            connection.close();
        }
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Server is off");
        }
    }
}
