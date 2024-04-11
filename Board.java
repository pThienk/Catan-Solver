import java.util.*;

public class Board {
    public List<Hex> hexes = new ArrayList<Hex>();
    public List<Spot> spots = new ArrayList<Spot>();
    public List<Settlement> settlements = new ArrayList<Settlement>();
    public List<Road> roads = new ArrayList<Road>();
    public List<Player> players = new ArrayList<Player>();

    public Spot blockedSpot;

    /**
     * Generates a Random Board
     */
    public Board(){
        //generate Hexes
        for (int i = 0; i < 5; i++) {
            int num = 4; if(i > 2) num = 3;
            for (int j = 0; j < num; j++) {
                hexes.add(new Hex(Hex.HexTypeArray[i]));
            }
        }
        hexes.add(new Hex(Hex_Type.Desert));
        //generate the corresponding dice rolls
        List<Integer> nums = new ArrayList<Integer>();
        nums.add(2); nums.add(12);
        for (int i = 3; i < 12; i++) {
            if(i == 7) continue;
            nums.add(i);
            nums.add(i);
        }
        Collections.shuffle(nums);
        for (int i = 0; i < hexes.size() - 1; i++) {
            hexes.get(i).diceNum = nums.get(i);
        }
    }




    /**
     * Print the board hexes and numbers in a good looking way
     */
    public void printBoard(){
        int index = 2;
        System.out.print("            ");
        for (int i = 0; i < hexes.size(); i++) {
            System.out.print(hexes.get(i).type + "_" + hexes.get(i).diceNum + "   ");
            if(i == 2 || i == 6 || i == 11 || i == 15 || i == 18){
                System.out.println();
                int spaceNum = 0;
                if(i == 2 || i == 11) spaceNum = 7;
                else if(i == 15) spaceNum = 12;
                for (int j = 0; j < spaceNum; j++) {
                    System.out.print(" ");
                }
            }
        }
    }
}
