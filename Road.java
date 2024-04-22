public class Road {
    public int playerId; //corresponds to the player ID that owns this road.
    public int id;// the unique ID of this road
    //update the spots of the connection
    public Spot spot1;
    public Spot spot2;
    public Road(Player player, Spot spot1, Spot spot2, int id){
        this.playerId = player.id;
        spot1.spotsConnectedByRoad.put(spot2, player.id);
        spot2.spotsConnectedByRoad.put(spot1, player.id);
        this.spot1 = spot1;
        this.spot2 = spot2;
    }
}
