import java.util.*;

public class Port {
    public static List<Port_Type> portTypeArrayList = new ArrayList<>
            (Arrays.asList( Port_Type.Wood, Port_Type.Brick, Port_Type.Sheep, Port_Type.Wheat,
                    Port_Type.Ore, Port_Type.General, Port_Type.General, Port_Type.General, Port_Type.General));

    public static int[] portLocationArray = {
            1, 2, 4, 5, 15, 16, 27, 38, 46, 47, 51, 52, 49, 48, 29, 39, 8, 17
    };

    public Port_Type type;

    public Port(Port_Type type) {
        this.type = type;
    }

    // public void setType(Port_Type type) {
    //     this.type = type;
    // }

    //can get position with method
}
