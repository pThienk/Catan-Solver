import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class DevelopmentCard {
    public static ArrayList<DevelopmentCard_Type> developmentCardsDeck = new ArrayList<>();
    public DevelopmentCard_Type type;
    public int roundBought = 0;
    public DevelopmentCard(DevelopmentCard_Type type, int roundBought){
        this.type = type;
        this.roundBought = roundBought;
    }

    public static boolean Use(Player player, DevelopmentCard card, Board board, boolean actuallyUse){
        if(card.roundBought <= board.round) return false;
        if(board.devCardUsedThisRound) return false;
        Scanner in = new Scanner(System.in);
        if(actuallyUse){
            if(card.type == DevelopmentCard_Type.Knight){
                board.devCardUsedThisRound = true;
                player.knightUsed ++;
                if(player.knightUsed > board.mostKnightAmount){
                    board.largestArmy = player;
                    board.mostKnightAmount = player.knightUsed;
                }
                System.out.println("Which spot u want to block: [SB(Wh)(Wd)O] [number]");
                Resource_Type type = Resources.resourcesList[in.nextInt()];
                int num = in.nextInt();
                for(Hex hex: board.hexes){
                    if(Resources.produce(hex.type) == type && hex.diceNum == num){
                        hex.isBlocked = true;
                        Player playerToStealFrom = null;
                        for (int i = 0; i < board.players.size(); i++) {
                            if(player.id != board.players.get(i).id){
                                playerToStealFrom = board.players.get(i);
                            }
                        }
                        if(playerToStealFrom != null && !playerToStealFrom.resourcesAtHand.isEmpty()){
                            int resourceToSteal = board.random.nextInt(playerToStealFrom.resourcesAtHand.size());
                            player.resourcesAtHand.add( playerToStealFrom.resourcesAtHand.get(resourceToSteal));
                            playerToStealFrom.resourcesAtHand.remove(resourceToSteal);
                        }
                    }
                }
                return true;
            }else if(card.type == DevelopmentCard_Type.Monopoly){
                board.devCardUsedThisRound = true;
                int totalCount = 0;
                Resource_Type type = null;
                for (Player plyr : board.players) {
                    int count = 0;
                    System.out.println("Enter the resource you want [SB(Wh)(Wd)O]");
                    int a = in.nextInt() % 5;
                    type = Resources.resourcesList[a];
                    if(plyr.id == player.id) continue;
                    for (int i = 0; i < plyr.resourcesAtHand.size(); i++) {
                        if(plyr.resourcesAtHand.get(i) == type){
                            count ++;
                        }
                    }
                    totalCount += count;
                    for (int i = 0; i < count; i++) {
                        plyr.resourcesAtHand.remove(type);
                    }
                }
                for (int i = 0; i < totalCount; i++) {
                    player.resourcesAtHand.add(type);
                }
                return true;
            }
            else if(card.type == DevelopmentCard_Type.RoadBuilder){
                board.devCardUsedThisRound = true;
                for (int i = 0; i < 2; i++) {
                    System.out.println("Enter the two spots you want the road: ");
                    int spot1 = in.nextInt() % 54;
                    int spot2 = in.nextInt() % 54;
                    boolean success =  board.CreateRoad(player, board.spots.get(spot1), board.spots.get(spot2), true);
                    while(!success){
                        System.out.println("Failed, enter two new ones: ");
                        spot1 = in.nextInt() % 54;
                        spot2 = in.nextInt() % 54;
                        success =  board.CreateRoad(player, board.spots.get(spot1), board.spots.get(spot2), true);
                    }
                }
                return true;
            }
            else if(card.type == DevelopmentCard_Type.YearOfPlenty){
                board.devCardUsedThisRound = true;
                System.out.println("Enter the 2 resource you want [SB(Wh)(Wd)O]");
                int a = in.nextInt() % 5;
                int b = in.nextInt() % 5;
                player.resourcesAtHand.add(Resources.resourcesList[a]);
                player.resourcesAtHand.add(Resources.resourcesList[b]);
                return true;
            }
            else return false;
        }
        return true;
    }
}
