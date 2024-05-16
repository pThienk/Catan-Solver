import java.util.*;

public class Board {
    public List<Hex> hexes = new ArrayList<Hex>();
    public List<Spot> spots = new ArrayList<Spot>();
    public List<Settlement> settlements = new ArrayList<Settlement>();
    public List<Road> roads = new ArrayList<Road>();
    public List<Player> players = new ArrayList<Player>();

    public boolean isBenchmarking = false;

    public Hex blockedHex;
    public int mostKnightAmount = 2;
    public Player largestArmy = null;
    public int longestRoadAmount = 4;
    public Player longestRoad = null;
    public boolean isInitialSettlementPhase = true;
    public boolean devCardUsedThisRound = false;

    public Player currentPlayer;
    public int round;
    public Dice dice = new Dice(false);
    public StringBuilder output = new StringBuilder(); // 用于收集输出数据的StringBuilder
    public Scanner input = new Scanner(System.in);
    public Random random;
    public int randSeed;

    /**
     * Generates a Random Board
     */
    public Board(){
       CreateRandomBoard();
       random = new Random();
    }

    public Board(Board otherBoard) {
        this.hexes = new ArrayList<>(otherBoard.hexes);
        this.spots = new ArrayList<>(otherBoard.spots);
        this.settlements = new ArrayList<>(otherBoard.settlements);
        this.roads = new ArrayList<>(otherBoard.roads);
        this.players = new ArrayList<>(otherBoard.players);
        this.isBenchmarking = otherBoard.isBenchmarking;
        this.blockedHex = otherBoard.blockedHex;
        this.mostKnightAmount = otherBoard.mostKnightAmount;
        this.largestArmy = otherBoard.largestArmy;
        this.longestRoadAmount = otherBoard.longestRoadAmount;
        this.longestRoad = otherBoard.longestRoad;
        this.isInitialSettlementPhase = otherBoard.isInitialSettlementPhase;
        this.devCardUsedThisRound = otherBoard.devCardUsedThisRound;
        this.currentPlayer = otherBoard.currentPlayer;
        this.round = otherBoard.round;
        this.dice = new Dice(otherBoard.dice);
        this.output = new StringBuilder(otherBoard.output);
        this.input = new Scanner(System.in);
        this.random = new Random(otherBoard.randSeed);
        this.randSeed = otherBoard.randSeed;
    }

    public Board(boolean isBenchmarking) {
        CreateRandomBoard();
        this.isBenchmarking = isBenchmarking;
        random = new Random();
    }

    public Board(boolean isBenchmarking, int randSeed) {
        CreateRandomBoard();
        this.isBenchmarking = isBenchmarking;
        this.randSeed = randSeed;
        random = new Random(randSeed);
    }

