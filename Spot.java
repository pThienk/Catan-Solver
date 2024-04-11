import java.util.*;

public class Spot {
    public int id;
    public boolean hasSettlement = false;
    public Settlement settlement= null;
    List<Hex> adjacentHexes = new ArrayList<Hex>();
    List<Spot> adjacentSpots = new ArrayList<Spot>();
    public static int[] Spot_Alignment  = {7, 16, 27, 38, 47, 54};
    public boolean hasPort = false;
    public Port port = null;

    public Spot(int id){
        this.id = id;
    }

    public String printAdjacentHexes(){
        String a = "";
        for (int i = 0; i < adjacentHexes.size(); i++) {
            a += adjacentHexes.get(i).type + " " + adjacentHexes.get(i).diceNum + ", ";
        }
        return a;
    }
    public String showAdjacentSpots(){
        String a = "";
        for (int i = 0; i < adjacentSpots.size(); i++) {
            a += adjacentSpots.get(i).id + " ";
        }
        return a;
    }
}
