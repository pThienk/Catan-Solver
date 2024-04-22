import java.util.*;

public class Spot {
    public int id;
    public boolean hasSettlement = false;
    public Settlement settlement= null;
    List<Hex> adjacentHexes = new ArrayList<Hex>();
    List<Spot> adjacentSpots = new ArrayList<Spot>();

    //the spots that connect to this spot by roads, the integer correspond to the playerID that owns this road
    Map<Spot, Integer> spotsConnectedByRoad = new HashMap<>();
    public static int[] Spot_Alignment  = {7, 16, 27, 38, 47, 54};
    public boolean hasPort = false;
    public Port port = null;

    public Spot(int id){
        this.id = id;
    }

    public String printAdjacentHexes(){
        StringBuilder a = new StringBuilder();
        for (Hex adjacentHex : adjacentHexes) {
            a.append(adjacentHex.type).append(" ").append(adjacentHex.diceNum).append(", ");
        }
        return a.toString();
    }
    public String showAdjacentSpots(){
        StringBuilder a = new StringBuilder();
        for (Spot adjacentSpot : adjacentSpots) {
            a.append(adjacentSpot.id).append(" ");
        }
        return a.toString();
    }
}
