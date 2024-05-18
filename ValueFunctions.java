import java.util.*;

public class ValueFunctions {

    public static final double ORE_VALUE_0 = 9.26;
    public static final double WHEAT_VALUE_0 = 10.06;
    public static final double SHEEP_VALUE_0 = 5.26;
    public static final double TREE_VALUE_0 = 8.2;
    public static final double BRICK_VALUE_0 = 8.2;
    public static final int[] HEX_NUM_VALUE = {
            -1, -1, 1, 2, 3, 4, 5, 0, 5, 4, 3, 2, 1
    };    // 0, 1, 2, 3, ..., 12

    public static final double REACHABILITY_DEDUCE_FACTOR = 0.0278;
    public static final int TRANSLATE_VARIETY = 4; // i.e. each new resource is like 4 production points

    public static final Map<String, Double> DEFAULT_WEIGHTS = new HashMap<>() {{
        put("public_vps", 3e14);
        put("production", 1e8);
        put("enemy_production", -1e8);
        put("num_tiles", 1.0);
        put("enemy_reachable_production_1", 0.0);
        put("reachable_production_1", 1e5);
        put("buildable_nodes", 1e3);
        put("longest_road", 10.0);
        put("hand_synergy", 1e3);
        put("hand_resources", 1.0);
        put("discard_penalty", -1500.0);
        put("hand_devs", 10.0);
        put("army_size", 10.1);
    }};

    public static final Map<String, Double> CONTENDER_WEIGHTS = new HashMap<>() {{
        put("public_vps", 300000000000001.94);
        put("production", 100000002.04188395);
        put("enemy_production", -99999998.03389844);
        put("num_tiles", 2.91440418);
        put("enemy_reachable_production_1", -10002.018773150001);
        put("reachable_production_1", 10002.018773150001);
        put("buildable_nodes", 1001.86278466);
        put("longest_road", 12.127388499999999);
        put("hand_synergy", 102.40606877);
        put("hand_resources", 2.43644327);
        put("discard_penalty", -3.00141993);
        put("hand_devs", 10.721669799999999);
        put("army_size", 12.93844622);
    }};

    public static final Map<String, Double> DICE_PROB = new HashMap<>() {{
        put("dice_2", 0.0278);
        put("dice_3", 0.0556);
        put("dice_4", 0.0833);
        put("dice_5", 0.1111);
        put("dice_6", 0.1389);
        put("dice_8", 0.1389);
        put("dice_9", 0.1111);
        put("dice_10", 0.0833);
        put("dice_11", 0.0576);
        put("dice_12", 0.0289);
    }};

    public static double baseValueFunc(Map<String, Double> params, Board board, Player player) {

        int myVP = getPlayerVP(board, player);

        double myProduction = valueProduction(player);
        double enemyProduction = valueProduction(player.getEnemy(board));

        int longestRoadLength =  findPlayerLongestRoad(board, player);

        double myReachabilityValue = getPlayerReachableProductionValue(board, player);
        double enemyReachabilityValue = getPlayerReachableProductionValue(board, player.getEnemy(board));

        double closeToCity = (Math.max(2 - playerHasResource(player, Resource_Type.Wheat), 0) +
                Math.max(3 - playerHasResource(player, Resource_Type.Ore), 0)) / 5.0;

        double closeToSettlement = (Math.max(1 - playerHasResource(player, Resource_Type.Wheat), 0)
                + Math.max(1 - playerHasResource(player, Resource_Type.Sheep), 0)
                + Math.max(1 - playerHasResource(player, Resource_Type.Brick), 0)
                + Math.max(1 - playerHasResource(player, Resource_Type.Wood), 0)
        ) / 4.0;

        double handSynergy = (2 - closeToCity - closeToSettlement) / 2.0;

        int numInHand = player.resourcesAtHand.size();
        double discardPenalty = 0;
        if (numInHand >= 7) {
            discardPenalty = params.get("discard_penalty");
        }

        ArrayList<Spot> ownedNodes = new ArrayList<>();
        for (Settlement settlement : player.settlements) {
            ownedNodes.add(settlement.spotLocated);
        }

        ArrayList<Hex> ownedHexes = new ArrayList<>();
        for (Spot spot : ownedNodes) {
            ownedHexes.addAll(spot.adjacentHexes);
        }

        int numHex = ownedHexes.size();

        int buildableNodes = getNumBuildableNodes(board, player);
        double longestRoadFactor = buildableNodes == 0 ? 0 : 0.1;

        double longestRoad = 0;
        if (board.longestRoad == player) {
            longestRoad = params.get("longest_road");
        }

        double sumAllValues = myVP * params.get("public_vps")
                + myProduction * params.get("production")
                + enemyProduction * params.get("enemy_production")
                + myReachabilityValue * params.get("reachable_production_1")
                + enemyReachabilityValue * params.get("enemy_reachable_production_1")
                + handSynergy * params.get("hand_synergy")
                + buildableNodes * params.get("buildable_nodes")
                + numHex * params.get("num_tiles")
                + numInHand * params.get("hand_resources")
                + discardPenalty
                + longestRoadLength * longestRoadFactor
                + longestRoad
                + player.devCards.size() * params.get("hand_devs")
                + player.knightUsed * params.get("army_size");

        return sumAllValues;

    }

