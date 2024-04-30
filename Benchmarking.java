public class Benchmarking {

    public static int getInitialSettlement(Player player, Board board) {
        return player.makeInitialSettlement(board);
    }

    public static int getRoadPlacement(Player player, int settlementSpot, Board board) {
        return player.makeInitialRoad(board, settlementSpot);
    }

    public static int[] benchmark(int gameNum, Player p1, Player p2) {

        int p1WonNum = 0;
        int p2WonNum = 0;

        for (int gameInd = 0; gameInd < gameNum; gameInd++) {

            Board benchmarkBoard = new Board(true);
            benchmarkBoard.players.add(p1);
            benchmarkBoard.players.add(p2);

            benchmarkBoard.SettlementPhase();
            benchmarkBoard.GamePhase();

            if (p1.hasWon) {
                p1WonNum++;
            } else {
                p2WonNum++;
            }

            p1.hasWon = false;
            p2.hasWon = false;
        }

        return new int[] {p1WonNum, p2WonNum};
    }

}
