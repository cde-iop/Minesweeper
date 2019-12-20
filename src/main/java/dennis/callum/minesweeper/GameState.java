package dennis.callum.minesweeper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import java.util.*;
import java.util.logging.Logger;


class GameState {
    private static final Logger logger = Logger.getLogger(GameState.class.getName());

    private WebDriver driver;

    private int[] dimensions;

    private Map<String, Square> allSquares;

    private WebElement game;

    private String format = "%s_%s";

    private String blank = "square blank";

    GameState(int[] dimensions, WebDriver driver) {
        this.dimensions = dimensions;
        this.driver = driver;
        //The game itself is nested within other elements, for some reason i couldn't fetch the webelement when i tried
        //sto fetch it from the webpage, instead we get down to the webelement that contains the game and fetch each of the
        //squares from there
        WebElement table = driver.findElement(new By.ByXPath("/html/body/table"));
        WebElement outerContainer = table.findElement(By.className("outer-container"));
        WebElement innerContainer = outerContainer.findElement(By.className("inner-container"));
        WebElement centerColumn = innerContainer.findElement(By.id("center-column"));
        WebElement gameContainer = centerColumn.findElement(By.id("game-container"));
        this.game = gameContainer.findElement(By.id("game"));
        constructSquareList();
    }

    Map<String, Square> getAllSquares() {
        return allSquares;
    }

    WebElement getGame() {
        return game;
    }

    void constructSquareList() {
        Map<String, Square> output = new TreeMap<>();
        String id;
        List<Square> surroundingSquares = new ArrayList<>();
        Square value;
        //iterate through the grid and create square objects to represent each webelement square
        for (int y = 1; y <= dimensions[0]; y++) {
            for (int x = 1; x <= dimensions[1]; x++) {
                id = String.format(format, y, x);
                Square tba = new Square(game.findElement(By.id(id)), dimensions);
                output.put(id, tba);
            }
        }
        for (Map.Entry<String, Square> entry: output.entrySet()){
            value = entry.getValue();
            int y = value.getCoordinates()[0];
            int x = value.getCoordinates()[1];
            // if its the top left corner square
            if (x == 1 && y == 1) {
                surroundingSquares.add(output.get((String.format(format, 1, 2))));
                surroundingSquares.add(output.get((String.format(format, 2, 2))));
                surroundingSquares.add(output.get((String.format(format, 2, 1))));
                //if its the bottom left corner square
            } else if (x == 1 && y == dimensions[0]) {
                surroundingSquares.add(output.get((String.format(format, y - 1, 1))));
                surroundingSquares.add(output.get((String.format(format, y - 1, 2))));
                surroundingSquares.add(output.get((String.format(format, y, 2))));
                //if its the top right corner square
            } else if (y == 1 && x == dimensions[1]) {
                surroundingSquares.add(output.get((String.format(format, 1, x - 1))));
                surroundingSquares.add(output.get((String.format(format, 2, x - 1))));
                surroundingSquares.add(output.get((String.format(format, 2, x))));
                //if its the bottom right corner square
            } else if (x == dimensions[1] && y == dimensions[0]) {
                surroundingSquares.add(output.get((String.format(format, y - 1, x))));
                surroundingSquares.add(output.get((String.format(format, y - 1, x - 1))));
                surroundingSquares.add(output.get((String.format(format, y, x - 1))));
                //if its along the right edge
            } else if (x == dimensions[1]) {
                surroundingSquares.add(output.get((String.format(format, y - 1, x))));
                surroundingSquares.add(output.get((String.format(format, y + 1, x))));
                surroundingSquares.add(output.get((String.format(format, y, x - 1))));
                surroundingSquares.add(output.get((String.format(format, y - 1, x - 1))));
                surroundingSquares.add(output.get((String.format(format, y + 1, x - 1))));
                //if its along the bottom
            } else if (y == dimensions[0]) {
                surroundingSquares.add(output.get(String.format(format, y, x - 1)));
                surroundingSquares.add(output.get((String.format(format, y, x + 1))));
                surroundingSquares.add(output.get((String.format(format, y - 1, x - 1))));
                surroundingSquares.add(output.get((String.format(format, y - 1, x))));
                surroundingSquares.add(output.get((String.format(format, y - 1, x + 1))));
                //if its along the left edge
            } else if (x == 1) {
                surroundingSquares.add(output.get((String.format(format, y - 1, x))));
                surroundingSquares.add(output.get((String.format(format, y + 1, x))));
                surroundingSquares.add(output.get((String.format(format, y + 1, x + 1))));
                surroundingSquares.add(output.get((String.format(format, y, x + 1))));
                surroundingSquares.add(output.get((String.format(format, y - 1, x + 1))));
                //if its along the top
            } else if (y == 1) {
                surroundingSquares.add(output.get((String.format(format, y, x - 1))));
                surroundingSquares.add(output.get((String.format(format, y, x + 1))));
                surroundingSquares.add(output.get((String.format(format, y + 1, x))));
                surroundingSquares.add(output.get((String.format(format, y + 1, x - 1))));
                surroundingSquares.add(output.get((String.format(format, y + 1, x + 1))));
                //if its a middle square
            } else {
                surroundingSquares.add(output.get((String.format(format, y - 1, x - 1))));
                surroundingSquares.add(output.get((String.format(format, y - 1, x))));
                surroundingSquares.add(output.get((String.format(format, y - 1, x + 1))));
                surroundingSquares.add(output.get((String.format(format, y, x - 1))));
                surroundingSquares.add(output.get((String.format(format, y, x + 1))));
                surroundingSquares.add(output.get((String.format(format, y + 1, x - 1))));
                surroundingSquares.add(output.get((String.format(format, y + 1, x))));
                surroundingSquares.add(output.get((String.format(format, y + 1, x + 1))));
            }
            value.addAllSurroundingSquares(surroundingSquares);
            entry.setValue(value);
            surroundingSquares.clear();


        }
        this.allSquares = output;

    }

