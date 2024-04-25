public class Action {
    public Spot thisSpot = null;
    public Spot potentialNextSpot = null;

    public Action(Spot thisSpot, Spot potentialNextSpot, DevelopmentCard_Type potentialDevCardToUse, Action_Type actionType) {
        this.thisSpot = thisSpot;
        this.potentialNextSpot = potentialNextSpot;
        this.potentialDevCardToUse = potentialDevCardToUse;
        this.actionType = actionType;
    }

    public DevelopmentCard_Type potentialDevCardToUse = null;
    public Action_Type actionType;

    public String toString(){
        String a = "";
        if(actionType == Action_Type.Pass){
            a = "Pass";
        }else if(actionType == Action_Type.BuildRoad){
            a = "Build Road between Spots: " + thisSpot.id + " " + potentialNextSpot.id;
        }else if(actionType == Action_Type.BuildSettlement){
            a = "Build Settlement on Spot: " + thisSpot.id;
        }else if(actionType == Action_Type.BuyDevelopmentCard){
            a = "Buy a development card";
        }else if(actionType == Action_Type.UseDevelopmentCard){
            a = "Use dev card: " + potentialDevCardToUse;
        }else if(actionType == Action_Type.UpGradeCity){
            a = "Upgrade City on spot " + thisSpot.id;
        }else if(actionType == Action_Type.Trade){
            a = "Trade";
        }
        return a;
    }
}
