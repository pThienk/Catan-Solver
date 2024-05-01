import java.util.*;

public class PlayerRandom extends Player {
    private Random random;
    private int randSeed;
    public PlayerRandom(int id, int randSeed) {
        super(id);
        this.randSeed = randSeed;
        random = new Random(this.randSeed);
    }

    // Completely random set of moves
    @Override
    public void MakeMove(Board board) {
        // super.MakeMove(board);
        ArrayList<Action> actions = new ArrayList<>(super.CheckPossibleMoves(board));
        int move = random.nextInt(actions.size());
        int pass = super.Commit(actions.get(move), board);

        while(pass != -1) {
            actions = new ArrayList<>(super.CheckPossibleMoves(board));
            move = random.nextInt(actions.size());
            pass = super.Commit(actions.get(move), board);
        }
    }

    public boolean Trade(boolean actuallyTrade){
        HashMap<Resource_Type, Integer> canTradeDIct = findTradingOptions();
        if(canTradeDIct.isEmpty()){
            return false;
        }
        if(actuallyTrade){
            int rand = random.nextInt(canTradeDIct.keySet().size());
            Resource_Type resOUT = null;
            int index = 0;
            for (Resource_Type type: canTradeDIct.keySet()) {
//                System.out.println("You can trade " + type);
                if(index == rand){
                    resOUT = type;
                    break;
                }
                index ++;
            }

            int randIN = random.nextInt(Resources.resourcesList.length);
            Resource_Type resIN = Resources.resourcesList[randIN];

            while(resIN == resOUT){
                randIN = random.nextInt(Resources.resourcesList.length);
                resIN = Resources.resourcesList[randIN];
            }
            for (int i = 0; i < canTradeDIct.get(resOUT); i++) {
                resourcesAtHand.remove(resOUT);
            }
            resourcesAtHand.add(resIN);
            System.out.println("Traded "  + resIN + " for " + resOUT);
        }
        return true;
    }

    @Override
    public int makeInitialSettlement(Board board) {
        return random.nextInt(54);
    }

    @Override
    public int makeInitialRoad(Board board, int spotNum) {

        List<Spot> availableSpots = board.spots.get(spotNum).adjacentSpots;

       // System.out.println(availableSpots.size() + " " + spotNum);
        int roadInd = random.nextInt(availableSpots.size());
        //System.out.println(roadInd);

//        while(board.CreateRoad(this, board.spots.get(spotNum), availableSpots.get(roadInd), false)) {
//            availableSpots.remove(roadInd);
//            roadInd = random.nextInt(availableSpots.size());
//        }

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
