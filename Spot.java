import java.util.*;

public class Spot {
    public int id;
    public boolean hasSettlement = false;
    public Settlement settlement= null;
    List<Hex> adjacentHexes = new ArrayList<Hex>();
    public boolean hasPort = false;
    public Port port = null;

    public Spot(int id){
        this.id = id;
    }
}
