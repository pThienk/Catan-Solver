
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class Main {
    static String str;
    public static void  main(String[] args) throws Exception {
        /** Board catanBoard = new Board();
        catanBoard.PrintBoard();

        Player player1 = new Player(1);
        Player player2 = new Player(2);
        catanBoard.players.add(player1);
        catanBoard.players.add(player2);

        //6 14 30 31 45 44 24 35
        catanBoard.SettlementPhase();
        catanBoard.PrintBoard();

        catanBoard.GamePhase();

        RunServer(); **/

        int[] scores = Benchmarking.benchmark(100);

        System.out.println("Player 1 wins " + scores[0] + " games, Player 2 wins " + scores[1] + " games");

    }

    private static void RunServer() throws Exception{
        //        class collect data
//        System.out.print(catanBoard.output);
//        Run Server
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(8080));
//        System.out.println("Server is running on port 8080...");
        while (true) {
            Socket clientSocket = serverSocket.accept();
            handleRequest(clientSocket);
            clientSocket.close();
        }
    }
    private static void handleRequest(Socket clientSocket) throws Exception {
        Board catanBoard = new Board();
        catanBoard.PrintBoard();
        str = String.valueOf(catanBoard.output);
        System.out.println(str);
        OutputStream outputStream = clientSocket.getOutputStream();
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                 "Access-Control-Allow-Origin: *\r\n" +
                "\r\n" + str;
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}

/*
log -
-19hexa and 54 points
-Hexa from 0 - n marked
ID and position in <Board> shows the information
-Every hex knows its adjacent hexa
-Every point knows its adjacent points
-Every point knows its port status
-Every hexadecimal knows adjacent point status
—Types are managed through the enumeration of Port_Type and Hex_Type
-Can make change in ui with pointer changed
 */
