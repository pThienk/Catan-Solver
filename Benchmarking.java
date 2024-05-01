public class Benchmarking {

    public static int getInitialSettlement(Player player, Board board) {
        return player.makeInitialSettlement(board);
    }

    public static int getRoadPlacement(Player player, int settlementSpot, Board board) {
        return player.makeInitialRoad(board, settlementSpot);
    }

    public static int[] benchmark(int gameNum) {

        int p1WonNum = 0;
        int p2WonNum = 0;

        for (int gameInd = 0; gameInd < gameNum; gameInd++) {
            Player p1 = new PlayerRandom(1, 111);
            Player p2 = new PlayerWRandom(2, 113);

            Board benchmarkBoard = new Board(true);
            int rand = benchmarkBoard.random.nextInt(2);
            if(rand > 0){
                benchmarkBoard.players.add(p1);
                benchmarkBoard.players.add(p2);
            }else{
                benchmarkBoard.players.add(p2);
                benchmarkBoard.players.add(p1);
            }


            benchmarkBoard.SettlementPhase();

            benchmarkBoard.PrintBoard();
            benchmarkBoard.GamePhase();

            if (p1.hasWon) {
                p1WonNum++;
            } else if(p2.hasWon){
                p2WonNum++;
            }else{
                System.out.println("No one won");
            }

            p1.hasWon = false;
            p2.hasWon = false;
        }

        return new int[] {p1WonNum, p2WonNum};
    }

}
