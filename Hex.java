enum Hex_Type{
    Wood,
    Brick,
    Sheep,
    Wheat,
    Ore,
    Desert
}

public class Hex {

    public static Hex_Type[] HexTypeArray =
            {Hex_Type.Wood, Hex_Type.Sheep, Hex_Type.Wheat, Hex_Type.Brick, Hex_Type.Ore};

    public boolean isBlocked = false;
    public Hex_Type type;
    public int diceNum;
    public Hex(Hex_Type type){
        this.type = type;
    }
}

