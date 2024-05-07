import java.util.*;

public class PlayerWRandom extends Player {

    public static double ORE_VALUE = 9.26;
    public static double WHEAT_VALUE = 10.06;
    public static double SHEEP_VALUE = 5.26;
    public static double TREE_VALUE = 8.2;
    public static double BRICK_VALUE = 8.2;
    public static int[] HEX_NUM_VALUE = {
            -1, -1, 1, 2, 3, 4, 5, 0, 5, 4, 3, 2, 1
    };    // 0, 1, 2, 3, ..., 12

    int randSeed;
    public static double[] DISCRETE_POISSON = {};

    Random random;

    public PlayerWRandom(int id, int randSeed) {
        super(id);
        random = new Random(randSeed);
        this.randSeed = randSeed;
    }

    public void setRandSeed(int randSeed) {
        this.randSeed = randSeed;
    }

    public int getRandSeed() {
        return randSeed;
    }

    @Override
    public void MakeMove(Board board) {

        ArrayList<Action> actions = new ArrayList<>(super.CheckPossibleMoves(board));

        int meanInd = 0;

        int pass = 0;

        while ((pass != -1 || actions.size() > 1) && meanInd != -1) {

            double stdDev = actions.size() / 4.0;

            for (Action action : actions) {
                if (action.actionType == Action_Type.UpGradeCity) {
                    meanInd = actions.indexOf(action);
                    break;
                } else if (action.actionType == Action_Type.BuildSettlement) {
                    meanInd = actions.indexOf(action);
                    break;
                } else if (action.actionType == Action_Type.BuyDevelopmentCard) {
                    meanInd = actions.indexOf(action);
                    break;
                } else {
                    meanInd = -1;
                    break;
                }
            }

            if (meanInd == -1) {
                while(!actions.isEmpty()) {
                    int move = random.nextInt(actions.size());

                    pass = super.Commit(actions.get(move), board);
                    actions.remove(move);

                    if (pass == -1) {
                        break;
                    }
                }
            } else {
                int chosenInd = (int) random.nextGaussian(meanInd, stdDev);

                while (chosenInd >= actions.size() || chosenInd < 0) {
                    chosenInd = (int) random.nextGaussian(meanInd, stdDev);
                }

                pass = super.Commit(actions.get(chosenInd), board);
                actions.remove(chosenInd);
            }
        }

    }

    @Override
    public int makeInitialSettlement(Board board) {

        double maxSpotValue = 0;
        Spot lastAvailableSpot = null;
        Spot maxSpot = null;


        for (Spot spot : board.spots) {
            double spotValue = 0;

            if (!board.CreateSettlement(this, spot, false)) {
                continue;
            }

            for (Hex hex : spot.adjacentHexes) {
                if (hex.type == Hex_Type.Brick) {
                    spotValue += BRICK_VALUE * HEX_NUM_VALUE[hex.diceNum];
                } else if (hex.type == Hex_Type.Wood) {
                    spotValue += TREE_VALUE * HEX_NUM_VALUE[hex.diceNum];
                } else if (hex.type == Hex_Type.Sheep) {
                    spotValue += SHEEP_VALUE * HEX_NUM_VALUE[hex.diceNum];
                } else if (hex.type == Hex_Type.Wheat) {
                    spotValue += WHEAT_VALUE * HEX_NUM_VALUE[hex.diceNum];
                } else if (hex.type == Hex_Type.Ore) {
                    spotValue += ORE_VALUE* HEX_NUM_VALUE[hex.diceNum];
                }
            }

            if (spotValue > maxSpotValue) {
                maxSpotValue = spotValue;
                maxSpot = spot;
            } else {
                lastAvailableSpot = spot;
            }
        }

        if (maxSpot == null) {
            maxSpot = lastAvailableSpot;
        }

        return maxSpot.id;
    }

    @Override
    public int makeInitialRoad(Board board, int spotNum) {

        List<Spot> availableSpots = board.spots.get(spotNum).adjacentSpots;

        // System.out.println(availableSpots.size() + " " + spotNum);
        int roadInd = random.nextInt(availableSpots.size());
        //System.out.println(roadInd);
//
//        while(board.CreateRoad(this, board.spots.get(spotNum), availableSpots.get(roadInd), false)) {
//            availableSpots.remove(roadInd);
//            roadInd = random.nextInt(availableSpots.size());
//        }

        return availableSpots.get(roadInd).id;
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
