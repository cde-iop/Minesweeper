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

        WebElement table = driver.findElement(new By.ByXPath("/html/body/table"));
        WebElement outer_container = table.findElement(By.className("outer-container"));
        WebElement inner_container = outer_container.findElement(By.className("inner-container"));
        WebElement center_column = inner_container.findElement(By.id("center-column"));
        WebElement game_container = center_column.findElement(By.id("game-container"));
        this.game = game_container.findElement(By.id("game"));
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
            if (x == 1 && y == 1) {
                surroundingSquares.add(output.get((String.format(format, 1, 2))));
                surroundingSquares.add(output.get((String.format(format, 2, 2))));
                surroundingSquares.add(output.get((String.format(format, 2, 1))));
            } else if (x == 1 && y == dimensions[0]) {
                surroundingSquares.add(output.get((String.format(format, y - 1, 1))));
                surroundingSquares.add(output.get((String.format(format, y - 1, 2))));
                surroundingSquares.add(output.get((String.format(format, y, 2))));
            } else if (y == 1 && x == dimensions[1]) {
                surroundingSquares.add(output.get((String.format(format, 1, x - 1))));
                surroundingSquares.add(output.get((String.format(format, 2, x - 1))));
                surroundingSquares.add(output.get((String.format(format, 2, x))));
            } else if (x == dimensions[1] && y == dimensions[0]) {
                surroundingSquares.add(output.get((String.format(format, y - 1, x))));
                surroundingSquares.add(output.get((String.format(format, y - 1, x - 1))));
                surroundingSquares.add(output.get((String.format(format, y, x - 1))));
            } else if (x == dimensions[1]) {
                surroundingSquares.add(output.get((String.format(format, y - 1, x))));
                surroundingSquares.add(output.get((String.format(format, y + 1, x))));
                surroundingSquares.add(output.get((String.format(format, y, x - 1))));
                surroundingSquares.add(output.get((String.format(format, y - 1, x - 1))));
                surroundingSquares.add(output.get((String.format(format, y + 1, x - 1))));
            } else if (y == dimensions[0]) {
                surroundingSquares.add(output.get(String.format(format, y, x - 1)));
                surroundingSquares.add(output.get((String.format(format, y, x + 1))));
                surroundingSquares.add(output.get((String.format(format, y - 1, x - 1))));
                surroundingSquares.add(output.get((String.format(format, y - 1, x))));
                surroundingSquares.add(output.get((String.format(format, y - 1, x + 1))));
            } else if (x == 1) {
                surroundingSquares.add(output.get((String.format(format, y - 1, x))));
                surroundingSquares.add(output.get((String.format(format, y + 1, x))));
                surroundingSquares.add(output.get((String.format(format, y + 1, x + 1))));
                surroundingSquares.add(output.get((String.format(format, y, x + 1))));
                surroundingSquares.add(output.get((String.format(format, y - 1, x + 1))));

            } else if (y == 1) {
                surroundingSquares.add(output.get((String.format(format, y, x - 1))));
                surroundingSquares.add(output.get((String.format(format, y, x + 1))));
                surroundingSquares.add(output.get((String.format(format, y + 1, x))));
                surroundingSquares.add(output.get((String.format(format, y + 1, x - 1))));
                surroundingSquares.add(output.get((String.format(format, y + 1, x + 1))));

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

    boolean isGameFinished() {
        boolean gameFinished = false;
        WebElement face;
        face = game.findElement(By.id("face"));

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
        }
        return gameFinished;
    }

    void contextClick(WebElement element) {
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
        if (checkFor11(element)){
            return false;
        }else if(checkFor121(element) || checkFor1221(element)) {
            return true;
        }else{
            return false;
        }

    }
    private boolean checkFor11(Square currentSquare) {
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


        private boolean checkFor121(Square currentSquare) {
        int[] elementCoords = currentSquare.getCoordinates();
        int y = elementCoords[0];
        int x = elementCoords[1];
        try {
            Square squareToLeft = allSquares.get(String.format(format, y, x - 1));
            Square squareToRight = allSquares.get(String.format(format, y, x + 1));

            if (squareToLeft.getNumber() - squareToLeft.getBombsFound() == 1
                    && currentSquare.getNumber() - currentSquare.getBombsFound() == 2
                    && squareToRight.getNumber() - squareToRight.getBombsFound() == 1) {
                try {
                    if (allSquares.get(String.format(format, y - 1, x - 1)).getEleClass().equals(blank)
                            && allSquares.get(String.format(format, y - 1, x + 1)).getEleClass().equals(blank)) {
                        contextClick(allSquares.get(String.format(format, y - 1, x - 1)).getWebEle());
                        for (Square thing: allSquares.get(String.format(format, y - 1, x - 1)).getSurroundingSquares()) {
                            thing.setBombsFound(thing.getBombsFound() + 1);
                        }
                        contextClick(allSquares.get(String.format(format, y - 1, x + 1)).getWebEle());
                        for (Square thing: allSquares.get(String.format(format, y - 1, x + 1)).getSurroundingSquares()) {
                            thing.setBombsFound(thing.getBombsFound() + 1);
                        }
                        allSquares.get(String.format(format, y - 1, x)).getWebEle().click();
                        return true;

                    } else if (allSquares.get(String.format(format, y + 1, x - 1)).getEleClass().equals(blank)
                            && allSquares.get(String.format(format, y + 1, x + 1)).getEleClass().equals(blank)) {
                        contextClick(allSquares.get(String.format(format, y + 1, x - 1)).getWebEle());
                        for (Square thing: allSquares.get(String.format(format, y + 1, x - 1)).getSurroundingSquares()) {
                            thing.setBombsFound(thing.getBombsFound() + 1);
                        }
                        contextClick(allSquares.get(String.format(format, y + 1, x + 1)).getWebEle());
                        for (Square thing: allSquares.get(String.format(format, y + 1, x + 1)).getSurroundingSquares()) {
                            thing.setBombsFound(thing.getBombsFound() + 1);
                        }
                        allSquares.get(String.format(format, y + 1, x)).getWebEle().click();
                        return true;
                    }
                }catch(NullPointerException e){
                    if (allSquares.get(String.format(format, y + 1, x - 1)).getEleClass().equals(blank)
                            && allSquares.get(String.format(format, y + 1, x + 1)).getEleClass().equals(blank)) {
                        contextClick(allSquares.get(String.format(format, y + 1, x - 1)).getWebEle());
                        for (Square thing: allSquares.get(String.format(format, y + 1, x - 1)).getSurroundingSquares()) {
                            thing.setBombsFound(thing.getBombsFound() + 1);
                        }
                        contextClick(allSquares.get(String.format(format, y + 1, x + 1)).getWebEle());
                        for (Square thing: allSquares.get(String.format(format, y + 1, x + 1)).getSurroundingSquares()) {
                            thing.setBombsFound(thing.getBombsFound() + 1);
                        }
                        allSquares.get(String.format(format, y + 1, x)).getWebEle().click();
                        return true;
                    }
                }
            }
        }catch(NullPointerException e){
            return false;
        }
        try{
            Square squareAbove = allSquares.get(String.format(format, y - 1, x));
            Square squareBelow = allSquares.get(String.format(format, y + 1, x));
            if (squareAbove.getNumber() - squareAbove.getBombsFound() == 1
                    && currentSquare.getNumber() - currentSquare.getBombsFound() == 2
                    && squareBelow.getNumber() - squareBelow.getBombsFound() == 1) {
                    try {
                        if (allSquares.get(String.format(format, y + 1, x - 1)).getEleClass().equals(blank)
                                && allSquares.get(String.format(format, y - 1, x - 1)).getEleClass().equals(blank)) {
                            contextClick(allSquares.get(String.format(format, y + 1, x - 1)).getWebEle());
                            for (Square thing: allSquares.get(String.format(format, y + 1, x - 1)).getSurroundingSquares()) {
                                thing.setBombsFound(thing.getBombsFound() + 1);
                            }
                            contextClick(allSquares.get(String.format(format, y - 1, x - 1)).getWebEle());
                            for (Square thing: allSquares.get(String.format(format, y - 1, x - 1)).getSurroundingSquares()) {
                                thing.setBombsFound(thing.getBombsFound() + 1);
                            }
                            allSquares.get(String.format(format, y, x - 1)).getWebEle().click();
                            return true;

                        } else if (allSquares.get(String.format(format, y + 1, x + 1)).getEleClass().equals(blank)
                                && allSquares.get(String.format(format, y - 1, x + 1)).getEleClass().equals(blank)) {
                            contextClick(allSquares.get(String.format(format, y + 1, x + 1)).getWebEle());
                            for (Square thing: allSquares.get(String.format(format, y + 1, x + 1)).getSurroundingSquares()) {
                                thing.setBombsFound(thing.getBombsFound() + 1);
                            }
                            contextClick(allSquares.get(String.format(format, y - 1, x + 1)).getWebEle());
                            for (Square thing: allSquares.get(String.format(format, y - 1, x + 1)).getSurroundingSquares()) {
                                thing.setBombsFound(thing.getBombsFound() + 1);
                            }
                            allSquares.get(String.format(format, y, x + 1)).getWebEle().click();
                            return true;
                        }
                    } catch (NullPointerException e) {
                        if (allSquares.get(String.format(format, y + 1, x + 1)).getEleClass().equals(blank)
                                && allSquares.get(String.format(format, y - 1, x + 1)).getEleClass().equals(blank)) {
                            contextClick(allSquares.get(String.format(format, y + 1, x + 1)).getWebEle());
                            for (Square thing: allSquares.get(String.format(format, y + 1, x + 1)).getSurroundingSquares()) {
                                thing.setBombsFound(thing.getBombsFound() + 1);
                            }
                            contextClick(allSquares.get(String.format(format, y - 1, x + 1)).getWebEle());
                            for (Square thing: allSquares.get(String.format(format, y - 1, x + 1)).getSurroundingSquares()) {
                                thing.setBombsFound(thing.getBombsFound() + 1);
                            }
                            allSquares.get(String.format(format, y, x + 1)).getWebEle().click();

                            return true;
                        }

                    }
            }
                return false;
        }catch(NullPointerException e){
            return false;
        }
    }

    private boolean checkFor1221(Square currentSquare) {
        int[] elementCoords = currentSquare.getCoordinates();
        int y = elementCoords[0];
        int x = elementCoords[1];
        try {
            Square squareToLeft = allSquares.get(String.format(format, y, x - 1));
            Square squareToRight = allSquares.get(String.format(format, y, x + 1));
            Square squareToRight2 = allSquares.get(String.format(format, y, x + 2));

            if (squareToLeft.getNumber() - squareToLeft.getBombsFound() == 1
                    && currentSquare.getNumber() - currentSquare.getBombsFound() == 2
                    && squareToRight.getNumber() - squareToRight.getBombsFound() == 2
                    && squareToRight2.getNumber() - squareToRight2.getBombsFound() == 1) {
                try {
                    if (allSquares.get(String.format(format, y + 1, x)).getEleClass().equals(blank)
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
                    } else if (allSquares.get(String.format(format, y - 1, x)).getEleClass().equals(blank)
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
                    if (allSquares.get(String.format(format, y - 1, x)).getEleClass().equals(blank)
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
                }
            }
        }catch(NullPointerException e){
                return false;
            }
        try{
            Square squareAbove = allSquares.get(String.format(format, y - 1, x));
            Square squareBelow = allSquares.get(String.format(format, y + 1, x));
            Square squareBelow2 = allSquares.get(String.format(format, y + 2, x));
            if(squareAbove.getNumber() - squareAbove.getBombsFound() == 1
                    && currentSquare.getNumber() - squareAbove.getBombsFound() == 2
                    && squareBelow.getNumber() - squareBelow.getBombsFound() == 2
                    && squareBelow2.getNumber() - squareBelow2.getBombsFound() == 1){
                try{
                    if(allSquares.get(String.format(format, y, x + 1)).getEleClass().equals(blank)
                            && allSquares.get(String.format(format, y + 1, x + 1)).getEleClass().equals(blank)) {
                        contextClick(allSquares.get(String.format(format, y + 1, x + 1)).getWebEle());
                        for (Square thing: allSquares.get(String.format(format, y + 1, x + 1)).getSurroundingSquares()) {
                            thing.setBombsFound(thing.getBombsFound() + 1);
                        }
                        contextClick(allSquares.get(String.format(format, y, x + 1)).getWebEle());
                        for (Square thing: allSquares.get(String.format(format, y, x + 1)).getSurroundingSquares()) {
                            thing.setBombsFound(thing.getBombsFound() + 1);
                        }
                        allSquares.get(String.format(format, y - 1, x + 1)).getWebEle().click();
                        allSquares.get(String.format(format, y + 2, x + 1)).getWebEle().click();
                        return true;
                    }else if(allSquares.get(String.format(format, y, x - 1)).getEleClass().equals(blank)
                            && allSquares.get(String.format(format, y + 1, x - 1)).getEleClass().equals(blank)) {
                        contextClick(allSquares.get(String.format(format, y, x - 1)).getWebEle());
                        for (Square thing : allSquares.get(String.format(format, y, x - 1)).getSurroundingSquares()) {
                            thing.setBombsFound(thing.getBombsFound() + 1);
                        }
                        contextClick(allSquares.get(String.format(format, y + 1, x - 1)).getWebEle());
                        for (Square thing : allSquares.get(String.format(format, y + 1, x - 1)).getSurroundingSquares()) {
                            thing.setBombsFound(thing.getBombsFound() + 1);
                        }
                        allSquares.get(String.format(format, y - 1, x - 1)).getWebEle().click();
                        allSquares.get(String.format(format, y + 2, x - 1)).getWebEle().click();
                        return true;
                    }
                }catch(NullPointerException e){
                    if(allSquares.get(String.format(format, y, x - 1)).getEleClass().equals(blank)
                            && allSquares.get(String.format(format, y + 1, x - 1)).getEleClass().equals(blank)) {
                        contextClick(allSquares.get(String.format(format, y, x - 1)).getWebEle());
                        for (Square thing : allSquares.get(String.format(format, y, x - 1)).getSurroundingSquares()) {
                            thing.setBombsFound(thing.getBombsFound() + 1);
                        }
                        contextClick(allSquares.get(String.format(format, y + 1, x - 1)).getWebEle());
                        for (Square thing : allSquares.get(String.format(format, y + 1, x - 1)).getSurroundingSquares()) {
                            thing.setBombsFound(thing.getBombsFound() + 1);
                        }
                        allSquares.get(String.format(format, y - 1, x - 1)).getWebEle().click();
                        allSquares.get(String.format(format, y + 2, x - 1)).getWebEle().click();
                        return true;
                    }

                }

            }
        }catch(NullPointerException e){
            return false;
        }
        return false;
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
        }catch(NullPointerException e){ }
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
}

