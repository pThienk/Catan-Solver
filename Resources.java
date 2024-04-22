import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Resources {
    public static Map<Action_Type, List<Resource_Type>> resourceCost = new HashMap<Action_Type, List<Resource_Type>>() {{
        put(Action_Type.BuildSettlement, Arrays.asList(Resource_Type.Sheep, Resource_Type.Brick, Resource_Type.Wheat, Resource_Type.Wood));
        put(Action_Type.BuyDevelopmentCard, Arrays.asList(Resource_Type.Sheep, Resource_Type.Wheat, Resource_Type.Ore));
        put(Action_Type.UpGradeCity, Arrays.asList(Resource_Type.Wheat, Resource_Type.Wheat, Resource_Type.Ore, Resource_Type.Ore, Resource_Type.Ore));
        put(Action_Type.BuildRoad, Arrays.asList(Resource_Type.Brick, Resource_Type.Wood));
    }};

    public static Resource_Type[] resourcesList = {Resource_Type.Sheep, Resource_Type.Brick, Resource_Type.Wheat, Resource_Type.Wood, Resource_Type.Ore};

    public static Resource_Type produce(Hex_Type type){
        if(type == Hex_Type.Ore) return Resource_Type.Ore;
        else if(type == Hex_Type.Wheat) return Resource_Type.Wheat;
        else if(type == Hex_Type.Sheep) return Resource_Type.Sheep;
        else if(type == Hex_Type.Wood) return Resource_Type.Wood;
        else if(type == Hex_Type.Brick) return Resource_Type.Brick;
        else return null;
    }

}
