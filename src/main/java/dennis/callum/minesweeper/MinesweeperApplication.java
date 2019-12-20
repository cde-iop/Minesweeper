package dennis.callum.minesweeper;

import java.util.Scanner;
import java.util.logging.Logger;



public class MinesweeperApplication {
    private static final Logger logger = Logger.getLogger(MinesweeperApplication.class.getName());


    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        boolean correctInput = true;
        while(correctInput) {
            logger.info("Easy (e), Medium (m), or Hard (h)");
            String inputString = input.next();
            if (inputString.equals("e") || inputString.equals("m") || inputString.equals("h")) {
                MinesweeperSolver.startGame(inputString);
                correctInput = false;
            } else {
                logger.warning("Please input e, m or h");
            }

        }
    }

}