    public static double valueProduction(Player player) {

        double probPoint = 2.778 / 100;

        double cumulativeValue = 0;
        int[] sampleSet = new int[] {0, 0, 0, 0, 0};
        for (Settlement settlement : player.settlements) {
            for (Hex hex : settlement.spotLocated.adjacentHexes) {
                if (hex.type == Hex_Type.Brick) {
                    cumulativeValue += BRICK_VALUE_0 * DICE_PROB.get("dice_" + hex.diceNum);
                    sampleSet[0] = 1;
                } else if (hex.type == Hex_Type.Wood) {
                    cumulativeValue += TREE_VALUE_0 * DICE_PROB.get("dice_" + hex.diceNum);
                    sampleSet[1] = 1;
                } else if (hex.type == Hex_Type.Sheep) {
                    cumulativeValue += SHEEP_VALUE_0 * DICE_PROB.get("dice_" + hex.diceNum);
                    sampleSet[2] = 1;
                } else if (hex.type == Hex_Type.Wheat) {
                    cumulativeValue += WHEAT_VALUE_0 * DICE_PROB.get("dice_" + hex.diceNum);
                    sampleSet[3] = 1;
                } else if (hex.type == Hex_Type.Ore) {
                    cumulativeValue += ORE_VALUE_0 * DICE_PROB.get("dice_" + hex.diceNum);
                    sampleSet[4] = 1;
                }
            }
        }

        int nonEmptyResources = 0;
        for (int i : sampleSet) {
            nonEmptyResources++;
        }

        return cumulativeValue + nonEmptyResources * TRANSLATE_VARIETY * probPoint;
    }

    public static int findPlayerLongestRoad(Board board, Player player) {

        int longestConnection = 0;
        for (Spot spot : board.spots) {
            boolean[] visited = new boolean[board.spots.size()];
            for (int i = 0; i < board.spots.size(); i++) {
                visited[i] = false;
            }

            visited[spot.id] = true;
            int max = 0;
            int max2 = 0;
            for (Spot next: spot.spotsConnectedByRoad.keySet()) {
                int length = board.FindLongestRoad(next, visited, player.id);
                if(length > max){
                    if(max > max2) max2 = max;
                    max = length;
                }else if(length > max2){
                    max2 = length;
                }
            }
            longestConnection = max + max2;
        }

        return longestConnection;
    }

    public static double getPlayerReachableProductionValue(Board board, Player player) {

        double cumulativeValue = 0;
        ArrayList<Action> availableActions = new ArrayList<>(player.CheckPossibleMoves(board));
        ArrayList<Spot> reachableSpots = new ArrayList<>();
        for (Action action : availableActions) {
            if (action.actionType == Action_Type.BuildRoad) {
                reachableSpots.add(action.potentialNextSpot);
            }
        }

        for (Spot spot : reachableSpots) {
            for (Hex hex : spot.adjacentHexes) {
                if (hex.type == Hex_Type.Brick) {
                    cumulativeValue += REACHABILITY_DEDUCE_FACTOR * BRICK_VALUE_0 * DICE_PROB.get("dice_" + hex.diceNum);
                } else if (hex.type == Hex_Type.Wood) {
                    cumulativeValue += REACHABILITY_DEDUCE_FACTOR * TREE_VALUE_0 * DICE_PROB.get("dice_" + hex.diceNum);
                } else if (hex.type == Hex_Type.Sheep) {
                    cumulativeValue += REACHABILITY_DEDUCE_FACTOR * SHEEP_VALUE_0 * DICE_PROB.get("dice_" + hex.diceNum);
                } else if (hex.type == Hex_Type.Wheat) {
                    cumulativeValue += REACHABILITY_DEDUCE_FACTOR * WHEAT_VALUE_0 * DICE_PROB.get("dice_" + hex.diceNum);
                } else if (hex.type == Hex_Type.Ore) {
                    cumulativeValue += REACHABILITY_DEDUCE_FACTOR * ORE_VALUE_0 * DICE_PROB.get("dice_" + hex.diceNum);
                }
            }
        }

        return cumulativeValue;
    }

    public static int getNumBuildableNodes(Board board, Player player) {
        ArrayList<Action> availableActions = new ArrayList<>(player.CheckPossibleMoves(board));
        int buildableNodes = 0;
        for (Action action : availableActions) {
            if (action.actionType == Action_Type.BuildRoad || action.actionType == Action_Type.BuildSettlement ||
            action.actionType == Action_Type.UpGradeCity) {
                buildableNodes++;
            }
        }

        return buildableNodes;
    }

    public static int getPlayerVP(Board board, Player player) {
        int VP = 0;
        if(board.largestArmy != null && player.id == board.largestArmy.id) VP += 2;
        if(board.longestRoad != null && player.id == board.longestRoad.id) VP += 2;
        for (int i = 0; i < player.settlements.size(); i++) {
            if (player.settlements.get(i).isCity) VP += 2;
            else VP += 1;
        }
        for (int i = 0; i < player.devCards.size(); i++) {
            if (player.devCards.get(i).type == DevelopmentCard_Type.VictoryPoint) VP += 1;
        }

        return VP;
    }

    public static double playerHasResource(Player player, Resource_Type resourceType) {

        int numResource = 0;
        for (Resource_Type resource : player.resourcesAtHand) {
            if (resource == resourceType) {
                numResource++;
            }
        }

        return numResource;
    }
}
