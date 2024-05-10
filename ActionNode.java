import java.util.ArrayList;
import java.util.List;

public class ActionNode {

    public Action nodeAction;
    public List<StateNode> children;
    public double nodeValue;
    public List<Double> probs;

    public ActionNode(Action action) {
        this.nodeAction = action;
        children = new ArrayList<>();
        probs = new ArrayList<>();
    }
}
