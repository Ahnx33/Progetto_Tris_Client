package ahnx33.tris_client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TicTacToeClient {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final Scanner scanner = new Scanner(System.in);

    public TicTacToeClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void play() {
        try {
            // first message from server: either WAIT or READY
            String response = in.readLine();
            System.out.println("Server : " + response);

            if ("READY".equals(response)) {
                // we can start playing
                System.out.println("Game started! Wait for the opponent...");
                String boardUpdate = in.readLine();
                System.out.println("Board status: " + boardUpdate);
                printBoard(boardUpdate);

            }

            if ("WAIT".equals(response)) {
                // wait for READY
                response = in.readLine();
                System.out.println("Server : " + response);
                System.out.println("Game started! You play first.");
            }

            // now game loop
            while (true) {
                System.out.print("Enter move (0-8): ");
                String move = scanner.nextLine();
                out.println(move);

                String serverReply = in.readLine();
                if (serverReply == null) {
                    System.out.println("Connection closed by server.");
                    break;
                }

                System.out.println("Server : " + serverReply);

                if ("KO".equals(serverReply)) {
                    System.out.println("Invalid move, try again.");
                    continue;
                }

                if ("W".equals(serverReply)) {
                    System.out.println("You won!");
                    break;
                }
                if ("P".equals(serverReply)) {
                    System.out.println("Draw!");
                    break;
                }

                // else serverReply == "OK"
                // read board update from opponent
                System.out.println("Wait for the opponent...");

                String boardUpdate = in.readLine();
                System.out.println("Board status: " + boardUpdate);

                // Show board and check if game ended
                if (printBoard(boardUpdate)) {
                    break;
                }

                // continue loop
            }

        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        } finally {
            close();
        }
    }

    /* also checks if the game has ended */
    private boolean printBoard(String boardMsg) {
        if (boardMsg == null)
            return false;
        // boardMsg is like "0,1,2,...,L" or trailing comma etc
        String[] parts = boardMsg.split(",");
        // last part is esito
        for (int i = 0; i < 9 && i < parts.length; i++) {
            String cell = parts[i];
            char symbol = '.';
            if ("1".equals(cell))
                symbol = 'X';
            else if ("2".equals(cell))
                symbol = 'O';
            System.out.print(symbol);
            if (i % 3 == 2)
                System.out.println();
        }
        String result = parts.length > 9 ? parts[9] : "";
        System.out.println("Result field: " + result);
        return !result.isEmpty();
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }

}
