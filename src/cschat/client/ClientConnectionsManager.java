package cschat.client;

import java.util.Scanner;

public class ClientConnectionsManager {
    Client client = new Client();

    public static void main(String[] args) {
        ClientConnectionsManager manager = new ClientConnectionsManager();
        Scanner scanner = new Scanner(System.in);
        String input = "";

        while (!input.equals(Client.EXIT)) {
            input = scanner.nextLine();
            if (input.contains(Client.CONNECT)) {
                String ip = input.split(" ")[1];
                client.connect(ip);
            }
            else if (input.equals(Client.DISCONNECT)) client.disconnect();
            else client.send(input);
        }
        client.disconnect();
    }
}
