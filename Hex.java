import java.util.ArrayList;

public class Hex {

    public static Hex_Type[] HexTypeArray =
            {Hex_Type.Wood, Hex_Type.Sheep, Hex_Type.Wheat, Hex_Type.Brick, Hex_Type.Ore};
    public static int[] Hex_Alignment  = {3, 7, 12, 16, 19};

    public int id;
    public ArrayList<Spot> adjacentSpots = new ArrayList<Spot>();
    public boolean isBlocked = false;
    public Hex_Type type;
    public int diceNum;
    public Hex(Hex_Type type, int id){
        this.type = type;
        this.id = id;
    }

    public String showAdjacentSpots(){
        String a = "";
        for (int i = 0; i < adjacentSpots.size(); i++) {
            a += adjacentSpots.get(i).id + " ";
        }
        return a;
    }
}

