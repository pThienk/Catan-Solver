import java.util.ArrayList;
import java.util.List;

public class StateNode {

    public int stateId;
    public List<ActionNode> children;
    public double nodeValue;
    public int playerId;

    public StateNode(int playerId) {
        this.playerId = playerId;
        children = new ArrayList<>();
    }
}