    List<Square> getBlankList(Square input) {
        //Searches through the surrounding square and creates a list of any that are blank, i.e unknown
        List<Square> blankList = new ArrayList<>();
        for (Square element : input.getSurroundingSquares()) {
            element.updateWebEle(game);
            if ((element.getEleClass()).equals(blank)) {
                blankList.add(element);
            }
        }
        if(blankList.isEmpty()){
            input.setCleared(true);
        }
        return blankList;
    }

    boolean isGameFinished(int noOfBombs) {
        boolean gameFinished = false;
        WebElement face;
        face = game.findElement(By.id("face"));
        Square value;
        if (noOfBombs == 0) {
            logger.info("Game Finished, clearing up!");
            for (Map.Entry<String, Square> entry : allSquares.entrySet()) {
                value = entry.getValue();
                if (value.getEleClass().equals("square blank")) {
                    value.getWebEle().click();
                }
            }
            gameFinished = true;
        }

        switch (face.getAttribute("class")) {
            case "facesmile":
                logger.info("Still solving");
                break;
            case "facewin":
                gameFinished = true;
                logger.info("Finished the game successfully!");
                driver.quit();
                break;
            case "facedead":
                gameFinished = true;
                logger.info("Lost the game...");
                driver.quit();
                break;
            default:
                gameFinished  = true;
                logger.info("Cannot determine state of the game... Ending the game");
                driver.quit();
                break;

        }
        return gameFinished;
    }

    void contextClick(WebElement element) {
        //Method to right click (in this case to mark bombs)
        try {
            Actions actions = new Actions(driver);
            actions.contextClick(element).perform();
        } catch (org.openqa.selenium.WebDriverException e){
            logger.info("Caught: " + e);
        }
    }

