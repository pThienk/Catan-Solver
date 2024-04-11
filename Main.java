public class Main {
    public static void main(String[] args) {
        Board catanBoard = new Board();
        catanBoard.PrintBoard();

    }
}

/*
log -
created the catan board
- there are 19 hexes and 54 spots
- the spots and hexes are labeled from 0 - n
    - Both its ID and its location in list shows this information
- each spot know of its adjacent hexes
- each spot know of its adjacent spots
- each spot know of its port status
- each hex know of its adjacent spot
- types are managed by enums of Port_Type, Hex_Type
    - Easy to be changed when interacting with user interface
 */