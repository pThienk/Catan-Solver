public class Settlement {
    public boolean isCity = false;
    public Spot spotLocated = null;
    Player playerOwned = null;

    public Settlement(Spot spot, Player player){
        spotLocated = spot;
        spot.hasSettlement = true;
        spot.settlement = this;
        playerOwned = player;
    }
}
