import java.util.Scanner;

public class minesweeperSolver {

    public static void main(String args[]) {
        Scanner input = new Scanner(System.in);
        Boolean correctInput = true;
        while(correctInput) {
            System.out.println("Easy (e), Medium (m), or Hard (h)");
            String inputString = input.next();
            if (inputString.equals("e") || inputString.equals("m") || inputString.equals("h")) {
                minesweeperlogic.startGame(inputString);
                correctInput = false;
            } else {
                System.out.println("Please input e, m or h");
            }

        }
    }

}
