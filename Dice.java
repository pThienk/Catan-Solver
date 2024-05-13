import java.util.Random;

public class Dice {
    public boolean isBalanced = false;
    private Random random = new Random();
    public Dice(boolean isBalanced){
        this.isBalanced = isBalanced;
    }

    public Dice(Dice dice) {
        this.isBalanced = dice.isBalanced;
    }

    public int Roll(){
        int a = random.nextInt(6) + 1;
        int b = random.nextInt(6) + 1;
        int diceRoll = a + b;
        return diceRoll;
    }
}