    public void SettlementPhase(){
        for (int i = 0; i < players.size(); i++) {
            System.out.println("Player " + players.get(i).id + " pick spot.");
            //here the Player chooses the spots

            int spotNum;
            if (isBenchmarking || players.get(i).tag.equals("bot")) {
                spotNum = Benchmarking.getInitialSettlement(players.get(i), this);
            } else {
                spotNum = input.nextInt() % 54;
            }

            boolean success = CreateSettlement(players.get(i), spots.get(spotNum), true);
            while(!success){
                System.out.println("Failed, Try Another Spot");

                if (isBenchmarking || players.get(i).tag.equals("bot")) {
                    spotNum = Benchmarking.getInitialSettlement(players.get(i), this);
                } else {
                    spotNum = input.nextInt();
                }
                success = CreateSettlement(players.get(i), spots.get(spotNum), true);
            }
            System.out.println("Player " + players.get(i).id + " has picked spot: " + spotNum );
            System.out.println("Player " + players.get(i).id + " pick road.");
            //here the Player chooses the road
            int spotRoad;

            if (isBenchmarking || players.get(i).tag.equals("bot")) {
                spotRoad = Benchmarking.getRoadPlacement(players.get(i), spotNum, this);
                //System.out.println("Road spot choose " + spotRoad);
            } else {
                spotRoad = input.nextInt();
            }
            success = CreateRoad(players.get(i), spots.get(spotRoad), spots.get(spotNum), true);

            while(!success){
                System.out.println("Failed, Try Another road");

                if (isBenchmarking || players.get(i).tag.equals("bot")) {
                    spotRoad = Benchmarking.getRoadPlacement(players.get(i), spotNum, this);
                } else {
                    spotRoad = input.nextInt();
                }

                success = CreateRoad(players.get(i), spots.get(spotRoad), spots.get(spotNum), true);
            }
        }
        for (int i = players.size() - 1; i >= 0 ; i--) {
            System.out.println("Player " + players.get(i).id + " pick spot.");
            //here the Player chooses the spots
            int spotNum;

            if (isBenchmarking || players.get(i).tag.equals("bot")) {
                spotNum = Benchmarking.getInitialSettlement(players.get(i), this);
            } else {
                spotNum = input.nextInt() % 54;
            }

            boolean success = CreateSettlement(players.get(i), spots.get(spotNum), true);
            while(!success){
                System.out.println("Failed, Try Another Spot");

                if (isBenchmarking || players.get(i).tag.equals("bot")) {
                    spotNum = Benchmarking.getInitialSettlement(players.get(i), this);
                } else {
                    spotNum = input.nextInt();
                }
                success = CreateSettlement(players.get(i), spots.get(spotNum), true);
            }
            System.out.println("Player " + players.get(i).id + " has picked spot: " + spotNum );
            //give the resources to the players.
            for (int j = 0; j < spots.get(spotNum).adjacentHexes.size(); j++) {
                if(spots.get(spotNum).adjacentHexes.get(j).type == Hex_Type.Desert) continue;
                Resource_Type res = Resources.produce(spots.get(spotNum).adjacentHexes.get(j).type);
                players.get(i).resourcesAtHand.add(res);
            }
            System.out.println("Player " + players.get(i).id + " pick road.");
            //here the Player chooses the raod`

            int spotRoad;
            if (isBenchmarking || players.get(i).tag.equals("bot")) {
                spotRoad = Benchmarking.getRoadPlacement(players.get(i), spotNum, this);
            } else {
                spotRoad = input.nextInt();
            }

            success = CreateRoad(players.get(i), spots.get(spotRoad), spots.get(spotNum), true);
            while(!success){
                System.out.println("Failed, Try Another road");

                if (isBenchmarking || players.get(i).tag.equals("bot")) {
                    spotRoad = Benchmarking.getRoadPlacement(players.get(i), spotNum, this);
                } else {
                    spotRoad = input.nextInt();
                }
                success = CreateRoad(players.get(i), spots.get(spotRoad), spots.get(spotNum), true);
            }
        }
        isInitialSettlementPhase = false;
        System.out.println("End of the Initial Settlement Phase");
    }
    public void GamePhase() {
        round = 0;
        currentPlayer = players.get(round % players.size());
        while (!CheckWin(false)){
            devCardUsedThisRound = false;
            System.out.println("Round " + round + ", Player " + currentPlayer.id + "'s turn");
//            if(UseDevelopmentCard(currentPlayer, DevelopmentCard_Type.Knight, false)){
//                System.out.println("Do u want to use knight (0-no/1-yes)?");
//                int num = input.nextInt() % 2;
//                if(num == 1){
//                    UseDevelopmentCard(currentPlayer, DevelopmentCard_Type.Knight, true);
//                }
//            }
            DiceRoll(currentPlayer);
            //here the player makes the move;
            currentPlayer.MakeMove(this);
            if(round % 10 == 0){
                PrintBoard();
                CheckWin(true);
            }
            if(round >= 1000){
                CheckWin(true);
                break;
            }
            round++;
            currentPlayer = players.get(round % players.size());
        }
        PrintBoard();
        CheckWin(true);
        System.out.println("GAME ENDS");
    }
    public boolean CheckWin(boolean print){
        for (Player player: players) {

            if(largestArmy != null && player.id == largestArmy.id) player.VP += 2;
            if(longestRoad != null && player.id == longestRoad.id) player.VP += 2;
            for (int i = 0; i < player.settlements.size(); i++) {
                if (player.settlements.get(i).isCity) player.VP += 2;
                else player.VP += 1;
            }
            for (int i = 0; i < player.devCards.size(); i++) {
                if (player.devCards.get(i).type == DevelopmentCard_Type.VictoryPoint) player.VP += 1;
            }
            if(print){
                System.out.println("Player " + player.id + " has " + player.VP + " VP");
            }
            if(player.VP >= 15) {
                player.hasWon = true;
                return true;
            }
        }
        return false;
    }
    public void DiceRoll(Player player){
        int diceRoll = dice.Roll();
        System.out.println("A " + diceRoll + " is rolled");
        if(diceRoll == 7) {
            for (Player tempPlayer: players) {
                if(tempPlayer.resourcesAtHand.size() > 9){
                    System.out.println("!!!You HAVE TO DISCARD!!!");
                    System.out.println("Here is your current resources, discard " + tempPlayer.resourcesAtHand.size() / 2);
                    System.out.println("Enter the resources you want to discard: [SB(Wh)(Wd)O]");

                    int discardNum = (tempPlayer.resourcesAtHand.size() / 2);
                    for (int i = 0; i < discardNum; i++) {
                        if (isBenchmarking || players.get(i).tag.equals("bot")) {
                            player.discardCards(discardNum);
                        } else {
                            int res = input.nextInt();
                            player.resourcesAtHand.remove(Resources.resourcesList[res]);
                        }
                    }
                }
            }
            System.out.println("Which spot u want to block: [SB(Wh)(Wd)O] [number]");

            Resource_Type type = null;
            int num = 0;

            if (isBenchmarking || player.tag.equals("bot")) {
                int[] typeNumPair = player.placeKnight(this);
                type = Resources.resourcesList[typeNumPair[0]];
                num = typeNumPair[1];

            } else {
                type = Resources.resourcesList[input.nextInt()];
                num = input.nextInt();
            }

            for(Hex hex: hexes)  {
                if(Resources.produce(hex.type) == type && hex.diceNum == num) {
                    if(hex.isBlocked){
                        System.out.println("Failed, must change a place");
                        break;
                    }
                    if(blockedHex != null) blockedHex.isBlocked = false;
                    hex.isBlocked = true;
                    blockedHex = hex;
                    Player playerToStealFrom = null;
                    for (int i = 0; i < players.size(); i++) {
                        if(player.id != players.get(i).id){
                            playerToStealFrom = players.get(i);
                        }
                    }
                    if(playerToStealFrom != null && !playerToStealFrom.resourcesAtHand.isEmpty()){
                        int resourceToSteal = random.nextInt(playerToStealFrom.resourcesAtHand.size());
                        player.resourcesAtHand.add( playerToStealFrom.resourcesAtHand.get(resourceToSteal));
                        playerToStealFrom.resourcesAtHand.remove(resourceToSteal);
                    }
                }
            }
        }
        for (int i = 0; i < hexes.size(); i++) {
            if(hexes.get(i).diceNum == diceRoll){
                if(hexes.get(i).isBlocked) continue;
                for (Spot spot : hexes.get(i).adjacentSpots) {
                    if(spot.hasSettlement){
                        if(spot.settlement.isCity){
                            for (int j = 0; j < 2; j++) {
                                spot.settlement.playerOwned.resourcesAtHand.add(Resources.produce(hexes.get(i).type));
                                System.out.print("Player " + spot.settlement.playerOwned.id + " gets " + hexes.get(i).type + "; ");
                            }
                        }else{
                            spot.settlement.playerOwned.resourcesAtHand.add(Resources.produce(hexes.get(i).type));
                            System.out.print("Player " + spot.settlement.playerOwned.id + " gets " + hexes.get(i).type + "; ");
                        }
                    }
                }
            }
        }
        System.out.println();
    }
    public boolean UpgradeToCity(Player player, Spot spot, boolean actuallyUpgrade){
        if(!spot.hasSettlement) return false;
        if(!player.canBuy(Action_Type.UpGradeCity)) return false;
        if(spot.settlement.isCity) return false;
        int cityNum = 0;
        for (int i = 0; i < player.settlements.size(); i++) {
            if(player.settlements.get(i).isCity) cityNum ++;
        }
        if(cityNum >= 4) return false;
        if(actuallyUpgrade){
            player.Buy(Action_Type.UpGradeCity);
            spot.settlement.isCity = true;
        }
        return true;
    }
    public boolean UseDevelopmentCard(Player player, DevelopmentCard_Type type, boolean actuallyUse){
        boolean hasCard = false;
        DevelopmentCard cardToUse = null;
        for (DevelopmentCard card : player.devCards) {
            if(card.type == type){ hasCard = true; cardToUse = card; break;}
        }
        if(!hasCard) return false;
        return DevelopmentCard.Use(player, cardToUse, this, actuallyUse);
    }
    public boolean BuyDevelopmentCard(Player player, int round, boolean actuallyBuy){
        if(DevelopmentCard.developmentCardsDeck.size() <= 0) return false;
        if(!player.canBuy(Action_Type.BuyDevelopmentCard)) return false;
        if(actuallyBuy){
            DevelopmentCard_Type type = DevelopmentCard.developmentCardsDeck.remove(0);
            DevelopmentCard devCard = new DevelopmentCard(type, round);
            player.Buy(Action_Type.BuyDevelopmentCard);
            player.devCards.add(devCard);
        }
        return true;
    }
    public boolean CreateRoad(Player player, Spot spot1, Spot spot2, boolean actuallyCreate){
        if(spot1.id == spot2.id) return false;
        if(player.roads.size() >= 15) return false;
        if(isInitialSettlementPhase){
            if(!spot1.adjacentSpots.contains(spot2)) return false;
            if(actuallyCreate){
                Road road = new Road(player, spot1, spot2, roads.size());
                player.roads.add(road);
                roads.add(road);
                spot1.spotsConnectedByRoad.put(spot2, player.id);
                spot2.spotsConnectedByRoad.put(spot1, player.id);
                return true;
            }
        }else{
            //check if player can buy
            if(!player.canBuy(Action_Type.BuildRoad)) return false;
            //check if this road already exist
            for (Spot spot: spot1.spotsConnectedByRoad.keySet()) {
                if(spot.id == spot2.id){ return false;}
            }
            //check if the road is connected to a road or a settlement
            int roadConnected = 0;
            for (Spot spot: spot1.spotsConnectedByRoad.keySet()) {
                if(spot1.spotsConnectedByRoad.get(spot) == player.id){
                    if(spot1.hasSettlement && spot1.settlement.playerOwned.id != player.id) continue;
                    roadConnected++;
                }
            }
            for (Spot spot: spot2.spotsConnectedByRoad.keySet()) {
                if(spot2.spotsConnectedByRoad.get(spot) == player.id){
                    if(spot2.hasSettlement && spot2.settlement.playerOwned.id != player.id) continue;
                    roadConnected++;
                }
            }
            if(roadConnected == 0){return false;}
            //create the road
            if(actuallyCreate){
                player.Buy(Action_Type.BuildRoad);
                Road road = new Road(player, spot1, spot2, roads.size());
                roads.add(road);
                player.roads.add(road);
                spot1.spotsConnectedByRoad.put(spot2, player.id);
                spot2.spotsConnectedByRoad.put(spot1, player.id);
                //UpdateLongestRoad
                int longestConnection = 0;
                for (Spot spot : spots) {
                    boolean[] visited = new boolean[spots.size()];
                    for (int i = 0; i < spots.size(); i++) {
                        visited[i] = false;
                    }

                    visited[spot.id] = true;
                    int max = 0;
                    int max2 = 0;
                    for (Spot next: spot.spotsConnectedByRoad.keySet()) {
                        int length = FindLongestRoad(next, visited, player.id);
                        if(length > max){
                            if(max > max2) max2 = max;
                            max = length;
                        }else if(length > max2){
                            max2 = length;
                        }
                    }
                    longestConnection = max + max2;
//                    System.out.println("You currently have longest connection of: " + longestConnection);
                    if(longestConnection > longestRoadAmount){
                        longestRoadAmount = longestConnection;
                        longestRoad = player;
                        System.out.println("New Longest Road Record! The current best is " + longestRoadAmount + " roads.");
                    }
                }
                return true;
            }
            return true;
        }
        return true;
    }
    public int FindLongestRoad(Spot spot, boolean[] visited, int playerID){

        visited[spot.id] = true;
        for (Spot next: spot.spotsConnectedByRoad.keySet()) {
            if(playerID == spot.spotsConnectedByRoad.get(next)){
                if(spot.hasSettlement && spot.settlement.playerOwned.id != playerID){
                    return 0;
                }
                if(!visited[next.id]){
                    return 1 + FindLongestRoad(next, visited, playerID);
                }
            }
        }
        return 1;
    }
    //if the player just wants to check if they can build a settlement, put false for actuallyCreate
    public boolean CreateSettlement(Player player, Spot spot, boolean actuallyCreate){
        if(isInitialSettlementPhase){
            //check adj spots
            if(spot.hasSettlement) return false;
            for (int i = 0; i < spot.adjacentSpots.size(); i++) {
                if(spot.adjacentSpots.get(i).hasSettlement) return false;
            }
            if(actuallyCreate){
                Settlement settlement = new Settlement(spot, player);
                player.settlements.add(settlement);
                spot.hasSettlement = true;
                spot.settlement = settlement;
                if(spot.hasPort){
                    player.ports.add(spot.port);
                }
            }
            return true;
        }else{
            //check if road connects
            int count = 0;
            for (Spot next: spot.spotsConnectedByRoad.keySet()) {
                if(spot.spotsConnectedByRoad.get(next) == player.id) count++;
            }
            if(count == 0) return false;
            if(spot.hasSettlement) return false;
            //check if resource allows
            if(!player.canBuy(Action_Type.BuildSettlement)) return false;
            //check adj spots
            for (int i = 0; i < spot.adjacentSpots.size(); i++) {
                if(spot.adjacentSpots.get(i).hasSettlement) return false;
            }
            //check if the player still has a settlement
            int settlementNum = 0;
            for (int i = 0; i < player.settlements.size(); i++) {
                if(!player.settlements.get(i).isCity) settlementNum += 1;
            }
            if(settlementNum == 5) return false;

            //create the settlement
            if(actuallyCreate){
                Settlement settlement = new Settlement(spot, player);
                player.settlements.add(settlement);
                spot.hasSettlement = true;
                spot.settlement = settlement;
                player.Buy(Action_Type.BuildSettlement);
            }
            return true;
        }
    }
    public void CreateRandomBoard(){
        //generate Hexes
        for (int i = 0; i < 5; i++) {
            int num = 4; if(i > 2) num = 3;
            for (int j = 0; j < num; j++) {
                hexes.add(new Hex(Hex.HexTypeArray[i]));
            }
        }
        hexes.add(new Hex(Hex_Type.Desert));
        Collections.shuffle(hexes);
        for (int i = 0; i < hexes.size(); i++) {
            hexes.get(i).id = i;
        }
        int hexAlignmentIndex = 0;
        int offset = 3;
        for (int i = 0; i < hexes.size(); i++) {
            if(i != Hex.Hex_Alignment[hexAlignmentIndex] - 1){
                if(i + 1 < 19){
                    hexes.get(i).adjacentHexes.add(hexes.get(i + 1));
                    hexes.get(i + 1).adjacentHexes.add(hexes.get(i));
                }
            }
            if(i < 7){
                //left down
                hexes.get(i).adjacentHexes.add(hexes.get(i + offset));
                hexes.get(i + offset).adjacentHexes.add(hexes.get(i));
//inverse
                hexes.get(19 - i - 1).adjacentHexes.add(hexes.get(19 - i - offset - 1));
                hexes.get(19 - i - offset - 1).adjacentHexes.add(hexes.get(19 - i - 1));
                //right down
                hexes.get(i).adjacentHexes.add(hexes.get(i + offset + 1));
                hexes.get(i + offset + 1).adjacentHexes.add(hexes.get(i));
//inverse
                hexes.get(19 - i - 1).adjacentHexes.add(hexes.get(19 - i - offset - 2));
                hexes.get(19 - i - offset - 2).adjacentHexes.add(hexes.get(19 - i - 1));

            }
            if(i == Hex.Hex_Alignment[hexAlignmentIndex] - 1 && hexAlignmentIndex <4){
                hexAlignmentIndex++;
                offset = Hex.Hex_Alignment[hexAlignmentIndex] - Hex.Hex_Alignment[hexAlignmentIndex - 1];
            }
        }
        //generate the corresponding dice rolls
        List<Integer> nums = new ArrayList<Integer>();
        nums.add(2); nums.add(12); nums.add(0);
        for (int i = 3; i < 12; i++) {
            if(i == 7) continue;
            nums.add(i);
            nums.add(i);
        }

        Collections.shuffle(nums);
        boolean reshuffle = true;
        while(reshuffle) {
            reshuffle = false;
            for (int i = 0; i < nums.size(); i++) {
                if(nums.get(i) == 6 || nums.get(i) == 8){
                    for (int j = 0; j < hexes.get(i).adjacentHexes.size(); j++) {
                        int adjID = hexes.get(i).adjacentHexes.get(j).id;
                        if(
                                nums.get(adjID) == 6 ||
                                        nums.get(adjID) == 8
                        ){
                            reshuffle = true; break;
                        }
                    }
                }
            }
            if(reshuffle){
                Collections.shuffle(nums);
            }
        }

        int newdesertPlace = 0;
        int olddesertPlace = 0;
        for (int i = 0; i < hexes.size(); i++) {
            hexes.get(i).diceNum = nums.get(i);
            if(hexes.get(i).diceNum == 0) newdesertPlace = hexes.get(i).id;
            if(hexes.get(i).type == Hex_Type.Desert) olddesertPlace = hexes.get(i).id;
        }
        if(newdesertPlace != olddesertPlace){
            hexes.get(olddesertPlace).type = hexes.get(newdesertPlace).type;
            hexes.get(newdesertPlace).type = Hex_Type.Desert;
        }

        //generate the spots
        for (int i = 0; i < 54; i++) {
            Spot spot = new Spot(i);
            spots.add(spot);
        }
        //adding every spot to its corresponding hex & hex to its corresponding spot
        //counting from the top & inverse
        hexAlignmentIndex = 0;
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 3; j++) {
                hexes.get(i).adjacentSpots.add(spots.get(i*2 + j + hexAlignmentIndex));
                spots.get(i*2 + j + hexAlignmentIndex).adjacentHexes.add(hexes.get(i));

                hexes.get(18 - i).adjacentSpots.add(spots.get(53 - (i*2 + j + hexAlignmentIndex)));
                spots.get(53 - (i*2 + j + hexAlignmentIndex)).adjacentHexes.add(hexes.get(18-i));
            }
            if(i == Hex.Hex_Alignment[hexAlignmentIndex] - 1) hexAlignmentIndex ++;
        }
        //counting from the bottom & inverse
        hexAlignmentIndex = 0;
        for (int i = 0; i < 7; i++) {
            for (int j = 8; j < 11; j++) {
                hexes.get(i).adjacentSpots.add(spots.get(i*2 + j + hexAlignmentIndex*3));
                spots.get(i*2 + j + hexAlignmentIndex*3).adjacentHexes.add(hexes.get(i));

                hexes.get(18-i).adjacentSpots.add(spots.get(53-(i*2 + j + hexAlignmentIndex*3)));
                spots.get(53 - (i*2 + j + hexAlignmentIndex*3)).adjacentHexes.add(hexes.get(18 - i));
            }
            if(i == Hex.Hex_Alignment[hexAlignmentIndex] - 1) hexAlignmentIndex ++;
        }
        //making every spot aware of its adjacent spot
        //add left/right
        int boundIndex = 0;
        int leftBound = 0;
        int rightBound = Spot.Spot_Alignment[boundIndex];
        for (int i = 0; i < 54; i++) {
            if(i == rightBound && i != 53){
                boundIndex++;
                leftBound = rightBound;
                rightBound = Spot.Spot_Alignment[boundIndex];
            }
            if(i - 1 >= leftBound)
                spots.get(i).adjacentSpots.add(spots.get(i - 1));
            if(i + 1 < rightBound)
                spots.get(i).adjacentSpots.add(spots.get(i + 1));
        }
        //add top/down
        offset = 8;
        for (int i = 0; i < 54; i+=2) {
            if(i + offset < 54){
                spots.get(i).adjacentSpots.add(spots.get(i + offset));
                spots.get(i + offset).adjacentSpots.add(spots.get(i));
            }
            if(i == 6) { i--; offset = 10; }
            else if(i == 15){ i--; offset = 11; }
            else if(i == 26){ offset = 10; }
            else if(i == 36){ i ++; offset = 8; }
        }
        //port
        Collections.shuffle(Port.portTypeArrayList);
        for (int i = 0; i < 9; i++) {
            Port port = new Port(Port.portTypeArrayList.get(i));
            spots.get( Port.portLocationArray[i*2] - 1 ).hasPort = true;
            spots.get( Port.portLocationArray[i*2 + 1] - 1 ).hasPort = true;
            spots.get(Port.portLocationArray[i*2] - 1).port = port;
            spots.get(Port.portLocationArray[i*2 + 1] - 1).port = port;
        }
        //devCards
        for (int i = 0; i < 5; i++) {
            DevelopmentCard.developmentCardsDeck.add(DevelopmentCard_Type.VictoryPoint);
        }
        for (int i = 0; i < 14; i++) {
            DevelopmentCard.developmentCardsDeck.add(DevelopmentCard_Type.Knight);
        }
        for (int i = 0; i < 2; i++) {
            DevelopmentCard.developmentCardsDeck.add(DevelopmentCard_Type.RoadBuilder);
        }
        for (int i = 0; i < 2; i++) {
            DevelopmentCard.developmentCardsDeck.add(DevelopmentCard_Type.Monopoly);
        }
        for (int i = 0; i < 2; i++) {
            DevelopmentCard.developmentCardsDeck.add(DevelopmentCard_Type.YearOfPlenty);
        }
        Collections.shuffle(DevelopmentCard.developmentCardsDeck);
    }
    public void PrintBoard(){
        for (int i = 0; i < 19; i++) {
//            System.out.println("Hex " + hexes.get(i).id + " has spots: " +
//                    hexes.get(i).showAdjacentSpots());
        }
        for (Spot spot : spots) {
//            if (spot.hasPort) {
//                System.out.print("Spot " + spot.id + " has Port " + spot.port.getType());
//                System.out.println(" and hexes " + spot.printAdjacentHexes());
//            }
        }
        for (int i = 0; i < spots.size(); i++) {
//            System.out.println("Spot " + i + " is next to: " + spots.get(i).showAdjacentSpots());
        }
        for (int i = 0; i < 19; i++) {
//            System.out.println("Hex " + hexes.get(i).id + " has hexes: " +
//                    hexes.get(i).showAdjacentHexes());
        }
        for (int i = 0; i < 19; i++) {
//            System.out.println("Hex " + hexes.get(i).id + " has hexes: " +
//                    hexes.get(i).showAdjacentHexes());
        }
        //print the resource hexes
        int index = 0;
        System.out.print("            ");
        for (int i = 0; i < hexes.size(); i++) {
            System.out.print(hexes.get(i).type + "_" + hexes.get(i).diceNum + "   ");
            output.append(hexes.get(i).type).append("_").append(hexes.get(i).diceNum).append(",");

            if(i == Hex.Hex_Alignment[index] - 1){
                System.out.println();
                int spaceNum = 0;
                if(index == 0 || index == 2) spaceNum = 7;
                else if(index == 3) spaceNum = 12;
                for (int j = 0; j < spaceNum; j++) {
                    System.out.print(" ");
                }
                index ++;
            }
        }
        //print the settlement/roads
        index = 0;
        System.out.print("      ");
        String downwardRoad = "";
        for (int i = 0; i < spots.size(); i++) {
            if(spots.get(i).hasSettlement){
                if(spots.get(i).settlement.isCity)
                    System.out.print("c" + spots.get(i).settlement.playerOwned.id);
                else
                    System.out.print("s" + spots.get(i).settlement.playerOwned.id);
            }else{
                System.out.print("o ");
            }
            if(i < spots.size() - 1 && spots.get(i).spotsConnectedByRoad.get(spots.get(i+1)) != null){
                //if(spots.get(i).spotsConnectedByRoad.get(spots.get(i+1)) == 2)
                    System.out.print("-");
                //else System.out.print(" ");
            }else{
                System.out.print(" ");
            }
            boolean hasDownwardRoad = false;
            for (Spot spot: spots.get(i).spotsConnectedByRoad.keySet()) {
                if(spot.id > spots.get(i).id + 1){
                    //if(spots.get(i).spotsConnectedByRoad.get(spots.get(i+1)) != null && (spots.get(i).spotsConnectedByRoad.get(spots.get(i+1)) == 2)){
                        hasDownwardRoad = true;
                        break;
                    //}
                }
            }
            if(hasDownwardRoad)downwardRoad += "|  ";
            else downwardRoad += "   ";

            if(i == Spot.Spot_Alignment[index] - 1){
                System.out.println();
                int spaceNum = 0;

                if(index == 0 || index == 4) spaceNum = 3;
                else if(index == 1 ) spaceNum = 3;
                for (int j = 0; j < spaceNum; j++) {
                    System.out.print(" ");
                }
                if(index == 0) System.out.print("   ");
                System.out.print(downwardRoad);
                downwardRoad = "";
                System.out.println();

                spaceNum = 0;
                if(index == 0 || index == 3) spaceNum = 3;
                else if(index == 4) spaceNum = 6;
                for (int j = 0; j < spaceNum; j++) {
                    System.out.print(" ");
                }
                index ++;
            }
        }
        //print the player information
        for (Player player : players) {
            System.out.print("Player : " + player.id + "'s resources: ");
            for (int i = 0; i < player.resourcesAtHand.size(); i++) {
                System.out.print(player.resourcesAtHand.get(i) + " ");
            }
            System.out.println();

            System.out.print("Player : " + player.id + "'s development cards: ");
            for (int i = 0; i < player.devCards.size(); i++) {
                System.out.print(player.devCards.get(i).type + " ");
            }
            System.out.println();
        }
        System.out.println(output);
    }
}
