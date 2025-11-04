package ahnx33.tris_client;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        final String serverIp = "localhost";
        int port = 3000;

        TicTacToeClient client = new TicTacToeClient(serverIp, port);
        client.play();
        
    }
}