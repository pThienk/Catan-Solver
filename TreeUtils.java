import java.util.ArrayList;
import java.util.Arrays;

public class TreeUtils {
    public static final ArrayList<Action_Type> DETERMINISTIC_ACTIONS = new ArrayList<>(Arrays.asList(new Action_Type[]{
            Action_Type.Pass,
            Action_Type.BuildSettlement,
            Action_Type.BuildRoad,
            Action_Type.UpGradeCity,
            Action_Type.UseDevelopmentCard,
            Action_Type.Trade
    }));

    public static ArrayList<Tuple<Board, Double>> executeDeterministic(Board board, Action action) {
        Board gameCopy = new Board(board);
        gameCopy.currentPlayer.Commit(action, gameCopy);

        ArrayList<Tuple<Board, Double>> results = new ArrayList<>();
        results.add(new Tuple<>(gameCopy, 1.0));

        return results;
    }

    public static ArrayList<Tuple<Board, Double>> executeSpectrum(Board board, Action action) {

        ArrayList<Tuple<Board, Double>> results = new ArrayList<>();

        if (DETERMINISTIC_ACTIONS.contains(action.actionType)) {
            return executeDeterministic(board, action);
        } else if (!board.isDiceRolled) {

            for (int roll = 2; roll <= 12; roll++) {
                // This shit is not doable right now

                /*

                outcome = Tuple(roll / 2, math.ceil(roll / 2))

                option_action = Action(action.color, action.action_type, outcome)
                option_game = game.copy()
                option_game.execute(option_action, validate_action=False)
                results.append((option_game, number_probability(roll))

                */
            }

        } else if (action.actionType == Action_Type.BuyDevelopmentCard) {

            ArrayList<DevelopmentCard_Type> currentDeck = new ArrayList<>(DevelopmentCard.developmentCardsDeck);
            Player enemy = board.currentPlayer.getEnemy(board);

            for (DevelopmentCard card : enemy.devCards) {
                currentDeck.add(card.type);
            }

            for (DevelopmentCard_Type card : currentDeck) {
                Action optionAction = new Action(null, null, card, Action_Type.UseDevelopmentCard);
                Board optionGame = new Board(board);

                optionGame.currentPlayer.Commit(optionAction, optionGame);

                results.add(new Tuple<>(optionGame, countCardsInDeck(currentDeck, card)/(double) currentDeck.size()));
            }

            return results;
        }

        /*
            else if (MOVE_ROBBER) {
             (coordinate, robbed_color, _) = action.value
            if robbed_color is None:  # no one to steal, then deterministic
                return execute_deterministic(game, action)

            results = []
            opponent_hand = get_player_freqdeck(game.state, robbed_color)
            opponent_hand_size = sum(opponent_hand)
            if (opponent_hand_size) == 0:
            # Nothing to steal
                 return execute_deterministic(game, action)

            for card in RESOURCES:
                option_action = Action(
                action.color,
                action.action_type,
                (coordinate, robbed_color, card),
            )

            option_game = game.copy()
            option_game.execute(option_action, validate_action=False)
            results.append((option_game, 1 / 5.0))
            return results
         }
         */
        return null;
    }

    public static ArrayList<Tuple<Action, ArrayList<Tuple<Board, Double>>>> expandSpectrum(Board board, ArrayList<Action> actions) {
        ArrayList<Tuple<Action, ArrayList<Tuple<Board, Double>>>> children = new ArrayList<>();

        for (Action action : actions) {
            ArrayList<Tuple<Board, Double>> outcomes = executeSpectrum(board, action);
            children.add(new Tuple<>(action, outcomes));
        }

        return children;
    }

    public static ArrayList<Action> listPrunedActions(Board board) {
        return null; // Do we even want to do this?
    }

    public static int countCardsInDeck(ArrayList<DevelopmentCard_Type> deck, DevelopmentCard_Type cardType) {
         int count = 0;

         for (DevelopmentCard_Type card : deck) {
             if (card == cardType) {
                 count++;
             }
         }

         return count;
    }
}
