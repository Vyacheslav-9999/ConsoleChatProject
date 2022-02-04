package cschat.client;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    public final static int PORT = 4005;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private class MessagesReader extends Thread {
        @Override
        public void run() {
            while (!socket.isClosed()) {
                try {
                    String message = in.readLine();
                    System.out.println(message);
                } catch (IOException e) {
                    System.out.print("");
                }
            }
        }
    }

    public void connect(String serverName, int port) {
        if(socket != null && !socket.isClosed())disconnect();
        try {
            socket = new Socket(serverName, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            MessagesReader incomingMessagesReader = new MessagesReader();
            incomingMessagesReader.start();
        }catch (UnknownHostException e){
            System.err.println("Wrong address." + e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        try {
            send("-disconnect");
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

    public static void main(String[] args) {
        Client client = new Client();
        Scanner scanner = new Scanner(System.in);
        String input = "";

        while (!input.equals("-exit")) {
            input = scanner.nextLine();
            if (input.contains("-connect ")) {
                String ip = input.split(" ")[1];
                client.connect(ip, PORT);
            }
            else if (client.socket == null || client.socket.isClosed()){
                System.out.println("wrong command");
            }
            else if (input.equals("-disconnect")) client.disconnect();
            else client.send(input);
        }
        client.disconnect();
    }
}
