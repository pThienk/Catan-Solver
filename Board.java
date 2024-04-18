import java.util.*;

public class Board {
    public List<Hex> hexes = new ArrayList<Hex>();
    public List<Spot> spots = new ArrayList<Spot>();
    public List<Settlement> settlements = new ArrayList<Settlement>();
    public List<Road> roads = new ArrayList<Road>();
    public List<Player> players = new ArrayList<Player>();

    public Spot blockedSpot;
    public StringBuilder output = new StringBuilder(); // 用于收集输出数据的StringBuilder
    /**
     * Generates a Random Board
     */
    public Board(){
       CreateRandomBoard();
    }

    public void CreateRandomBoard(){
        //generate Hexes
        for (int i = 0; i < 5; i++) {
            int num = 4; if(i > 2) num = 3;
            for (int j = 0; j < num; j++) {
                hexes.add(new Hex(Hex.HexTypeArray[i]));
            }
        }
        hexes.add(new Hex(Hex_Type.Desert));
        Collections.shuffle(hexes);
        for (int i = 0; i < hexes.size(); i++) {
            hexes.get(i).id = i;
        }
        int hexAlignmentIndex = 0;
        int offset = 3;
        for (int i = 0; i < hexes.size(); i++) {
            if(i != Hex.Hex_Alignment[hexAlignmentIndex] - 1){
                if(i + 1 < 19){
                    hexes.get(i).adjacentHexes.add(hexes.get(i + 1));
                    hexes.get(i + 1).adjacentHexes.add(hexes.get(i));
                }
            }
            if(i < 7){
                //left down
                hexes.get(i).adjacentHexes.add(hexes.get(i + offset));
                hexes.get(i + offset).adjacentHexes.add(hexes.get(i));
//inverse
                hexes.get(19 - i - 1).adjacentHexes.add(hexes.get(19 - i - offset - 1));
                hexes.get(19 - i - offset - 1).adjacentHexes.add(hexes.get(19 - i - 1));
                //right down
                hexes.get(i).adjacentHexes.add(hexes.get(i + offset + 1));
                hexes.get(i + offset + 1).adjacentHexes.add(hexes.get(i));
//inverse
                hexes.get(19 - i - 1).adjacentHexes.add(hexes.get(19 - i - offset - 2));
                hexes.get(19 - i - offset - 2).adjacentHexes.add(hexes.get(19 - i - 1));

            }
            if(i == Hex.Hex_Alignment[hexAlignmentIndex] - 1 && hexAlignmentIndex <4){
                hexAlignmentIndex++;
                offset = Hex.Hex_Alignment[hexAlignmentIndex] - Hex.Hex_Alignment[hexAlignmentIndex - 1];
            }
        }
        //generate the corresponding dice rolls
        List<Integer> nums = new ArrayList<Integer>();
        nums.add(2); nums.add(12); nums.add(0);
        for (int i = 3; i < 12; i++) {
            if(i == 7) continue;
            nums.add(i);
            nums.add(i);
        }

        Collections.shuffle(nums);
        boolean reshuffle = true;
        while(reshuffle) {
            reshuffle = false;
            for (int i = 0; i < nums.size(); i++) {
                if(nums.get(i) == 6 || nums.get(i) == 8){
                    for (int j = 0; j < hexes.get(i).adjacentHexes.size(); j++) {
                        int adjID = hexes.get(i).adjacentHexes.get(j).id;
                        if(
                                nums.get(adjID) == 6 ||
                                        nums.get(adjID) == 8
                        ){
                            reshuffle = true; break;
                        }
                    }
                }
            }
            if(reshuffle){
                Collections.shuffle(nums);
            }
        }

        int newdesertPlace = 0;
        int olddesertPlace = 0;
        for (int i = 0; i < hexes.size(); i++) {
            hexes.get(i).diceNum = nums.get(i);
            if(hexes.get(i).diceNum == 0) newdesertPlace = hexes.get(i).id;
            if(hexes.get(i).type == Hex_Type.Desert) olddesertPlace = hexes.get(i).id;
        }
        if(newdesertPlace != olddesertPlace){
            hexes.get(olddesertPlace).type = hexes.get(newdesertPlace).type;
            hexes.get(newdesertPlace).type = Hex_Type.Desert;
        }

        //generate the spots
        for (int i = 0; i < 54; i++) {
            Spot spot = new Spot(i);
            spots.add(spot);
        }
        //adding every spot to its corresponding hex & hex to its corresponding spot
        //counting from the top & inverse
        hexAlignmentIndex = 0;
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
        //making every spot aware of its adjacent spot
        //add left/right
        int boundIndex = 0;
        int leftBound = 0;
        int rightBound = Spot.Spot_Alignment[boundIndex];
        for (int i = 0; i < 54; i++) {
            if(i == rightBound && i != 53){
                boundIndex++;
                leftBound = rightBound;
                rightBound = Spot.Spot_Alignment[boundIndex];
            }
            if(i - 1 >= leftBound)
                spots.get(i).adjacentSpots.add(spots.get(i - 1));
            if(i + 1 < rightBound)
                spots.get(i).adjacentSpots.add(spots.get(i + 1));
        }
        //add top/down
        offset = 8;
        for (int i = 0; i < 54; i+=2) {
            if(i + offset < 54){
                spots.get(i).adjacentSpots.add(spots.get(i + offset));
                spots.get(i + offset).adjacentSpots.add(spots.get(i));
            }
            if(i == 6) { i--; offset = 10; }
            else if(i == 15){ i--; offset = 11; }
            else if(i == 26){ offset = 10; }
            else if(i == 36){ i ++; offset = 8; }
        }
        //porty
        for (int i = 0; i < 9; i++) {
            Port port = new Port(Port.portTypeArray[i]);
            spots.get( Port.portLocationArray[i*2] - 1 ).hasPort = true;
            spots.get( Port.portLocationArray[i*2 + 1] - 1 ).hasPort = true;
            spots.get(Port.portLocationArray[i*2] - 1).port = port;
            spots.get(Port.portLocationArray[i*2 + 1] - 1).port = port;
        }
    }
    public void PrintBoard(){
        for (int i = 0; i < 19; i++) {
//            System.out.println("Hex " + hexes.get(i).id + " has spots: " +
//                    hexes.get(i).showAdjacentSpots());
        }
        for (Spot spot : spots) {
            if (spot.hasPort) {
//                System.out.print("Spot " + spot.id + " has Port " + spot.port.getType());
//                System.out.println(" and hexes " + spot.printAdjacentHexes());
            }
        }
        for (int i = 0; i < spots.size(); i++) {
//            System.out.println("Spot " + i + " is next to: " + spots.get(i).showAdjacentSpots());
        }
        for (int i = 0; i < 19; i++) {
//            System.out.println("Hex " + hexes.get(i).id + " has hexes: " +
//                    hexes.get(i).showAdjacentHexes());
        }
        for (int i = 0; i < 19; i++) {
            System.out.println("Hex " + hexes.get(i).id + " has hexes: " +
                    hexes.get(i).showAdjacentHexes());
        }
        int index = 0;
        System.out.print("            ");
        for (int i = 0; i < hexes.size(); i++) {
            System.out.print(hexes.get(i).type + "_" + hexes.get(i).diceNum + "   ");
            output.append(hexes.get(i).type + "_" + hexes.get(i).diceNum + ",");

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
        System.out.print(output);

    }

}
