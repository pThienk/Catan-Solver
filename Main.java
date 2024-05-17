
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

        //runPvPGame();

        //int[] scores = Benchmarking.benchmark(1000, 1876, 1945, 2001);

        //System.out.println("Player 1 wins " + scores[0] + " games, Player 2 wins " + scores[1] + " games" +
        //        ", Nobody won for " + scores[2] + " games");

        //double[] stats = getBenchmarkStatistics(100, 100, true);

        //System.out.println("P1 wins an avg of " + stats[0] + " percent, with StdDEV "+ stats[1] + ". P2 wins an avg of " +
              // stats[2] + " percent, with StdDEV " + stats[3]);

        //runPvBotGame();
        //runPvMinMaxGame();

        int [] results = Benchmarking.benchmarkMinMax(true, 100, 1500, 3346, 3333);

        System.out.println("WRand: " + results[0] + ", " + "MinMax: " + results[1] + ", " + "Discarded: " + results[2]);
    }

    public static void runPvPGame() {
        Board catanBoard = new Board();
        catanBoard.PrintBoard();

        Player player1 = new Player(1, "human");
        Player player2 = new Player(2, "human");
        catanBoard.players.add(player1);
        catanBoard.players.add(player2);

        //6 14 30 31 45 44 24 35
        catanBoard.SettlementPhase();
        catanBoard.PrintBoard();

        catanBoard.GamePhase();
    }

    public static void runPvBotGame() {
        Board catanBoard = new Board();
        catanBoard.PrintBoard();

        Player player1 = new Player(1, "human");
        Player player2 = new PlayerWRandom(2, "bot", 1134);
        catanBoard.players.add(player1);
        catanBoard.players.add(player2);

        //6 14 30 31 45 44 24 35
        catanBoard.SettlementPhase();
        catanBoard.PrintBoard();

        catanBoard.GamePhase();
    }

    public static void runPvMinMaxGame() {
        Board catanBoard = new Board();
        catanBoard.PrintBoard();

        Player player1 = new Player(1, "human");
        Player player2 = new PlayerMinMax(2, "bot", 1112, 3, 0.2, false);
        catanBoard.players.add(player1);
        catanBoard.players.add(player2);

        //6 14 30 31 45 44 24 35
        catanBoard.SettlementPhase();
        catanBoard.PrintBoard();

        catanBoard.GamePhase();
    }

    public static double[] getBenchmarkStatistics(int dataPoints, int gameNumPerPoint, boolean boardHeldConstant) {

        final int P1_ROOT_SEED = 131071; // 6th Mersenne Prime
        final int P2_ROOT_SEED = 524287; // 7th Mersenne Prime
        final int BOARD_ROOT_SEED = 2147483647; // 8th Mersenne Prime

        double p1CulPercent = 0;
        double p2CulPercent = 0;
        double p1CulPercentSq = 0;
        double p2CulPercentSq = 0;

        for (int i = 0; i < dataPoints; i++) {

            int[] pointScores = Benchmarking.benchmark(gameNumPerPoint, P1_ROOT_SEED / (i + 1),
                    P2_ROOT_SEED / (i + 1), boardHeldConstant ? BOARD_ROOT_SEED : BOARD_ROOT_SEED / (i + 1));

            p1CulPercent += (pointScores[0] / (double) (gameNumPerPoint - pointScores[2])) * 100;
            p2CulPercent += (pointScores[1] / (double) (gameNumPerPoint - pointScores[2])) * 100;

            p1CulPercentSq += Math.pow((pointScores[0] / (double) (gameNumPerPoint - pointScores[2])) * 100, 2);
            p2CulPercentSq += Math.pow((pointScores[1] / (double) (gameNumPerPoint - pointScores[2])) * 100, 2);
        }

        double p1Avg = p1CulPercent / dataPoints;
        double p2Avg = p2CulPercent / dataPoints;

        double p1StdDev = Math.sqrt((p1CulPercentSq/dataPoints) - Math.pow(p1Avg, 2));
        double p2StdDev = Math.sqrt((p2CulPercentSq/dataPoints) - Math.pow(p2Avg, 2));

        return new double[] {p1Avg, p1StdDev, p2Avg, p2StdDev};
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
