public class Main {
    public static void main(String[] args) {
        Board catanBoard = new Board();
        catanBoard.PrintBoard();

    }
}

/*
log -
created the Catan board
- there are 19 hexes and 54 spots
- the spots and hexes are labeled from 0 - n
    - Both their ID and their location in the list in <Board> shows this information
- each spot knows of its adjacent hexes
- each spot knows of its adjacent spots
- each spot knows of its port status
- each hex knows of its adjacent spots
- types are managed by enums of Port_Type and Hex_Type
    - Easy to be changed when interacting with user interface
 */