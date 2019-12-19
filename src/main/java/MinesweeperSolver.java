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
                iterationCount = 4;

                break;
            case "h":
                dimensions[0] = 16;
                dimensions[1] = 30;
                driver.get("http://minesweeperonline.com/#150-night");
                gameState = new GameState(dimensions, driver);
                gameState.constructSquareList();
                noOfBombs = 99;
                iterationCount = 4;
                break;


        }
        WebElement game = gameState.getGame();
        Map<String, Square> squares = gameState.getAllSquares();
        Boolean gameStart = false;
        String format = "%s_%s";
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
        while (!gameFinished) {
            for (Map.Entry<String, Square> entry : squares.entrySet()) {
                Square element = entry.getValue();
                element.updateWebEle(game.getGame());
                game.resetChecked();
                checkAndClearSquares(element, game);
            }
            iterationCount -= 1;
            gameFinished = game.isGameFinished();
        }
    }

    private static void checkAndClearSquares(Square element, GameState game) {
        int number;
        List<Square> blankList;
        element.updateWebEle(game.getGame());
        Square value;
        int bombsFound;
        if (noOfBombs == 0) {
            logger.info("Game Finished, clearing up!");
            for (Map.Entry<String, Square> entry : game.getAllSquares().entrySet()) {
                value = entry.getValue();
                if (value.getEleClass().equals("square blank")) {
                    value.getWebEle().click();
                }
            }
            gameFinished = true;
        }
        if (!element.isCleared() && !element.isChecked()) {
            number = element.getNumber();
            if (number != 0) {
                blankList = game.getBlankList(element);
                bombsFound = element.getBombsFound();
                element.setChecked(true);
                if (bombsFound + blankList.size() == number) {
                    for (Square item : blankList) {
                        game.contextClick(item.getWebEle());
                        noOfBombs -= 1;
                        for (Square thing : item.getSurroundingSquares()) {
                            thing.setBombsFound(thing.getBombsFound() + 1);
                        }
                        item.updateWebEle(game.getGame());
                        item.setChecked(false);
                    }
                    element.setCleared(true);
                    for (Square item : element.getSurroundingSquares()) {
                        checkAndClearSquares(item, game);
                    }
                } else if (bombsFound == number) {
                    element.setChecked(true);
                    for (Square item : blankList) {
                        item.getWebEle().click();
                        item.updateWebEle(game.getGame());
                        item.setChecked(false);
                    }
                    element.setCleared(true);
                    for (Square item : element.getSurroundingSquares()) {
                        checkAndClearSquares(item, game);
                    }

                } else if (iterationCount <= 0) {
                    logger.info("Guessing random square");
                    blankList.get(rand.nextInt(blankList.size())).getWebEle().click();
                    iterationCount = 2;
                } else {
                    element.setChecked(true);
                    if (game.checkForPattern(element)) {
                        noOfBombs -= 2;
                        element.setCleared(true);
                        for (Square item : element.getSurroundingSquares()) {
                            checkAndClearSquares(item, game);
                        }
                    } else if (iterationCount <= 2 && game.checkFor21(element)) {
                        noOfBombs -= 1;
                    }
                }
            }
        }
    }
}
