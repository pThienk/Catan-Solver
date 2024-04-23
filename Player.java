import java.util.*;

public class Player {
    public int id;
    List<Settlement> settlements = new ArrayList<>();
    List<Road> roads = new ArrayList<>();
    List<Port> ports = new ArrayList<>();
    List<DevelopmentCard> devCards = new ArrayList<>();
    List<Resource_Type> resourcesAtHand = new ArrayList<>();

    int knightUsed = 0;
    public int longestRoadConnection(){
        return 0;
    }

    public Player(int id){
        this.id = id;
    }

    public boolean canBuy(Action_Type type){
        List<Resource_Type> cost = Resources.resourceCost.get(type);
        List<Resource_Type> hand = new ArrayList<>();
        if(resourcesAtHand.isEmpty()) return false;
        for (int i = 0; i < resourcesAtHand.size(); i++) {
            hand.add(resourcesAtHand.get(i));
        }
        for (int i = 0; i < cost.size(); i++) {
            if(hand.contains(cost.get(i))){
                hand.remove(cost.get(i));
            }else{
                return false;
            }
        }
        return true;
    }
    public void Buy(Action_Type type){
        List<Resource_Type> cost = Resources.resourceCost.get(type);
        for (int i = 0; i < cost.size(); i++) {
            if(resourcesAtHand.contains(cost.get(i))){
                resourcesAtHand.remove(cost.get(i));
            }else{
                System.out.println("Error: Resources Doesn't Exist");
            }
        }
    }

    public void MakeMove(Board board){
        int pass = 0;
        while(pass != -1){
            ArrayList<Action> actions = CheckPossibleMoves(board);
            for (int i = 0; i < actions.size(); i++) {
                System.out.println("Action " + i + " : " + actions.get(i).toString());
            }
            System.out.println("Enter which action u want to take: ");
            int move = board.input.nextInt();
            if(move == -1){
                System.out.println("Cheat Code");
                this.resourcesAtHand.add(Resource_Type.Brick);
                this.resourcesAtHand.add(Resource_Type.Wood);
                this.resourcesAtHand.add(Resource_Type.Ore);
                this.resourcesAtHand.add(Resource_Type.Wheat);
                this.resourcesAtHand.add(Resource_Type.Sheep);
                pass = 1;
            }else{
                pass = Commit(actions.get(move), board);
            }
        }
    }
    public int Commit(Action action, Board board){
        if(action.actionType == Action_Type.Pass){
            return -1;
        }else if(action.actionType == Action_Type.BuildRoad){
            board.CreateRoad(this, action.thisSpot, action.potentialNextSpot, true);
            return 1;
        }else if(action.actionType == Action_Type.BuildSettlement){
            board.CreateSettlement(this, action.thisSpot, true);
        }else if(action.actionType == Action_Type.BuyDevelopmentCard){
            board.BuyDevelopmentCard(this, board.round, true);
            return 1;
        }else if(action.actionType == Action_Type.UseDevelopmentCard){
            board.UseDevelopmentCard(this, action.potentialDevCardToUse, true);
            return 1;
        }else if(action.actionType == Action_Type.UpGradeCity){
            board.UpgradeToCity(this, action.thisSpot, true);
            return 1;
        }
        return 0;
    }
    public ArrayList<Action> CheckPossibleMoves(Board board){
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(new Action(null, null, null, Action_Type.Pass));
        if(board.BuyDevelopmentCard(this, board.round, false)){
            actions.add(new Action(null, null, null, Action_Type.BuyDevelopmentCard));
        }
        for (Spot spot : board.spots) {
            //settlement
            if(board.CreateSettlement(this, spot, false)){
                actions.add(new Action(spot, null, null, Action_Type.BuildSettlement));
            }
            //road
            for (Spot adjSpot: spot.adjacentSpots){
                if(board.CreateRoad(this, spot, adjSpot, false )){
                    actions.add(new Action(spot, adjSpot, null, Action_Type.BuildRoad));
                }
            }
            ArrayList<Action> repeatedRoadActions = new ArrayList<>();
            for (Action action1: actions) {
                if(action1.actionType != Action_Type.BuildRoad) continue;
                for (Action action2: actions) {
                    if(action2.actionType != Action_Type.BuildRoad) continue;
                    if(action1.thisSpot.id == action2.potentialNextSpot.id && action2.thisSpot.id == action1.potentialNextSpot.id){
                        if(!repeatedRoadActions.contains(action2))
                            repeatedRoadActions.add(action1);
                    }
                }
            }
            if(!repeatedRoadActions.isEmpty()){
                actions.removeAll(repeatedRoadActions);
            }
            if(spot.hasSettlement){
                if(board.UpgradeToCity(this, spot, false)){
                    actions.add(new Action(spot, null, null, Action_Type.UpGradeCity));
                }
            }
        }
        //dev cards
        for (DevelopmentCard card: devCards) {
            if(board.UseDevelopmentCard(this, card.type, false))
                actions.add(new Action(null, null, card.type, Action_Type.UseDevelopmentCard));
        }

        return actions;
    }
}
