package dennis.callum.minesweeper;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

class MinesweeperSolver {
    private static final Logger logger = Logger.getLogger(MinesweeperSolver.class.getName());
    private static int noOfBombs;
    private static int iterationCount;
    private static boolean gameFinished = false;
    private static Random rand = new Random();

    static void startGame(String difficulty) {
        //Checks for the difficulty and sets the internal game state accordingly
        WebDriver driver = new FirefoxDriver();
        int[] dimensions = new int[2];
        GameState gameState = null;

        switch (difficulty) {
            case "e":
                dimensions[0] = 9;
                dimensions[1] = 9;
                driver.get("http://minesweeperonline.com/#beginner-150-night");
                gameState = new GameState(dimensions, driver);
                gameState.constructSquareList();
                noOfBombs = 10;
                iterationCount = 1;
                break;
            case "m":
                driver.get("http://minesweeperonline.com/#intermediate-150-night");
                dimensions[0] = 16;
                dimensions[1] = 16;
                gameState = new GameState(dimensions, driver);
                gameState.constructSquareList();
                noOfBombs = 40;
                iterationCount = 2;
                break;
            case "h":
                dimensions[0] = 16;
                dimensions[1] = 30;
                driver.get("http://minesweeperonline.com/#150-night");
                gameState = new GameState(dimensions, driver);
                gameState.constructSquareList();
                noOfBombs = 99;
                iterationCount = 3;
                break;
        }
        WebElement game = gameState.getGame();
        Map<String, Square> squares = gameState.getAllSquares();
        boolean gameStart = false;
        String format = "%s_%s";
        //Click three random squares if the game ends, restart the game and do it again.
        while (!gameStart) {
            for (int i = 0; i < 3; i++) {
                int y = rand.nextInt(dimensions[0]) + 1;
                int x = rand.nextInt(dimensions[1]) + 1;
                squares.get(String.format(format, y, x)).getWebEle().click();
            }
            if (!game.findElements(By.className("facedead")).isEmpty()) {
                logger.info("Game over at game start restarting");
                (game.findElement(By.className("facedead"))).click();
            } else {
                logger.info("Game Started, now solving...");
                solveTheGame(gameState);
                gameStart = true;
            }
        }

    }

    private static void solveTheGame(GameState game) {
        Map<String, Square> squares = game.getAllSquares();
        //While the game is unfinished iterate through each of the squares and check
        while (!gameFinished) {
            for (Map.Entry<String, Square> entry : squares.entrySet()) {
                Square element = entry.getValue();
                game.resetChecked();
                checkAndClearSquares(element, game);
            }
            iterationCount -= 1;
            gameFinished = game.isGameFinished(noOfBombs);
        }
    }

    static void checkAndClearSquares(Square element, GameState game) {
        int number;
        List<Square> blankList;
        element.updateWebEle(game.getGame());
        int bombsFound;
        //If  the square we're on hasn't already been checked this iteration or cleared (no more bombs to find)
        if (!element.isCleared() && !element.isChecked()) {
            number = element.getNumber();
            if (number != 0) {
                blankList = game.getBlankList(element);
                bombsFound = element.getBombsFound();
                element.setChecked(true);
                //If the number of bombs found and the number of blank squares equals the total number of bombs, flag the blanks
                if (bombsFound + blankList.size() == number) {
                    for (Square item : blankList) {
                        game.contextClick(item.getWebEle());
                        noOfBombs -= 1;
                        for (Square thing : item.getSurroundingSquares()) {
                            thing.setBombsFound(thing.getBombsFound() + 1);
                        }
                        item.updateWebEle(game.getGame());
                        item.setChecked(false);
                        for(Square thing : item.getSurroundingSquares()){
                            thing.setChecked(false);
                        }
                        element.setCleared(true);
                    }
                    //Recursive call, squares around this one are more likely to be solvable so we check those before continuing the iteration
                    for(Square item : blankList){
                        for(Square thing : item.getSurroundingSquares()){
                            checkAndClearSquares(thing, game);
                            for(Square test : thing.getSurroundingSquares()){
                                checkAndClearSquares(test, game);
                            }
                        }
                    }
                } else if (bombsFound == number) {
                    // if the bombs found is the same as total number of bombs, click any blanks and mark the square as cleared
                    for (Square item : blankList) {
                        item.getWebEle().click();
                        item.updateWebEle(game.getGame());
                        item.setChecked(false);
                    }
                    element.setCleared(true);
                    for(Square item : blankList){
                        for(Square thing : item.getSurroundingSquares()){
                            checkAndClearSquares(thing, game);
                        }
                    }

                }
                //If its been stuck for too long guess
                else if (iterationCount < 0) {
                    logger.info("Guessing random square");
                    blankList.get(rand.nextInt(blankList.size())).getWebEle().click();
                    iterationCount = 1;
                } else {
                    //Check for patterns and if there is one reduce the number of bombs and check squares around the pattern
                    element.setChecked(true);
                    if(game.checkFor11(element)){
                        element.iterateSurroundingSquares(game);
                    }else if (game.checkForPattern(element)) {
                        noOfBombs -= 2;
                        element.setCleared(true);
                        element.iterateSurroundingSquares(game);
                    }else if (iterationCount < 1 && game.checkFor21(element)) {
                        noOfBombs -= 1;
                        element.iterateSurroundingSquares(game);
                    }
                }
            }
        }
    }
    private MinesweeperSolver(){
        //So that people can't instantiate the MineSweeperSolverClass
        throw new UnsupportedOperationException("Don't instantiate the Minesweeper Solver class");
    }
}

