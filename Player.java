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
        Collections.copy(hand, resourcesAtHand);
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
}
