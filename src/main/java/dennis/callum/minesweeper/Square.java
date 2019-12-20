package dennis.callum.minesweeper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.ArrayList;
import java.util.List;

class Square {
    private WebElement webEle;
    private String eleClass;
    private String id;
    private int[] coordinates = new int[2];
    private List<Square> surroundingSquares = new ArrayList<>();
    private boolean cleared;
    private boolean checked;
    private int number;
    private int bombsFound = 0;

    Square(WebElement webEle, int[] dimensions) {
        this.webEle = webEle;
        eleClass = webEle.getAttribute("class");
        id = webEle.getAttribute("id");
        if (dimensions[0] >= 10 && dimensions[1] >= 10) {
            if (id.charAt(2) == '_') {
                if (id.length() == 5) {
                    coordinates[0] = Integer.parseInt(id.substring(0, 2));
                    coordinates[1] = Integer.parseInt(id.substring(id.length() - 2));
                } else if (id.length() == 4) {
                    coordinates[0] = Integer.parseInt(id.substring(0, 2));
                    coordinates[1] = Integer.parseInt(id.substring(id.length() - 1));
                }

            } else if (id.charAt(1) == '_') {
                if (id.length() == 4) {
                    coordinates[0] = Integer.parseInt(id.substring(0, 1));
                    coordinates[1] = Integer.parseInt(id.substring(id.length() - 2));
                } else if (id.length() == 3) {
                    coordinates[0] = Integer.parseInt(id.substring(0, 1));
                    coordinates[1] = Integer.parseInt(id.substring(id.length() - 1));
                }
            }
        } else {
            coordinates[0] = Integer.parseInt(id.substring(0, 1));
            coordinates[1] = Integer.parseInt(id.substring(id.length() - 1));
        }
        cleared = false;
        checked = false;


    }

    void iterateSurroundingSquares(GameState game){
        for (Square item : getSurroundingSquares()) {
            MinesweeperSolver.checkAndClearSquares(item, game);
        }
    }

    WebElement getWebEle() {
        return webEle;
    }

    private void setWebEle(WebElement webEle) {
        this.webEle = webEle;
        this.eleClass = webEle.getAttribute("class");
        this.id = webEle.getAttribute("id");
        if (!eleClass.equals("square blank") && !eleClass.equals("square bombflagged")) {
            number = Integer.parseInt(eleClass.substring(eleClass.length() - 1));
        }
    }

    void updateWebEle(WebElement game) {
        setWebEle(game.findElement(By.id(id)));
    }

    int getNumber() {
        return number;
    }

    String getEleClass() {
        return eleClass;
    }

    boolean isCleared() {
        return cleared;
    }

    void setCleared(boolean cleared) {
        this.cleared = cleared;
    }

    int[] getCoordinates() {
        return coordinates;
    }

    List<Square> getSurroundingSquares() {
        return surroundingSquares;
    }

    private void setSurroundingSquares(List<Square> surroundingSquares) {
        this.surroundingSquares = surroundingSquares;
    }

    void addAllSurroundingSquares(List<Square> surroundingSquares) {
        List<Square> newList = new ArrayList<>(surroundingSquares);
        setSurroundingSquares(newList);
    }

    boolean isChecked() {
        return checked;
    }

    void setChecked(boolean checked) {
        this.checked = checked;
    }


    void setBombsFound(int bombsLeft) {
        this.bombsFound = bombsLeft;
    }

    int getBombsFound() {
        return bombsFound;
    }

}