    void resetChecked() {
        for (Map.Entry<String, Square> entry: allSquares.entrySet()){
            entry.getValue().setChecked(false);

        }
    }
    boolean checkForPattern(Square element) {
        if (checkFor121H(element) || checkFor121V(element) || checkFor1221H(element) || checkFor1221V(element)){
            return true;
        }else{
            return false;
        }

    }

    boolean checkFor11(Square currentSquare) {
        int[] elementCoords = currentSquare.getCoordinates();
        int y = elementCoords[0];
        int x = elementCoords[1];
        try {
            Square squareToLeft = allSquares.get(String.format(format, y, x - 1));
            if(checkFor11AndClear(currentSquare, squareToLeft)){
                return true;
            }
        } catch (NullPointerException e) {}
        try {
            Square squareToRight = allSquares.get(String.format(format, y, x + 1));
            if(checkFor11AndClear(currentSquare, squareToRight)){
                return true;
            }
        } catch (NullPointerException e) {
        }
        try {
            Square squareAbove = allSquares.get(String.format(format, y - 1, x));
            if(checkFor11AndClear(currentSquare, squareAbove)){
                return true;
            }
        } catch (NullPointerException e) {
        }
        try {
            Square squareBelow = allSquares.get(String.format(format, y + 1, x));
            if(checkFor11AndClear(currentSquare, squareBelow)){
                return true;
            }
        }catch(NullPointerException e){
            return false;
        }
        return false;
    }


    private boolean checkFor121H(Square currentSquare) {
        int[] elementCoords = currentSquare.getCoordinates();
        int y = elementCoords[0];
        int x = elementCoords[1];
        try {
            Square squareToLeft = allSquares.get(String.format(format, y, x - 1));
            Square squareToRight = allSquares.get(String.format(format, y, x + 1));
            if(checkForAndDealWith121IfBlanksBelow(squareToLeft, currentSquare, squareToRight)){
                return true;
            }else {
                return checkForAndDealWith121IfBlanksToAbove(squareToLeft, currentSquare, squareToRight);
            }
        }catch(NullPointerException e){
                return false;
        }
    }

    private boolean checkFor121V(Square currentSquare) {
        int[] elementCoords = currentSquare.getCoordinates();
        int y = elementCoords[0];
        int x = elementCoords[1];
        try {
            Square squareAbove = allSquares.get(String.format(format, y - 1, x));
            Square squareBelow = allSquares.get(String.format(format, y + 1, x));
            if(checkForAndDealWith121IfBlanksLeft(squareAbove, currentSquare, squareBelow)){
                return true;
            }else {
                return checkForAndDealWith121IfBlanksRight(squareAbove,currentSquare,squareBelow);
            }
        }catch(NullPointerException e){
            return false;
        }
    }

    private boolean checkFor1221H(Square currentSquare) {
        int[] elementCoords = currentSquare.getCoordinates();
        int y = elementCoords[0];
        int x = elementCoords[1];
        try {
            Square squareToLeft = allSquares.get(String.format(format, y, x - 1));
            Square squareToRight = allSquares.get(String.format(format, y, x + 1));
            Square squareToRight2 = allSquares.get(String.format(format, y, x + 2));
            if(checkForAndDealWith1221IfBlanksAbove(squareToLeft, currentSquare, squareToRight, squareToRight2)){
                return true;
            }else {
                return checkForAndDealWith1221IfBlanksBelow(squareToLeft, currentSquare, squareToRight, squareToRight2);
            }
        } catch (NullPointerException e) {
            return false;
        }
    }

