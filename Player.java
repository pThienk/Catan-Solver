import java.util.*;

public class Player {
    public int id;
    List<Settlement> settlements = new ArrayList<>();
    List<Road> roads = new ArrayList<>();
    List<Port> ports = new ArrayList<>();
    List<DevelopmentCard> devCards = new ArrayList<>();
    List<Resource_Type> resourcesAtHand = new ArrayList<>();
    boolean hasWon = false;
    public String tag;
    int knightUsed = 0;
    int VP = 0;

    public Player(int id, String tag){
        this.id = id;
        this.tag = tag;
    }

    public Player getEnemy(Board board) {
        for (Player p : board.players) {
            if (p != this) {
                return p;
            }
        }

        return null;
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

    public boolean Trade(boolean actuallyTrade){
        HashMap<Resource_Type, Integer> canTradeDIct = findTradingOptions();
        if(canTradeDIct.isEmpty()){
            return false;
        }
        if(actuallyTrade){
            for (Resource_Type type: canTradeDIct.keySet()) {
                System.out.println("You can trade " + type);
            }
            Scanner in = new Scanner(System.in);
            System.out.println("Enter the trade you want to do [SB(Wh)(Wd)O]");
            Resource_Type resOUT = Resources.resourcesList[in.nextInt()];
            System.out.println("Enter the new resource you want [SB(Wh)(Wd)O]: ");
            Resource_Type resIN = Resources.resourcesList[in.nextInt()];
            for (int i = 0; i < canTradeDIct.get(resOUT); i++) {
                resourcesAtHand.remove(resOUT);
            }
            resourcesAtHand.add(resIN);
        }
        return true;
    }
    public Map<Resource_Type, Integer> organizeResources(){
        Map<Resource_Type, Integer> resourceCount = new HashMap<>();
        for (Resource_Type resource: resourcesAtHand) {
            if(!resourceCount.containsKey(resource)){
                resourceCount.put(resource, 1);
            }else{
                int oldCount = resourceCount.get(resource);
                resourceCount.put(resource, 1 + oldCount);
            }
        }
        return resourceCount;
    }
    public HashMap<Resource_Type, Integer> findTradingOptions(){
        Map<Resource_Type, Integer> resourceCount = organizeResources();
        Map<Resource_Type, Integer> tradeDict = new HashMap<>();
        int generalTrade = 4;
        for (int i = 0; i < Resources.resourcesList.length; i++) {
            tradeDict.put(Resources.resourcesList[i], generalTrade);
        }
        for (Port port: ports) {
            if(port.type == Port_Type.General){
                generalTrade = 3;
                for (int i = 0; i < Resources.resourcesList.length; i++) {
                    if(tradeDict.get(Resources.resourcesList[i]) > generalTrade){
                        tradeDict.put(Resources.resourcesList[i], generalTrade);
                    }
                }
            }if(port.type == Port_Type.Sheep){
                tradeDict.put(Resource_Type.Sheep, 2);
            }if(port.type == Port_Type.Wood){
                tradeDict.put(Resource_Type.Wood, 2);
            }if(port.type == Port_Type.Ore){
                tradeDict.put(Resource_Type.Ore, 2);
            }if(port.type == Port_Type.Wheat){
                tradeDict.put(Resource_Type.Wheat, 2);
            }if(port.type == Port_Type.Brick){
                tradeDict.put(Resource_Type.Brick, 2);
            }
        }
        HashMap<Resource_Type, Integer> ableToTrade = new HashMap<>();
        for (Resource_Type res: resourceCount.keySet()) {
            if(resourceCount.get(res) >= tradeDict.get(res)){
                ableToTrade.put(res, tradeDict.get(res));
            }
        }
        return ableToTrade;
    }

    public void MakeMove(Board board){
        int pass = 0;
        while(pass != -1) {
            ArrayList<Action> actions = CheckPossibleMoves(board);
            for (int i = 0; i < actions.size(); i++) {
                System.out.println("Action " + i + " : " + actions.get(i).toString());
            }
            System.out.println("Enter which action u want to take: ");
            int move = board.input.nextInt();
            if(move == -1) {
                System.out.println("Cheat Code");
                this.resourcesAtHand.add(Resource_Type.Brick);
                this.resourcesAtHand.add(Resource_Type.Wood);
                this.resourcesAtHand.add(Resource_Type.Ore);
                this.resourcesAtHand.add(Resource_Type.Wheat);
                this.resourcesAtHand.add(Resource_Type.Sheep);
                Map<Resource_Type, Integer> resourceCount = organizeResources();
                System.out.println("resourceCount size: " + resourceCount.size());
                System.out.println("hand size: " + resourcesAtHand.size());
                pass = 1;
            } else if(move == -2) {
                System.out.println("Cheat Code");
                System.out.println("hand size before: " + resourcesAtHand.size());
                Map<Resource_Type, Integer> resourceCount = organizeResources();
                System.out.println("resourceCount size: " + resourceCount.size());
                System.out.println("hand size: " + resourcesAtHand.size());
                for (Resource_Type type: resourceCount.keySet()) {
                    System.out.print(type + ": " + resourceCount.get(type) + ", ");
                }
                System.out.println();
                pass = 1;
            } else {
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
        }else if(action.actionType == Action_Type.Trade){
            Trade(true);
        }
        return 0;
    }
    public ArrayList<Action> CheckPossibleMoves(Board board){
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(new Action(null, null, null, Action_Type.Pass));
        if(Trade(false)){
            actions.add(new Action(null, null, null, Action_Type.Trade));
        }
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

    // For benchmarking
    public int makeInitialSettlement(Board board) {
        return 0;
    }

    // For benchmarking
    public int makeInitialRoad(Board board, int spotNum) {
        return 0;
    }

    public void discardCards(int num) {

    }

    public int[] placeKnight(Board board) {
        return new int[] {};
    }
}
