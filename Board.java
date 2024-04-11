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
                hexes.add(new Hex(Hex.HexTypeArray[i], hexes.size()));
            }
        }
        hexes.add(new Hex(Hex_Type.Desert, hexes.size()));
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
        //generate the spots
        for (int i = 0; i < 54; i++) {
            Spot spot = new Spot(i);
            spots.add(spot);
        }
        //adding every spot to its corresponding hex & hex to its corresponding spot
        //counting from the top & inverse
        int hexAlignmentIndex = 0;
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 3; j++) {
                hexes.get(i).adjacentSpots.add(spots.get(i*2 + j + hexAlignmentIndex));
                spots.get(i*2 + j + hexAlignmentIndex).adjacentHexes.add(hexes.get(i));

                hexes.get(18 - i).adjacentSpots.add(spots.get(53 - (i*2 + j + hexAlignmentIndex)));
                spots.get(53 - (i*2 + j + hexAlignmentIndex)).adjacentHexes.add(hexes.get(18-i));
            }
            if(i == Hex.Hex_Alignment[hexAlignmentIndex] - 1) hexAlignmentIndex ++;
        }
        //counting from the bottom & inverse
        hexAlignmentIndex = 0;
        for (int i = 0; i < 7; i++) {
            for (int j = 8; j < 11; j++) {
                hexes.get(i).adjacentSpots.add(spots.get(i*2 + j + hexAlignmentIndex*3));
                spots.get(i*2 + j + hexAlignmentIndex*3).adjacentHexes.add(hexes.get(i));

                hexes.get(18-i).adjacentSpots.add(spots.get(53-(i*2 + j + hexAlignmentIndex*3)));
                spots.get(53 - (i*2 + j + hexAlignmentIndex*3)).adjacentHexes.add(hexes.get(18 - i));
            }
            if(i == Hex.Hex_Alignment[hexAlignmentIndex] - 1) hexAlignmentIndex ++;
        }


        for (int i = 0; i < 19; i++) {
            System.out.println("Hex " + hexes.get(i).id + " has spots: " +
                    hexes.get(i).showAdjacentSpots());
        }
    }




    /**
     * Print the board hexes and numbers in a good looking way
     */
    public void printBoard(){
        int index = 0;
        System.out.print("            ");
        for (int i = 0; i < hexes.size(); i++) {
            System.out.print(hexes.get(i).type + "_" + hexes.get(i).diceNum + "   ");
            if(i == Hex.Hex_Alignment[index] - 1){
                System.out.println();

                int spaceNum = 0;
                if(index == 0 || index == 2) spaceNum = 7;
                else if(index == 3) spaceNum = 12;
                for (int j = 0; j < spaceNum; j++) {
                    System.out.print(" ");
                }

                index ++;
            }
        }
    }
}
