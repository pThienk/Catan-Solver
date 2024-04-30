import java.util.ArrayList;
import java.util.Collections;
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
        ArrayList<Action> actions = new ArrayList<>(super.CheckPossibleMoves(board));

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

        List<Spot> availableSpots = board.spots.get(spotNum).adjacentSpots;

       // System.out.println(availableSpots.size() + " " + spotNum);
        int roadInd = random.nextInt(availableSpots.size());
        //System.out.println(roadInd);

        while(board.CreateRoad(this, board.spots.get(spotNum), availableSpots.get(roadInd), false)) {
            availableSpots.remove(roadInd);
            roadInd = random.nextInt(availableSpots.size());
        }

        return availableSpots.get(roadInd).id;
    }

    @Override
    public void discardCards(int num) {

        for (int i = 0; i < num; i++) {
            if (!super.resourcesAtHand.isEmpty()) {
                super.resourcesAtHand.remove(random.nextInt(super.resourcesAtHand.size()));
            } else {
                break;
            }
        }
    }

    @Override
    public int[] placeKnight(Board board) {

        return new int[] {random.nextInt(Resources.resourcesList.length), random.nextInt(11) + 1};
    }
}