    private boolean checkFor1221V(Square currentSquare) {
        int[] elementCoords = currentSquare.getCoordinates();
        int y = elementCoords[0];
        int x = elementCoords[1];
        try {
            Square squareAbove = allSquares.get(String.format(format, y - 1, x));
            Square squareBelow = allSquares.get(String.format(format, y + 1, x));
            Square squareBelow2 = allSquares.get(String.format(format, y + 2, x));
            if (checkForAndDealWith1221IfBlanksRight(squareAbove, currentSquare, squareBelow, squareBelow2)) {
                return true;
            } else {
                return checkForAndDealWith1221IfBlanksLeft(squareAbove, currentSquare, squareBelow, squareBelow2);
            }
        }catch(NullPointerException e){
            return false;
        }
    }
    private boolean checkFor11AndClear(Square currentSquare, Square adjacentSquare) {
        List<Square> currentSquareBlanks = getBlankList(currentSquare);
        List<Square> adjacentSquareBlanks = getBlankList(adjacentSquare);
        if(currentSquare.getNumber() - currentSquare.getBombsFound() == 1
                && adjacentSquare.getNumber() - adjacentSquare.getBombsFound() == 1){
            if (adjacentSquareBlanks.containsAll(currentSquareBlanks)
                    && currentSquareBlanks.size() > 1
                    && adjacentSquareBlanks.size() > 1) {
                adjacentSquareBlanks.removeAll(currentSquareBlanks);
                for (Square element : adjacentSquareBlanks) {
                    element.getWebEle().click();
                }
                return true;
            } else if (currentSquareBlanks.containsAll(adjacentSquareBlanks)
                    && currentSquareBlanks.size() > 1
                    && adjacentSquareBlanks.size() > 1) {
                currentSquareBlanks.removeAll(adjacentSquareBlanks);
                for (Square element : currentSquareBlanks) {
                    element.getWebEle().click();
                }
                return true;
            }
        }else {
            return false;
        }
        return false;

    }

     boolean checkFor21(Square currentSquare) {
        int[] elementCoords = currentSquare.getCoordinates();
        int y = elementCoords[0];
        int x = elementCoords[1];
        try {
            Square squareToLeft = allSquares.get(String.format(format, y, x - 1));
            if(markIf21(currentSquare, squareToLeft)){
                return true;
            }
        }catch(NullPointerException e){
        }
        try{
            Square squareToRight = allSquares.get(String.format(format, y, x + 1));
            if(markIf21(currentSquare, squareToRight)){
                return true;
            }
        }catch(NullPointerException e){ }
         try{
             Square squareAbove = allSquares.get(String.format(format, y - 1, x));
             if(markIf21(currentSquare, squareAbove)){
                 return true;
             }
         }catch(NullPointerException e){ }
         try{
             Square squareBelow = allSquares.get(String.format(format, y + 1, x));
             if(markIf21(currentSquare, squareBelow)){
                 return true;
             }
         }catch(NullPointerException e){
             return false;
         }

         return false;
    }

    private boolean markIf21(Square currentSquare, Square adjacentSquare) {
        List<Square> currentSquareBlanks = getBlankList(currentSquare);
        List<Square> adjacentSquareBlanks = getBlankList(adjacentSquare);
        if(currentSquare.getNumber() - currentSquare.getBombsFound() == 2
                && adjacentSquare.getNumber() - adjacentSquare.getBombsFound() == 1){
            currentSquareBlanks.removeAll(adjacentSquareBlanks);
            if(currentSquareBlanks.size() == 1){
                contextClick(currentSquareBlanks.get(0).getWebEle());
                for(Square item : currentSquareBlanks.get(0).getSurroundingSquares()){
                    item.setBombsFound(item.getBombsFound() + 1);
                }
                return true;
            }else{
                return false;
            }
        } else{
            return false;
        }
    }

