import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerRandom extends Player {
    private Random random;
    private int randSeed;
    public PlayerRandom(int id, int randSeed) {
        super(id);
        this.randSeed = randSeed;
        random = new Random(this.randSeed);
    }

    public int getRandSeed() {
        return randSeed;
    }

    public void setRandSeed(int randSeed) {
        this.randSeed = randSeed;
    }

    // Completely random set of moves
    @Override
    public void MakeMove(Board board) {
        // super.MakeMove(board);
        ArrayList<Action> actions = super.CheckPossibleMoves(board);

        while(!actions.isEmpty()) {
            int move = random.nextInt(actions.size());

            int pass = super.Commit(actions.get(move), board);
            actions.remove(move);

            if (pass == -1) {
                break;
            }
        }

    }

    @Override
    public int makeInitialSettlement(Board board) {
        return random.nextInt(54) + 1;
    }

    @Override
    public int makeInitialRoad(Board board, int spotNum) {

        System.out.println("Making road");
        List<Spot> availableSpots = board.spots.get(spotNum).adjacentSpots;
        int roadInd = random.nextInt(availableSpots.size());

        while(!board.CreateRoad(this, board.spots.get(spotNum), availableSpots.get(roadInd), false)) {
            roadInd = random.nextInt(availableSpots.size());
        }

        return availableSpots.get(roadInd).id;
    }

    @Override
    public void discardCards(int num) {

        for (int i = 0; i < num; i++) {
            super.resourcesAtHand.remove(random.nextInt(super.resourcesAtHand.size()));
        }
    }

    @Override
    public int[] placeKnight(Board board) {

        return new int[] {random.nextInt(Resources.resourcesList.length), random.nextInt(11) + 1};
    }
}
