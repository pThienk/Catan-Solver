import java.util.List;

public class ActionNode {

    public Action nodeAction;
    public List<ActionNode> children;
    public double nodeValue;
    public double[] probs;

    public ActionNode(Action action) {
        this.nodeAction = action;
    }
}
