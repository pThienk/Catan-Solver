import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class PlayerMinMax extends Player {

    public static final int ALPHA_DEFAULT_DEPTH = 5;
    public static final int MAX_SEARCH_TIME_SECS = 20;

    private int depth;
    private boolean pruning;
    private double epsilon;
    public int randSeed;

    public Random random;

    public PlayerMinMax(int id, String tag, int randSeed, int depth, double epsilon, boolean pruning) {
        super(id, tag);
        this.randSeed = randSeed;
        random = new Random(randSeed);
        init(depth, epsilon, pruning);
    }

    public void init(int depth, double epsilon, boolean pruning) {
        this.depth = depth;
        this.epsilon = epsilon;
        this.pruning = pruning;
    }

    public Tuple<Action, Double> alphaBetaAction(Board board, int depth, double alpha, double beta, long deadline,
                                             StateNode node) {

        if (depth == 0 || board.CheckWin(false) || System.nanoTime() >= deadline) {
            double value = ValueFunctions.baseValueFunc(ValueFunctions.DEFAULT_WEIGHTS, board, board.currentPlayer);
            node.nodeValue = value;

            return new Tuple<>(null, value);
        }

        boolean maximizingPlayer = (board.currentPlayer == this);
        ArrayList<Action> actions = getActions(board);
        ArrayList<Tuple<Action, ArrayList<Tuple<Board, Double>>>> actionsOutcomes = TreeUtils.expandSpectrum(board, actions);

        if (maximizingPlayer) {
            Action bestAction = null;
            double bestValue = -Double.MAX_VALUE;

            for (Tuple<Action, ArrayList<Tuple<Board, Double>>> actionOutcome : actionsOutcomes) {
                ActionNode aNode = new ActionNode(actionOutcome.getFirstElement());
                double nodeValue = 0;

                for (Tuple<Board, Double> outcome : actionOutcome.getSecondElement()) {

                    StateNode outNode = new StateNode(outcome.getFirstElement().currentPlayer.id);


                    Tuple<Action, Double> result = alphaBetaAction(
                        outcome.getFirstElement(), depth - 1, alpha, beta, deadline, outNode
                    );

                    double value = result.getSecondElement();
                    nodeValue += outcome.getSecondElement() * value;

                    aNode.children.add(outNode);
                    aNode.probs.add(outcome.getSecondElement());

                }


                aNode.nodeValue = nodeValue;
                node.children.add(aNode);

                if (nodeValue > bestValue) {
                    bestAction = actionOutcome.getFirstElement();
                    bestValue = nodeValue;
                }
                alpha = Math.max(alpha, bestValue);
                if (alpha >= beta) {
                     break;
                }

            }

            node.nodeValue = bestValue;
            return new Tuple<>(bestAction, bestValue);
        } else {

            Action bestAction = null;
            double bestValue = Double.MAX_VALUE;

            for (Tuple<Action, ArrayList<Tuple<Board, Double>>> actionOutcome : actionsOutcomes) {
                ActionNode aNode = new ActionNode(actionOutcome.getFirstElement());
                double nodeValue = 0;

                for (Tuple<Board, Double> outcome : actionOutcome.getSecondElement()) {

                    StateNode outNode = new StateNode(outcome.getFirstElement().currentPlayer.id);


                    Tuple<Action, Double> result = alphaBetaAction(
                            outcome.getFirstElement(), depth - 1, alpha, beta, deadline, outNode
                    );

                    double value = result.getSecondElement();
                    nodeValue += outcome.getSecondElement() * value;

                    aNode.children.add(outNode);
                    aNode.probs.add(outcome.getSecondElement());

                }


                aNode.nodeValue = nodeValue;
                node.children.add(aNode);

                if (nodeValue < bestValue) {
                    bestAction = actionOutcome.getFirstElement();
                    bestValue = nodeValue;
                }
                alpha = Math.max(alpha, bestValue);
                if (alpha <= beta) {
                    break;
                }

            }

            node.nodeValue = bestValue;
            return new Tuple<>(bestAction, bestValue);

        }
    }

    public ArrayList<Action> getActions(Board board) {
        ArrayList<Action> actions = null;
        if (pruning) {
            actions = TreeUtils.listPrunedActions(board); // Not done yet
        } else {
            actions = new ArrayList<>(super.CheckPossibleMoves(board));
        }

        return actions;
    }

    @Override
    public void MakeMove(Board board) {

        ArrayList<Action> actions = getActions(board);

        if (actions.size() == 1) {
            Commit(actions.get(0), board);
        }

        if (epsilon != 0 && random.nextInt(100) < epsilon * 100) {
            makeWRandomMove(board);
        }

        long start_t = System.nanoTime();
        //int stateId = actions.size();
        StateNode node = new StateNode(board.currentPlayer.id);
        long deadline = (long) (start_t + MAX_SEARCH_TIME_SECS * Math.floor(1e9));

        Tuple<Action, Double> resultAction = alphaBetaAction(board, ALPHA_DEFAULT_DEPTH, Double.MAX_VALUE, -Double.MAX_VALUE,
                deadline, node);

        if (resultAction.getFirstElement() == null) {
            makeWRandomMove(board);
        } else {
            Commit(resultAction.getFirstElement(), board);
        }

    }

    public void makeWRandomMove(Board board) {
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
    public boolean Trade(boolean actuallyTrade) {
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
                    spotValue += ValueFunctions.BRICK_VALUE_0 * ValueFunctions.DICE_PROB.get("dice_" + hex.diceNum);
                } else if (hex.type == Hex_Type.Wood) {
                    spotValue += ValueFunctions.TREE_VALUE_0 * ValueFunctions.DICE_PROB.get("dice_" + hex.diceNum);
                } else if (hex.type == Hex_Type.Sheep) {
                    spotValue += ValueFunctions.SHEEP_VALUE_0 * ValueFunctions.DICE_PROB.get("dice_" + hex.diceNum);
                } else if (hex.type == Hex_Type.Wheat) {
                    spotValue += ValueFunctions.WHEAT_VALUE_0 * ValueFunctions.DICE_PROB.get("dice_" + hex.diceNum);
                } else if (hex.type == Hex_Type.Ore) {
                    spotValue += ValueFunctions.ORE_VALUE_0 * ValueFunctions.DICE_PROB.get("dice_" + hex.diceNum);
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
