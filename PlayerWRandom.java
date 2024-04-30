import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerWRandom extends Player {

    public static final double STONE_VALUE = 9.26;
    public static final double WHEAT_VALUE = 10.06;
    public static final double SHEEP_VALUE = 5.26;
    public static final double TREE_VALUE = 8.2;
    public static final double BRICK_VALUE = 8.2;

    int randSeed;
    public static final double[] DISCRETE_POISSON = {};

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
        ArrayList<Action> actions = CheckPossibleMoves(board);

        int meanInd = 0;
        double stdDev = actions.size() / 4.0;

        int pass = 0;

        while ((pass != -1 || actions.size() > 1) && meanInd != -1) {
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
                    spotValue += BRICK_VALUE;
                } else if (hex.type == Hex_Type.Wood) {
                    spotValue += TREE_VALUE;
                } else if (hex.type == Hex_Type.Sheep) {
                    spotValue += SHEEP_VALUE;
                } else if (hex.type == Hex_Type.Wheat) {
                    spotValue += WHEAT_VALUE;
                } else if (hex.type == Hex_Type.Ore) {
                    spotValue += STONE_VALUE;
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