    private boolean checkForAndDealWith121IfBlanksToAbove(Square adjacentSquare1, Square currentSquare, Square adjacentSquare2) {
        int y = currentSquare.getCoordinates()[0];
        int x = currentSquare.getCoordinates()[1];
        try {
            if (adjacentSquare1.getNumber() - adjacentSquare1.getBombsFound() == 1
                    && currentSquare.getNumber() - currentSquare.getBombsFound() == 2
                    && adjacentSquare2.getNumber() - adjacentSquare2.getBombsFound() == 1
                    && allSquares.get(String.format(format, y - 1, x - 1)).getEleClass().equals(blank)
                    && allSquares.get(String.format(format, y - 1, x + 1)).getEleClass().equals(blank)) {
                contextClick(allSquares.get(String.format(format, y - 1, x - 1)).getWebEle());
                for (Square thing : allSquares.get(String.format(format, y - 1, x - 1)).getSurroundingSquares()) {
                    thing.setBombsFound(thing.getBombsFound() + 1);
                }
                contextClick(allSquares.get(String.format(format, y - 1, x + 1)).getWebEle());
                for (Square thing : allSquares.get(String.format(format, y - 1, x + 1)).getSurroundingSquares()) {
                    thing.setBombsFound(thing.getBombsFound() + 1);
                }
                allSquares.get(String.format(format, y - 1, x)).getWebEle().click();
                return true;

            }
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }

    private boolean checkForAndDealWith121IfBlanksBelow(Square adjacentSquare1, Square currentSquare, Square adjacentSquare2){
        int y = currentSquare.getCoordinates()[0];
        int x = currentSquare.getCoordinates()[1];
        try {
            if (adjacentSquare1.getNumber() - adjacentSquare1.getBombsFound() == 1
                    && currentSquare.getNumber() - currentSquare.getBombsFound() == 2
                    && adjacentSquare2.getNumber() - adjacentSquare2.getBombsFound() == 1
                    && allSquares.get(String.format(format, y + 1, x - 1)).getEleClass().equals(blank)
                    && allSquares.get(String.format(format, y + 1, x + 1)).getEleClass().equals(blank)) {
                contextClick(allSquares.get(String.format(format, y + 1, x - 1)).getWebEle());
                for (Square thing : allSquares.get(String.format(format, y + 1, x - 1)).getSurroundingSquares()) {
                    thing.setBombsFound(thing.getBombsFound() + 1);
                }
                contextClick(allSquares.get(String.format(format, y + 1, x + 1)).getWebEle());
                for (Square thing : allSquares.get(String.format(format, y + 1, x + 1)).getSurroundingSquares()) {
                    thing.setBombsFound(thing.getBombsFound() + 1);
                }
                allSquares.get(String.format(format, y + 1, x)).getWebEle().click();
                return true;
            }

        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }

    private boolean checkForAndDealWith121IfBlanksLeft(Square adjacentSquare1, Square currentSquare, Square adjacentSquare2) {
        int y = currentSquare.getCoordinates()[0];
        int x = currentSquare.getCoordinates()[1];
        try {
            if (adjacentSquare1.getNumber() - adjacentSquare1.getBombsFound() == 1
                    && currentSquare.getNumber() - currentSquare.getBombsFound() == 2
                    && adjacentSquare2.getNumber() - adjacentSquare2.getBombsFound() == 1
                    && allSquares.get(String.format(format, y + 1, x - 1)).getEleClass().equals(blank)
                    && allSquares.get(String.format(format, y - 1, x - 1)).getEleClass().equals(blank)) {
                contextClick(allSquares.get(String.format(format, y + 1, x - 1)).getWebEle());
                for (Square thing : allSquares.get(String.format(format, y + 1, x - 1)).getSurroundingSquares()) {
                    thing.setBombsFound(thing.getBombsFound() + 1);
                }
                contextClick(allSquares.get(String.format(format, y - 1, x - 1)).getWebEle());
                for (Square thing : allSquares.get(String.format(format, y - 1, x - 1)).getSurroundingSquares()) {
                    thing.setBombsFound(thing.getBombsFound() + 1);
                }
                allSquares.get(String.format(format, y, x - 1)).getWebEle().click();
                return true;

            }
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }

    private boolean checkForAndDealWith121IfBlanksRight(Square adjacentSquare1, Square currentSquare, Square adjacentSquare2){
        int y = currentSquare.getCoordinates()[0];
        int x = currentSquare.getCoordinates()[1];
        try {
            if (adjacentSquare1.getNumber() - adjacentSquare1.getBombsFound() == 1
                    && currentSquare.getNumber() - currentSquare.getBombsFound() == 2
                    && adjacentSquare2.getNumber() - adjacentSquare2.getBombsFound() == 1
                    && allSquares.get(String.format(format, y + 1, x + 1)).getEleClass().equals(blank)
                    && allSquares.get(String.format(format, y - 1, x + 1)).getEleClass().equals(blank)) {
                contextClick(allSquares.get(String.format(format, y + 1, x + 1)).getWebEle());
                for (Square thing : allSquares.get(String.format(format, y + 1, x + 1)).getSurroundingSquares()) {
                    thing.setBombsFound(thing.getBombsFound() + 1);
                }
                contextClick(allSquares.get(String.format(format, y - 1, x + 1)).getWebEle());
                for (Square thing : allSquares.get(String.format(format, y - 1, x + 1)).getSurroundingSquares()) {
                    thing.setBombsFound(thing.getBombsFound() + 1);
                }
                allSquares.get(String.format(format, y, x + 1)).getWebEle().click();
                return true;
            }
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }

    private boolean checkForAndDealWith1221IfBlanksBelow(Square adjacentSquare1, Square currentSquare, Square adjacentSquare2, Square adjacentSquare3){
        int y = currentSquare.getCoordinates()[0];
        int x = currentSquare.getCoordinates()[1];
        try {
            if (adjacentSquare1.getNumber() - adjacentSquare1.getBombsFound() == 1
                    && currentSquare.getNumber() - currentSquare.getBombsFound() == 2
                    && adjacentSquare2.getNumber() - adjacentSquare2.getBombsFound() == 2
                    && adjacentSquare3.getNumber() - adjacentSquare3.getBombsFound() == 1
                    && allSquares.get(String.format(format, y + 1, x)).getEleClass().equals(blank)
                    && allSquares.get(String.format(format, y + 1, x + 1)).getEleClass().equals(blank)) {
                contextClick(allSquares.get(String.format(format, y + 1, x)).getWebEle());
                for (Square thing : allSquares.get(String.format(format, y + 1, x)).getSurroundingSquares()) {
                    thing.setBombsFound(thing.getBombsFound() + 1);
                }
                contextClick(allSquares.get(String.format(format, y + 1, x + 1)).getWebEle());
                for (Square thing : allSquares.get(String.format(format, y + 1, x + 1)).getSurroundingSquares()) {
                    thing.setBombsFound(thing.getBombsFound() + 1);
                }
                allSquares.get(String.format(format, y + 1, x - 1)).getWebEle().click();
                allSquares.get(String.format(format, y + 1, x + 2)).getWebEle().click();
                return true;
            }
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }

    private boolean checkForAndDealWith1221IfBlanksAbove(Square adjacentSquare1, Square currentSquare, Square adjacentSquare2, Square adjacentSquare3){
        int y = currentSquare.getCoordinates()[0];
        int x = currentSquare.getCoordinates()[1];
        try {
            if (adjacentSquare1.getNumber() - adjacentSquare1.getBombsFound() == 1
                    && currentSquare.getNumber() - currentSquare.getBombsFound() == 2
                    && adjacentSquare2.getNumber() - adjacentSquare2.getBombsFound() == 2
                    && adjacentSquare3.getNumber() - adjacentSquare3.getBombsFound() == 1
                    && allSquares.get(String.format(format, y - 1, x)).getEleClass().equals(blank)
                    && allSquares.get(String.format(format, y - 1, x + 1)).getEleClass().equals(blank)) {
                contextClick(allSquares.get(String.format(format, y - 1, x)).getWebEle());
                for (Square thing : allSquares.get(String.format(format, y - 1, x)).getSurroundingSquares()) {
                    thing.setBombsFound(thing.getBombsFound() + 1);
                }
                contextClick(allSquares.get(String.format(format, y - 1, x + 1)).getWebEle());
                for (Square thing : allSquares.get(String.format(format, y - 1, x + 1)).getSurroundingSquares()) {
                    thing.setBombsFound(thing.getBombsFound() + 1);
                }
                allSquares.get(String.format(format, y - 1, x - 1)).getWebEle().click();
                allSquares.get(String.format(format, y - 1, x + 2)).getWebEle().click();
                return true;
            }
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }

    private boolean checkForAndDealWith1221IfBlanksRight(Square adjacentSquare1, Square currentSquare, Square adjacentSquare2, Square adjacentSquare3){
        int y = currentSquare.getCoordinates()[0];
        int x = currentSquare.getCoordinates()[1];
        try {
            if(adjacentSquare1.getNumber() - adjacentSquare1.getBombsFound() == 1
                    && currentSquare.getNumber() - currentSquare.getBombsFound() == 2
                    && adjacentSquare2.getNumber() - adjacentSquare2.getBombsFound() == 2
                    && adjacentSquare3.getNumber() - adjacentSquare3.getBombsFound() == 1
                    && allSquares.get(String.format(format, y, x + 1)).getEleClass().equals(blank)
                    && allSquares.get(String.format(format, y + 1, x + 1)).getEleClass().equals(blank)) {
                contextClick(allSquares.get(String.format(format, y + 1, x + 1)).getWebEle());
                for (Square thing : allSquares.get(String.format(format, y + 1, x + 1)).getSurroundingSquares()) {
                    thing.setBombsFound(thing.getBombsFound() + 1);
                }
                contextClick(allSquares.get(String.format(format, y, x + 1)).getWebEle());
                for (Square thing : allSquares.get(String.format(format, y, x + 1)).getSurroundingSquares()) {
                    thing.setBombsFound(thing.getBombsFound() + 1);
                }
                allSquares.get(String.format(format, y - 1, x + 1)).getWebEle().click();
                allSquares.get(String.format(format, y + 2, x + 1)).getWebEle().click();
                return true;
            }
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }
    private boolean checkForAndDealWith1221IfBlanksLeft(Square adjacentSquare1, Square currentSquare, Square adjacentSquare2, Square adjacentSquare3){
        int y = currentSquare.getCoordinates()[0];
        int x = currentSquare.getCoordinates()[1];
        try {
            if(adjacentSquare1.getNumber() - adjacentSquare1.getBombsFound() == 1
                    && currentSquare.getNumber() - currentSquare.getBombsFound() == 2
                    && adjacentSquare2.getNumber() - adjacentSquare2.getBombsFound() == 2
                    && adjacentSquare3.getNumber() - adjacentSquare3.getBombsFound() == 1
                    && allSquares.get(String.format(format, y, x - 1)).getEleClass().equals(blank)
                    && allSquares.get(String.format(format, y + 1, x - 1)).getEleClass().equals(blank)) {
                contextClick(allSquares.get(String.format(format, y + 1, x - 1)).getWebEle());
                for (Square thing : allSquares.get(String.format(format, y + 1, x - 1)).getSurroundingSquares()) {
                    thing.setBombsFound(thing.getBombsFound() + 1);
                }
                contextClick(allSquares.get(String.format(format, y, x - 1)).getWebEle());
                for (Square thing : allSquares.get(String.format(format, y, x - 1)).getSurroundingSquares()) {
                    thing.setBombsFound(thing.getBombsFound() + 1);
                }
                allSquares.get(String.format(format, y - 1, x - 1)).getWebEle().click();
                allSquares.get(String.format(format, y + 2, x - 1)).getWebEle().click();
                return true;
            }
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

}

