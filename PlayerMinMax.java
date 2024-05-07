import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class PlayerMinMax extends Player {

    public static final int ALPHA_DEFAULT_DEPTH = 3;
    public static final int MAX_SEARCH_TIME_SECS = 20;

    private int depth;
    private boolean prunning;
    private double epsilon;
    public int randSeed;

    public Random random;

    public PlayerMinMax(int id, int randSeed) {
        super(id);
        this.randSeed = randSeed;
        random = new Random(randSeed);
    }

    public void init(int depth, double epsilon, boolean prunning) {
        this.depth = depth;
        this.epsilon = epsilon;
        this.prunning = prunning;
    }

    public int[] alphaBetaAction(Board board, int depth, double alpha, double beta, long deadline,
                                             StateNode node) {

        if (depth == 0 || board.CheckWin(false) || System.nanoTime() >= deadline) {
            int value = 0; //value_function(board, this.id)
            node.nodeValue = value;
            return new int[] {-1, value};
        }

        boolean maximizingPlayer = (board.currentPlayer == this);
        ArrayList<Action> actions = getActions(board);
        Map<Action, ArrayList<Map<Board, Double>>> actionsOutcomes = null; // = expand_nodes(actions, board)

        if (maximizingPlayer) {
            int bestAction = -1;
            double bestValue = -Double.MAX_VALUE;

            for (Action action : actionsOutcomes.keySet()) {
                ActionNode aNode = new ActionNode(action);
                double nodeValue = 0;

                for (Map<Board, Double> map : actionsOutcomes.get(action)) {


                    // What the f--k do I do here?

                    // StateNode outNode = new StateNode(outcome.playerId)

                    /*
                    double [] result = alphabetaAction(
                        outcome, depth - 1, alpha, beta, deadline, outNode
                    )
                    value = result[1]
                    expected_value += proba * value

                    action_node.children.add(outNode)
                    action_node.probs.add(prob)
                    */

                }

                /*
                action_node.nodeValue = nodeValue;
                node.children.add(action_node);

                if (nodeValue > bestValue) {
                    bestAction = action;
                    bestValue = nodeValue;
                   }
                alpha = Math.max(alpha, bestValue)
                if (alpha >= beta) {
                     break;  # beta cutoff
                   }
                */
            }

            //node.nodeValue = bestValue
            //return new double[] {best_action, best_value}
        } else {

            // Same stuff until:

            /*
                ...
                   if (nodeValue < bestValue) {
                    bestAction = action
                    bestValue = nodeValue
                }
                beta = Math.min(beta, bestValue)
                if beta <= alpha:
                    break;  # alpha cutoff
            }
            node.nodeValue = bestValue;
            return bestAction, bestValue;

            */

        }


        return null;
    }

    public ArrayList<Action> getActions(Board board) {
        ArrayList<Action> actions = null;
        if (prunning) {
            // actions = list_prunned_actions(board)
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

        if (epsilon != 0 && random.nextDouble() < epsilon) {
            makeWRandomMove(board);
        }

        long start_t = System.nanoTime();
        int stateId = actions.size();
        StateNode node = new StateNode(board.currentPlayer.id);
        long deadline = (long) ( start_t + MAX_SEARCH_TIME_SECS * Math.floor(1e9) );

        int[] resultAction = alphaBetaAction(board, ALPHA_DEFAULT_DEPTH, Double.MAX_VALUE, -Double.MAX_VALUE,
                deadline, node);

        if (resultAction[0] == -1) {
            makeWRandomMove(board);
        } else {
            Commit(super.CheckPossibleMoves(board).get(resultAction[0]), board);
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
       return super.Trade(actuallyTrade);
    }

    @Override
    public int makeInitialSettlement(Board board) {
       return super.makeInitialSettlement(board);
    }

    @Override
    public int makeInitialRoad(Board board, int spotNum) {
       return super.makeInitialRoad(board, spotNum);
    }

    @Override
    public void discardCards(int num) {
        super.discardCards(num);
    }

    @Override
    public int[] placeKnight(Board board) {
        return super.placeKnight(board);
    }
}
