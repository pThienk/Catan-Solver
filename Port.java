public class Port {
    public static final Port_Type[] portTypeArray = {
            Port_Type.Wood, Port_Type.Brick, Port_Type.Sheep, Port_Type.Wheat, Port_Type.Ore,
            Port_Type.General, Port_Type.General, Port_Type.General, Port_Type.General
    };

    public static final int[] portLocationArray = {
            1, 2, 4, 5, 15, 16, 27, 38, 46, 47, 51, 52, 49, 48, 29, 39, 8, 17
    };

    private Port_Type type;

    public Port(Port_Type type) {
        this.type = type;
    }

    public Port_Type getType() {
        return type;
    }

    // public void setType(Port_Type type) {
    //     this.type = type;
    // }

    //can get position with method
}
